package edu.ucne.doers.presentation.navigation

import android.app.Activity.RESULT_OK
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import edu.ucne.doers.presentation.home.HomeScreen
import edu.ucne.doers.presentation.padres.PadreViewModel
import edu.ucne.doers.presentation.sign_in.GoogleAuthUiClient
import kotlinx.coroutines.launch

@Composable
fun DoersNavHost(
    navHostController: NavHostController,
    googleAuthUiClient: GoogleAuthUiClient
) {
    val scope = rememberCoroutineScope()
    NavHost(
        navController = navHostController,
        startDestination = Screen.Home
    ) {
        composable<Screen.Home> {
            val viewModel: PadreViewModel = hiltViewModel()
            val state by viewModel.uiState.collectAsStateWithLifecycle()

            // sonar-ignore: launcher is used indirectly in onSignUsuarioClick
            val launcher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.StartIntentSenderForResult(),
                onResult = {result ->
                    if(result.resultCode == RESULT_OK){
                        scope.launch {
                            val signInResult =
                                googleAuthUiClient.signInWithIntent(
                                    intent = result.data ?: return@launch
                                )
                            viewModel.onSignInResult(signInResult)
                            navHostController.navigate(Screen.Home){
                                popUpTo(Screen.Home) { inclusive = true }
                            }
                        }
                    }
                }
            )
            HomeScreen(
                onSignClickWithGoogle = {
                    scope.launch {
                        val signInIntentSender = googleAuthUiClient.signIn()
                        launcher.launch(
                            IntentSenderRequest.Builder(
                                signInIntentSender ?: return@launch
                            ).build()
                        )
                    }
                }
            )
        }
    }
}