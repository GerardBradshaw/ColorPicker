package com.gerardbradshaw.exampleapp.radialview

import android.os.SystemClock
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.BoundedMatcher
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import com.gerardbradshaw.exampleapp.R
import com.gerardbradshaw.exampleapp.testutil.GlobalTestUtil.isWithinAPercentOf
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers.allOf
import kotlin.math.*

object RadialViewAndroidTestUtil {
  private const val TAG = "RadialViewAndroidTestUt"

  // -------------------- MOVE THUMB --------------------

  fun moveThumbTo(tintRatio: Double, colorRatio: Double) {
    onView(allOf(withId(R.id.color_picker_library_large_window), isDisplayed()))
      .perform(touchEventDownAndUpAction(tintRatio, colorRatio))
  }

  private fun touchEventDownAndUpAction(tintRatio: Double, colorRatio: Double): ViewAction? {
    return object : ViewAction {
      override fun getConstraints(): Matcher<View> {
        return isDisplayed()
      }

      override fun getDescription(): String {
        return "touch event down and up"
      }

      override fun perform(uiController: UiController, view: View) {
        val viewLocation = IntArray(2)
        view.getLocationOnScreen(viewLocation)

        val acuteAngle = (2.0 * Math.PI) * when {
          colorRatio < 0.25 -> colorRatio
          colorRatio < 0.5 -> 0.5 - colorRatio
          colorRatio < 0.75 -> colorRatio - 0.5
          else -> 1.0 - colorRatio
        }

        val radius = view.width / 2.0

        val horizontalOffset = (radius * (1.0 - tintRatio)) * cos(acuteAngle)
        val verticalOffset = (radius * (1.0 - tintRatio)) * sin(acuteAngle)

        val touchX = when {
          colorRatio < 0.25 || colorRatio > 0.75 -> {
            (radius + horizontalOffset).toFloat()
          }
          else -> (radius - horizontalOffset).toFloat()
        }

        val touchY = when {
          colorRatio > 0.5 -> (radius - verticalOffset).toFloat()
          else -> (radius + verticalOffset).toFloat()
        }

        val motionEvent = MotionEvent.obtain(
          SystemClock.uptimeMillis(),
          SystemClock.uptimeMillis() + 100,
          MotionEvent.ACTION_DOWN,
          touchX,
          touchY,
          0)

        view.dispatchTouchEvent(motionEvent)
      }
    }
  }

  // -------------------- CHECK THUMB POSITION --------------------

  fun checkThumbIsAtRatioPosition(tintRatio: Double, colorRatio: Double) {
    onView(allOf(withId(R.id.color_picker_library_large_window_and_thumb_container), isDisplayed()))
      .check(matches(thumbIsApproximatelyAtRatioPosition(tintRatio, colorRatio)))
  }

  private fun thumbIsApproximatelyAtRatioPosition(tintRatio: Double, colorRatio: Double): Matcher<View?>? {
    return object : BoundedMatcher<View?, FrameLayout>(FrameLayout::class.java) {
      override fun matchesSafely(view: FrameLayout): Boolean {
        val window: FrameLayout = view.findViewById(R.id.color_picker_library_large_window)
        val thumb: ImageView = view.findViewById(R.id.color_picker_library_large_thumb)

        val radius = window.width / 2.0
        val xDistanceToCenter = thumb.x - radius
        val xRatio = xDistanceToCenter / radius

        val yDistanceToCenter = radius - thumb.y
        val yRatio = yDistanceToCenter / radius

        val actualTintRatio = 1.0 - sqrt(xRatio.pow(2) + yRatio.pow(2))

        if (1.0.isWithinAPercentOf(actualTintRatio)) {
          return 1.0.isWithinAPercentOf(tintRatio)
        }

        var angle =
          if (xRatio != 0.0 && yRatio != 0.0) atan(yRatio / xRatio)
          else 0.0

        angle += when {
          xRatio > 0.0 && yRatio > 0.0 -> 0.0
          xRatio < 0.0 -> Math.PI
          else -> 2.0 * Math.PI
        }

        val actualColorRatio = 1.0 - (angle / (2.0 * Math.PI))

        val isColorRatioOk = actualColorRatio.isWithinAPercentOf(colorRatio) ||
            (actualColorRatio.isWithinAPercentOf(1.0) &&
                colorRatio.isWithinAPercentOf(0.0)) ||
            (actualColorRatio.isWithinAPercentOf(0.0) &&
                colorRatio.isWithinAPercentOf(1.0))

        return tintRatio.isWithinAPercentOf(actualTintRatio) && isColorRatioOk
      }

      override fun describeTo(description: Description) {
        description.appendText("Matches to a RadialColorPicker with tintRatio and colorRatio as specified.")
      }
    }
  }
}