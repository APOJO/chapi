package chapi.ast.csharpast

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

internal class CSharpFullIdentListenerTest {
    private val helloworld = """
    using System; 
      
    namespace HelloWorldApp { 
        class Geeks { 
            static void Main(string[] args) { 
                Console.WriteLine("Hello World!"); 
                Console.ReadKey(); 
            } 
        } 
    } 
    """

    @Test
    fun shouldIdentUsingSystem() {
        val codeFile = CSharpAnalyser().analysis(helloworld, "hello.cs")
        assertEquals(codeFile.Imports.size, 1)
        assertEquals(codeFile.Imports[0].Source, "System")
    }

    @Test
    fun shouldIdentUsingNamespace() {
        val code = """
using SomeNameSpace.Nested;

"""
        val codeFile = CSharpAnalyser().analysis(code, "hello.cs")
        assertEquals(codeFile.Imports.size, 1)
        assertEquals(codeFile.Imports[0].Source, "SomeNameSpace.Nested")
    }

    @Test
    fun shouldIdentUsingAlias() {
        val code = """
using generics = System.Collections.Generic;

"""
        val codeFile = CSharpAnalyser().analysis(code, "hello.cs")
        assertEquals(codeFile.Imports.size, 1)
        assertEquals(codeFile.Imports[0].Source, "System.Collections.Generic")
        assertEquals(codeFile.Imports[0].AsName, "generics")
    }

    @Test
    fun shouldIdentDeclNameSpace() {
        val code = """
using System; 
  
namespace HelloWorldApp {

}
"""
        val codeContainer = CSharpAnalyser().analysis(code, "hello.cs")
        assertEquals(codeContainer.Containers.size, 1)
        assertEquals(codeContainer.Containers[0].PackageName, "HelloWorldApp")
    }

    @Test
    fun shouldIdentDeclNameSpaceInNameSpace() {
        val code = """
using System; 
  
namespace HelloWorldApp {
  namespace HelloWorldApp2 {
    namespace HelloWorldApp3 {
    
    }
  }
}
"""
        val codeContainer = CSharpAnalyser().analysis(code, "hello.cs")
        assertEquals(codeContainer.Containers.size, 1)
        assertEquals(codeContainer.Containers[0].PackageName, "HelloWorldApp")
        assertEquals(codeContainer.Containers[0].Containers[0].PackageName, "HelloWorldApp2")
        assertEquals(codeContainer.Containers[0].Containers[0].Containers[0].PackageName, "HelloWorldApp3")
    }

    @Test
    fun shouldIdentClassName() {
        val code = """
using System; 
  
namespace HelloWorldApp { 
    class Geeks { 

    } 
} 
"""
        val codeContainer = CSharpAnalyser().analysis(code, "hello.cs")
        assertEquals(codeContainer.Containers[0].DataStructures.size, 1)
        assertEquals(codeContainer.Containers[0].DataStructures[0].NodeName, "Geeks")
    }

    @Test
    fun shouldIdentNameSpaceClassInNameSpaceName() {
        val code = """
using System; 
  
namespace HelloWorldApp { 
    class Geeks {}
    namespace HelloWorldApp2 {
        class Geeks2 {}    
    }
} 
"""
        val codeContainer = CSharpAnalyser().analysis(code, "hello.cs")
        assertEquals(codeContainer.Containers[0].DataStructures.size, 1)
        assertEquals(codeContainer.Containers[0].DataStructures[0].NodeName, "Geeks")
        assertEquals(codeContainer.Containers[0].Containers[0].DataStructures.size, 1)
        assertEquals(codeContainer.Containers[0].Containers[0].DataStructures[0].NodeName, "Geeks2")
    }

    @Test
    fun shouldIdentNameSpaceClassMethodSupport() {
        val codeContainer = CSharpAnalyser().analysis(helloworld, "hello.cs")
        val codeDataStruct = codeContainer.Containers[0].DataStructures[0]
        assertEquals(codeDataStruct.Functions.size, 1)
        assertEquals(codeDataStruct.Functions[0].Name, "Main")
        assertEquals(codeDataStruct.Functions[0].Modifiers.size, 1)
        assertEquals(codeDataStruct.Functions[0].Modifiers[0], "static")
    }

    @Test
    fun shouldIdentNameSpaceClassParametersSupport() {
        val codeContainer = CSharpAnalyser().analysis(helloworld, "hello.cs")
        val codeDataStruct = codeContainer.Containers[0].DataStructures[0]
        assertEquals(codeDataStruct.Functions[0].Parameters.size, 1)
        assertEquals(codeDataStruct.Functions[0].Parameters[0].TypeType, "string[]")
        assertEquals(codeDataStruct.Functions[0].Parameters[0].TypeValue, "args")
    }
}
