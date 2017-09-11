package com.scrawlsoft.brailler

import io.reactivex.Observable
import io.reactivex.rxkotlin.Observables
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.subjects.PublishSubject

class Brailler(switches: Array<Observable<Boolean>>) {
    val cellOutput: Observable<Cell>
    val ledOutput: Array<Observable<Boolean>>;

    private val cellOutputSubject: PublishSubject<Cell> = PublishSubject.create()
    private val ledOutputSubjects: Array<PublishSubject<Boolean>> = Array(6) { PublishSubject.create<Boolean>() }
    private var resetting: Boolean = false
    private var lastValue: Int = 0

    init {
        cellOutput = cellOutputSubject.hide()
        ledOutput = Array(ledOutputSubjects.size) { ledOutputSubjects[it].hide() }

        if (switches.size != 6) {
            throw IllegalArgumentException("Brailler only works with 6 switches.")
        }

        var value = 1
        val switchesWithValue = switches.map {
            val currentValue = value
            value *= 2;
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

    companion object {

        private fun switchWithValue(switch: Observable<Boolean>, value: Int): Observable<Int> {
            // Map the incoming Boolean to the supplied value (or 0 iff false). Prime it with 0.
            return switch.map {
                if (it) {
                    value
                } else {
                    0
                }
            }.startWith(0)
        }

//        private fun

    }
}
