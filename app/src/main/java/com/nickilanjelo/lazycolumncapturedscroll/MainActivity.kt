package com.nickilanjelo.lazycolumncapturedscroll

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.LocalOverscrollConfiguration
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import com.nickilanjelo.lazycolumncapturedscroll.ui.theme.LazyColumnCapturedScrollTheme

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    @OptIn(ExperimentalFoundationApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {

            val overscrollConfig = LocalOverscrollConfiguration.current

            val items by viewModel.items.collectAsState()
            val nextPageLoad by viewModel.nextPageLoad.collectAsState()
            val lazyListState = rememberLazyListState()

            val shouldStartPagination by remember {
                derivedStateOf { lazyListState.shouldLoadNextPage(items.size) }
            }
            LaunchedEffect(key1 = shouldStartPagination) {
                if (shouldStartPagination && !nextPageLoad) {
                    viewModel.getNext()
                }
            }

            LazyColumnCapturedScrollTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Scaffold { paddingValues ->
                        CompositionLocalProvider(
                            LocalOverscrollConfiguration provides if (nextPageLoad) null else overscrollConfig
                        ) {
                            LazyColumn(
                                state = lazyListState,
                                contentPadding = paddingValues
                            ) {
                                items.onEach {
                                    item {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(8.dp)
                                                .height(30.dp),
                                            contentAlignment = Alignment.CenterStart
                                        ) {
                                            Text(
                                                text = it.toString(),
                                                fontSize = TextUnit(20f, TextUnitType.Sp),
                                                fontFamily = FontFamily.SansSerif
                                            )
                                        }
                                    }
                                }
                                if (nextPageLoad) {
                                    item {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(30.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            CircularProgressIndicator()
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun LazyListState.shouldLoadNextPage(itemsSize: Int): Boolean =
    layoutInfo.visibleItemsInfo.lastOrNull()?.index?.let { lastItemIndex ->
        lastItemIndex >= itemsSize - OffscreenItemsLimit
    } ?: false

private const val OffscreenItemsLimit = 10