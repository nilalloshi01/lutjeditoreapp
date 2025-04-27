class MainActivity : AppCompatActivity() {
    private lateinit var quoteText: TextView
    private lateinit var authorText: TextView
    private lateinit var nextButton: Button
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        quoteText = findViewById(R.id.quoteText)
        authorText = findViewById(R.id.authorText)
        nextButton = findViewById(R.id.nextButton)
        progressBar = findViewById(R.id.progressBar)

        fetchQuote()

        nextButton.setOnClickListener {
            fetchQuote()
        }
    }

    private fun fetchQuote() {
        progressBar.visibility = View.VISIBLE
        val url = "https://api.quotable.io/random"

        val request = JsonObjectRequest(
            Request.Method.GET, url, null,
            { response ->
                quoteText.text = response.getString("content")
                authorText.text = "- " + response.getString("author")
                progressBar.visibility = View.GONE
            },
            { error ->
                quoteText.text = "Failed to load quote"
                authorText.text = ""
                progressBar.visibility = View.GONE
            }
        )
        Volley.newRequestQueue(this).add(request)
    }
}
