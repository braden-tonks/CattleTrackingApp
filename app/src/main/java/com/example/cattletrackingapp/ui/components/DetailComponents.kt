package com.example.cattletrackingapp.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.cattletrackingapp.R
import com.example.cattletrackingapp.data.remote.Models.Calf
import com.example.cattletrackingapp.data.remote.Models.CowVaccine
import com.example.cattletrackingapp.data.remote.Models.CowVaccineWithName
import com.example.cattletrackingapp.data.remote.Models.Weight
import com.example.cattletrackingapp.ui.navigation.Screen
import com.example.cattletrackingapp.ui.theme.detailHeaderLarge
import com.example.cattletrackingapp.ui.theme.detailHeaderSmall

@Composable
fun DetailHeader(
    iconPainter: Painter,
    tagNumber: String,
    type: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp, 32.dp, 32.dp, 10.dp),
    ) {
        Image(
            painter = iconPainter,
            contentDescription = type,
            modifier = Modifier.size(120.dp)
        )

        Spacer(Modifier.width(35.dp))

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = tagNumber,
                style = MaterialTheme.typography.detailHeaderLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(16.dp))
            Text(
                text = type,
                style = MaterialTheme.typography.detailHeaderSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}


@Composable
fun DetailTabRow(
    tabs: List<String>,
    selectedTabIndex: Int,
    onTabSelected: (Int) -> Unit
) {
    TabRow(
        selectedTabIndex = selectedTabIndex,
        containerColor = MaterialTheme.colorScheme.surfaceVariant,
        contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        tabs.forEachIndexed { index, title ->
            Tab(
                selected = selectedTabIndex == index,
                onClick = { onTabSelected(index) },
                text = {
                    Text(
                        text = title,
                        style = if (selectedTabIndex == index)
                            MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                        else
                            MaterialTheme.typography.labelLarge
                    )
                }

            )
        }
    }

}

@Composable
fun InfoRow(label: String, value: String) {
    Column(
        modifier = Modifier
            .padding(vertical = 10.dp)
            .fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Bold
                )
            }
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium
            )
        }

        HorizontalDivider(
            modifier = Modifier.padding(top = 15.dp),
            thickness = DividerDefaults.Thickness,
            color = DividerDefaults.color
        )
    }
}


@Composable
fun CalfListSection(calves: List<Calf>, onClick: (Calf) -> Unit) {
    CattleList(
        items = calves,
        onClick = onClick
    ) { calf, onClickItem ->
        CattleCard(
            title = "Tag: ${calf.tag_number}",
            subtitle = "DOB: ${calf.birth_date}",
            iconPainter = painterResource(R.drawable.cow_icon),
            onClick = onClickItem
        )
    }
}


// Used for CowDetail, BullDetail, and CalfDetail pages to present a list of vaccines given to them
@Composable
fun VaccineListSection(cvwn: List<CowVaccineWithName>, navController: NavController) {
    CattleList(
        items = cvwn,
        onClick = { navController.navigate(Screen.Vaccinations.route) }
    ) { cvwnItem, onClickItem ->
        CattleCard(
            title = cvwnItem.vaccine_id.name,
            subtitle = "Date Given: ${cvwnItem.date_given}",
            iconPainter = painterResource(R.drawable.svg_vaccine_icon),
            onClick = onClickItem
        )
    }
}


@Composable
fun WeightListSection(weights: List<Weight>, navController: NavController) {
    println("SyncDebug: WeightListSection called: $weights")
    CattleList(
        items = weights,
        onClick = { navController.navigate(Screen.WeightList.route) }
    ) { weight, onClickItem ->
        CattleCard(
            title = "Weight: ${weight.weight}",
            subtitle = "Date Weighed: ${weight.date_weighed}",
            //iconPainter = painterResource(R.drawable.svg_weight_icon),
            onClick = onClickItem
        )

    }
}