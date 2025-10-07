# Network Layer Documentation

## Overview

The network layer provides a complete integration with the ThinkTwice API, including:
- OAuth2 authentication (Google/Apple)
- Automatic token refresh
- Centralized error handling
- Repository pattern for API calls

## Architecture

```
NetworkModule (DI Container)
    ├── AuthTokenManager (Token storage & validation)
    ├── HttpClientFactory (HTTP client setup)
    ├── ApiService (API interfaces)
    └── Repositories
        ├── AuthRepository
        ├── UserRepository
        ├── AnalyticsRepository
        ├── CurrencyRepository
        └── ReferralRepository
```

## Setup

### Initialize Network Module

```kotlin
val database = DatabaseManager.getDatabase()
val networkModule = NetworkModule(database)
```

## Usage Examples

### 1. Authentication

#### Sign in with OAuth2

```kotlin
val authRepository = networkModule.authRepository

// Sign in with Google
val result = authRepository.signInWithOAuth2(
    provider = "google",
    token = "google_id_token",
    deviceId = "device_123",
    userAgent = "ThinkTwice-Android/1.0",
    referralCode = "TT0001ABC123" // Optional
)

result.onSuccess { response ->
    // Access token and refresh token are automatically stored
    val user = response.user
    println("Logged in as: ${user.email}")
}

result.onFailure { exception ->
    when (exception) {
        is ApiException.AuthException -> {
            // Handle auth error
        }
        is ApiException.NetworkException -> {
            // Handle network error
        }
        else -> {
            // Handle other errors
        }
    }
}
```

#### Check Token Validity

```kotlin
val userId = 123L
val isValid = authRepository.isTokenValid(userId)

if (!isValid) {
    // Token will be automatically refreshed on next API call
    // Or manually refresh:
    authRepository.refreshToken(userId)
}
```

#### Logout

```kotlin
authRepository.logout(userId).onSuccess {
    // Tokens cleared, user logged out
}
```

### 2. User Management

```kotlin
val userRepository = networkModule.userRepository

// Get all users
userRepository.getAllUsers(
    userId = currentUserId,
    skip = 0,
    limit = 100
).onSuccess { response ->
    response.users.forEach { user ->
        println("${user.full_name} - ${user.email}")
    }
}

// Get specific user
userRepository.getUserById(currentUserId, targetUserId = 456)
    .onSuccess { user ->
        println(user.full_name)
    }

// Update user
userRepository.updateUser(
    userId = currentUserId,
    targetUserId = currentUserId.toInt(),
    fullName = "John Doe",
    isActive = true
).onSuccess { updatedUser ->
    println("Updated: ${updatedUser.full_name}")
}
```

### 3. Analytics

```kotlin
val analyticsRepository = networkModule.analyticsRepository

// Get spending analytics
analyticsRepository.getSpendingAnalytics(
    userId = currentUserId,
    period = "month" // day, week, month, year
).onSuccess { analytics ->
    println("Total spent: ${analytics.total_spent}")
    println("Categories:")
    analytics.categories.forEach { category ->
        println("  ${category.category}: ${category.amount} (${category.percentage}%)")
    }
}

// Get goal progress
analyticsRepository.getGoalProgress(currentUserId)
    .onSuccess { progress ->
        println("Active goals: ${progress.active_goals}")
        progress.goals.forEach { goal ->
            println("${goal.name}: ${goal.progress_percentage}%")
        }
    }
```

### 4. Currency & Countries

```kotlin
val currencyRepository = networkModule.currencyRepository

// Get all countries
currencyRepository.getAllCountries()
    .onSuccess { response ->
        response.countries.forEach { country ->
            println("${country.country_flag_icon} ${country.country_name} - ${country.currency?.code}")
        }
    }

// Search countries
currencyRepository.searchCountries("United")
    .onSuccess { countries ->
        countries.forEach { country ->
            println(country.country_name)
        }
    }

// Get all currencies
currencyRepository.getAllCurrencies()
    .onSuccess { response ->
        response.currencies.forEach { currency ->
            println("${currency.code} - ${currency.name}")
        }
    }
```

### 5. Referral System

