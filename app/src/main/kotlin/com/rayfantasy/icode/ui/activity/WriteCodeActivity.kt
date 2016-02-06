package com.rayfantasy.icode.ui.activity

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.widget.LinearLayoutManager
import com.android.volley.Request
import com.h6ah4i.android.widget.advrecyclerview.animator.RefactoredDefaultItemAnimator
import com.h6ah4i.android.widget.advrecyclerview.draggable.RecyclerViewDragDropManager
import com.rayfantasy.icode.R
import com.rayfantasy.icode.databinding.ActivityWriteCodeBinding
import com.rayfantasy.icode.extension.snackBar
import com.rayfantasy.icode.iCodeTheme
import com.rayfantasy.icode.postutil.PostUtil
import com.rayfantasy.icode.postutil.bean.CodeGood
import com.rayfantasy.icode.postutil.extension.e
import com.rayfantasy.icode.ui.adapter.EditBlockAdapter
import com.rayfantasy.icode.util.error
import kotlinx.android.synthetic.main.activity_write_code.*
import kotlinx.android.synthetic.main.content_write_code.*
import org.jetbrains.anko.onClick

class WriteCodeActivity : ActivityBindingStatus() {
    private var request: Request<*>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DataBindingUtil.setContentView<ActivityWriteCodeBinding>(this, R.layout.activity_write_code).theme = iCodeTheme
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val blockAdapter = EditBlockAdapter(this)
        val recyclerViewDragDropManager = RecyclerViewDragDropManager()
        recyclerViewDragDropManager.setInitiateOnLongPress(true)
        recyclerViewDragDropManager.setInitiateOnMove(false)
        with(recyclerView) {
            layoutManager = LinearLayoutManager(this@WriteCodeActivity)
            adapter = recyclerViewDragDropManager.createWrappedAdapter(blockAdapter)
            itemAnimator = RefactoredDefaultItemAnimator()
            recyclerViewDragDropManager.attachRecyclerView(this)
        }
        write_code_tv_fab.onClick {
            if (request != null) return@onClick

            request = PostUtil.insertCodeGood(CodeGood(
                    blockAdapter.title,
                    blockAdapter.subTitle,
                    blockAdapter.content),
                    {
                        write_code_tv_fab.snackBar(getText(R.string.upload_success), Snackbar.LENGTH_LONG)
                        finish()
                        request = null
                    },
                    { t, rc ->
                        e("failed, rc =  $rc")
                        write_code_tv_fab.snackBar("${getText(R.string.cannot_upload)}${error("insertCodeGood", rc, this)}", Snackbar.LENGTH_LONG)
                        request = null
                    })
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        request?.let {
            PostUtil.cancel(request)
            request = null
        }
    }
}


