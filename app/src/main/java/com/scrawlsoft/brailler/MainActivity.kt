package com.scrawlsoft.brailler

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.MotionEvent
import android.widget.TextView
import com.jakewharton.rxbinding2.view.RxView
import io.reactivex.Observable
import io.reactivex.rxkotlin.subscribeBy

//  3. Make the buttons turn red when pushed.
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
                    .share()
        }

        val switches = arrayOf(
                makeSwitch(R.id.dot1),
                makeSwitch(R.id.dot2),
                makeSwitch(R.id.dot3),
                makeSwitch(R.id.dot4),
                makeSwitch(R.id.dot5),
                makeSwitch(R.id.dot6))

        switches[0].subscribeBy { findViewById(R.id.circle1).isPressed = it }
        switches[1].subscribeBy { findViewById(R.id.circle2).isPressed = it }
        switches[2].subscribeBy { findViewById(R.id.circle3).isPressed = it }
        switches[3].subscribeBy { findViewById(R.id.circle4).isPressed = it }
        switches[4].subscribeBy { findViewById(R.id.circle5).isPressed = it }
        switches[5].subscribeBy { findViewById(R.id.circle6).isPressed = it }

        val brailler = Brailler(switches)
        brailler.output.subscribeBy {
            val textView = findViewById(R.id.textView) as TextView
            textView.append("" + it.codePoint)
        }
    }
}