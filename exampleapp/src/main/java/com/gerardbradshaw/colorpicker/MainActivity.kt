package com.gerardbradshaw.colorpicker

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import kotlin.math.roundToInt

class MainActivity : AppCompatActivity() {

  val sliderMax = 16777216

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
  }

  fun buttonClicked(view: View) {
    val activityToLaunch = when (view.id) {
      R.id.compact_example_button -> CompactViewActivity::class.java
      R.id.square_example_button -> SquareViewActivity::class.java
      R.id.round_example_button -> RadialViewActivity::class.java
      else -> return
    }

    startActivity(Intent(this, activityToLaunch))
  }

  fun params(): Collection<Array<Any>> {
    val inputParams = Array<Any>(7) {
      val colorProgress = (it * sliderMax.toDouble() / 6.0).roundToInt()

      val shadeProgress = (sliderMax - colorProgress)

      val tintProgress =
        when (it) {
          0 -> 0
          6 -> ((sliderMax.toDouble() / 6.0)).roundToInt()
          else -> ((sliderMax.toDouble() / 6.0) + shadeProgress).roundToInt()
        }

      InputParams(colorProgress, shadeProgress, tintProgress)
    }

    val pureColors: Array<Int> =
      arrayOf(-65536, -256, -16711936, -16711681, -16776961, -65281, -65535)

    val expectedOutputs = Array<Any>(7) {
      OutputColors(pureColors[it], inputParams[it] as InputParams)
    }

    return Array(7) {
      arrayOf(inputParams[it], expectedOutputs[it])
    }.asList()
  }

  companion object {
    private const val TAG = "MainActivity"
  }
}