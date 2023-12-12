package hr.itrojnar.eventmanagement.nav

sealed class Screen(val route: String) {
    object Main : Screen("main")
    object UpdateEvent : Screen("updateEvent")
}
