package com.example.desafiojavapop.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.desafiojavapop.R
import com.example.desafiojavapop.adapters.PullRequestAdapter
import com.example.desafiojavapop.database.AppDatabase
import com.example.desafiojavapop.databinding.ActivityPullRequestBinding
import com.example.desafiojavapop.model.HomeModel
import com.example.desafiojavapop.model.PullRequestModel
import com.example.desafiojavapop.repository.HomeRepositoryImpl
import com.example.desafiojavapop.repository.PullRequestRepositoryImpl
import com.example.desafiojavapop.rest.RetrofitService
import com.example.desafiojavapop.usecase.FetchPullRequestsUseCase
import com.example.desafiojavapop.util.NetworkUtils
import com.example.desafiojavapop.util.ResultWrapper
import com.example.desafiojavapop.viewmodel.PullRequestViewModel
import com.example.desafiojavapop.viewmodel.PullRequestViewModelFactory
import com.google.android.material.snackbar.Snackbar

class PullRequestActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPullRequestBinding
    private val viewModel: PullRequestViewModel by viewModels {
        PullRequestViewModelFactory(
            FetchPullRequestsUseCase(PullRequestRepositoryImpl(
                RetrofitService.apiService,
                AppDatabase.getDatabase(this),
                NetworkUtils(this)
            )),
            HomeRepositoryImpl(
                RetrofitService.apiService,
                AppDatabase.getDatabase(this),
                NetworkUtils(this)
            )
        )
    }

    private val pullRequestAdapter = PullRequestAdapter(this::openLink)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPullRequestBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        observeViewModel()

        val repoId = intent?.data?.getQueryParameter("repoId")?.toIntOrNull()

        repoId?.let {
            viewModel.loadRepositoryDetailsFromDb(it)
        }
    }

    private fun setupRecyclerView()=
        binding.prRecyclerview.apply {
        layoutManager = LinearLayoutManager(context)
        setHasFixedSize(true)
        adapter = pullRequestAdapter
    }

    private fun observeViewModel() {
        viewModel.pullRequests.observe(this) { result ->
            when (result) {
                is ResultWrapper.Success -> handleSuccess(result.data)
                is ResultWrapper.Failure -> showError()
                else -> showUnexpectedError()
            }
        }

    }

    private fun handleSuccess(data: List<PullRequestModel>) {
        if (data.isEmpty()) {
            showNoPullRequestsView()
        } else {
            showPullRequestsView(data)
        }
    }

    private fun showNoPullRequestsView() {
        binding.noPullRequests.visibility = View.VISIBLE
        binding.prRecyclerview.visibility = View.GONE
    }

    private fun showPullRequestsView(data: List<PullRequestModel>) {
        binding.noPullRequests.visibility = View.GONE
        binding.prRecyclerview.visibility = View.VISIBLE
        pullRequestAdapter.setList(data)
    }

    private fun showError() {
        Snackbar.make(binding.root, R.string.erro_pr, Snackbar.LENGTH_LONG).show()
    }
    private fun showUnexpectedError() {
        Snackbar.make(binding.root, R.string.erro_pr, Snackbar.LENGTH_LONG).show()
    }

    private fun openLink(url: String) {
        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
    }


}
