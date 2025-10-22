package com.example.cattletrackingapp.ui.screens.vaccinations

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Vaccines
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.ui.draw.rotate


@Composable
fun VaccinationsScreen(
    navController: NavController,
    vm: VaccinationsViewModel = hiltViewModel()
) {
    val state = vm.uiState

    // one-screen wizard state
    var step by remember { mutableStateOf(Step.List) }
    val selected = remember { mutableStateListOf<String>() }   // vaccine ids
    var target by remember { mutableStateOf(TargetGroup.Cows) }
    // dialog + save state
    var showAddDialog by remember { mutableStateOf(false) }
    var newVaccineName by remember { mutableStateOf("") }
    var newVaccineDescription by remember { mutableStateOf("") }
    var newVaccineNotes by remember { mutableStateOf("") }
    var saving by remember { mutableStateOf(false) }
    var saveError by remember { mutableStateOf<String?>(null) }
    val expandedIds = remember { mutableStateListOf<String>() } // which vaccine cards are expanded (Step.List only)



    Column(
        Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Title
        Text(
            text = if (step == Step.List) "Vaccinations" else "Select Vaccines",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        when (step) {
            Step.List -> {
                // Read-only names list + Start button
                // Row with the Add button ABOVE the list

                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    OutlinedButton(onClick = { showAddDialog = true }) {
                        Text("Add Vaccine")
                    }
                }

                when {
                    state.loading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                    state.error != null -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(state.error!!)
                    }
                    state.vaccines.isEmpty() -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No vaccines found")
                    }
                    else -> {
                        LazyColumn(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(state.vaccines, key = { it.id }) { v ->
                                val id = v.id.ifEmpty { v.name }          // safe fallback key
                                val isExpanded = id in expandedIds
                                val rotation by animateFloatAsState(targetValue = if (isExpanded) 180f else 0f, label = "expand")

                                ElevatedCard(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            if (isExpanded) expandedIds.remove(id) else expandedIds.add(id)
                                        }
                                ) {
                                    Column(Modifier.padding(16.dp)) {

                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(Icons.Filled.Vaccines, contentDescription = null, modifier = Modifier.size(20.dp))
                                            Spacer(Modifier.width(8.dp))

                                            // Name takes remaining space
                                            Text(
                                                v.name,
                                                style = MaterialTheme.typography.titleMedium,
                                                fontWeight = FontWeight.SemiBold,
                                                modifier = Modifier.weight(1f)   // <-- ensures badge + arrow fit on the right
                                            )

                                            // Option C: tiny colored badge for the vaccineId
                                            v.vaccineId?.let {
                                                Surface(
                                                    color = MaterialTheme.colorScheme.secondaryContainer,
                                                    shape = MaterialTheme.shapes.small
                                                ) {
                                                    Text(
                                                        "ID $it",
                                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                                        style = MaterialTheme.typography.labelSmall
                                                    )
                                                }
                                                Spacer(Modifier.width(8.dp))
                                            }

                                            // Keep the dropdown/expand arrow
                                            Icon(
                                                imageVector = Icons.Filled.ExpandMore,
                                                contentDescription = null,
                                                modifier = Modifier.rotate(rotation)
                                            )
                                        }


                                        AnimatedVisibility(visible = isExpanded) {
                                            Column(Modifier.padding(top = 12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {

                                                val hasDesc = !v.description.isNullOrBlank()
                                                val hasNotes = !v.notes.isNullOrBlank()

                                                if (hasDesc) {
                                                    Text("Description", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
                                                    Text(v.description!!)   // safe because hasDesc checked above
                                                }
                                                if (hasNotes) {
                                                    Text("Notes", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
                                                    Text(v.notes!!)         // safe because hasNotes checked above
                                                }
                                                if (!hasDesc && !hasNotes) {
                                                    Text("No extra information.")
                                                }
                                            }
                                        }

                                    }
                                }
                            }

                        }
                    }
                }

                Button(
                    onClick = { step = Step.Select },
                    modifier = Modifier.fillMaxWidth()
                ) { Text("Start") }
            }

            Step.Select -> {
                // Target group toggle (Cows/Bulls/Calves)
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TargetGroup.values().forEach { tg ->
                        val isSel = target == tg
                        OutlinedButton(
                            onClick = { target = tg },
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = if (isSel)
                                    MaterialTheme.colorScheme.secondaryContainer
                                else
                                    MaterialTheme.colorScheme.surface
                            )
                        ) { Text(tg.label) }
                    }
                }

                HorizontalDivider(Modifier, DividerDefaults.Thickness, DividerDefaults.color)

                // Checkbox list of vaccines
                when {
                    state.loading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                    state.error != null -> Text(state.error!!)
                    else -> {
                        LazyColumn(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(state.vaccines, key = { it.id }) { v ->
                                val id = v.id.ifEmpty { v.name }          // safe fallback key
                                val isChecked = id in selected            // checkbox state for this row

                                ListItem(
                                    // left icon
                                    leadingContent = {
                                        Icon(Icons.Filled.Vaccines, contentDescription = null, modifier = Modifier.size(20.dp))
                                    },
                                    // title (name only — no ID under the name)
                                    headlineContent = { Text(v.name) },

                                    // RIGHT SIDE: ID badge + checkbox
                                    trailingContent = {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            v.vaccineId?.let {
                                                Surface(
                                                    color = MaterialTheme.colorScheme.secondaryContainer,
                                                    shape = MaterialTheme.shapes.small
                                                ) {
                                                    Text(
                                                        "ID $it",
                                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                                        style = MaterialTheme.typography.labelSmall
                                                    )
                                                }
                                                Spacer(Modifier.width(8.dp))
                                            }
                                            Checkbox(
                                                checked = isChecked,
                                                onCheckedChange = { now ->
                                                    if (now) {
                                                        if (id !in selected) selected.add(id)
                                                    } else {
                                                        selected.remove(id)
                                                    }
                                                }
                                            )
                                        }
                                    }
                                    // NOTE: no supportingContent here, so nothing shows under the name
                                )

                                HorizontalDivider(
                                    Modifier,
                                    DividerDefaults.Thickness,
                                    DividerDefaults.color
                                )
                            }

                        }
                    }
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedButton(
                        onClick = { step = Step.List },
                        modifier = Modifier.weight(1f)
                    ) { Text("Back") }

                    Button(
                        onClick = {
                        },
                        enabled = selected.isNotEmpty(),
                        modifier = Modifier.weight(1f)
                    ) { Text("Next") }
                }
            }
        }
    }
    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = {
                if (!saving) { showAddDialog = false; newVaccineName = ""; saveError = null }
            },
            title = { Text("Add Vaccine") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = newVaccineName,
                        onValueChange = { newVaccineName = it },
                        label = { Text("Name *") },
                        singleLine = true,
                        supportingText = {
                            if (saveError != null) Text(saveError!!, color = MaterialTheme.colorScheme.error)
                        }
                    )
                    OutlinedTextField(
                        value = newVaccineDescription,
                        onValueChange = { newVaccineDescription = it },
                        label = { Text("Description") },
                        singleLine = false,
                        minLines = 2,
                        maxLines = 5
                    )
                    OutlinedTextField(
                        value = newVaccineNotes,
                        onValueChange = { newVaccineNotes = it },
                        label = { Text("Notes") },
                        singleLine = false,
                        minLines = 2,
                        maxLines = 5
                    )
                }
            },
            confirmButton = {
                TextButton(
                    enabled = !saving,
                    onClick = {
                        if (newVaccineName.isBlank()) {
                            saveError = "Name is required"
                            return@TextButton
                        }
                        saving = true
                        saveError = null
                        vm.addVaccine(
                            name = newVaccineName.trim(),
                            description = newVaccineDescription.trim().ifEmpty { null },
                            notes = newVaccineNotes.trim().ifEmpty { null },
                            onDone = { ok, err ->
                                saving = false
                                if (ok) {
                                    // clear all fields on success
                                    newVaccineName = ""
                                    newVaccineDescription = ""
                                    newVaccineNotes = ""
                                    showAddDialog = false
                                } else {
                                    saveError = err ?: "Failed to save"
                                }
                            }
                        )
                    }
                ) { Text(if (saving) "Saving..." else "Save") }
            },
            dismissButton = {
                TextButton(
                    enabled = !saving,
                    onClick = { showAddDialog = false; newVaccineName = ""; saveError = null }
                ) { Text("Cancel") }
            }
        )
    }

}

private enum class Step { List, Select }
enum class TargetGroup(val label: String) { Cows("Cows"), Bulls("Bulls"), Calves("Calves") }
