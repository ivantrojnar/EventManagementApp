package hr.itrojnar.eventmanagement.model

data class UserAuthDetails(
    val username: String,
    val password: String,
    val accessToken: String,
    val refreshToken: String
)
