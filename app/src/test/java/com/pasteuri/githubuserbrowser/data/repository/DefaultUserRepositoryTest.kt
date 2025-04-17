package com.pasteuri.githubuserbrowser.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import com.pasteuri.githubuserbrowser.data.remote.service.SearchService
import com.pasteuri.githubuserbrowser.data.remote.service.UserService
import com.pasteuri.githubuserbrowser.domain.model.User
import com.pasteuri.githubuserbrowser.domain.model.VisitedUser
import com.pasteuri.githubuserbrowser.domain.repository.UserRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

@OptIn(ExperimentalCoroutinesApi::class)
class DefaultUserRepositoryTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private val testScope = TestScope(testDispatcher + Job())
    private lateinit var mockWebServer: MockWebServer
    private lateinit var userService: UserService
    private lateinit var searchService: SearchService
    private lateinit var dataStore: DataStore<Preferences>
    private lateinit var repository: DefaultUserRepository

    @get:Rule
    val tmpFolder: TemporaryFolder = TemporaryFolder.builder().assureDeletion().build()

    @Before
    fun setup() {
        mockWebServer = MockWebServer()
        mockWebServer.start()

        val retrofit = TestRepositoryHelper.retrofitBuilder(mockWebServer)
        userService = retrofit.create(UserService::class.java)
        searchService = retrofit.create(SearchService::class.java)

        dataStore = PreferenceDataStoreFactory.create(
            scope = testScope,
            produceFile = { tmpFolder.newFile("user.preferences_pb") }
        )

        repository = DefaultUserRepository(
            userService = { userService },
            searchService = { searchService },
            dataStore = dataStore
        )
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun `searchUsers with valid query returns success result`() = runTest {
        // Given
        val query = "test"
        val jsonResponse = """
            {
                "total_count": 1,
                "incomplete_results": false,
                "items": [
                    {
                        "id": 123,
                        "login": "testuser",
                        "bio": "A test user",
                        "avatar_url": "https://avatar.url",
                        "name": "Test User",
                        "followers": 100,
                        "following": 50,
                        "public_repos": 10,
                        "public_gists": 5,
                        "type": "User"
                    }
                ]
            }
        """.trimIndent()

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setHeader("Content-Type", "application/json")
                .setHeader("Link", "<https://api.github.com/search/users?q=test&per_page=20&page=1&sort=followers&order=desc>; rel=\"prev\", <https://api.github.com/search/users?q=test&per_page=20&page=3&sort=followers&order=desc>; rel=\"next\", <https://api.github.com/search/users?q=test&per_page=20&page=50&sort=followers&order=desc>; rel=\"last\", <https://api.github.com/search/users?q=test&per_page=20&page=1&sort=followers&order=desc>; rel=\"first\"")
                .setBody(jsonResponse)
        )

        // When
        val result = repository.searchUsers(
            query = query,
            perPage = 30,
            page = 1,
            sort = UserRepository.SearchUserSort.FOLLOWERS,
            order = UserRepository.SearchOrder.DESC
        )

        // Then
        val request = mockWebServer.takeRequest()
        assertEquals("GET", request.method)
        assertEquals("/search/users?q=test&per_page=30&page=1&sort=followers&order=desc", request.path)

        assertTrue(result.isSuccess)
        val paginationResult = result.getOrNull()!!
        assertEquals(1, paginationResult.total)
        assertEquals(3, paginationResult.nextPage)
        assertEquals(1, paginationResult.items.size)
        with(paginationResult.items[0]) {
            assertEquals(123L, id)
            assertEquals("testuser", username)
            assertEquals("A test user", bio)
            assertEquals("https://avatar.url", avatarUrl)
            assertEquals("Test User", name)
            assertEquals(100, followers)
            assertEquals(50, following)
            assertEquals(10, publicRepoCount)
            assertEquals(5, publicGistCount)
            assertEquals(User.Type.USER, type)
        }
    }

    @Test
    fun `searchUsers returns empty result`() = runTest {
        // Given
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setHeader("Content-Type", "application/json")
                .setBody("{}")
        )

        // When
        val result = repository.searchUsers(
            query = "lorem",
            perPage = null,
            page = null,
            sort = null,
            order = null
        )

        // Then
        assertTrue(result.isSuccess)
        val paginationResult = result.getOrNull()!!
        assertEquals(0, paginationResult.total)
        assertNull(paginationResult.nextPage)
        assertTrue(paginationResult.items.isEmpty())
    }

    @Test
    fun `searchUsers with server error returns failure result`() = testScope.runTest {
        // Given
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(500)
                .setBody("Internal Server Error")
        )

        // When
        val result = repository.searchUsers(
            query = "test",
            perPage = null,
            page = null,
            sort = null,
            order = null
        )

        // Then
        assertTrue(result.isFailure)
    }

    @Test
    fun `getUserDetail returns success result with valid username`() = testScope.runTest {
        // Given
        val username = "testuser"
        val jsonResponse = """
            {
                "id": 123,
                "login": "testuser",
                "bio": "A test user",
                "avatar_url": "https://avatar.url",
                "name": "Test User",
                "followers": 100,
                "following": 50,
                "public_repos": 10,
                "public_gists": 5,
                "type": "User"
            }
        """.trimIndent()

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setHeader("Content-Type", "application/json")
                .setBody(jsonResponse)
        )

        // When
        val result = repository.getUserDetail(username)

        // Then
        val request = mockWebServer.takeRequest()
        assertEquals("GET", request.method)
        assertEquals("/users/testuser", request.path)

        assertTrue(result.isSuccess)
        val user = result.getOrNull()!!
        assertEquals("testuser", user.username)
        assertEquals("Test User", user.name)
        assertEquals(100, user.followers)
    }

    @Test
    fun `getUserDetail with server error returns failure result`() = testScope.runTest {
        // Given
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(404)
                .setBody("User not found")
        )

        // When
        val result = repository.getUserDetail("nonexistentuser")

        // Then
        assertTrue(result.isFailure)
    }

    @Test
    fun `cacheVisitedUser adds user to preferences`() = testScope.runTest {
        // Given
        val visitedUser = VisitedUser(username = "testuser", avatarUrl = "https://avatar.url", type = User.Type.USER)
        val limit = 5

        // When
        repository.cacheVisitedUser(visitedUser, limit)

        // Then
        repository.getCachedVisitedUsers(5).first().let {
            assertEquals(1, it.size)
            assertEquals("testuser", it[0].username)
        }
    }

    @Test
    fun `cacheVisitedUser respects limit and order`() = testScope.runTest {
        // Given
        val testVisitedUsers = (0..3).map {
            VisitedUser(
                username = "testuser$it",
                avatarUrl = "https://avatar$it.url",
                type = if (it == 1) User.Type.ORG else User.Type.USER
            )
        }
        val limit = 3

        // When
        testVisitedUsers.forEach {
            repository.cacheVisitedUser(it, limit)
        }

        // Then
        repository.getCachedVisitedUsers(5).first().let {
            assertEquals(3, it.size)
            assertEquals("testuser3", it[0].username)
            assertEquals("https://avatar3.url", it[0].avatarUrl)
            assertEquals(User.Type.USER, it[0].type)
            assertEquals("testuser2", it[1].username)
            assertEquals("https://avatar2.url", it[1].avatarUrl)
            assertEquals(User.Type.USER, it[1].type)
            assertEquals("testuser1", it[2].username)
            assertEquals("https://avatar1.url", it[2].avatarUrl)
            assertEquals(User.Type.ORG, it[2].type)
        }
    }
}
