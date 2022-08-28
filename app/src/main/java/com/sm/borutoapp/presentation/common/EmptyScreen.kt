package com.sm.borutoapp.presentation.common

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ContentAlpha
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.paging.LoadState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.paging.compose.LazyPagingItems
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.sm.borutoapp.R
import com.sm.borutoapp.domain.model.Hero
import com.sm.borutoapp.ui.theme.DarkGray
import com.sm.borutoapp.ui.theme.LightGray
import com.sm.borutoapp.ui.theme.NETWORK_ERROR_ICON_SIZE
import com.sm.borutoapp.ui.theme.SMALL_PADDING

@Composable
fun EmptyScreen(
    error : LoadState.Error? = null ,
    heroes : LazyPagingItems<Hero>? = null
) {

    var message by remember { mutableStateOf("Find your Favourite Hero!") }
    var icon by remember{ mutableStateOf(R.drawable.ic_search_document) }

    if (error != null){
        message = parseErrorMessage(message = error.toString())
        icon = R.drawable.ic_network_error
    }

    var startAnimation by remember { mutableStateOf(false) }
    val alphaAnim by animateFloatAsState(
        targetValue = if (startAnimation) ContentAlpha.disabled else 0f ,
        animationSpec = tween(durationMillis = 1000)
    )

    // run only at first time :
    LaunchedEffect(key1 = true){
        startAnimation = true
    }

    EmptyContent(
        alphaAnim = alphaAnim,
        icon = icon,
        message = message ,
        heroes = heroes,
        error = error
    )

}

@Composable
fun EmptyContent(
    alphaAnim: Float,
    icon: Int,
    message: String,
    heroes: LazyPagingItems<Hero>? = null ,
    error : LoadState.Error? = null
) {

    var isRefreshing by remember { mutableStateOf(false) }
    
    SwipeRefresh(
        swipeEnabled = error != null ,
        state = rememberSwipeRefreshState(isRefreshing = isRefreshing),
        onRefresh = {
            isRefreshing = true
            heroes?.refresh()
            isRefreshing = false
        }
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()) ,
            horizontalAlignment = Alignment.CenterHorizontally ,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                modifier = Modifier
                    .size(NETWORK_ERROR_ICON_SIZE)
                    .alpha(alpha = alphaAnim) ,
                painter = painterResource(id = icon),
                contentDescription = stringResource(R.string.network_error_icon) ,
                tint = if (isSystemInDarkTheme()) LightGray else DarkGray
            )
            Text(
                modifier = Modifier
                    .padding(top = SMALL_PADDING)
                    .alpha(alpha = alphaAnim) ,
                text = message ,
                color = if (isSystemInDarkTheme()) LightGray else DarkGray ,
                textAlign = TextAlign.Center ,
                fontWeight = FontWeight.Medium ,
                fontSize = MaterialTheme.typography.subtitle1.fontSize
            )
        }

    }

}

fun parseErrorMessage(message:String) : String{
    return when{
        message.contains("SocketTimeoutException") -> { "Server Unavailable" }
        message.contains("ConnectException") -> { "Internet Unavailable" }
        else -> { "Unknown Error!" }
    }
}

@Preview(showBackground = true)
@Composable
fun EmptyScreenPreview() {
    EmptyContent(
        alphaAnim = ContentAlpha.disabled,
        icon = R.drawable.ic_network_error,
        message = "Internet Unavailable"
    )
}

@Preview(showBackground = true , uiMode = UI_MODE_NIGHT_YES)
@Composable
fun EmptyScreenDarkPreview() {
    EmptyContent(
        alphaAnim = ContentAlpha.disabled,
        icon = R.drawable.ic_network_error,
        message = "Internet Unavailable"
    )
}
