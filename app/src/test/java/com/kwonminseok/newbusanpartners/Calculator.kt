package com.kwonminseok.newbusanpartners

import junit.framework.TestCase.assertEquals
import org.junit.Test

class Calculator {
    fun add(a: Int, b: Int): Int {
        return a + b
    }
}

class CalculatorTest {

    @Test
    fun testAddition() {
        val calculator = Calculator()
        val result = calculator.add(2, 3)
        assertEquals(5, result)
    }
}
