package com.example.sharemenu

  import android.content.ComponentName
  import android.content.Intent
  import android.os.Bundle
  import androidx.activity.ComponentActivity
  import androidx.activity.compose.setContent
  import androidx.compose.material3.ExperimentalMaterial3Api
  import androidx.compose.material3.rememberModalBottomSheetState
  import androidx.compose.runtime.LaunchedEffect
  import androidx.compose.runtime.getValue
  import androidx.compose.runtime.mutableStateOf
  import androidx.compose.runtime.remember
  import androidx.compose.runtime.rememberCoroutineScope
  import androidx.compose.runtime.setValue
  import com.example.sharemenu.ui.theme.ShareMenuTheme
  import kotlinx.coroutines.launch

  @OptIn(ExperimentalMaterial3Api::class)
  class MainActivity : ComponentActivity() {

      override fun onCreate(savedInstanceState: Bundle?) {
          super.onCreate(savedInstanceState)

          val incomingIntent = intent

          setContent {
              ShareMenuTheme {
                  val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
                  val scope = rememberCoroutineScope()
                  var apps by remember { mutableStateOf<List<AppInfo>>(emptyList()) }
                  var sheetVisible by remember { mutableStateOf(false) }

                  LaunchedEffect(Unit) {
                      apps = AppResolver.resolve(this@MainActivity, incomingIntent)
                      sheetVisible = true
                  }

                  if (sheetVisible) {
                      ShareBottomSheet(
                          apps = apps,
                          sheetState = sheetState,
                          onDismiss = { finish() },
                          onAppSelected = { app ->
                              scope.launch { sheetState.hide() }.invokeOnCompletion {
                                  forwardIntent(incomingIntent, app)
                              }
                          },
                      )
                  }
              }
          }
      }

      private fun forwardIntent(original: Intent, app: AppInfo) {
          val forward = Intent(original).apply {
              component = ComponentName(app.packageName, app.activityName)
              flags = original.flags and Intent.FLAG_ACTIVITY_FORWARD_RESULT.inv()
          }
          startActivity(forward)
          finish()
      }
  }