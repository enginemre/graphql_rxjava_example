package com.engin.graphqlex.views.userdetail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.engin.graphqlex.databinding.FragmentUserDetaillBinding
import com.engin.graphqlex.mvibase.IView
import com.engin.graphqlex.views.userhome.UserHomeIntent
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.subjects.PublishSubject


@AndroidEntryPoint
class UserDetailFragment : Fragment(),IView<UserDetailIntent,UserDetailState> {

    private var _binding: FragmentUserDetaillBinding? = null
    private val binding get() = _binding!!
    private lateinit var id: String
//    private val viewModel by viewModels<UserDetailViewModel>()
    private val viewModel by viewModels<UserDetailViewModel>()

    private val disposable = CompositeDisposable()
    private val deletePublisher = PublishSubject.create<UserDetailIntent.DeleteUserIntent>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onStart() {
        super.onStart()
        bind()
    }


    /**
     * Connect the [IView] with the [IViewModel]
     * We subscribe to the [IViewModel] before passing it the [IView]'s [IIntent]s.
     * If we were to pass [IIntent]s to the [IViewModel] before listening to it,
     * emitted [IState]s could be lost
     */
    private fun bind() {
        // Subscribe to the ViewModel and call render for every emitted state
        disposable.add(viewModel.states().subscribe(this::render))
        // Pass the UI's intents to the ViewModel
        viewModel.processIntents(intents())
    }

    override fun onStop() {
        super.onStop()
        disposable.clear()
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
//        viewModel.getUserById(id)

//        observeUI()
    }

    override fun render(state: UserDetailState) {
        binding.userDetailLayout.visibility = View.INVISIBLE
        binding.userDetailProgressBar.isVisible = state.isLoading
        if(state.user != null){
            binding.userDetailErrorMessage.visibility = View.GONE
            binding.userDetailProgressBar.visibility = View.GONE
            binding.userDetailLayout.visibility = View.VISIBLE
            binding.data = state.user
        }
        if (state.deleteData != null){
            binding.userDetailErrorMessage.visibility = View.GONE
            binding.userDetailProgressBar.visibility = View.GONE
            binding.userDetailLayout.visibility = View.VISIBLE
            Toast.makeText(requireContext(), "Kullanıcı Silindi", Toast.LENGTH_LONG).show()
            findNavController().navigateUp()
        }
        if (state.error != null) {
            binding.userDetailErrorMessage.visibility = View.VISIBLE
            binding.userDetailProgressBar.visibility = View.GONE
            binding.userDetailLayout.visibility = View.GONE
            Toast.makeText(requireContext(), "Error Loading episodes", Toast.LENGTH_LONG).show()
        }
    }

    /**
     * Observing UI for user action
     *
     */
    /*private fun observeUI() {
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
    }*/

    private fun initUI() {
        // Delete current user
        binding.userDetailDeleteButton.setOnClickListener {  deleteUser()}
    }

    /**
     * Deleting user by user ID
     */
    private fun deleteUser() {
        deletePublisher.onNext(UserDetailIntent.DeleteUserIntent(id))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun intents(): Observable<UserDetailIntent> {
            return Observable.merge(loadUserIntent(), deleteIntent())
    }

    private fun deleteIntent(): Observable< UserDetailIntent.DeleteUserIntent> {
        return deletePublisher
    }
    /**
     * The initial Intent the [IView] emit to convey to the [IViewModel]
     * that it is ready to receive data.
     * This initial Intent is also used to pass any parameters the [IViewModel] might need
     * to render the initial [IState] (e.g. the task id to load).
     */
    private fun loadUserIntent(): Observable<UserDetailIntent> {
        return  Observable.just(UserDetailIntent.LoadUserIntent(id))
    }




}
