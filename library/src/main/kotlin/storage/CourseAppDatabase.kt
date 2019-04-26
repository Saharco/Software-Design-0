package storage

object CourseAppDatabase : Database {
    private const val path = "/"

    override fun collection(name: String): CollectionReference {
        return object : CourseAppCollection(
                path + name.replace("/", "")) {}

    }
}