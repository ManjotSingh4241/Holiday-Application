package com.netlifymanjot.christmas.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.netlifymanjot.christmas.R
import com.squareup.picasso.Picasso
import android.os.Build
import android.text.Html


data class GiftData(val name: String, val url: String, val imageUrl: String, val description: String)

class GiftAdapter(
    private val giftItems: List<GiftData>,
    private val onGiftClick: (String) -> Unit
) : RecyclerView.Adapter<GiftAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val giftImage: ImageView = itemView.findViewById(R.id.giftImage)
        val giftName: TextView = itemView.findViewById(R.id.giftName)
        val giftDescription: TextView = itemView.findViewById(R.id.giftDescription) // Add this line
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.gift_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val giftData = giftItems[position]

        // Set the gift name
        holder.giftName.text = giftData.name

        // Handle backward compatibility for Html.fromHtml
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            holder.giftDescription.text = Html.fromHtml(
                giftData.description.replace("\\n", "<br>").replace("\\u2022", "&#8226;"),
                Html.FROM_HTML_MODE_LEGACY
            )
        } else {
            holder.giftDescription.text = Html.fromHtml(
                giftData.description.replace("\\n", "<br>").replace("\\u2022", "&#8226;")
            )
        }

        // Load the gift image
        Picasso.get().load(giftData.imageUrl).into(holder.giftImage)

        // Handle click for the "Shop Now" button
        holder.itemView.findViewById<Button>(R.id.shopNowButton).setOnClickListener {
            onGiftClick(giftData.url)
        }

        // Handle click for the image
        holder.giftImage.setOnClickListener {
            onGiftClick(giftData.url)
        }
    }



    override fun getItemCount(): Int = giftItems.size
}
