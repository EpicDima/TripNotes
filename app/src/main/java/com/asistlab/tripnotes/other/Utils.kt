package com.asistlab.tripnotes.other

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.asistlab.tripnotes.R
import com.asistlab.tripnotes.data.model.Trip
import com.google.firebase.auth.FirebaseAuth

/**
 * @author EpicDima
 */
fun View.show() {
    this.visibility = View.VISIBLE
}

fun View.hide() {
    this.visibility = View.INVISIBLE
}

fun View.gone() {
    this.visibility = View.GONE
}

fun EditText.afterTextChanged(afterTextChanged: (String) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(editable: Editable?) {
            afterTextChanged.invoke(editable.toString())
        }

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
    })
}

fun RecyclerView.addDelimiter(context: Context) {
    addItemDecoration(
        DividerItemDecoration(
            ContextCompat.getDrawable(
                context,
                R.drawable.divider
            )!!
        )
    )
}

fun Fragment.supportActionBar(): ActionBar
        = (requireActivity() as AppCompatActivity).supportActionBar!!

fun Fragment.showBackButton() {
    supportActionBar().setDisplayHomeAsUpEnabled(true)
    supportActionBar().setDisplayShowHomeEnabled(true)
}

fun getImageName(userId: String, trip: Trip): String {
    return userId + "-" + trip.id
}

fun getImageName(auth: FirebaseAuth, trip: Trip): String {
    return getImageName(auth.currentUser!!.uid, trip)
}
