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
import com.asistlab.tripnotes.data.model.Trip
import com.asistlab.tripnotes.databinding.FragmentTripBinding
import com.asistlab.tripnotes.other.*
import com.asistlab.tripnotes.ui.choose.ChoosePointActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
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

    private var enableEdit: Boolean = true
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
        setEdittextListeners()
        setInputObservers()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        requireActivity().menuInflater.inflate(R.menu.trip_menu, menu)
        edit = menu.findItem(R.id.edit)
        done = menu.findItem(R.id.done)
        ok = menu.findItem(R.id.ok)
        toMap = menu.findItem(R.id.to_map)
        super.onCreateOptionsMenu(menu, inflater)
        setTripObserver()
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

    private fun updateOnPageChange(
        image: Boolean, edit: Boolean, done: Boolean, ok: Boolean,
        toMap: Boolean, homeIcon: Int, title: Int
    ) {
        binding.image.isEnabled = image
        this.edit?.isVisible = edit
        this.done?.isVisible = done
        this.ok?.isVisible = ok
        this.toMap?.isVisible = toMap
        supportActionBar().apply {
            setHomeAsUpIndicator(homeIcon)
            this.title = getString(title)
        }
    }

    private fun updateAllOnPageChange() {
        val page = viewModel.page.value
        binding.page = page
        when (page) {
            Page.SHOW -> updateOnPageChange(
                image = false,
                edit = enableEdit,
                done = enableEdit,
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
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .skipMemoryCache(true)
                        .placeholder(R.drawable.ic_default_trip_image_24dp)
                        .error(R.drawable.ic_default_trip_image_24dp)
                        .into(image)
                    if (it.id != 0L && it.isOver()) {
                        enableEdit = false
                    }
                    firstTime = false
                    updateAllOnPageChange()
                }
                name.text = it.name
                nameEdittext.setText(it.name)
                if (it.description.isNotEmpty()) {
                    description.text = it.description
                    descriptionEdittext.setText(it.description)
                } else {
                    description.gone()
                }
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
        binding.delete.setOnClickListener { openDeleteConfirmationDialog() }
    }

    private fun openDeleteConfirmationDialog() {
        MaterialAlertDialogBuilder(requireContext()).apply {
            setTitle(getString(R.string.delete_trip_dialog_title))
            setMessage(R.string.delete_trip_dialog_message)
            setPositiveButton(getString(R.string.yes)) { _, _ ->
                viewModel.delete()
                requireActivity().finish()
            }
            setNegativeButton(getString(R.string.cancel)) { dialog, _ -> dialog.cancel() }
        }.create().show()
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
        val location2 = Trip.LatLng(location.latitude, location.longitude)
        viewModel.addPoint(address, location2)
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