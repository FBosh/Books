package com.pchi.books.fragments

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.pchi.books.App
import com.pchi.books.Constants
import com.pchi.books.R
import com.pchi.books.adapters.BookAdapter
import com.pchi.books.adapters.IllustrationsAdapter
import com.pchi.books.utilities.Utilities
import kotlinx.android.synthetic.main.frag_book.*

class BookFragment : BaseFragment() {
    override val layoutRes = R.layout.frag_book

    private val mAdapter = BookAdapter()

    override fun initUI() {
        et_bid?.apply {
            setText(App.shared().appSP?.getString(Constants.KEY_BID, Constants.DEFAULT_BID)
                    ?: Constants.DEFAULT_BID)
            setSelection(length())
        }

        btn_connect?.setOnClickListener {
            it?.isEnabled = false

            recycler_layout?.apply {
                visibility = View.VISIBLE
                (adapter as? IllustrationsAdapter)?.clear()
                adapter = IllustrationsAdapter(mAdapter)
            }

            val url = StringBuilder("http://").apply {
                App.shared().appSP.also { sp ->
                    append(sp?.getString(Constants.KEY_IP, Constants.DEFAULT_IP) ?: "")
                    append("/list/")
                    append(et_bid?.text ?: sp?.getString(Constants.KEY_BID, Constants.DEFAULT_BID))
                    append("/")
                }
            }.toString()

            mAdapter.connectHTTP(url, object : BookAdapter.OnConnectListener {
                override fun onComplete(msg: String) {
                    (recycler_layout.adapter as IllustrationsAdapter).apply {
                        addIllustrations(mAdapter.latestPage.illustrations, object : IllustrationsAdapter.OnUpdateListener {
                            override fun onComplete() {
                                App.shared().appHandler.post {
                                    it?.isEnabled = true
                                    if (itemCount == 0) recycler_layout?.visibility = View.GONE
                                }
                            }
                        })
                    }

                    hideKeyboard()
                }

                override fun onFail(msg: String) {
                    printBoshLog(msg)

                    App.shared().appHandler.post { it?.isEnabled = true }
                }
            })
        }

        recycler_layout?.apply {
            layoutManager = LinearLayoutManager(context)
//            adapter = IllustrationsAdapter(mAdapter)
            visibility = View.GONE
        }

        ib_settings?.setOnClickListener {
            Utilities.handleFragment(
                    requireFragmentManager(),
                    R.id.layout_frag_container,
                    Constants.FRAG_HIDE_THEN_ADD,
                    this,
                    SettingsFragment()
            )
        }
    }

    override fun onDestroyView() {
        App.shared().appSP?.edit()?.putString(Constants.KEY_BID, et_bid.text.toString())?.apply()

        super.onDestroyView()
    }
}
