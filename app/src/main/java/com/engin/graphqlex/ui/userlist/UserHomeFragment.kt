package com.engin.graphqlex.ui.userlist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.engin.graphqlex.UsersListQuery
import com.engin.graphqlex.ui.adapters.UserRecyclerAdapter
import com.engin.graphqlex.databinding.FragmentUserHomeBinding
import com.engin.graphqlex.base.IView
import com.engin.graphqlex.app.states.UserHomeState
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.CompositeDisposable


/**
 * Display [UsersListQuery.User]s.
 */
@AndroidEntryPoint
class UserHomeFragment : Fragment(), IView<UserHomeIntent, UserHomeState> {

    private var _binding: FragmentUserHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var userRecyclerAdapter: UserRecyclerAdapter
//    private val viewModel by viewModels<UserViewModel>()
    private val viewModel by viewModels<UserHomeViewModel>()
    // Used to manage the data flow lifecycle and avoid memory leak.
    private val disposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUserHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initUI() {
        userRecyclerAdapter = UserRecyclerAdapter(arrayListOf<UsersListQuery.User>(), onClick())
        binding.userListRecyclerView.layoutManager =
            LinearLayoutManager(requireContext().applicationContext)
        binding.userListRecyclerView.adapter = userRecyclerAdapter
        binding.userListRecyclerView.setHasFixedSize(true)
    }

    /**
     * Observing UI for user action
     *
     */
/*    private fun observeUI() {
        viewModel.users.observe(viewLifecycleOwner) {
            val arrayListOfUser = arrayListOf<UsersListQuery.User>()
            arrayListOfUser.addAll(it)
            userRecyclerAdapter.updateList(arrayListOfUser)
        }
        viewModel.loading.observe(viewLifecycleOwner) {
            if (it) {
                binding.userListProgressBar.visibility = View.VISIBLE
                binding.userListRecyclerView.visibility = View.GONE
            } else {
                binding.userListProgressBar.visibility = View.GONE
                binding.userListRecyclerView.visibility = View.VISIBLE
            }
        }
        viewModel.error.observe(viewLifecycleOwner) {
            if (it) {
                binding.userListProgressBar.visibility = View.GONE
                binding.userListRecyclerView.visibility = View.GONE
                binding.userListErrorMessage.visibility = View.VISIBLE
            }
        }
    }*/

    /**
     *
     * Clicking action for recyclerview action
     *
     * @return function
     */
    private fun onClick(): UserRecyclerAdapter.OnClickListener {
        return UserRecyclerAdapter.OnClickListener {
            val action =
                UserHomeFragmentDirections.actionUserHomeFragmentToUserDetailFragment(it.id.toString())
            findNavController().navigate(action)
        }

    }

    override fun onStart() {
        super.onStart()
        bind()
    }

    override fun onStop() {
        super.onStop()
        disposable.clear()
    }
    /**
     * Connect the [IView] with the [IViewModel]
     * We subscribe to the [IViewModel] before passing it the [IView]'s [IIntent]s.
     * If we were to pass [IIntent]s to the [IViewModel] before listening to it,
     * emitted [IState]s could be lost
     */
    private fun bind(){
        // Subscribe to the ViewModel and call render for every emitted state
        disposable.add(viewModel.states().subscribe(this::render))
        // Pass the UI's intents to the ViewModel
        viewModel.processIntents(intents())
    }

    override fun intents(): Observable<UserHomeIntent> {
        return Observable.merge(loadUserListIntent(), emptyIntent())
    }

    override fun render(state: UserHomeState) {
        binding.userListProgressBar.isVisible = state.isLoading
        if (state.data.isNotEmpty()) {
            binding.userListErrorMessage.visibility = View.GONE
            binding.userListRecyclerView.visibility = View.VISIBLE
            val arrayListOfUser = arrayListOf<UsersListQuery.User>()
            arrayListOfUser.addAll(state.data)
            userRecyclerAdapter.updateList(arrayListOfUser)
        }
        if (state.error != null) {
            binding.userListProgressBar.visibility = View.GONE
            binding.userListRecyclerView.visibility = View.GONE
            binding.userListErrorMessage.visibility = View.VISIBLE
        }
    }
    /**
     * The initial Intent the [IView] emit to convey to the [IViewModel]
     * that it is ready to receive data.
     * This initial Intent is also used to pass any parameters the [IViewModel] might need
     * to render the initial [IState] (e.g. the task id to load).
     */
    private fun loadUserListIntent(): Observable<UserHomeIntent.LoadListIntent> {
        return Observable.just(UserHomeIntent.LoadListIntent)
    }

    private fun emptyIntent(): Observable<UserHomeIntent> {
        return Observable.just(UserHomeIntent.EmptyIntent)
    }
}