package com.gerardbradshaw.colorpicker

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

  private lateinit var textView: TextView

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

//    val slider: ColorSliderView = findViewById(R.id.my_slider)
//    slider.setOnColorSelectedListener(this)
//
//    slider.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
//      override fun onGlobalLayout() {
//        if(slider.height > 0) {
//          Log.d(TAG, "onCreate: slider width = ${slider.width}")
//          slider.viewTreeObserver.removeOnGlobalLayoutListener(this)
//        }
//      }
//    })
//
//    Log.d(TAG, "onCreate: slider width = ${slider.width}")
  }


  companion object {
    private const val TAG = "MainActivity"
  }

//  override fun onColorChanged(color: Int) {
//    val hexColor = String.format("#%06X", 0xFFFFFF and color)
//    textView.text = hexColor
//    textView.setBackgroundColor(Color.parseColor(hexColor))
//  }
}