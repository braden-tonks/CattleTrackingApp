package com.example.cattletrackingapp.ui.components

import android.app.DatePickerDialog
import android.widget.DatePicker
import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import java.util.*

@Composable
fun DatePickerField(
    label: String,
    selectedDate: String,
    onDateSelected: (String) -> Unit
) {
    val context = LocalContext.current
    var error by remember { mutableStateOf<String?>(null) }

    // Default to today's date
    val calendar = Calendar.getInstance()

    // Parse selected date ("MM/dd/yyyy")
    if (selectedDate.isNotBlank() && error == null) {
        try {
            val parts = selectedDate.split("/")
            if (parts.size == 3) {
                val month = parts[0].toInt() - 1 // Calendar months are 0-based
                val day = parts[1].toInt()
                val year = parts[2].toInt()
                calendar.set(year, month, day)
            }
        } catch (_: Exception) {
            // Ignore parse errors, fallback to today
        }
    }

    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH)
    val day = calendar.get(Calendar.DAY_OF_MONTH)

    // Create DatePickerDialog
    val datePickerDialog = DatePickerDialog(
        context,
        { _: DatePicker, pickedYear: Int, pickedMonth: Int, pickedDay: Int ->
            val formatted = "%02d/%02d/%04d".format(pickedMonth + 1, pickedDay, pickedYear)
            onDateSelected(formatted)
            error = validateDate(formatted)
        },
        year,
        month,
        day
    )

    Column {
        OutlinedTextField(
            value = selectedDate,
            onValueChange = {
                onDateSelected(it)
                error = validateDate(it)
            },
            label = { Text(label, color = MaterialTheme.colorScheme.onSurfaceVariant) },
            isError = error != null,
            trailingIcon = {
                IconButton(onClick = { datePickerDialog.show() }) {
                    Icon(
                        imageVector = Icons.Default.DateRange,
                        contentDescription = "Pick date",
                        tint = if (error == null)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.error
                    )
                }
            },
            colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                errorBorderColor = MaterialTheme.colorScheme.error,
                cursorColor = MaterialTheme.colorScheme.primary
            )
        )

        if (error != null) {
            Text(
                text = error ?: "",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

fun validateDate(date: String): String? {
    if (date.length != 10) return "Invalid date format"

    if (date.substring(2, 3) != "/" || date.substring(5, 6) != "/")
        return "Invalid date format"

    return try {
        val month = date.substring(0, 2).toInt()
        val day = date.substring(3, 5).toInt()

        when {
            month !in 1..12 -> "Invalid month"
            day !in 1..31 -> "Invalid day"
            else -> null // ✅ valid
        }
    } catch (e: Exception) {
        "Invalid date format"
    }
}
