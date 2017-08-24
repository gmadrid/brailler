package com.scrawlsoft.brailler;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Function6;
import io.reactivex.subjects.PublishSubject;

/**
 * Translate six switch inputs into a braille Cell.
 * The six inputs correspond to the six embossing keys on a brailler.
 *
 * Each switch input is expected to have the following behavior:
 * - Switches between true and false. (May send duplicate values and the Brailler will deal.)
 * - Never fails or succeeds.
 *
 * The Brailler behavior
 * - dots are added to the current character until any switch turns off,
 * - when any switch turns off, the accumulated switches are aggregated into a single Cell output,
 * - all switches must be turned off before another Cell can be started.
 */
public class Brailler {
    private PublishSubject<Cell> cellOutputSubject = PublishSubject.create();

    private boolean resetting = false;
    private int lastValue = 0;

    public Brailler(Observable<Boolean>[] switches) {
        if (switches.length != 6) {
            throw new IllegalArgumentException("Brailler must get 6 switches.");
        }

        // Assign each switch to a value equivalent to its dot number's value.
        List<Observable<Integer>> switchesWithValues = new ArrayList<>(switches.length);
        int value = 1;
        for (Observable<Boolean> swtch : switches) {
            Observable<Integer> switchWithValue = switchWithValue(swtch, value);
            switchesWithValues.add(switchWithValue);
            value *= 2;
        }

        // Combine all of the dot values into a single cell value when the conditions are met.
        Observable.combineLatest(switchesWithValues.get(0),
                switchesWithValues.get(1),
                switchesWithValues.get(2),
                switchesWithValues.get(3),
                switchesWithValues.get(4),
                switchesWithValues.get(5),
                new Function6<Integer, Integer, Integer, Integer, Integer, Integer, Integer>() {
                    @Override
                    public Integer apply(@NonNull Integer dot1Value,
                                         @NonNull Integer dot2Value,
                                         @NonNull Integer dot3Value,
                                         @NonNull Integer dot4Value,
                                         @NonNull Integer dot5Value,
                                         @NonNull Integer dot6Value) throws Exception {
                        return dot1Value + dot2Value + dot3Value + dot4Value + dot5Value + dot6Value;
                    }
                })
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer combinedValue) throws Exception {
                        // The value will go down as soon as we release any key.
                        // If we are resetting, then never send a value.
                        if (!resetting && combinedValue < lastValue) {
                            // Send the value *before* releasing a key, and enter resetting state.
                            cellOutputSubject.onNext(new Cell(lastValue));
                            resetting = true;
                        }
                        if (!resetting) {
                            lastValue = combinedValue;
                        }

                        // As soon as all switches are released, start over.
                        if (combinedValue == 0) {
                            resetting = false;
                        }
                    }
                });



    }

    private static Observable<Integer> switchWithValue(Observable<Boolean> swtch, final int value) {
        return swtch.map(new Function<Boolean, Integer>() {
            @Override
            public Integer apply(@NonNull Boolean aBoolean) throws Exception {
                return aBoolean ? value : 0;
            }
        }).startWith(0);
    }

    public Observable<Cell> getOutput() { return cellOutputSubject.hide(); }
}
