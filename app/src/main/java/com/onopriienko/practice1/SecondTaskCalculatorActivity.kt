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

    private lateinit var editTextHydrogen: EditText
    private lateinit var editTextOxygen: EditText
    private lateinit var editTextCarbon: EditText
    private lateinit var editTextSulphur: EditText
    private lateinit var editTextWetness: EditText
    private lateinit var editTextAsh: EditText
    private lateinit var editTextVanadium: EditText
    private lateinit var editTextLowestHeatOfFlammableMass: EditText

    private lateinit var textViewHydrogen: TextView
    private lateinit var textViewOxygen: TextView
    private lateinit var textViewCarbon: TextView
    private lateinit var textViewSulphur: TextView
    private lateinit var textViewAsh: TextView
    private lateinit var textViewVanadium: TextView
    private lateinit var textViewResultLowHeatWorking: TextView

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

        bindViews()

        findViewById<Button>(R.id.buttonCalculateElementaryContent).setOnClickListener {
            calculateElementaryContent()
        }
        findViewById<Button>(R.id.buttonCalculateLowestHeat).setOnClickListener {
            calculateLowestHeat()
        }
    }

    private fun bindViews() {
        editTextHydrogen = findViewById(R.id.editTextH)
        editTextOxygen = findViewById(R.id.editTextO)
        editTextCarbon = findViewById(R.id.editTextC)
        editTextSulphur = findViewById(R.id.editTextS)
        editTextWetness = findViewById(R.id.editTextW)
        editTextAsh = findViewById(R.id.editTextA)
        editTextVanadium = findViewById(R.id.editTextV)
        editTextLowestHeatOfFlammableMass = findViewById(R.id.editTextLowestHeatOfFlammableMass)

        textViewHydrogen = findViewById(R.id.textViewResultH)
        textViewOxygen = findViewById(R.id.textViewResultO)
        textViewCarbon = findViewById(R.id.textViewResultC)
        textViewSulphur = findViewById(R.id.textViewResultS)
        textViewAsh = findViewById(R.id.textViewResultA)
        textViewVanadium = findViewById(R.id.textViewResultV)
        textViewResultLowHeatWorking = findViewById(R.id.textViewResultLowHeatWorking)
    }

    private fun calculateElementaryContent() {
        val h = editTextHydrogen.getDouble()
        val o = editTextOxygen.getDouble()
        val c = editTextCarbon.getDouble()
        val s = editTextSulphur.getDouble()
        val w = editTextWetness.getDouble()
        val a = editTextAsh.getDouble()
        val v = editTextVanadium.getDouble()

        val flammableToWorking = calculateFlammableToWorkingCoefficient(w, a)
        val dryToWorking = calculateDryToWorkingCoefficient(w)

        textViewHydrogen.text = formattedResult(HYDROGEN_RESULT, h * flammableToWorking)
        textViewOxygen.text = formattedResult(OXYGEN_RESULT, o * flammableToWorking)
        textViewCarbon.text = formattedResult(CARBON_RESULT, c * flammableToWorking)
        textViewSulphur.text = formattedResult(SULPHUR_RESULT, s * flammableToWorking)
        textViewAsh.text = formattedResult(ASH_RESULT, a * dryToWorking)
        textViewVanadium.text = String.format(
            Locale.getDefault(),
            "%.4f",
            v * flammableToWorking,
        )
    }

    private fun calculateLowestHeat() {
        val lowestHeatFlammable = editTextLowestHeatOfFlammableMass.getDouble()
        val w = editTextWetness.getDouble()
        val a = editTextAsh.getDouble()

        val lowestHeatWorking = calculateLowestHeatForWorkingMass(lowestHeatFlammable, a, w)

        textViewResultLowHeatWorking.text =
            String.format(Locale.getDefault(), "%.4f", lowestHeatWorking)
    }

    private fun EditText.getDouble(): Double = text.toString().trim().toDoubleOrNull() ?: 0.0

    private fun calculateDryToWorkingCoefficient(wetness: Double): Double = (100 - wetness) / 100

    private fun calculateFlammableToWorkingCoefficient(wetness: Double, ash: Double): Double =
        (100 - wetness - ash) / 100

    private fun calculateLowestHeatForWorkingMass(
        lowestHeatOfFlammable: Double,
        ash: Double,
        wetness: Double,
    ): Double = lowestHeatOfFlammable * calculateFlammableToWorkingCoefficient(wetness, ash) -
            WETNESS_MULTIPLIER_FOR_HEAT * wetness

    private fun formattedResult(label: String, value: Double): String =
        String.format(Locale.getDefault(), RESULT_TEMPLATE, label, value)

    companion object {
        const val RESULT_TEMPLATE = "%s %.4f"

        const val HYDROGEN_RESULT = "H, %:"
        const val OXYGEN_RESULT = "O, %:"
        const val CARBON_RESULT = "C, %:"
        const val SULPHUR_RESULT = "S, %:"
        const val ASH_RESULT = "A, %:"

        const val WETNESS_MULTIPLIER_FOR_HEAT: Double = 0.025
    }
}
