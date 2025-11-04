package com.example.cattletrackingapp.ui.components.HomePageComponents

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Vaccines
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.cattletrackingapp.R


data class StatItem(
    val id: Int,
    val title: String,
    val value: String,
    val iconPainter: Painter? = null,
    val iconVector: ImageVector? = null
)

@Composable
fun DashboardScreen(
    viewModel: HomeDashboardViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    when {
        uiState.isLoading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
        uiState.error != null -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Error: ${uiState.error}",
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
        else -> {
            DashboardSection(
                totalCattle = uiState.totalCattle,
                totalCows = uiState.totalCows,
                totalCalves = uiState.totalCalves,
                totalBulls = uiState.totalBulls
            )
        }
    }
}


@Composable
fun DashboardSection(
    totalCattle: Int,
    totalCows: Int,
    totalCalves: Int,
    totalBulls: Int,
    lastVaccinationDate: String = "10/23/2025" // optional for now
) {
    val statItems = listOf(
        StatItem(
            id = 0,
            title = "Total Cattle",
            value = totalCattle.toString(),
            iconPainter = painterResource(R.drawable.cow_icon)
        ),
        StatItem(
            id = 1,
            title = "Cows",
            value = totalCows.toString(),
            iconPainter = painterResource(R.drawable.cow_icon)
        ),
        StatItem(
            id = 2,
            title = "Calves",
            value = totalCalves.toString(),
            iconPainter = painterResource(R.drawable.cow_icon)
        ),
        StatItem(
            id = 3,
            title = "Avg Daily Gain",
            value = "1 lbs",
            iconPainter = painterResource(R.drawable.dashboardicon)
        ),
        StatItem(
            id = 4,
            title = "Last Vaccination",
            value = lastVaccinationDate,
            iconVector = Icons.Default.Vaccines
        )
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Text(
            text = "Dashboard",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(start = 16.dp, bottom = 12.dp)
        )

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(horizontal = 16.dp)
        ) {
            items(statItems) { item ->
                StatCard(
                    title = item.title,
                    value = item.value,
                    iconPainter = item.iconPainter,
                    iconVector = item.iconVector,
                    modifier = Modifier
                        .width(160.dp)
                        .height(150.dp)
                )
            }
        }
    }
}




@Composable
fun StatCard(
    title: String,
    value: String,
    iconPainter: Painter? = null,
    iconVector: ImageVector? = null,
    modifier: Modifier = Modifier
) {
    ElevatedCard(
        modifier = modifier
            .height(120.dp)
            .clip(RoundedCornerShape(16.dp))
            .border(2.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(16.dp)),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            when {
                iconPainter != null -> Image(
                    painter = iconPainter,
                    contentDescription = null,
                    modifier = Modifier.size(50.dp)
                )

                iconVector != null -> Icon(
                    imageVector = iconVector,
                    contentDescription = null,
                    modifier = Modifier.size(50.dp)
                )
            }

            Text(
                text = value,
                style = MaterialTheme.typography.headlineSmall.copy(
                    color = MaterialTheme.colorScheme.onSurface
                )
            )

            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }
    }
}


//These are the buttons that take you to different pages/modules
@Composable
fun MenuItem(
    label: String,
    route: String,
    icon: ImageVector,
    navController: NavController
) {
    ElevatedButton(
        onClick = { navController.navigate(route) },
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.tertiary,
            contentColor = Color.White
        ),
        border = BorderStroke(2.dp, MaterialTheme.colorScheme.tertiary),
        contentPadding = PaddingValues(horizontal = 24.dp) // adds left/right padding

    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start // <— aligns content to left
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                modifier = Modifier.size(35.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.titleLarge
            )
        }
    }

}
