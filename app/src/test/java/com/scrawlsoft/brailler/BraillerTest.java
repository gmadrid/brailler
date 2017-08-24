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

    Cell outputCell;

    Brailler makeStandardBrailler() {
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
        Brailler brailler = new Brailler(observables.toArray(new Observable[0]));
    }

    @Test(expected = java.lang.IllegalArgumentException.class)
    public void brailler_create_fails_bad_size() throws Exception {
        List<Observable<Boolean>> observables = new ArrayList<>(5);
        for (int i = 0; i < 5; i++) {
            observables.add(Observable.just(true));
        }
        Brailler brailler = new Brailler(observables.toArray(new Observable[0]));
    }

    @Test
    public void brailler_simple_a() {
        Brailler brailler = makeStandardBrailler();

        brailler.getOutput().subscribe(new Consumer<Cell>() {
            @Override
            public void accept(Cell cell) throws Exception {
                outputCell = cell;
                System.out.println(cell);
            }});

        assertNull(outputCell);

        // Simulate pushing and releasing dot 1.
        dot1.onNext(true);
        dot1.onNext(false);
        assertNotNull(outputCell);

        // We should output a cell with only dot 1 set.
        assertEquals(Cell.DOT_1, outputCell.getValue());
    }

    @Test
    public void brailler_simple_g() {
        Brailler brailler = makeStandardBrailler();

        brailler.getOutput().subscribe(new Consumer<Cell>() {
            @Override
            public void accept(Cell cell) throws Exception {
                outputCell = cell;
                System.out.println(cell);
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

        // We should output a cell with only dot 1 set.
        assertEquals(Cell.DOT_1 | Cell.DOT_2 | Cell.DOT_4 | Cell.DOT_5, outputCell.getValue());
    }
}

