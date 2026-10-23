package task07

sealed interface ApiRequest
data class LoginRequest(val username: String, val password: String) : ApiRequest
data object LogoutRequest : ApiRequest
data class RefreshRequest(val token: String) : ApiRequest

data class UserData(val userId: String, val name: String)

sealed class ApiResponse {
    data class UserSuccess(val user: UserData) : ApiResponse()
    data object NotLoggedIn : ApiResponse()
    data class Error(val message: String) : ApiResponse()
}

val accounts = mapOf("ali" to "1234", "dilnoza" to "qwerty")
var currentUser: UserData? = null

fun handleRequest(request: ApiRequest): ApiResponse = when (request) {
    is LoginRequest -> {
        if (accounts[request.username] == request.password) {
            val name = request.username.replaceFirstChar { it.uppercase() }
            val user = UserData("u-${request.username}", name)
            currentUser = user
            ApiResponse.UserSuccess(user)
        } else {
            ApiResponse.Error("Invalid username or password")
        }
    }
    LogoutRequest -> {
        val user = currentUser
        currentUser = null
        if (user == null) ApiResponse.NotLoggedIn else ApiResponse.UserSuccess(user)
    }
    is RefreshRequest -> {
        ApiResponse.Error("Refresh is not supported yet")
    }
}

fun show(response: ApiResponse) {
    when (response) {
        is ApiResponse.UserSuccess -> println("OK: ${response.user.name} (${response.user.userId})")
        ApiResponse.NotLoggedIn -> println("Nobody is logged in")
        is ApiResponse.Error -> println("FAILED: ${response.message}")
    }
}

fun main() {
    println("Commands: login, logout, refresh, exit")
    while (true) {
        print("> ")
        when (readln().trim()) {
            "login" -> {
                print("Username: ")
                val username = readln().trim()
                print("Password: ")
                val password = readln().trim()
                show(handleRequest(LoginRequest(username, password)))
            }
            "logout" -> show(handleRequest(LogoutRequest))
            "refresh" -> {
                print("Token: ")
                val token = readln().trim()
                show(handleRequest(RefreshRequest(token)))
            }
            "exit" -> return
            else -> println("Unknown command")
        }
    }
}
