package com.engin.graphqlex.views

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.engin.graphqlex.databinding.FragmentUserDetaillBinding
import com.engin.graphqlex.viewmodel.UserDetailViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class UserDetailFragment : Fragment() {

    private var _binding: FragmentUserDetaillBinding? = null
    private val binding get() = _binding!!
    private lateinit var id: String
    private val viewModel by viewModels<UserDetailViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUserDetaillBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Getting user id from [userListFragment]
        arguments?.let {
            id = UserDetailFragmentArgs.fromBundle(it).userId
        }
        initUI()
        // Getting user by id
        viewModel.getUserById(id)
        // Delete current user
        binding.userDetailDeleteButton.setOnClickListener { deleteUser() }
        observeUI()
    }

    /**
     * Observing UI for user action
     *
     */
    private fun observeUI() {
        viewModel.user.observe(viewLifecycleOwner) {
            binding.data = it!!
        }
        viewModel.loading.observe(viewLifecycleOwner) {
            // Showing progress bar
            if (it) {
                binding.userDetailProgressBar.visibility = View.VISIBLE
                binding.userDetailLayout.visibility = View.GONE
            } else {
                binding.userDetailProgressBar.visibility = View.GONE
                binding.userDetailLayout.visibility = View.VISIBLE
            }
        }

        viewModel.error.observe(viewLifecycleOwner) {
            // Showing error screen
            if (it) {
                binding.userDetailProgressBar.visibility = View.GONE
                binding.userDetailLayout.visibility = View.GONE
                binding.userDetailErrorMessage.visibility = View.VISIBLE
            }
        }
    }

    private fun initUI() {}

    /**
     * Deleting user by user ID
     */
    private fun deleteUser() {
        viewModel.deleteUser(id)
        val snackBar = Snackbar
            .make(binding.userDetailMain, "Kullanıcı silindi", Snackbar.LENGTH_LONG)
            .setAction("Geri") {
                findNavController().popBackStack()
            }
        snackBar.duration = Snackbar.LENGTH_LONG
        snackBar.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}
