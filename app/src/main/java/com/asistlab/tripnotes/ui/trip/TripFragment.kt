package com.asistlab.tripnotes.ui.trip

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.asistlab.tripnotes.R
import com.asistlab.tripnotes.databinding.FragmentTripBinding
import com.asistlab.tripnotes.other.addDelimiter
import com.asistlab.tripnotes.other.afterTextChanged
import com.asistlab.tripnotes.other.showBackButton
import com.asistlab.tripnotes.other.supportActionBar
import com.asistlab.tripnotes.ui.choose.ChoosePointActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.datepicker.MaterialDatePicker
import dagger.hilt.android.AndroidEntryPoint

/**
 * @author EpicDima
 */
@AndroidEntryPoint
class TripFragment : Fragment() {

    companion object {
        const val SELECT_IMAGE_REQUEST_CODE = 4543
        const val CHOOSE_POINT_REQUEST_CODE = 3526

        fun newInstance(tripId: Long = 0) = TripFragment().apply {
            this.tripId = tripId
        }
    }

    private var tripId: Long = 0
    private val viewModel: TripViewModel by viewModels()
    private lateinit var binding: FragmentTripBinding

    private var edit: MenuItem? = null
    private var done: MenuItem? = null
    private var ok: MenuItem? = null
    private var toMap: MenuItem? = null

    private var editDone = true
    private var firstTime: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTripBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        showBackButton()
        viewModel.init(tripId)
        viewModel.page.observe(viewLifecycleOwner) { updateAllOnPageChange() }
        viewModel.success.observe(viewLifecycleOwner) { requireActivity().finish() }

