package edu.ucne.doers.presentation.recompensa.components

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import edu.ucne.doers.R
import edu.ucne.doers.data.local.entity.RecompensaEntity
import edu.ucne.doers.data.local.model.CondicionRecompensa
import edu.ucne.doers.data.local.model.EstadoRecompensa
import java.io.File

@Composable
fun RecompensaCard(
    recompensa: RecompensaEntity,
    onEdit: (Int) -> Unit,
    onDelete: (RecompensaEntity) -> Unit,
    onCondicionChange: (CondicionRecompensa) -> Unit
) {
    var isExpanded by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    val isSwitchChecked = recompensa.estado == EstadoRecompensa.DISPONIBLE
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    isExpanded = !isExpanded
                    Log.d("RecompensaCard", "isExpanded: $isExpanded")
                },
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Row(modifier = Modifier.height(IntrinsicSize.Min)) {
                if (recompensa.imagenURL.isNotBlank()) {
                    Image(
                        painter = rememberAsyncImagePainter(File(recompensa.imagenURL)),
                        contentDescription = "Imagen recompensa",
                        modifier = Modifier
                            .width(100.dp)
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(topStart = 12.dp, bottomStart = 12.dp)),
                        contentScale = ContentScale.Crop
                    )
                }

                Column(
                    modifier = Modifier
                        .padding(12.dp)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = recompensa.descripcion,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.weight(1f)
                        )
                        Switch(
                            checked = isSwitchChecked,
                            onCheckedChange = { isChecked ->
                                val nuevoEstado = if (isChecked) CondicionRecompensa.ACTIVA else CondicionRecompensa.INACTIVA
                                onCondicionChange(nuevoEstado)
                                Log.d("RecompensaCard", "Switch checked: $isChecked, nuevoEstado: $nuevoEstado")
                                Toast.makeText(
                                    context,
                                    if (isChecked) "Recompensa activada" else "Recompensa desactivada",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        )
                    }

                    Spacer(Modifier.height(4.dp))

                    Text(
                        text = "Puntos necesarios: ${recompensa.puntosNecesarios}",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    if (isExpanded) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            OutlinedButton(onClick = { onEdit(recompensa.recompensaId) }) {
                                Icon(Icons.Default.Edit, contentDescription = "Editar")
                                Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
                                Text("Editar")
                            }
                            OutlinedButton(onClick = { showDeleteDialog = true }) {
                                Icon(Icons.Default.Delete, contentDescription = "Eliminar")
                                Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
                                Text("Eliminar")
                            }
                        }
                    }
                }
            }
        }

        if (showDeleteDialog) {
            AlertDialog(
                onDismissRequest = { showDeleteDialog = false },
                title = {
                    Text(
                        text = "Confirmar eliminación",
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                },
                text = {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "¿Estás seguro que deseas eliminar esta recompensa?",
                            fontWeight = FontWeight.SemiBold,
                            textAlign = TextAlign.Center,
                            fontSize = 20.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Icon(
                            painter = painterResource(id = R.drawable.pensado_negro),
                            contentDescription = "Pensando",
                            modifier = Modifier.size(70.dp),
                            tint = Color(0xFF1976D2)
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            onDelete(recompensa)
                            showDeleteDialog = false
                            Log.d("RecompensaCard", "Recompensa eliminada: ${recompensa.recompensaId}")
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.trash),
                            contentDescription = "Eliminar"
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Eliminar")
                    }
                }
            )
        }
    }
}
