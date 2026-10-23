package task11

sealed interface Chargeable { val batteryHours: Int }
sealed interface Wireless { val range: Int }
sealed interface Waterproof { val ipRating: String }

data class Phone(
    override val batteryHours: Int = 24,
    override val range: Int = 50
) : Chargeable, Wireless

data class Headphones(
    override val batteryHours: Int = 8,
    override val range: Int = 10,
    override val ipRating: String = "IPX7"
) : Chargeable, Wireless, Waterproof

data class SmartLamp(
    override val range: Int = 30,
    override val batteryHours: Int = 4
) : Wireless, Chargeable

class Toaster

fun create(name: String): Any? = when (name.lowercase()) {
    "phone" -> Phone()
    "headphones" -> Headphones()
    "lamp" -> SmartLamp()
    "toaster" -> Toaster()
    else -> null
}

fun chargingNote(device: Chargeable): String = when (device) {
    is Phone -> "Charge the phone every night"
    is Headphones -> "Charge the headphones after 8 hours"
    is SmartLamp -> "Charge the smart lamp when battery low"
}

fun main() {
    val devices = mutableListOf<Any>()
    println("Add devices: phone, headphones, lamp, toaster (type 'done' to finish)")
    while (true) {
        print("> ")
        val name = readln().trim()
        if (name == "done") break
        val device = create(name)
        if (device == null) println("Unknown device") else devices.add(device)
    }
    for (device in devices) {
        val abilities = mutableListOf<String>()
        if (device is Chargeable) abilities.add("battery ${device.batteryHours} h")
        if (device is Wireless) abilities.add("wireless ${device.range} m")
        if (device is Waterproof) abilities.add("waterproof (${device.ipRating})")
        val text = if (abilities.isEmpty()) "no special abilities" else abilities.joinToString()
        println("${device::class.simpleName}: $text")
    }
    println("Chargeable devices: ${devices.count { it is Chargeable }}")
}
