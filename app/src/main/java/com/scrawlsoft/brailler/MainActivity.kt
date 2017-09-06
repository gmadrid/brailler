package com.scrawlsoft.brailler

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.MotionEvent
import android.widget.TextView
import com.jakewharton.rxbinding2.view.RxView
import io.reactivex.Observable
import io.reactivex.rxkotlin.subscribeBy

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fun makeSwitch(id: Int) : Observable<Boolean> {
            return RxView.touches(findViewById(id))
                    .filter { evt ->
                        // TODO: what about cancel?
                        evt.action == MotionEvent.ACTION_DOWN ||
                                evt.action == MotionEvent.ACTION_UP
                        }
                    .map { it.action == MotionEvent.ACTION_DOWN }
        }

        val switches = arrayOf(
                makeSwitch(R.id.dot1),
                makeSwitch(R.id.dot2),
                makeSwitch(R.id.dot3),
                makeSwitch(R.id.dot4),
                makeSwitch(R.id.dot5),
                makeSwitch(R.id.dot6))

        val brailler = Brailler(switches)
        brailler.output.subscribeBy {
            val textView = findViewById(R.id.textView) as TextView
            textView.append("" + it.codePoint)
        }
    }
}