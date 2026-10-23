package task14

sealed interface Outcome<out T> {
    data class Ok<T>(val value: T) : Outcome<T>
    data class Fail(val reason: String) : Outcome<Nothing>
}

fun parseQuantity(text: String): Outcome<Int> {
    val number = text.trim().toIntOrNull()
        ?: return Outcome.Fail("'$text' is not a whole number")
    if (number <= 0) return Outcome.Fail("The quantity must be positive")
    if (number > 100) return Outcome.Fail("We cannot sell more than 100 at once")
    return Outcome.Ok(number)
}

fun parsePrice(text: String): Outcome<Double> {
    val price = text.trim().toDoubleOrNull()
        ?: return Outcome.Fail("'$text' is not a price")
    if (price <= 0.0) return Outcome.Fail("The price must be positive")
    return Outcome.Ok(price)
}

fun parseEmail(text: String): Outcome<String> {
    val email = text.trim()
    if ('@' !in email) return Outcome.Fail("'$email' is not a valid email address")
    return Outcome.Ok(email)
}

fun main() {
    print("Quantity: ")
    val quantity = parseQuantity(readln())
    print("Unit price: ")
    val price = parsePrice(readln())
    print("E-mail: ")
    val email = parseEmail(readln())

    when (quantity) {
        is Outcome.Fail -> println("Quantity error: ${quantity.reason}")
        is Outcome.Ok -> when (price) {
            is Outcome.Fail -> println("Price error: ${price.reason}")
            is Outcome.Ok -> when (email) {
                is Outcome.Fail -> println("Email error: ${email.reason}")
                is Outcome.Ok -> println("Total for ${email.value}: ${quantity.value * price.value}")
            }
        }
    }
}
