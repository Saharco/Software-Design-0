package storage

import il.ac.technion.cs.softwaredesign.storage.write
import il.ac.technion.cs.softwaredesign.storage.read

object CourseAppDatabase : Database {
    private const val path = "/"

    override fun collection(path: String): CollectionReference {
        return object : CourseAppCollection(
                this.path + path.replace("/", "")) {}

    }
}

abstract class CourseAppCollection(path: String) : CollectionReference {
    private val path: String = "$path/"

    override fun document(path: String): ExtendableDocumentReference {
        return object : CourseAppExtendableDocument(
                this.path + path.replace("/", "")) {}
    }
}

abstract class CourseAppExtendableDocument(path: String) : CourseAppDocument(path), ExtendableDocumentReference {
    override fun collection(path: String): CollectionReference {
        return object : CourseAppCollection(
                this.path + path.replace("/", "")) {}
    }
}

abstract class CourseAppDocument(path: String) : DocumentReference {
    protected val path: String = "$path/"
    private var data: HashMap<String, String?> = HashMap()

    override fun create(data: Map<String, String?>): CourseAppDocument {
        this.data = data as HashMap<String, String?>
        return this
    }

    override fun set(field: String, value: String?): CourseAppDocument {
        data[field] = value
        return this
    }

    override fun write() {
        if (data.isEmpty())
            throw IllegalStateException("Can\'t write empty document")
        if (documentExists())
            throw IllegalArgumentException("Document already exists")
        allocatePath()

        for (entry in data.entries) {
            writeData(entry.key, entry.value)
        }
    }

    override fun read(field: String): String {
        if (!isValidPath())
            throw IllegalArgumentException("Document does not exist")

        val key = ("$path$field/").toByteArray()
        val value = read(key)?.toList()
        if (value == null || value[0] == 0.toByte())
            throw IllegalArgumentException("Field does not exist")
        return String(value
                .takeLast(value.size - 1)
                .toByteArray())
    }

    override fun update() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun delete() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun isValidPath(): Boolean {
        val reg = Regex("(?<=/)")
        val pathSequence = ArrayList<String>(path.split(reg))
        var currentPath = pathSequence.removeAt(0)
        while (pathSequence.size > 2) {
            val extension = pathSequence.removeAt(0) + pathSequence.removeAt(0)
            currentPath += extension
            if (!pathExists(currentPath))
                return false
        }
        return true
    }

    private fun pathExists(pathToCheck: String): Boolean {
        val key = pathToCheck.toByteArray()
        val value = read(key) ?: return false
        return value[0] != 0.toByte()
    }


    private fun documentExists(): Boolean {
        return pathExists(path)
    }

    private fun allocatePath() {
        //TODO: refactor this (duplicate of isValidPath)
        val reg = Regex("(?<=/)")
        val pathSequence = ArrayList<String>(path.split(reg))
        var currentPath = pathSequence.removeAt(0)
        while (pathSequence.size > 2) {
            val extension = pathSequence.removeAt(0) + pathSequence.removeAt(0)
            currentPath += extension
            val key = currentPath.toByteArray()
            write(key, ByteArray(1) { 1.toByte() })
        }
    }

    private fun writeData(flag: String, value: String?) {

    }
}

