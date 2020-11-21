package com.asistlab.tripnotes.ui.statistics

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.asistlab.tripnotes.databinding.FragmentStatisticsBinding
import dagger.hilt.android.AndroidEntryPoint

/**
 * @author EpicDima
 */
@AndroidEntryPoint
class StatisticsFragment : Fragment() {

    private val viewModel: StatisticsViewModel by viewModels()
    private lateinit var binding: FragmentStatisticsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentStatisticsBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.categoriesCountMap.observe(viewLifecycleOwner) { map ->
            binding.categoryBarchart.animate(LinkedHashMap(map
                .mapKeys { getString(it.key.key) }
                .mapValues { it.value.toFloat() })
            )
        }
        viewModel.durationCountMap.observe(viewLifecycleOwner) { map ->
            binding.durationLinechart.animate(LinkedHashMap(map
                .mapKeys { it.key.toString() }
                .mapValues { it.value.toFloat() })
            )
        }
        viewModel.locationCountMap.observe(viewLifecycleOwner) { map ->
            binding.locationLinechart.animate(LinkedHashMap(map
                .mapKeys { it.key.toString() }
                .mapValues { it.value.toFloat() })
            )
        }
    }
}