package com.example.stackexchange.user.presentation

import com.example.stackexchange.usercore.data.UserRepository
import com.example.stackexchange.usercore.model.Badge
import com.example.stackexchange.usercore.model.TopTag
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
import java.io.IOException
import java.time.LocalDate

@OptIn(ExperimentalCoroutinesApi::class)
class UserDetailViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private val topTags = listOf(
        TopTag(name = "test1"),
        TopTag(name = "test2"),
        TopTag(name = "test3")
    )

    private val badges = listOf(
        Badge(
            id = 1,
            name = "test1",
            rank = "bronze"
        ),
        Badge(
            id = 2,
            name = "test2",
            rank = "silver"
        ),
        Badge(
            id = 3,
            name = "test3",
            rank = "gold"
        )
    )

    private val badgeUiState = listOf(
        BadgeUiState(
            name = "test1",
            rank = "bronze"
        ),
        BadgeUiState(
            name = "test2",
            rank = "silver"
        ),
        BadgeUiState(
            name = "test3",
            rank = "gold"
        )
    )

    private val topTagNames = listOf("test1", "test2", "test3")
    private val userId = 1
    private var repository = mockk<UserRepository>()  {
        every { getUserById(1) } returns flowOf(createUser())
        every { getUserTopTags(userId) } returns flowOf(topTags)
        every { getUserBadges(userId) } returns flowOf(badges)
    }
    private lateinit var viewModel: UserDetailViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = UserDetailViewModel(userId, repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `given viewmodel is created, then initial state is Loading`() =
        runTest(testDispatcher) {

            // Given

            every {
                repository.getUserById(userId)
            } returns flowOf(createUser())

            // When
            viewModel = UserDetailViewModel(
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
                            name = "test user",
                            reputation = "150",
                            location = "Manchester",
                            imageUrl = "https://example.com/image.jpg",
                            creationDate = "2020-01-01",
                            topTags = topTagNames,
                            badges = badgeUiState
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
            val user = createUser(
                id = userId,
                location = null
            )

            every {
                repository.getUserById(userId)
            } returns flowOf(user)

            viewModel = UserDetailViewModel(
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
            every { repository.getUserById(userId) } returns flow { throw NullPointerException() }

            viewModel = UserDetailViewModel(
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
                        ERROR_MESSAGE
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
            every { repository.getUserTopTags(userId) } returns flowOf(emptyList())
            every { repository.getUserBadges(userId) } returns flowOf(emptyList())

            viewModel = UserDetailViewModel(
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
            verify(exactly = 1) { repository.getUserById(42) }

            collectJob.cancel()
        }

    @Test
    fun `given user has top tags when user details are loaded then top tags are included in success state`() =
        runTest(testDispatcher) {

            // Given

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
            val successState = states
                .filterIsInstance<UserDetailUiState.Success>()
                .first()

            assertEquals(topTagNames, successState.user.topTags)

            verify(exactly = 1) {
                repository.getUserTopTags(userId)
            }

            collectJob.cancel()
        }

    @Test
    fun `given user has no top tags when user details are loaded then success contains empty top tags`() =
        runTest(testDispatcher) {

            // Given
            every { repository.getUserTopTags(userId) } returns flowOf(emptyList())

            viewModel = UserDetailViewModel(
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
            val successState = states
                .filterIsInstance<UserDetailUiState.Success>()
                .first()

            assertEquals(
                emptyList<String>(),
                successState.user.topTags
            )

            collectJob.cancel()
        }

    @Test
    fun `given top tags request fails when user details are loaded then error state is emitted`() =
        runTest(testDispatcher) {

            // Given
            every { repository.getUserById(userId) } returns flowOf(createUser(id = userId))

            every { repository.getUserTopTags(userId) } returns flow { throw IOException(ERROR_MESSAGE) }

            viewModel = UserDetailViewModel(
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
                    UserDetailUiState.Error(ERROR_MESSAGE)
                ),
                states
            )

            collectJob.cancel()
        }

    @Test
    fun `given user has badges when user details are loaded then badges are mapped to ui model`() =
        runTest(testDispatcher) {

            // Given
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
            val successState = states
                .filterIsInstance<UserDetailUiState.Success>()
                .first()

            assertEquals(badgeUiState, successState.user.badges)

            verify(exactly = 1) { repository.getUserBadges(userId) }

            collectJob.cancel()
        }

    @Test
    fun `given user has no badges when user details are loaded then success contains empty badges`() =
        runTest(testDispatcher) {

            // Given

            every { repository.getUserBadges(userId) } returns flowOf(emptyList())

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
            val successState = states
                .filterIsInstance<UserDetailUiState.Success>()
                .first()

            assertEquals(emptyList<BadgeUiState>(), successState.user.badges)

            collectJob.cancel()
        }

    @Test
    fun `given badges request fails when user details are loaded then error state is emitted`() =
        runTest(testDispatcher) {

            // Given
            every { repository.getUserBadges(userId) } returns
                    flow { throw IOException(ERROR_MESSAGE) }

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
                    UserDetailUiState.Error(ERROR_MESSAGE)
                ),
                states
            )
            collectJob.cancel()
        }

    companion object {
        private const val ERROR_MESSAGE = "Failed to retrieve user details"
    }
    private fun createUser(
        id: Int = 1,
        name: String = "test user",
        reputation: Int = 150,
        location: String? = "Manchester"
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