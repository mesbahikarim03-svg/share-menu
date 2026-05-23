package com.example.sharemenu

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build

object AppResolver {

    fun resolve(context: Context, intent: Intent): List<AppInfo> {
        val pm = context.packageManager

        val queryIntent = Intent(intent.action).apply {
            type = intent.type
            if (intent.clipData != null) clipData = intent.clipData
        }

        val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PackageManager.MATCH_ALL.toLong()
        } else {
            0L
        }

        @Suppress("DEPRECATION")
        val resolveInfoList = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            pm.queryIntentActivities(
                queryIntent,
                PackageManager.ResolveInfoFlags.of(flags)
            )
        } else {
            pm.queryIntentActivities(queryIntent, 0)
        }

        return resolveInfoList
            .filter { it.activityInfo.packageName != context.packageName }
            .sortedBy { it.loadLabel(pm).toString().lowercase() }
            .map { info ->
                AppInfo(
                    label = info.loadLabel(pm).toString(),
                    packageName = info.activityInfo.packageName,
                    activityName = info.activityInfo.name,
                    icon = info.loadIcon(pm),
                )
            }
    }
}
