package com.gerardbradshaw.exampleapp.squareview

import android.view.View
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.MotionEvents
import androidx.test.espresso.matcher.ViewMatchers.*
import org.hamcrest.Matcher
import org.hamcrest.Matchers.allOf


object SquareViewAndroidTestUtil {
  fun moveThumbTo(xRatio: Double, yRatio: Double, squareResId: Int) {
    onView(allOf(withId(squareResId), isDisplayed()))
      .perform(touchSquareDownAndUp(xRatio, yRatio))
  }

  fun touchSquareDownAndUp(xRatio: Double, yRatio: Double): ViewAction? {
    return object : ViewAction {
      override fun getConstraints(): Matcher<View> {
        return isDisplayed()
      }

      override fun getDescription(): String {
        return "Send touch events."
      }

      override fun perform(uiController: UiController, view: View) {
        val touchX = (view.width * xRatio).toFloat()
        val touchY = (view.height * yRatio).toFloat()

        val touchCoordinates = floatArrayOf(touchX, touchY)
        val precision = floatArrayOf(1f, 1f)

        val down = MotionEvents.sendDown(uiController, touchCoordinates, precision).down
        uiController.loopMainThreadForAtLeast(200)
        MotionEvents.sendUp(uiController, down, touchCoordinates)
      }
    }
  }
}