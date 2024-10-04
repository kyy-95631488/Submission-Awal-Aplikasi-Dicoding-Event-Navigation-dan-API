package com.callcenter.dicodingevent

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.callcenter.dicodingevent.databinding.ActivityEventDetailBinding
import com.google.android.material.snackbar.Snackbar
import com.squareup.picasso.Picasso
import android.text.Html
import android.text.method.LinkMovementMethod
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.view.View
import java.text.SimpleDateFormat
import java.util.Locale

class EventDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEventDetailBinding
    private val viewModel: EventViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_event_detail)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        // Set up the toolbar
        val toolbar = binding.toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true) // Show the back button

        if (isNetworkAvailable()) {
            val eventId = intent.getIntExtra("EVENT_ID", -1)
            if (eventId != -1) {
                // Show the loading spinner
                binding.loadingSpinner.visibility = View.VISIBLE

                // Fetch the event details using the ViewModel
                viewModel.fetchEventDetails(eventId)

                viewModel.event.observe(this, { event ->
                    event?.let {
                        // Hide the loading spinner
                        binding.loadingSpinner.visibility = View.GONE

                        binding.event = it
                        binding.eventTitle.text = it.name
                        binding.eventDescription.text = Html.fromHtml(it.description, Html.FROM_HTML_MODE_COMPACT)
                        binding.eventDescription.movementMethod = LinkMovementMethod.getInstance()

                        Picasso.get().load(it.mediaCover).into(binding.eventImage)

                        binding.eventBeginTime.text = getString(R.string.event_begin_time, it.beginTime)
                        binding.eventEndTime.text = getString(R.string.event_end_time, it.endTime)

                        val remainingQuota = it.quota - it.registrants
                        binding.eventRemainingQuota.text = getString(R.string.event_remaining_quota, remainingQuota)
                        binding.eventCityName.text = getString(R.string.event_city_name, it.cityName)
                        binding.eventOwnerName.text = getString(R.string.event_owner_name, it.ownerName)
                        binding.eventCategory.text = getString(R.string.event_category, it.category)

                        // Check if the event has passed
                        val eventEndTime = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).parse(it.endTime)?.time
                        val currentTime = System.currentTimeMillis()

                        if (eventEndTime != null && currentTime > eventEndTime) {
                            // Event has passed: hide the register button and show "Event Ditutup"
                            binding.registerButton.visibility = View.GONE
                            binding.kuotaHabisText.text = getString(R.string.event_closed)
                            binding.kuotaHabisText.visibility = View.VISIBLE
                        } else if (remainingQuota > 0) {
                            // Event is still open and quota is available
                            binding.registerButton.visibility = View.VISIBLE
                            binding.kuotaHabisText.visibility = View.GONE
                            binding.registerButton.setOnClickListener {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(event.link))
                                startActivity(intent)
                            }
                        } else {
                            // No remaining quota
                            binding.registerButton.visibility = View.GONE
                            binding.kuotaHabisText.text = getString(R.string.kuota_habis)
                            binding.kuotaHabisText.visibility = View.VISIBLE
                        }
                    }
                })

                viewModel.errorMessage.observe(this, { errorMessage ->
                    errorMessage?.let {
                        // Hide the loading spinner
                        binding.loadingSpinner.visibility = View.GONE
                        Snackbar.make(binding.root, it, Snackbar.LENGTH_LONG).show()
                    }
                })

                viewModel.isLoading.observe(this, { isLoading ->
                    if (isLoading) {
                        binding.loadingSpinner.visibility = View.VISIBLE
                    } else {
                        binding.loadingSpinner.visibility = View.GONE
                    }
                })
            }
        } else {
            showNoInternetMessage()
        }
    }

    private fun showNoInternetMessage() {
        binding.eventTitle.visibility = View.GONE
        binding.eventDescription.visibility = View.GONE
        binding.eventImage.visibility = View.GONE
        binding.eventBeginTime.visibility = View.GONE
        binding.eventEndTime.visibility = View.GONE
        binding.eventRemainingQuota.visibility = View.GONE
        binding.eventCityName.visibility = View.GONE
        binding.eventOwnerName.visibility = View.GONE
        binding.eventCategory.visibility = View.GONE
        binding.registerButton.visibility = View.GONE

        binding.noInternetText.visibility = View.VISIBLE
        binding.noInternetText.text = getString(R.string.no_internet_message)
    }

    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork
        val capabilities = connectivityManager.getNetworkCapabilities(network)
        return capabilities != null && capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
