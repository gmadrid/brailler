package com.scrawlsoft.brailler;

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
 */
public class Cell {
    private static final char BRAILLE_PATTERN_BLANK = '\u2800';
    public static final int DOT_1 = 0x01;
    public static final int DOT_2 = 0x02;
    public static final int DOT_3 = 0x04;
    public static final int DOT_4 = 0x08;
    public static final int DOT_5 = 0x10;
    public static final int DOT_6 = 0x20;
    public static final int DOT_7 = 0x40;
    public static final int DOT_8 = 0x80;

    private char dots = 0;

    public Cell() {}

    public char getCodePoint() {
        return (char) (dots + BRAILLE_PATTERN_BLANK);
    }
    public int getValue() { return dots; }

    public Cell dot1() { this.dots |= DOT_1; return this; }
    public Cell dot2() { this.dots |= DOT_2; return this; }
    public Cell dot3() { this.dots |= DOT_3; return this; }
    public Cell dot4() { this.dots |= DOT_4; return this; }
    public Cell dot5() { this.dots |= DOT_5; return this; }
    public Cell dot6() { this.dots |= DOT_6; return this; }
    public Cell dot7() { this.dots |= DOT_7; return this; }
    public Cell dot8() { this.dots |= DOT_8; return this; }

}
