package com.example.cattletrackingapp.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.painterResource


//This card will be used as the detail card for detail pages
//See example usage on CalfDetailScreen
@Composable
fun InfoCards (
    title: String,
    fields: List<Pair<String, String?>>,
    modifier: Modifier = Modifier
){

    Column(
        modifier
            . fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        ElevatedCard(Modifier.fillMaxWidth()) {
            Column(
                Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                fields.forEach { (label, value) ->
                    value?.takeIf { it.isNotBlank() }?.let {
                        LabeledValue(label, it)
                    }
                }
            }
        }
    }
}

@Composable
fun LabeledValue(label: String, value: String) {
    Column {
        Text(
            label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.primary
        )
        Text(value, style = MaterialTheme.typography.bodyLarge)
    }
}

//End of Detail Cards (Created by Nick Heislen)


//This one is the a Cattle List card for the list of cows, calves, and bulls
//currently in the herd
@Composable
fun CattleCard(
    title: String,
    subtitle: String? = null,
    iconPainter: Painter? = null,
    onClick: () -> Unit
) {
    ElevatedCard(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (iconPainter != null) {
                Icon(
                    painter = iconPainter,
                    contentDescription = null,
                    modifier = Modifier
                        .size(60.dp)
                        .padding(end = 16.dp)
                )
            }

            Column(Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    overflow = TextOverflow.Ellipsis
                )
                if (subtitle != null) {
                    Text(subtitle, style = MaterialTheme.typography.bodyMedium)
                }
            }
            Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null)
        }
    }
}