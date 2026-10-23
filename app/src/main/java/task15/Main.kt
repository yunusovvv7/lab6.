package task15

enum class Priority { LOW, NORMAL, HIGH, URGENT }

sealed interface Action
data class Open(val title: String, val priority: Priority) : Action
data class Close(val id: Int) : Action
data class Comment(val id: Int, val text: String) : Action
data class Reopen(val id: Int) : Action
data object ListAll : Action

class Ticket(val id: Int, val title: String, val priority: Priority) {
    var isOpen = true
    val comments = mutableListOf<String>()
}

val tickets = mutableListOf<Ticket>()

fun parse(line: String): Action? {
    val parts = line.trim().split(" ", limit = 3)
    return when (parts[0].lowercase()) {
        "open" -> {
            val priority = Priority.entries.firstOrNull {
                it.name.equals(parts.getOrNull(1), ignoreCase = true)
            }
            val title = parts.getOrNull(2)
            if (priority == null || title.isNullOrBlank()) null else Open(title, priority)
        }
        "close" -> parts.getOrNull(1)?.toIntOrNull()?.let { Close(it) }
        "comment" -> {
            val id = parts.getOrNull(1)?.toIntOrNull()
            val text = parts.getOrNull(2)
            if (id == null || text.isNullOrBlank()) null else Comment(id, text)
        }
        "reopen" -> parts.getOrNull(1)?.toIntOrNull()?.let { Reopen(it) }
        "list" -> ListAll
        else -> null
    }
}

fun execute(action: Action) {
    when (action) {
        is Open -> {
            val ticket = Ticket(tickets.size + 1, action.title, action.priority)
            tickets.add(ticket)
            println("Opened #${ticket.id}: ${ticket.title} [${ticket.priority}]")
        }
        is Close -> {
            val ticket = tickets.find { it.id == action.id }
            if (ticket == null) {
                println("There is no ticket #${action.id}")
            } else {
                ticket.isOpen = false
                println("Closed #${ticket.id}")
            }
        }
        is Comment -> {
            val ticket = tickets.find { it.id == action.id }
            if (ticket == null) {
                println("There is no ticket #${action.id}")
            } else {
                ticket.comments.add(action.text)
                println("Comment added to #${ticket.id} (${ticket.comments.size} in total)")
            }
        }
        is Reopen -> {
            val ticket = tickets.find { it.id == action.id }
            if (ticket == null) {
                println("There is no ticket #${action.id}")
            } else {
                ticket.isOpen = true
                println("Reopened #${ticket.id}")
            }
        }
        ListAll -> {
            for (ticket in tickets.sortedByDescending { it.priority }) {
                val state = if (ticket.isOpen) "open " else "closed"
                println("#${ticket.id} $state [${ticket.priority}] ${ticket.title}")
            }
            println("Open tickets: ${tickets.count { it.isOpen }}")
        }
    }
}

fun main() {
    println("Commands: open PRIORITY TITLE, close ID, comment ID TEXT, reopen ID, list, exit")
    println("Priorities: ${Priority.entries.joinToString()}")
    while (true) {
        print("> ")
        val line = readln()
        if (line.trim() == "exit") break
        val action = parse(line)
        if (action == null) println("I do not understand that") else execute(action)
    }
}
