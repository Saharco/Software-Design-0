package il.ac.technion.cs.softwaredesign

import storage.CourseAppDatabase
import java.lang.IllegalArgumentException
import java.util.*

typealias db = CourseAppDatabase

/**
 * This is the class implementing CourseApp, a course discussion group system.
 *
 * You may assume that [CourseAppInitializer.setup] was called before this class was instantiated.
 *
 * Currently specified:
 * + User authentication.
 */
class CourseApp {
    /**
     * Log in a user identified by [username] and [password], returning an authentication token that can be used in
     * future calls. If this username did not previously log in to the system, it will be automatically registered with
     * the provided password. Otherwise, the password will be checked against the previously provided password.
     *
     * Note: Allowing enumeration of valid usernames is not a good property for a system to have, from a security
     * standpoint. But this is the way this system will work.
     *
     * This is a *create* command.
     *
     * @throws IllegalArgumentException If the password does not match the username, or the user is already logged in.
     * @return An authentication token to be used in other calls.
     */
    fun login(username: String, password: String): String {
        val userDocument = db.collection("users")
                .document(username)
        val storedPassword = userDocument.read("password")

        if (storedPassword != null && storedPassword != password)
            throw IllegalArgumentException("Incorrect password")
        if (userDocument.read("token") != null)
            throw IllegalArgumentException("User already logged in")

        val token = UUID.randomUUID().toString()
        userDocument.set(Pair("token", token))
        if (storedPassword == null)
            userDocument.set(Pair("password", password))

        userDocument.write()

        db.collection("tokens")
                .document(token)
                .set(Pair("username", username))
                .write()

        return token
    }

    /**
     * Log out the user with this authentication [token]. The [token] will be invalidated and can not be used for future
     * calls.
     *
     * This is a *delete* command.
     *
     * @throws IllegalArgumentException If the auth [token] is invalid.
     */
    fun logout(token: String) {
        val tokenDocument = db.collection("tokens")
                .document(token)
        val username = tokenDocument.read("username")
                ?: throw IllegalArgumentException("Invalid token")

        tokenDocument.delete()

        db.collection("users")
                .document(username)
                .delete(listOf("token"))
    }

    /**
     * Indicate the status of [username] in the application.
     *
     * A valid authentication [token] (for *any* user) is required to perform this operation.
     *
     * This is a *read* command.
     *
     * @throws IllegalArgumentException If the auth [token] is invalid.
     * @return True if [username] exists and is logged in, false if it exists and is not logged in, and null if it does
     * not exist.
     */
    fun isUserLoggedIn(token: String, username: String): Boolean? {
        if (!db.collection("tokens")
                        .document(token)
                        .exists())
            throw IllegalArgumentException("Invalid token")

        return try {
            val otherToken = db.collection("users")
                    .document(username)
                    .read("token")
            otherToken != null
        } catch (e: IllegalArgumentException) {
            null
        }
    }
}