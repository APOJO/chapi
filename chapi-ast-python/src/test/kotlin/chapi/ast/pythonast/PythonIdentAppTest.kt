package chapi.ast.pythonast

import org.junit.jupiter.api.Test

internal class PythonIdentAppTest {
    @Test
    fun shouldAnalysisPython2() {
        val python2HelloWorld = """
print("Hello, World!")
        """

        PythonIdentApp().analysis(python2HelloWorld)
    }

    @Test
    fun shouldAnalysisPython3() {
        val py3HelloWorld = """
print "Hello, World!"
        """
        PythonIdentApp().analysis(py3HelloWorld)
    }
}
