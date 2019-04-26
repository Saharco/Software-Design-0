import il.ac.technion.cs.softwaredesign.CourseApp
import il.ac.technion.cs.softwaredesign.CourseAppInitializer
import il.ac.technion.cs.softwaredesign.storage.read
import il.ac.technion.cs.softwaredesign.storage.write
import io.mockk.every
import io.mockk.mockkStatic
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.lang.IllegalArgumentException

class CourseAppTest {
    private val courseAppInitializer = CourseAppInitializer()
    private val app = CourseApp()

    private var db = hashMapOf<String, String>()

    init {
        courseAppInitializer.setup()
        val charset = charset("UTF-8")

        mockkStatic("il.ac.technion.cs.softwaredesign.storage.SecureStorageKt")
        every {
            read(any())
        } answers {
            db[firstArg<ByteArray>().toString(charset)]?.toByteArray()
        }
        every {
            write(any(), any())
        } answers {
            db[firstArg<ByteArray>().toString(charset)] = secondArg<ByteArray>().toString(charset)
        }
    }

    @BeforeEach
    fun resetDatabase() {
        db = hashMapOf()
    }

    @Test
    fun `user successfully logged in after login`() {
        val token = app.login("sahar", "a very strong password")

        assertEquals(app.isUserLoggedIn(token, "sahar"), true)
    }

    @Test
    fun `cant create two users with same username`() {
        app.login("sahar", "a very strong password")
        assertThrows<IllegalArgumentException> {
            app.login("sahar", "weak password")
        }
    }

    @Test
    fun `using token to check login session after self's login session expires should not work`() {
        val token = app.login("sahar", "a very strong password")
        app.login("yuval", "popcorn")
        app.logout(token)
        assertThrows<IllegalArgumentException> {
            app.isUserLoggedIn(token, "yuval")
        }
    }

    @Test
    fun `two different users should have different tokens`() {
        val token1 = app.login("sahar", "a very strong password")
        val token2 = app.login("yuval", "popcorn")
        assertTrue(token1 != token2)
    }
}