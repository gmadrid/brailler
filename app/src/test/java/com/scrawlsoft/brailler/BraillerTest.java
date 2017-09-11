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
    private PublishSubject<Boolean> dot1 = PublishSubject.create();
    private PublishSubject<Boolean> dot2 = PublishSubject.create();
    private PublishSubject<Boolean> dot3 = PublishSubject.create();
    private PublishSubject<Boolean> dot4 = PublishSubject.create();
    private PublishSubject<Boolean> dot5 = PublishSubject.create();
    private PublishSubject<Boolean> dot6 = PublishSubject.create();

    private Cell outputCell;
    private int outputCount = 0;

    private Brailler makeStandardBrailler() {
        List<Observable<Boolean>> dotList = new ArrayList<>();
        dotList.add(dot1);
        dotList.add(dot2);
        dotList.add(dot3);
        dotList.add(dot4);
        dotList.add(dot5);
        dotList.add(dot6);
        return new Brailler(dotList.toArray(new Observable[0]));
    }

    @Test
    public void brailler_create_valid() throws Exception {
        List<Observable<Boolean>> observables = new ArrayList<>(6);
        for (int i = 0; i < 6; i++) {
            observables.add(Observable.just(true));
        }
        new Brailler(observables.toArray(new Observable[0]));
    }

    @Test(expected = java.lang.IllegalArgumentException.class)
    public void brailler_create_fails_bad_size() throws Exception {
        List<Observable<Boolean>> observables = new ArrayList<>(5);
        for (int i = 0; i < 5; i++) {
            observables.add(Observable.just(true));
        }
        new Brailler(observables.toArray(new Observable[0]));
    }


    /*
     * A very simple test case that checks behavior with a single key switch.
     */
    @Test
    public void brailler_simple_a() {
        Brailler brailler = makeStandardBrailler();

        brailler.getCellOutput().subscribe(new Consumer<Cell>() {
            @Override
            public void accept(Cell cell) throws Exception {
                outputCell = cell;
            }});

        assertNull(outputCell);

        // Simulate pushing and releasing dot 1.
        dot1.onNext(true);
        dot1.onNext(false);
        assertNotNull(outputCell);

        // We should cellOutput a cell with only dot 1 set.
        assertEquals(Cell.DOT1, outputCell.toShort());
    }

    @Test
    public void brailler_simple_g() {
        Brailler brailler = makeStandardBrailler();

        brailler.getCellOutput().subscribe(new Consumer<Cell>() {
            @Override
            public void accept(Cell cell) throws Exception {
                outputCell = cell;
            }});

        assertNull(outputCell);

        // Simulate pushing and releasing dots 1, 2, 4, 5.
        // Make sure that there is no cellOutput until the first released key.
        dot1.onNext(true);
        assertNull(outputCell);
        dot2.onNext(true);
        assertNull(outputCell);
        dot4.onNext(true);
        assertNull(outputCell);
        dot5.onNext(true);
        assertNull(outputCell);

        // Immediately on releasing a single key, we should get cellOutput.
        dot1.onNext(false);
        assertNotNull(outputCell);

        // Check that there is no new cell value as we release keys.
        Cell rememberedCell = outputCell;
        dot2.onNext(false);
        assertEquals(rememberedCell, outputCell);
        dot3.onNext(false);
        assertEquals(rememberedCell, outputCell);
        dot4.onNext(false);
        assertEquals(rememberedCell, outputCell);

        // We should cellOutput a cell with only dot 1 set.
        assertEquals(Cell.DOT1 | Cell.DOT2 | Cell.DOT4 | Cell.DOT5, outputCell.toShort());
    }

    @Test
    public void brailler_two_cells() {
        Brailler brailler = makeStandardBrailler();

        brailler.getCellOutput().subscribe(new Consumer<Cell>() {
            @Override
            public void accept(Cell cell) throws Exception {
                outputCell = cell;
                outputCount++;
            }});

        // Simulate an 'e'
        dot1.onNext(true);
        dot5.onNext(true);
        dot1.onNext(false);
        dot5.onNext(false);
        Cell firstCell = outputCell;

        // Simulate an 'i' with an extra key hit in the middle of resetting.
        dot2.onNext(true);
        dot4.onNext(true);
        dot4.onNext(false);
        dot5.onNext(true);
        dot2.onNext(false);
        dot5.onNext(false);
        Cell secondCell = outputCell;

        assertEquals(outputCount, 2);
        assertEquals(Cell.DOT1 | Cell.DOT5, firstCell.toShort());
        assertEquals(Cell.DOT2 | Cell.DOT4, secondCell.toShort());
    }

    @Test
    public void correct_number_leds() {
        Brailler brailler = makeStandardBrailler();

        assertEquals(6, brailler.getLedOutput().length);
    }
}

