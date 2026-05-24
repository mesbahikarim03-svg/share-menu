package com.example.sharemenu

  import android.content.Context

  /**
   * Persists user customisations (pinned / hidden apps) in SharedPreferences.
   */
  class AppPreferences(context: Context) {

      private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

      fun getPinned(): Set<String> = prefs.getStringSet(KEY_PINNED, emptySet()) ?: emptySet()
      fun getHidden(): Set<String> = prefs.getStringSet(KEY_HIDDEN, emptySet()) ?: emptySet()

      fun isPinned(pkg: String) = getPinned().contains(pkg)
      fun isHidden(pkg: String) = getHidden().contains(pkg)

      fun togglePin(pkg: String) = toggle(KEY_PINNED, pkg)
      fun toggleHide(pkg: String) = toggle(KEY_HIDDEN, pkg)

      private fun toggle(key: String, pkg: String) {
          val set = (prefs.getStringSet(key, emptySet()) ?: emptySet()).toMutableSet()
          if (!set.add(pkg)) set.remove(pkg)
          prefs.edit().putStringSet(key, set).apply()
      }

      companion object {
          private const val PREFS_NAME = "share_menu_prefs"
          private const val KEY_PINNED = "pinned"
          private const val KEY_HIDDEN = "hidden"
      }
  }
  