# Stack Exchange Users

An Android application built in Kotlin that uses the Stack Exchange API to search for Stack Overflow users and display their profile details.

The application was developed as a technical exercise with a focus on clean separation of concerns, asynchronous data loading, error handling, testability, and modern Android development practices.

## Features

- Search Stack Overflow users by name
- Display up to 20 users in alphabetical order
- Display username and reputation in search results
- Navigate to a user's detail screen
- Display user profile information:
    - Avatar
    - Username
    - Reputation
    - Location
    - Creation date
    - Top tags
    - Badges and badge rank
- Loading, success and error states
- Scrollable search results
- Back navigation from the user detail screen

## Tech Stack

- Kotlin
- Jetpack Compose
- MVVM
- Kotlin Coroutines
- Flow / StateFlow
- Retrofit
- OkHttp
- Gson
- Coil
- Jetpack Navigation Compose
- JUnit 4
- MockK
- Kotlin Coroutines Test

## Architecture

The application follows an MVVM-style architecture with a repository layer separating the UI and data sources.

The UI observes immutable `StateFlow` state exposed by the ViewModels. ViewModels coordinate the application's presentation logic and request data through the repository abstraction.

The repository communicates with the Stack Exchange API through Retrofit and maps API response DTOs into domain models before exposing them to the rest of the application.

The detail ViewModel combines the user, top tags, and badge streams into a single `UserDetailUiState`. This means the Compose screen only needs to observe one source of UI state.

## Project Structure

The main responsibilities are separated as follows:

```text
UI
├── Compose screens
├── UI models
└── UI state

ViewModel
├── UsersViewModel
└── UserDetailViewModel

Repository
├── UserRepository
└── UserRepositoryImpl

Network
├── RetrofitClient
├── UserService
└── API response models

Domain
├── User
├── TopTag
└── Badge

Mapping
├── UserResponse -> User
├── TopTagResponse -> TopTag
└── BadgeResponse -> Badge
```

## API

The application uses the Stack Exchange API v2.3.

The Retrofit base URL is:

```text
https://api.stackexchange.com/2.3/
```

The application uses the following endpoints:

```text
GET users
GET users/{id}
GET users/{id}/top-tags
GET users/{id}/badges
```

Requests target Stack Overflow using:

```text
site=stackoverflow
```

User searches request a maximum of 20 results ordered alphabetically.

## Asynchronous Data

Retrofit `suspend` functions are used for network requests.

The repository exposes data using Kotlin `Flow`, while ViewModels expose UI state using `StateFlow`.

For example:

```text
Retrofit suspend request
        ↓
Repository Flow
        ↓
ViewModel
        ↓
StateFlow<UiState>
        ↓
Jetpack Compose
```

Search requests use `flatMapLatest`, allowing a newer search request to replace an earlier request.

The user detail screen uses `combine` to combine the user profile, top tags and badges into a single UI state.

## Error Handling

Network and API failures propagate from the repository to the ViewModel.

ViewModels convert failures into explicit UI states:

```kotlin
sealed interface UiState {
    data object Idle : UiState
    data object Loading : UiState
    data class Success(...) : UiState
    data class Error(val message: String) : UiState
}
```

This keeps networking concerns outside the Compose UI and allows the screen to render based only on its current state.

## Testing

Unit tests cover business and presentation logic.

### Repository Tests

Repository tests verify:

- API responses are correctly mapped to domain models
- Empty API responses are handled correctly
- Network exceptions are propagated
- User details are mapped correctly
- Top tags are mapped correctly
- Badges and badge ranks are mapped correctly

Retrofit service calls are mocked using MockK.

### ViewModel Tests

ViewModel tests verify:

- Initial state
- Loading state
- Successful search results
- Error states
- User detail mapping
- Top tag mapping
- Badge mapping
- Empty top tags and badges
- Repository failures are converted into error UI states

Coroutine tests use `kotlinx-coroutines-test` so asynchronous behaviour can be tested deterministically.

## Build

The project requires Android Studio and an Android SDK capable of compiling the configured SDK version.

Clone the repository and open it in Android Studio, or build from the command line:

```bash
./gradlew lint test assembleDebug
```

This runs:

- Android lint checks
- Unit tests
- Debug APK compilation

## Minimum Android Version

The application supports:

```text
minSdk = 21
```

## Design Decisions

### Repository abstraction

ViewModels depend on `UserRepository` rather than directly on Retrofit. This keeps network implementation details out of the presentation layer and makes ViewModels straightforward to unit test.

### API DTOs and domain models

API response objects are kept separate from domain models.

For example:

```text
BadgeResponse
     ↓
   Mapper
     ↓
Badge
```

This prevents Stack Exchange API naming and response structure from leaking into the rest of the application.

### Navigation by user ID

Only the user's ID is passed between the search and detail screens.

The detail screen retrieves the required information through its ViewModel rather than passing an entire user object through navigation.

This keeps navigation arguments small and ensures the detail screen obtains its data from the application's data layer.

### Single detail UI state

User information, top tags, and badges are combined in `UserDetailViewModel`.

The Compose UI therefore observes a single:

```text
StateFlow<UserDetailUiState>
```

rather than coordinating several independent data streams itself.

## Potential Improvements

Given more time, possible improvements include:

- Modularisation
- Dependency injection
- More granular error handling and user-friendly error messages
- Retry functionality
- Partial-success handling so optional tag or badge failures do not prevent core user information from being displayed
- Pagination for larger result sets
- UI/instrumentation tests
- Improved accessibility coverage
- Additional formatting for reputation, dates, tags and badge ranks
- Dependency injection using a DI framework
- Offline caching
- Better UI designs

## API Documentation

Stack Exchange API documentation:

https://api.stackexchange.com/docs

