package task13

sealed class Passenger {
    data class Adult(val name: String) : Passenger()
    data class Child(val name: String, val age: Int) : Passenger()
    data class Senior(val name: String, val age: Int) : Passenger()
}

fun fare(passenger: Passenger, baseFare: Int): Int = when (passenger) {
    is Passenger.Child if passenger.age < 4 -> 0
    is Passenger.Child -> baseFare / 2
    is Passenger.Senior if passenger.age >= 80 -> 0
    is Passenger.Senior -> baseFare * 7 / 10
    is Passenger.Adult -> baseFare
}

fun main() {
    print("Name: ")
    val name = readln().trim()
    print("Age: ")
    val age = readln().trim().toIntOrNull()
    if (age == null || age < 0) {
        println("Invalid age")
        return
    }
    val passenger = when {
        age >= 60 -> Passenger.Senior(name, age)
        age >= 18 -> Passenger.Adult(name)
        else -> Passenger.Child(name, age)
    }
    println("$passenger pays ${fare(passenger, 2000)} UZS")
}
