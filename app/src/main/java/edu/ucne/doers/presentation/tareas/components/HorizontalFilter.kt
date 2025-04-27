package edu.ucne.doers.presentation.tareas.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@Composable
fun HorizontalFilter(
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    val chipColors = listOf(
        Color(0xFFFF9999), // Rosa
        Color(0xFF99CCFF), // Azul
        Color(0xFD67D26A), // Verde
        Color(0xFFD4A5FF)  // Morado
    )

    Row(
        modifier = modifier
            .horizontalScroll(scrollState)
            .padding(horizontal = 8.dp, vertical = 8.dp)
            .clip(RoundedCornerShape(16.dp)),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        options.forEachIndexed { index, option ->
            FilterChip(
                selected = (option == selectedOption),
                onClick = { onOptionSelected(option) },
                label = {
                    Text(
                        text = option,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = if (option == selectedOption) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 16.sp,
                            color = if (option == selectedOption) Color.White else Color.Black
                        )
                    )
                },
                colors = FilterChipDefaults.filterChipColors(
                    containerColor = chipColors[index % chipColors.size],
                    selectedContainerColor = chipColors[index % chipColors.size].copy(alpha = 0.8f),
                    labelColor = Color.Black,
                    selectedLabelColor = Color.White
                ),
                modifier = Modifier
                    .padding(vertical = 6.dp)
                    .height(40.dp)
            )
        }
    }
}