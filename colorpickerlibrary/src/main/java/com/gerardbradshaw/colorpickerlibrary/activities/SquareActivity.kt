package com.gerardbradshaw.colorpickerlibrary.activities

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.gerardbradshaw.colorpickerlibrary.R
import com.gerardbradshaw.colorpickerlibrary.views.AbstractColorPickerView

internal class SquareActivity : AppCompatActivity(), AbstractColorPickerView.ColorChangedListener {

  private lateinit var picker: AbstractColorPickerView
  private lateinit var listener: View

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.color_picker_library_activity_square_example)

    picker = findViewById(R.id.color_picker_library_example_square_picker)
    listener = findViewById(R.id.color_picker_library_example_listener)

    picker.setOnColorSelectedListener(this)
  }

  override fun onColorChanged(color: Int) {
    listener.setBackgroundColor(color)
  }
}