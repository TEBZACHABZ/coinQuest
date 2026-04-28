package com.example.coinquest.ui.fragments

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.coinquest.data.Category
import com.example.coinquest.data.Expense
import com.example.coinquest.databinding.FragmentAddExpenseBinding
import com.example.coinquest.ui.AppViewModel
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class AddExpenseFragment : Fragment() {

    private var _binding: FragmentAddExpenseBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: AppViewModel
    private var selectedDate: Calendar = Calendar.getInstance()
    private var currentPhotoPath: String? = null
    private var selectedCategory: Category? = null
    private var categoriesList: List<Category> = emptyList()

    private val takePictureLauncher = registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success) {
            binding.ivPhotoPreview.visibility = View.VISIBLE
            binding.ivPhotoPreview.setImageURI(Uri.fromFile(File(currentPhotoPath!!)))
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentAddExpenseBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(requireActivity()).get(AppViewModel::class.java)

        setupCategorySpinner()
        setupDateTimePickers()

        binding.btnAddCategory.setOnClickListener {
            showAddCategoryDialog()
        }

        binding.btnAddPhoto.setOnClickListener {
            dispatchTakePictureIntent()
        }

        binding.btnSaveExpense.setOnClickListener {
            saveExpense()
        }
    }

    private fun setupCategorySpinner() {
        viewModel.allCategories.observe(viewLifecycleOwner) { categories ->
            categoriesList = categories
            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, categories.map { it.name })
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.spinnerCategory.adapter = adapter
        }
    }

    private fun setupDateTimePickers() {
        binding.btnPickDate.setOnClickListener {
            DatePickerDialog(requireContext(), { _, year, month, dayOfMonth ->
                selectedDate.set(year, month, dayOfMonth)
                binding.tvSelectedDate.text = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(selectedDate.time)
            }, selectedDate.get(Calendar.YEAR), selectedDate.get(Calendar.MONTH), selectedDate.get(Calendar.DAY_OF_MONTH)).show()
        }

        binding.btnStartTime.setOnClickListener {
            TimePickerDialog(requireContext(), { _, hourOfDay, minute ->
                binding.tvStartTime.text = String.format("%02d:%02d", hourOfDay, minute)
            }, 12, 0, true).show()
        }

        binding.btnEndTime.setOnClickListener {
            TimePickerDialog(requireContext(), { _, hourOfDay, minute ->
                binding.tvEndTime.text = String.format("%02d:%02d", hourOfDay, minute)
            }, 13, 0, true).show()
        }
    }

    private fun showAddCategoryDialog() {
        val editText = EditText(requireContext())
        AlertDialog.Builder(requireContext())
            .setTitle("Add Category")
            .setView(editText)
            .setPositiveButton("Add") { _, _ ->
                val name = editText.text.toString()
                if (name.isNotEmpty()) {
                    viewModel.insertCategory(Category(name = name))
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun dispatchTakePictureIntent() {
        val photoFile: File? = try {
            createImageFile()
        } catch (ex: Exception) {
            null
        }
        photoFile?.also {
            val photoURI: Uri = FileProvider.getUriForFile(requireContext(), "com.example.coinquest.fileprovider", it)
            takePictureLauncher.launch(photoURI)
        }
    }

    private fun createImageFile(): File {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir: File? = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile("JPEG_${timeStamp}_", ".jpg", storageDir).apply {
            currentPhotoPath = absolutePath
        }
    }

    private fun saveExpense() {
        val amountStr = binding.etAmount.text.toString()
        val description = binding.etDescription.text.toString()
        val startTime = binding.tvStartTime.text.toString()
        val endTime = binding.tvEndTime.text.toString()
        val categoryIndex = binding.spinnerCategory.selectedItemPosition

        if (amountStr.isEmpty() || categoryIndex == -1) {
            Toast.makeText(requireContext(), "Please enter amount and select category", Toast.LENGTH_SHORT).show()
            return
        }

        val amount = amountStr.toDoubleOrNull() ?: 0.0
        val categoryId = categoriesList[categoryIndex].id

        val expense = Expense(
            date = selectedDate.timeInMillis,
            startTime = startTime,
            endTime = endTime,
            description = description,
            amount = amount,
            categoryId = categoryId,
            photoPath = currentPhotoPath
        )

        viewModel.insertExpense(expense)
        Toast.makeText(requireContext(), "Expense saved", Toast.LENGTH_SHORT).show()
        // Reset fields or navigate
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}