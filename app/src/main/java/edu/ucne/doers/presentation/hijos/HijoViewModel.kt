package edu.ucne.doers.presentation.hijos

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import edu.ucne.doers.data.local.entity.CanjeoEntity
import edu.ucne.doers.data.local.entity.HijoEntity
import edu.ucne.doers.data.local.entity.TareaHijo
import edu.ucne.doers.data.local.entity.TransaccionHijo
import edu.ucne.doers.data.local.model.EstadoRecompensa
import edu.ucne.doers.data.local.model.EstadoTareaHijo
import edu.ucne.doers.data.local.model.PeriodicidadTarea
import edu.ucne.doers.data.remote.Resource
import edu.ucne.doers.data.repository.CanjeoRepository
import edu.ucne.doers.data.repository.HijoRepository
import edu.ucne.doers.data.repository.RecompensaRepository
import edu.ucne.doers.data.repository.TareaHijoRepository
import edu.ucne.doers.data.repository.TareaRepository
import edu.ucne.doers.data.repository.TransaccionHijoRepository
import edu.ucne.doers.presentation.extension.ifNullOrBlank
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class HijoViewModel @Inject constructor(
    private val hijoRepository: HijoRepository,
    private val tareaRepository: TareaRepository,
    private val tareaHijoRepository: TareaHijoRepository,
    private val transaccionHijoRepository: TransaccionHijoRepository,
    private val recompensaRepository: RecompensaRepository,
    private val canjeoRepository: CanjeoRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow(HijoUiState())
    val uiState = _uiState.asStateFlow()

    private val sharedPreferences = context.getSharedPreferences("HijoPrefs", Context.MODE_PRIVATE)
    private val _periodicidadesDisponibles = MutableStateFlow<List<String>>(emptyList())
    val periodicidadesDisponibles = _periodicidadesDisponibles.asStateFlow()

    private val _transacciones = MutableStateFlow<List<TransaccionHijo>>(emptyList())
    val transacciones: StateFlow<List<TransaccionHijo>> = _transacciones.asStateFlow()


    init {
        checkAuthenticatedHijo()
        actualizarPeriodicidades()
        observeTaskChanges()
        getTransacciones()
        loadSaldoActual()
    }

    fun loadSaldoActual() {
        viewModelScope.launch {
            val hijoId = _uiState.value.hijoId

            if (hijoId != null) {
                val hijo = hijoRepository.find(hijoId)
                if (hijo != null) {
                    _uiState.update {
                        it.copy(saldoActual = hijo.saldoActual)
                    }
                }
            }
        }
    }

    private fun observeTaskChanges() {
        viewModelScope.launch {
            tareaHijoRepository.getAll().collect { resource ->
                when (resource) {
                    is Resource.Success -> {
                        loadTareas()
                    }

                    is Resource.Error -> {
                        _uiState.update {
                            it.copy(
                                errorMessage = resource.message ?: "Error al observar tareas hijo"
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

    private fun checkAuthenticatedHijo() {
        viewModelScope.launch {
            val hijoId = sharedPreferences.getInt("hijoId", 0)
            if (hijoId > 0) {
                val hijo = hijoRepository.find(hijoId)
                if (hijo != null) {
                    _uiState.update {
                        it.copy(
                            hijoId = hijo.hijoId,
                            padreId = hijo.padreId,
                            nombre = hijo.nombre,
                            fotoPerfil = hijo.fotoPerfil,
                            isAuthenticated = true
                        )
                    }
                    loadTareas()
                } else {
                    sharedPreferences.edit().remove("hijoId").apply()
                    _uiState.update {
                        it.copy(
                            hijoId = 0,
                            padreId = "",
                            nombre = "",
                            isAuthenticated = false
                        )
                    }
                }
            }
        }
    }

    fun isAuthenticated(): Boolean = uiState.value.isAuthenticated

    fun loginChild(nombre: String, codigoSala: String) {
        viewModelScope.launch {
            if (nombre.isBlank()) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        signInError = "El nombre no puede estar vacío",
                        isSignInSuccessful = false,
                        isSuccess = false,
                        isAuthenticated = false
                    )
                }
                return@launch
            }

            _uiState.update { it.copy(isLoading = true, signInError = null) }

            when (val result = hijoRepository.loginHijo(nombre, codigoSala)) {
                is Resource.Success -> {
                    val hijo = result.data ?: run {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                signInError = "Error desconocido al guardar el hijo",
                                isAuthenticated = false
                            )
                        }
                        return@launch
                    }

                    val padreDto = hijoRepository.getPadreByCodigoSala(codigoSala)
                    val codigoSalaReal = padreDto.codigoSala ?: codigoSala

                    val fotoAsignada = hijo.fotoPerfil.ifNullOrBlank("personaje_1")
                    val hijoConFoto = hijo.copy(fotoPerfil = fotoAsignada)

                    hijoRepository.saveLocal(hijoConFoto)

                    _uiState.update { it.copy(isLoading = true) }

                    viewModelScope.launch {
                        delay(150)

                        _uiState.update {
                            it.copy(
                                hijoId = hijoConFoto.hijoId,
                                padreId = hijoConFoto.padreId,
                                nombre = hijoConFoto.nombre,
                                fotoPerfil = hijoConFoto.fotoPerfil,
                                codigoSala = codigoSalaReal,
                                isSuccess = true,
                                isSignInSuccessful = true,
                                isLoading = false,
                                isAuthenticated = true
                            )
                        }
                    }

                    sharedPreferences.edit().putInt("hijoId", hijoConFoto.hijoId).apply()
                }

                is Resource.Error -> {
                    _uiState.update {
                        it.copy(
                            isSuccess = false,
                            isSignInSuccessful = false,
                            isLoading = false,
                            signInError = result.message,
                            isAuthenticated = false
                        )
                    }
                }

                else -> {}
            }
        }
    }

    fun getHijosByPadre(padreId: String) {
        viewModelScope.launch {
            hijoRepository.getAll().collect { result ->
                when (result) {
                    is Resource.Loading -> {
                        _uiState.update { it.copy(isLoading = true) }
                    }

                    is Resource.Success -> {
                        val hijosFiltrados =
                            result.data?.filter { it.padreId == padreId } ?: emptyList()
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                hijos = hijosFiltrados
                            )
                        }
                    }

                    is Resource.Error -> {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                errorMessage = result.message ?: "Error al obtener hijos"
                            )
                        }
                    }
                }
            }
        }
    }

    fun agregarPuntos(hijo: HijoEntity, puntos: String) {
        viewModelScope.launch {
            try {
                val puntosInt = puntos.toIntOrNull()
                    ?: throw IllegalArgumentException("Ingrese un número válido de puntos")
                if (puntosInt <= 0) throw IllegalArgumentException("Los puntos deben ser mayores a 0")

                val hijoActualizado = hijo.copy(saldoActual = hijo.saldoActual + puntosInt)
                hijoRepository.save(hijoActualizado).collect { result ->
                    when (result) {
                        is Resource.Success -> {
                            _uiState.update { it.copy(successMessage = "Puntos agregados con éxito") }
                        }

                        is Resource.Error -> throw Exception("Error al agregar puntos: ${result.message}")
                        is Resource.Loading -> Log.d("HijoViewModel", "Guardando puntos...")
                    }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessage = "Error al agregar puntos: ${e.message}") }
            }
        }
    }

    fun eliminarHijo(hijo: HijoEntity) {
        viewModelScope.launch {
            when (val result = hijoRepository.delete(hijo)) {
                is Resource.Success -> getHijosByPadre(hijo.padreId)
                is Resource.Error -> _uiState.update { it.copy(errorMessage = result.message) }
                is Resource.Loading -> _uiState.update { it.copy(isLoading = true) }
            }
        }
    }

    fun completarTarea(tareaId: Int) {
        viewModelScope.launch {
            val hijoId = _uiState.value.hijoId
            if (hijoId == 0 || hijoId == null) {
                _uiState.update { it.copy(errorMessage = "Error: No se encontró el ID del hijo") }
                return@launch
            }

            _uiState.update {
                it.copy(
                    isLoading = true,
                    ultimaAccionProcesada = tareaId,
                    errorMessage = null,
                    successMessage = null
                )
            }

            try {
                val countPending = tareaHijoRepository.countPendingTasks(
                    tareaId = tareaId,
                    hijoId = hijoId,
                    estado = EstadoTareaHijo.PENDIENTE_VERIFICACION
                )

                if (countPending > 0) {
                    throw Exception("Ya tienes esta tarea pendiente de verificación")
                }
                val nuevaTareaHijo = TareaHijo(
                    tareaId = tareaId,
                    hijoId = hijoId,
                    estado = EstadoTareaHijo.PENDIENTE_VERIFICACION
                )

                tareaHijoRepository.save(nuevaTareaHijo).collect { saveResult ->
                    when (saveResult) {
                        is Resource.Success -> {
                            _uiState.update {
                                it.copy(
                                    isLoading = false,
                                    successMessage = "Tarea enviada para revisión",
                                    ultimaAccionProcesada = null
                                )
                            }
                            loadTareas()
                        }

                        is Resource.Error -> {
                            Log.e("HijoViewModel", "Error al guardar: ${saveResult.message}")
                            _uiState.update {
                                it.copy(
                                    isLoading = false,
                                    errorMessage = saveResult.message ?: "Error al guardar tarea",
                                    ultimaAccionProcesada = null
                                )
                            }
                        }

                        is Resource.Loading -> {}
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = e.message ?: "Error al completar tarea",
                        ultimaAccionProcesada = null
                    )
                }
            }
        }
    }

    fun reclamarRecompensa(recompensaId: Int) {
        viewModelScope.launch {
            val hijoId = _uiState.value.hijoId
            Log.d("reclamarRecompensa", "Inicio - hijoId: $hijoId")
            if (hijoId == 0) {
                _uiState.update { it.copy(errorMessage = "Error: No se encontró el ID del hijo") }
                Log.e("reclamarRecompensa", "No se encontró el ID del hijo")
                return@launch
            }

            _uiState.update {
                it.copy(
                    isLoading = true,
                    errorMessage = null,
                    successMessage = null
                )
            }

            try {
                val recompensa = recompensaRepository.find(recompensaId)
                    ?: throw Exception("Recompensa no encontrada")
                Log.d("reclamarRecompensa", "Recompensa encontrada: $recompensa")

                val hijo = hijoId?.let { hijoRepository.find(it) }
                    ?: throw Exception("Hijo no encontrado")
                Log.d("reclamarRecompensa", "Hijo encontrado: $hijo, saldoActual: ${hijo.saldoActual}")

                if (hijo.saldoActual < recompensa.puntosNecesarios) {
                    throw Exception("No tienes suficientes puntos para reclamar esta recompensa")
                }

                val nuevoCanjeo = CanjeoEntity(
                    canjeoId = 0,
                    hijoId = hijoId,
                    recompensaId = recompensaId,
                    fecha = Date(),
                    estado = EstadoRecompensa.PENDIENTE
                )
                Log.d("reclamarRecompensa", "Nuevo canjeo creado: $nuevoCanjeo")

                canjeoRepository.save(nuevoCanjeo).collect { resource ->
                    when (resource) {
                        is Resource.Loading -> {
                            Log.d("reclamarRecompensa", "Guardando canjeo: Loading")
                        }
                        is Resource.Success -> {
                            Log.d("reclamarRecompensa", "Canjeo guardado con éxito")
                            val nuevoSaldo = hijo.saldoActual - recompensa.puntosNecesarios
                            val hijoActualizado = hijo.copy(saldoActual = nuevoSaldo)
                            hijoRepository.save(hijoActualizado).collect { saveResult ->
                                when (saveResult) {
                                    is Resource.Success -> {
                                        _uiState.update {
                                            it.copy(
                                                isLoading = false,
                                                saldoActual = nuevoSaldo,
                                                successMessage = "Recompensa reclamada, esperando verificación del padre"
                                            )
                                        }
                                        Log.d("reclamarRecompensa", "Recompensa reclamada con éxito")
                                        getTransacciones()
                                    }
                                    is Resource.Error -> throw Exception("Error al actualizar saldo: ${saveResult.message}")
                                    is Resource.Loading -> {}
                                }
                            }
                        }
                        is Resource.Error -> {
                            throw Exception("Error al guardar el canjeo: ${resource.message}")
                        }
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = e.message ?: "Error al reclamar recompensa"
                    )
                }
                Log.e("reclamarRecompensa", "Error: ${e.message}")
            }
        }
    }

    private fun loadTareas() {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true) }
                combine(
                    tareaRepository.getActiveTasks(),
                    tareaHijoRepository.getAll()
                ) { tareasActivasResource, tareasCompletadasResource ->
                    when {
                        tareasActivasResource is Resource.Success && tareasCompletadasResource is Resource.Success<*> -> {
                            val tareasActivas = tareasActivasResource.data ?: emptyList()
                            val tareasCompletadas =
                                (tareasCompletadasResource.data as? List<TareaHijo>)
                                    ?.filter { it.hijoId == _uiState.value.hijoId } ?: emptyList()

                            val tareasDisponibles = tareasActivas.filter { tarea ->
                                !tareasCompletadas.any {
                                    it.tareaId == tarea.tareaId && it.estado == EstadoTareaHijo.APROBADA
                                }
                            }

                            _uiState.update {
                                it.copy(
                                    listaTareas = tareasDisponibles,
                                    listaTareasFiltradas = tareasDisponibles,
                                    isLoading = false,
                                    errorMessage = null
                                )
                            }
                        }

                        tareasActivasResource is Resource.Error<*> || tareasCompletadasResource is Resource.Error<*> -> {
                            val mensajeError = buildString {
                                if (tareasActivasResource is Resource.Error<*>)
                                    append("Tareas activas: ${tareasActivasResource.message} ")
                                if (tareasCompletadasResource is Resource.Error<*>)
                                    append("Tareas completadas: ${tareasCompletadasResource.message}")
                            }.ifBlank { "Error desconocido al cargar tareas" }

                            _uiState.update {
                                it.copy(
                                    errorMessage = mensajeError,
                                    isLoading = false
                                )
                            }
                        }

                        else -> {
                            _uiState.update { it.copy(isLoading = true) }
                        }
                    }
                }.collect {}
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        errorMessage = "Error cargando tareas: ${e.message}",
                        isLoading = false
                    )
                }
            }
        }
    }

    fun filtrarTareas(periodicidad: String) {
        viewModelScope.launch {
            val todasTareas = _uiState.value.listaTareas
            val filtradas = if (periodicidad == "Todas") {
                todasTareas
            } else {
                todasTareas.filter {
                    it.periodicidad?.nombreMostrable() == periodicidad
                }
            }

            _uiState.update {
                it.copy(listaTareasFiltradas = filtradas)
            }
        }
    }

    private fun actualizarPeriodicidades() {
        viewModelScope.launch {
            val periodicidades =
                listOf("Todas") + PeriodicidadTarea.entries.map { it.nombreMostrable() }
            _periodicidadesDisponibles.value = periodicidades
        }
    }

    fun clearMessages() {
        _uiState.update {
            it.copy(
                errorMessage = null,
                successMessage = null
            )
        }
    }

    fun getTransacciones() {
        viewModelScope.launch {
            val hijoId = _uiState.value.hijoId ?: return@launch
            Log.d("HijoViewModel", "Cargando transacciones para hijoId: $hijoId")
            transaccionHijoRepository.getAll().collect { result ->
                when (result) {
                    is Resource.Success -> {
                        val filtered = result.data?.filter { it.hijoId == hijoId } ?: emptyList()
                        Log.d("HijoViewModel", "Transacciones obtenidas: $filtered")
                        _transacciones.value = filtered
                    }

                    is Resource.Error -> Log.e(
                        "HijoViewModel",
                        "Error al cargar transacciones: ${result.message}"
                    )

                    is Resource.Loading -> Log.d("HijoViewModel", "Cargando transacciones...")
                }
            }
        }
    }
}