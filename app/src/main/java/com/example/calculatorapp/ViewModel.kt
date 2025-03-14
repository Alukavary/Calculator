package com.example.calculatorapp
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.mozilla.javascript.Context
import org.mozilla.javascript.Scriptable


class ViewModelCalculator:ViewModel() {

    var mathOperation = MutableLiveData<String>()
    var result = MutableLiveData<String>()

    var viewCanAddOperation = MutableLiveData<Boolean>().apply { value = false }
    var viewCanAddDecimal = MutableLiveData<Boolean>().apply { value = true }
    var viewIsEmpty = MutableLiveData<Boolean>().apply { value = true }
    var viewCountDecimal = MutableLiveData<Int>().apply { value = 0 }
    var viewCountZero = MutableLiveData<Int>().apply { value = 0 }

    fun updateOperation(b: Boolean){
        viewCanAddOperation.value = b
    }

    fun updateDecimal(b: Boolean){
        viewCanAddDecimal.value = b
    }
    fun updateCountDecimal(n: Int){
        viewCountDecimal.value = n
    }
    fun updateEmpty(b: Boolean){
        viewIsEmpty.value = b
    }
    fun updateCountZero(n: Int){
        viewCountZero.value = n
    }

    fun updateExpression(newExpression:String){
        result.value = newExpression
    }

    fun resultExpression(newExpression: String){
        mathOperation.value = result.value
        result.value = formIntOrDouble(calculate(newExpression))

    }

    fun canCalculate(result:String) {
        var text = result
        var length: Int = text.length
        var lastChar = text.lastOrNull()
        var isHaveOperation = text.any{ it in "/+*-"}

        if (lastChar != null && lastChar.isDigit() && isHaveOperation && length > 1)  {
            mathOperation.value = result
            viewCanAddOperation.value = true
            resultExpression(result)
        } else if (lastChar != null && !lastChar.isDigit()){
            text = text.dropLast(1)
            if(text.any{it in "/+*-"}) {
                mathOperation.value = text
                resultExpression(result)
                mathOperation.value = result

            }
        }
    }

    private fun calculate(expression:String): Double?{
        return try {
            val context = Context.enter()
            context.optimizationLevel = -1 // Включаем совместимость
            val scope: Scriptable = context.initStandardObjects()
            val result = context.evaluateString(scope, expression, "JavaScript", 1, null)
            Context.exit()
            val value = result.toString().toDoubleOrNull() ?: 0.0
            if(value.isNaN() || value.isInfinite())
                0.0
            else
                value
        } catch (e: Exception) {
            null
        }
    }

    fun formIntOrDouble (result: Double?): String{
        return when {
            result == null -> ""
            result % 1.0 == 0.0 -> result.toInt().toString()
            else -> result.toString()

        }

    }

}