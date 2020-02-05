package chapi.ast.javaast

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class JavaIdentAppTest {
    @Test
    fun shouldIdentifyFilePackageName() {
        val code = """
package chapi.ast.javaast;

import org.junit.Test;
"""
        val codeFile = JavaIdentApp().analysis(code, "")
        assertEquals(codeFile.PackageName, "chapi.ast.javaast")
    }

    @Test
    fun shouldIdentifyFileImports() {
        val code = """
package chapi.ast.javaast;

import org.junit.Test;
"""
        val codeFile = JavaIdentApp().analysis(code, "")
        assertEquals(codeFile.Imports[0].Source, "org.junit.Test")
    }

    @Test
    fun shouldIdentifyDataStructureName() {
        val code = """
public class HelloWorld {
    public static void main(String []args) {
       System.out.println("Hello World");
    }
}
"""
        val codeFile = JavaIdentApp().analysis(code, "")
        assertEquals(codeFile.DataStructures[0].NodeName, "HelloWorld")
    }

    @Test
    fun shouldIdentifyFunctionParameters() {
        val code = """
public class HelloWorld {
    public static void main(String []args) {
       System.out.println("Hello World");
    }
}
"""
        val codeFile = JavaIdentApp().analysis(code, "")
        val firstParameter = codeFile.DataStructures[0].Functions[0].Parameters[0]
        assertEquals(firstParameter.TypeType, "String[]")
        assertEquals(firstParameter.TypeValue, "args")
    }

    @Test
    fun shouldIdentifyDataStructureMethodName() {
        val code = """
public class HelloWorld {
    public static void main(String []args) {
       System.out.println("Hello World");
    }
}
"""
        val codeFile = JavaIdentApp().analysis(code, "")
        assertEquals(codeFile.DataStructures[0].Functions.size, 1)
        assertEquals(codeFile.DataStructures[0].Functions[0].Name, "main")
    }

    @Test
    fun shouldIdentifyExtendsName() {
        val code = """
  class Inner extends HasStatic {
    static final int x = 3;
    static int y = 4;
    public static void pr() {

    }
  }
"""
        val codeFile = JavaIdentApp().analysis(code, "")
        assertEquals(codeFile.DataStructures[0].Extend, "HasStatic")
    }

    @Test
    fun shouldIdentifyFields() {
        val code = """
package hello;

public class JavaCallApp {
    private JavaDaoParser daoParser;

    public daoCall() {
        daoParser.Call();
    }
}

"""
        val codeFile = JavaIdentApp().analysis(code, "")
        println(codeFile.DataStructures[0].Fields.size)

        assertEquals(codeFile.DataStructures[0].Fields.size, 1)
        assertEquals(codeFile.DataStructures[0].Fields[0].TypeType, "JavaDaoParser")
        assertEquals(codeFile.DataStructures[0].Fields[0].TypeValue, "daoParser")
    }

    @Test
    fun shouldIdentifyImplementName() {
        val code = """
class Pig implements Animal {
  public void animalSound() {
    System.out.println("The pig says: wee wee");
  }
  public void sleep() {
    System.out.println("Zzz");
  }
}
"""
        val codeFile = JavaIdentApp().analysis(code, "")
        assertEquals(codeFile.DataStructures[0].Implements[0], "Animal")
    }

    @Test
    fun shouldIdentifyInnerStructureName() {
        val code = """
public class Outer {
  final int z=10;

  class Inner extends HasStatic {
    static final int x = 3;
    static int y = 4;
    public static void pr() {

    }
  }

  public static void main(String[] args) {
    Outer outer = new Outer();
    System.out.println(outer.new Inner().y);
  }
}
"""
        val codeFile = JavaIdentApp().analysis(code, "")
//        assertEquals(codeFile.DataStructures[0].Functions.size, 1)
//        assertEquals(codeFile.DataStructures[0].Functions[0].Name, "main")
//        assertEquals(codeFile.DataStructures[0].InnerStructures.size, 0)
        println(codeFile.DataStructures.size)
    }
}
