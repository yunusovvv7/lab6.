package com.example.myapplication

import org.junit.Assert.*
import org.junit.Test
import task01.OrderStatus
import task02.Operation
import task03.Delivery
import task03.shippingCost
import task04.Payment
import task06.UiState
import task06.loadWeather
import task07.ApiRequest
import task07.ApiResponse
import task07.LoginRequest
import task07.LogoutRequest
import task07.RefreshRequest
import task07.handleRequest
import task08.validate
import task09.Incident
import task09.Severity
import task09.describe
import task10.Basic
import task10.Player
import task10.Seek
import task10.Volume
import task10.parse
import task11.Phone
import task11.chargingNote
import task11.create
import task12.DiskFull
import task12.IoError
import task12.QuotaExceeded
import task12.Timeout
import task12.createError
import task13.Passenger
import task13.fare
import task14.Outcome
import task14.parseEmail
import task14.parsePrice
import task14.parseQuantity
import task15.Action
import task15.Close
import task15.Comment
import task15.ListAll
import task15.Open
import task15.Priority
import task15.Reopen
import task15.execute
import task15.tickets

class TaskExecutionTest {

    @Test
    fun testTask01OrderStatus() {
        assertEquals("NEW", OrderStatus.NEW.name)
        assertEquals(5, OrderStatus.entries.size)
        assertTrue(OrderStatus.PAID === OrderStatus.valueOf("PAID"))
    }

    @Test
    fun testTask02Operations() {
        assertEquals(16.0, Operation.ADD.apply(12.0, 4.0), 0.001)
        assertEquals(3.5, Operation.DIVIDE.apply(7.0, 2.0), 0.001)
        assertEquals(1.0, Operation.MODULO.apply(7.0, 3.0), 0.001)
        assertEquals(1024.0, Operation.POWER.apply(2.0, 10.0), 0.001)
        assertEquals(Operation.ADD, Operation.fromSymbol("+"))
    }

    @Test
    fun testTask03ShippingCost() {
        assertEquals(0.0, shippingCost(Delivery.PICKUP, 10.0), 0.001)
        assertEquals(20000.0, shippingCost(Delivery.COURIER, 2.5), 0.001)
        assertEquals(45000.0, shippingCost(Delivery.EXPRESS, 1.0), 0.001)
        assertEquals(28000.0, shippingCost(Delivery.DRONE, 1.0), 0.001)
    }

    @Test
    fun testTask04PaymentTypes() {
        val cc: Payment = Payment.CreditCard("8600123412341234", "09/28")
        val cash: Payment = Payment.Cash
        val crypto = task05.Payment.Crypto("W123")
        assertNotNull(cc)
        assertNotNull(cash)
        assertNotNull(crypto)
    }

    @Test
    fun testTask06UiState() {
        val tashkent = loadWeather("tashkent")
        assertTrue(tashkent is UiState.Success)
        assertEquals(24, (tashkent as UiState.Success).temperature)

        val offline = loadWeather("offline")
        assertTrue(offline is UiState.Offline)
    }

    @Test
    fun testTask07ApiRequests() {
        val loginRes = handleRequest(LoginRequest("ali", "1234"))
        assertTrue(loginRes is ApiResponse.UserSuccess)

        val logoutRes = handleRequest(LogoutRequest)
        assertTrue(logoutRes is ApiResponse.UserSuccess)

        val refreshRes = handleRequest(RefreshRequest("token123"))
        assertTrue(refreshRes is ApiResponse.Error)
    }

    @Test
    fun testTask08Validation() {
        val validErrors = validate("Ali", "25", "ali@mail.uz")
        assertTrue(validErrors.isEmpty())

        val invalidErrors = validate("", "abc", "xyz")
        assertEquals(3, invalidErrors.size)
    }

    @Test
    fun testTask09Incident() {
        val incident: Incident = Incident.DatabaseDown("users")
        assertEquals(Severity.CRITICAL, incident.severity)
        assertEquals("Database users is down", describe(incident))
    }

    @Test
    fun testTask10PlayerCommands() {
        assertEquals(Basic.PLAY, parse("play"))
        assertEquals(Seek(90), parse("seek 90"))
        assertEquals(Volume(80), parse("volume 80"))

        val player = Player()
        player.handle(Basic.PLAY)
    }

    @Test
    fun testTask11Devices() {
        val phone = create("phone")
        assertTrue(phone is Phone)

        val note = chargingNote(phone as Phone)
        assertEquals("Charge the phone every night", note)
    }

    @Test
    fun testTask12ErrorHierarchy() {
        val missingErr = createError("missing file.txt")
        assertTrue(missingErr is IoError.FileMissing)

        val timeoutErr = createError("timeout")
        assertTrue(timeoutErr is Timeout)

        val diskErr = createError("disk 12")
        assertTrue(diskErr is DiskFull)

        val quotaErr = createError("quota 5")
        assertTrue(quotaErr is QuotaExceeded)
    }

    @Test
    fun testTask13Fare() {
        val baby = Passenger.Child("Dilnoza", 2)
        val child = Passenger.Child("Vali", 9)
        val adult = Passenger.Adult("Ali")
        val senior = Passenger.Senior("Karim", 70)
        val superSenior = Passenger.Senior("Boti", 85)

        assertEquals(0, fare(baby, 2000))
        assertEquals(1000, fare(child, 2000))
        assertEquals(2000, fare(adult, 2000))
        assertEquals(1400, fare(senior, 2000))
        assertEquals(0, fare(superSenior, 2000))
    }

    @Test
    fun testTask14Outcome() {
        val okQty = parseQuantity("3")
        assertTrue(okQty is Outcome.Ok)
        assertEquals(3, (okQty as Outcome.Ok).value)

        val failPrice = parsePrice("-1")
        assertTrue(failPrice is Outcome.Fail)

        val okEmail = parseEmail("test@mail.com")
        assertTrue(okEmail is Outcome.Ok)
    }

    @Test
    fun testTask15SupportDesk() {
        tickets.clear()
        execute(Open("Printer broken", Priority.URGENT))
        assertEquals(1, tickets.size)
        assertEquals("Printer broken", tickets[0].title)

        execute(Comment(1, "Checked cable"))
        assertEquals(1, tickets[0].comments.size)

        execute(Close(1))
        assertFalse(tickets[0].isOpen)

        execute(Reopen(1))
        assertTrue(tickets[0].isOpen)

        execute(ListAll)
    }
}
