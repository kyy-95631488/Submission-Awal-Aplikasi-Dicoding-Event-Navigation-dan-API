package com.callcenter.dicodingevent

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.callcenter.dicodingevent.databinding.FragmentEventListBinding

class EventListFragment : Fragment() {

    private lateinit var binding: FragmentEventListBinding
    private val viewModel: EventViewModel by viewModels()
    private lateinit var adapter: EventAdapter

    companion object {
        private const val ARG_STATUS = "status"

        fun newInstance(status: Int): EventListFragment {
            val fragment = EventListFragment()
            val args = Bundle().apply {
                putInt(ARG_STATUS, status)
            }
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentEventListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize views
        val loadingSpinner: ProgressBar = requireActivity().findViewById(R.id.loading_spinner)
        val errorMessage: TextView = requireActivity().findViewById(R.id.error_message)

        adapter = EventAdapter()
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter

        val status = arguments?.getInt(ARG_STATUS) ?: 1
        viewModel.fetchEvents(status)

        viewModel.events.observe(viewLifecycleOwner, { events ->
            if (events.isNotEmpty()) {
                adapter.submitList(events)
                binding.recyclerView.visibility = View.VISIBLE
                errorMessage.visibility = View.GONE
            } else {
                binding.recyclerView.visibility = View.GONE
                errorMessage.visibility = View.GONE
            }
        })

        viewModel.isLoading.observe(viewLifecycleOwner, { isLoading ->
            if (isLoading) {
                loadingSpinner.visibility = View.VISIBLE
                binding.recyclerView.visibility = View.GONE
                errorMessage.visibility = View.GONE
            } else {
                loadingSpinner.visibility = View.GONE
            }
        })

        viewModel.errorMessage.observe(viewLifecycleOwner, { errorMessageText ->
            if (!errorMessageText.isNullOrEmpty()) {
                // Tampilkan pesan kesalahan jika spinner sudah disembunyikan
                if (loadingSpinner.visibility == View.GONE) {
                    errorMessage.text = errorMessageText
                    errorMessage.visibility = View.VISIBLE
                    binding.recyclerView.visibility = View.GONE
                }
            }
        })

        binding.searchView.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let { viewModel.searchEvents(status, it) }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let { viewModel.searchEvents(status, it) }
                return true
            }
        })
    }
}
