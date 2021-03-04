package it.salmanapp.jetpackcomposewithstateflow

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import it.salmanapp.jetpackcomposewithstateflow.data.models.Item
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlin.random.Random

class MainViewModel:ViewModel() {

    private val _homeState = MutableStateFlow<HomeState>(HomeState.Empty)
    val homeState:StateFlow<HomeState> = _homeState
    private var _items = mutableListOf<Item>()

    fun addObject(item: Item)=viewModelScope.launch {
        _homeState.value = HomeState.Loading
        val randomState = Random.nextInt(0,100)
        delay(1000L)
        if (randomState>65){
            _homeState.value = HomeState.Error("Error occourred", _items)
        }else {
            _items = _items.toMutableList()
            _items.add(item)
            Log.d("items", _items.toString())
            _homeState.value = HomeState.Success(items = _items)
        }
    }


    fun removeAll()=viewModelScope.launch {
        _homeState.value = HomeState.Loading
        delay(1000L)
        _items = _items.toMutableList()
        _items.clear()
        _homeState.value = HomeState.Success(items = _items)
    }

    sealed class HomeState{
        data class Success(val items:List<Item>):HomeState()
        data class Error(val message:String, val items:List<Item>):HomeState()
        object Loading:HomeState()
        object Empty:HomeState()

    }
}