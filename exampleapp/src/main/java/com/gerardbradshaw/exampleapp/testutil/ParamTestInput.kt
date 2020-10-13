package com.gerardbradshaw.exampleapp.testutil

internal data class ParamTestInput(
  val colorProgress: Int,
  val shadeProgress: Int,
  val tintProgress: Int,
  val sliderMax: Int = 16777216
) {
  val colorRatio = colorProgress.toDouble() / sliderMax.toDouble()
  val shadeRatio = shadeProgress.toDouble() / sliderMax.toDouble()
  val tintRatio = tintProgress.toDouble() / sliderMax.toDouble()

  override fun toString(): String {
    return "color = ${colorRatio.format(2)}, shade = ${shadeRatio.format(2)}, tint = ${tintRatio.format(2)}"
  }

  private fun Double.format(digits: Int) = "%.${digits}f".format(this)
}