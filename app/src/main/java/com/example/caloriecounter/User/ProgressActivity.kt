package com.example.caloriecounter.User

import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.caloriecounter.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.jjoe64.graphview.GraphView
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries
import java.text.SimpleDateFormat
import java.util.*

data class ProgressEntry(
    val id: String = "",      // document ID
    val date: String = "",
    val weight: Float = 0f,
    val bodyFat: Float = 0f,
    val timestamp: Long = 0L
)


class ProgressActivity : AppCompatActivity() {

    private lateinit var dateButton: Button
    private lateinit var weightInput: EditText
    private lateinit var bodyFatInput: EditText
    private lateinit var saveButton: Button
    private lateinit var graph: GraphView
    private lateinit var backButton: ImageButton
    private var selectedDate: String = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    private val db = FirebaseFirestore.getInstance()
    private val userId = FirebaseAuth.getInstance().currentUser?.uid
    private var currentUser: FirebaseUser? = null
    private lateinit var adapter: ProgressEntryAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_progress)

        dateButton = findViewById(R.id.date_button)
        weightInput = findViewById(R.id.weight_input)
        bodyFatInput = findViewById(R.id.bodyfat_input)
        saveButton = findViewById(R.id.save_button)
        graph = findViewById(R.id.progress_graph)
        backButton = findViewById(R.id.back_button)
        dateButton.text = selectedDate
        dateButton.setOnClickListener { pickDate() }
        saveButton.setOnClickListener { saveProgress() }


        currentUser = FirebaseAuth.getInstance().currentUser
        backButton.setOnClickListener {
            // Navigate back to UserDashboardActivity
            val intent = Intent(this, UserDashboard::class.java)
            startActivity(intent)
            finish()  // Optional: Call finish() if you don't want to keep this activity in the back stack
        }
        // Initialize RecyclerView and Adapter
        val recyclerView = findViewById<RecyclerView>(R.id.progress_entries_list)
        adapter = ProgressEntryAdapter(mutableListOf()) { entry ->
            // Delete clicked
            db.collection("users")
                .document(currentUser!!.uid)
                .collection("progress")
                .document(entry.id)
                .delete()
                .addOnSuccessListener {
                    Toast.makeText(this, "Deleted entry", Toast.LENGTH_SHORT).show()
                    loadEntries() // reload after delete
                }
        }
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
        loadEntries()
        loadProgress()
    }
    override fun onBackPressed() {
        val intent = Intent(this, UserDashboard::class.java)
        startActivity(intent)
        finish()  // Optional: Call finish() if you don't want to keep this activity in the back stack
    }
    fun loadEntries() {
        if (userId != null) {
            db.collection("users")
                .document(userId)
                .collection("progress")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener { snapshot ->
                    val list = snapshot.map { doc ->
                        ProgressEntry(
                            id = doc.id,
                            date = doc.getString("date") ?: "",
                            weight = doc.getDouble("weight")?.toFloat() ?: 0f,
                            bodyFat = doc.getDouble("bodyFat")?.toFloat() ?: 0f,
                            timestamp = doc.getLong("timestamp") ?: 0L
                        )
                    }
                    adapter.updateList(list)
                }
        }
    }
    private fun pickDate() {
        val calendar = Calendar.getInstance()
        val dpd = DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                val cal = Calendar.getInstance()
                cal.set(year, month, dayOfMonth)
                selectedDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(cal.time)
                dateButton.text = selectedDate
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        dpd.show()
    }

    private fun saveProgress() {
        val weight = weightInput.text.toString().toFloatOrNull()
        val bodyFat = bodyFatInput.text.toString().toFloatOrNull()

        if (weight == null || bodyFat == null) {
            Toast.makeText(this, "Enter valid numbers", Toast.LENGTH_SHORT).show()
            return
        }

        if (userId == null) return

        val data = hashMapOf(
            "weight" to weight,
            "bodyFat" to bodyFat,
            "date" to selectedDate,
            "timestamp" to System.currentTimeMillis()
        )

        db.collection("users")
            .document(userId)
            .collection("progress")
            .add(data)
            .addOnSuccessListener {
                Toast.makeText(this, "Progress saved for $selectedDate", Toast.LENGTH_SHORT).show()
                weightInput.text.clear()
                bodyFatInput.text.clear()
                loadProgress()
                loadEntries()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to save progress", Toast.LENGTH_SHORT).show()
            }
    }

    private fun loadProgress() {
        if (userId == null) return

        db.collection("users")
            .document(userId)
            .collection("progress")
            .orderBy("timestamp")
            .get()
            .addOnSuccessListener { snapshot ->
                val weightSeries = LineGraphSeries<DataPoint>()
                val bodyFatSeries = LineGraphSeries<DataPoint>()

                snapshot.forEach { doc ->
                    val weight = doc.getDouble("weight")?.toFloat() ?: return@forEach
                    val bodyFat = doc.getDouble("bodyFat")?.toFloat() ?: return@forEach
                    val timestamp = doc.getLong("timestamp")?.toDouble() ?: return@forEach

                    weightSeries.appendData(DataPoint(timestamp, weight.toDouble()), true, snapshot.size())
                    bodyFatSeries.appendData(DataPoint(timestamp, bodyFat.toDouble()), true, snapshot.size())
                }

                weightSeries.title = "Weight (kg)"
                bodyFatSeries.title = "Body Fat (%)"
                weightSeries.color = Color.BLUE
                bodyFatSeries.color = Color.RED
                graph.removeAllSeries()
                graph.addSeries(weightSeries)
                graph.addSeries(bodyFatSeries)
                graph.gridLabelRenderer.labelFormatter = DateAsXAxisLabelFormatter(this)
                val timestamps = snapshot.mapNotNull { it.getLong("timestamp") }
                if (timestamps.isNotEmpty()) {
                    graph.viewport.setMinX(timestamps.minOrNull()?.toDouble() ?: 0.0)
                    graph.viewport.setMaxX(timestamps.maxOrNull()?.toDouble() ?: 0.0)
                }
                graph.viewport.isScrollable = true
                graph.viewport.isScalable = true
                val gridLabel = graph.gridLabelRenderer
                gridLabel.horizontalAxisTitle = "Entries"
                gridLabel.verticalAxisTitle = "Value"
                weightSeries.isDrawDataPoints = true
                bodyFatSeries.isDrawDataPoints = true
                weightSeries.setOnDataPointTapListener { series, dataPoint ->
                    Toast.makeText(this, "Weight: ${dataPoint.y} kg", Toast.LENGTH_SHORT).show()
                }

                bodyFatSeries.setOnDataPointTapListener { series, dataPoint ->
                    Toast.makeText(this, "Body Fat: ${dataPoint.y} %", Toast.LENGTH_SHORT).show()
                }

                weightSeries.dataPointsRadius = 8f       // size of the dots
                bodyFatSeries.dataPointsRadius = 8f
                graph.legendRenderer.isVisible = true
            }
    }
}
