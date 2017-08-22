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

        Cell cellF = new Cell().dot1().dot2().dot4();
        Cell cellO = new Cell().dot1().dot3().dot5();

        String foo = "FOO: " + cellF.getCodePoint() + cellO.getCodePoint() + cellO.getCodePoint();
        editText.setText(foo);
    }
}
