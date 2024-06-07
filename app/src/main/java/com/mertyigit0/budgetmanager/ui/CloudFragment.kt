package com.mertyigit0.budgetmanager.ui

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.mertyigit0.budgetmanager.adapters.CloudAdapter
import com.mertyigit0.budgetmanager.data.CloudItem
import com.mertyigit0.budgetmanager.databinding.FragmentCloudBinding

class CloudFragment : Fragment() {

    private var _binding: FragmentCloudBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentCloudBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.cloudRecyclerView.layoutManager = LinearLayoutManager(requireContext())


        val items = getDownloadUrlsFromPreferences()


        val adapter = CloudAdapter(items)
        binding.cloudRecyclerView.adapter = adapter
    }

    private fun getDownloadUrlsFromPreferences(): List<CloudItem> {
        val sharedPreferences = requireContext().getSharedPreferences("DownloadUrls", Context.MODE_PRIVATE)
        val allEntries = sharedPreferences.all

        val items = mutableListOf<CloudItem>()
        for ((key, value) in allEntries) {
            if (value is String) {
                items.add(CloudItem(key, value))
            }
        }
        return items
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
