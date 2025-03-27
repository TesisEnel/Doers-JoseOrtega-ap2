package edu.ucne.doers

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.auth.api.identity.Identity
import dagger.hilt.android.AndroidEntryPoint
import edu.ucne.doers.presentation.navigation.DoersNavHost
import edu.ucne.doers.presentation.sign_in.GoogleAuthUiClient
import edu.ucne.doers.ui.theme.DoersTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DoersTheme {
                val navHost = rememberNavController()
                val googleAuthUiClient by lazy {
                    GoogleAuthUiClient(
                        context = applicationContext,
                        oneTapClient = Identity.getSignInClient(applicationContext),
                        sharedPreferences = getSharedPreferences("user", MODE_PRIVATE)
                    )
                }
                DoersNavHost(navHost, googleAuthUiClient)
            }
        }
    }
}