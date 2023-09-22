package com.pchi.books.fragments

import com.pchi.books.App
import com.pchi.books.Constants
import com.pchi.books.R
import com.pchi.books.utilities.Utilities
import kotlinx.android.synthetic.main.frag_settings.*

class SettingsFragment : BaseFragment() {
    override val layoutRes = R.layout.frag_settings

    override fun initUI() {
        et_sv_ip?.apply {
            setText(App.shared().appSP?.getString(Constants.KEY_IP, Constants.DEFAULT_IP)
                    ?: Constants.DEFAULT_IP)
            setSelection(length())
        }

        btn_save?.setOnClickListener {
            if (et_sv_ip.text.toString() == App.shared().appSP?.getString(Constants.KEY_IP, Constants.DEFAULT_IP)) {
                Utilities.handleFragment(
                        requireFragmentManager(), R.id.layout_frag_container, Constants.FRAG_REMOVE, this
                )

                Utilities.handleFragment(
                        requireFragmentManager(),
                        R.id.layout_frag_container,
                        Constants.FRAG_SHOW,
                        requireFragmentManager().fragments[0] as BaseFragment
                )

                return@setOnClickListener
            }

            App.shared().appSP?.edit()?.apply {
                putString(Constants.KEY_IP, et_sv_ip.text.toString())
                apply()
            }

            Utilities.handleFragment(
                    requireFragmentManager(), R.id.layout_frag_container, Constants.FRAG_REPLACE, BookFragment()
            )
        }
    }
}
