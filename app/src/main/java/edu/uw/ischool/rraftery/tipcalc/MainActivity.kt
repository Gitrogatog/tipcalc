package edu.uw.ischool.rraftery.tipcalc

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {
    val currency = Currency()
    var tipAmount : Int = 15
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val input = this.findViewById<EditText>(R.id.dollarInput)
        val tipper = this.findViewById<Button>(R.id.btnTip)
        tipper.setEnabled(false)

        val moneyCallback : (Int, Int) -> Unit = {
            dollars: Int, pennies: Int ->
            currency.dollars = dollars
            currency.pennies = pennies
            tipper.isEnabled = !(dollars == 0 && pennies == 0)
        }
        tipper.setOnClickListener {
            val tipStr = CalculateTip(tipAmount)
            val toast = Toast.makeText(this, tipStr, Toast.LENGTH_LONG) // in Activity
            toast.show()
        }
        input.addTextChangedListener(DollarTextWatcher(input, moneyCallback));
//        input.setText("0.00")

        val tipInput = this.findViewById<EditText>(R.id.tipInput)
        val tipCallback : (Int) -> Unit = {
            tipAmount = it
        }
        tipInput.addTextChangedListener(TipTextWatcher(tipInput, tipCallback))
        tipInput.setText("15")
    }

    fun CalculateTip(tip : Int) : String {
        var dollarTip = currency.dollars * tip
        var pennyTip = dollarTip % 100
        dollarTip /= 100
        pennyTip += (currency.pennies * tip) / 100
        if (pennyTip >= 100) {
            dollarTip += pennyTip / 100
            pennyTip %= 100
        }
        if(pennyTip < 10){
            return "$$dollarTip.0$pennyTip"
        }
        return "$$dollarTip.$pennyTip"
    }
}
class Currency {
    var dollars : Int = 0
    var pennies : Int = 0
}

class DollarTextWatcher(private val editText: EditText, private val moneyCallback : (Int, Int) -> Unit) : TextWatcher {
    override fun afterTextChanged(e: Editable) {
        editText.removeTextChangedListener(this)
        var total : Int = 0
        for(i in (0..e.length-1)){
            if(e[i].isDigit()){
                val num : Int = e[i].digitToInt()
                total *= 10
                total += num
            }
        }
        val pennies = total % 100
        val dollars = total / 100
        if(pennies < 10){
            e.replace(0, e.length, "$dollars.0$pennies")
        }
        else{
            e.replace(0, e.length, "$dollars.$pennies")
        }
        moneyCallback(dollars, pennies)
        editText.addTextChangedListener(this)
        Log.i("Tip Calc", "Dollars: $dollars, Pennies: $pennies")
    }

    override fun beforeTextChanged(s: CharSequence, start: Int,
                                   count: Int, after: Int) {
    }

    override fun onTextChanged(s: CharSequence, start: Int,
                               before: Int, count: Int) {
//        Log.i("My tag", "text updated")
    }
}

class TipTextWatcher(private val editText: EditText, private val tipCallback : (Int) -> Unit) : TextWatcher {

    override fun afterTextChanged(e: Editable) {
        editText.removeTextChangedListener(this)
        var total: Int? = e.toString().toIntOrNull()
        if(total != null){
            e.replace(0, e.length, "$total")
            tipCallback(total)
            Log.i("Tip Calc", "Tip: $total")
        }
        else {
            e.replace(0, e.length, "0")
            tipCallback(0)
            Log.i("Tip Calc", "Tip was not Int")
        }
        editText.addTextChangedListener(this)
    }

    override fun beforeTextChanged(
        s: CharSequence, start: Int,
        count: Int, after: Int
    ) {
    }

    override fun onTextChanged(
        s: CharSequence, start: Int,
        before: Int, count: Int
    ) {
//        Log.i("My tag", "text updated")
    }
}
