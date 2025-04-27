package com.test.lutjeditore

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.IOException
import java.util.Random

class MainActivity : AppCompatActivity() {

    private lateinit var quoteText: TextView
    private lateinit var sourceText: TextView
    private lateinit var nextButton: Button
    private lateinit var progressBar: ProgressBar

    private var quotesList: List<Quote> = emptyList()
    private var categoriesMap: Map<Int, String> = emptyMap()
    private val random = Random()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        quoteText = findViewById(R.id.quoteText)
        // Make sure your layout XML has an ID like 'sourceText' or 'authorText'
        // If you kept 'authorText' in XML, use that ID here:
        sourceText = findViewById(R.id.authorText) // Or R.id.sourceText if you rename in XML
        nextButton = findViewById(R.id.nextButton)
        progressBar = findViewById(R.id.progressBar)

        loadDataFromAssets()

        nextButton.setOnClickListener {
            displayRandomQuote()
        }

        // Display the first quote immediately after loading
        if (quotesList.isNotEmpty()) {
            displayRandomQuote()
        } else {
            quoteText.text = "Nuk u gjetën lutje." // No quotes found message
            sourceText.text = ""
            progressBar.visibility = View.GONE
            Log.e("MainActivity", "Quotes list is empty after loading.")
        }
    }

    private fun loadDataFromAssets() {
        progressBar.visibility = View.VISIBLE
        val gson = Gson()

        try {
            // Load Categories
            val categoriesJsonString = readJsonFromAssets("categories.json")
            if (categoriesJsonString != null) {
                val categoryListType = object : TypeToken<List<Category>>() {}.type
                val categories: List<Category> = gson.fromJson(categoriesJsonString, categoryListType)
                // Create a map for easy lookup: ID -> Name
                categoriesMap = categories.associateBy({ it.id }, { it.name })
                Log.d("MainActivity", "Loaded ${categoriesMap.size} categories.")
            } else {
                Log.e("MainActivity", "Could not read categories.json")
                sourceText.text = "Gabim në ngarkimin e kategorive" // Error message
            }


            // Load Quotes
            val quotesJsonString = readJsonFromAssets("quotes.json")
            if (quotesJsonString != null) {
                // *** IMPORTANT: Use the correct cleaned quotes.json file ***
                val quoteListType = object : TypeToken<List<Quote>>() {}.type
                quotesList = gson.fromJson(quotesJsonString, quoteListType)
                Log.d("MainActivity", "Loaded ${quotesList.size} quotes.")

            } else {
                Log.e("MainActivity", "Could not read quotes.json")
                quoteText.text = "Gabim në ngarkimin e lutjeve" // Error message
                progressBar.visibility = View.GONE // Hide progress bar on error
                return // Exit if quotes failed to load
            }

        } catch (e: Exception) { // Catch potential JSON parsing errors too
            Log.e("MainActivity", "Error loading or parsing JSON", e)
            quoteText.text = "Gabim në përpunimin e të dhënave."
            sourceText.text = ""
            progressBar.visibility = View.GONE
            return // Exit on error
        }
        progressBar.visibility = View.GONE // Hide progress bar after successful load
        Log.d("MainActivity", "Data loading complete.")

    }

    // Helper function to read file from assets
    private fun readJsonFromAssets(fileName: String): String? {
        return try {
            val inputStream = assets.open(fileName)
            val size = inputStream.available()
            val buffer = ByteArray(size)
            inputStream.read(buffer)
            inputStream.close()
            String(buffer, Charsets.UTF_8) // Specify Charset UTF-8 for compatibility
        } catch (e: IOException) {
            Log.e("MainActivity", "Error reading $fileName from assets", e)
            null
        }
    }

    private fun displayRandomQuote() {
        if (quotesList.isEmpty()) {
            Log.w("MainActivity", "Attempted to display quote but list is empty.")
            quoteText.text = "Nuk ka lutje për të shfaqur."
            sourceText.text = ""
            return
        }

        // Get a random quote from the list
        val randomIndex = random.nextInt(quotesList.size)
        val randomQuote = quotesList[randomIndex]

        // Find the category name using the map
        val categoryName = categoriesMap[randomQuote.category_id] ?: "Burim i panjohur" // Default if category not found

        // Display the translation and the category name
        quoteText.text = randomQuote.translation
        sourceText.text = "- $categoryName" // Display category name as the source

        Log.d("MainActivity", "Displaying quote ID: ${randomQuote.id}, Category: $categoryName")

    }
}