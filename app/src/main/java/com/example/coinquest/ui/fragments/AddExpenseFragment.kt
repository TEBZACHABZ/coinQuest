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
import android.Manifest
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
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

    private val requestCameraPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) {
            dispatchTakePictureIntent()
        } else {
            Toast.makeText(requireContext(), "Camera permission is required to take photos", Toast.LENGTH_SHORT).show()
        }
    }

    private val takePictureLauncher = registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success) {
            binding.ivPhotoPreview.visibility = View.VISIBLE
            binding.layoutPhotoPlaceholder.visibility = View.GONE
            currentPhotoPath?.let { path ->
                binding.ivPhotoPreview.setImageURI(Uri.fromFile(File(path)))
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentAddExpenseBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(requireActivity()).get(AppViewModel::class.java)

        // Restore photo path if fragment was recreated
        savedInstanceState?.getString("photo_path")?.let {
            currentPhotoPath = it
            binding.ivPhotoPreview.visibility = View.VISIBLE
            binding.layoutPhotoPlaceholder.visibility = View.GONE
            binding.ivPhotoPreview.setImageURI(Uri.fromFile(File(it)))
        }

        setupCategorySpinner()
        setupDateTimePickers()
        
        // Set initial date display
        binding.etDate.setText(SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(selectedDate.time))

        binding.btnAddCategory.setOnClickListener {
            showAddCategoryDialog()
        }

        binding.cardAddPhoto.setOnClickListener {
            checkCameraPermissionAndLaunch()
        }

        binding.btnSaveExpense.setOnClickListener {
            saveExpense()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("photo_path", currentPhotoPath)
    }

    private fun checkCameraPermissionAndLaunch() {
        when {
            ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED -> {
                dispatchTakePictureIntent()
            }
            else -> {
                requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
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
        binding.etDate.setOnClickListener {
            DatePickerDialog(requireContext(), { _, year, month, dayOfMonth ->
                selectedDate.set(year, month, dayOfMonth)
                binding.etDate.setText(SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(selectedDate.time))
            }, selectedDate.get(Calendar.YEAR), selectedDate.get(Calendar.MONTH), selectedDate.get(Calendar.DAY_OF_MONTH)).show()
        }

        binding.etStartTime.setOnClickListener {
            TimePickerDialog(requireContext(), { _, hourOfDay, minute ->
                binding.etStartTime.setText(String.format("%02d:%02d", hourOfDay, minute))
            }, 12, 0, true).show()
        }

        binding.etEndTime.setOnClickListener {
            TimePickerDialog(requireContext(), { _, hourOfDay, minute ->
                binding.etEndTime.setText(String.format("%02d:%02d", hourOfDay, minute))
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
        val startTime = binding.etStartTime.text.toString()
        val endTime = binding.etEndTime.text.toString()
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
        
        // Reset fields
        binding.etAmount.text?.clear()
        binding.etDescription.text?.clear()
        binding.ivPhotoPreview.visibility = View.GONE
        binding.layoutPhotoPlaceholder.visibility = View.VISIBLE
        currentPhotoPath = null
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}