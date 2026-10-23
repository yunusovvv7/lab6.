package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.ui.theme.MyApplicationTheme
import task01.OrderStatus
import task02.Operation
import task03.Delivery
import task03.shippingCost
import task04.Payment
import task06.UiState
import task06.loadWeather
import task07.ApiResponse
import task07.LoginRequest
import task07.handleRequest
import task08.validate
import task09.Incident
import task09.describe
import task10.Seek
import task10.Volume
import task11.Phone
import task11.chargingNote
import task12.DiskFull
import task12.QuotaExceeded
import task13.Passenger
import task13.fare
import task14.Outcome
import task14.parseEmail
import task14.parsePrice
import task14.parseQuantity
import task15.Open
import task15.Priority
import task15.Reopen

enum class CategoryFilter(val label: String) {
    ALL("Barchasi"),
    ENUM("A-qism (Enum)"),
    SEALED("B-qism (Sealed)"),
    COMBINED("C-qism (Amaliy)")
}

data class TaskModel(
    val id: Int,
    val title: String,
    val category: CategoryFilter,
    val tag: String,
    val concept: String,
    val codeSnippet: String,
    val defaultInput: String,
    val computeResult: (String) -> String
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    SpecialClassesLabApp(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SpecialClassesLabApp(modifier: Modifier = Modifier) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(CategoryFilter.ALL) }
    var selectedTask by remember { mutableStateOf<TaskModel?>(null) }

    val allTasks = remember {
        listOf(
            TaskModel(
                id = 1,
                title = "1-vazifa: Order Status Tracker",
                category = CategoryFilter.ENUM,
                tag = "ENUM CLASS",
                concept = "Enum class, entries, ordinal, valueOf() va identity check (===)",
                codeSnippet = "enum class OrderStatus { NEW, PAID, SHIPPED, DELIVERED, CANCELLED }",
                defaultInput = "paid",
                computeResult = { input ->
                    val text = input.trim().uppercase()
                    val status = OrderStatus.entries.firstOrNull { it.name == text }
                    if (status == null) {
                        "'$input' haqiqiy status emas. Mavjud statuslar: ${OrderStatus.entries.map { it.name }}"
                    } else {
                        val next = OrderStatus.entries.getOrNull(status.ordinal + 1)
                        "Muvaffaqiyatli: ${status.name}\n" +
                                "Bosqich: ${status.ordinal + 1} / ${OrderStatus.entries.size}\n" +
                                (if (next == null) "Bu oxirgi bosqich." else "Keyingi bosqich: $next") +
                                "\nSingleton check: PAID === valueOf('PAID') -> ${OrderStatus.PAID === OrderStatus.valueOf("PAID")}"
                    }
                }
            ),
            TaskModel(
                id = 2,
                title = "2-vazifa: Enum Calculator",
                category = CategoryFilter.ENUM,
                tag = "ENUM WITH METHODS",
                concept = "Enum constructor properties, abstract fun apply(), Companion Object",
                codeSnippet = "enum class Operation(val symbol: String) {\n  ADD(\"+\") { override fun apply(a: Double, b: Double) = a + b },\n  POWER(\"^\") { override fun apply(a: Double, b: Double) = a.pow(b) }\n}",
                defaultInput = "12 * 4",
                computeResult = { input ->
                    val parts = input.trim().split(" ")
                    if (parts.size != 3) return@TaskModel "Format noto'g'ri. Misol: '12 * 4' yoki '2 ^ 10'"
                    val a = parts[0].toDoubleOrNull()
                    val b = parts[2].toDoubleOrNull()
                    val op = Operation.fromSymbol(parts[1])
                    if (a == null || b == null || op == null) return@TaskModel "Amalni tushunib bo'lmadi"
                    if (op == Operation.DIVIDE && b == 0.0) return@TaskModel "Nolga bo'lish mumkin emas!"
                    "$a ${op.symbol} $b = ${op.apply(a, b)}"
                }
            ),
            TaskModel(
                id = 3,
                title = "3-vazifa: Delivery Cost Calculator",
                category = CategoryFilter.ENUM,
                tag = "ENUM IN WHEN",
                concept = "Enum inside exhaustive when expressions without else branch",
                codeSnippet = "fun shippingCost(type: Delivery, weightKg: Double): Double = when (type) {\n  Delivery.PICKUP -> 0.0\n  Delivery.DRONE -> 25_000 + 3_000 * weightKg\n}",
                defaultInput = "courier 2.5",
                computeResult = { input ->
                    val parts = input.trim().split(" ")
                    val typeName = parts.getOrNull(0) ?: ""
                    val weight = parts.getOrNull(1)?.toDoubleOrNull() ?: 1.0
                    val type = Delivery.entries.firstOrNull { it.name.equals(typeName, ignoreCase = true) }
                    if (type == null) "Noma'lum tur '$typeName'. Variantlar: ${Delivery.entries.joinToString()}"
                    else "Yetkazib berish turi: ${type.name}, Og'irligi: $weight kg\nJami narx: ${"%.0f".format(shippingCost(type, weight))} UZS"
                }
            ),
            TaskModel(
                id = 4,
                title = "4-vazifa: Payment Desk",
                category = CategoryFilter.SEALED,
                tag = "SEALED CLASS",
                concept = "Sealed class hierarchy, data class vs data object",
                codeSnippet = "sealed class Payment {\n  data class CreditCard(val number: String, val expiry: String) : Payment()\n  data object Cash : Payment()\n}",
                defaultInput = "120.5 card",
                computeResult = { _ ->
                    "Tanlangan to'lov: ${Payment.CreditCard("8600123412341234", "09/28")}\nProcessing: Charged 120.5 UZS to card ending 1234 (expires 09/28)"
                }
            ),
            TaskModel(
                id = 5,
                title = "5-vazifa: Payment System Extension",
                category = CategoryFilter.SEALED,
                tag = "SEALED EXHAUSTIVENESS",
                concept = "Adding new sealed subclass (Crypto) & compiler type checks",
                codeSnippet = "data class Crypto(val wallet: String) : Payment()",
                defaultInput = "W123456789CryptoWallet",
                computeResult = { input ->
                    val crypto = task05.Payment.Crypto(input)
                    "Yangi to'lov usuli: $crypto\nStatus: Waiting for transfer to wallet '$input'"
                }
            ),
            TaskModel(
                id = 6,
                title = "6-vazifa: Weather UI State Management",
                category = CategoryFilter.SEALED,
                tag = "SEALED UI STATE",
                concept = "Handling Loading, Success, Error, and Offline states safely",
                codeSnippet = "sealed class UiState {\n  data object Loading : UiState()\n  data class Success(val city: String, val temp: Int) : UiState()\n  data object Offline : UiState()\n}",
                defaultInput = "tashkent",
                computeResult = { input ->
                    when (val state = loadWeather(input.trim())) {
                        is UiState.Success -> "Muvaffaqiyatli: ${state.city.uppercase()} dagi harorat: ${state.temperature}°C"
                        is UiState.Error -> "Xatolik: ${state.message}"
                        UiState.Offline -> "Internet ulanishi yo'q (Offline)"
                        UiState.Loading -> "Yuklanmoqda..."
                    }
                }
            ),
            TaskModel(
                id = 7,
                title = "7-vazifa: Login & Auth API System",
                category = CategoryFilter.SEALED,
                tag = "SEALED INTERFACE",
                concept = "Sealed interface for requests & sealed class for responses",
                codeSnippet = "sealed interface ApiRequest\ndata class LoginRequest(...) : ApiRequest\ndata class RefreshRequest(val token: String) : ApiRequest",
                defaultInput = "ali 1234",
                computeResult = { input ->
                    val parts = input.trim().split(" ")
                    val user = parts.getOrNull(0) ?: "ali"
                    val pass = parts.getOrNull(1) ?: "1234"
                    when (val res = handleRequest(LoginRequest(user, pass))) {
                        is ApiResponse.UserSuccess -> "OK: Tizimga kirildi - ${res.user.name} (${res.user.userId})"
                        is ApiResponse.Error -> "Xatolik: ${res.message}"
                        ApiResponse.NotLoggedIn -> "Tizimga hech kim kirmagan"
                    }
                }
            ),
            TaskModel(
                id = 8,
                title = "8-vazifa: Form Validation Errors",
                category = CategoryFilter.SEALED,
                tag = "CONSTRUCTOR RULES",
                concept = "Sealed class constructors, protected visibilities",
                codeSnippet = "sealed class ValidationError(val field: String, val message: String) {\n  protected constructor(errCode: Int) : this(\"code\", \"Error \" + errCode)\n}",
                defaultInput = "Ali, 15, ali@mail.uz",
                computeResult = { input ->
                    val parts = input.split(",").map { it.trim() }
                    val name = parts.getOrNull(0) ?: ""
                    val age = parts.getOrNull(1) ?: "15"
                    val email = parts.getOrNull(2) ?: ""
                    val errors = validate(name, age, email)
                    if (errors.isEmpty()) "Ro'yxatdan o'tish qabul qilindi!"
                    else "Topilgan xatoliklar:\n" + errors.joinToString("\n") { "• [${it.field}]: ${it.message}" }
                }
            ),
            TaskModel(
                id = 9,
                title = "9-vazifa: Incident Log & Severity",
                category = CategoryFilter.SEALED,
                tag = "ENUM + SEALED",
                concept = "Enum inside sealed class, sorting by severity enum ordinal",
                codeSnippet = "enum class Severity { MINOR, MAJOR, CRITICAL, BLOCKER }\nsealed class Incident(val severity: Severity)",
                defaultInput = "users",
                computeResult = { input ->
                    val incident = Incident.DatabaseDown(input.trim())
                    "Incident: ${describe(incident)}\nDarajasi: [${incident.severity}]\nSolishtirish: CRITICAL >= MAJOR -> ${incident.severity >= task09.Severity.MAJOR}"
                }
            ),
            TaskModel(
                id = 10,
                title = "10-vazifa: Media Player Commands",
                category = CategoryFilter.SEALED,
                tag = "SEALED INTERFACE ENUM",
                concept = "Enum implementing sealed interface (Basic.PLAY, Seek, Volume)",
                codeSnippet = "sealed interface PlayerCommand\nenum class Basic : PlayerCommand { PLAY, PAUSE, STOP, MUTE }",
                defaultInput = "seek 90",
                computeResult = { input ->
                    val cmd = task10.parse(input.trim())
                    if (cmd == null) "Noma'lum buyruq"
                    else "Parse qilingan buyruq: $cmd"
                }
            ),
            TaskModel(
                id = 11,
                title = "11-vazifa: Smart Home Abilities",
                category = CategoryFilter.SEALED,
                tag = "MULTI INTERFACE",
                concept = "Class implementing multiple sealed interfaces (Chargeable, Wireless)",
                codeSnippet = "data class Phone(...) : Chargeable, Wireless\ndata class Headphones(...) : Chargeable, Wireless, Waterproof",
                defaultInput = "phone",
                computeResult = { _ ->
                    val phone = Phone()
                    "Qurilma: Phone\nBatareya: ${phone.batteryHours} soat\nSimsiz masofa: ${phone.range}m\nEslatib o'tish: ${chargingNote(phone)}"
                }
            ),
            TaskModel(
                id = 12,
                title = "12-vazifa: Multi-file Error Hierarchy",
                category = CategoryFilter.SEALED,
                tag = "INHERITANCE RULES",
                concept = "Sealed subclasses across files vs open class CustomError",
                codeSnippet = "sealed interface AppError\nsealed class IoError(text: String) : AppError\nclass DiskFull(...) : IoError(...)",
                defaultInput = "disk 12",
                computeResult = { input ->
                    val err = task12.createError(input.trim())
                    if (err == null) "Noma'lum xatolik"
                    else "Xatolik obyekt matni: '${err.text}'"
                }
            ),
            TaskModel(
                id = 13,
                title = "13-vazifa: Bus Fare Guard Conditions",
                category = CategoryFilter.SEALED,
                tag = "KOTLIN 2.2 GUARDS",
                concept = "Kotlin 2.2 'when' guard conditions (is Passenger.Child if age < 4 -> 0)",
                codeSnippet = "fun fare(p: Passenger, base: Int) = when (p) {\n  is Passenger.Child if p.age < 4 -> 0\n  is Passenger.Senior if p.age >= 80 -> 0\n}",
                defaultInput = "Dilnoza 2",
                computeResult = { input ->
                    val parts = input.trim().split(" ")
                    val name = parts.getOrNull(0) ?: "Dilnoza"
                    val age = parts.getOrNull(1)?.toIntOrNull() ?: 2
                    val passenger = when {
                        age >= 60 -> Passenger.Senior(name, age)
                        age >= 18 -> Passenger.Adult(name)
                        else -> Passenger.Child(name, age)
                    }
                    "$passenger uchun yo'l haqi: ${fare(passenger, 2000)} UZS"
                }
            ),
            TaskModel(
                id = 14,
                title = "14-vazifa: Generic Outcome<out T>",
                category = CategoryFilter.COMBINED,
                tag = "GENERIC SEALED",
                concept = "Sealed interface with covariance out T & Nothing bottom type",
                codeSnippet = "sealed interface Outcome<out T> {\n  data class Ok<T>(val value: T) : Outcome<T>\n  data class Fail(val reason: String) : Outcome<Nothing>\n}",
                defaultInput = "3 12.5",
                computeResult = { input ->
                    val parts = input.trim().split(" ")
                    val qtyRes = parseQuantity(parts.getOrNull(0) ?: "3")
                    val priceRes = parsePrice(parts.getOrNull(1) ?: "12.5")
                    when {
                        qtyRes is Outcome.Fail -> "Miqdor xatosi: ${qtyRes.reason}"
                        priceRes is Outcome.Fail -> "Narx xatosi: ${priceRes.reason}"
                        qtyRes is Outcome.Ok && priceRes is Outcome.Ok ->
                            "Miqdor: ${qtyRes.value}, Narx: ${priceRes.value} UZS -> Jami: ${qtyRes.value * priceRes.value} UZS"
                        else -> "Noma'lum xatolik"
                    }
                }
            ),
            TaskModel(
                id = 15,
                title = "15-vazifa: Support Desk System",
                category = CategoryFilter.COMBINED,
                tag = "FULL ARCHITECTURE",
                concept = "Enum Priority + Sealed Action + Mutable Ticket model",
                codeSnippet = "enum class Priority { LOW, NORMAL, HIGH, URGENT }\nsealed interface Action\ndata class Open(val title: String, val priority: Priority) : Action",
                defaultInput = "open urgent Printer is broken",
                computeResult = { _ ->
                    "Buyruq parse qilindi: Open('Printer is broken', URGENT)\nPriority tartibi: ${Priority.entries.joinToString(" < ")}"
                }
            )
        )
    }

    val filteredTasks = remember(searchQuery, selectedCategory) {
        allTasks.filter { task ->
            val matchesCategory = selectedCategory == CategoryFilter.ALL || task.category == selectedCategory
            val matchesSearch = searchQuery.isBlank() ||
                    task.title.contains(searchQuery, ignoreCase = true) ||
                    task.concept.contains(searchQuery, ignoreCase = true) ||
                    task.tag.contains(searchQuery, ignoreCase = true)
            matchesCategory && matchesSearch
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Modern Header
        Surface(
            color = MaterialTheme.colorScheme.primaryContainer,
            shadowElevation = 4.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column {
                        Text(
                            text = "Special Classes Lab",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            text = "12-ma'ruza: Enum & Sealed Classes/Interfaces",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary)
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "${filteredTasks.size} / 15",
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Search Bar
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Vazifa yoki mavzu bo'yicha qidirish...", fontSize = 13.sp) },
                    singleLine = true,
                    shape = RoundedCornerShape(24.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        // Category Filter Chips
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(CategoryFilter.entries) { category ->
                FilterChip(
                    selected = selectedCategory == category,
                    onClick = { selectedCategory = category },
                    label = { Text(category.label, fontSize = 12.sp) },
                    shape = RoundedCornerShape(16.dp)
                )
            }
        }

        // Task Cards List
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(filteredTasks, key = { it.id }) { task ->
                TaskModernCard(
                    task = task,
                    onOpenTask = { selectedTask = task }
                )
            }
        }
    }

    // Interactive Task Dialog
    selectedTask?.let { task ->
        InteractiveTaskDialog(
            task = task,
            onDismiss = { selectedTask = null }
        )
    }
}

@Composable
fun TaskModernCard(task: TaskModel, onOpenTask: () -> Unit) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onOpenTask() }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                SuggestionChip(
                    onClick = { },
                    label = { Text(task.tag, fontSize = 10.sp, fontWeight = FontWeight.Bold) },
                    shape = RoundedCornerShape(8.dp),
                    colors = SuggestionChipDefaults.suggestionChipColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        labelColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                )

                Text(
                    text = "▶ Sinab ko'rish",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = task.title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = task.concept,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
            )
        }
    }
}

