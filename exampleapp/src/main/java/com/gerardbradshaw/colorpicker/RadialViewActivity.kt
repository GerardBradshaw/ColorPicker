package com.gerardbradshaw.colorpicker

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.LayerDrawable
import android.os.Bundle
import android.view.View
import android.view.ViewTreeObserver
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory
import androidx.core.graphics.drawable.toBitmap
import com.gerardbradshaw.library.AbstractColorPicker
import kotlin.math.min

class RadialViewActivity : AppCompatActivity(), AbstractColorPicker.ColorChangedListener {

  private lateinit var picker: AbstractColorPicker
  private lateinit var listener: View

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_radial_view)

    picker = findViewById(R.id.ex_radial_view)
    picker.setOnColorSelectedListener(this)

    listener = findViewById(R.id.ex_radial_listener)
  }


  override fun onColorChanged(hexColor: Int) {
    listener.setBackgroundColor(hexColor)
  }
}