package com.example.calculatorapp

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.HorizontalScrollView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider

class MainActivity : AppCompatActivity() {

    private var canAddOperation = false
    private var canAddDecimal = true
    private var isEmpty = true
    private var countDecimal = 0
    private var countZero = 0

    private lateinit var resultText: TextView
    private lateinit var mathOperationText: TextView
    private lateinit var viewModel: ViewModelCalculator
    private lateinit var scroll: HorizontalScrollView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        resultText = findViewById(R.id.result_text)
        mathOperationText = findViewById(R.id.math_operation)

        viewModel = ViewModelProvider(this)[ViewModelCalculator::class.java]
        viewModel.mathOperation.observe(this){newExpression ->
            mathOperationText.text = newExpression
        }
        viewModel.result.observe(this){result ->
            resultText.text = result
        }

        viewModel.viewCanAddOperation.observe(this) { canAddOperation = it }
        viewModel.viewCanAddDecimal.observe(this) { canAddDecimal = it }
        viewModel.viewIsEmpty.observe(this) { isEmpty = it }
        viewModel.viewCountDecimal.observe(this) { countDecimal = it }
        viewModel.viewCountZero.observe(this) { countZero = it }
    }

    fun scrollToEnd(){
        scroll = findViewById<HorizontalScrollView>(R.id.scrollable)
        scroll.postDelayed({
            scroll.smoothScrollTo(resultText.width, 0)
        }, 70)
    }
    fun allClearAction(view: View) {
        viewModel.updateEmpty(true)
        mathOperationText.text = ""
        viewModel.updateExpression("")

        viewModel.updateDecimal(true)
        viewModel.updateCountDecimal(0)
        viewModel.updateCountZero(0)
        viewModel.updateEmpty(true)
        viewModel.updateOperation(false)

    }

    fun backspace(view: View) {
        val mathText = resultText.text
        val lastChar = mathText.lastOrNull().toString()
        if (lastChar == ".") {
                viewModel.updateDecimal(true)
                viewModel.updateCountDecimal(0)
            }
        if(lastChar == "0" && mathText.dropLast(1).isEmpty()){
            viewModel.updateCountZero(0)
            viewModel.updateEmpty(true)

        }

        resultText.text = mathText.dropLast(1)

        if(resultText.text.length == 0)
            viewModel.updateEmpty(true)

        viewModel.updateOperation(true)
        viewModel.updateExpression(resultText.text.toString())
    }

    fun numberAction(view: View) {
        if (view is Button) {

            var number = view.text

            if (number == "0") {
                if (isEmpty) {
                    viewModel.updateExpression(resultText.text.toString() + view.text)
                    viewModel.updateCountZero(1)
                    viewModel.updateOperation(true)
                } else if (countZero != 1) {
                    viewModel.updateExpression(resultText.text.toString() + view.text)
                    viewModel.updateOperation(true)
                }
                viewModel.updateDecimal(true)
                viewModel.updateEmpty(false)

            } else if (view.text == ".") {
                if (canAddDecimal && !isEmpty && countDecimal == 0) {
                    viewModel.updateExpression(resultText.text.toString() + view.text)
                    viewModel.updateDecimal(false)
                    viewModel.updateCountDecimal(1)
                    viewModel.updateCountZero(0)
                }
            } else {
                viewModel.updateExpression(resultText.text.toString() + view.text)
                viewModel.updateEmpty(false)
                viewModel.updateCountZero(0)
                viewModel.updateOperation(true)
                viewModel.updateDecimal(true)

            }

            scrollToEnd()
        }
    }

    fun operationAction(view: View) {
        if (view is Button) {
            if (canAddOperation) {
                viewModel.updateExpression(resultText.text.toString() + view.text)
                viewModel.updateOperation(false)
                viewModel.updateEmpty(true)
                viewModel.updateCountDecimal(0)
                viewModel.updateDecimal(true)

            }
            scrollToEnd()
        }
    }

    fun equalsAction(view: View) {
viewModel.canCalculate(resultText.text.toString())

    }
}
