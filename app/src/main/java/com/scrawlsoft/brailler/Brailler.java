package com.scrawlsoft.brailler;

import io.reactivex.Observable;
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


    }

    public Observable<Cell> getOutput() { return cellOutputSubject.hide(); }


}
