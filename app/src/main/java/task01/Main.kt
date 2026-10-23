package task01

enum class OrderStatus { NEW, PAID, SHIPPED, DELIVERED, CANCELLED }

fun main() {
    println("Statuses: ${OrderStatus.entries.joinToString()}")
    print("Current status of your order: ")
    val text = readln().trim().uppercase()
    val status = try {
        OrderStatus.valueOf(text)
    } catch (e: IllegalArgumentException) {
        println("'$text' is not a status. Valid: ${OrderStatus.entries.map { it.name }}")
        return
    }
    println("You entered ${status.name}: step ${status.ordinal + 1} of ${OrderStatus.entries.size}")
    val next = OrderStatus.entries.getOrNull(status.ordinal + 1)
    println(if (next == null) "This is the last step." else "Next step: $next")

    // Enum singleton instance check
    println("PAID === valueOf(\"PAID\"): ${OrderStatus.PAID === OrderStatus.valueOf("PAID")}")
}
