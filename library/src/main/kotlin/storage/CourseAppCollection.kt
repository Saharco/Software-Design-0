package storage

abstract class CourseAppCollection internal constructor(path: String) : CollectionReference {
    private val path: String = "$path/"

    override fun document(name: String): ExtendableDocumentReference {
        val hasher = Hasher()
        return object : CourseAppExtendableDocument(
                path + hasher(name)) {}
    }
}