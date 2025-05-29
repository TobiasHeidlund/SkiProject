package me.heidlund.skiapp.uiComponents

import android.util.Log
import android.view.ViewGroup
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.viewinterop.AndroidView
import me.heidlund.skiapp.models.SkiPoint

@Composable
fun WebbView(
    modifier: Modifier = Modifier
){
    var webViewInstance: WebView? by remember { mutableStateOf(null) }
    var canWebViewGoBack by remember { mutableStateOf(false) }
    BackHandler(enabled = canWebViewGoBack) {
        Log.d("WebViewBack", "BackHandler triggered")
        webViewInstance?.goBack()
    }

    AndroidView( modifier = Modifier.padding(Dp(5f)),
        factory = { context ->
            // Factory block: creates and initializes the WebView
            WebView(context).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                settings.javaScriptEnabled = true
                settings.blockNetworkLoads = false
                settings.domStorageEnabled = true
                settings.loadWithOverviewMode = true
                settings.useWideViewPort = true
                webViewClient = object : WebViewClient() {
                    override fun shouldOverrideUrlLoading(
                        view: WebView?,
                        request: WebResourceRequest?
                    ): Boolean {
                        val urlToLoad = request?.url?.toString()
                        Log.d(
                            "MyWebView",
                            "shouldOverrideUrlLoading called with URL: $urlToLoad"
                        )
                        if (urlToLoad != null) {
                            view?.loadUrl(urlToLoad)
                            return true
                        }
                        return false
                    }
                    override fun onPageFinished(view: WebView?, url: String?) {
                        super.onPageFinished(view, url)
                        canWebViewGoBack = view?.canGoBack() ?: false
                        Log.d("WebViewBack", "Page finished. Can go back: $canWebViewGoBack")
                    }
                }
                loadUrl("http://192.168.0.24:5173")
                webViewInstance = this
            }
        },update = { webView ->
            canWebViewGoBack = webView.canGoBack()
            Log.d("WebViewBack", "WebView updated. Can go back: $canWebViewGoBack")
        }
    )
}

@Composable
fun LiveView(modifier: Modifier, skiPoint: State<SkiPoint?>){

    Box(modifier){
        Column(Modifier.align(Alignment.Center)) {
            Row {
                Text(text = "Speed:")
                Text(text= skiPoint.value?.speed.toString())
            }
            Row {
                Text(text = "Altitude:")
                Text(text= skiPoint.value?.alt.toString())
            }
            Row {
                Text(text = "Longitude:")
                Text(text= skiPoint.value?.lon.toString())
            }
            Row {
                Text(text = "Latitude:")
                Text(text = skiPoint.value?.lat.toString())
            }
            Row {
                Text(text = "Accuracy")
                Text(text = skiPoint.value?.error.toString())
            }
    }
    }
}