package com.example.stackexchange.usercore.data

import com.example.stackexchange.usercore.model.SearchUserListResponse
import com.example.stackexchange.usercore.model.TopTag
import com.example.stackexchange.usercore.model.TopTagResponse
import com.example.stackexchange.usercore.model.TopTagsListResponse
import com.example.stackexchange.usercore.model.User
import com.example.stackexchange.usercore.model.UserResponse
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.fail
import org.junit.Test
import java.io.IOException
import java.time.LocalDate

class UserRepositoryImplTest {

    private val service: UserService = mockk()

    private val repository = UserRepositoryImpl(
        service = service
    )

    private val user = User(
        id = 1,
        name = "test user",
        reputation = 150,
        profileImageUrl = "https://example.com/test.jpg",
        location = "Manchester",
        creationDate = LocalDate.of(2020, 1, 1)
    )

    private val userResponse = UserResponse(
        user_id = 1,
        display_name = "test user",
        reputation = 150,
        profile_image = "https://example.com/test.jpg",
        location = "Manchester",
        creation_date = 1577836800L
    )

    @Test
    fun `given users response when getUsers is called then mapped users are returned`() =
        runTest {

            // Given
            val query = "test"

            val response = SearchUserListResponse(
                items = listOf(userResponse)
            )

            coEvery { service.getUsers(query) } returns response

            // When
            val result = repository.getUsers(query).first()

            // Then
            assertEquals(listOf(user), result)

            coVerify(exactly = 1) {
                service.getUsers(query)
            }
        }

    @Test
    fun `given empty users response when getUsers is called then empty list is returned`() =
        runTest {

            // Given
            val query = "unknown"

            coEvery { service.getUsers(query) } returns
                    SearchUserListResponse(items = emptyList())

            // When
            val result = repository.getUsers(query).first()

            // Then
            assertEquals(emptyList<User>(), result)

            coVerify(exactly = 1) { service.getUsers(query) }
        }

    @Test
    fun `given service throws IOException when getUsers is called then exception is propagated`() =
        runTest {

            // Given
            val query = "test"

            coEvery { service.getUsers(query) } throws IOException("Network error")

            // When
            try {
                repository.getUsers(query).first()

                fail("Expected IOException")
            } catch (exception: IOException) {

                // Then
                assertEquals("Network error", exception.message)
            }

            coVerify(exactly = 1) { service.getUsers(query) }
        }

    @Test
    fun `given user response when getUserById is called then mapped user is returned`() =
        runTest {

            // Given
            val userId = 1

            val response = SearchUserListResponse(items = listOf(userResponse))

            coEvery { service.getUserById(userId) } returns response

            // When
            val result = repository.getUserById(userId).first()

            // Then
            assertEquals(user,result)

            coVerify(exactly = 1) {
                service.getUserById(userId)
            }
        }

    @Test
    fun `given empty response when getUserById is called then user not found exception is thrown`() =
        runTest {

            // Given
            val userId = 999

            coEvery { service.getUserById(userId) } returns
                    SearchUserListResponse(items = emptyList())

            // When
            try {
                repository.getUserById(userId).first()

                fail("Expected IllegalStateException")
            } catch (exception: IllegalStateException) {

                // Then
                assertEquals("User not found", exception.message)
            }

            coVerify(exactly = 1) {
                service.getUserById(userId)
            }
        }

    @Test
    fun `given service throws IOException when getUserById is called then exception is propagated`() =
        runTest {

            // Given
            val userId = 1

            coEvery { service.getUserById(userId) } throws IOException("Network error")

            // When
            try {
                repository.getUserById(userId).first()

                fail("Expected IOException")
            } catch (exception: IOException) {

                // Then
                assertEquals(
                    "Network error",
                    exception.message
                )
            }

            coVerify(exactly = 1) {
                service.getUserById(userId)
            }
        }

    @Test
    fun `given top tags response when getUserTopTags is called then mapped top tags are returned`() =
        runTest {

            // Given
            val userId = 1

            val response = TopTagsListResponse(
                items = listOf(
                    TopTagResponse(
                        tag_name = "test1"
                    ),
                    TopTagResponse(
                        tag_name = "test2"
                    ),
                    TopTagResponse(
                        tag_name = "test3"
                    )
                )
            )

            coEvery {
                service.getUserTopTags(userId)
            } returns response

            // When
            val result = repository.getUserTopTags(userId).first()

            // Then
            assertEquals(
                listOf(
                    TopTag(name = "test1"),
                    TopTag(name = "test2"),
                    TopTag(name = "test3")
                ),
                result
            )

            coVerify(exactly = 1) { service.getUserTopTags(userId) }
        }

    @Test
    fun `given empty top tags response when getUserTopTags is called then empty list is returned`() =
        runTest {

            // Given
            val userId = 1

            coEvery { service.getUserTopTags(userId) } returns
                    TopTagsListResponse(items = emptyList())

            // When
            val result = repository.getUserTopTags(userId).first()

            // Then
            assertEquals(emptyList<TopTag>(), result)

            coVerify(exactly = 1) { service.getUserTopTags(userId) }
        }

    @Test
    fun `given service throws IOException when getUserTopTags is called then exception is thrown`() =
        runTest {

            // Given
            val userId = 1
            val errorMessage = "Network error"

            coEvery { service.getUserTopTags(userId) } throws IOException(errorMessage)

            // When
            try {
                repository.getUserTopTags(userId).first()

                fail("Expected IOException")

            } catch (exception: IOException) {

                // Then
                assertEquals(errorMessage, exception.message)
            }

            coVerify(exactly = 1) { service.getUserTopTags(userId) }
        }
}