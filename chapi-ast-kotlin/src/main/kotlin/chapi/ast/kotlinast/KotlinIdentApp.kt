package chapi.ast.kotlinast

import chapi.ast.antlr.KotlinLexer
import chapi.ast.antlr.KotlinParser
import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
import org.antlr.v4.runtime.tree.ParseTreeWalker

open class KotlinIdentApp {
    open fun analysis(str: String) {
        val context = this.parse(str).kotlinFile()
        val listener = KotlinIdentListener()

        ParseTreeWalker().walk(listener, context)

        listener.getNodeInfo()
    }

    open fun parse(str: String): KotlinParser {
        val fromString = CharStreams.fromString(str)
        val lexer = KotlinLexer(fromString)
        val tokenStream = CommonTokenStream(lexer)
        val parser = KotlinParser(tokenStream)
        return parser
    }
}
