package com.example.cattletrackingapp.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.cattletrackingapp.ui.navigation.Screen


enum class CattleType(
    val displayName: String,
    val singularName: String,
    val addRoute: String,
) {
    ALL("All Cattle", "", Screen.ChooseAddCattle.route),
    COW("Cows", "Cow", Screen.AddCow.route),
    CALF("Calves", "Calf", Screen.AddCalf.route),
    BULL("Bulls", "Bull", Screen.AddBull.route)
}


@Composable
fun CattleTypeTabs (
    selectedType: CattleType,
    onTypeSelected: (CattleType) -> Unit
) {
    TabRow(
        selectedTabIndex = selectedType.ordinal,
        containerColor = MaterialTheme.colorScheme.surfaceVariant,
        contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
        ) {
        CattleType.values().forEachIndexed { index, type ->
            Tab(
                selected = selectedType.ordinal == index,
                onClick = { onTypeSelected(type) },
                text = { Text(
                    type.displayName,
                    style = if (selectedType.ordinal == index)
                        MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                    else
                        MaterialTheme.typography.labelLarge
                ) }

            )
        }
    }
}


// For the Dashboard and List pages on the Weights Module ~ Braden
enum class DashboardList(
    val displayName: String,
    val singularName: String,
    val addRoute: String) {
    DASHBOARD("DashBoard", "", Screen.DashBoard.route),
    LIST("List", "", Screen.WeightList.route)
}


@Composable
fun WeightModuleTabs (
    selectedType: DashboardList,
    onTypeSelected: (DashboardList) -> Unit
) {
    TabRow(
        selectedTabIndex = selectedType.ordinal,
        containerColor = MaterialTheme.colorScheme.surfaceVariant,
        contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        DashboardList.values().forEachIndexed { index, type ->
            Tab(
                selected = selectedType.ordinal == index,
                onClick = { onTypeSelected(type) },
                text = { Text(
                    type.displayName,
                    style = if (selectedType.ordinal == index)
                        MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                    else
                        MaterialTheme.typography.labelLarge
                ) }

            )
        }
    }
}


//end of Tab row for list pages (Created by Nick Heislen

//This is the List Header (Title and Add button)
@Composable
fun ListHeader(
    title: String,
    onAddClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ){
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.weight(1f)
        )
        FilledTonalButton(
            onClick = onAddClick
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add")
            Spacer(Modifier.width(6.dp))
            Text("Add")

        }
    }
}

//end of List Header (Nick Heislen 10/15/2025)

//This will be the component for the Cattle list itself
//Takes a list of items and allows them to be clicked
@Composable
fun <T> CattleList(
    items: List<T>,
    onClick: (T) -> Unit,
    itemContent: @Composable (T, () -> Unit) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(items) { item ->
            itemContent(item) {
                onClick(item)
            }
        }
    }
}