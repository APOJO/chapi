package chapi.ast.scalaast

import chapi.ast.antlr.ScalaParser
import chapi.domain.core.CodeContainer
import chapi.domain.core.CodeDataStruct
import chapi.domain.core.DataStructType

class ScalaFullIdentListener(var fileName: String) : ScalaAstBaseListener() {
    private var codeContainer: CodeContainer = CodeContainer(FullName = fileName)

    override fun enterObjectDef(ctx: ScalaParser.ObjectDefContext?) {
        val objectName = ctx!!.Id().text
        val codeDataStruct = CodeDataStruct(
            Type = DataStructType.OBJECT,
            NodeName = objectName,
            FilePath = codeContainer.FullName
        )

        codeContainer.DataStructures += codeDataStruct
    }

    fun getNodeInfo(): CodeContainer {
        return this.codeContainer
    }
}
