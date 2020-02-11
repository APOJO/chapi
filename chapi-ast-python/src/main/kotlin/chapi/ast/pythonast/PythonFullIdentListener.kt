package chapi.ast.pythonast

import chapi.ast.antlr.PythonParser
import domain.core.CodeDataStruct
import domain.core.CodeFile
import domain.core.CodeFunction

class PythonFullIdentListener(var fileName: String) : PythonAstBaseListener() {
    private var currentFunction: CodeFunction = CodeFunction()
    private var hasEnterClass = false
    private var codeFile: CodeFile = CodeFile(FullName = fileName)

    private var currentNode: CodeDataStruct = CodeDataStruct()

    override fun enterClassdef(ctx: PythonParser.ClassdefContext?) {
        hasEnterClass = true
        currentNode = CodeDataStruct(
            NodeName = ctx!!.name().text
        )

        if (ctx.arglist() != null) {
            for (argumentContext in ctx.arglist().argument()) {
                currentNode.MultipleExtend += argumentContext.text
            }
        }
    }

    override fun exitClassdef(ctx: PythonParser.ClassdefContext?) {
        hasEnterClass = false
        codeFile.DataStructures += currentNode
        currentNode = CodeDataStruct()
    }

    override fun enterFuncdef(ctx: PythonParser.FuncdefContext?) {
        val funcName = ctx!!.name().text
        currentFunction = CodeFunction(
            Name = funcName
        )
    }

    override fun exitFuncdef(ctx: PythonParser.FuncdefContext?) {
        currentNode.Functions += currentFunction
        currentFunction = CodeFunction()
    }

    fun getNodeInfo(): CodeFile {
        return this.codeFile
    }
}
