package com.example.sharemenu

  import android.content.DialogInterface
  import android.content.Intent
  import android.os.Build
  import android.os.Bundle
  import android.view.LayoutInflater
  import android.view.View
  import android.view.ViewGroup
  import android.widget.TextView
  import androidx.recyclerview.widget.GridLayoutManager
  import androidx.recyclerview.widget.RecyclerView
  import com.google.android.material.bottomsheet.BottomSheetDialogFragment

  /**
   * Material-3 BottomSheetDialogFragment that shows all apps able to handle
   * the shared content. Supports Pin-to-top and Hide via long-press.
   */
  class ShareBottomSheetFragment : BottomSheetDialogFragment() {

      private val originalIntent: Intent by lazy {
          if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
              requireArguments().getParcelable(ARG_INTENT, Intent::class.java)!!
          } else {
              @Suppress("DEPRECATION")
              requireArguments().getParcelable(ARG_INTENT)!!
          }
      }

      private lateinit var prefs: AppPreferences
      private lateinit var adapter: AppAdapter
      private lateinit var recycler: RecyclerView

      override fun onCreateView(
          inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
      ): View = inflater.inflate(R.layout.fragment_share_bottom_sheet, container, false)

      override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
          super.onViewCreated(view, savedInstanceState)
          prefs = AppPreferences(requireContext())

          recycler = view.findViewById(R.id.apps_recycler_view)
          recycler.layoutManager = GridLayoutManager(requireContext(), 4)

          adapter = AppAdapter(
              items       = emptyList(),
              onAppClick  = ::forwardIntent,
              onPinToggle = { app ->
                  prefs.togglePin(app.packageName)
                  refreshApps()
              },
              onHideToggle = { app ->
                  prefs.toggleHide(app.packageName)
                  refreshApps()
              },
          )
          recycler.adapter = adapter

          // Show empty state label if needed
          refreshApps()
      }

      private fun refreshApps() {
          val apps = AppDiscovery.query(requireContext(), originalIntent)
              .filter { !it.isHidden }
          adapter.submitList(apps)

          view?.findViewById<TextView>(R.id.empty_label)?.visibility =
              if (apps.isEmpty()) View.VISIBLE else View.GONE
      }

      private fun forwardIntent(app: AppInfo) {
          val forward = Intent(originalIntent).apply {
              setClassName(app.packageName, app.activityName)
              // Grant read access to any content URIs in the intent
              addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
              // Strip forward-result flag to avoid ActivityNotFoundException
              flags = flags and Intent.FLAG_ACTIVITY_FORWARD_RESULT.inv()
          }
          startActivity(forward)
          dismiss()
          activity?.finish()
      }

      override fun onDismiss(dialog: DialogInterface) {
          super.onDismiss(dialog)
          activity?.finish()
      }

      companion object {
          const val TAG = "ShareBottomSheetFragment"
          private const val ARG_INTENT = "original_intent"

          fun newInstance(intent: Intent) = ShareBottomSheetFragment().apply {
              arguments = Bundle().apply { putParcelable(ARG_INTENT, intent) }
          }
      }
  }
  