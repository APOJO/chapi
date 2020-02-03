package chapi.ast.javaast

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

internal class JavaIdentAppTest {
    @Test
    internal fun shouldAnalysis() {
        val code = """
package chapi.ast.javaast;

import org.junit.Test;
"""
        val fileName = "import-demo.java"
        val codeFile = JavaIdentApp().analysis(code, fileName)
        assertEquals(codeFile.FullName, fileName)
    }

    @Test
    internal fun shouldGetFilePackageName() {
        val code = """
package chapi.ast.javaast;

import org.junit.Test;
"""
        val codeFile = JavaIdentApp().analysis(code, "")
        assertEquals(codeFile.PackageName, "chapi.ast.javaast")
    }

    @Test
    internal fun shouldGetFileImports() {
        val code = """
package chapi.ast.javaast;

import org.junit.Test;
"""
        val codeFile = JavaIdentApp().analysis(code, "")
        assertEquals(codeFile.Imports[0].Source, "org.junit.Test")
    }
}
