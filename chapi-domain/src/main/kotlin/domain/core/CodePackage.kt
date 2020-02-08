package domain.core

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject

@Serializable
class CodePackage(
    var Name: String = "",
    var ID: String = "",
    var CodeFiles: Array<CodeFile> = arrayOf(),
    var Extension: JsonElement = JsonObject(HashMap())
) {

}
