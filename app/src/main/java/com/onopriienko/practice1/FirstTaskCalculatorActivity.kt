package com.onopriienko.practice1

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.util.Locale

class FirstTaskCalculatorActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.first_task_calculator)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.first_task_calculator)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        findViewById<Button>(R.id.buttonBackToMenu).setOnClickListener { finish() }

        val buttonCalculateDry = findViewById<Button>(R.id.buttonCalculateDry)
        val buttonCalculateFlammable = findViewById<Button>(R.id.buttonCalculateFlammable)
        val buttonCalculateLowestHeat = findViewById<Button>(R.id.buttonCalculateLowestHeat)

        val editTextHydrogen = findViewById<EditText>(R.id.editTextH)
        val editTextOxygen = findViewById<EditText>(R.id.editTextO)
        val editTextCarbon = findViewById<EditText>(R.id.editTextC)
        val editTextNitrogen = findViewById<EditText>(R.id.editTextN)
        val editTextSulphur = findViewById<EditText>(R.id.editTextS)
        val editTextWetness = findViewById<EditText>(R.id.editTextW)
        val editTextAsh = findViewById<EditText>(R.id.editTextA)

        val textViewHydrogen = findViewById<TextView>(R.id.textViewResultH)
        val textViewOxygen = findViewById<TextView>(R.id.textViewResultO)
        val textViewCarbon = findViewById<TextView>(R.id.textViewResultC)
        val textViewNitrogen = findViewById<TextView>(R.id.textViewResultN)
        val textViewSulphur = findViewById<TextView>(R.id.textViewResultS)
        val textViewAsh = findViewById<TextView>(R.id.textViewResultA)

        val textViewResultLowHeatWorking = findViewById<TextView>(R.id.textViewResultLowHeatWorking)
        val textViewResultLowHeatDry = findViewById<TextView>(R.id.textViewResultLowHeatDry)
        val textViewResultLowHeatFlammable =
            findViewById<TextView>(R.id.textViewResultLowHeatFlammable)

        buttonCalculateDry.setOnClickListener {
            val h = editTextHydrogen.getDouble()
            val o = editTextOxygen.getDouble()
            val c = editTextCarbon.getDouble()
            val n = editTextNitrogen.getDouble()
            val s = editTextSulphur.getDouble()
            val w = editTextWetness.getDouble()
            val a = editTextAsh.getDouble()
            val k = calculateDryCoefficient(w)

            val newH = h * k
            val newO = o * k
            val newC = c * k
            val newN = n * k
            val newS = s * k
            val newA = a * k

            textViewHydrogen.text =
                String.format(Locale.getDefault(), RESULT_TEMPLATE, HYDROGEN_RESULT, newH)
            textViewOxygen.text =
                String.format(Locale.getDefault(), RESULT_TEMPLATE, OXYGEN_RESULT, newO)
            textViewCarbon.text =
                String.format(Locale.getDefault(), RESULT_TEMPLATE, CARBON_RESULT, newC)
            textViewNitrogen.text =
                String.format(Locale.getDefault(), RESULT_TEMPLATE, NITROGEN_RESULT, newN)
            textViewSulphur.text =
                String.format(Locale.getDefault(), RESULT_TEMPLATE, SULPHUR_RESULT, newS)
            textViewAsh.text = String.format(Locale.getDefault(), RESULT_TEMPLATE, ASH_RESULT, newA)
        }

        buttonCalculateFlammable.setOnClickListener {
            val h = editTextHydrogen.getDouble()
            val o = editTextOxygen.getDouble()
            val c = editTextCarbon.getDouble()
            val n = editTextNitrogen.getDouble()
            val s = editTextSulphur.getDouble()
            val w = editTextWetness.getDouble()
            val a = editTextAsh.getDouble()
            val k = calculateFlammableCoefficient(wetness = w, ash = a)

            val newH = h * k
            val newO = o * k
            val newC = c * k
            val newN = n * k
            val newS = s * k

            textViewHydrogen.text =
                String.format(Locale.getDefault(), RESULT_TEMPLATE, HYDROGEN_RESULT, newH)
            textViewOxygen.text =
                String.format(Locale.getDefault(), RESULT_TEMPLATE, OXYGEN_RESULT, newO)
            textViewCarbon.text =
                String.format(Locale.getDefault(), RESULT_TEMPLATE, CARBON_RESULT, newC)
            textViewNitrogen.text =
                String.format(Locale.getDefault(), RESULT_TEMPLATE, NITROGEN_RESULT, newN)
            textViewSulphur.text =
                String.format(Locale.getDefault(), RESULT_TEMPLATE, SULPHUR_RESULT, newS)
            textViewAsh.text = ""
        }

        buttonCalculateLowestHeat.setOnClickListener {
            val h = editTextHydrogen.getDouble()
            val o = editTextOxygen.getDouble()
            val c = editTextCarbon.getDouble()
            val s = editTextSulphur.getDouble()
            val w = editTextWetness.getDouble()
            val a = editTextAsh.getDouble()
            val lowestHeatForWorkingMass = calculateLowestHeatForWorkingMass(
                c = c,
                h = h,
                o = o,
                s = s,
                w = w,
            )

            val lowestHeatForDryMass =
                (lowestHeatForWorkingMass + WETNESS_MULTIPLIER_FOR_HEAT * w) * (100 / (100 - w))
            val lowestHeatForFlammableMass =
                (lowestHeatForWorkingMass + WETNESS_MULTIPLIER_FOR_HEAT * w) * (100 / (100 - w - a))

            textViewResultLowHeatWorking.text = String.format(
                Locale.getDefault(),
                LOWEST_HEAT_WORKING_RESULT_TEMPLATE,
                convertKilosToMega(lowestHeatForWorkingMass),
            )
            textViewResultLowHeatDry.text = String.format(
                Locale.getDefault(),
                LOWEST_HEAT_DRY_RESULT_TEMPLATE,
                convertKilosToMega(lowestHeatForDryMass),
            )
            textViewResultLowHeatFlammable.text = String.format(
                Locale.getDefault(),
                LOWEST_HEAT_FLAMMABLE_RESULT_TEMPLATE,
                convertKilosToMega(lowestHeatForFlammableMass),
            )
        }
    }

    private fun EditText.getDouble(): Double = this.text.toString().trim().toDoubleOrNull() ?: 0.0

    private fun calculateDryCoefficient(wetness: Double): Double =
        100 / (100 - wetness)

    private fun calculateFlammableCoefficient(wetness: Double, ash: Double): Double =
        100 / (100 - wetness - ash)

    private fun calculateLowestHeatForWorkingMass(
        c: Double,
        h: Double,
        o: Double,
        s: Double,
        w: Double
    ): Double = LOWEST_HEAT_CARBON_MULTIPLIER * c +
            LOWEST_HEAT_HYDROGEN_MULTIPLIER * h -
            LOWEST_HEAT_OXYGEN_SULPHUR_MULTIPLIER * (o - s) -
            LOWEST_HEAT_WETNESS_MULTIPLIER * w

    private fun convertKilosToMega(value: Double): Double = value / KILOS_IN_MEGA

    companion object {
        const val RESULT_TEMPLATE = "%s %.3f"

        const val HYDROGEN_RESULT = "H, %:"
        const val OXYGEN_RESULT = "O, %:"
        const val CARBON_RESULT = "C, %:"
        const val NITROGEN_RESULT = "N, %:"
        const val SULPHUR_RESULT = "S, %:"
        const val ASH_RESULT = "A, %:"

        const val LOWEST_HEAT_CARBON_MULTIPLIER: Int = 339
        const val LOWEST_HEAT_HYDROGEN_MULTIPLIER: Int = 1030
        const val LOWEST_HEAT_OXYGEN_SULPHUR_MULTIPLIER: Double = 108.8
        const val LOWEST_HEAT_WETNESS_MULTIPLIER: Int = 25

        const val WETNESS_MULTIPLIER_FOR_HEAT: Double = 0.025

        const val KILOS_IN_MEGA: Double = 1000.0

        const val LOWEST_HEAT_WORKING_RESULT_TEMPLATE = "Робочої маси, кДж/кг: %.3f"
        const val LOWEST_HEAT_DRY_RESULT_TEMPLATE = "Сухої маси, кДж/кг: %.3f"
        const val LOWEST_HEAT_FLAMMABLE_RESULT_TEMPLATE = "Горючої маси, кДж/кг: %.3f"
    }
}
