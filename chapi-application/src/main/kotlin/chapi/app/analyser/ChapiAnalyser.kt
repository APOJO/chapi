package chapi.app.analyser

import chapi.app.analyser.config.ChapiConfig
import chapi.app.analyser.support.IAnalyser
import chapi.domain.core.CodeDataStruct

open class ChapiAnalyser(
    var config: ChapiConfig = ChapiConfig()
) {

    open fun analysis(path: String): Array<CodeDataStruct> {
        val appAnalyser = getLangAppAnalyser()
        return appAnalyser.analysisNodeByPath(path)
    }

    open fun getLangAppAnalyser(): IAnalyser {
        when (config.language) {
            "java" -> {
                return JavaAnalyserApp(config)
            }
            "go" -> {
                return GoAnalyserApp(config)
            }
            "python" -> {
                return PythonAnalyserApp(config)
            }
            "typescript" -> {
                return TypeScriptAnalyserApp(config)
            }
            "scala" -> {
                return ScalaAnalyserApp(config)
            }
            else -> {
                return JavaAnalyserApp(config)
            }
        }
    }
}
