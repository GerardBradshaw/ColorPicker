package com.gerardbradshaw.colorpickerlibrary.util

internal data class ParamTestInput(
  val colorProgress: Int,
  val shadeProgress: Int,
  val tintProgress: Int,
  val sliderMax: Int = 16777216
) {
  val colorRatio = colorProgress.toDouble() / sliderMax.toDouble()
  val shadeRatio = shadeProgress.toDouble() / sliderMax.toDouble()
  val tintRatio = tintProgress.toDouble() / sliderMax.toDouble()
}