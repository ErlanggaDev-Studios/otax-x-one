
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





// compileSdk app tidak diwarisi plugin; AGP membaca DSL baru (setCompileSdk), bukan setCompileSdkVersion.
subprojects {
    plugins.withId("com.android.library") {
        val a = project.extensions.findByName("android") ?: return@withId
        val applied = a.javaClass.methods.any {
            it.name == "setCompileSdk" && it.parameterTypes.singleOrNull() == Integer::class.java &&
                runCatching { it.invoke(a, 36) }.isSuccess
        }
        println("[compileSdk-override] ${project.name}: " + if (applied) "36" else "GAGAL")
    }
}
