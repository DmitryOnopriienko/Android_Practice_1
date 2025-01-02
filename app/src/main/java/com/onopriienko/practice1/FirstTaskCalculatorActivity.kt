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

class FirstTaskCalculatorActivity : AppCompatActivity() {

    private lateinit var editTextHydrogen: EditText
    private lateinit var editTextOxygen: EditText
    private lateinit var editTextCarbon: EditText
    private lateinit var editTextNitrogen: EditText
    private lateinit var editTextSulphur: EditText
    private lateinit var editTextWetness: EditText
    private lateinit var editTextAsh: EditText

    private lateinit var textViewHydrogen: TextView
    private lateinit var textViewOxygen: TextView
    private lateinit var textViewCarbon: TextView
    private lateinit var textViewNitrogen: TextView
    private lateinit var textViewSulphur: TextView
    private lateinit var textViewAsh: TextView

    private lateinit var textViewResultLowHeatWorking: TextView
    private lateinit var textViewResultLowHeatDry: TextView
    private lateinit var textViewResultLowHeatFlammable: TextView


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

        bindViews()

        findViewById<Button>(R.id.buttonCalculateDry).setOnClickListener {
            calculate(CalculationType.DRY)
        }
        findViewById<Button>(R.id.buttonCalculateFlammable).setOnClickListener {
            calculate(CalculationType.FLAMMABLE)
        }
        findViewById<Button>(R.id.buttonCalculateLowestHeat).setOnClickListener {
            calculateLowestHeat()
        }
    }

    private fun bindViews() {
        editTextHydrogen = findViewById(R.id.editTextH)
        editTextOxygen = findViewById(R.id.editTextO)
        editTextCarbon = findViewById(R.id.editTextC)
        editTextNitrogen = findViewById(R.id.editTextN)
        editTextSulphur = findViewById(R.id.editTextS)
        editTextWetness = findViewById(R.id.editTextW)
        editTextAsh = findViewById(R.id.editTextA)

        textViewHydrogen = findViewById(R.id.textViewResultH)
        textViewOxygen = findViewById(R.id.textViewResultO)
        textViewCarbon = findViewById(R.id.textViewResultC)
        textViewNitrogen = findViewById(R.id.textViewResultN)
        textViewSulphur = findViewById(R.id.textViewResultS)
        textViewAsh = findViewById(R.id.textViewResultA)

        textViewResultLowHeatWorking = findViewById(R.id.textViewResultLowHeatWorking)
        textViewResultLowHeatDry = findViewById(R.id.textViewResultLowHeatDry)
        textViewResultLowHeatFlammable = findViewById(R.id.textViewResultLowHeatFlammable)
    }

    private enum class CalculationType {
        DRY,
        FLAMMABLE,
    }

    private fun calculate(type: CalculationType) {
        val h = editTextHydrogen.getDouble()
        val o = editTextOxygen.getDouble()
        val c = editTextCarbon.getDouble()
        val n = editTextNitrogen.getDouble()
        val s = editTextSulphur.getDouble()
        val w = editTextWetness.getDouble()
        val a = editTextAsh.getDouble()

        val k = when (type) {
            CalculationType.DRY -> calculateDryCoefficient(w)
            CalculationType.FLAMMABLE -> calculateFlammableCoefficient(w, a)
        }

        val newH = h * k
        val newO = o * k
        val newC = c * k
        val newN = n * k
        val newS = s * k

        textViewHydrogen.text = formattedResult(HYDROGEN_RESULT, newH)
        textViewOxygen.text = formattedResult(OXYGEN_RESULT, newO)
        textViewCarbon.text = formattedResult(CARBON_RESULT, newC)
        textViewNitrogen.text = formattedResult(NITROGEN_RESULT, newN)
        textViewSulphur.text = formattedResult(SULPHUR_RESULT, newS)
        textViewAsh.text = if (type == CalculationType.DRY) {
            formattedResult(ASH_RESULT, a * k)
        } else {
            ""
        }
    }

    private fun calculateLowestHeat() {
        val h = editTextHydrogen.getDouble()
        val o = editTextOxygen.getDouble()
        val c = editTextCarbon.getDouble()
        val s = editTextSulphur.getDouble()
        val w = editTextWetness.getDouble()
        val a = editTextAsh.getDouble()

        val lowestHeatWorking = calculateLowestHeatForWorkingMass(c, h, o, s, w)
        val lowestHeatDry =
            (lowestHeatWorking + WETNESS_MULTIPLIER_FOR_HEAT * w) * calculateDryCoefficient(w)
        val lowestHeatFlammable =
            (lowestHeatWorking + WETNESS_MULTIPLIER_FOR_HEAT * w) *
                    calculateFlammableCoefficient(w, a)

        textViewResultLowHeatWorking.text =
            String.format(
                Locale.getDefault(),
                LOWEST_HEAT_WORKING_RESULT_TEMPLATE,
                convertKilosToMega(lowestHeatWorking)
            )
        textViewResultLowHeatDry.text =
            String.format(
                Locale.getDefault(),
                LOWEST_HEAT_DRY_RESULT_TEMPLATE,
                convertKilosToMega(lowestHeatDry)
            )
        textViewResultLowHeatFlammable.text =
            String.format(
                Locale.getDefault(),
                LOWEST_HEAT_FLAMMABLE_RESULT_TEMPLATE,
                convertKilosToMega(lowestHeatFlammable)
            )

    }

    private fun EditText.getDouble(): Double = text.toString().trim().toDoubleOrNull() ?: 0.0

    private fun calculateDryCoefficient(wetness: Double): Double = 100 / (100 - wetness)

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

    private fun formattedResult(label: String, value: Double): String =
        String.format(Locale.getDefault(), RESULT_TEMPLATE, label, value)

    companion object {
        const val RESULT_TEMPLATE = "%s %.4f"

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

        const val LOWEST_HEAT_WORKING_RESULT_TEMPLATE = "Робочої маси, кДж/кг: %.4f"
        const val LOWEST_HEAT_DRY_RESULT_TEMPLATE = "Сухої маси, кДж/кг: %.4f"
        const val LOWEST_HEAT_FLAMMABLE_RESULT_TEMPLATE = "Горючої маси, кДж/кг: %.4f"
    }
}
