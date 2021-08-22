package com.pipeline.content

class ContentId : java.io.Serializable {

    lateinit var filename: String
    lateinit var lineNumber: String

    constructor()

    constructor(filename: String, lineNumber: String) {
        this.filename = filename
        this.lineNumber = lineNumber
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ContentId

        if (filename != other.filename) return false
        if (lineNumber != other.lineNumber) return false

        return true
    }

    override fun hashCode(): Int {
        var result = filename.hashCode()
        result = 31 * result + lineNumber.hashCode()
        return result
    }

}