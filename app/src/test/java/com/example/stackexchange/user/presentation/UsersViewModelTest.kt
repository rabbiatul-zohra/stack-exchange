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
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.io.IOException
import java.time.LocalDate

@OptIn(ExperimentalCoroutinesApi::class)
class UsersViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var repository: UserRepository
    private lateinit var viewModel: UsersViewModel

    private val mockUsers = listOf(
        User(
            id = 1,
            name = "John Doe",
            reputation = 150,
            profileImageUrl = "",
            location = "New York",
            creationDate = LocalDate.of(2019, 5, 12)
        ),
        User(
            id = 2,
            name = "John Smith",
            reputation = 100,
            profileImageUrl = "",
            location = "London",
            creationDate = LocalDate.of(2020, 1, 1)
        )
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)

        repository = mockk()

        viewModel = UsersViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `given initial state, when viewmodel is created, then state should be Idle`() =
        runTest(testDispatcher) {

            // Given
            // ViewModel created in setUp()

            // When
            val currentState = viewModel.uiState.value

            // Then
            assertEquals(
                UiState.Idle,
                currentState
            )
        }

    @Test
    fun `given valid query, when search is performed, then state transitions from Loading to Success`() =
        runTest(testDispatcher) {

            // Given
            val query = "john"

            every {
                repository.getUsers(query)
            } returns flowOf(mockUsers)

            val collectedStates = mutableListOf<UiState>()

            val collectJob = backgroundScope.launch(
                UnconfinedTestDispatcher(testScheduler)
            ) {
                viewModel.uiState.collect {
                    collectedStates.add(it)
                }
            }
            runCurrent()
            // When
            viewModel.search(query)

            runCurrent()

            // Then
            assertEquals(
                listOf(
                    UiState.Idle,
                    UiState.Loading,
                    UiState.Success(mockUsers)
                ),
                collectedStates
            )

            verify(exactly = 1) {
                repository.getUsers(query)
            }

            collectJob.cancel()
        }

    @Test
    fun `given blank query, when search is performed, then repository is not called and state remains Idle`() =
        runTest(testDispatcher) {

            // Given
            val query = "   "

            // When
            viewModel.search(query)

            advanceUntilIdle()

            // Then
            assertEquals(
                UiState.Idle,
                viewModel.uiState.value
            )

            verify(exactly = 0) {
                repository.getUsers(any())
            }
        }

    @Test
    fun `given repository throws exception, when search is performed, then state transitions from Loading to Error`() =
        runTest {

            // Given
            val query = "error"
            val errorMessage = "Network failure"

            every {
                repository.getUsers(query)
            } returns flow {
                throw IOException(errorMessage)
            }
            val collectedStates = mutableListOf<UiState>()

            val collectJob = backgroundScope.launch(
                UnconfinedTestDispatcher(testScheduler)
            ) {
                viewModel.uiState.collect {
                    collectedStates.add(it)
                }
            }
            runCurrent()

            // When
            viewModel.search(query)

            runCurrent()

            // Then
            assertEquals(
                listOf(
                    UiState.Idle,
                    UiState.Loading,
                    UiState.Error(errorMessage)
                ),
                collectedStates
            )

            verify(exactly = 1) {
                repository.getUsers(query)
            }

            collectJob.cancel()
        }

    @Test
    fun `given exception with null message, when repository throws exception, then fallback error message is emitted`() =
        runTest(testDispatcher) {

            // Given
            val query = "unknown_error"

            every {
                repository.getUsers(query)
            } returns flow {
                throw NullPointerException()
            }

            val collectedStates = mutableListOf<UiState>()

            val collectJob = backgroundScope.launch(
                UnconfinedTestDispatcher(testScheduler)
            ) {
                viewModel.uiState.collect {
                    collectedStates.add(it)
                }
            }

            runCurrent()
            // When
            viewModel.search(query)

            runCurrent()

            // Then
            assertEquals(
                listOf(
                    UiState.Idle,
                    UiState.Loading,
                    UiState.Error("Failed to retrieve users")
                ),
                collectedStates
            )

            collectJob.cancel()
        }

    @Test
    fun `given query with extra spaces, when search is performed, then query is trimmed before searching`() =
        runTest(testDispatcher) {

            // Given
            val queryWithSpaces = "  alice  "
            val trimmedQuery = "alice"

            val alice = User(
                id = 3,
                name = "Alice",
                reputation = 250,
                profileImageUrl = "",
                location = "San Francisco",
                creationDate = LocalDate.of(2021, 3, 15)
            )

            every {
                repository.getUsers(trimmedQuery)
            } returns flowOf(listOf(alice))

            val collectedStates = mutableListOf<UiState>()

            val collectJob = backgroundScope.launch(
                UnconfinedTestDispatcher(testScheduler)
            ) {
                viewModel.uiState.collect {
                    collectedStates.add(it)
                }
            }
            runCurrent()
            // When
            viewModel.search(queryWithSpaces)

            runCurrent()

            // Then
            verify(exactly = 1) {
                repository.getUsers(trimmedQuery)
            }

            assertEquals(
                listOf(
                    UiState.Idle,
                    UiState.Loading,
                    UiState.Success(listOf(alice))
                ),
                collectedStates
            )

            collectJob.cancel()
        }
}