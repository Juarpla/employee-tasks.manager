import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.transitions.SlideTransition
import data.MongoDB
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.core.context.startKoin
import org.koin.dsl.module
import presentation.screen.home.HomeScreen
import presentation.screen.home.HomeViewModel
import presentation.screen.task.TaskViewModel

val lightBlueColor = Color(color = 0xFF81D4FA)
val darkBlueColor = Color(color = 0xFF1565C0)

@Composable
@Preview
fun App() {
    initializeKoin()

    // Define the color scheme for the app from Material Design 3
    val lightColors = lightColorScheme(
        primary = lightBlueColor,
        onPrimary = darkBlueColor,
        primaryContainer = lightBlueColor,
        onPrimaryContainer = darkBlueColor
    )
    val darkColors = darkColorScheme(
        primary = lightBlueColor,
        onPrimary = darkBlueColor,
        primaryContainer = lightBlueColor,
        onPrimaryContainer = darkBlueColor
    )
    // Set the color scheme based on the system theme
    val colors by mutableStateOf(
        if (isSystemInDarkTheme()) darkColors else lightColors
    )

    // Implement the Navigator with the SlideTransition
    MaterialTheme(colorScheme = colors) {
        Navigator(HomeScreen()) {
            SlideTransition(it)
        }
    }
}

// Define the module for the MongoDB dependency as persistence memory
val mongoModule = module {
    single {MongoDB()}
    factory {HomeViewModel(get())}
    factory {TaskViewModel(get())}
}

// Initialize Koin as a dependency injection framework
fun initializeKoin() {
    startKoin {
        modules(mongoModule)
    }
}