package com.example.coinquest.ui.fragments

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.coinquest.data.Expense
import com.example.coinquest.databinding.ItemExpenseBinding
import java.io.File
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

class ExpenseAdapter(private val onPhotoClick: (String) -> Unit) : ListAdapter<Expense, ExpenseAdapter.ExpenseViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpenseViewHolder {
        val binding = ItemExpenseBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ExpenseViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ExpenseViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ExpenseViewHolder(private val binding: ItemExpenseBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(expense: Expense) {
            binding.tvExpenseDescription.text = expense.description
            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            binding.tvExpenseDetails.text = "${dateFormat.format(Date(expense.date))} | ${expense.startTime} - ${expense.endTime}"
            
            val currencyFormat = NumberFormat.getCurrencyInstance()
            binding.tvExpenseAmount.text = currencyFormat.format(expense.amount)

            if (expense.photoPath != null) {
                binding.ivExpensePhoto.visibility = View.VISIBLE
                binding.ivExpensePhoto.setImageURI(Uri.fromFile(File(expense.photoPath)))
                binding.ivExpensePhoto.setOnClickListener { onPhotoClick(expense.photoPath) }
            } else {
                binding.ivExpensePhoto.visibility = View.GONE
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<Expense>() {
        override fun areItemsTheSame(oldItem: Expense, newItem: Expense) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Expense, newItem: Expense) = oldItem == newItem
    }
}