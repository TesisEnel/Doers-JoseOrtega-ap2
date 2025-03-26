package edu.ucne.doers.presentation.hijos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.ucne.doers.data.remote.Resource
import edu.ucne.doers.data.repository.HijoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HijoViewModel @Inject constructor(
    private val hijoRepository: HijoRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HijoUiState())
    val uiState = _uiState.asStateFlow()

    fun loginChild(nombre: String, codigoSala: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, signInError = null) }
            when (val resource = hijoRepository.loginHijo(nombre, codigoSala)) {
                is Resource.Success -> {
                    _uiState.update {
                        it.copy(
                            isSuccess = true,
                            isSignInSuccessful = true,
                            nombre = nombre,
                            codigoSala = codigoSala,
                            isLoading = false,
                            signInError = null
                        )
                    }
                }
                is Resource.Error -> {
                    _uiState.update {
                        it.copy(
                            isSuccess = false,
                            isSignInSuccessful = false,
                            isLoading = false,
                            signInError = resource.message
                        )
                    }
                }
                is Resource.Loading -> {
                    _uiState.update { it.copy(isLoading = true) }
                }
            }
        }
    }
}
