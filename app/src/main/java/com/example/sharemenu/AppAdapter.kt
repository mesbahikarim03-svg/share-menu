package com.example.sharemenu

  import android.view.LayoutInflater
  import android.view.View
  import android.view.ViewGroup
  import android.widget.ImageView
  import android.widget.TextView
  import androidx.appcompat.widget.PopupMenu
  import androidx.recyclerview.widget.RecyclerView

  /**
   * Grid adapter for the share bottom sheet.
   * Long-press shows a popup to Pin or Hide an app.
   */
  class AppAdapter(
      private var items: List<AppInfo>,
      private val onAppClick: (AppInfo) -> Unit,
      private val onPinToggle: (AppInfo) -> Unit,
      private val onHideToggle: (AppInfo) -> Unit,
  ) : RecyclerView.Adapter<AppAdapter.VH>() {

      inner class VH(view: View) : RecyclerView.ViewHolder(view) {
          val icon:         ImageView = view.findViewById(R.id.app_icon)
          val label:        TextView  = view.findViewById(R.id.app_label)
          val pinIndicator: ImageView = view.findViewById(R.id.pin_indicator)
      }

      override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH =
          VH(LayoutInflater.from(parent.context).inflate(R.layout.item_app_grid, parent, false))

      override fun getItemCount() = items.size

      override fun onBindViewHolder(holder: VH, position: Int) {
          val app = items[position]
          holder.icon.setImageDrawable(app.icon)
          holder.label.text = app.label
          holder.pinIndicator.visibility = if (app.isPinned) View.VISIBLE else View.GONE

          holder.itemView.setOnClickListener { onAppClick(app) }
          holder.itemView.setOnLongClickListener {
              showPopup(holder.itemView, app)
              true
          }
      }

      private fun showPopup(anchor: View, app: AppInfo) {
          PopupMenu(anchor.context, anchor).apply {
              menu.add(0, 0, 0, if (app.isPinned) anchor.context.getString(R.string.unpin)
                                 else              anchor.context.getString(R.string.pin))
              menu.add(0, 1, 1, anchor.context.getString(R.string.hide))
              setOnMenuItemClickListener { item ->
                  when (item.itemId) {
                      0 -> onPinToggle(app)
                      1 -> onHideToggle(app)
                  }
                  true
              }
              show()
          }
      }

      fun submitList(newItems: List<AppInfo>) {
          items = newItems
          notifyDataSetChanged()
      }
  }
  