import il.ac.technion.cs.softwaredesign.storage.read
import il.ac.technion.cs.softwaredesign.storage.write
import io.mockk.every
import io.mockk.mockkStatic
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import storage.CourseAppDatabase
import java.lang.IllegalArgumentException


class CourseAppDatabaseTest {
    private var storageMock = hashMapOf<String, String>()
    private val db = CourseAppDatabase

    init {
        val charset = charset("UTF-8")

        mockkStatic("il.ac.technion.cs.softwaredesign.storage.SecureStorageKt")
        every {
            read(any())
        } answers {
            val result = storageMock[firstArg<ByteArray>().toString(charset)]?.toByteArray()
            if (result != null)
            // wait for the specified amount of time
                Thread.sleep(result.size.toLong())
            result
        }
        every {
            write(any(), any())
        } answers {
            storageMock[firstArg<ByteArray>().toString(charset)] = secondArg<ByteArray>().toString(charset)
        }
    }

    @BeforeEach
    fun resetDatabase() {
        storageMock = hashMapOf()
    }

    @Test
    fun `single field write in a document is properly read after document is written`() {
        db.collection("programming languages")
                .document("kotlin")
                .set(Pair("isCool", "true"))
                .write()

        val result = db.collection("programming languages")
                .document("kotlin")
                .read("isCool")

        assertEquals(result, "true")
    }

    @Test
    fun `multiple fields write in a document are properly read after document is written`() {
        val data = hashMapOf("date" to "April 21, 2019",
                "isColored" to "true",
                "isPublic" to "false",
                "takenAt" to "technion")

        val documentRef = db.collection("users")
                .document("sahar cohen")
                .collection("photos")
                .document("awkward photo")

        documentRef.set(data)
                .write()

        assertEquals(documentRef.read("date"), "April 21, 2019")
        assertEquals(documentRef.read("isColored"), "true")
        assertEquals(documentRef.read("isPublic"), "false")
        assertEquals(documentRef.read("takenAt"), "technion")
    }

    @Test
    fun `reading fields or documents that do not exist should return null`() {
        db.collection("users")
                .document("sahar")
                .set(Pair("eye color", "green"))
                .write()

        var result = db.collection("users")
                .document("sahar")
                .read("hair color")

        assertNull(result)

        result = db.collection("users")
                .document("yuval")
                .read("hair color")

        assertNull(result)
    }

    @Test
    fun `writing to a document that already exists should throw IllegalArgumentException`() {
        db.collection("users")
                .document("sahar")
                .set(Pair("eye color", "green"))
                .write()

        assertThrows<IllegalArgumentException> {
            db.collection("users")
                    .document("sahar")
                    .set(Pair("surname", "cohen"))
                    .write()
        }
    }

    @Test
    fun `writing an empty document should throw IllegalStateException`() {
        assertThrows<IllegalStateException> {
            db.collection("users")
                    .document("sahar")
                    .write()
        }
    }

    @Test
    fun `reading document after deletion should return null`() {
        val userRef = db.collection("users")
                .document("sahar")

        userRef.set(Pair("eye color", "green"))
                .write()

        userRef.delete()

        val result = userRef.read("eye color")
        assertNull(result)

    }

    @Test
    fun `deleting some fields in a document should not delete the others`() {
        val data = hashMapOf("date" to "April 21, 2019",
                "isColored" to "true",
                "isPublic" to "false",
                "takenAt" to "technion")

        val documentRef = db.collection("users")
                .document("sahar cohen")
                .collection("photos")
                .document("awkward photo")

        documentRef.set(data)
                .write()

        documentRef.delete(listOf("isColored", "isPublic"))

        assertNull(documentRef.read("isColored"))
        assertEquals(documentRef.read("date"), "April 21, 2019")
        assertEquals(documentRef.read("takenAt"), "technion")
    }

    @Test
    fun `can check if a document exists`() {
        var documentRef = db.collection("users")
                .document("sahar")

        documentRef.set(Pair("eye color", "green"))
                .write()

        assertTrue(documentRef.exists())

        documentRef = db.collection("students")
                .document("sahar")

        assertFalse(documentRef.exists())
    }

    @Test
    fun `can update existing and non existing fields in a document which may or may not exist`() {
        var documentRef = db.collection("users")
                .document("sahar")

        documentRef.set(Pair("favorite food", "pizza"))
                .write()

        documentRef.set(Pair("favorite food", "ice cream"))
                .set(Pair("favorite animal", "dog"))
                .update()

        assertEquals(documentRef.read("favorite food"), "ice cream")
        assertEquals(documentRef.read("favorite animal"), "dog")

        documentRef = db.collection("users")
                .document("yuval")

        documentRef.set(Pair("favorite food", "pizza"))
                .update()

        assertEquals(documentRef.read("favorite food"), "pizza")
    }

    @Test
    fun `any character can be used as a document's name`() {
        val chars = generateCharactersList()
        val collectionRef = db.collection("root")
        val data = Pair("key", "value")
        for (str in chars) {
            collectionRef.document(str)
                    .set(data)
                    .write()
        }
    }

    private fun generateCharactersList(): ArrayList<String> {
        val list = ArrayList<String>()
        for (i in 0 until 128) {
            list.add((i.toChar().toString()))
        }
        return list
    }
}


