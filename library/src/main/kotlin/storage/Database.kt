package storage

interface Database {
    fun collection(path: String): CollectionReference
}

interface CollectionReference {
    fun document(path: String): ExtendableDocumentReference
}

interface ExtendableDocumentReference : DocumentReference {
    fun collection(path: String): CollectionReference
}

interface DocumentReference {
    fun create(data: Map<String, String?>): DocumentReference

    fun set(field: String, value: String?): CourseAppDocument

    fun write()

    fun read(field: String): String

    fun update()

    fun delete()
}