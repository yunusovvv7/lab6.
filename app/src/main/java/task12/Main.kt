package task12

class QuotaExceeded(val limit: Int) : CustomError("Quota of $limit exceeded")

fun report(error: AppError) {
    val kind = when (error) {
        is IoError -> "I/O"
        Timeout -> "timeout"
        is CustomError -> "custom"
    }
    println("[$kind] ${error.text}")
}

fun createError(line: String): AppError? {
    val parts = line.trim().split(" ", limit = 2)
    val argument = parts.getOrElse(1) { "" }
    return when (parts[0]) {
        "missing" -> IoError.FileMissing(argument)
        "denied" -> IoError.NoPermission(argument)
        "disk" -> DiskFull(argument.toIntOrNull() ?: 0)
        "timeout" -> Timeout
        "quota" -> QuotaExceeded(argument.toIntOrNull() ?: 0)
        "other" -> CustomError(argument)
        else -> null
    }
}

fun main() {
    println("Describe an error: missing PATH, denied PATH, disk MB, timeout, quota N, other TEXT, or end")
    while (true) {
        print("> ")
        val line = readln()
        if (line.trim() == "end") break
        val error = createError(line)
        if (error == null) println("Unknown error type") else report(error)
    }
}
