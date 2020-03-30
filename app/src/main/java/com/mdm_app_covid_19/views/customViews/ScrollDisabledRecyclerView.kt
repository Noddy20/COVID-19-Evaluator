package com.mdm_app_covid_19.views.customViews

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import androidx.recyclerview.widget.RecyclerView

class ScrollDisabledRecyclerView : RecyclerView {
    constructor(context: Context): super(context)

    constructor(context: Context, @Nullable attrs: AttributeSet): super(context, attrs)

    constructor(@NonNull context:Context, @Nullable attrs: AttributeSet, defStyle:Int):super(context,attrs,defStyle)

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(e: MotionEvent): Boolean = false

    override fun onInterceptTouchEvent(e: MotionEvent): Boolean = false
}