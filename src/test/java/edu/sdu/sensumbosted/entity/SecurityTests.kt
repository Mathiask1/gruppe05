package edu.sdu.sensumbosted.entity

import edu.sdu.sensumbosted.data.DataService
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock

@Suppress("ClassName")
class SecurityTests {

    val ctx = Context(mock(DataService::class.java))
    val department = Department("Main department")
    val department2 = Department("Other department")
    val patient = Patient(department, "Patient")
    val patient2 = Patient(department2, "Patient2")
    val practitioner = Practitioner(department, "Practitioner")
    val caseworker = Manager(department, "Caseworker", AuthLevel.CASEWORKER)
    val localAdmin = Manager(department, "LocalAdmin", AuthLevel.LOCAL_ADMIN)
    val localAdmin2 = Manager(department, "LocalAdmin2", AuthLevel.LOCAL_ADMIN)
    val superuser = Manager(department, "Superuser", AuthLevel.SUPERUSER)

    @Nested
    inner class `context checkMinimum` {
        @Test
        fun `check patient permissions`() {
            ctx.user = patient
            assertTrue(ctx.checkMinimum(department, AuthLevel.NO_AUTH))
            assertTrue(ctx.checkMinimum(department, AuthLevel.PATIENT))
            assertFalse(ctx.checkMinimum(department, AuthLevel.PRACTITIONER))
            assertFalse(ctx.checkMinimum(department, AuthLevel.CASEWORKER))
            assertFalse(ctx.checkMinimum(department, AuthLevel.LOCAL_ADMIN))
            assertFalse(ctx.checkMinimum(department, AuthLevel.SUPERUSER))

            assertFalse(ctx.checkMinimum(department2, AuthLevel.NO_AUTH))
            assertFalse(ctx.checkMinimum(department2, AuthLevel.PATIENT))
        }

        @Test
        fun `check superuser permissions`() {
            ctx.user = superuser
            assertTrue(ctx.checkMinimum(department, AuthLevel.NO_AUTH))
            assertTrue(ctx.checkMinimum(department, AuthLevel.PATIENT))
            assertTrue(ctx.checkMinimum(department, AuthLevel.PRACTITIONER))
            assertTrue(ctx.checkMinimum(department, AuthLevel.CASEWORKER))
            assertTrue(ctx.checkMinimum(department, AuthLevel.LOCAL_ADMIN))
            assertTrue(ctx.checkMinimum(department, AuthLevel.SUPERUSER))

            assertTrue(ctx.checkMinimum(department2, AuthLevel.NO_AUTH))
            assertTrue(ctx.checkMinimum(department2, AuthLevel.PATIENT))
        }
    }

    @Nested
    inner class `test user deletion` {
        @Test
        fun `low permissions`() {
            ctx.user = caseworker
            fails { department.deleteUser(ctx, patient) }
            fails { department.deleteUser(ctx, patient2) }
            fails { department.deleteUser(ctx, caseworker) }
            fails { department.deleteUser(ctx, localAdmin) }

            ctx.user = localAdmin
            fails { department.deleteUser(ctx, superuser) }
        }

        @Test
        fun `wrong department`() {
            ctx.user = localAdmin
            fails { department.deleteUser(ctx, patient2) }
            fails { department2.deleteUser(ctx, patient2) }
            fails { department2.deleteUser(ctx, patient) }
        }

        @Test
        fun `no delete self`() {
            ctx.user = superuser
            fails { department.deleteUser(ctx, superuser) }
        }

        @Test
        fun `test allowed`() {
            ctx.user = localAdmin
            department.deleteUser(ctx, patient)
            department.deleteUser(ctx, practitioner)
            department.deleteUser(ctx, caseworker)
            department.deleteUser(ctx, localAdmin2)

            ctx.user = superuser
            department.deleteUser(ctx, localAdmin)
            department2.deleteUser(ctx, patient2)
        }
    }

    fun fails(func: () -> Unit) {
        try {
            func()
            return fail("Expected exception")
        }
        catch (ex: SensumAccessException) {}
        catch (ex: IllegalArgumentException) {}
    }
}
