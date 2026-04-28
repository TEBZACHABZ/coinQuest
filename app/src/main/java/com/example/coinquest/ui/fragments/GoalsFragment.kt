package com.example.coinquest.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.coinquest.data.Goal
import com.example.coinquest.databinding.FragmentGoalsBinding
import com.example.coinquest.ui.AppViewModel

/**
 * Fragment to set and view monthly spending goals.
 * Demonstrates use of SeekBar and EditText interaction.
 */
class GoalsFragment : Fragment() {

    private var _binding: FragmentGoalsBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: AppViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentGoalsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(requireActivity()).get(AppViewModel::class.java)

        // Observe existing goal
        viewModel.goal.observe(viewLifecycleOwner) { goal ->
            goal?.let {
                binding.etMinGoal.setText(it.minGoal.toString())
                binding.sbMinGoal.progress = it.minGoal.toInt()
                binding.etMaxGoal.setText(it.maxGoal.toString())
                binding.sbMaxGoal.progress = it.maxGoal.toInt()
            }
        }

        setupSeekBarListeners()

        binding.btnSaveGoals.setOnClickListener {
            saveGoals()
        }
    }

    private fun setupSeekBarListeners() {
        binding.sbMinGoal.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) binding.etMinGoal.setText(progress.toString())
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        binding.sbMaxGoal.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) binding.etMaxGoal.setText(progress.toString())
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }

    private fun saveGoals() {
        val minGoal = binding.etMinGoal.text.toString().toDoubleOrNull() ?: 0.0
        val maxGoal = binding.etMaxGoal.text.toString().toDoubleOrNull() ?: 0.0

        if (minGoal > maxGoal) {
            Toast.makeText(requireContext(), "Minimum goal cannot be greater than maximum", Toast.LENGTH_SHORT).show()
            return
        }

        val goal = Goal(minGoal = minGoal, maxGoal = maxGoal)
        viewModel.setGoal(goal)
        Log.d("GoalsFragment", "Goals saved: Min=$minGoal, Max=$maxGoal")
        Toast.makeText(requireContext(), "Goals updated successfully", Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}