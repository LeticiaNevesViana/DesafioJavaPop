package com.example.desafiojavapop.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.desafiojavapop.adapters.HomeAdapter
import com.example.desafiojavapop.database.AppDatabase
import com.example.desafiojavapop.databinding.ActivityHomeBinding
import com.example.desafiojavapop.model.HomeModel
import com.example.desafiojavapop.repository.HomeRepositoryImpl
import com.example.desafiojavapop.rest.RetrofitService
import com.example.desafiojavapop.usecase.HomeFetchRepositoriesUseCase
import com.example.desafiojavapop.util.NetworkUtils
import com.example.desafiojavapop.util.ResultWrapper
import com.example.desafiojavapop.viewmodel.HomeViewModel
import com.example.desafiojavapop.viewmodel.HomeViewModelFactory


class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private lateinit var progressBar: ProgressBar
    private val homeAdapter = HomeAdapter { repo ->
        navigateToPullRequestPage(repo.id)
    }

    private val viewModel: HomeViewModel by viewModels {
        HomeViewModelFactory(
            HomeFetchRepositoriesUseCase(
                HomeRepositoryImpl(
                    RetrofitService.apiService,
                    AppDatabase.getDatabase(this),
                    NetworkUtils(this)
                )
            )
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        progressBar = binding.progressBarScroll
        setupUI()
        observeViewModel()
        viewModel.fetchRepositories(REPO_QUERY, SORT_CRITERIA)
    }

    private fun setupUI() {
        setupRecyclerview()
        setupScrollListener()
        pullToRefresh()
    }

    private fun observeViewModel() {
        viewModel.repoList.observe(this) { result ->
            when (result) {
                is ResultWrapper.Success -> homeAdapter.updateList(result.data)
                is ResultWrapper.Failure -> showError(result.exception.message)
                else -> {}
            }
        }

        viewModel.isLoading.observe(this) { isLoading ->
            toggleProgressBar(isLoading, isLoadingForRefresh = true)
        }
        viewModel.isLoadingMore.observe(this) { isLoadingMore ->
            toggleProgressBar(isLoadingMore, isLoadingForRefresh = false)
        }
        viewModel.currentRepoList.observe(this) { updatedList ->
            homeAdapter.updateList(updatedList)
        }

        viewModel.isLastPageLiveData.observe(this) { isLast ->
            binding.noMorpages.visibility = if (isLast) View.VISIBLE else View.GONE
        }
    }

    private fun setupRecyclerview() {
        binding.homeRecyclerview.apply {
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(true)
            adapter = homeAdapter
        }
    }

    private fun setupScrollListener() {
        binding.homeRecyclerview.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                handleScroll(dy)
            }
        })
    }

    private fun handleScroll(dy: Int) {
        if (dy > 0) {
            val layoutManager = binding.homeRecyclerview.layoutManager as LinearLayoutManager
            val totalItemCount = layoutManager.itemCount
            val lastVisibleItem = layoutManager.findLastVisibleItemPosition()
            val isNotLoadingMore = viewModel.isLoadingMore.value ?: false // Evita NullPointerException
            if (!isNotLoadingMore && lastVisibleItem == totalItemCount - 1) {
                viewModel.loadNextPage(REPO_QUERY, SORT_CRITERIA)
            }
        }
    }


    private fun pullToRefresh() {
        binding.pullRefresh.setOnRefreshListener {
            viewModel.fetchRepositories(REPO_QUERY, SORT_CRITERIA, isRefresh = true)
        }

        viewModel.refreshComplete.observe(this) { isComplete ->
            if (isComplete) {
                binding.pullRefresh.isRefreshing = false
            }
        }
    }

    private fun toggleProgressBar(isVisible: Boolean, isLoadingForRefresh: Boolean) {
        progressBar.visibility = if (isVisible && !isLoadingForRefresh) View.VISIBLE else View.GONE

    }
    private fun showError(message: String?) {
        Toast.makeText(this, "Erro: $message", Toast.LENGTH_LONG).show()
    }

    private fun navigateToPullRequestPage(repoId: Int) {
        val deepLink = Uri.Builder()
            .scheme("app")
            .authority("desafiojavapop")
            .appendPath("pullrequest")
            .appendQueryParameter("repoId", repoId.toString())
            .build()

        val intent = Intent(Intent.ACTION_VIEW, deepLink)
        startActivity(intent)
    }

    companion object {
        private const val REPO_QUERY = "language:Java"
        private const val SORT_CRITERIA = "stars"
    }
}
