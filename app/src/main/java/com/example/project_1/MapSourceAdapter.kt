package com.example.project_1

import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso

class MapSourceAdapter(private val articlesList: List<Source>): RecyclerView.Adapter<MapSourceAdapter.ViewHolder>(){
    class ViewHolder(rootLayout: View) : RecyclerView.ViewHolder(rootLayout) {
        val newsSource: TextView = rootLayout.findViewById(R.id.newsSource)
        val newsHeadline: TextView = rootLayout.findViewById(R.id.newsHeadline)
        val newsContent: TextView = rootLayout.findViewById(R.id.newsContent)
        val url: TextView = rootLayout.findViewById(R.id.url)
        val thumbnail: ImageView = rootLayout.findViewById(R.id.articleThumbnail)
        val clickHereMap: ConstraintLayout = rootLayout.findViewById(R.id.clickHereMap)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // A LayoutInflater is an object that knows how to read & parse an XML file
        val layoutInflater: LayoutInflater = LayoutInflater.from(parent.context)

        // Read & parse the XML file to create a new row at runtime
        // The 'inflate' function returns a reference to the root layout (the "top" view in the hierarchy) in our newly created row
        val rootLayout: View = layoutInflater.inflate(R.layout.map_source, parent, false)

        // We can now create a ViewHolder from the root view
        val viewHolder = ViewHolder(rootLayout)
        return viewHolder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentSource = articlesList[position]

        holder.newsHeadline.text = currentSource.newsHeadline
        if(currentSource.newsHeadline == "null"){
            holder.newsHeadline.text = "No headline available"
        }

        holder.newsSource.text = currentSource.newsSource
        if(currentSource.newsSource == "null"){
            holder.newsSource.text = "Source unavailable"
        }

        holder.newsContent.text = currentSource.newsContent
        if(currentSource.newsContent == "null"){
            holder.newsContent.text= "No preview available"
        }

        holder.url.text = currentSource.url

        //Load image using Picasso
        if(currentSource.thumbnailURL != "null"){
            Picasso.get().setIndicatorsEnabled(true)
            Picasso.get()
                .load(currentSource.thumbnailURL)
                .into(holder.thumbnail)
        }

        val url: String = currentSource.url

        //Click on cardview to open URL
        Log.d("URL", url)
        holder.clickHereMap.setOnClickListener(){
            val urlIntent: Intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            // "Executes" our Intent to start a new Activity
            holder.url.context.startActivity(urlIntent)
        }
    }

    override fun getItemCount(): Int {
        return articlesList.size
    }
}