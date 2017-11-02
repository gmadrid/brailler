package com.scrawlsoft.brailler

import io.reactivex.Observable
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.subjects.PublishSubject
import org.junit.Assert.*
import org.junit.Test
import kotlin.experimental.or

class BraillerTest {

    private var dot1 = PublishSubject.create<Boolean>()
    private var dot2 = PublishSubject.create<Boolean>()
    private var dot3 = PublishSubject.create<Boolean>()
    private var dot4 = PublishSubject.create<Boolean>()
    private var dot5 = PublishSubject.create<Boolean>()
    private var dot6 = PublishSubject.create<Boolean>()

    private var outputCell: Cell? = null
    private var outputCount = 0

    private fun makeTestBrailler(): Brailler {
        val dotList = arrayOf<Observable<Boolean>>(
                dot1, dot2, dot3, dot4, dot5, dot6
        )

        val brailler = Brailler(dotList)
        brailler.cellOutput.subscribeBy {
            outputCell = it
            outputCount++
        }
        return brailler
    }

    private fun type(vararg dots: PublishSubject<Boolean>) {
        on(*dots)
        off(*dots)
    }

    private fun on(vararg dots: PublishSubject<Boolean>) {
        setDots(true, *dots)
    }

    private fun off(vararg dots: PublishSubject<Boolean>) {
        setDots(false, *dots)
    }

    private fun setDots(value: Boolean, vararg dots: PublishSubject<Boolean>) {
        for (dot in dots) {
            dot.onNext(value)
        }
    }

    @Test
    fun createValidBrailler() {
        val observables = arrayOf(
                Observable.just(true),
                Observable.just(true),
                Observable.just(true),
                Observable.just(true),
                Observable.just(true),
                Observable.just(true)
        )
        Brailler(observables)
    }

    @Test
    fun correctNumberOfLeds() {
        val brailler = makeTestBrailler()
        assertEquals(6, brailler.ledState.size)
    }


    @Test(expected = java.lang.IllegalArgumentException::class)
    fun createFailsWithBadArraySize() {
        val observables = arrayOf(
                Observable.just(true),
                Observable.just(true),
                Observable.just(true),
                Observable.just(true),
                Observable.just(true)
        )
        Brailler(observables)

    }

    /*
     * A very simple test case that checks behavior with a single key switch.
     */
    @Test
    fun simpleA() {
        makeTestBrailler()
        assertNull(outputCell)

        // Simulate pushing and releasing dot 1.
        on(dot1)
        off(dot1)
        assertNotNull(outputCell)

        // cellOutput should be a cell with only dot 1 set.
        assertEquals(Cell.DOT1, outputCell?.cellValue)
    }

    @Test
    fun simpleGWithSequencingTests() {
        makeTestBrailler()
        assertNull(outputCell)

        // Simulate pushing and releasing dots 1, 2, 4, 5.
        // Make sure that there is no cellOutput until the first released key.
        on(dot1)
        assertNull(outputCell)
        on(dot2)
        assertNull(outputCell)
        on(dot4)
        assertNull(outputCell)
        on(dot5)
        assertNull(outputCell)

        // Immediately on releasing a single key, we should get cellOutput.
        off(dot1)
        assertNotNull(outputCell)

        // Check that there is no new cell value as we release keys.
        val rememberedCell = outputCell
        dot2.onNext(false)
        assertEquals(rememberedCell, outputCell)
        dot3.onNext(false)
        assertEquals(rememberedCell, outputCell)
        dot4.onNext(false)
        assertEquals(rememberedCell, outputCell)

        // cellOutput should have dots 1, 2, 4, and 5 set.
        assertEquals(
                Cell.DOT1 or Cell.DOT2 or Cell.DOT4 or Cell.DOT5,
                outputCell?.cellValue)
    }

    @Test
    fun twoCells() {
        makeTestBrailler()

        // Simulate an 'e'
        type(dot1, dot5)
        val firstCell = outputCell

        // Simulate an 'i'
        type(dot2, dot4)
        val secondCell = outputCell

        assertEquals(outputCount.toLong(), 2)
        assertEquals((Cell.DOT1 or Cell.DOT5), firstCell?.cellValue)
        assertEquals((Cell.DOT2 or Cell.DOT4), secondCell?.cellValue)
    }

    @Test
    fun extraDownDuringReset() {
        makeTestBrailler()

        // Simulate an 'i' with an extra key hit in the middle of resetting
        dot2.onNext(true)
        dot4.onNext(true)
        dot4.onNext(false)
        dot5.onNext(true)
        dot2.onNext(false)
        dot5.onNext(false)

        assertEquals(Cell.DOT2 or Cell.DOT4, outputCell?.cellValue)
    }

    @Test
    fun testLedsOn() {
        val brailler = makeTestBrailler()

        // All of the dots
        val dots = arrayOf(dot1, dot2, dot3, dot4, dot5, dot6)

        // LEDs start off
        brailler.ledState.forEach { assertFalse(it.value) }
        on(*dots)
        // Turn on when keys are pressed
        brailler.ledState.forEach { assertTrue(it.value) }
        off(*dots)
        // Turn back off when released
        brailler.ledState.forEach { assertFalse(it.value) }
    }

    @Test
    fun testLedsOnUntilOutput() {
        var brailler = makeTestBrailler()

        // All of the dots
        val dots = arrayOf(dot1, dot2, dot3, dot4, dot5, dot6)

        // LEDs start off
        brailler.ledState.forEach { assertFalse(it.value) }

        // Press the dots in a 'd'.
        on(dot1, dot4, dot5)
        val ledVals = arrayOf(true, false, false, true, true, false)
        ledVals.zip(brailler.ledState) { expected, state ->
            assertEquals(expected, state.value)
        }

        // Let go of one button and make sure all LEDs stay on.
        off(dot1)
        ledVals.zip(brailler.ledState) { expected, state ->
            assertEquals(expected, state.value)
        }

        // Let go of the second one.
        off(dot4)
        ledVals.zip(brailler.ledState) { expected, state ->
            assertEquals(expected, state.value)
        }

        // When all keys are released, LEDs should clear.
        off(dot5)
        brailler.ledState.forEach { assertFalse(it.value) }
    }
}