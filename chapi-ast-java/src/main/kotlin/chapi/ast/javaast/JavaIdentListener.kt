package chapi.ast.javaast

import chapi.ast.antlr.JavaParser
import chapi.ast.antlr.JavaParserBaseListener
import domain.core.*

class JavaIdentListener(fileName: String) : JavaParserBaseListener() {
    private var methodCalls = arrayOf<CodeCall>()
    private var localVars: HashMap<String, String> = HashMap<String, String>()
    private var methodMap: MutableMap<String, CodeFunction> = mutableMapOf<String, CodeFunction>()
    private var currentClz: String = ""
    private var currentClzExtend: String = ""
    private var hasEnterClass: Boolean = false

    private var classNodes: Array<CodeDataStruct> = arrayOf()
    private var classNodeQueue: Array<CodeDataStruct> = arrayOf()

    private var methodQueue: Array<CodeFunction> = arrayOf()

    private var imports: Array<CodeImport> = arrayOf()

    private var currentNode = CodeDataStruct()
    private var currentFunction = CodeFunction()
    private var currentType: String = ""

    private var codeFile: CodeFile = CodeFile(FullName = fileName)

    override fun enterPackageDeclaration(ctx: JavaParser.PackageDeclarationContext?) {
        super.enterPackageDeclaration(ctx)
        codeFile.PackageName = ctx?.qualifiedName()!!.text
    }

    override fun enterImportDeclaration(ctx: JavaParser.ImportDeclarationContext?) {
        super.enterImportDeclaration(ctx)
        val codeImport = CodeImport(Source = ctx!!.qualifiedName()!!.text)
        imports += codeImport

        codeFile.Imports += codeImport
    }

    override fun enterClassDeclaration(ctx: JavaParser.ClassDeclarationContext?) {
        super.enterClassDeclaration(ctx)

        if (currentNode.NodeName != "") {
            classNodeQueue += currentNode
            currentType = "InnerStructures"
        } else {
            currentType = "NodeName"
        }

        hasEnterClass = true
        currentClzExtend = ""

        if (ctx!!.IDENTIFIER() != null) {
            currentClz = ctx.IDENTIFIER().text
            currentNode.NodeName = currentClz
        }

        if (ctx.EXTENDS() != null) {
            currentClzExtend = ctx.typeType().text
            this.buildExtend(currentClzExtend)
        }

        if (ctx.IMPLEMENTS() != null) {
            for (_type in ctx.typeList().typeType()) {
                this.buildImplement(_type.text)
            }
        }

        currentNode.Type = currentType
    }

    override fun exitClassBody(ctx: JavaParser.ClassBodyContext?) {
        super.exitClassBody(ctx)

        hasEnterClass = false
        this.exitBodyAction()
    }

    private fun exitBodyAction() {
        currentNode.setMethodsFromMap(methodMap)
        classNodes += currentNode
    }

    override fun enterMethodDeclaration(ctx: JavaParser.MethodDeclarationContext?) {
        super.enterMethodDeclaration(ctx)

        var name = ctx!!.IDENTIFIER().text
        var typeType = ctx.typeTypeOrVoid().text

        val codePosition = CodePosition(
            StartLine = ctx.start.line,
            StartLinePosition = ctx.IDENTIFIER().symbol.startIndex,
            StopLine = ctx.stop.line,
            StopLinePosition = ctx.IDENTIFIER().symbol.stopIndex
        )

        val codeFunction = CodeFunction(
            Name = name,
            ReturnType = typeType,
            Position = codePosition
        )

        val params = ctx.formalParameters()
        if (params != null) {
            if (params.getChild(0) == null || params.text == "()" || params.getChild(0) == null) {
                this.updateCodeFunction(codeFunction)
                return
            }

            codeFunction.Parameters = this.buildMethodParameters(params)
        }

        this.updateCodeFunction(codeFunction)
    }

    private fun buildMethodParameters(params: JavaParser.FormalParametersContext?): Array<CodeProperty> {
        var methodParams = arrayOf<CodeProperty>()
        val parameterList = params!!.getChild(1) as JavaParser.FormalParameterListContext
        for (param in parameterList.formalParameter()) {
            val paramCtx = param as JavaParser.FormalParameterContext
            val paramType = paramCtx.typeType().text
            val paramValue = paramCtx.variableDeclaratorId().IDENTIFIER().text
            localVars[paramValue] = paramType

            var parameter = CodeProperty(TypeValue = paramValue, TypeType = paramType)

            methodParams += parameter
        }

        return methodParams
    }

