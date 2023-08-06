import java.io.File
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Paths
import org.eclipse.jgit.api.Git


fun main(args: Array<String>) {
    val argument = args.getOrNull(0)
    val projectName = args.getOrNull(1)

    when {
        argument != "init" -> println("Usage ktcli init project-name")
        projectName.isNullOrEmpty() || !projectName.isValidName() -> println("Project should be not null or contain special character")
        else -> createProject(projectName)
    }
}

fun String.isValidName() = "^[A-Za-z -]+\$".toRegex().matches(this)

fun cloneRepository(repositoryUrl: String, directory: File) {
    Git.cloneRepository()
        .setURI(repositoryUrl)
        .setDirectory(directory)
        .call()
}

fun deleteDirs(path: String, dirs: List<String>) {

    for (dir in dirs) {
        File("$path/$dir").run {
            if (this.exists()) {
                this.deleteRecursively()
            }
        }
    }
}

fun replacePackageName(path: String, projectPackage: String) {
    val source = "$path/src/main/kotlin/com/kaiqkt"
    val oldSource = Paths.get("$source/{{projectPackage}}")
    val newSource = Paths.get("$source/$projectPackage")
    Files.move(oldSource, newSource)
}

fun replaceValue(path: String, oldValue: String, newValue: String) {
    val encodedBytes = Files.readAllBytes(Paths.get(path))
    val content = String(encodedBytes, StandardCharsets.UTF_8)
    val updatedContent: String = content.replace(oldValue, newValue)
    Files.write(Paths.get(path), updatedContent.toByteArray(StandardCharsets.UTF_8));
}

fun replacePackage(path: String, projectPackage: String) {
    val source = "$path/src/main/kotlin/com/kaiqkt"
    val files = File("$source/$projectPackage").listFiles()

    if (files != null) {
        for (file in files) {
            when {
                file.path.contains("/resources") -> replaceValue(
                    "${file.path}/swagger/SpringDocsConfiguration.kt",
                    "`{{projectPackage}}`",
                    projectPackage
                )

                else -> replaceValue(file.path, "`{{projectPackage}}`", projectPackage)
            }
        }
    }
}

fun replaceFile(path: String) {
    try {
        val source = File("gradle.properties").inputStream()
        val destiny = File("$path/gradle.properties").outputStream()

        source.copyTo(destiny)

        source.close()
        destiny.close()
    } catch (e: Exception) {
        println("Erro ao substituir o arquivo gradle.properties $e")
    }
}

//trocar o arquivo gradle.properties com a senha e user do git
//usar o comando ktcli init project-name e ser feliz
//criar alias para o cli
//criar documentacao