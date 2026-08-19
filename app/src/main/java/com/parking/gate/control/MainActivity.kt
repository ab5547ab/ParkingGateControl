package com.parking.gate.control

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.gridlayout.widget.GridLayout
import android.widget.EditText
import android.widget.Button
import android.widget.TextView

class MainActivity : AppCompatActivity() {

    companion object {
        private const val PERMISSION_CALL_PHONE = 1
        private const val PREFS_NAME = "ParkingGatePrefs"
        private const val GATES_KEY = "gates"
    }

    private lateinit var gatesContainer: GridLayout
    private lateinit var prefs: SharedPreferences
    private lateinit var gatesList: MutableList<GateData>
    private var mediaPlayer: MediaPlayer? = null

    data class GateData(
        var id: String,
        var name: String,
        var phoneNumber: String,
        var emoji: String = "🅿️"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        gatesContainer = findViewById(R.id.gatesContainer)
        
        requestPhonePermission()
        loadGates()
        buildUI()
        setupSettingsButton()
    }

    private fun requestPhonePermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CALL_PHONE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CALL_PHONE),
                PERMISSION_CALL_PHONE
            )
        }
    }

    private fun loadGates() {
        gatesList = mutableListOf()
        
        val gatesJson = prefs.getString(GATES_KEY, null)
        if (gatesJson != null) {
            try {
                val gates = gatesJson.split("|")
                gates.forEach { gateStr ->
                    val parts = gateStr.split(",")
                    if (parts.size >= 3) {
                        gatesList.add(
                            GateData(
                                parts[0],
                                parts[1],
                                parts[2],
                                if (parts.size > 3) parts[3] else "🅿️"
                            )
                        )
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        
        if (gatesList.isEmpty()) {
            gatesList.add(GateData("gate1", "חנייה עילית", "0559643981", "🅿️"))
            gatesList.add(GateData("gate2", "חנייה תת קרקעית", "0559643987", "🅿️"))
            saveGates()
        }
    }

    private fun saveGates() {
        val gatesJson = gatesList.joinToString("|") { gate ->
            "${gate.id},${gate.name},${gate.phoneNumber},${gate.emoji}"
        }
        prefs.edit().putString(GATES_KEY, gatesJson).apply()
    }

    private fun buildUI() {
        gatesContainer.removeAllViews()
        gatesContainer.columnCount = 2
        
        gatesList.forEach { gate ->
            val button = createGateButton(gate)
            val params = GridLayout.LayoutParams().apply {
                width = 0
                height = GridLayout.LayoutParams.WRAP_CONTENT
                columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
                setMargins(16, 16, 16, 16)
            }
            gatesContainer.addView(button, params)
        }
    }

    private fun createGateButton(gate: GateData): View {
        val button = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                200
            )
            setBackgroundColor(ContextCompat.getColor(this@MainActivity, R.color.gate_button_bg))
            elevation = 8f
            setPadding(16, 16, 16, 16)
            
            setOnClickListener {
                callGate(gate)
            }
            
            val emojiView = TextView(this@MainActivity).apply {
                text = gate.emoji
                textSize = 48f
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                textAlignment = View.TEXT_ALIGNMENT_CENTER
            }
            
            val nameView = TextView(this@MainActivity).apply {
                text = gate.name
                textSize = 16f
                setTextColor(ContextCompat.getColor(this@MainActivity, R.color.text_primary))
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                textAlignment = View.TEXT_ALIGNMENT_CENTER
            }
            
            addView(emojiView)
            addView(nameView)
        }
        
        return button
    }

    private fun callGate(gate: GateData) {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CALL_PHONE
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            val intent = Intent(Intent.ACTION_CALL).apply {
                data = Uri.parse("tel:${gate.phoneNumber}")
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
            }
            
            try {
                startActivity(intent)
                scheduleSoundAfterDelay()
            } catch (e: Exception) {
                Toast.makeText(
                    this,
                    "שגיאה בחיוג: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        } else {
            Toast.makeText(
                this,
                "דרוש הרשאה לחיוג",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun scheduleSoundAfterDelay() {
        Thread {
            Thread.sleep(4000)
            playGateOpenSound()
        }.start()
    }

    private fun playGateOpenSound() {
        try {
            mediaPlayer?.release()
            mediaPlayer = MediaPlayer.create(this, R.raw.gate_open)
            mediaPlayer?.start()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setupSettingsButton() {
        val settingsBtn = findViewById<Button>(R.id.settingsButton)
        settingsBtn?.setOnClickListener {
            showSettingsDialog()
        }
    }

    private fun showSettingsDialog() {
        val dialog = AlertDialog.Builder(this)
            .setTitle("הגדרות")
            .setItems(arrayOf("עריכת שערים", "הוסף שער חדש")) { _, which ->
                when (which) {
                    0 -> showEditGatesDialog()
                    1 -> showAddGateDialog()
                }
            }
            .show()
    }

    private fun showEditGatesDialog() {
        val gateNames = gatesList.map { it.name }.toTypedArray()
        
        AlertDialog.Builder(this)
            .setTitle("בחר שער לעריכה")
            .setItems(gateNames) { _, which ->
                editGate(gatesList[which])
            }
            .show()
    }

    private fun editGate(gate: GateData) {
        val dialogView = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(16, 16, 16, 16)
        }
        
        val nameInput = EditText(this).apply {
            setText(gate.name)
            hint = "שם השער"
        }
        
        val phoneInput = EditText(this).apply {
            setText(gate.phoneNumber)
            hint = "מספר טלפון"
            inputType = android.text.InputType.TYPE_CLASS_NUMBER or android.text.InputType.TYPE_CLASS_PHONE
        }
        
        dialogView.addView(nameInput)
        dialogView.addView(phoneInput)
        
        AlertDialog.Builder(this)
            .setTitle("עריכת: ${gate.name}")
            .setView(dialogView)
            .setPositiveButton("שמור") { _, _ ->
                gate.name = nameInput.text.toString()
                gate.phoneNumber = phoneInput.text.toString()
                saveGates()
                buildUI()
            }
            .setNegativeButton("מחק") { _, _ ->
                gatesList.remove(gate)
                saveGates()
                buildUI()
            }
            .show()
    }

    private fun showAddGateDialog() {
        val dialogView = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(16, 16, 16, 16)
        }
        
        val nameInput = EditText(this).apply {
            hint = "שם השער"
        }
        
        val phoneInput = EditText(this).apply {
            hint = "מספר טלפון"
            inputType = android.text.InputType.TYPE_CLASS_NUMBER or android.text.InputType.TYPE_CLASS_PHONE
        }
        
        dialogView.addView(nameInput)
        dialogView.addView(phoneInput)
        
        AlertDialog.Builder(this)
            .setTitle("הוסף שער חדש")
            .setView(dialogView)
            .setPositiveButton("הוסף") { _, _ ->
                val newGate = GateData(
                    "gate_${System.currentTimeMillis()}",
                    nameInput.text.toString(),
                    phoneInput.text.toString()
                )
                gatesList.add(newGate)
                saveGates()
                buildUI()
                Toast.makeText(this, "שער נוסף בהצלחה", Toast.LENGTH_SHORT).show()
            }
            .show()
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.release()
    }
}
