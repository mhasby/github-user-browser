package com.pasteuri.githubuserbrowser.util

import java.util.Locale

fun String.reformatEnum(): String {
    return replace("_", "").lowercase().capitalize(Locale.getDefault())
}