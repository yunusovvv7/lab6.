package task12

sealed interface AppError {
    val text: String
}

sealed class IoError(override val text: String) : AppError {
    class FileMissing(val path: String) : IoError("File not found: $path")
    class NoPermission(val path: String) : IoError("No permission for $path")
}

object Timeout : AppError {
    override val text = "The operation timed out"
}

open class CustomError(override val text: String) : AppError
