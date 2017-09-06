package com.scrawlsoft.brailler;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.functions.Consumer;
import io.reactivex.subjects.PublishSubject;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;


public class BraillerTest {
    PublishSubject<Boolean> dot1 = PublishSubject.create();
    PublishSubject<Boolean> dot2 = PublishSubject.create();
    PublishSubject<Boolean> dot3 = PublishSubject.create();
    PublishSubject<Boolean> dot4 = PublishSubject.create();
    PublishSubject<Boolean> dot5 = PublishSubject.create();
    PublishSubject<Boolean> dot6 = PublishSubject.create();

    KCell outputCell;
    int outputCount = 0;

    KBrailler makeStandardBrailler() {
        List<Observable<Boolean>> dotList = new ArrayList<>();
        dotList.add(dot1);
        dotList.add(dot2);
        dotList.add(dot3);
        dotList.add(dot4);
        dotList.add(dot5);
        dotList.add(dot6);
        return new KBrailler(dotList.toArray(new Observable[0]));
    }

    @Test
    public void brailler_create_valid() throws Exception {
        List<Observable<Boolean>> observables = new ArrayList<>(6);
        for (int i = 0; i < 6; i++) {
            observables.add(Observable.just(true));
        }
        KBrailler brailler = new KBrailler(observables.toArray(new Observable[0]));
    }

    @Test(expected = java.lang.IllegalArgumentException.class)
    public void brailler_create_fails_bad_size() throws Exception {
        List<Observable<Boolean>> observables = new ArrayList<>(5);
        for (int i = 0; i < 5; i++) {
            observables.add(Observable.just(true));
        }
        KBrailler brailler = new KBrailler(observables.toArray(new Observable[0]));
    }


    /*
     * A very simple test case that checks behavior with a single key switch.
     */
    @Test
    public void brailler_simple_a() {
        KBrailler brailler = makeStandardBrailler();

        brailler.getOutput().subscribe(new Consumer<KCell>() {
            @Override
            public void accept(KCell cell) throws Exception {
                outputCell = cell;
            }});

        assertNull(outputCell);

        // Simulate pushing and releasing dot 1.
        dot1.onNext(true);
        dot1.onNext(false);
        assertNotNull(outputCell);

        // We should output a cell with only dot 1 set.
        assertEquals(KCell.DOT1, outputCell.toShort());
    }

    @Test
    public void brailler_simple_g() {
        KBrailler brailler = makeStandardBrailler();

        brailler.getOutput().subscribe(new Consumer<KCell>() {
            @Override
            public void accept(KCell cell) throws Exception {
                outputCell = cell;
            }});

        assertNull(outputCell);

        // Simulate pushing and releasing dots 1, 2, 4, 5.
        // Make sure that there is no output until the first released key.
        dot1.onNext(true);
        assertNull(outputCell);
        dot2.onNext(true);
        assertNull(outputCell);
        dot4.onNext(true);
        assertNull(outputCell);
        dot5.onNext(true);
        assertNull(outputCell);

        // Immediately on releasing a single key, we should get output.
        dot1.onNext(false);
        assertNotNull(outputCell);

        // Check that there is no new cell value as we release keys.
        KCell rememberedCell = outputCell;
        dot2.onNext(false);
        assertEquals(rememberedCell, outputCell);
        dot3.onNext(false);
        assertEquals(rememberedCell, outputCell);
        dot4.onNext(false);
        assertEquals(rememberedCell, outputCell);

        // We should output a cell with only dot 1 set.
        assertEquals(KCell.DOT1 | KCell.DOT2 | KCell.DOT4 | KCell.DOT5, outputCell.toShort());
    }

    @Test
    public void brailler_two_cells() {
        KBrailler brailler = makeStandardBrailler();

        brailler.getOutput().subscribe(new Consumer<KCell>() {
            @Override
            public void accept(KCell cell) throws Exception {
                outputCell = cell;
                outputCount++;
            }});

        // Simulate an 'e'
        dot1.onNext(true);
        dot5.onNext(true);
        dot1.onNext(false);
        dot5.onNext(false);
        KCell firstCell = outputCell;

        // Simulate an 'i' with an extra key hit in the middle of resetting.
        dot2.onNext(true);
        dot4.onNext(true);
        dot4.onNext(false);
        dot5.onNext(true);
        dot2.onNext(false);
        dot5.onNext(false);
        KCell secondCell = outputCell;

        assertEquals(outputCount, 2);
        assertEquals(KCell.DOT1 | KCell.DOT5, firstCell.toShort());
        assertEquals(KCell.DOT2 | KCell.DOT4, secondCell.toShort());
    }
}

