package com.igorronner.irinterstitial.utils

import android.util.Log
import androidx.annotation.StringDef
import com.igorronner.irinterstitial.BuildConfig

/**
 * Takes care of logging to the default logcat stream.
 * Avoids logging if running a release build.
 *
 * Keep in mind that the logcat **is readable** in rooted devices or by power users
 * with USB debugging on, its content can even be used to trigger tasks,
 * meaning it's constantly being parsed, in which case we don't
 * want to potentially leak business info or have the purchase flow hijacked.
 *
 * @see <a href="https://tasker.joaoapps.com/userguide/en/help/eh_logcat_entry.html">Tasker Logcat Entry</a>
 *
 */
object Logger {
    @JvmOverloads fun log(tag: String? = TAG, d: String) = baseLog(d, LEVEL_DEBUG)
    @JvmOverloads fun logInfo(tag: String? = TAG, i: String) = baseLog(i, LEVEL_INFO)
    @JvmOverloads fun logWarning(tag: String? = TAG, w: String) = baseLog(w, LEVEL_WARNING)
    @JvmOverloads fun logError(tag: String? = TAG, e: String) = baseLog(e, LEVEL_ERROR)
    @JvmOverloads fun logWTF(tag: String? = TAG, s: String) = baseLog(s, LEVEL_WTF)
    @JvmOverloads fun logException(tag: String? = TAG, t: Throwable) {
        t.printStackTrace()
        baseLog(t.message ?: "No message", LEVEL_FATAL)
    }

    private fun baseLog(message: String, @LogLevel logLevel: String) {
        if (!BuildConfig.DEBUG) return

        when (logLevel) {
            LEVEL_DEBUG -> Log.d(TAG, message)
            LEVEL_INFO -> Log.i(TAG, message)
            LEVEL_WARNING -> Log.w(TAG, message)
            LEVEL_ERROR -> Log.e(TAG, message)
            LEVEL_WTF -> Log.wtf(TAG, message)
            else -> Log.v(TAG, message)
        }
    }

    @Retention(AnnotationRetention.SOURCE)
    @StringDef(LEVEL_INFO, LEVEL_WARNING, LEVEL_ERROR, LEVEL_WTF)
    private annotation class LogLevel

    private const val TAG = "IRAds"
    private const val LEVEL_DEBUG = "[DEBUG]"
    private const val LEVEL_INFO = "[INFO]"
    private const val LEVEL_WARNING = "[WARN]"
    private const val LEVEL_ERROR = "[ERROR]"
    private const val LEVEL_FATAL = "[FATAL]"
    private const val LEVEL_WTF = "[WTF]"
}