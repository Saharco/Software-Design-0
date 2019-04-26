package storage

abstract class CourseAppCollection internal constructor(path: String) : CollectionReference {
    private val path: String = "$path/"

    override fun document(name: String): ExtendableDocumentReference {
        return object : CourseAppExtendableDocument(
                path + name.hashCode()) {}
    }
}