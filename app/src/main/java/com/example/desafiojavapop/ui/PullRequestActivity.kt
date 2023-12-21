package com.example.desafiojavapop.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.desafiojavapop.adapters.PullRequestAdapter
import com.example.desafiojavapop.database.AppDatabase
import com.example.desafiojavapop.databinding.ActivityPullRequestBinding
import com.example.desafiojavapop.model.HomeModel
import com.example.desafiojavapop.repository.PullRequestRepositoryImpl
import com.example.desafiojavapop.rest.RetrofitService
import com.example.desafiojavapop.usecase.FetchPullRequestsUseCase
import com.example.desafiojavapop.util.ResultWrapper
import com.example.desafiojavapop.viewmodel.PullRequestViewModel
import com.example.desafiojavapop.viewmodel.PullRequestViewModelFactory
import com.google.android.material.snackbar.Snackbar

class PullRequestActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPullRequestBinding
    private val viewModel: PullRequestViewModel by viewModels {
        PullRequestViewModelFactory(
            FetchPullRequestsUseCase(PullRequestRepositoryImpl(
            RetrofitService.apiService, AppDatabase.getDatabase(this)))
        )
    }

    private val pullRequestAdapter = PullRequestAdapter(this::openLink)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPullRequestBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        observeViewModel()

        val homeModel = intent.getSerializableExtra(EXTRA_FULL_NAME) as? HomeModel
        homeModel?.let { model ->
            model.repo?.let { model.owner.let{
                    it1 -> viewModel.loadPullRequests(it1.login, it) } }
        }
    }

    private fun setupRecyclerView() = with(binding.prRecyclerview) {
        layoutManager = LinearLayoutManager(context)
        setHasFixedSize(true)
        adapter = pullRequestAdapter
    }

    private fun observeViewModel() {
        viewModel.pullRequests.observe(this) { result ->
            when (result) {
                is ResultWrapper.Success -> pullRequestAdapter.setList(result.data)
                is ResultWrapper.Empty -> showEmptyPullList()
                is ResultWrapper.Failure -> showError()
            }
        }
    }

    private fun showEmptyPullList(){
        Snackbar.make(binding.root,com.example.desafiojavapop.R.string.empty_pr,
            Snackbar.LENGTH_LONG).show()
    }

    private fun showError() {
        Snackbar.make(binding.root,com.example.desafiojavapop.R.string.erro_pr,
            Snackbar.LENGTH_LONG).show()
    }

    private fun openLink(url: String) {
        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
    }

    companion object {
        const val EXTRA_FULL_NAME = "full_name"
    }
}
