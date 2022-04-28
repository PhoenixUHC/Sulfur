package io.phoenix.sulfur.api

interface Metadata {
    operator fun set(key: String, value: String)
    operator fun get(key: String): String?
    /** Removes the value associated with the given key from the metadata hash */
    fun remove(key: String)
    /** Clears the metadata hash from the database */
    fun clear()
}
