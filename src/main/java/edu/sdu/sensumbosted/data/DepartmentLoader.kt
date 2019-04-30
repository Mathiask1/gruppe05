package edu.sdu.sensumbosted.data

import edu.sdu.sensumbosted.entity.*
import org.slf4j.LoggerFactory
import java.sql.ResultSet
import java.util.*

/**
 * This is the only class in this project that is written in Kotlin.
 *
 * While this project is intended to be written in Java, Kotlin brings with it a lot of features in relation to lambdas
 * and declarative collection handling.
 */
class DepartmentLoader(private val data: DataService) {

    companion object {
        private val log = LoggerFactory.getLogger(DepartmentLoader::class.java)
    }

    fun loadDepartments(): HashMap<UUID, Department> {
        log.info("Loading departments and their members")

        // Load departments
        val depsList = data.jdbc.query("SELECT * FROM departments;") { rs, rowNum ->
            val department = Department(getId(rs, "id"), rs.getString("name"))
            log.debug("Loaded $department")
            department
        }
        val departments = depsList.associateBy { it.id }
        log.info("Loaded ${departments.size} departments")

        val managers = data.jdbc.query("SELECT * FROM managers;") { rs, rowNum ->
            val manager = Manager(
                    getDepartment(departments, rs),
                    rs.getString("name"),
                    AuthLevel.valueOf(rs.getString("auth"))
            )
            log.debug("Loaded $manager")
            manager
        }

        log.info("Loaded ${managers.size} managers")

        val assigneeRelations = mutableMapOf<UUID, List<Practitioner>>()
        val assignedRelations = mutableMapOf<UUID, List<Patient>>()

        val practitioners = data.jdbc.query("SELECT * FROM practitioners;") { rs, rowNum ->
            val practitioner = Practitioner(getDepartment(departments, rs), rs.getString("name"))
            log.info("Loaded $practitioner")
            practitioner
        }

        log.info("Loaded ${practitioners.size} practitioners")

        return HashMap(departments)
    }

    private fun getId(rs: ResultSet, label: String): UUID = UUID.fromString(rs.getString(label))

    private fun getDepartment(departments: Map<UUID, Department>, rs: ResultSet): Department =
            departments[UUID.fromString(rs.getString("department"))] ?: error("Map should contain ID")
}
