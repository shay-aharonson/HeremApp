package com.heremapp.utility.messaging

import android.os.Build
import android.util.Log
import android.view.View
import androidx.annotation.IntDef
import com.google.android.material.snackbar.Snackbar
import java.util.regex.Pattern
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Utility messaging class, enables app wide logging and snackbar messages.
 */
@Singleton
class MessageHandler @Inject constructor() {

    companion object {
        const val LENGTH_INDEFINITE = -2
        const val LENGTH_SHORT = -1
        const val LENGTH_LONG = 0

        @IntDef(
            LENGTH_INDEFINITE,
            LENGTH_SHORT,
            LENGTH_LONG
        )
        annotation class DisplayTime

        fun logW(message: String, throwable: Throwable? = null) {
            val msg = "${Thread.currentThread().name} - $message"
            Log.w(tag, msg, throwable)
        }

        fun log(throwable: Throwable) {
            Log.e(tag, Thread.currentThread().name, throwable)
        }

        fun log(message: String, throwable: Throwable? = null) {
            val msg = "${Thread.currentThread().name} - $message"
            if (throwable != null)
                Log.e(tag, msg, throwable)
            else
                Log.d(tag, msg)
        }

        private const val MAX_TAG_LENGTH = 23
        private val ANONYMOUS_CLASS = Pattern.compile("(\\$\\d+)+$")

        private val tag: String?
            get() = Throwable().stackTrace.first { MessageHandler::class.java.name !in it.className }
                .let(::createStackElementTag)

        /**
         * Extract the tag which should be used for the message from the `element`. By default
         * this will use the class name without any anonymous class suffixes (e.g., `Foo$1`
         * becomes `Foo`).
         */
        private fun createStackElementTag(element: StackTraceElement): String? {
            var tag = element.className.substringAfterLast('.')
            val m = ANONYMOUS_CLASS.matcher(tag)
            if (m.find()) {
                tag = m.replaceAll("")
            }
            // Tag length limit was removed in API 24.
            return if (tag.length <= MAX_TAG_LENGTH || Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                tag
            } else {
                tag.substring(0, MAX_TAG_LENGTH)
            }
        }
    }

    /**
     * Display a [Snackbar] with the given message for the given duration.
     */
    fun message(view: View, @DisplayTime displayTime: Int, message: String, throwable: Throwable? = null) {
        Snackbar.make(
            view,
            String.format(
                "%s%s",
                message,
                if (throwable != null) "\n" + throwable.printStackTrace() else ""
            ),
            displayTime
        ).show()

        if (throwable != null)
            log(message, throwable)
        else
            log(message)
    }
}