```kotlin
val referralRepository = networkModule.referralRepository

// Create referral code
referralRepository.createReferralCode(
    userId = currentUserId,
    rewardType = "gucci_bag",
    rewardDescription = "Luxury Gucci handbag",
    maxUses = 10,
    expiresInDays = 30
).onSuccess { code ->
    println("Referral code: ${code.code}")
}

// Validate referral code
referralRepository.validateReferralCode("TT0001ABC123")
    .onSuccess { validation ->
        if (validation.is_valid) {
            println("Valid code! Reward: ${validation.reward_description}")
        }
    }

// Use referral code
referralRepository.useReferralCode("TT0001ABC123")
    .onSuccess { response ->
        println("Referral applied! Status: ${response.reward_status}")
    }

// Get stats
referralRepository.getReferralStats(currentUserId)
    .onSuccess { stats ->
        println("Referrals sent: ${stats.total_referrals_sent}")
        println("Rewards earned: ${stats.total_rewards_earned}")
    }

// Redeem reward
referralRepository.redeemReward(
    userId = currentUserId,
    referralId = 1
).onSuccess { reward ->
    println("Redemption code: ${reward.redemption_code}")
    println("Instructions: ${reward.redemption_instructions}")
}
```

## Error Handling

All repository methods return `Result<T>`. Handle errors appropriately:

```kotlin
result.onSuccess { data ->
    // Use data
}.onFailure { exception ->
    when (exception) {
        is ApiException.NetworkException -> {
            // No internet connection
            showMessage("Please check your internet connection")
        }
        is ApiException.AuthException -> {
            // Unauthorized (401/403)
            navigateToLogin()
        }
        is ApiException.ValidationException -> {
            // Bad request (400/422)
            showMessage(exception.errorResponse.detail)
        }
        is ApiException.RateLimitException -> {
            // Too many requests (429)
            val retryAfter = exception.retryAfterSeconds
            showMessage("Rate limit exceeded. Try again in $retryAfter seconds")
        }
        is ApiException.NotFoundException -> {
            // Resource not found (404)
            showMessage("Resource not found")
        }
        is ApiException.ServerException -> {
            // Server error (5xx)
            showMessage("Server error. Please try again later")
        }
        else -> {
            showMessage("An error occurred: ${exception.message}")
        }
    }
}
```

## Token Management

Tokens are automatically:
1. **Stored** in the local database after sign-in/refresh
2. **Validated** before each API call
3. **Refreshed** when expired
4. **Added** to request headers automatically

### Manual Token Operations

```kotlin
val tokenManager = networkModule.authTokenManager

// Get access token
val accessToken = tokenManager.getAccessToken(userId)

// Get refresh token
val refreshToken = tokenManager.getRefreshToken(userId)

// Check validity
val isValid = tokenManager.isTokenValid(userId)

// Clear tokens (logout)
tokenManager.clearTokens(userId)
```

## API Endpoints Coverage

### Authentication
- ✅ POST /api/v1/oauth2 - OAuth2 Sign In
- ✅ POST /api/v1/auth/refresh - Refresh Token
- ✅ POST /api/v1/auth/logout - Logout

### User Management
- ✅ GET /api/v1/users - Get All Users
- ✅ GET /api/v1/users/{user_id} - Get User by ID
- ✅ PUT /api/v1/users/{user_id} - Update User

### Analytics
- ✅ GET /api/v1/analytics/spending - Get Spending Analytics
- ✅ GET /api/v1/analytics/goals/progress - Get Goal Progress

### Currency & Countries
- ✅ GET /api/v1/currency/countries - Get All Countries
- ✅ GET /api/v1/currency/countries/search - Search Countries
- ✅ GET /api/v1/currency/currencies - Get All Currencies

### Referral System
- ✅ POST /api/v1/referral/codes - Create Referral Code
- ✅ POST /api/v1/referral/validate - Validate Referral Code
- ✅ POST /api/v1/referral/use - Use Referral Code
- ✅ GET /api/v1/referral/stats - Get Referral Stats
- ✅ POST /api/v1/referral/rewards/redeem - Redeem Reward

## Best Practices

1. **Always use repositories** - Don't call API directly
2. **Handle all error cases** - Use comprehensive when statements
3. **Use Result pattern** - onSuccess/onFailure for clean code
4. **Don't store tokens manually** - AuthTokenManager handles it
5. **Check token validity** - Before critical operations
6. **Clean up resources** - Call networkModule.close() when done
