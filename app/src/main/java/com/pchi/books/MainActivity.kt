package com.pchi.books

import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import com.pchi.books.fragments.BookFragment
import com.pchi.books.utilities.Utilities
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (view_mask != null) {
            init()
            initUI()
        }
    }

    private fun init() {
        fun replaceBookFragment() {
            Utilities.handleFragment(
                    supportFragmentManager, R.id.layout_frag_container, Constants.FRAG_REPLACE, BookFragment()
            )
        }

        replaceBookFragment()
    }

    private fun initUI() {
        findViewById<Toolbar>(resources.getIdentifier("action_bar", "id", packageName))
                ?.setOnLongClickListener {
                    init()

                    true
                }
    }
}
