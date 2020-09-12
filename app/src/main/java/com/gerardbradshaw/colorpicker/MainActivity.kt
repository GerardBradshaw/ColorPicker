package com.gerardbradshaw.colorpicker

import android.os.Bundle
import android.util.TypedValue
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
  }


  companion object {
    private const val TAG = "MainActivity"
  }
}