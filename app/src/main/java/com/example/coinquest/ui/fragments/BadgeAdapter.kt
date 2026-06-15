package com.example.coinquest.ui.fragments

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.coinquest.data.Badge
import com.example.coinquest.databinding.ItemBadgeBinding

class BadgeAdapter(private var badges: List<Badge>) : RecyclerView.Adapter<BadgeAdapter.BadgeViewHolder>() {

    class BadgeViewHolder(val binding: ItemBadgeBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BadgeViewHolder {
        val binding = ItemBadgeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BadgeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BadgeViewHolder, position: Int) {
        val badge = badges[position]
        holder.binding.tvBadgeName.text = badge.name
        holder.binding.ivBadgeIcon.setImageResource(badge.iconResId)
        holder.binding.root.alpha = if (badge.isEarned) 1.0f else 0.3f
    }

    override fun getItemCount() = badges.size

    fun updateBadges(newBadges: List<Badge>) {
        badges = newBadges
        notifyDataSetChanged()
    }
}
