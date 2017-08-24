package com.scrawlsoft.brailler;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.widget.EditText;

import com.jakewharton.rxbinding2.view.RxView;

import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RxView.touches(findViewById(R.id.dot6)).map(new Function<Object, Object>() {

            @Override
            public Object apply(@NonNull Object o) throws Exception {
                MotionEvent evt = (MotionEvent) o;

                System.out.println(evt.getAction());
                return new Integer(3);
            }
        }).subscribe(new Consumer<Object>() {
            @Override
            public void accept(Object o) throws Exception {
//                System.out.println(o);
            }
        });
    }
}
