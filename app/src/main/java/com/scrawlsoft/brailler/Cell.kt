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
 *    Cell.new().dot1().dot5().toCell()
 *
 *  Once instantiated, the Cell is immutable.
 */
data class Cell(private val dotsAsBits: Short) {
    val codePoint: Char
        get() = (BRAILLE_PATTERN_BLANK + dotsAsBits).toChar()

    fun toShort(): Short = dotsAsBits

    class Builder internal constructor() {

        private var bits: Int = 0

        fun dot1(): Builder { bits = bits.or(DOT1); return this }
        fun dot2(): Builder { bits = bits.or(DOT2); return this }
        fun dot3(): Builder { bits = bits.or(DOT3); return this }
        fun dot4(): Builder { bits = bits.or(DOT4); return this }
        fun dot5(): Builder { bits = bits.or(DOT5); return this }
        fun dot6(): Builder { bits = bits.or(DOT6); return this }
        fun dot7(): Builder { bits = bits.or(DOT7); return this }
        fun dot8(): Builder { bits = bits.or(DOT8); return this }

        fun toCell() = Cell(bits.toShort())
    }

    companion object {
        private const val BRAILLE_PATTERN_BLANK: Short = '\u2800'.toShort();
        const val DOT1: Int = 0x01
        const val DOT2: Int = 0x02
        const val DOT3: Int = 0x04
        const val DOT4: Int = 0x08
        const val DOT5: Int = 0x10
        const val DOT6: Int = 0x20
        const val DOT7: Int = 0x40
        const val DOT8: Int = 0x80

        fun new() : Builder {
            return Builder()
        }
    }
}

