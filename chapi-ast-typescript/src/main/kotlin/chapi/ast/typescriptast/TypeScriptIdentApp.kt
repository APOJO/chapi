package chapi.ast.typescriptast

import chapi.ast.antlr.TypeScriptLexer
import chapi.ast.antlr.TypeScriptParser
import domain.core.CodeFile
import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
import org.antlr.v4.runtime.tree.ParseTreeWalker

open class TypeScriptIdentApp {
    open fun analysis(str: String, fileName: String): CodeFile {
        val context = this.parse(str).initializer()
        val listener = TypeScriptIdentListener(fileName)

        ParseTreeWalker().walk(listener, context)

        return listener.getNodeInfo()
    }

    private fun parse(str: String): TypeScriptParser {
        val fromString = CharStreams.fromString(str)
        val lexer = TypeScriptLexer(fromString)
        val tokenStream = CommonTokenStream(lexer)
        return TypeScriptParser(tokenStream)
    }
}
