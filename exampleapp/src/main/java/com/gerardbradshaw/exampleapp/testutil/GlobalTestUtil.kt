package com.gerardbradshaw.exampleapp.testutil

import kotlin.math.roundToInt

internal object GlobalTestUtil {
  const val sliderMax = 16777216

  fun getParameterizedTestIO(): Collection<Array<Any>> {
    val inputParams = Array<Any>(7) {
      val colorProgress = (it * sliderMax.toDouble() / 6.0).roundToInt()

      val shadeProgress = (sliderMax - colorProgress)

      val tintProgress =
        when (it) {
          0 -> 0
          6 -> ((sliderMax.toDouble() / 6.0)).roundToInt()
          else -> ((sliderMax.toDouble() / 6.0) + shadeProgress).roundToInt()
        }

      ParamTestInput(
        colorProgress,
        shadeProgress,
        tintProgress
      )
    }

    val pureColors: Array<Int> = arrayOf(
      -65536, -256, -16711936, -16711681, -16776961, -65281, -65535)

    val shadedColors: Array<Int> = arrayOf(
      -16777216, -13948160, -16755456, -16744320, -16777046, -2883372, -65535)

    val tintedColors: Array<Int> = arrayOf(
      -65536, -1, -2752555, -5570561, -8355585, -43521, -54485)

    val shadedAndTintedColors: Array<Int> = arrayOf(
      -16777216, -13948117, -12102329, -11173760, -11184726, -2865196, -54485)

    val expectedOutputs = Array<Any>(7) {
      ParamTestOutput(
        pureColors[it],
        shadedColors[it],
        tintedColors[it],
        shadedAndTintedColors[it]
      )
    }

    return Array(7) {
      arrayOf(inputParams[it], expectedOutputs[it])
    }.asList()
  }

  fun Double.isWithinAPercentOf(double: Double): Boolean {
    val bound = 0.99 * this .. 1.01 * this
    return double in bound
  }
}