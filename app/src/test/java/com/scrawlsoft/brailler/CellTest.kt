package com.scrawlsoft.brailler

import org.junit.Test

import org.junit.Assert.*

class CellTest {
    @Test
    fun basicConstructor() {
        val cell26 = Cell(0b00100010)
        assertEquals(0b00100010.toShort(), cell26.cellValue)

        val cell3458 = Cell(0b10011100)
        assertEquals(0b10011100.toShort(), cell3458.cellValue)
    }

    @Test
    fun basicBuilder() {
        val cell13 = Cell.build().dot1().dot3().toCell()
        assertEquals(0b00000101.toShort(), cell13.cellValue)

        val cell12345678 = Cell.build().dot1().dot2().dot3().dot4().dot5().dot6().dot7().dot8().toCell()
        assertEquals(0b11111111.toShort(), cell12345678.cellValue)
    }

    @Test
    fun getCodePoint() {
        val cellA = Cell.build().dot1().toCell()
        assertEquals('⠁', cellA.codePoint)

        var cellG = Cell.build().dot1().dot2().dot4().dot5().toCell()
        assertEquals('⠛', cellG.codePoint)
    }
}