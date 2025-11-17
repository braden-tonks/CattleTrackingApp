package com.example.cattletrackingapp.ui.screens.Vaccinations

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Vaccines
import androidx.compose.material3.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.compose.ui.zIndex

private enum class Step { List, Select, Assign }

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun VaccinationsScreen(
    navController: NavController,
    vm: VaccinationsViewModel = hiltViewModel()
) {
    val state = vm.uiState

    // Wizard state
    var step by remember { mutableStateOf(Step.List) }
    val selectedVaccineIds = remember { mutableStateListOf<String>() }
    var target by remember { mutableStateOf(TargetGroup.Cows) }

    // Add dialog state
    var showAddDialog by remember { mutableStateOf(false) }
    var newVaccineName by remember { mutableStateOf("") }
    var newVaccineDescription by remember { mutableStateOf("") }
    var newVaccineNotes by remember { mutableStateOf("") }
    var saving by remember { mutableStateOf(false) }
    var saveError by remember { mutableStateOf<String?>(null) }

    // Expanded cards (Step.List)
    val expandedIds = remember { mutableStateListOf<String>() }

    // Snackbar (single host, overlaid above content)
    val snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(vm.toastMessage) {
        vm.toastMessage?.let {
            snackbarHostState.showSnackbar(it)
            vm.clearToast()
        }
    }

    // Picked animals for Assign step (local – not in VM)
    val pickedAnimals = remember { mutableStateListOf<AnimalHit>() }
    // Clear any selected animals if group changes
    LaunchedEffect(target) { pickedAnimals.clear() }

    Box(Modifier.fillMaxSize()) {

        // -------- Main content --------
        Column(
            Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = when (step) {
                    Step.List -> "Vaccinations"
                    Step.Select -> "Select Vaccines"
                    Step.Assign -> "Assign to ${target.label}"
                },
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            when (step) {
                /* ---------------- STEP 1: Read-only list ---------------- */
                Step.List -> {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                        OutlinedButton(onClick = { showAddDialog = true }) { Text("Add Vaccine") }
                    }

                    when {
                        state.loading -> BoxFullCenter { CircularProgressIndicator() }
                        state.error != null -> BoxFullCenter { Text(state.error) }
                        state.vaccines.isEmpty() -> BoxFullCenter { Text("No vaccines found") }
                        else -> {
                            LazyColumn(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                items(state.vaccines, key = { it.id }) { v ->
                                    val key = v.id.ifEmpty { v.name }
                                    val isExpanded = key in expandedIds
                                    val rotation by animateFloatAsState(
                                        targetValue = if (isExpanded) 180f else 0f,
                                        label = "expand"
                                    )

                                    ElevatedCard(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable {
                                                if (isExpanded) expandedIds.remove(key) else expandedIds.add(key)
                                            }
                                    ) {
                                        Column(Modifier.padding(16.dp)) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(Icons.Filled.Vaccines, contentDescription = null)
                                                Spacer(Modifier.width(8.dp))
                                                Text(
                                                    v.name,
                                                    style = MaterialTheme.typography.titleMedium,
                                                    fontWeight = FontWeight.SemiBold,
                                                    modifier = Modifier.weight(1f)
                                                )
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
                                                Icon(
                                                    imageVector = Icons.Filled.ExpandMore,
                                                    contentDescription = null,
                                                    modifier = Modifier.rotate(rotation)
                                                )
                                            }

                                            AnimatedVisibility(visible = isExpanded) {
                                                Column(
                                                    Modifier.padding(top = 12.dp),
                                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                                ) {
                                                    val hasDesc = !v.description.isNullOrBlank()
                                                    val hasNotes = !v.notes.isNullOrBlank()
                                                    if (hasDesc) {
                                                        Text(
                                                            "Description",
                                                            style = MaterialTheme.typography.labelLarge,
                                                            color = MaterialTheme.colorScheme.primary
                                                        )
                                                        Text(v.description)
                                                    }
                                                    if (hasNotes) {
                                                        Text(
                                                            "Notes",
                                                            style = MaterialTheme.typography.labelLarge,
                                                            color = MaterialTheme.colorScheme.primary
                                                        )
                                                        Text(v.notes)
                                                    }
                                                    if (!hasDesc && !hasNotes) Text("No extra information.")
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

                /* ---------------- STEP 2: Select vaccines + choose group ---------------- */
                Step.Select -> {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TargetGroup.entries.forEach { tg ->
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

                    when {
                        state.loading -> BoxFullCenter { CircularProgressIndicator() }
                        state.error != null -> Text(state.error)
                        else -> {
                            LazyColumn(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                items(state.vaccines, key = { it.id }) { v ->
                                    val key = v.id.ifEmpty { v.name }
                                    val isChecked = key in selectedVaccineIds

                                    ListItem(
                                        leadingContent = { Icon(Icons.Filled.Vaccines, contentDescription = null) },
                                        headlineContent = { Text(v.name) },
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
                                                            if (key !in selectedVaccineIds) selectedVaccineIds.add(key)
                                                        } else {
                                                            selectedVaccineIds.remove(key)
                                                        }
                                                    }
                                                )
                                            }
                                        }
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
                            onClick = { step = Step.Assign },
                            enabled = selectedVaccineIds.isNotEmpty(),
                            modifier = Modifier.weight(1f)
                        ) { Text("Next") }
                    }
                }

                /* ---------------- STEP 3: Assign to animals of the chosen group ---------------- */
                Step.Assign -> {
                    // Load the list for the chosen group once
                    LaunchedEffect(target) {
                        vm.ensureAnimalsLoaded(target)
                    }

                    var filter by remember { mutableStateOf("") }
                    val all: List<AnimalHit> = vm.animalsByGroup[target].orEmpty()

                    val visible: List<AnimalHit> = remember(all, filter) {
                        val q = filter.trim()
                        if (q.isEmpty()) all else all.filter { it.tag.contains(q, ignoreCase = true) }
                    }

                    OutlinedTextField(
                        value = filter,
                        onValueChange = { filter = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Filter by tag number…") },
                        singleLine = true
                    )

                    Spacer(Modifier.height(12.dp))

                    if (visible.isEmpty()) {
                        Text(if (all.isEmpty()) "No ${target.label.lowercase()} found." else "No matches for \"$filter\".")
                    } else {
                        LazyColumn(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(visible, key = { it.id }) { hit ->
                                val isPicked = pickedAnimals.any { it.id == hit.id }

                                ListItem(
                                    headlineContent = { Text("Tag ${hit.tag}") },
                                    supportingContent = {
                                        val label = when (target) {
                                            TargetGroup.Cows  -> "Cow"
                                            TargetGroup.Bulls -> "Bull"
                                            TargetGroup.Calves -> buildString {
                                                append("Calf")
                                                hit.gender?.takeIf { it.isNotBlank() }?.let { g ->
                                                    append("-")
                                                    append(g.replaceFirstChar { it.uppercase() }) // Calf-Male / Calf-Female
                                                }
                                            }
                                        }
                                        Text(label)
                                    },
                                    trailingContent = {
                                        Checkbox(
                                            checked = isPicked,
                                            onCheckedChange = { now ->
                                                if (now) {
                                                    if (!isPicked) pickedAnimals.add(hit)
                                                } else {
                                                    pickedAnimals.removeAll { it.id == hit.id }
                                                }
                                            }
                                        )
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            if (!isPicked) pickedAnimals.add(hit)
                                            else pickedAnimals.removeAll { it.id == hit.id }
                                        }
                                )

                                HorizontalDivider(
                                    Modifier,
                                    DividerDefaults.Thickness,
                                    DividerDefaults.color
                                )
                            }
                        }
                    }

                    if (pickedAnimals.isNotEmpty()) {
                        Spacer(Modifier.height(8.dp))
                        Text("Selected ${target.label.lowercase()}:")
                        Spacer(Modifier.height(6.dp))
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            pickedAnimals.forEach { animal ->
                                AssistChip(
                                    onClick = { /* no-op */ },
                                    label = { Text("Tag ${animal.tag}") },
                                    trailingIcon = {
                                        Text(
                                            "✕",
                                            modifier = Modifier
                                                .clickable { pickedAnimals.remove(animal) }
                                                .padding(horizontal = 4.dp)
                                        )
                                    }
                                )
                            }
                        }
                    }

                    Spacer(Modifier.weight(1f))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedButton(
                            onClick = { step = Step.Select },
                            modifier = Modifier.weight(1f)
                        ) { Text("Back") }

                        var finishing by remember { mutableStateOf(false) }

                        Button(
                            onClick = {
                                finishing = true
                                vm.logVaccinations(
                                    vaccines = selectedVaccineIds,
                                    group = target,
                                    animalIds = pickedAnimals.map { it.id },
                                    dateGiven = java.time.LocalDate.now().toString(),
                                    remarks = null
                                ) { ok ->
                                    finishing = false
                                    if (ok) {
                                        // reset wizard + selections
                                        selectedVaccineIds.clear()
                                        pickedAnimals.clear()
                                        expandedIds.clear()
                                        step = Step.List
                                        vm.refresh() // reload the list
                                    }
                                }
                            },
                            enabled = pickedAnimals.isNotEmpty() && !finishing,
                            modifier = Modifier.weight(1f)
                        ) { Text(if (finishing) "Saving..." else "Finish") }
                    }
                }
            }
        }

        // -------- Snackbar overlay (always on top) --------
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
                .zIndex(1f),
            snackbar = { data ->
                Snackbar(
                    snackbarData = data,
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = Color.White,
                    actionColor = Color.White,
                    dismissActionContentColor = Color.White
                )
            }
        )
    }

    // Add Vaccine dialog
    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = {
                if (!saving) {
                    showAddDialog = false
                    newVaccineName = ""; newVaccineDescription = ""; newVaccineNotes = ""
                    saveError = null
                }
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
                            saveError?.let { Text(it, color = MaterialTheme.colorScheme.error) }
                        }
                    )
                    OutlinedTextField(
                        value = newVaccineDescription,
                        onValueChange = { newVaccineDescription = it },
                        label = { Text("Description") },
                        minLines = 2,
                        maxLines = 5
                    )
                    OutlinedTextField(
                        value = newVaccineNotes,
                        onValueChange = { newVaccineNotes = it },
                        label = { Text("Notes") },
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
                            saveError = "Name is required"; return@TextButton
                        }
                        saving = true; saveError = null
                        vm.addVaccine(
                            name = newVaccineName.trim(),
                            description = newVaccineDescription.trim().ifEmpty { null },
                            notes = newVaccineNotes.trim().ifEmpty { null },
                            onDone = { ok, err ->
                                saving = false
                                if (ok) {
                                    newVaccineName = ""; newVaccineDescription = ""; newVaccineNotes = ""
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
                    onClick = {
                        showAddDialog = false
                        newVaccineName = ""; newVaccineDescription = ""; newVaccineNotes = ""
                        saveError = null
                    }
                ) { Text("Cancel") }
            }
        )
    }
}

/* Helpers */
@Composable
private fun BoxFullCenter(content: @Composable () -> Unit) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { content() }
}
