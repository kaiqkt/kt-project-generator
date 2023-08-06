import java.io.File

fun createProject(name: String) {
    val packageName = name.replace("-service", "").replace("-", "")
    val projectName = name.replace("-service", "")

    val directory = File("../$name")
    val repositoryUrl = "https://github.com/kaiqkt/spring-sample-service.git"

    runCatching {
        cloneRepository(repositoryUrl, directory)
        deleteDirs(directory.path, listOf(".git", ".github"))
        replacePackageName(directory.path, packageName)
        replaceValue("${directory.path}/build.gradle.kts", "{{projectPackage}}", packageName)
        replaceValue("${directory.path}/settings.gradle.kts", "{{projectName}}", projectName)
        replaceValue("${directory.path}/src/main/resources/static/api-config.json", "{{projectPackage}}", projectName)
        replaceValue("${directory.path}/src/main/resources/static/api-docs.yml", "{{projectName}}", projectName)
        replaceValue("${directory.path}/src/main/resources/application.yml", "{{projectName}}", projectName)
        replacePackage(directory.path, packageName)
        replaceFile(directory.path)
    }.onFailure {
        println("Error while cloning the repository: ${it.message}")
    }
}
