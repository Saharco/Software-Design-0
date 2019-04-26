package storage

interface Database {
    fun collection(name: String): CollectionReference
}

interface CollectionReference {
    fun document(name: String): ExtendableDocumentReference
}

interface ExtendableDocumentReference : DocumentReference {
    fun collection(name: String): CollectionReference
}

interface DocumentReference {
    fun create(data: Map<String, String>): DocumentReference

    fun set(field: Pair<String,String>): CourseAppDocument

    fun write()

    fun read(field: String): String?

    fun update()

    fun delete()

    fun delete(fields: List<String>)

    fun exists(): Boolean
}