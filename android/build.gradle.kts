allprojects {
    repositories {
        google()
        mavenCentral()
    }
}

val newBuildDir: Directory = rootProject.layout.buildDirectory.dir("../../build").get()
rootProject.layout.buildDirectory.value(newBuildDir)

subprojects {
    val newSubprojectBuildDir: Directory = newBuildDir.dir(project.name)
    project.layout.buildDirectory.value(newSubprojectBuildDir)
}
subprojects {
    project.evaluationDependsOn(":app")
}

tasks.register<Delete>("clean") {
    delete(rootProject.layout.buildDirectory)
}



// compileSdk app tidak diwarisi plugin. Paksa library plugin ke android-36.
subprojects {
    pluginManager.withPlugin("com.android.library") {
        val android = extensions.findByName("android") ?: return@withPlugin
        val candidates = android.javaClass.methods.filter { m ->
            (m.name == "setCompileSdkVersion" || m.name == "compileSdkVersion") &&
                m.parameterTypes.size == 1
        }
        var applied = false
        for (m in candidates) {
            val t = m.parameterTypes[0]
            val value: Any = when {
                t == String::class.java -> "android-36"
                t == Int::class.javaPrimitiveType -> 36
                t == java.lang.Integer::class.java -> Integer.valueOf(36)
                else -> continue
            }
            if (runCatching { m.invoke(android, value) }.isSuccess) {
                applied = true
                break
            }
        }
        println("[compileSdk-override] ${project.name}: " + if (applied) "android-36" else "GAGAL")
    }
}
