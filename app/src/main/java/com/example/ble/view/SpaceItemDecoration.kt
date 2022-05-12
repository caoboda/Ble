package com.example.ble.view

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import com.blankj.utilcode.util.LogUtils

class SpaceItemDecoration(private val space: Int, private val isSkipFirst: Boolean) : ItemDecoration() {
    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        if (isSkipFirst && parent.getChildLayoutPosition(view) == 0) {
            LogUtils.d("跳过首个item")
        } else {
            if (parent.getChildLayoutPosition(view) == 0) {
                outRect.top = space
            } else {
                outRect.top = space / 2
            }
            outRect.left = space
            outRect.right = space
            outRect.bottom = space / 2
        }
    }
}