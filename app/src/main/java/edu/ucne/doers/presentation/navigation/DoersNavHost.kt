package edu.ucne.doers.presentation.navigation

import android.app.Activity.RESULT_OK
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
import edu.ucne.doers.presentation.hijos.HijoLoginScreen
import edu.ucne.doers.presentation.hijos.HijoScreen
import edu.ucne.doers.presentation.hijos.HijoViewModel
import edu.ucne.doers.presentation.home.HomeScreen
import edu.ucne.doers.presentation.padres.PadreScreen
import edu.ucne.doers.presentation.padres.PadreViewModel
import edu.ucne.doers.presentation.recompensa.hijo.RecompensasHijoScreen
import edu.ucne.doers.presentation.recompensa.padre.RecompensaScreen
import edu.ucne.doers.presentation.recompensa.padre.RecompensasListScreen
import edu.ucne.doers.presentation.sign_in.GoogleAuthUiClient
import edu.ucne.doers.presentation.tareas.hijo.HijoListScreen
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
    val padreViewModel: PadreViewModel = hiltViewModel()
    val hijoViewModel: HijoViewModel = hiltViewModel()
    val padreState by padreViewModel.uiState.collectAsStateWithLifecycle()

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult(),
        onResult = { result ->
            if (result.resultCode == RESULT_OK) {
                scope.launch {
                    val signInResult = googleAuthUiClient.signInWithIntent(
                        intent = result.data ?: return@launch
                    )
                    padreViewModel.onSignInResult(signInResult) {
                        navHostController.navigate(Screen.Padre) {
                            popUpTo(Screen.Home) { inclusive = true }
                        }
                    }
                }
            } else {
                padreViewModel.setLoading(false)
                padreViewModel.setSignInError("Inicio de sesión cancelado")
            }
        }
    )

    if (padreState.isLoading) {
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
                            val isAuthenticated = padreViewModel.isAuthenticated()
                            if (isAuthenticated) {
                                // Ya autenticado → ir directo al perfil
                                navHostController.navigate(Screen.Padre) {
                                    popUpTo(Screen.Home) { inclusive = true }
                                }
                            } else {
                                padreViewModel.setLoading(true)
                                val intentSender = googleAuthUiClient.signIn()
                                if (intentSender != null) {
                                    launcher.launch(IntentSenderRequest.Builder(intentSender).build())
                                } else {
                                    padreViewModel.setLoading(false)
                                    padreViewModel.setSignInError("Error al iniciar sesión con Google")
                                }
                            }
                        }
                    },
                    onHijoClick = {
                        scope.launch {
                            val isHijoAuthenticated = hijoViewModel.isAuthenticated()
                            if (isHijoAuthenticated) {
                                navHostController.navigate(Screen.Hijo) {
                                    popUpTo(Screen.Home) { inclusive = true }
                                }
                            } else {
                                navHostController.navigate(Screen.HijoLogin)
                            }
                        }
                    }
                )
            }

            composable<Screen.Padre> {
                PadreScreen(
                    padreViewModel = padreViewModel,
                    hijoViewModel = hijoViewModel,
                    onNavigateToTareas = { navHostController.navigate(Screen.TareaList) },
                    onNavigateToRecompensas = { navHostController.navigate(Screen.RecompensaList) },
                    onNavigateToPerfil = { navHostController.navigate(Screen.Home) },
                    onSignOut = {
                        padreViewModel.signOut()
                        navHostController.navigate(Screen.Home) {
                            popUpTo(Screen.Padre) { inclusive = true }
                        }
                    }
                )
            }
            composable<Screen.Hijo> {
                HijoScreen(
                    hijoViewModel = hijoViewModel,
                    padreViewModel = padreViewModel,
                    onNavigateToTareas = { navHostController.navigate(Screen.TareaHijo) },
                    onNavigateToRecompensas = { navHostController.navigate(Screen.RecompensaHijo) }
                )
            }
            composable<Screen.HijoLogin> {
                HijoLoginScreen(
                    onChildLoggedIn = {
                        scope.launch {
                            navHostController.navigate(Screen.Hijo) {
                                popUpTo(Screen.Home) { inclusive = true }
                            }
                        }
                    },
                    navController = navHostController
                )
            }
            composable<Screen.Recompensa> { arg ->
                val recompensaId = arg.toRoute<Screen.Recompensa>().recompensaId
                RecompensaScreen(
                    recompensaId = recompensaId,
                    goRecompensasList = { navHostController.navigate(Screen.RecompensaList) }
                )
            }
            composable<Screen.RecompensaList> {
                RecompensasListScreen(
                    goToAgregarRecompensa = { navHostController.navigate(Screen.Recompensa(0)) },
                    goToEditarRecompensa = { navHostController.navigate(Screen.Recompensa(it)) },
                    onNavigateToPerfil = { navHostController.navigate(Screen.Padre) },
                    onNavigateToTareas = { navHostController.navigate(Screen.TareaList) }
                )
            }
            composable<Screen.RecompensaHijo> {
                RecompensasHijoScreen(
                    padreId = padreState.padreId ?: "",
                    onNavigateToPerfil = { navHostController.navigate(Screen.Hijo) },
                    onNavigateToTareas = { navHostController.navigate(Screen.TareaHijo) }
                )
            }
            composable<Screen.TareaList> {
                TareasListScreen(
                    goToAgregarTarea = { navHostController.navigate(Screen.Tarea(0)) },
                    onNavigateToPerfil = { navHostController.navigate(Screen.Padre) },
                    onNavigateToRecompensas = { navHostController.navigate(Screen.RecompensaList) },
                    goToEditarTarea = { tareaId ->
                        navHostController.navigate(Screen.Tarea(tareaId))
                    }
                )
            }
            composable<Screen.Tarea> {
                val tareaId = it.toRoute<Screen.Tarea>().tareaId
                TareaScreen(
                    goBackToPantallaTareas = { navHostController.navigate(Screen.TareaList) },
                    tareaId = tareaId
                )
            }
            composable<Screen.TareaHijo> {
                HijoListScreen(
                    onNavigateToPerfil = { navHostController.navigate(Screen.Hijo) },
                    onNavigateToRecompensas = { navHostController.navigate(Screen.RecompensaHijo) }
                )
            }
        }
    }
}