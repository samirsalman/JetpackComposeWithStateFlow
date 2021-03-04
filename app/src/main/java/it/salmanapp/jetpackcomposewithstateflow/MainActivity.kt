package it.salmanapp.jetpackcomposewithstateflow

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.MaterialTheme.typography
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.Icon
import androidx.compose.material.icons.rounded.DeleteForever
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import com.skydoves.landscapist.coil.CoilImage
import it.salmanapp.jetpackcomposewithstateflow.MainViewModel.HomeState.*
import it.salmanapp.jetpackcomposewithstateflow.data.models.Item
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        lifecycleScope.launchWhenStarted {
            viewModel.homeState.collect { homeState ->

                setContent {
                    val snackbarHostState = remember { SnackbarHostState() }
                    val scaffoldState = rememberScaffoldState(snackbarHostState = snackbarHostState)
                    if(homeState is Error) {
                        lifecycleScope.launch {
                            scaffoldState.snackbarHostState
                                .showSnackbar(
                                    message = homeState.message,
                                    duration = SnackbarDuration.Short,
                                    actionLabel = "Close",
                                    )
                        }

                    }
                    Scaffold(scaffoldState = scaffoldState,
                    topBar = {
                        TopAppBar(title={Text(text="JetpackWithStateFlow")},
                            actions = {
                                IconButton(onClick = { Log.d("Favorite", "Go to favorites") },){
                                    Icon(Icons.Rounded.Favorite, "favorite")
                                }
                                IconButton(onClick = {
                                    Log.d("Delete All", "Delete all elements")
                                    viewModel.removeAll()
                                    lifecycleScope.launch {
                                        scaffoldState.snackbarHostState
                                            .showSnackbar(
                                                message = "Elements Deleted",
                                                duration = SnackbarDuration.Short,
                                                actionLabel = "Close",
                                            )
                                    }
                                                     }
                                    ,){
                                    Icon(Icons.Rounded.DeleteForever, "delete")
                                }
                        })
                    })
                    {

                        AppBody(homeState = homeState)
                    }
                }
            }

        }
    }

        private val viewModel: MainViewModel by viewModels()

        @Composable
        fun Loading() {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()) {
                CircularProgressIndicator(strokeWidth = 3.dp)
            }
        }

        @Composable
        @Preview
        fun ItemCreation() {
            val name = remember { mutableStateOf(TextFieldValue()) }
            val value = remember { mutableStateOf(0.0F) }


            Column(modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)) {
                TextField(
                    value = name.value, onValueChange = { nv -> name.value = nv },
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(2.dp, Color.Transparent)
                        .padding(2.dp),
                    placeholder = { Text("Item Name") },
                )

                Slider(value = value.value,
                    onValueChange = { value.value = it },
                    valueRange = 0f..50f)

                Button(onClick = {
                    viewModel.addObject(Item(name.value.text,
                        value.value.toDouble()))
                },
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(2.dp, Color.Transparent)
                        .padding(2.dp)) {
                    Text("ADD ITEM")
                }
            }
        }

        @Preview
        @Composable
        fun InfoCard(name: String = "name", value: Float = 0.0F, image: String = "") {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(shape = RoundedCornerShape(16.dp))
                    .padding(8.dp),
                elevation = 6.dp
            ){
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CoilImage(
                        imageModel = image,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .width(80.dp)
                            .height(80.dp)
                            .clip(shape = CircleShape)
                    )
                    Text(name, style = typography.body2, fontWeight = FontWeight.Light)
                    Text(value.roundToInt().toString(),
                        style = typography.body1,
                        fontWeight = FontWeight.Bold)
                }
            }
        }

        @Composable
        fun ItemsList(itemsList: List<Item>) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                ItemCreation()
                LazyColumn {
                    items(itemsList) { i ->
                        InfoCard(name = i.name,
                            value = i.value.toFloat(),
                            image = "https://cdn4.iconfinder.com/data/icons/avatars-xmas-giveaway/128/batman_hero_avatar_comics-512.png")
                        Spacer(Modifier.requiredHeight(32.dp))
                    }
                }
            }

        }

        @Composable
        fun AppBody(homeState: MainViewModel.HomeState) {

            when (homeState) {
                is Loading -> Loading()

                is Success -> ItemsList(itemsList = homeState.items)

                is Empty -> ItemCreation()

                is Error -> ItemsList(itemsList = homeState.items)
            }
        }
    }
