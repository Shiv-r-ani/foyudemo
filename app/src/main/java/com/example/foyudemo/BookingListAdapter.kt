package com.example.foyudemo

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

class BookingListAdapter(
    private val onShare      : (BookingEntity) -> Unit,
    private val onCancel     : (BookingEntity) -> Unit,
    private val onReschedule : (BookingEntity) -> Unit
) : ListAdapter<BookingEntity, BookingListAdapter.VH>(Diff()) {

    class VH(view: View) : RecyclerView.ViewHolder(view) {
        val tvService     : TextView    = view.findViewById(R.id.tvCardService)
        val tvPackage     : TextView    = view.findViewById(R.id.tvCardPackage)
        val tvDateTime    : TextView    = view.findViewById(R.id.tvCardDateTime)
        val tvAddress     : TextView    = view.findViewById(R.id.tvCardAddress)
        val btnShare      : ImageButton = view.findViewById(R.id.btnCardShare)
        val btnCancel     : Button      = view.findViewById(R.id.btnCardCancel)
        val btnReschedule : Button      = view.findViewById(R.id.btnCardReschedule)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        VH(LayoutInflater.from(parent.context)
            .inflate(R.layout.item_booking_card, parent, false))

    override fun onBindViewHolder(holder: VH, position: Int) {
        val b = getItem(position)
        holder.tvService.text  = b.serviceName
        holder.tvPackage.text  = "${b.packageName} · ${b.packagePrice}"
        holder.tvDateTime.text = "📅 ${b.date}  🕐 ${b.time}"
        holder.tvAddress.text  = "📍 ${b.address}"

        holder.btnShare.setOnClickListener      { onShare(b) }
        holder.btnCancel.setOnClickListener     { onCancel(b) }
        holder.btnReschedule.setOnClickListener { onReschedule(b) }
    }

    class Diff : DiffUtil.ItemCallback<BookingEntity>() {
        override fun areItemsTheSame(a: BookingEntity, b: BookingEntity) = a.id == b.id
        override fun areContentsTheSame(a: BookingEntity, b: BookingEntity) = a == b
    }
}