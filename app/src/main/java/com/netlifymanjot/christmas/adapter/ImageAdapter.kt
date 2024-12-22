package com.netlifymanjot.christmas.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.netlifymanjot.christmas.R
import com.squareup.picasso.Picasso

data class ImageData(val name: String, val url: String)

class ImageAdapter(private val images: List<ImageData>) : RecyclerView.Adapter<ImageAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.uploadedImage)
        val textView: TextView = itemView.findViewById(R.id.nameSection)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.image_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val imageData = images[position]
        holder.textView.text = imageData.name
        Picasso.get().load(imageData.url).into(holder.imageView)
    }

    override fun getItemCount(): Int = images.size
}
