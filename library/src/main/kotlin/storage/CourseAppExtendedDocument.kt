package storage

abstract class CourseAppExtendableDocument internal constructor(path: String) :
        CourseAppDocument(path), ExtendableDocumentReference {

    override fun collection(name: String): CollectionReference {
        return object : CourseAppCollection(
                path + name.replace("/", "")) {}
    }
}