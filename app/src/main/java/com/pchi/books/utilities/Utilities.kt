package com.pchi.books.utilities

import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager
import com.pchi.books.App
import com.pchi.books.Constants
import com.pchi.books.fragments.BaseFragment

open class Utilities {
    companion object {
        fun printBoshLog(msg: String) = Log.i("Bosh_Tag", msg)

        fun handleFragment(
                fm: FragmentManager,
                containerId: Int,
                way: String,
                frag1: BaseFragment,
                frag2: BaseFragment? = null
        ) {
            fm.beginTransaction().apply {
                when (way) {
                    Constants.FRAG_ADD -> add(containerId, frag1)
                    Constants.FRAG_REPLACE -> replace(containerId, frag1)
                    Constants.FRAG_REMOVE -> remove(frag1)
                    Constants.FRAG_HIDE -> hide(frag1)
                    Constants.FRAG_SHOW -> show(frag1)
                    Constants.FRAG_HIDE_THEN_ADD -> {
                        hide(frag1).add(containerId, frag2 ?: return@apply)
                    }
                }
            }.commit()
        }

        fun getColor(colorRes: Int) = ContextCompat.getColor(App.shared().applicationContext, colorRes)

        fun substringBetween(strTarget: String, firstDelimiter: String, secondDelimiter: String): String {
            return strTarget.substringAfter(firstDelimiter).substringBefore(secondDelimiter)
        }

        fun scrollToBottom(v: View) {
            while (v.canScrollVertically(87)) v.scrollBy(0, 1)
        }
    }
}
