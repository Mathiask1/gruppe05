package edu.sdu.sensumbosted.data

import edu.sdu.sensumbosted.entity.*
import org.json.JSONArray
import org.json.JSONObject
import org.slf4j.LoggerFactory
import java.sql.ResultSet
import java.util.*

/**
 * This is the only class in this project that is written in Kotlin.
 *
 * While this project is intended to be written in Java, Kotlin brings with it a lot of features in relation to lambdas
 * and declarative collection handling (especially [associateBy]).
 *
 * This class loads the database upon startup, and returns a map of all the departments as a [HashMap].
 */
class DepartmentLoader(private val data: DataService) {

    companion object {
        private val log = LoggerFactory.getLogger(DepartmentLoader::class.java)
    }

    fun loadDepartments(): HashMap<UUID, Department> {
        log.info("Loading departments and their members")

        // Load departments to a map
        val departments = data.jdbc.query("SELECT * FROM departments;") { rs, _ ->
            val department = Department(rs.getId("id"), rs.getString("name"))
            log.debug("Loaded $department")
            department
        }.associateBy { it.id }
        val departmentUserMaps = mutableMapOf<UUID, MutableMap<UUID, User>>()
        departments.forEach { departmentUserMaps[it.key] = mutableMapOf() }
        log.info("Loaded ${departments.size} departments")

        fun User.addToDepartment(rs: ResultSet) {
            val depId = rs.getId("department")
            if (!departmentUserMaps.containsKey(depId)) {
                log.warn("Received dangling department ID $depId for user $this")
                return
            }
            departmentUserMaps[depId]!![id] = this
        }

        val managers = data.jdbc.query("SELECT * FROM managers;") { rs, rowNum ->
            val manager = Manager(
                    departments.getDepartment(rs),
                    rs.getString("name"),
                    AuthLevel.valueOf(rs.getString("auth"))
            )
            log.debug("Loaded $manager")
            manager.addToDepartment(rs)
            manager
        }

        log.info("Loaded ${managers.size} managers")

        val assigneeRelations = mutableMapOf<UUID, MutableList<Practitioner>>()
        val practitioners = data.jdbc.query("SELECT * FROM practitioners;") { rs, _ ->
            val practitioner = Practitioner(departments.getDepartment(rs), rs.getString("name"))
            log.info("Loaded $practitioner")
            assigneeRelations[practitioner.id] = mutableListOf()
            practitioner.addToDepartment(rs)
            practitioner
        }.associateBy { it.id }
        log.info("Loaded ${practitioners.size} practitioners")

        val assignedRelations = mutableMapOf<UUID, MutableList<Patient>>()
        val patients = data.jdbc.query("SELECT * FROM patients;") { rs, _ ->
            val patient = Patient(
                    rs.getId("id"),
                    departments.getDepartment(rs),
                    rs.getString("name"),
                    JSONObject(rs.getString("diary")),
                    JSONArray(rs.getString("calendar")),
                    rs.getBoolean("enrolled")
            )
            log.info("Loaded $patient")
            assigneeRelations[patient.id] = mutableListOf()
            patient.addToDepartment(rs)
            patient
        }.associateBy { it.id }
        log.info("Loaded ${patients.size} practitioners")

        var relations = 0
        var dangling = 0
        data.jdbc.query("SELECT * FROM practitionerpatientrelation;") { rs ->
            val practitioner = practitioners[rs.getId("practitioner")]
            val patient = patients[rs.getId("patient")]

            if (practitioner == null || patient == null) {
                dangling++
                return@query
            }

            assignedRelations[practitioner.id]!!.add(patient)
            assigneeRelations[patient.id]!!.add(practitioner)
            relations++
        }

        log.info("Found $relations relations between patients and practitioners.")
        if (dangling > 0) log.warn("$dangling relations were dangling.")

        departmentUserMaps.forEach { departments.getValue(it.key).lateInit(it.value) }
        assignedRelations.forEach { practitioners.getValue(it.key).lateInit(it.value) }
        assigneeRelations.forEach { patients.getValue(it.key).lateInit(it.value) }

        if (log.isDebugEnabled) printDebug(departments.values.toList())

        return HashMap(departments)
    }

    private fun ResultSet.getId(label: String): UUID = UUID.fromString(this.getString(label))

    private fun Map<UUID, Department>.getDepartment(rs: ResultSet) =
            this[UUID.fromString(rs.getString("department"))] ?: error("Map should contain ID")

    private fun printDebug(deps: List<Department>) {
        log.debug(buildString {
            append("\n")
            appendln("${deps.size} departments")
            deps.forEach { dep ->
                appendln("├── $dep")
                dep.getUsers(data.context).forEach {
                    appendln("│   ├── $it")
                }
            }
        })
    }
}
