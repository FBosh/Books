package com.pchi.books.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.pchi.books.App
import com.pchi.books.utilities.Utilities

abstract class BaseFragment : Fragment() {
    protected val appCtx = App.shared().applicationContext ?: App.shared()

    abstract val layoutRes: Int

    abstract fun initUI()

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(layoutRes, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initUI()
    }

    protected fun printBoshLog(msg: String) = Utilities.printBoshLog(msg)

    protected fun logOnTV(tv: TextView?, msg: String) {
        if (tv == null) return

        tv.text = StringBuilder(tv.text).apply {
            if (tv.text.isNotEmpty()) append("\n")
            append(msg)
        }

        scrollToBottom(tv)
    }

    protected fun hideKeyboard() {
        (context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).also { imm ->
            imm.hideSoftInputFromWindow(activity?.currentFocus?.windowToken, 0)
        }
    }

    protected fun scrollToBottom(v: View) = Utilities.scrollToBottom(v)
}
