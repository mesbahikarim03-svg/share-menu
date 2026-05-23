package com.example.sharemenu.ui.theme

  import androidx.compose.material3.MaterialTheme
  import androidx.compose.material3.lightColorScheme
  import androidx.compose.runtime.Composable

  private val Colors = lightColorScheme()

  @Composable
  fun ShareMenuTheme(content: @Composable () -> Unit) {
      MaterialTheme(
          colorScheme = Colors,
          content = content,
      )
  }