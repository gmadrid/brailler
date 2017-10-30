package com.scrawlsoft.brailler

import io.reactivex.Observable
import io.reactivex.rxkotlin.Observables
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.subjects.PublishSubject

/**
 * Emulated a Perkins Brailler with some extra electronic features.
 *
 * Inputs:
 *   Embossing switches (1-6).
 *   Return switch
 *   Backspace switch
 *   Space switch
 *
 * Outputs:
 *   Cell: the Cell that will be "embossed".
 *   Embossing enabled: whether the embossing switches are enabled
 *   Others enabled: whether the return, backspace, and space switches are enabled.
 *   LEDs: 6 LED lights, one for each embossing switch.
 */
class Brailler(switches: Array<Observable<Boolean>>) {
    val output: Observable<Cell>
        get() = cellOutputSubject.hide()

    private val cellOutputSubject: PublishSubject<Cell> = PublishSubject.create()

    /*
     * Behaviors
     *   - START mode
     *     - On entering START mode, turn off all LEDs
     *     - Once any embossing switch is pressed, we enter EMBOSSING mode.
     *   - EMBOSSING mode
     *     - Hitting any more embossing switches
     *       - add those dots to the final character.
     *       - turn on the associated LED
     *     - Releasing any embossing switch enters RESET mode
     *   - RESET mode
     *     - ignore all switches until no switches are pressed.
     *     - when no switches are pressed, enter START mode
     */

    val cellOutput: Observable<Cell>
    val ledOutput: Array<Observable<Boolean>>

    private enum class Mode {
        START,
        EMBOSSING,
        RESET
    }

    private val cellOutputSubject: PublishSubject<Cell> = PublishSubject.create()
    private val ledOutputSubjects: Array<PublishSubject<Boolean>> =
            Array(6) { PublishSubject.create<Boolean>() }

    private var mode: Mode = Mode.START
        set(value) {
            when (value) {
                Mode.EMBOSSING -> {
                    ledOutputSubjects.forEach { it.onNext(false) }
                }
                else -> {}
            }
        }

    private var resetting: Boolean = false
    private var lastValue: Int = 0

    init {
        if (switches.size != 6) {
            throw IllegalArgumentException("Brailler only works with 6 switches.")
        }

        fun switchWithValue(switch: Observable<Boolean>, value: Int): Observable<Int> {
            // Map the incoming Boolean to the supplied value (or 0 iff false). Prime it with 0.
            return switch.map {
                if (it) {
                    value
                } else {
                    0
                }
            }.startWith(0)
        }

        var value = 1
        val switchesWithValue = switches.map {
            val currentValue = value
            value *= 2
            switchWithValue(it, currentValue)
        }

        Observables.combineLatest(
                switchesWithValue[0],
                switchesWithValue[1],
                switchesWithValue[2],
                switchesWithValue[3],
                switchesWithValue[4],
                switchesWithValue[5],
                { i1, i2, i3, i4, i5, i6 -> i1 + i2 + i3 + i4 + i5 + i6 })
                .subscribeBy(onNext = { combinedValue ->
                    // The value will go down as soon as we release any key.
                    // If we are resetting, then never send a value.
                    if (!resetting && combinedValue < lastValue) {
                        // Send the value *before* releasing a key, and enter resetting state.
                        // cellOutputSubject foo
                        cellOutputSubject.onNext(Cell(lastValue.toShort()))
                        resetting = true
                    }
                    if (!resetting) {
                        lastValue = combinedValue
                    }
                    // As soon as all switches are released, start over.
                    if (combinedValue == 0) {
                        resetting = false
                        lastValue = 0
                    }
                })
    }
}
