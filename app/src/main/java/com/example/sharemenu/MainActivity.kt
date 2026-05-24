package com.example.sharemenu

  import android.os.Bundle
  import androidx.appcompat.app.AppCompatActivity

  /**
   * Transparent shell activity — simply shows the share bottom sheet,
   * then finishes itself when the sheet is dismissed.
   */
  class MainActivity : AppCompatActivity() {

      override fun onCreate(savedInstanceState: Bundle?) {
          super.onCreate(savedInstanceState)
          if (savedInstanceState == null) {
              ShareBottomSheetFragment
                  .newInstance(intent)
                  .show(supportFragmentManager, ShareBottomSheetFragment.TAG)
          }
      }
  }
  