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
            val result = db[firstArg<ByteArray>().toString(charset)]?.toByteArray()
            if (result != null)
                // wait for the specified amount of time
                Thread.sleep(result.size.toLong())
            result
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

    @Test
    fun `system can hold lots of distinct users and tokens`() {
        val strings = ArrayList<String>()
        populateWithRandomStrings(strings)
        val users = strings.distinct()
        val systemSize = users.size
        val tokens = ArrayList<String>()

        for (i in 0 until systemSize) {
            // Dont care about exact values here: username & password are the same for each user
            val token = app.login(users[i], users[i])
            tokens.add(token)
        }

        assertEquals(tokens.size, users.size)

        for (token in tokens) {
            app.logout(token)
        }
    }

    private fun populateWithRandomStrings(list: ArrayList<String>, amount: Int = 1000,
                                          maxSize: Int = 20, charPool: List<Char>? = null) {
        val pool = charPool ?: ('a'..'z') + ('A'..'Z') + ('0'..'9') + '/'
        for (i in 0 until amount) {
            val randomString = (1..maxSize)
                    .map { kotlin.random.Random.nextInt(0, pool.size) }
                    .map(pool::get)
                    .joinToString("")
            list.add(randomString)
        }
    }
}