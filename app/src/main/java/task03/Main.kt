package task03

enum class Delivery { PICKUP, COURIER, EXPRESS, DRONE }

fun shippingCost(type: Delivery, weightKg: Double): Double = when (type) {
    Delivery.PICKUP -> 0.0
    Delivery.COURIER -> 15_000 + 2_000 * weightKg
    Delivery.EXPRESS -> 40_000 + 5_000 * weightKg
    Delivery.DRONE -> 25_000 + 3_000 * weightKg
}

fun main() {
    println("Delivery types: ${Delivery.entries.joinToString()}")
    print("Delivery type: ")
    val typeName = readln().trim()
    val type = Delivery.entries.firstOrNull { it.name.equals(typeName, ignoreCase = true) }
    if (type == null) {
        println("Unknown delivery type")
        return
    }
    print("Weight in kg: ")
    val weight = readln().trim().toDoubleOrNull()
    if (weight == null || weight < 0) {
        println("Invalid weight")
        return
    }
    println("Shipping cost: ${"%.0f".format(shippingCost(type, weight))} UZS")
}
