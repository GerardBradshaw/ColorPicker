package com.gerardbradshaw.exampleapp.testutil

import android.graphics.Color
import kotlin.math.max
import kotlin.math.roundToInt

internal class ParamTestOutput {

  // -------------------- PROPERTIES --------------------
  val pureColor: Int
  val shadedColor: Int
  val tintedColor: Int
  val shadedAndTintedColor: Int



  // -------------------- CONSTRUCTORS --------------------

  constructor(pureColor: Int, shadedColor: Int, tintedColor: Int, shadedAndTintedColor: Int) {
    this.pureColor = pureColor
    this.shadedColor = shadedColor
    this.tintedColor = tintedColor
    this.shadedAndTintedColor = shadedAndTintedColor
  }

  constructor(pureColor: Int, inputParams: ParamTestInput) {
    this.pureColor = pureColor
    shadedColor = getShadedColor(pureColor, inputParams.shadeRatio)
    tintedColor = getTintedColor(pureColor, inputParams.tintRatio)
    shadedAndTintedColor = getTintedColor(shadedColor, inputParams.tintRatio)
  }



  // -------------------- UTIL --------------------

  private fun getShadedColor(color: Int, shadeRatio: Double): Int {
    val shadeFactor = 1.0 - shadeRatio
    val red = (Color.red(color) * shadeFactor).roundToInt()
    val green = (Color.green(color) * shadeFactor).roundToInt()
    val blue = (Color.blue(color) * shadeFactor).roundToInt()

    return Color.argb(255, red, green, blue)
  }

  private fun getTintedColor(color: Int, tintRatio: Double): Int {
    val red = Color.red(color)
    val green = Color.green(color)
    val blue = Color.blue(color)

    return when (max(red, max(green, blue))) {
      red -> {
        Color.argb(
          255,
          red,
          green + ((red - green).toFloat() * tintRatio).roundToInt(),
          blue + ((red - blue).toFloat() * tintRatio).roundToInt()
        )
      }

      green -> {
        Color.argb(
          255,
          red + ((green - red).toFloat() * tintRatio).roundToInt(),
          green,
          blue + ((green - blue).toFloat() * tintRatio).roundToInt()
        )
      }

      blue -> {
        Color.argb(
          255,
          red + ((blue - red).toFloat() * tintRatio).roundToInt(),
          green + ((blue - green).toFloat() * tintRatio).roundToInt(),
          blue
        )
      }

      else -> Color.argb(255, red, green, blue)
    }
  }
}