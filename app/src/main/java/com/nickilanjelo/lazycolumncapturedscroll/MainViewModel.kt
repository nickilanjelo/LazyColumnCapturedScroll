package com.nickilanjelo.lazycolumncapturedscroll

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * @author Илясов Никита
 */
class MainViewModel : ViewModel() {

    private val mutableItems: MutableStateFlow<List<Int>> = MutableStateFlow(emptyList())
    private val mutableNextPageLoad: MutableStateFlow<Boolean> = MutableStateFlow(false)
    private val savedItems = mutableListOf<Int>()

    @Volatile
    private var itemIndex: Int = 0

    val items: StateFlow<List<Int>>
        get() = mutableItems.asStateFlow()

    val nextPageLoad: StateFlow<Boolean>
        get() = mutableNextPageLoad.asStateFlow()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            savedItems += List(50) {
                ++itemIndex
            }
            mutableItems.emit(savedItems.toList())
        }
    }

    fun getNext() {
        viewModelScope.launch(Dispatchers.IO) {
            mutableNextPageLoad.emit(true)
            delay(2000)
            mutableNextPageLoad.emit(false)
            savedItems += List(50) {
                ++itemIndex
            }
            mutableItems.emit(savedItems.toList())
        }
    }
}