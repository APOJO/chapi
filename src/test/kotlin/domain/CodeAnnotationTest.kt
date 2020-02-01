package domain

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class CodeAnnotationTest {
    @Test
    fun shouldHandleIsRepository() {
        val emptyStringArray = arrayOf<AnnotationKeyValue>()
        val isComponent = CodeAnnotation("Repository", emptyStringArray).isComponentOrRepository()
        assertEquals(isComponent, true)
    }

    @Test
    fun shouldHandleIsComponent() {
        val emptyStringArray = arrayOf<AnnotationKeyValue>()
        val isComponent = CodeAnnotation("Component", emptyStringArray).isComponentOrRepository()
        assertEquals(isComponent, true)
    }

    @Test
    fun shouldHandleIsTest() {
        val emptyStringArray = arrayOf<AnnotationKeyValue>()
        val isComponent = CodeAnnotation("Test", emptyStringArray).isTest()
        assertEquals(isComponent, true)
    }

    @Test
    fun shouldHandleIsIgnore() {
        val emptyStringArray = arrayOf<AnnotationKeyValue>()
        val isComponent = CodeAnnotation("Ignore", emptyStringArray).isIgnore()
        assertEquals(isComponent, true)
    }

    @Test
    fun shouldHandleIsIgnoreOrTest() {
        val emptyStringArray = arrayOf<AnnotationKeyValue>()
        val isComponent = CodeAnnotation("Ignore", emptyStringArray).isIgnoreOrTest()
        assertEquals(isComponent, true)
    }
}

