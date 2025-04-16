package com.pasteuri.githubuserbrowser.ui.screen.detail

import android.view.ViewGroup
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import com.pasteuri.githubuserbrowser.R
import com.pasteuri.githubuserbrowser.ui.component.EmptyLayout

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RepoDetailWebView(title: String, url: String, navController: NavController){

    var isLoading by remember { mutableStateOf(true) }
    var isError by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(title) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
                )
            )
        }
    ) { contentPadding ->
        Box(modifier = Modifier.padding(contentPadding).fillMaxSize()) {
            AndroidView(factory = {
                WebView(it).apply {
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                    this.webViewClient = CustomWebViewClient(
                        onPageFinished = {
                            isLoading = false
                        },
                        onPageError = {
                            isError = true
                        }
                    )
                }
            }, update = {
                it.loadUrl(url)
            })
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier
                    .size(48.dp)
                    .align(Alignment.Center))
            } else if (isError) {
                EmptyLayout(
                    title = stringResource(R.string.error_repo_web_title),
                    description = stringResource(R.string.error_repo_web_desc)
                )
            }
        }
    }
}

class CustomWebViewClient(
    private val onPageFinished: () -> Unit,
    private val onPageError: (WebResourceError?) -> Unit,
): WebViewClient() {
    override fun onPageFinished(view: WebView?, url: String?) {
        super.onPageFinished(view, url)
        onPageFinished()
    }

    override fun onReceivedError(
        view: WebView?,
        request: WebResourceRequest?,
        error: WebResourceError?
    ) {
        super.onReceivedError(view, request, error)
        onPageError(error)
    }
}
