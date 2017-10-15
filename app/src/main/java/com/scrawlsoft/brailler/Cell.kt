package com.scrawlsoft.brailler

/**
 * A braille cell has 6 (or 8) dots which can be easily encoded into a single byte.
 * We choose to use the same bit correspondences as the Unicode Braille Patterns
 * since why not? This also means that we can convert easily to the Unicode character for a cell
 * by simple adding the dot values to U+2800.
 *
 *      1  (0x1) o  o 4 (0x8)
 *      2  (0x2) o  o 5 (0x10)
 *      3  (0x4) o  o 6 (0x20)
 *      7 (0x40) o  o 8 (0x80)
 *
 *  So, for example, a Braille 'e', encoded as dots 1,5, would be 0x01 + 0x10 = 0x11.
 *
 *  You may also use the Builder, so a Braille 'e' would be:
 *
 *    Cell.build().dot1().dot5().toCell()
 *
 *  Once instantiated, the Cell is immutable.
 */
data class Cell(private val dotsAsBits: Short) {
    val codePoint: Char
        get() = (BRAILLE_PATTERN_BLANK + dotsAsBits).toChar()

    fun toShort(): Short = dotsAsBits

    class Builder internal constructor() {

        private var bits: Short = 0

        fun dot1(): Builder { bits = bits.toInt().or(DOT1.toInt()).toShort(); return this }
        fun dot2(): Builder { bits = bits.toInt().or(DOT2.toInt()).toShort(); return this }
        fun dot3(): Builder { bits = bits.toInt().or(DOT3.toInt()).toShort(); return this }
        fun dot4(): Builder { bits = bits.toInt().or(DOT4.toInt()).toShort(); return this }
        fun dot5(): Builder { bits = bits.toInt().or(DOT5.toInt()).toShort(); return this }
        fun dot6(): Builder { bits = bits.toInt().or(DOT6.toInt()).toShort(); return this }
        fun dot7(): Builder { bits = bits.toInt().or(DOT7.toInt()).toShort(); return this }
        fun dot8(): Builder { bits = bits.toInt().or(DOT8.toInt()).toShort(); return this }

        fun toCell() = Cell(bits.toShort())
    }

    companion object {
        private const val BRAILLE_PATTERN_BLANK: Short = '\u2800'.toShort();
        const val DOT1: Short = 0x01
        const val DOT2: Short = 0x02
        const val DOT3: Short = 0x04
        const val DOT4: Short = 0x08
        const val DOT5: Short = 0x10
        const val DOT6: Short = 0x20
        const val DOT7: Short = 0x40
        const val DOT8: Short = 0x80

        fun build() : Builder {
            return Builder()
        }
    }
}

