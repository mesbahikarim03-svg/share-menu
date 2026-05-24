package com.example.sharemenu

  import android.content.Context
  import android.content.Intent
  import android.content.pm.PackageManager
  import android.os.Build

  /**
   * Queries PackageManager for every installed activity that can handle
   * the received share intent, then annotates results with pin/hide state.
   */
  object AppDiscovery {

      fun query(context: Context, intent: Intent): List<AppInfo> {
          val pm = context.packageManager
          val prefs = AppPreferences(context)

          // Build a clean query intent — keep only action + type
          val queryIntent = Intent(intent.action).apply { type = intent.type }

          val resolveList = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
              pm.queryIntentActivities(
                  queryIntent,
                  PackageManager.ResolveInfoFlags.of(PackageManager.MATCH_ALL.toLong())
              )
          } else {
              @Suppress("DEPRECATION")
              pm.queryIntentActivities(queryIntent, PackageManager.MATCH_ALL)
          }

          return resolveList
              .filter { it.activityInfo.packageName != context.packageName } // exclude self
              .map { info ->
                  val pkg = info.activityInfo.packageName
                  AppInfo(
                      label        = info.loadLabel(pm).toString(),
                      packageName  = pkg,
                      activityName = info.activityInfo.name,
                      icon         = info.loadIcon(pm),
                      isPinned     = prefs.isPinned(pkg),
                      isHidden     = prefs.isHidden(pkg),
                  )
              }
              // Pinned apps first, then alphabetical
              .sortedWith(compareBy({ !it.isPinned }, { it.label.lowercase() }))
      }
  }
  