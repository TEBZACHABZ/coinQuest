package com.example.coinquest.ui.fragments

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.coinquest.databinding.FragmentReportsBinding
import com.example.coinquest.ui.AppViewModel
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

class ReportsFragment : Fragment() {

    private var _binding: FragmentReportsBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: AppViewModel
    private lateinit var adapter: CategoryReportAdapter

    private var fromDate: Calendar = Calendar.getInstance().apply { add(Calendar.MONTH, -1) }
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

        binding.btnRepFromDate.setOnClickListener {
            showDatePicker(fromDate) {
                updateDateDisplays()
                observeReports()
            }
        }

        binding.btnRepToDate.setOnClickListener {
            showDatePicker(toDate) {
                updateDateDisplays()
                observeReports()
            }
        }
    }

    private fun setupRecyclerView() {
        adapter = CategoryReportAdapter()
        binding.rvCategorySpending.layoutManager = LinearLayoutManager(requireContext())
        binding.rvCategorySpending.adapter = adapter
    }

    private fun updateDateDisplays() {
        val format = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        binding.tvRepFromDate.text = format.format(fromDate.time)
        binding.tvRepToDate.text = format.format(toDate.time)
    }

    private fun observeReports() {
        viewModel.getCategorySpending(fromDate.timeInMillis, toDate.timeInMillis).observe(viewLifecycleOwner) { spending ->
            adapter.submitList(spending)
            val total = spending.sumOf { it.totalAmount }
            val currencyFormat = NumberFormat.getCurrencyInstance()
            binding.tvTotalPeriodSpending.text = "Total: ${currencyFormat.format(total)}"
        }
    }

    private fun showDatePicker(calendar: Calendar, onDateSet: () -> Unit) {
        DatePickerDialog(requireContext(), { _, year, month, dayOfMonth ->
            calendar.set(year, month, dayOfMonth)
            onDateSet()
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}