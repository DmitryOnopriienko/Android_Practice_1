package com.onopriienko.practice1

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.util.Locale

class SecondTaskCalculatorActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.second_task_calculator)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.second_task_calculator)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        findViewById<Button>(R.id.buttonBackToMenu).setOnClickListener { finish() }

        val buttonCalculateElementaryContent =
            findViewById<Button>(R.id.buttonCalculateElementaryContent)
        val buttonCalculateLowestHeat = findViewById<Button>(R.id.buttonCalculateLowestHeat)

        val editTextHydrogen = findViewById<EditText>(R.id.editTextH)
        val editTextOxygen = findViewById<EditText>(R.id.editTextO)
        val editTextCarbon = findViewById<EditText>(R.id.editTextC)
        val editTextSulphur = findViewById<EditText>(R.id.editTextS)
        val editTextWetness = findViewById<EditText>(R.id.editTextW)
        val editTextAsh = findViewById<EditText>(R.id.editTextA)
        val editTextVanadium = findViewById<EditText>(R.id.editTextV)
        val editTextLowestHeatOfFlammableMass =
            findViewById<EditText>(R.id.editTextLowestHeatOfFlammableMass)

        val textViewHydrogen = findViewById<TextView>(R.id.textViewResultH)
        val textViewOxygen = findViewById<TextView>(R.id.textViewResultO)
        val textViewCarbon = findViewById<TextView>(R.id.textViewResultC)
        val textViewSulphur = findViewById<TextView>(R.id.textViewResultS)
        val textViewAsh = findViewById<TextView>(R.id.textViewResultA)
        val textViewVanadium = findViewById<TextView>(R.id.textViewResultV)

        val textViewResultLowHeatWorking = findViewById<TextView>(R.id.textViewResultLowHeatWorking)

        buttonCalculateElementaryContent.setOnClickListener {
            val h = editTextHydrogen.getDouble()
            val o = editTextOxygen.getDouble()
            val c = editTextCarbon.getDouble()
            val s = editTextSulphur.getDouble()
            val w = editTextWetness.getDouble()
            val a = editTextAsh.getDouble()
            val v = editTextVanadium.getDouble()

            val k = calculateFlammableToWorkingCoefficient(w, a)

            val newH = h * k
            val newO = o * k
            val newC = c * k
            val newS = s * k
            val newV = v * k
            val newA = a * calculateDryToWorkingCoefficient(w)

            textViewHydrogen.text =
                String.format(Locale.getDefault(), RESULT_TEMPLATE, HYDROGEN_RESULT, newH)
            textViewOxygen.text =
                String.format(Locale.getDefault(), RESULT_TEMPLATE, OXYGEN_RESULT, newO)
            textViewCarbon.text =
                String.format(Locale.getDefault(), RESULT_TEMPLATE, CARBON_RESULT, newC)
            textViewSulphur.text =
                String.format(Locale.getDefault(), RESULT_TEMPLATE, SULPHUR_RESULT, newS)
            textViewAsh.text =
                String.format(Locale.getDefault(), RESULT_TEMPLATE, ASH_RESULT, newA)
            textViewVanadium.text = String.format(Locale.getDefault(), "%.3f", newV)
        }

        buttonCalculateLowestHeat.setOnClickListener {
            val lowestHeatFlammable = editTextLowestHeatOfFlammableMass.getDouble()
            val w = editTextWetness.getDouble()
            val a = editTextAsh.getDouble()
            val lowestHeatForWorkingMass =
                calculateLowestHeatForWorkingMass(lowestHeatFlammable, a, w)

            textViewResultLowHeatWorking.text = String.format(
                Locale.getDefault(),
                "%.3f",
                lowestHeatForWorkingMass,
            )
        }
    }

    private fun EditText.getDouble(): Double = this.text.toString().trim().toDoubleOrNull() ?: 0.0

    private fun calculateDryToWorkingCoefficient(wetness: Double): Double =
        (100 - wetness) / 100

    private fun calculateFlammableToWorkingCoefficient(wetness: Double, ash: Double): Double =
        (100 - wetness - ash) / 100

    private fun calculateLowestHeatForWorkingMass(
        lowestHeatOfFlammable: Double,
        ash: Double,
        wetness: Double,
    ): Double = lowestHeatOfFlammable * calculateFlammableToWorkingCoefficient(wetness, ash) -
            WETNESS_MULTIPLIER_FOR_HEAT * wetness

    companion object {
        const val RESULT_TEMPLATE = "%s %.3f"

        const val HYDROGEN_RESULT = "H, %:"
        const val OXYGEN_RESULT = "O, %:"
        const val CARBON_RESULT = "C, %:"
        const val SULPHUR_RESULT = "S, %:"
        const val ASH_RESULT = "A, %:"

        const val WETNESS_MULTIPLIER_FOR_HEAT: Double = 0.025
    }
}
