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


// compileSdk app tidak diwarisi plugin. Paksa semua library plugin ke 36.
subprojects {
    pluginManager.withPlugin("com.android.library") {
        val android = extensions.findByName("android") ?: return@withPlugin
        android.javaClass.methods
            .firstOrNull {
                it.name == "setCompileSdkVersion" &&
                    it.parameterTypes.singleOrNull() == Int::class.javaPrimitiveType
            }
            ?.invoke(android, 36)
    }
}
