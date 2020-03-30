package com.mdm_app_covid_19.utils

import android.text.InputFilter
import android.text.Spanned

object EmojiFilter {
    fun getFilter(maxLength: Int): Array<InputFilter> {
        val EMOJI_FILTER = InputFilter { source: CharSequence, start: Int, end: Int, dest: Spanned?, dstart: Int, dend: Int ->
            var index = start
            while (index < end) {
                val type = Character.getType(source[index])
                if (type == Character.SURROGATE.toInt() || type == Character.NON_SPACING_MARK.toInt() || type == Character.OTHER_SYMBOL.toInt()) {
                    return@InputFilter ""
                }
                index++
            }
            null
        }
        val FILTER_LENGTH: InputFilter = InputFilter.LengthFilter(maxLength)
        return arrayOf(EMOJI_FILTER, FILTER_LENGTH)
    }

    val passwordFilter: Array<InputFilter>
        get() {
            val EMOJI_FILTER = InputFilter { source: CharSequence, start: Int, end: Int, dest: Spanned?, dstart: Int, dend: Int ->
                var index = start
                while (index < end) {
                    val type = Character.getType(source[index])
                    if (type == Character.SURROGATE.toInt() || type == Character.NON_SPACING_MARK.toInt() || type == Character.OTHER_SYMBOL.toInt()) {
                        return@InputFilter ""
                    }
                    index++
                }
                null
            }
            val FILTER_LENGTH: InputFilter = InputFilter.LengthFilter(80)
            return arrayOf(EMOJI_FILTER, FILTER_LENGTH)
        }
}