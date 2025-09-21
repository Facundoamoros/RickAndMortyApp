package com.facundoamoros.rickandmortyapp

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.facundoamoros.rickandmorty.model.Character
import com.facundoamoros.rickandmorty.viewmodel.CharacterViewModel
import com.facundoamoros.rickandmortyapp.ui.theme.RickAndMortyAppTheme
import android.util.Log
import android.net.Uri
import com.google.gson.Gson
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack

// Initialize main activity and set up Compose theme
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RickAndMortyAppTheme { //Apply custom app theme
                MainScreenWrapper() //Initialize navigation wrapper
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreenWrapper() {
    val navController = rememberNavController() //Set up navigation controller

    //Implement navigation between main screen and detail screen
    NavHost(navController = navController, startDestination = "main") {
        composable("main") { MainScreen(navController) } //Main screen route
        composable(
            route = "detail/{characterJson}",
            arguments = listOf(navArgument("characterJson") { type = NavType.StringType })
        ) { backStackEntry ->
            val json = backStackEntry.arguments?.getString("characterJson")
            val character = Gson().fromJson(json, Character::class.java)
            CharacterDetailScreen(character) { navController.popBackStack() } //Detail screen route
        }
    }
}


//Add MainScreen composable with state handling and error display
@Composable
fun MainScreen(navController: androidx.navigation.NavHostController, modifier: Modifier = Modifier) {
    val viewModel: CharacterViewModel = viewModel()
    val characters by viewModel.characters.collectAsState()
    val error by viewModel.error.collectAsState()
    val isLoading = characters.isEmpty() && error.isNullOrEmpty()
    val context = LocalContext.current

    LaunchedEffect(characters, error) {
        if (!error.isNullOrEmpty()) {
            Log.e("MainActivity", "Error fetching characters: $error")
            Toast.makeText(context, error, Toast.LENGTH_LONG).show()
        } else if (characters.isNotEmpty()) {
            Log.d("MainActivity", "Fetched ${characters.size} characters")
        }
    }

    Surface(modifier = modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        when {
            isLoading -> Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) { CircularProgressIndicator() } //Show loading indicator

            !error.isNullOrEmpty() -> Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(text = error!!, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(16.dp))
            }

            else -> CharacterList(characters, navController) //Show list of characters
        }
    }
}

//Implement character list and clickable items
@Composable
fun CharacterList(characters: List<Character>, navController: androidx.navigation.NavHostController) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(characters) { character ->
            CharacterItem(character, navController) //Individual character item
            Divider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)) //Visual separation between items
        }
    }
}

@Composable
fun CharacterItem(character: Character, navController: androidx.navigation.NavHostController) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable {
                val json = Uri.encode(Gson().toJson(character))
                navController.navigate("detail/$json") //Navigate to detail screen on click
            },
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        AsyncImage(model = character.image, contentDescription = character.name, modifier = Modifier.size(60.dp))
        Column(modifier = Modifier.padding(start = 8.dp)) {
            Text(text = character.name, style = MaterialTheme.typography.titleMedium)
            Text(text = character.species, style = MaterialTheme.typography.bodyMedium)
            Text(text = character.status, style = MaterialTheme.typography.bodySmall)
        }
    }
}


//Create CharacterDetailScreen with top bar and scrollable content
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CharacterDetailScreen(character: Character, onBack: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(character.name) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, contentDescription = "Back") } }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.Top
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp),
                shape = MaterialTheme.shapes.medium,
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp) //Card elevation
            ) {
                AsyncImage(model = character.image, contentDescription = character.name, modifier = Modifier.fillMaxSize()) //Show character image
            }

            Spacer(modifier = Modifier.height(16.dp))

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shadowElevation = 4.dp, //Add shadow to info card
                shape = MaterialTheme.shapes.medium
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Nombre: ${character.name}", style = MaterialTheme.typography.titleMedium)
                    Text("Especie: ${character.species}", style = MaterialTheme.typography.bodyMedium)
                    Text("Estado: ${character.status}", style = MaterialTheme.typography.bodyMedium)
                    Text("Género: ${character.gender}", style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}

