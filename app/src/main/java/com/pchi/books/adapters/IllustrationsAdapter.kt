package com.pchi.books.adapters

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.pchi.books.App
import com.pchi.books.R
import com.pchi.books.models.Illus
import com.pchi.books.utilities.Utilities

class IllustrationsAdapter(private val mBookAdapter: BookAdapter) : RecyclerView.Adapter<IllustrationsAdapter.ViewHolder>() {
    private val mBitmaps = arrayListOf<Bitmap>()

    private var isAdding = false

//    private val mBitmaps = mutableMapOf<Int, Bitmap>()

//    private val mCache = object : LruCache<Int, Bitmap>((Runtime.getRuntime().maxMemory() / 1024 / 8).toInt()) {
//        override fun sizeOf(key: Int, bitmap: Bitmap) = bitmap.byteCount / 1024
//    }

    override fun onCreateViewHolder(parent: ViewGroup, viewtype: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.recyclable_item, parent, false)

        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(mBitmaps[position])
//        holder.bind(mBitmaps[position]!!)

        if (position == itemCount - 1 && !isAdding && mBookAdapter.latestBookURL.isNotEmpty() && mBookAdapter.latestPage.no != -1) {
            val urlNextPage = mBookAdapter.latestBookURL + (mBookAdapter.latestPage.no + 1)

            mBookAdapter.connectHTTP(urlNextPage, object : BookAdapter.OnConnectListener {
                override fun onComplete(msg: String) {
                    addIllustrations(mBookAdapter.latestPage.illustrations, object : OnUpdateListener {
                        override fun onComplete() {
//                            App.shared().appHandler.post { Utilities.scrollToBottom(holder.itemView.parent as View) }

                            if (itemCount > 30) {
                                for (i in 0 until 10 * (mBookAdapter.latestPage.no - 3)) {
                                    mBitmaps[i].recycle()
//                                    mBitmaps[i]?.recycle()
                                }
                            }
                        }
                    })
                }

                override fun onFail(msg: String) {
                    printBoshLog(msg)
                }
            })
        }

        printBoshLog("itemCount= $itemCount")
        printBoshLog("position= $position")
    }

    override fun getItemCount() = mBitmaps.size

//    override fun onViewRecycled(holder: ViewHolder) {
//        printBoshLog("onViewRecycled(...)")
//    }

    fun addIllustrations(newIllustrations: Collection<Illus>, listenerOU: OnUpdateListener) {
        fun startAddition(index: Int) {
            isAdding = true

            if (index == newIllustrations.size) {
                listenerOU.onComplete()
                isAdding = false

                return
            }

            mBookAdapter.connectHTTP(newIllustrations.toList()[index].url, object : BookAdapter.OnConnectListener {
                override fun onComplete(msg: String) {
                    Base64.decode(msg.substringAfter("base64,"), Base64.DEFAULT).also { ba ->
                        mBitmaps.add(BitmapFactory.decodeByteArray(ba, 0, ba.size))
//                        mBitmaps[mBitmaps.size] = BitmapFactory.decodeByteArray(ba, 0, ba.size)
                    }

                    App.shared().appHandler.post { notifyDataSetChanged() }

                    startAddition(index + 1)
                }

                override fun onFail(msg: String) {
                    printBoshLog(msg)
                    listenerOU.onComplete()
                }
            })
        }

//        mBitmaps.clear()

        if (newIllustrations.isEmpty()) {
            App.shared().appHandler.post { notifyDataSetChanged() }
            listenerOU.onComplete()

            return
        }

        startAddition(0)
    }

    fun clear() = mBitmaps.clear()

    private fun printBoshLog(msg: String) = Utilities.printBoshLog(msg)

    interface OnUpdateListener {
        fun onComplete()
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val ivIllus = itemView.findViewById<ImageView>(R.id.iv_illus)

        fun bind(bitmap: Bitmap) {
            ivIllus.setImageBitmap(if (!bitmap.isRecycled) bitmap else null)
            itemView.layoutParams.height = if (!bitmap.isRecycled) -1 else 0
        }
    }
}
