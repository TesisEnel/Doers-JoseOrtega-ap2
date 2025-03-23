package edu.ucne.doers.presentation.navigation

import android.app.Activity.RESULT_OK
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import edu.ucne.doers.presentation.home.HomeScreen
import edu.ucne.doers.presentation.padres.PadreScreen
import edu.ucne.doers.presentation.padres.PadreViewModel
import edu.ucne.doers.presentation.recompensa.RecompensaScreen
import edu.ucne.doers.presentation.recompensa.RecompensasListScreen
import edu.ucne.doers.presentation.sign_in.GoogleAuthUiClient
import edu.ucne.doers.presentation.tareas.padre.TareaScreen
import edu.ucne.doers.presentation.tareas.padre.TareasListScreen
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun DoersNavHost(
    navHostController: NavHostController,
    googleAuthUiClient: GoogleAuthUiClient,
) {
    val scope = rememberCoroutineScope()
    val viewModel: PadreViewModel = hiltViewModel()
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult(),
        onResult = { result ->
            Log.d("DoersNavHost", "Launcher onResult: resultCode = ${result.resultCode}")
            if (result.resultCode == RESULT_OK) {
                scope.launch {
                    Log.d("DoersNavHost", "Procesando resultado del inicio de sesión...")
                    val signInResult = googleAuthUiClient.signInWithIntent(
                        intent = result.data ?: return@launch
                    )
                    viewModel.onSignInResult(signInResult)
                    delay(500)
                    if (state.isSignInSuccessful && signInResult.errorMessage == null) {
                        Log.d("DoersNavHost", "Inicio de sesión exitoso, navegando a PadreScreen")
                        navHostController.navigate(Screen.Padre) {
                            popUpTo(Screen.Home) { inclusive = true }
                        }
                    } else {
                        Log.e("DoersNavHost", "Error en inicio de sesión: ${signInResult.errorMessage}")
                        viewModel.setLoading(false)
                        viewModel.setSignInError(signInResult.errorMessage ?: "Error desconocido")
                    }
                }
            } else {
                Log.e("DoersNavHost", "Resultado no OK: ${result.resultCode}")
                viewModel.setLoading(false)
                viewModel.setSignInError("Inicio de sesión cancelado")
            }
        }
    )

    if (state.isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    } else {
        NavHost(
            navController = navHostController,
            startDestination = Screen.Home
        ) {
            composable<Screen.Home> {
                HomeScreen(
                    onSignClickWithGoogle = {
                        scope.launch {
                            Log.d("DoersNavHost", "Verificando si el usuario está autenticado...")
                            val isAuthenticated = viewModel.isAuthenticated()
                            Log.d("DoersNavHost", "isAuthenticated = $isAuthenticated")
                            if (isAuthenticated) {
                                Log.d("DoersNavHost", "Usuario ya autenticado, cargando datos...")
                                viewModel.setLoading(true)
                                viewModel.getCurrentUser()
                                delay(100)
                                if (state.isSignInSuccessful) {
                                    Log.d("DoersNavHost", "Datos cargados, navegando a PadreScreen")
                                    navHostController.navigate(Screen.Padre) {
                                        popUpTo(Screen.Home) { inclusive = true }
                                    }
                                } else {
                                    Log.e("DoersNavHost", "Usuario autenticado pero datos no cargados")
                                    viewModel.setSignInError("Error al cargar datos del usuario")
                                }
                                viewModel.setLoading(false)
                            } else {
                                Log.d("DoersNavHost", "Usuario no autenticado, iniciando proceso de inicio de sesión...")
                                viewModel.setLoading(true)
                                val signInIntentSender = googleAuthUiClient.signIn()
                                if (signInIntentSender != null) {
                                    launcher.launch(
                                        IntentSenderRequest.Builder(signInIntentSender).build()
                                    )
                                } else {
                                    Log.e("DoersNavHost", "signInIntentSender es null")
                                    viewModel.setLoading(false)
                                    viewModel.setSignInError("Error al iniciar el proceso de autenticación")
                                }
                            }
                        }
                    }
                )
            }

            composable<Screen.Padre> {
                PadreScreen(
                    viewModel = viewModel,
                    navController = navHostController,
                )
            }

            composable<Screen.Recompensa> { arg ->
                val recompensaId = arg.toRoute<Screen.Recompensa>().recompensaId
                RecompensaScreen(
                    recompensaId = recompensaId,
                    goRecompensasList = {
                        navHostController.navigate(Screen.RecompensaList)
                    }
                )
            }

            composable<Screen.RecompensaList> {
                RecompensasListScreen(
                    createRecompensa = {
                        navHostController.navigate(Screen.Recompensa(0))
                    },
                    goToRecompensa = {
                        navHostController.navigate(Screen.Recompensa(it))
                    },
                    navController = navHostController
                )
            }
            composable<Screen.TareasList> {
                TareasListScreen(
                    goToAgregarTarea = { navHostController.navigate(Screen.Tarea(0)) },
                    navController = navHostController
                )
            }

            composable<Screen.Tarea> {
                val tareaId = it.toRoute<Screen.Tarea>().tareaId
                TareaScreen(
                    goBackToPantallaTareas = { navHostController.navigate(Screen.TareasList) },
                    tareaId = tareaId
                )
            }
        }
    }
}