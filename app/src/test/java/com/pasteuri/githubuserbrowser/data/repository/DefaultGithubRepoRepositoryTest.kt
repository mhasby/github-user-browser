package com.pasteuri.githubuserbrowser.data.repository

import com.pasteuri.githubuserbrowser.data.remote.service.GithubRepoService
import com.pasteuri.githubuserbrowser.domain.model.GithubRepo
import com.pasteuri.githubuserbrowser.domain.model.User
import com.pasteuri.githubuserbrowser.domain.repository.GithubRepoRepository
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class DefaultGithubRepoRepositoryTest {

    private lateinit var mockWebServer: MockWebServer
    private lateinit var githubRepoService: GithubRepoService
    private lateinit var repository: DefaultGithubRepoRepository

    private val successJsonResponse  = """
        [
            {
                "id": 1,
                "name": "repo1",
                "full_name": "repo 1",
                "description": "Test repo 1",
                "html_url": "https://github.com/testuser/repo1",
                "language": "Kotlin",
                "stargazers_count": 10
            }
        ]
    """.trimIndent()

    @Before
    fun setup() {
        mockWebServer = MockWebServer()
        mockWebServer.start()

        val retrofit = TestRepositoryHelper.retrofitBuilder(mockWebServer)
        githubRepoService = retrofit.create(GithubRepoService::class.java)

        repository = DefaultGithubRepoRepository { githubRepoService }
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun `getRepoByUser returns success result with user repositories`() = runBlocking {
        // Given
        val username = "testuser"

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setHeader("Content-Type", "application/json")
                .setHeader("Link", "<https://api.github.com/user/repos?page=2>; rel=\"next\"")
                .setBody(successJsonResponse)
        )

        // When
        val result = repository.getRepoByUser(
            username = username,
            type = User.Type.USER,
            perPage = 30,
            page = 1,
            filterType = GithubRepoRepository.ListFilterType.ALL,
            sort = GithubRepoRepository.ListSort.UPDATED,
            order = GithubRepoRepository.ListOrder.DESC
        )

        // Then
        val request = mockWebServer.takeRequest()
        assertEquals("GET", request.method)
        assertEquals("/users/testuser/repos?per_page=30&page=1&type=all&sort=updated&order=desc", request.path)

        assertTrue(result.isSuccess)
        val paginationResult = result.getOrNull()!!
        assertEquals(2, paginationResult.nextPage)
        assertEquals(1, paginationResult.items.size)
        assertSuccessGithubRepoItem(paginationResult.items[0])
    }

    @Test
    fun `getRepoByUser returns success result with org repositories`() = runBlocking {
        // Given
        val orgName = "testorg"

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setHeader("Content-Type", "application/json")
                .setBody(successJsonResponse)
        )

        // When
        val result = repository.getRepoByUser(
            username = orgName,
            type = User.Type.ORG,
            perPage = null,
            page = null,
            filterType = null,
            sort = null,
            order = null
        )

        // Then
        val request = mockWebServer.takeRequest()
        assertEquals("GET", request.method)
        assertEquals("/orgs/testorg/repos", request.path)

        assertTrue(result.isSuccess)
        val paginationResult = result.getOrNull()!!
        assertEquals(null, paginationResult.nextPage)
        assertEquals(1, paginationResult.items.size)
        assertSuccessGithubRepoItem(paginationResult.items[0])
    }

    @Test
    fun `getRepoByUser returns failure result when service returns error response`() = runBlocking {
        // Given
        val username = "testuser"

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(404)
                .setBody("{\"message\": \"Not Found\"}")
        )

        // When
        val result = repository.getRepoByUser(
            username = username,
            type = User.Type.USER,
            perPage = null,
            page = null,
            filterType = null,
            sort = null,
            order = null
        )

        // Then
        assertTrue(result.isFailure)
    }

    @Test
    fun `getRepoByUser returns failure result with malformed response`() = runBlocking {
        // Given
        val username = "testuser"

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody("invalid json")
        )

        // When
        val result = repository.getRepoByUser(
            username = username,
            type = User.Type.USER,
            perPage = null,
            page = null,
            filterType = null,
            sort = null,
            order = null
        )

        // Then
        assertTrue(result.isFailure)
    }

    @Test
    fun `getRepoByUser handles different filter types correctly`() = runBlocking {
        // Given
        val username = "testuser"
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody("[]")
        )

        // When
        repository.getRepoByUser(
            username = username,
            type = User.Type.USER,
            perPage = null,
            page = null,
            filterType = GithubRepoRepository.ListFilterType.FORKS,
            sort = null,
            order = null
        )

        // Then
        val request = mockWebServer.takeRequest()
        assertEquals("/users/testuser/repos?type=forks", request.path)
    }

    private fun assertSuccessGithubRepoItem(githubRepo: GithubRepo) {
        assertEquals(1L, githubRepo.id)
        assertEquals("repo1", githubRepo.name)
        assertEquals("repo 1", githubRepo.fullName)
        assertEquals("Test repo 1", githubRepo.description)
        assertEquals("https://github.com/testuser/repo1", githubRepo.url)
        assertEquals("Kotlin", githubRepo.language)
        assertEquals(10, githubRepo.stars)
    }
}
