package com.pchi.books.adapters

import com.pchi.books.utilities.Utilities

abstract class BaseAdapter {
    protected fun printBoshLog(msg: String) = Utilities.printBoshLog(msg)
}
