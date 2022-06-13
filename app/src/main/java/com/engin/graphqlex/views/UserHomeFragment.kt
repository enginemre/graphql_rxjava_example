package com.engin.graphqlex.views

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.engin.graphqlex.UsersListQuery
import com.engin.graphqlex.adapters.UserRecyclerAdapter
import com.engin.graphqlex.databinding.FragmentUserHomeBinding
import com.engin.graphqlex.viewmodel.UserViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UserHomeFragment : Fragment() {

    private var _binding:FragmentUserHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var userRecyclerAdapter: UserRecyclerAdapter
    private val viewModel by viewModels<UserViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUserHomeBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
        viewModel.getUsers()
        observeUI()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initUI(){
        userRecyclerAdapter= UserRecyclerAdapter(arrayListOf<UsersListQuery.User>(),onClick())
        binding.userListRecyclerView.layoutManager  = LinearLayoutManager(requireContext()  .applicationContext)
        binding.userListRecyclerView.adapter =userRecyclerAdapter
        binding.userListRecyclerView.setHasFixedSize(true)
    }

    /**
     * Observing UI for user action
     *
     */
    private fun observeUI(){
        viewModel.users.observe(viewLifecycleOwner){
            val arrayListOfUser = arrayListOf<UsersListQuery.User>()
            arrayListOfUser.addAll(it)
            userRecyclerAdapter.updateList(arrayListOfUser)
        }
        viewModel.loading.observe(viewLifecycleOwner){
            if(it){
                binding.userListProgressBar.visibility = View.VISIBLE
                binding.userListRecyclerView.visibility = View.GONE
            }else{
                binding.userListProgressBar.visibility = View.GONE
                binding.userListRecyclerView.visibility = View.VISIBLE
            }
        }
        viewModel.error.observe(viewLifecycleOwner){
            if(it){
                binding.userListProgressBar.visibility = View.GONE
                binding.userListRecyclerView.visibility = View.GONE
                binding.userListErrorMessage.visibility = View.VISIBLE
            }
        }
    }

    /**
     *
     * Clicking action for recyclerview action
     *
     * @return function
     */
    private fun onClick():UserRecyclerAdapter.OnClickListener{
       return UserRecyclerAdapter.OnClickListener{
            val action = UserHomeFragmentDirections.actionUserHomeFragmentToUserDetailFragment(it.id.toString())
            findNavController().navigate(action)
        }

    }
}