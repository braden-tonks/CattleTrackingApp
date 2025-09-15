package com.example.cattletrackingapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.cattletrackingapp.ui.theme.CattleTrackingAppTheme
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable

val supabase = createSupabaseClient(
    supabaseUrl = "https://nwslbsjyliunyhvzmipy.supabase.co",
    supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Im53c2xic2p5bGl1bnlodnptaXB5Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3NTYyNDIwMzYsImV4cCI6MjA3MTgxODAzNn0.AfPsBmB8LRu-WvAyg5sZnymIjAZnJpbX30URIsLQFxA"
) {
    install(Postgrest)
}

@Serializable
data class Farmer(
    val id: Int,
    val name: String
)
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CattleTrackingAppTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    FarmersList()
                }
            }
        }
    }
}
@Composable
fun FarmersList() {
    var farmers by remember { mutableStateOf<List<Farmer>>(listOf()) }
    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            farmers = supabase.from("farmer")
                .select().decodeList<Farmer>()
        }
    }
    LazyColumn {
        items(
            farmers,
            key = { farmer -> farmer.id },
        ) { farmer ->
            Text(
                farmer.name,
                modifier = Modifier.padding(8.dp),
            )
        }
    }
}