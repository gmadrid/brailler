package com.scrawlsoft.brailler

import org.junit.Test

import org.junit.Assert.*

class KCellTest {
    @Test
    fun basicConstructor() {
        val cell26 = KCell(0b00100010)
        assertEquals(0b00100010.toShort(), cell26.toShort())

        val cell3458 = KCell(0b10011100)
        assertEquals(0b10011100.toShort(), cell3458.toShort())
    }

    @Test
    fun basicBuilder() {
        val cell13 = KCell.new().dot1().dot3().toCell()
        assertEquals(0b00000101.toShort(), cell13.toShort())

        val cell12345678 = KCell.new().dot1().dot2().dot3().dot4().dot5().dot6().dot7().dot8().toCell()
        assertEquals(0b11111111.toShort(), cell12345678.toShort())
    }

    @Test
    fun getCodePoint() {
        val cellA = KCell.new().dot1().toCell()
        assertEquals('⠁', cellA.codePoint)

        var cellG = KCell.new().dot1().dot2().dot4().dot5().toCell()
        assertEquals('⠛', cellG.codePoint)
    }
}