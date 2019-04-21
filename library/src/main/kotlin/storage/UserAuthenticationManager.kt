package storage

import il.ac.technion.cs.softwaredesign.storage.read
import il.ac.technion.cs.softwaredesign.storage.write
import java.util.*

interface UserAuthenticationCRUD {

    fun readUserPassword(username: String): String?

    fun readUserToken(username: String): String?

    fun createUserToken(username: String)

    fun deleteUserToken(username: String)

    fun createUser(username: String, password: String)
}

/**
 *
 */
object UserAuthenticationManager : UserAuthenticationCRUD {

    enum class DataType {
        PASSWORD, TOKEN;

        override fun toString(): String {
            return this.name
        }
    }

    private fun readUserData(username: String, dataType: DataType): String? {
        val key = ("$username/$dataType").toByteArray()
        val value = read(key)
        return if (value != null) String(value) else null
    }

    private fun writeUserData(username: String, data: String, dataType: DataType) {
        val key = ("$username/$dataType").toByteArray()
        val value = data.toByteArray()
        write(key, value)
    }

    private fun userExists(username: String): Boolean {
        return readUserPassword(username) != null
    }

    override fun readUserPassword(username: String): String? {
        return readUserData(username, DataType.PASSWORD)
    }

    override fun readUserToken(username: String): String? {
        val token = readUserData(username, DataType.TOKEN) ?:
            throw IllegalArgumentException("User does not exist")
        if (token.isEmpty()) return null
        return token
    }

    override fun createUserToken(username: String) {
        if (!userExists(username))
            throw IllegalArgumentException("User does not exist")
        val uuid = UUID.randomUUID().toString()
        writeUserData(username, uuid, DataType.TOKEN)
    }

    override fun deleteUserToken(username: String) {
        if (!userExists(username))
            throw IllegalArgumentException("User does not exist")
        writeUserData(username, "", DataType.TOKEN)
    }

    override fun createUser(username: String, password: String) {
        if (userExists(username))
            throw IllegalArgumentException("Username already exists")
        writeUserData(username, password, DataType.PASSWORD)
    }
}