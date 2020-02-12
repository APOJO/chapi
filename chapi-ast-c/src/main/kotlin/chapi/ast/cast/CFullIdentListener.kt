package chapi.ast.cast

import chapi.ast.antlr.CBaseListener
import chapi.ast.antlr.CParser
import domain.core.*

open class CFullIdentListener(fileName: String) : CBaseListener() {
    private var currentDataStruct = CodeDataStruct()
    private var codeFile: CodeFile = CodeFile(FullName = fileName)

    override fun enterFunctionDefinition(ctx: CParser.FunctionDefinitionContext?) {
        super.enterFunctionDefinition(ctx)
    }

    override fun enterIncludeDeclaration(ctx: CParser.IncludeDeclarationContext?) {
        val importName = ctx!!.includeIdentifier().text
        val imp = CodeImport(
            Source = importName
        )
        codeFile.Imports += imp
    }

    override fun enterStructOrUnionSpecifier(ctx: CParser.StructOrUnionSpecifierContext?) {
        val codeDataStruct = CodeDataStruct()
        if (ctx!!.Identifier() != null) {
            codeDataStruct.NodeName = ctx.Identifier().text
        }

        currentDataStruct = codeDataStruct

        if (ctx.structDeclarationList() != null) {
            val structDecl = ctx.structDeclarationList().structDeclaration()
            val specifierQualifierList = structDecl.specifierQualifierList()
            if (specifierQualifierList != null) {
                val key = specifierQualifierList.typeSpecifier().text
                val value = specifierQualifierList.specifierQualifierList().text

                val field = CodeField(
                    TypeType = key,
                    TypeValue = value
                )
                codeDataStruct.Fields += field
            }

        }

        codeFile.DataStructures += codeDataStruct
    }

    fun getNodeInfo(): CodeFile {
        return codeFile
    }
}
