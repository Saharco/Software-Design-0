package storage

/**
 * This class is a reference to the database's root whose sole purpose is
 * to branch to different collections that contain the database's information (documents)
 *
 * This is the *root* of the file system
 *
 * @see CollectionReference
 * @see ExtendableDocumentReference
 */
interface Database {
    /**
     * Access a collection in the database's root. It will be created if it does not exist
     *
     * @param name: name of the collection
     * @return the collection's reference
     */
    fun collection(name: String): CollectionReference
}

/**
 * Reference to a collection of documents inside the database. This class's sole purpose is to contain documents
 * Akin to *folders* in the file system.
 *
 * @see ExtendableDocumentReference
 */
interface CollectionReference {
    /**
     * Access a document stored inside this collection. It will be created upon writing to it if it does not exist
     *
     * @param name: name of the document
     * @return the document's reference
     */
    fun document(name: String): ExtendableDocumentReference
}

/**
 * Reference to a document inside the database that may also contain other collections.
 * @see DocumentReference
 */
interface ExtendableDocumentReference : DocumentReference {
    /**
     * Access a collection inside this document. It will be created if it does not exist
     *
     * @param name: name of the collection
     * @return the collection's reference
     */
    fun collection(name: String): CollectionReference
}

/**
 *  Reference to a document in the database which stores information in a field-value fashion.
 *
 *  Akin to *files* in the file system
 */
interface DocumentReference {

    /**
     * Sets a field in the document.
     * The information will be stored upon creating the document with a [write]
     *
     * @param field: 1st value: field's name. 2nd value: data
     */
    fun set(field: Pair<String, String>): CourseAppDocument

    /**
     * Sets multiple fields in the document.
     * The information will be stored upon creating the document with a [write]
     *
     * @param data: map of field-value information
     */
    fun set(data: Map<String, String>): DocumentReference

    /**
     * Write the document to the database.
     *
     * This is a *terminal* operation
     */
    fun write()

    /**
     * Read a document's field from the database.
     *
     * This is a *terminal* operation
     *
     * @param field: name of the field from which the desired information will be read
     */
    fun read(field: String): String?

    /**
     * Update information of a document in the database. This operation may be performed on an existing document
     *
     * This is a *terminal* operation
     */
    fun update()

    /**
     * Delete a document from the database.
     *
     * This is a *terminal* operation
     */
    fun delete()

    /**
     * Delete a document's fields from the database.
     *
     * This is a *terminal* operation
     *
     * @param fields: list of fields to be deleted
     */
    fun delete(fields: List<String>)

    /**
     * Returns whether or not the document exists in the database.
     *
     * This is a *terminal* operation
     */
    fun exists(): Boolean
}