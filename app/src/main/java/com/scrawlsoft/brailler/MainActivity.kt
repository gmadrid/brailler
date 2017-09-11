package com.scrawlsoft.brailler

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.MotionEvent
import android.widget.TextView
import com.jakewharton.rxbinding2.view.RxView
import io.reactivex.Observable
import io.reactivex.rxkotlin.subscribeBy

//  4. Make the red be 'sticky' until the Cell is produced.
//  5. Add delete, space, return buttons.
//  6. Disable delete, space, return, while an embossing key is pressed.
//  7. Disable embossing keys while delete, space, or return is pressed.
//  8. Haptic feedback when any key is pressed.
//  9. Make "switched" mode.
// 10. Dragable separator.
// 11. Scalable braille font
// 12. "Shadow" braille font (hard)
class MainActivity : AppCompatActivity() {

    companion object {
        val ledIds = arrayOf(R.id.circle1, R.id.circle2, R.id.circle3, R.id.circle4, R.id.circle5, R.id.circle6)
        val switchIds = arrayOf(R.id.dot1, R.id.dot2, R.id.dot3, R.id.dot4, R.id.dot5, R.id.dot6)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fun makeSwitch(id: Int): Observable<Boolean> {
            return RxView.touches(findViewById(id))
                    .filter { evt ->
                        // TODO: what about cancel?
                        evt.action == MotionEvent.ACTION_DOWN ||
                                evt.action == MotionEvent.ACTION_UP
                    }
                    .map { it.action == MotionEvent.ACTION_DOWN }
                    .share()
        }

        val ledViews = ledIds.map { findViewById(it) }
        val switches = switchIds.map { makeSwitch(it) }

        switches.zip(ledViews) { switch, ledView ->
            switch.subscribeBy { if (it) ledView.isPressed = true }
        }

        val brailler = Brailler(switches.toTypedArray())
        brailler.output.subscribeBy {
            val textView = findViewById(R.id.textView) as TextView
            textView.append("" + it.codePoint)

            ledViews.forEach { it.isPressed = false }
        }
    }
}