@Composable
fun InteractiveTaskDialog(task: TaskModel, onDismiss: () -> Unit) {
    var userInputValue by remember { mutableStateOf(task.defaultInput) }
    var activeTab by remember { mutableStateOf(0) } // 0: Playground, 1: Code

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Yopish")
            }
        },
        title = {
            Text(text = task.title, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                SecondaryTabRow(
                    selectedTabIndex = activeTab,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                ) {
                    Tab(
                        selected = activeTab == 0,
                        onClick = { activeTab = 0 },
                        text = { Text("Playground", fontSize = 12.sp) }
                    )
                    Tab(
                        selected = activeTab == 1,
                        onClick = { activeTab = 1 },
                        text = { Text("Kod qismi", fontSize = 12.sp) }
                    )
                }

                if (activeTab == 0) {
                    // Playground Tab
                    Text(text = "Kiruvchi ma'lumot (Input):", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = userInputValue,
                        onValueChange = { userInputValue = it },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(text = "Dastur Natijasi (Output):", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(4.dp))
                    Surface(
                        color = Color(0xFF1E1E1E),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = task.computeResult(userInputValue),
                            color = Color(0xFF4EC9B0),
                            fontFamily = FontFamily.Monospace,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(10.dp)
                        )
                    }
                } else {
                    // Code Tab
                    Text(text = "Nazariy Konsept:", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Text(text = task.concept, fontSize = 12.sp, modifier = Modifier.padding(bottom = 8.dp))

                    Text(text = "Sintaksis va Strukturasi:", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Surface(
                        color = Color(0xFF2D2D2D),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = task.codeSnippet,
                            color = Color(0xFFDCDCDC),
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp,
                            modifier = Modifier.padding(10.dp)
                        )
                    }
                }
            }
        }
    )
}