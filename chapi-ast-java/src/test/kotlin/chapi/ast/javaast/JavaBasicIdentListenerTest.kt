package chapi.ast.javaast

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class JavaBasicIdentListenerTest {
    @Test
    internal fun shouldIdentifyImport() {
        val code = """
package chapi.ast.javaast;

import org.junit.Test;
"""
        val codeFile = JavaFullIdent().identBasicInfo(code, "basic")
        kotlin.test.assertEquals(codeFile.PackageName, "chapi.ast.javaast")
    }

    @Test
    internal fun shouldIdentifyClass() {
        val code = """
package chapi.ast.javaast;

import hello.Expandable;

class IntegerArray implements Expandable {
    void addItem(Integer item) {
    }
}
"""
        val codeFile = JavaFullIdent().identBasicInfo(code, "basic")
        kotlin.test.assertEquals(codeFile.DataStructures.size, 1)
        kotlin.test.assertEquals(codeFile.DataStructures[0].NodeName, "IntegerArray")
        kotlin.test.assertEquals(codeFile.DataStructures[0].Implements[0], "Expandable")
    }

    @Test
    internal fun shouldIdentifyExtends() {
        val code = """
package chapi.ast.javaast;

import hello.Expandable;

class IntegerArray extends Expandable {
    void addItem(Integer item) {
    }
}
"""
        val codeFile = JavaFullIdent().identBasicInfo(code, "basic")
        kotlin.test.assertEquals(codeFile.DataStructures.size, 1)
        kotlin.test.assertEquals(codeFile.DataStructures[0].NodeName, "IntegerArray")
        kotlin.test.assertEquals(codeFile.DataStructures[0].Extend, "Expandable")
    }
}
