package com.example.cattletrackingapp.ui.screens.Vaccinations

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Vaccines
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

private enum class Step { List, Select, Assign }

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun VaccinationsScreen(
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

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) } // no TopAppBar -> no off-color background
    ) { inner ->
        Box(Modifier.padding(inner)) {
            Column(
                Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                // ---------- Title (always visible) ----------
                Text(
                    text = "Vaccinations",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(8.dp))

                when (step) {
                    /* ---------------- STEP 1: Read-only list with Delete mode ---------------- */
                    Step.List -> {

                        // ---------- Buttons row ONLY on Step.List ----------
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Red Delete button
                            OutlinedButton(
                                onClick = { vm.toggleDeleteMode() },
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = MaterialTheme.colorScheme.error
                                ),
                                border = ButtonDefaults.outlinedButtonBorder.copy(
                                    brush = SolidColor(MaterialTheme.colorScheme.error)
                                ),
                                shape = MaterialTheme.shapes.extraLarge
                            ) { Text(if (vm.deleteMode) "Cancel" else "Delete") }

                            Spacer(Modifier.width(12.dp))

                            // Add Vaccine with green outline
                            OutlinedButton(
                                onClick = { showAddDialog = true },
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = MaterialTheme.colorScheme.primary
                                ),
                                border = ButtonDefaults.outlinedButtonBorder.copy(
                                    brush = SolidColor(MaterialTheme.colorScheme.primary)
                                ),
                                shape = MaterialTheme.shapes.extraLarge
                            ) { Text("Add Vaccine") }
                        }
                        // ---------------------------------------------------

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
                                                .then(
                                                    if (vm.deleteMode)
                                                        Modifier.border(
                                                            width = 1.dp,
                                                            color = MaterialTheme.colorScheme.error,
                                                            shape = MaterialTheme.shapes.extraLarge
                                                        )
                                                    else Modifier
                                                )
                                                .clickable {
                                                    if (vm.deleteMode) {
                                                        vm.chooseDelete(v.id)
                                                    } else {
                                                        if (isExpanded) expandedIds.remove(key) else expandedIds.add(key)
                                                    }
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

                    /* ---------------- STEP 3: Assign ---------------- */
                    Step.Assign -> {
                        LaunchedEffect(target) { vm.ensureAnimalsLoaded(target) }

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

                        val pickedAnimals = remember { mutableStateListOf<AnimalHit>() }
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
                                                        append(g.replaceFirstChar { c -> c.uppercase() })
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
                        }

                        Spacer(Modifier.height(8.dp))

                        var finishing by remember { mutableStateOf(false) }
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            OutlinedButton(
                                onClick = { step = Step.Select },
                                modifier = Modifier.weight(1f)
                            ) { Text("Back") }

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
                                            selectedVaccineIds.clear()
                                            pickedAnimals.clear()
                                            expandedIds.clear()
                                            step = Step.List
                                            vm.refresh()
                                        }
                                    }
                                },
                                enabled = !finishing,
                                modifier = Modifier.weight(1f)
                            ) { Text(if (finishing) "Saving..." else "Finish") }
                        }
                    }
                }
            }

            // Confirm delete dialog
            if (vm.deleteCandidateId != null) {
                AlertDialog(
                    onDismissRequest = { vm.clearDeleteCandidate() },
                    title = { Text("Delete vaccine") },
                    text = { Text("Are you sure you want to delete this vaccine from the catalog? This cannot be undone.") },
                    confirmButton = {
                        TextButton(
                            enabled = !vm.deleting,
                            onClick = { vm.confirmDeleteSelected() }
                        ) { Text(if (vm.deleting) "Deleting…" else "Delete", color = MaterialTheme.colorScheme.error) }
                    },
                    dismissButton = {
                        TextButton(
                            enabled = !vm.deleting,
                            onClick = { vm.clearDeleteCandidate() }
                        ) { Text("Cancel") }
                    }
                )
            }
        }
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
