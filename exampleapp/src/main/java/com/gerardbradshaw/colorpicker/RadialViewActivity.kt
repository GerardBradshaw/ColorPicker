package com.gerardbradshaw.colorpicker

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.gerardbradshaw.colorpickerlibrary.views.AbstractColorPickerView

class RadialViewActivity : AppCompatActivity(), AbstractColorPickerView.ColorChangedListener {

  private lateinit var picker: AbstractColorPickerView
  private lateinit var listener: View

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_radial_view)

    picker = findViewById(R.id.ex_radial_view)
    picker.setOnColorSelectedListener(this)

    listener = findViewById(R.id.ex_radial_listener)
  }


  override fun onColorChanged(color: Int) {
    listener.setBackgroundColor(color)
  }
}