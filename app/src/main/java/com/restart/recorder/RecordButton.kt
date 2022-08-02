package com.restart.recorder

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageButton


//AppCompatImageButton(Context,AttributeSet)을 상속받아 xml에서 사용할 수 있는 RecordButton클래스로 만들기.
class RecordButton(
    context: Context,
    attrs: AttributeSet
): AppCompatImageButton(context, attrs) {

    init {
        setBackgroundResource(R.drawable.shape_oval_button)
    }

    fun updateIconWithState(state: State){ //enum class로 만든 State 인자로 받기
        when(state) {
            State.BEFORE_RECORDING -> { //녹음전 상태일 때 보여질 이미지
                setImageResource(R.drawable.ic_record)
            }
            State.ON_RECORDING -> { //녹음 중일때 보여질 이미지
                setImageResource(R.drawable.ic_stop)
            }
            State.AFTER_RECORDING -> { // 녹음 종료 후 보여질 이미지
                setImageResource(R.drawable.ic_play)
            }
            State.ON_PLAYING -> { // 녹음 재생시 보여이미 이미지
                setImageResource(R.drawable.ic_stop)
            }
        }
    }

}