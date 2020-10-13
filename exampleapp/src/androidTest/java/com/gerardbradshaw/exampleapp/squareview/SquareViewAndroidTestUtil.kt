package com.gerardbradshaw.exampleapp.squareview

import android.os.SystemClock
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.ViewInteraction
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.BoundedMatcher
import androidx.test.espresso.matcher.ViewMatchers.*
import com.gerardbradshaw.colorpickerlibrary.R
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.MatcherAssert
import org.hamcrest.Matchers
import org.hamcrest.Matchers.allOf
import org.hamcrest.Matchers.equalTo

object SquareViewAndroidTestUtil {
  private const val TAG = "SquareViewAndroidTestUt"

  fun moveThumbTo(xRatio: Double, yRatio: Double) {
    onWindow()
      .perform(touchViewUpAndDown(xRatio, yRatio))
  }

  private fun onWindow(): ViewInteraction {
    return onView(allOf(withId(R.id.color_picker_library_large_window), isDisplayed()))
  }

  fun touchViewUpAndDown(xRatio: Double, yRatio: Double): ViewAction? {
    return object : ViewAction {
      override fun getConstraints(): Matcher<View> {
        return isDisplayed()
      }

      override fun getDescription(): String {
        return "Send touch events."
      }

      override fun perform(uiController: UiController, view: View) {
        val viewLocation = IntArray(2)
        view.getLocationOnScreen(viewLocation)

        val touchX = ((1.0 - xRatio) * view.width).toFloat()
        val touchY = (yRatio * view.height).toFloat()

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

  fun checkThumbIsAtRatioPosition(tintRatio: Double, shadeRatio: Double) {
    onThumbAndWindowContainer()
      .check(matches(thumbIsApproximatelyAtRatioPosition(tintRatio, shadeRatio)))
  }

  private fun onThumbAndWindowContainer(): ViewInteraction {
    return onView(allOf(
      withId(R.id.color_picker_library_large_window_and_thumb_container),
      isDisplayed()))
  }

  private fun thumbIsApproximatelyAtRatioPosition(tintRatio: Double, shadeRatio: Double): Matcher<View?>? {
    return object : BoundedMatcher<View?, FrameLayout>(FrameLayout::class.java) {
      override fun matchesSafely(view: FrameLayout): Boolean {
        val window: FrameLayout = view.findViewById(R.id.color_picker_library_large_window)
        val thumb: ImageView = view.findViewById(R.id.color_picker_library_large_thumb)

        val actualTintRatio = 1.0 - (thumb.x / window.width.toDouble())
        val actualShadeRatio = thumb.y / window.height.toDouble()

        val tintRatioRange = 0.99 * tintRatio .. 1.01 * tintRatio
        val shadeRatioRange = 0.99 * shadeRatio .. 1.01 * shadeRatio

        return actualTintRatio in tintRatioRange && actualShadeRatio in shadeRatioRange
      }

      override fun describeTo(description: Description) {
        description.appendText("thumb is at ratio position")
      }
    }
  }}