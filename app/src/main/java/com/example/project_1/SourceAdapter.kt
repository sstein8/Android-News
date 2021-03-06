package com.example.project_1

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView

class SourceAdapter(val sources: List<Source>) : RecyclerView.Adapter<SourceAdapter.ViewHolder>() {

    // A ViewHolder represents the Views that comprise a single row in our list (e.g.
    // our row to display a Tweet contains three TextViews and one ImageView).
    //
    // The "rootLayout" passed into the constructor comes from onCreateViewHolder. From the root layout, we can
    // call findViewById to search through the hierarchy to find the Views we care about in our new row.
    class ViewHolder(rootLayout: View) : RecyclerView.ViewHolder(rootLayout) {
        val newsSource: TextView = rootLayout.findViewById(R.id.newsSource)
        val sourceDescription: TextView = rootLayout.findViewById(R.id.sourceDescription)
        val sourceID: TextView = rootLayout.findViewById(R.id.sourceID)
        val clickHere: ConstraintLayout = rootLayout.findViewById(R.id.clickHere)
    }

    // The RecyclerView needs a "fresh" / new row, so we need to:
    // 1. Read in the XML file for the row type
    // 2. Use the new row to build a ViewHolder to return
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // A LayoutInflater is an object that knows how to read & parse an XML file
        val layoutInflater: LayoutInflater = LayoutInflater.from(parent.context)

        // Read & parse the XML file to create a new row at runtime
        // The 'inflate' function returns a reference to the root layout (the "top" view in the hierarchy) in our newly created row
        val rootLayout: View = layoutInflater.inflate(R.layout.row_source, parent, false)

        // We can now create a ViewHolder from the root view
        val viewHolder = ViewHolder(rootLayout)
        return viewHolder
    }

    // The RecyclerView is ready to display a new (or recycled) row on the screen, represented a our ViewHolder.
    // We're given the row position / index that needs to be rendered.
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentSource = sources[position]

        holder.newsSource.text = currentSource.newsSource
        if(currentSource.newsSource == "null"){
            holder.newsSource.text = "Source unavailable"
        }
        holder.sourceDescription.text = currentSource.sourceDescription
        if(currentSource.sourceDescription == "null"){
            holder.sourceDescription.text = "Description unavailable"
        }

        holder.sourceID.text = currentSource.sourceID
        holder.sourceID.setVisibility(View.GONE)

        //Get the source and the search term to send to the intent
        val newsSource: String = currentSource.newsSource
        val searchTerm: String = currentSource.term
        val sourceID: String = currentSource.sourceID

        //when you click on the source, it should open up a new page with articles related to your search term
        holder.clickHere.setOnClickListener(){
            val context = holder.clickHere.context
            val sourceArticleIntent: Intent = Intent(context, SourceResultsActivity::class.java)
            sourceArticleIntent.putExtra("SearchInput", searchTerm)
            sourceArticleIntent.putExtra("SourceName", newsSource)
            sourceArticleIntent.putExtra("SourceID", sourceID)
            context.startActivity(sourceArticleIntent)
        }

    }

    // How many rows (total) do you want the adapter to render?
    override fun getItemCount(): Int {
        return sources.size
    }
}