    override fun exitMethodDeclaration(ctx: JavaParser.MethodDeclarationContext?) {
        super.exitMethodDeclaration(ctx)

        currentFunction = CodeFunction()
    }

    override fun enterMethodCall(ctx: JavaParser.MethodCallContext?) {
        val codeCall = CodeCall()

        val targetCtx = ctx!!.parent.getChild(0)
        var targetType = this.parseTargetType(targetCtx.text)

        if (targetCtx.getChild(0) != null) {
            val currentCtx = targetCtx.getChild(0)
            when (currentCtx::class.simpleName) {
                "MethodCallContext" -> {
                    targetType = (currentCtx as JavaParser.MethodCallContext).IDENTIFIER().text
                }
            }
        }

        val callee = ctx.getChild(0).text

        buildMethodCallLocation(codeCall, ctx, callee)
        buildMethodCallMethod(codeCall, callee, targetType, ctx)
        buildMethodCallParameters(codeCall, ctx)

        sendResultToMethodCallMap(codeCall)
    }

    private fun buildMethodCallParameters(codeCall: CodeCall, ctx: JavaParser.MethodCallContext) {
        if (ctx.expressionList() != null) {
            var parameters = arrayOf<CodeProperty>()
            for (exprCtx in ctx.expressionList().expression()) {
                val parameter = CodeProperty(TypeType = "", TypeValue = exprCtx.text)
                parameters += parameter
            }

            codeCall.Parameters = parameters
        }
    }

    private fun buildMethodCallLocation(codeCall: CodeCall, ctx: JavaParser.MethodCallContext, callee: String?) {
        codeCall.Position.StartLine = ctx.start.line
        codeCall.Position.StartLinePosition = ctx.start.charPositionInLine
        codeCall.Position.StopLine = ctx.stop.line
        codeCall.Position.StopLinePosition = ctx.stop.charPositionInLine
    }

    private fun sendResultToMethodCallMap(codeCall: CodeCall) {
        methodCalls += codeCall
        val currentMethodName = getMethodMapName(currentFunction)
        var method = methodMap[currentMethodName]
        if (method != null) {
            method.FunctionCalls += codeCall
            methodMap[currentMethodName] = method
        }
    }

    private fun buildMethodCallMethod(
        codeCall: CodeCall,
        callee: String = "",
        targetType: String?,
        ctx: JavaParser.MethodCallContext
    ) {
        var packageName = codeFile.PackageName
        var methodName = callee

        codeCall.Package = packageName
        codeCall.FunctionName = methodName
        codeCall.NodeName = targetType ?: ""
    }

    private fun parseTargetType(targetType: String?): String? {
        return targetType
    }

    private fun updateCodeFunction(codeFunction: CodeFunction) {
        currentFunction = codeFunction
        methodQueue += currentFunction
        methodMap[getMethodMapName(codeFunction)] = codeFunction
    }

    private fun getMethodMapName(function: CodeFunction): String {
        var name = function.Name
        if (name != "" && methodQueue.size > 1) {
            name = methodQueue[methodQueue.size - 1].Name
        }

        return codeFile.PackageName + "." + currentClz + "." + name + ":" + function.Position.StartLine.toString()
    }

    private fun buildImplement(typeText: String) {
        var target = this.warpTargetFullType(typeText)
        if (target == "") {
            target = typeText
        }
        currentNode.Implements += target
    }

    private fun buildExtend(extendName: String) {
        val target: String = this.warpTargetFullType(extendName)
        if (target != "") {
            currentNode.Extend = target
        } else {
            currentNode.Extend = extendName
        }
    }

    private fun warpTargetFullType(targetType: String): String {
        var callType = ""
        if (currentClz == targetType) {
            callType = "self"
            return codeFile.PackageName + "." + targetType
        }

        val split = targetType.split(".")
        var first = split[0]
        val pureTargetType = first.replace("[", "").replace("]", "")

        if (pureTargetType != "") {
            for (imp in imports) {
                if (imp.Source.endsWith(pureTargetType)) {
                    callType = "chain"
                    return imp.Source
                }
            }
        }

        // todo: add ident clzs

        if (pureTargetType == "super" || pureTargetType == "this") {
            for (imp in imports) {
                if (imp.Source.endsWith(currentClzExtend)) {
                    callType = "super"
                    return imp.Source
                }
            }
        }

        // todo: add identMap
        return ""
    }

    fun getNodeInfo(): CodeFile {
        codeFile.DataStructures = classNodes
        return codeFile
    }
}

