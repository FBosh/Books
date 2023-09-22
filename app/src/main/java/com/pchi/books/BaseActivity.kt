package com.pchi.books

import android.content.pm.ActivityInfo
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.pchi.books.fragments.BaseFragment
import com.pchi.books.fragments.SettingsFragment
import com.pchi.books.utilities.Utilities

open class BaseActivity : AppCompatActivity() {
    private var canCloseApp = false

    override fun onStart() {
        super.onStart()

        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    }

    override fun onBackPressed() {
        supportFragmentManager.fragments.also { list ->
            if (list.size == 2 && list[1] is SettingsFragment) {
                Utilities.handleFragment(
                        supportFragmentManager,
                        R.id.layout_frag_container,
                        Constants.FRAG_REPLACE,
                        list[0] as BaseFragment
                )

                Utilities.handleFragment(
                        supportFragmentManager,
                        R.id.layout_frag_container,
                        Constants.FRAG_SHOW,
                        list[0] as BaseFragment
                )

                return
            }
        }

        if (canCloseApp) {
            finish()
        } else {
            canCloseApp = true

            Toast.makeText(this, "Tap again to close", Toast.LENGTH_SHORT).show()

//            Handler().postDelayed({ canCloseApp = false }, 2500)
            App.shared().appHandler.postDelayed({ canCloseApp = false }, 2500)
        }
    }

    protected fun printBoshLog(msg: String) = Utilities.printBoshLog(msg)
}
