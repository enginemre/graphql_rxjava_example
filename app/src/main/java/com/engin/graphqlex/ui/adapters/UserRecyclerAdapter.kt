package com.engin.graphqlex.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.engin.graphqlex.UsersListQuery
import com.engin.graphqlex.databinding.ItemUserBinding

/**
 *
 * Users Recyclerview adapter
 *
 * @param userList user of spaceX
 * @param onClickListener Click listener for RV view
 *
 */
class UserRecyclerAdapter(
    private val userList: ArrayList<UsersListQuery.User>,
    private val onClickListener: OnClickListener,
) :
    RecyclerView.Adapter<UserRecyclerAdapter.UserViewHolder>() {
    private lateinit var binding: ItemUserBinding

    class UserViewHolder(
        private val binding: ItemUserBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(user: UsersListQuery.User) {
            binding.data = user
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        binding = ItemUserBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return UserViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = userList[position]
        holder.bind(user)
        holder.itemView.setOnClickListener {
            onClickListener.onClick(userList[position])
        }

    }

    /**
     *  Updating RV
     */
    fun updateList(list: ArrayList<UsersListQuery.User>) {
        userList.clear()
        userList.addAll(list)
        notifyDataSetChanged()
    }

    override fun getItemCount() = userList.size

    class OnClickListener(val clickListener: (item: UsersListQuery.User) -> Unit) {
        fun onClick(item: UsersListQuery.User) = clickListener(item)
    }
}