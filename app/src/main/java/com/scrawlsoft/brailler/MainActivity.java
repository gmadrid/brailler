package com.scrawlsoft.brailler;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        EditText editText = (EditText) findViewById(R.id.textThing);
        editText.setText("GEORGE");

        Cell cellF = new Cell((char) (Cell.DOT_1 | Cell.DOT_2 | Cell.DOT_4));
        Cell cellO = new Cell((char) (Cell.DOT_1 | Cell.DOT_3 | Cell.DOT_5));

        String foo = "FOO: " + cellF.getCodePoint() + cellO.getCodePoint() + cellO.getCodePoint();
        editText.setText(foo);

    }
}
