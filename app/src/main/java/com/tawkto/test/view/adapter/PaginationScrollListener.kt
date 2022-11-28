package com.tawkto.test.view.adapter

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

// pagination
abstract class PaginationScrollListener(var layoutManager: LinearLayoutManager) : RecyclerView.OnScrollListener() {
    abstract fun isLastPage(): Boolean

    abstract fun isLoading(): Boolean

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)

        val totalItemCount = layoutManager.itemCount
        if (!isLoading() && !isLastPage()) {
            if (layoutManager.findLastCompletelyVisibleItemPosition() == totalItemCount - 1) {
                loadMoreItems()
            }
        }
    }

    abstract fun loadMoreItems()
}