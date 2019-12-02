package com.truongkhanh.supernote.view.evaluate.createevaluate.adapter

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.jakewharton.rxbinding2.view.RxView
import com.truongkhanh.supernote.model.EvaluateTodo
import com.truongkhanh.supernote.utils.DisposeBag
import com.truongkhanh.supernote.utils.THROTTLE_TIME
import com.truongkhanh.supernote.utils.disposedBy
import kotlinx.android.synthetic.main.item_evaluate_detail.view.*
import java.util.concurrent.TimeUnit

class EvaluateTodoDetailViewHolder(itemClickListener: (EvaluateTodo) -> Unit, bag: DisposeBag, item: View) : RecyclerView.ViewHolder(item) {

    var data: EvaluateTodo? = null
    val title: TextView = item.tvTodoTitle
    val startDate: TextView = item.tvStartTime
    val endDate: TextView = item.tvEndTime
    val separate: TextView = item.tvSeparate
    val checkSummary: TextView = item.tvCheckSummary

    init {
        RxView.clicks(item)
            .throttleFirst(THROTTLE_TIME, TimeUnit.MILLISECONDS)
            .subscribe {
                data?.let{ itemClickListener(it) }
            }.disposedBy(bag)
    }
}