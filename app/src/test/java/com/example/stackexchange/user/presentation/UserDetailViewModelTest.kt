package com.example.stackexchange.user.presentation

import com.example.stackexchange.usercore.data.UserRepository
import com.example.stackexchange.usercore.model.User
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.time.LocalDate

@OptIn(ExperimentalCoroutinesApi::class)
class UserDetailViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var repository: UserRepository

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        repository = mockk()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `given viewmodel is created, then initial state is Loading`() =
        runTest(testDispatcher) {

            // Given
            val userId = 1

            every {
                repository.getUserById(userId)
            } returns flowOf(createUser())

            // When
            val viewModel = UserDetailViewModel(
                userId = userId,
                repository = repository
            )

            // Then
            assertEquals(
                UserDetailUiState.Loading,
                viewModel.userDetailUiState.value
            )
        }

    @Test
    fun `given user is returned, when state is collected, then Loading and Success are emitted`() =
        runTest(testDispatcher) {

            // Given
            val userId = 1

            val user = createUser(
                id = userId,
                name = "John Doe",
                reputation = 150,
                location = "London"
            )

            every {
                repository.getUserById(userId)
            } returns flowOf(user)

            val viewModel = UserDetailViewModel(
                userId = userId,
                repository = repository
            )

            val states = mutableListOf<UserDetailUiState>()

            val collectJob = backgroundScope.launch(
                UnconfinedTestDispatcher(testScheduler)
            ) {
                viewModel.userDetailUiState.collect {
                    states.add(it)
                }
            }

            // When
            runCurrent()

            // Then
            assertEquals(
                listOf(
                    UserDetailUiState.Loading,
                    UserDetailUiState.Success(
                        UserDetailUiModel(
                            name = "John Doe",
                            reputation = "150",
                            location = "London",
                            imageUrl = "https://example.com/image.jpg",
                            creationDate = "2020-01-01"
                        )
                    )
                ),
                states
            )

            verify(exactly = 1) {
                repository.getUserById(userId)
            }

            collectJob.cancel()
        }

    @Test
    fun `given user has null location, when user is returned, then location is Not provided`() =
        runTest(testDispatcher) {

            // Given
            val userId = 1

            val user = createUser(
                id = userId,
                location = null
            )

            every {
                repository.getUserById(userId)
            } returns flowOf(user)

            val viewModel = UserDetailViewModel(
                userId = userId,
                repository = repository
            )

            val states = mutableListOf<UserDetailUiState>()

            val collectJob = backgroundScope.launch(
                UnconfinedTestDispatcher(testScheduler)
            ) {
                viewModel.userDetailUiState.collect {
                    states.add(it)
                }
            }

            // When
            runCurrent()

            // Then
            val success = states
                .filterIsInstance<UserDetailUiState.Success>()
                .first()

            assertEquals(
                "Not provided",
                success.user.location
            )

            collectJob.cancel()
        }

    @Test
    fun `given exception has null message, when state is collected, then fallback error message is emitted`() =
        runTest(testDispatcher) {

            // Given
            val userId = 1

            every {
                repository.getUserById(userId)
            } returns flow {
                throw NullPointerException()
            }

            val viewModel = UserDetailViewModel(
                userId = userId,
                repository = repository
            )

            val states = mutableListOf<UserDetailUiState>()

            val collectJob = backgroundScope.launch(
                UnconfinedTestDispatcher(testScheduler)
            ) {
                viewModel.userDetailUiState.collect {
                    states.add(it)
                }
            }

            // When
            runCurrent()

            // Then
            assertEquals(
                listOf(
                    UserDetailUiState.Loading,
                    UserDetailUiState.Error(
                        "Failed to retrieve user"
                    )
                ),
                states
            )

            collectJob.cancel()
        }

    @Test
    fun `given user id, when viewmodel is created and collected, then repository is called with correct id`() =
        runTest(testDispatcher) {

            // Given
            val userId = 42

            every {
                repository.getUserById(userId)
            } returns flowOf(
                createUser(id = userId)
            )

            val viewModel = UserDetailViewModel(
                userId = userId,
                repository = repository
            )

            val collectJob = backgroundScope.launch(
                UnconfinedTestDispatcher(testScheduler)
            ) {
                viewModel.userDetailUiState.collect {}
            }

            // When
            runCurrent()

            // Then
            verify(exactly = 1) {
                repository.getUserById(42)
            }

            collectJob.cancel()
        }

    private fun createUser(
        id: Int = 1,
        name: String = "John Doe",
        reputation: Int = 150,
        location: String? = "London"
    ): User {
        return User(
            id = id,
            name = name,
            reputation = reputation,
            profileImageUrl = "https://example.com/image.jpg",
            location = location,
            creationDate = LocalDate.of(2020, 1, 1)
        )
    }
}