package chapi.ast.goast

import chapi.ast.antlr.GoParser
import domain.core.*
import org.antlr.v4.runtime.tree.ParseTree

class GoFullIdentListener(var fileName: String) : GoAstListener() {
    private var codeFile: CodeFile = CodeFile(FullName = fileName)

    private var currentNode = CodeDataStruct()
    private var defaultNode = CodeDataStruct()
    private var structMap = mutableMapOf<String, CodeDataStruct>()

    private var currentFunction = CodeFunction(IsConstructor = false)

    override fun enterImportDecl(ctx: GoParser.ImportDeclContext?) {
        super.enterImportDecl(ctx)
    }

    override fun enterPackageClause(ctx: GoParser.PackageClauseContext?) {
        codeFile.PackageName = ctx!!.IDENTIFIER().text
    }

    override fun enterImportSpec(ctx: GoParser.ImportSpecContext?) {
        val originSource = ctx!!.importPath().text
        val sourceName = originSource.replace("\"", "")
        val codeImport = CodeImport(Source = sourceName)

        if (ctx.DOT() != null) {
            codeImport.AsName = "."
        }

        if (ctx.IDENTIFIER() != null) {
            codeImport.UsageName += ctx.IDENTIFIER().text
        }

        codeFile.Imports += codeImport
    }

    override fun enterFunctionDecl(ctx: GoParser.FunctionDeclContext?) {
        val funcName = ctx!!.IDENTIFIER().text

        val codeFunction = CodeFunction(
            Name = funcName,
            Package = codeFile.PackageName
        )

        codeFunction.Parameters = this.buildParameters(ctx.signature().parameters())
        codeFunction.MultipleReturns = this.buildReturnTypeFromSignature(codeFunction, ctx.signature())

        currentFunction = codeFunction
    }

    override fun exitFunctionDecl(ctx: GoParser.FunctionDeclContext?) {
        defaultNode.Functions += currentFunction
        currentFunction = CodeFunction()
    }

    override fun enterTypeDecl(ctx: GoParser.TypeDeclContext?) {
        val typeSpecs = ctx!!.typeSpec()
        for (typeSpec in typeSpecs) {
            buildTypeSpec(typeSpec)
        }
    }

    override fun enterMethodDecl(ctx: GoParser.MethodDeclContext?) {
        val funcName = ctx!!.IDENTIFIER().text
        val codeFunction = CodeFunction(
            Name = funcName
        )

        codeFunction.MultipleReturns = this.buildReturnTypeFromSignature(codeFunction, ctx.signature())
        codeFunction.Parameters = this.buildParameters(ctx.signature().parameters())

        val receiverName = this.getStructNameFromReceiver(ctx.receiver().parameters())!!

        this.addReceiverToStruct(receiverName, codeFunction)
    }

    override fun exitMethodDecl(ctx: GoParser.MethodDeclContext?) {
//        defaultNode.Functions += currentFunction
//        currentFunction = CodeFunction()
    }

    private fun addReceiverToStruct(receiverName: String, codeFunction: CodeFunction) {
        if (structMap[receiverName] == null) {
            val struct = createStructByName(receiverName)

            struct.Functions += codeFunction
            structMap[receiverName] = struct
        } else {
            structMap[receiverName]!!.Functions += codeFunction
        }
    }

    private fun buildTypeSpec(typeSpec: GoParser.TypeSpecContext) {
        val identifyName = typeSpec.IDENTIFIER().text
        val typeLit = typeSpec.type_().typeLit()
        if (typeLit != null) {
            val typeChild = typeLit.getChild(0)
            when (typeChild::class.java.simpleName) {
                "StructTypeContext" -> {
                    buildStruct(identifyName, typeChild)
                }
                else -> {

                }
            }
        }
    }

    private fun buildStruct(identifyName: String, typeChild: ParseTree?) {
        val struct = createStructByName(identifyName)
        val structTypeCtx = typeChild as GoParser.StructTypeContext

        val fields = buildStructFields(structTypeCtx)

        if (structMap[identifyName] != null) {
            structMap[identifyName]!!.Fields = fields
        } else {
            struct.Fields = fields
            structMap[identifyName] = struct
        }
    }

    private fun createStructByName(identifyName: String): CodeDataStruct {
        val struct = CodeDataStruct(
            NodeName = identifyName,
            Package = codeFile.PackageName,
            FilePath = codeFile.FullName
        )
        return struct
    }

    private fun buildStructFields(structTypeCtx: GoParser.StructTypeContext): Array<CodeField> {
        var fields: Array<CodeField> = arrayOf()
        for (fieldDeclContext in structTypeCtx.fieldDecl()) {
            val typeValue = fieldDeclContext.identifierList().text
            val typeType = fieldDeclContext.type_().text

            val field = CodeField(
                TypeType = typeType,
                TypeValue = typeValue
            )

            fields += field
        }

        return fields
    }

    override fun enterExpression(ctx: GoParser.ExpressionContext?) {
        val firstChild = ctx!!.getChild(0)
        when (firstChild::class.java.simpleName) {
            "PrimaryExprContext" -> {
                if (firstChild.getChild(1) != null) {
                    this.buildPrimaryExprCtx(firstChild)
                }
            }
        }
    }

    private fun buildPrimaryExprCtx(primaryExprCtx: ParseTree?) {
        when (primaryExprCtx!!.getChild(1)::class.java.simpleName) {
            "ArgumentsContext" -> {
                val codeCall = CodeCall(
                    NodeName = primaryExprCtx.getChild(0).text
                )
                val argumentsContext = primaryExprCtx.getChild(1) as GoParser.ArgumentsContext
                for (expressionContext in argumentsContext.expressionList().expression()) {
                    val codeProperty = CodeProperty(
                        TypeValue = expressionContext.text,
                        TypeType = ""
                    )
                    codeCall.Parameters += codeProperty
                }
                currentFunction.FunctionCalls += codeCall
            }
        }
    }

    fun getNodeInfo(): CodeFile {

        for (entry in structMap) {
            codeFile.DataStructures += entry.value
        }

        if (defaultNode.Functions.isNotEmpty()) {
            codeFile.DataStructures += defaultNode
        }

        return codeFile
    }
}
