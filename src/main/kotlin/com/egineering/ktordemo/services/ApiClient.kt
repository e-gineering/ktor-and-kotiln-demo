package com.egineering.ktordemo.services

import com.egineering.ktordemo.configuration.ApplicationConfiguration
import com.egineering.ktordemo.dtos.*
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory

interface ApiClient {

    suspend fun getProjectsSync(): List<Project>
    suspend fun getProjects(): List<Project>
}

class ApiClientImpl(private val applicationConfiguration: ApplicationConfiguration) : ApiClient {

    private val log = LoggerFactory.getLogger(ApiClient::class.java)

    private val client = HttpClient {

        install(ContentNegotiation) {
            json()
        }

        install(HttpTimeout) {
            connectTimeoutMillis = 20000
            requestTimeoutMillis = 30000
        }

        install(Logging) {
            level = LogLevel.NONE
        }
    }

    override suspend fun getProjectsSync(): List<Project> {

        log.info("Retrieving Projects Synchronously.")

        val projectResponses: List<ProjectResponse> =
            client.get("${applicationConfiguration.apiUrl}/projects") {
                headers {
                    accept(ContentType.Application.Json)
                }
            }.body()

        return projectResponses.asSequence()
            .map { projectResponse ->
                var tasks: List<TaskResponse>
                var categories: List<CategoryResponse>

                runBlocking {
                    tasks =
                        client.get("${applicationConfiguration.apiUrl}/projects/${projectResponse.id}/tasks").body()
                }

                runBlocking {
                    categories =
                        client.get("${applicationConfiguration.apiUrl}/projects/${projectResponse.id}/categories").body()
                }

                Project(
                    projectResponse.id,
                    projectResponse.name,
                    tasks.asSequence().map { Task(it) }.toList(),
                    categories.asSequence().map { Category(it) }.toList()
                )
            }.toList()
    }

    override suspend fun getProjects(): List<Project> {

        log.info("Retrieving Projects.")

        val projectResponses: List<ProjectResponse> =
            client.get("${applicationConfiguration.apiUrl}/projects").body()

        val projects = runBlocking {
            projectResponses.map {
                async { retrieveProjectChildren(it) }
            }.awaitAll()
        }

        log.info("DONE Retrieving Projects.")

        return projects
    }

    private fun retrieveProjectChildren(projectResponse: ProjectResponse): Project {

        log.info("{} Retrieving Children For Project '{}'.", projectResponse.id, projectResponse.name)

        return runBlocking {

            var tasks: List<Task> = listOf()
            var categories: List<Category> = listOf()

            val tasksAsync = async {
                tasks = retrieveTasks(projectResponse)
            }

            val categoriesAsync = async {
                categories = retrieveCategories(projectResponse)
            }

            tasksAsync.await()
            categoriesAsync.await()
            log.info("{} DONE Retrieving Children For Project '{}'.", projectResponse.id, projectResponse.name)
            Project(projectResponse.id, projectResponse.name, tasks, categories)
        }
    }

    private suspend fun retrieveTasks(projectResponse: ProjectResponse): List<Task> {

        log.info("{} Retrieving Tasks For Project '{}'.", projectResponse.id, projectResponse.name)

        val tasks: List<TaskResponse> =
            client.get("${applicationConfiguration.apiUrl}/projects/${projectResponse.id}/tasks").body()

        log.info("{} DONE Retrieving Tasks For Project '{}'.", projectResponse.id, projectResponse.name)

        return tasks.asSequence()
            .map { Task(it) }
            .toList()
    }

    private suspend fun retrieveCategories(projectResponse: ProjectResponse): List<Category> {

        log.info("{} Retrieving Categories For Project '{}'.", projectResponse.id, projectResponse.name)

        val categories: List<CategoryResponse> =
            client.get("${applicationConfiguration.apiUrl}/projects/${projectResponse.id}/categories").body()

        log.info("{} DONE Retrieving Categories For Project '{}'.", projectResponse.id, projectResponse.name)

        return categories.asSequence()
            .map { Category(it) }
            .toList()
    }
}
