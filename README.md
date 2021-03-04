# JetpackComposeWithStateFlow
 Android app that combine Jetpack Compose and StateFlow APIs
 
## What is Jetpack Compose?

Jetpack Compose is Android’s modern toolkit for building native UI. It simplifies and accelerates UI development on Android. Quickly bring your app to life with less code, powerful tools, and intuitive Kotlin APIs. Jetpack Compose is in beta channel. I try to experiment Jetpack Compose and StateFlow APIs in order to create an approach similar to Flutter Bloc pattern (https://github.com/felangel/bloc/tree/master/packages/flutter_bloc). Bloc makes it easy to separate presentation from business logic, making your code fast, easy to test, and reusable. 

![image](https://user-images.githubusercontent.com/33979978/109959824-4bbf0980-7ce8-11eb-88ae-f7af571cc613.png)


## StateFlow APIs

StateFlow is a state-holder observable flow that emits the current and new state updates to its collectors. The current state value can also be read through its value property. To update state and send it to the flow, assign a new value to the value property of the MutableStateFlow class.

In Android, StateFlow is a great fit for classes that need to maintain an observable mutable state.

(https://developer.android.com/kotlin/flow/stateflow-and-sharedflow)

## Structure Understanding
In this simple app I separate Buisness Logic from UI in this way:

- Composable Function to create the UI
- ViewModel to encapsulate BuisnessLogic
- State Management with:
- HomeState (4 different states)
- Events (functions that changes state)

## ViewModel state and exposed field

```kotlin
private val _homeState = MutableStateFlow<HomeState>(HomeState.Empty)
val homeState:StateFlow<HomeState> = _homeState
private var _items = mutableListOf<Item>()
```

## HomeState

ViewModel Sealed class that allow us to model the app (or page) state.

```kotlin
sealed class HomeState {
    data class Success(val items:List<Item>):HomeState()
    data class Error(val message:String, val items:List<Item>):HomeState()
    object Loading:HomeState()
    object Empty:HomeState()
}
```

## Events

Functions that changes the app (or page) state.

```kotlin
fun addObject(item: Item)
fun removeAll()
```

## Conditional UI (state dependency)

```kotlin
@Composable
fun AppBody(homeState: MainViewModel.HomeState) {

    when (homeState) {
        is Loading -> Loading()

        is Success -> ItemsList(itemsList = homeState.items)

        is Empty -> ItemCreation()

        is Error -> ItemsList(itemsList = homeState.items)
    }
}
```

## App Screenshots

![image](https://user-images.githubusercontent.com/33979978/109961982-f506ff00-7cea-11eb-85b7-db2aa1770481.png)

![image](https://user-images.githubusercontent.com/33979978/109961998-fa644980-7cea-11eb-8c23-bac39cdc70a5.png)

![image](https://user-images.githubusercontent.com/33979978/109962144-22ec4380-7ceb-11eb-9203-1364c8c7d430.png)

![image](https://user-images.githubusercontent.com/33979978/109962168-28e22480-7ceb-11eb-87ac-9c65539d0869.png)




