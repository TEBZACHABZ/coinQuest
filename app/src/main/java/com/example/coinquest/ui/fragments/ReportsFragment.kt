package com.example.coinquest.ui.fragments

import android.app.DatePickerDialog
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.coinquest.databinding.FragmentReportsBinding
import com.example.coinquest.ui.AppViewModel
import com.example.coinquest.data.CategorySpending
import com.example.coinquest.data.Goal
import android.util.Log
import com.google.android.material.datepicker.MaterialDatePicker
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

class ReportsFragment : Fragment() {

    private var _binding: FragmentReportsBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: AppViewModel
    private lateinit var adapter: CategoryReportAdapter
    private lateinit var badgeAdapter: BadgeAdapter

    private var fromDate: Calendar = Calendar.getInstance().apply { 
        set(Calendar.DAY_OF_MONTH, 1) 
    }
    private var toDate: Calendar = Calendar.getInstance()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentReportsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(requireActivity()).get(AppViewModel::class.java)

        setupRecyclerView()
        updateDateDisplays()
        observeReports()

        binding.btnRepFilterDate.setOnClickListener {
            showRangePicker()
        }
    }

    private fun showRangePicker() {
        val builder = MaterialDatePicker.Builder.dateRangePicker()
        builder.setTitleText("Select Report Period")
        val picker = builder.build()
        picker.addOnPositiveButtonClickListener { range ->
            fromDate.timeInMillis = range.first
            toDate.timeInMillis = range.second
            updateDateDisplays()
            observeReports()
        }
        picker.show(childFragmentManager, "report_date_range_picker")
    }

    private fun setupRecyclerView() {
        adapter = CategoryReportAdapter()
        binding.rvCategorySpending.layoutManager = LinearLayoutManager(requireContext())
        binding.rvCategorySpending.adapter = adapter

        badgeAdapter = BadgeAdapter(emptyList())
        binding.rvBadges.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.rvBadges.adapter = badgeAdapter
    }

    private fun updateDateDisplays() {
        val format = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
        binding.tvRepDateRange.text = "${format.format(fromDate.time)} - ${format.format(toDate.time)}"
    }

    private fun observeReports() {
        var currentSpending: List<CategorySpending> = emptyList()
        var currentGoal: Goal? = null
        var expensesCount = 0

        fun updateUI() {
            val total = currentSpending.sumOf { it.totalAmount }
            Log.d("ReportsFragment", "Updating UI with total spending: $total")
            val currencyFormat = NumberFormat.getCurrencyInstance()
            binding.tvTotalPeriodSpending.text = "Total: ${currencyFormat.format(total)}"
            binding.tvCurrentSpending.text = "Spent: ${currencyFormat.format(total)}"

            val min = currentGoal?.minGoal ?: 0.0
            val max = currentGoal?.maxGoal ?: 0.0

            binding.spendingChartView.setData(currentSpending, min, max)

            binding.tvMinGoal.text = "Min: ${currencyFormat.format(min)}"
            binding.tvMaxGoal.text = "Max: ${currencyFormat.format(max)}"

            if (max > 0) {
                binding.pbGoalStatus.max = max.toInt()
                binding.pbGoalStatus.progress = total.toInt()

                when {
                    total < min -> {
                        binding.tvGoalMessage.text = "Below minimum goal. Good job saving!"
                        binding.tvGoalMessage.setTextColor(Color.BLUE)
                    }
                    total <= max -> {
                        binding.tvGoalMessage.text = "Within your goal range. Well done!"
                        binding.tvGoalMessage.setTextColor(Color.parseColor("#4CAF50")) // Green
                    }
                    else -> {
                        binding.tvGoalMessage.text = "Above maximum goal. Try to cut back!"
                        binding.tvGoalMessage.setTextColor(Color.RED)
                    }
                }
            } else {
                binding.pbGoalStatus.progress = 0
                binding.tvGoalMessage.text = "Set your goals in the Goals tab!"
            }

            val badges = viewModel.getBadges(total, expensesCount, min, max)
            badgeAdapter.updateBadges(badges)
        }

        viewModel.getCategorySpending(fromDate.timeInMillis, toDate.timeInMillis).observe(viewLifecycleOwner) { spending ->
            currentSpending = spending
            adapter.submitList(spending)
            updateUI()
        }

        viewModel.goal.observe(viewLifecycleOwner) { goal ->
            currentGoal = goal
            updateUI()
        }

        viewModel.getExpensesCount(fromDate.timeInMillis, toDate.timeInMillis).observe(viewLifecycleOwner) { count ->
            expensesCount = count
            updateUI()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}