        setAdapter()
        setListeners()
        setTripObserver()
        setEdittextListeners()
        setInputObservers()
    }

    private fun updateOnPageChange(
        image: Boolean? = null, edit: Boolean? = null, done: Boolean? = null, ok: Boolean? = null,
        toMap: Boolean? = null, homeIcon: Int? = null, title: Int? = null
    ) {
        if (image != null) {
            binding.image.isEnabled = image
        }
        if (edit != null) {
            this.edit?.isVisible = edit
        }
        if (done != null) {
            this.done?.isVisible = done
        }
        if (ok != null) {
            this.ok?.isVisible = ok
        }
        if (toMap != null) {
            this.toMap?.isVisible = toMap
        }
        supportActionBar().apply {
            if (homeIcon != null) {
                setHomeAsUpIndicator(homeIcon)
            }
            this.title = title?.let { getString(it) }
        }
    }

    private fun updateAllOnPageChange() {
        val page = viewModel.page.value
        binding.page = page
        when (page) {
            Page.SHOW -> updateOnPageChange(
                image = false,
                edit = true,
                done = true,
                ok = false,
                toMap = true,
                homeIcon = R.drawable.ic_arrow_back_24dp,
                title = R.string.title_trip
            )
            Page.CREATE -> updateOnPageChange(
                image = true,
                edit = false,
                done = false,
                ok = true,
                toMap = false,
                homeIcon = R.drawable.ic_close_24dp,
                title = R.string.create_trip
            )
            Page.EDIT -> updateOnPageChange(
                image = true,
                edit = false,
                done = false,
                ok = true,
                toMap = false,
                homeIcon = R.drawable.ic_close_24dp,
                title = R.string.edit_trip
            )
            else -> {
            }
        }
        if (!editDone) {
            updateOnPageChange(edit = false, done = false)
        }
    }

    private fun setInputObservers() {
        viewModel.formState.observe(viewLifecycleOwner) {
            val state = it ?: return@observe
            if (state.nameError != null) {
                binding.nameEdittext.error = getString(state.nameError)
            }
            if (state.descriptionError != null) {
                binding.descriptionEdittext.error = getString(state.descriptionError)
            }
        }
        viewModel.error.observe(viewLifecycleOwner) {
            if (it != null) {
                Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun setTripObserver() {
        viewModel.tripLiveData.observe(viewLifecycleOwner) {
            binding.apply {
                if (firstTime) {
                    Glide.with(image)
                        .load(viewModel.getImageStorage())
                        .placeholder(R.drawable.ic_default_trip_image_24dp)
                        .error(R.drawable.ic_default_trip_image_24dp)
                        .into(binding.image)
                    if (it.id != 0L && it.isOver()) {
                        editDone = false
                    }
                    firstTime = false
                }
                name.text = it.name
                nameEdittext.setText(it.name)
                description.text = it.name
                descriptionEdittext.setText(it.name)
                start.text = it.startDateToString()
                end.text = it.endDateToString()
            }
        }
    }

    private fun setAdapter() {
        val adapter = PointsAdapter()
        binding.pointsRecyclerView.adapter = adapter
        binding.pointsRecyclerView.addDelimiter(requireContext())
        binding.pointsRecyclerView.layoutManager = LinearLayoutManager(context)
        viewModel.points.observe(viewLifecycleOwner) {
            adapter.submitList(it)
            binding.removePoint.isEnabled = it.isNotEmpty()
            binding.addPoint.isEnabled = (it.size < TripViewModel.MAX_POINTS)
        }
    }

    private fun setListeners() {
        binding.image.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, SELECT_IMAGE_REQUEST_CODE)
        }
        binding.chooseTime.setOnClickListener { chooseTripTime() }
        binding.addPoint.setOnClickListener {
            startActivityForResult(
                Intent(requireContext(), ChoosePointActivity::class.java),
                CHOOSE_POINT_REQUEST_CODE
            )
        }
        binding.removePoint.setOnClickListener { viewModel.removePoint() }
        binding.delete.setOnClickListener {
            viewModel.delete()
            requireActivity().finish()
        }
    }

    private fun setEdittextListeners() {
        val onInputChange: (String) -> Unit = {
            viewModel.inputChanged(
                binding.nameEdittext.text.toString(),
                binding.descriptionEdittext.text.toString()
            )
        }

        binding.nameEdittext.afterTextChanged(onInputChange)
        binding.descriptionEdittext.afterTextChanged(onInputChange)
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        requireActivity().menuInflater.inflate(R.menu.trip_menu, menu)
        edit = menu.findItem(R.id.edit)
        done = menu.findItem(R.id.done)
        ok = menu.findItem(R.id.ok)
        toMap = menu.findItem(R.id.to_map)
        super.onCreateOptionsMenu(menu, inflater)
        updateAllOnPageChange()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        R.id.edit -> {
            viewModel.edit()
            true
        }
        R.id.done -> {
            viewModel.done()
            true
        }
        R.id.ok -> {
            viewModel.save()
            true
        }
        R.id.to_map -> {
            val linkForMap = viewModel.getLinkForMap()
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(linkForMap)
            startActivity(intent)
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    private fun chooseTripTime() {
        MaterialDatePicker.Builder.dateRangePicker().build().apply {
            addOnPositiveButtonClickListener {
                viewModel.setStartAndEnd(it.first!!, it.second!!)
            }
        }.show(requireActivity().supportFragmentManager, null)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode != RESULT_OK) {
            return
        }
        when (requestCode) {
            CHOOSE_POINT_REQUEST_CODE -> {
                if (data != null) {
                    addPoint(data)
                }
            }
            SELECT_IMAGE_REQUEST_CODE -> {
                if (data != null) {
                    selectImage(data)
                }
            }
        }
    }

    private fun addPoint(intent: Intent) {
        val address = intent.getStringExtra(ChoosePointActivity.ADDRESS_KEY)!!
        val location = intent.getParcelableExtra<LatLng>(ChoosePointActivity.LOCATION_KEY)!!
        viewModel.addPoint(address, location)
    }

    private fun selectImage(intent: Intent) {
        val imageUri: Uri? = intent.data
        if (imageUri != null) {
            Glide.with(this)
                .asBitmap()
                .load(imageUri)
                .into(object : CustomTarget<Bitmap>() {
                    override fun onResourceReady(
                        resource: Bitmap,
                        transition: Transition<in Bitmap>?
                    ) {
                        binding.image.setImageBitmap(resource)
                        viewModel.setImage(resource)
                    }

                    override fun onLoadCleared(placeholder: Drawable?) {}
                })
        }
    }
}