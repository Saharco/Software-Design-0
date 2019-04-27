package storage

/**
 * Implementation of [Database].
 * Database's root reference is a singleton
 */
object CourseAppDatabase : Database {
    private const val path = "/"

    override fun collection(name: String): CollectionReference {
        return object : CourseAppCollection(
                path + name.replace("/", "")) {}

    }
}