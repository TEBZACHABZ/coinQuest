package com.example.coinquest.ui.fragments

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.coinquest.data.CategorySpending
import com.example.coinquest.databinding.ItemCategoryReportBinding
import java.text.NumberFormat

/**
 * Adapter for displaying category-wise spending reports.
 */
class CategoryReportAdapter : ListAdapter<CategorySpending, CategoryReportAdapter.ReportViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReportViewHolder {
        val binding = ItemCategoryReportBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ReportViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ReportViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ReportViewHolder(private val binding: ItemCategoryReportBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(report: CategorySpending) {
            binding.tvCategoryName.text = report.categoryName
            val currencyFormat = NumberFormat.getCurrencyInstance()
            binding.tvTotalAmount.text = currencyFormat.format(report.totalAmount)
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<CategorySpending>() {
        override fun areItemsTheSame(oldItem: CategorySpending, newItem: CategorySpending) = 
            oldItem.categoryName == newItem.categoryName
        override fun areContentsTheSame(oldItem: CategorySpending, newItem: CategorySpending) = 
            oldItem == newItem
    }
}