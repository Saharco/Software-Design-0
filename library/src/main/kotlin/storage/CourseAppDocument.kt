package storage

import il.ac.technion.cs.softwaredesign.storage.read
import il.ac.technion.cs.softwaredesign.storage.write

abstract class CourseAppDocument internal constructor(path: String) : DocumentReference {
    protected val path: String = "$path/"
    private var data: HashMap<String, String> = HashMap()

    override fun create(data: Map<String, String>): CourseAppDocument {
        this.data = data as HashMap<String, String>
        return this
    }

    override fun set(field: Pair<String, String>): CourseAppDocument {
        data[field.first] = field.second
        return this
    }

    override fun write() {
        if (data.isEmpty())
            throw IllegalStateException("Can\'t write empty document")
        if (exists())
            throw IllegalArgumentException("Document already exists")
        allocatePath()

        for (entry in data.entries) {
            writeEntry("$path${entry.key}/", entry.value)
        }
    }

    override fun read(field: String): String? {
        if (!isValidPath())
            return null

        val key = ("$path$field/").toByteArray()
        val value = read(key)?.toList()
        if (value == null || value[0] == 0.toByte())
            return null
        return String(value
                .takeLast(value.size - 1)
                .toByteArray())
    }

    override fun update() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun delete() {
        deleteEntry(path)
    }

    override fun delete(fields: List<String>) {
        for (field in fields) {
            deleteEntry("$path$field/")
        }
    }

    override fun exists(): Boolean {
        return pathExists(path)
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

    private fun statusBlock(activated: Boolean = true): ByteArray {
        val status = if (activated) 1 else 0
        return ByteArray(1) { status.toByte() }
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
            write(key, statusBlock(activated = true))
        }
    }

    private fun writeEntry(flag: String, value: String) {
        val key = flag.toByteArray()
        val data = statusBlock(activated = true) + value.toByteArray()
        write(key, data)
    }

    private fun deleteEntry(path: String) {
        val key = path.toByteArray()
        val store = statusBlock(activated = false)
        write(key, store)
    }

}

