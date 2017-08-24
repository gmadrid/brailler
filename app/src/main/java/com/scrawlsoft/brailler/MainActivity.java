package com.scrawlsoft.brailler;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.widget.EditText;
import android.widget.TextView;

import com.jakewharton.rxbinding2.view.RxView;

import io.reactivex.Observable;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Observable<Boolean>[] observables = new Observable[] {
                convertMotionToBooleanObservable(R.id.dot1),
                convertMotionToBooleanObservable(R.id.dot2),
                convertMotionToBooleanObservable(R.id.dot3),
                convertMotionToBooleanObservable(R.id.dot4),
                convertMotionToBooleanObservable(R.id.dot5),
                convertMotionToBooleanObservable(R.id.dot6)
        };

        Brailler brailler = new Brailler(observables);
        brailler.getOutput().subscribe(new Consumer<Cell>() {
            @Override
            public void accept(Cell cell) throws Exception {
                System.out.println(cell.getCodePoint());
                TextView textView = (TextView) findViewById(R.id.textView);
                textView.append("" + cell.getCodePoint());
            }
        });
    }

    final Observable<Boolean> convertMotionToBooleanObservable(int id) {
        return RxView.touches(findViewById(id)).filter(new Predicate<MotionEvent>() {
            @Override
            public boolean test(@NonNull MotionEvent motionEvent) throws Exception {
                // TODO: what about cancel??
                return motionEvent.getAction() == MotionEvent.ACTION_DOWN ||
                        motionEvent.getAction() == MotionEvent.ACTION_UP;
            }
        }).map(new Function<MotionEvent, Boolean>() {
            @Override
            public Boolean apply(@NonNull MotionEvent evt) throws Exception {
                return evt.getAction() == MotionEvent.ACTION_DOWN;
            }
        });
    }
}
