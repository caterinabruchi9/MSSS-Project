package it.unipi.dii.fingerprintingapplication

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView

// MapsAdapter for RecyclerView with a list of MapInfoDistance objects
class MapsAdapter(
    private val maps: List<MapInfoDistance>, // List of maps with distances
    private val onMapClick: (MapInfoDistance) -> Unit // Click listener with MapInfoDistance
) : RecyclerView.Adapter<MapsAdapter.MapViewHolder>() {

    // Inflate the layout containing a button for each map
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MapViewHolder {
        val button = LayoutInflater.from(parent.context).inflate(
            R.layout.item_button_map, parent, false // Inflate the button layout
        ) as Button
        return MapViewHolder(button, onMapClick) // Return the view holder with a button
    }

    // Bind the data to the button in the ViewHolder
    override fun onBindViewHolder(holder: MapViewHolder, position: Int) {
        val mapInfo = maps[position] // Get the MapInfoDistance object for the current position
        holder.bind(mapInfo) // Bind the map data to the button
    }

    override fun getItemCount(): Int {
        return maps.size // Return the total number of items
    }

    // ViewHolder for individual button-based map items
    class MapViewHolder(private val button: Button, private val onMapClick: (MapInfoDistance) -> Unit) : RecyclerView.ViewHolder(button) {
        fun bind(mapInfo: MapInfoDistance) {
            button.text = "${mapInfo.buildingName} - ${String.format("%.2f m", mapInfo.distance)}" // Set the button text
            button.setOnClickListener { onMapClick(mapInfo) } // Set click listener
        }
    }

}
