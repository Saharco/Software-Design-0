package storage

import java.security.MessageDigest

/**
 *
 */
class Hasher {
    fun hash(message: String): String {
        val bytes = message.toByteArray()
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(bytes)
        return digest.fold("", { str, it -> str + "%02x".format(it) })
    }

    operator fun invoke(message: String): String {
        return hash(message)
    }
}