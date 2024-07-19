pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
        maven { url = uri("https://naver.jfrog.io/artifactory/maven/") }
    }
}
dependencyResolutionManagement {
    //  프로젝트레벨에서 레포지토리를 설정하지 않도록 주석 처리
    //    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://naver.jfrog.io/artifactory/maven/") }
    }
}

rootProject.name = "GDMS_FRONT"
include(":app")
 