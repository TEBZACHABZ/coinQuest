package com.example.coinquest.ui.fragments

import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.coinquest.databinding.FragmentExpensesBinding
import com.example.coinquest.ui.AppViewModel
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class ExpensesFragment : Fragment() {

    private var _binding: FragmentExpensesBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: AppViewModel
    private lateinit var adapter: ExpenseAdapter
    
    private var fromDate: Calendar = Calendar.getInstance().apply { add(Calendar.MONTH, -1) }
    private var toDate: Calendar = Calendar.getInstance()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentExpensesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(requireActivity()).get(AppViewModel::class.java)

        setupRecyclerView()
        updateDateDisplays()
        observeExpenses()

        binding.btnFromDate.setOnClickListener {
            showDatePicker(fromDate) {
                updateDateDisplays()
                observeExpenses()
            }
        }

        binding.btnToDate.setOnClickListener {
            showDatePicker(toDate) {
                updateDateDisplays()
                observeExpenses()
            }
        }
    }

    private fun setupRecyclerView() {
        adapter = ExpenseAdapter { photoPath ->
            val photoFile = File(photoPath)
            val uri = FileProvider.getUriForFile(requireContext(), "com.example.coinquest.fileprovider", photoFile)
            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uri, "image/*")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            startActivity(intent)
        }
        binding.rvExpenses.layoutManager = LinearLayoutManager(requireContext())
        binding.rvExpenses.adapter = adapter
    }

    private fun updateDateDisplays() {
        val format = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        binding.tvFromDate.text = format.format(fromDate.time)
        binding.tvToDate.text = format.format(toDate.time)
    }

    private fun observeExpenses() {
        // Use a new observer every time the date range changes
        viewModel.getExpensesBetweenDates(fromDate.timeInMillis, toDate.timeInMillis).observe(viewLifecycleOwner) { expenses ->
            adapter.submitList(expenses)
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