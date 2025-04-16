package com.pasteuri.githubuserbrowser.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.googlefonts.Font
import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.compose.ui.unit.sp
import com.pasteuri.githubuserbrowser.R

private val fontName = GoogleFont("Rubik")
private val provider = GoogleFont.Provider(
    providerAuthority = "com.google.android.gms.fonts",
    providerPackage = "com.google.android.gms",
    certificates = R.array.com_google_android_gms_fonts_certs
)
private val fontFamily = FontFamily(
    Font(googleFont = fontName, fontProvider = provider)
)
private val defaultTypography = Typography()

// Set of Material typography styles to start with
val Typography = Typography(
    headlineLarge = defaultTypography.headlineLarge.copy(
        fontFamily = fontFamily,
    ),
    headlineMedium = defaultTypography.headlineMedium.copy(
        fontFamily = fontFamily,
    ),
    headlineSmall = defaultTypography.headlineSmall.copy(
        fontFamily = fontFamily,
    ),
    displayLarge = defaultTypography.displayLarge.copy(
        fontFamily = fontFamily,
    ),
    displayMedium = defaultTypography.displayMedium.copy(
        fontFamily = fontFamily,
    ),
    displaySmall = defaultTypography.displaySmall.copy(
        fontFamily = fontFamily,
        fontWeight = FontWeight.Bold,
    ),
    titleLarge = defaultTypography.titleLarge.copy(
        fontSize = 20.sp,
        fontFamily = fontFamily,
        fontWeight = FontWeight.Medium,
    ),
    titleMedium = defaultTypography.titleMedium.copy(
        fontFamily = fontFamily,
        fontWeight = FontWeight.Medium,
    ),
    titleSmall = defaultTypography.titleSmall.copy(
        fontFamily = fontFamily,
        fontWeight = FontWeight.Medium,
    ),
    bodyLarge = defaultTypography.bodyLarge.copy(
        fontFamily = fontFamily,
        fontWeight = FontWeight.Medium,
    ),
    bodyMedium = defaultTypography.bodyMedium.copy(
        fontFamily = fontFamily,
        fontWeight = FontWeight.Medium,
    ),
)
