package com.scrawlsoft.brailler;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.annotations.NonNull;
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

    public Brailler(Observable<Boolean>[] switches) {
        if (switches.length != 6) {
            throw new IllegalArgumentException("Brailler must get 6 switches.");
        }

        List<Observable<Integer>> switchesWithValues = new ArrayList<>(switches.length);
        int value = 1;
        for (Observable<Boolean> swtch : switches) {
            Observable<Integer> switchWithValue = switchWithValue(swtch, value);
            switchesWithValues.add(switchWithValue);
            value *= 2;
        }

        Observable.combineLatest(switchesWithValues.get(0),
                switchesWithValues.get(1),
                switchesWithValues.get(2),
                switchesWithValues.get(3),
                switchesWithValues.get(4),
                switchesWithValues.get(5),
                new Function6<Integer, Integer, Integer, Integer, Integer, Integer, Integer>() {
                    @Override
                    public Integer apply(@NonNull Integer integer, @NonNull Integer integer2, @NonNull Integer integer3, @NonNull Integer integer4, @NonNull Integer integer5, @NonNull Integer integer6) throws Exception {
                        return integer + integer2 + integer3 + integer4 + integer5 + integer6;
                    }
                });
    }

    private static Observable<Integer> switchWithValue(Observable<Boolean> swtch, final int value) {
        return swtch.map(new Function<Boolean, Integer>() {
            @Override
            public Integer apply(@NonNull Boolean aBoolean) throws Exception {
                return aBoolean ? value : 0;
            }
        });
    }

    public Observable<Cell> getOutput() { return cellOutputSubject.hide(); }


}
