package task02

import kotlin.math.pow

enum class Operation(val symbol: String) {
    ADD("+") {
        override fun apply(a: Double, b: Double) = a + b
    },
    SUBTRACT("-") {
        override fun apply(a: Double, b: Double) = a - b
    },
    MULTIPLY("*") {
        override fun apply(a: Double, b: Double) = a * b
    },
    DIVIDE("/") {
        override fun apply(a: Double, b: Double) = a / b
    },
    MODULO("%") {
        override fun apply(a: Double, b: Double) = a % b
    },
    POWER("^") {
        override fun apply(a: Double, b: Double) = a.pow(b)
    };

    abstract fun apply(a: Double, b: Double): Double

    companion object {
        fun fromSymbol(symbol: String): Operation? =
            entries.firstOrNull { it.symbol == symbol }
    }
}

fun main() {
    print("Expression (for example 12 * 4): ")
    val parts = readln().trim().split(" ")
    if (parts.size != 3) {
        println("Use: number operator number")
        return
    }
    val a = parts[0].toDoubleOrNull()
    val b = parts[2].toDoubleOrNull()
    val operation = Operation.fromSymbol(parts[1])
    if (a == null || b == null || operation == null) {
        println("Cannot understand '${parts.joinToString(" ")}'")
        return
    }
    if (operation == Operation.DIVIDE && b == 0.0) {
        println("Division by zero")
        return
    }
    println("$a ${operation.symbol} $b = ${operation.apply(a, b)}")
}
