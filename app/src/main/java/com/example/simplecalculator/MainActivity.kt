package com.example.simplecalculator

import kotlin.math.*
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.activity.ComponentActivity

class MainActivity : ComponentActivity(){

    private val pi = 3.141592653589793

    private lateinit var resultTv: TextView
    private var decimalEncountered = false //Single number can't have more than one decimal points

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        resultTv = findViewById(R.id.resultTv)
    }

    fun numberAction(view: View){

        val button = view as Button
        val num = button.text.toString()

        if(resultTv.text.isEmpty()){
            resultTv.append(num)
            return
        }
        val prev = resultTv.text[resultTv.text.length-1]
        if(checkBeforeNumberAction(prev)){
            resultTv.append("×$num")
        }
        else resultTv.append(num)  //PREV = DECIMAL ALSO HANDLED HERE
    }

    fun decimalAction(view: View){
        if(decimalEncountered) return //DECIMAL ALREADY THERE IN NUMBER
        //IF NOT PREVIOUSLY THEN NOW IT IS , SO MAKE IT TRUE
        decimalEncountered = true

        if(resultTv.text.isEmpty()){
            resultTv.append("0.")
            return
        }

        if(resultTv.text[resultTv.text.length-1] == 'π' || resultTv.text[resultTv.text.length-1] == '.'){
            return
        }

        val prev = resultTv.text[resultTv.text.length-1]
        checkBeforeDecimalAction(prev)
    }

    fun piAction(view: View){
        decimalEncountered = false
        if(resultTv.text.isEmpty()) {
            resultTv.append("π")
            return
        }

        var prev = resultTv.text[resultTv.text.length-1]

        if(prev == '.'){
            resultTv.text = resultTv.text.substring(0, resultTv.text.length - 1)

            prev = resultTv.text[resultTv.text.length-1]
        }
        if(checkBeforePiAction(prev)){
            resultTv.append("π")
        }
        else resultTv.append("×π")
    }

    fun powerAction(view: View){
        if(resultTv.text.isEmpty()) return

        decimalEncountered = false
        if(resultTv.text[resultTv.text.length-1] == '.'){   //PREV = DECIMAL HANDLED HERE
            resultTv.text = resultTv.text.substring(0, resultTv.text.length - 1)

        }

        val prev = resultTv.text[resultTv.text.length-1]
        if(checkBeforePowerAction(prev)){
            resultTv.append("^")
        }
    }

    fun operationAction(view: View){
        val button = view as Button
        val operationSymbol = button.text.toString() //Tells whether operation is + , - , * or /
        if(resultTv.text.isEmpty() && operationSymbol == "-"){
            resultTv.text = "(-"
            return
        }
        if(resultTv.text.isEmpty()) return

        if(resultTv.text[resultTv.text.length-1] == '.'){   //PREV = DECIMAL HANDLED HERE
            resultTv.text = resultTv.text.substring(0, resultTv.text.length - 1)
            //DECIMAL FILTERED
        }
        decimalEncountered = false



        val n = resultTv.text.length
        val prev = resultTv.text[n-1]

        if(prev == '^' &&  operationSymbol == "-"){
            resultTv.append("-")
            return
        }

        if(operationSymbol == "-" && prev == '('){
            resultTv.append(operationSymbol)
            return
        }

        if(checkBeforeOperationAction(prev)){
            resultTv.append(operationSymbol)
        }

    }

    fun allClearAction(view: View){
        decimalEncountered = false
        resultTv.text = ""
    }

    fun rootAction(view: View){
        decimalEncountered = false
        if(resultTv.text.isEmpty()){
            resultTv.append("√(")
            return
        }

        if(resultTv.text[resultTv.text.length-1] == '.'){   //PREV = DECIMAL HANDLED HERE
            resultTv.text = resultTv.text.substring(0, resultTv.text.length - 1)
        }
        val n = resultTv.text.length
        val prev = resultTv.text[n-1]
        if(checkBeforeRootAction(prev)){
            resultTv.append("×√(")
        }
        else resultTv.append("√(")
    }

    fun factorialAction(view: View){
        if(resultTv.text.isEmpty()) return
        decimalEncountered = false
        if(resultTv.text[resultTv.text.length-1] == '.'){   //PREV = DECIMAL HANDLED HERE
            resultTv.text = resultTv.text.substring(0, resultTv.text.length - 1)
            //DECIMAL FILTERED
        }
        val n = resultTv.text.length
        val prev = resultTv.text[n-1]
        if(checkBeforeFactorial(prev)){
            resultTv.append("!")
        }
    }

    fun backspaceAction(view: View){
        if(resultTv.text.isNotEmpty()) {
            if (resultTv.text[resultTv.text.length - 1] == '.') decimalEncountered = false
            resultTv.text = resultTv.text.substring(0, resultTv.text.length - 1)

        }
    }

    fun openBracketAction(view: View){ //DECIMAL FILTERED IN CHECK FUNCTION
        decimalEncountered = false
        val n = resultTv.text.length
        if(n == 0){
            resultTv.append("(")
            return
        }
        else{
            if(checkBeforeOpenBracket()) resultTv.append("(")
            else resultTv.append("×(")
        }
    }

    fun closeBracketAction(view: View){
        decimalEncountered = false
        if(resultTv.text.isEmpty()) return
        if(resultTv.text[resultTv.text.length-1] == '.') resultTv.text = resultTv.text.substring(0, resultTv.text.length - 1)

        if(checkBeforeCloseBracket()){
            resultTv.append(")")
        }
    }

    fun equalsAction(view: View){
        decimalEncountered = false
        //body here, postfix evaluation + shunting yard algo
        // We have to tokenize the char list as string list as numbers can be decimal and they can't ne separated
        val s = resultTv.text.toString()

        try {
            val tokens = tokenize(s)
            val postfix = toPostfix(tokens)
            val result = evaluatePostfix(postfix)
            val temp = result.toString()
            resultTv.text = temp
        } catch (e: ArithmeticException) {
            resultTv.text = "Math Error : ${e.message}"
        } catch (e: IllegalArgumentException) {
            resultTv.text = "Syntax Error : ${e.message}"
        } catch (e: Exception) {
            resultTv.text = "Error: ${e.message}"
        }


    }

    private fun checkBeforeOpenBracket():Boolean{
        if(resultTv.text[resultTv.text.length-1] == '.'){
            resultTv.text = resultTv.text.substring(0, resultTv.text.length - 1)

        }
        val prev = resultTv.text[resultTv.text.length-1]
        return prev == '^' || prev == '(' || prev == '÷' || prev == '×' || prev == '+' || prev == '-'
    }

    private fun checkBeforeCloseBracket():Boolean{
        if(resultTv.text[resultTv.text.length-1] == '.'){
            resultTv.text = resultTv.text.substring(0, resultTv.text.length - 1)

        }
        val prev = resultTv.text[resultTv.text.length-1]
        return !(prev == '^' || prev == '(' || prev == '÷' || prev == '×' || prev == '+' || prev == '-' || prev == '√')
    }

    private fun checkBeforeOperationAction(prev: Char):Boolean{
        return !(prev == '√' || prev == '^' || prev == '(' || prev == '÷' || prev == '×' || prev == '+' || prev == '-')
    }

    private fun checkBeforeFactorial(prev: Char):Boolean{
        return !(prev == '√' || prev == '^' || prev == '(' || prev == '÷' || prev == '×' || prev == '+' || prev == '-' || prev == '!')
    }

    private fun checkBeforeRootAction(prev: Char):Boolean{
        return !(prev == '√' || prev == '^' || prev == '(' || prev == '÷' || prev == '×' || prev == '+' || prev == '-')
    }

    private fun checkBeforePowerAction(prev: Char):Boolean{
        return !(prev == '√' || prev == '^' || prev == '(' || prev == '÷' || prev == '×' || prev == '+' || prev == '-')
    }

    private fun checkBeforePiAction(prev: Char):Boolean{
        return (prev == '√' || prev == '^' || prev == '(' || prev == '÷' || prev == '×' || prev == '+' || prev == '-')
    }

    private fun checkBeforeNumberAction(prev: Char):Boolean{
        return (prev == 'π' || prev == '!' || prev == ')')
    }

    private fun checkBeforeDecimalAction(prev: Char){
        when (prev) {
            '√', '^', '(', '÷', '×', '+', '-' -> {
                resultTv.append("0.")
            }
            ')', '!' -> {
                resultTv.append("×0.")
            }
            else -> {
                resultTv.append(".")
            }
        }
    }

    private fun tokenize(s: String): List<String>{
        val tokens = mutableListOf<String>()
        val numberBuffer = StringBuilder()
        //12.6*6-9.67^6.98!/5
        var i = 0
        while(i < s.length) {
            val char = s[i]

            if(char == '-' && (i==0 || s[i-1] in "+-×÷^(")){
                numberBuffer.append(char)
                i++
                continue
            }

            when {
                (isDigit(char) || char == '.')->{
                    numberBuffer.append(char)
                }
                (char == 'π')->{
                    if(numberBuffer.isNotEmpty()){
                        tokens.add(numberBuffer.toString())
                        numberBuffer.clear()
                    }
                    tokens.add(pi.toString())
                }
                else->{
                    if(numberBuffer.isNotEmpty()){
                        tokens.add(numberBuffer.toString())
                        numberBuffer.clear()
                    }
                    tokens.add(char.toString())
                }
            }
            i++
        }
        if(numberBuffer.isNotEmpty()) tokens.add(numberBuffer.toString())
        return tokens
    }

    private fun isDigit(ch: Char): Boolean = ch in '0'..'9'

    private fun toPostfix(s: List<String>):List<String>{
        //Still needs Recheck
        val precedence = mapOf(
            "+" to 1,
            "-" to 1,
            "×" to 2,
            "÷" to 2,
            "^" to 3,
            "√" to 4,
            "!" to 5,
//            "(" to 0  REMEMBER THIS
        )
        val isRightAssociative = setOf("√", "!") //should I include "^" here??
        val resultant = mutableListOf<String>() // Postfix Expression
        val operationStack = mutableListOf<String>() //Operation Stack

        for(token in s){
            when{
                (token.toDoubleOrNull()!=null) -> resultant.add(token) //Number directly added to resultant; Note : pi symbol already filtered to double
                token == "(" -> operationStack.add(token)
                token == ")" -> {
                    Log.d("CalcDebug", "hi there") // <--- DEBUG LOG
                    while (operationStack.last() != "("){
                        val op = operationStack.removeAt(operationStack.size - 1)
                        Log.d("CalcDebug", "hello") // <--- DEBUG LOG
                        resultant.add(op)
                        if(operationStack.isEmpty()) throw IllegalArgumentException("Invalid Expression")
                    }
                    if(operationStack.last() == "("){
                        operationStack.removeAt(operationStack.size-1)
                    }
                }
                token in precedence -> {
                    while(operationStack.isNotEmpty()){
                        if(operationStack.last() == "("){
//                            operationStack.add(token) //this
                            break
                        }
                        else{
                            if(operationStack.last() in isRightAssociative){
                                resultant.add(operationStack.removeAt(operationStack.size-1))
                            }
                            else if(token in isRightAssociative){
//                                resultant.add(token) //this
                                break
                            }
                            else if(precedence[operationStack.last()]!! < precedence[token]!! ){
//                                operationStack.add(token) //this
                                break
                            }
                            else{
                                resultant.add(operationStack.removeAt(operationStack.size-1))
                            }
                        }
                    }
                    operationStack.add(token)

                }
                else->{
                    throw IllegalArgumentException("Unknown token found")
                }
            }
        }
        while (operationStack.isNotEmpty()) {
            val op = operationStack.removeAt(operationStack.size - 1)
            if (op == "(" || op == ")") throw IllegalArgumentException("Mismatched parentheses")
            resultant.add(op)
        }
        return resultant
    }

    private fun evaluatePostfix(tokens : List<String>):Double{
        val stack = ArrayDeque<Double>()

        if(tokens.isEmpty()) return 0.0

        for(token in tokens){
            when{
                token.toDoubleOrNull() != null ->{
                    //we got a number
                    stack.add(token.toDouble())
                    Log.d("evaluate", "Token: $token") // <--- DEBUG LOG
                }
                token == "+" ->{
                    if (stack.size < 2) throw IllegalArgumentException("test")
                    val a = stack.removeLast()
                    val b = stack.removeLast()
                    stack.add(b+a)
                    Log.d("evaluate", "Token: ${b+a}") // <--- DEBUG LOG
                }
                token == "-" -> {
                    if (stack.size < 2) throw IllegalArgumentException("Invalid expression")
                    val a = stack.removeLast()
                    val b = stack.removeLast()
                    stack.add(b-a)
                }
                token == "×" -> {
                    if (stack.size < 2) throw IllegalArgumentException("Invalid expression")
                    val a = stack.removeLast()
                    val b = stack.removeLast()
                    stack.add(b*a)
                }
                token == "÷" -> {
                    if (stack.size < 2) throw IllegalArgumentException("Invalid expression")
                    val a = stack.removeLast()
                    val b = stack.removeLast()
                    if(a != 0.0) stack.add(b/a)
                    else throw ArithmeticException("Division by zero attempted")

                }
                token == "^" -> {
                    if (stack.size < 2) throw IllegalArgumentException("Invalid expression")
                    val a = stack.removeLast()
                    val b = stack.removeLast()
                    stack.add(b.pow(a))
                }
                token == "√" -> {
                    if(stack.isEmpty()) throw ArithmeticException("Invalid Expression")
                    val a = stack.removeLast()
                    if(a < 0.0){
                        throw ArithmeticException("Root of negative attempted")
                    }
                    stack.add(sqrt(a))
                }
                token == "!" -> {
                    if(stack.isEmpty()) throw ArithmeticException("Invalid Expression")
                    val a = stack.removeLast()
                    stack.add(lanczosFactorial(a))
                }
            }
        }
        return stack.last()
    }

    private fun lanczosGamma(z: Double): Double {
        val p = doubleArrayOf(
            676.5203681218851,
            -1259.1392167224028,
            771.32342877765313,
            -176.61502916214059,
            12.507343278686905,
            -0.13857109526572012,
            9.9843695780195716e-6,
            1.5056327351493116e-7
        )
        val g = 7.0

        if (z < 0.5) {
            // Reflection formula for negative values and values < 0.5
            return PI / (sin(PI * z) * lanczosGamma(1 - z))
        }

        var x = 0.99999999999980993
        for (i in p.indices) {
            x += p[i] / (z + i)
        }

        val t = z + g - 0.5
        return sqrt(2 * PI) * t.pow(z - 0.5) * exp(-t) * x
    }

    private fun lanczosFactorial(n: Double): Double {
        require(n >= 0.0) { "Factorial is not defined for negative numbers." }
        return lanczosGamma(n + 1)
    }

}

