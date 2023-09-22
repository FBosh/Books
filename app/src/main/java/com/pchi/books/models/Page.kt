package com.pchi.books.models

data class Page(
        val no: Int,
        val size: Int,
        val total: Int,
        val pages: Int,
        val illustrations: Collection<Illus>,
        val edition: String
) {
    companion object {
        val defaultPage = Page(-1, -1, -1, -1, listOf(), "")
    }
}
