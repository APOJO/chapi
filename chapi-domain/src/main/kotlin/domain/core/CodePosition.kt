package domain.core

import kotlinx.serialization.*
import kotlinx.serialization.json.*

@Serializable
class CodePosition(
    var StartLine: Int = 0,
    var StartLinePosition: Int = 0,
    var StopLine: Int = 0,
    var StopLinePosition: Int = 0
) {

}
