package com.nickilanjelo.lazycolumncapturedscroll

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.ExperimentalUnitApi
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import com.nickilanjelo.lazycolumncapturedscroll.ui.theme.LazyColumnCapturedScrollTheme

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    @OptIn(ExperimentalMaterial3Api::class, ExperimentalUnitApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {

            val items by viewModel.items.collectAsState()
            val nextPageLoad by viewModel.nextPageLoad.collectAsState()
            val lazyListState = rememberLazyListState()

            val shouldStartPagination = remember { derivedStateOf { lazyListState.shouldLoadNextPage(items.size) } }
            LaunchedEffect(key1 = shouldStartPagination.value) {
                if (shouldStartPagination.value && !nextPageLoad) {
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

private fun LazyListState.shouldLoadNextPage(itemsSize: Int): Boolean =
    layoutInfo.visibleItemsInfo.lastOrNull()?.index?.let { lastItemIndex ->
        lastItemIndex >= itemsSize - OffscreenItemsLimit
    } ?: false

private const val OffscreenItemsLimit = 10