package com.example.desafiojavapop.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.desafiojavapop.adapters.HomeAdapter
import com.example.desafiojavapop.database.AppDatabase
import com.example.desafiojavapop.databinding.ActivityHomeBinding
import com.example.desafiojavapop.model.HomeModel
import com.example.desafiojavapop.repository.HomeRepositoryImpl
import com.example.desafiojavapop.rest.RetrofitService
import com.example.desafiojavapop.usecase.HomeFetchRepositoriesUseCase
import com.example.desafiojavapop.util.ResultWrapper
import com.example.desafiojavapop.viewmodel.HomeViewModel
import com.example.desafiojavapop.viewmodel.HomeViewModelFactory

class HomeActivity : AppCompatActivity() {


    private lateinit var binding: ActivityHomeBinding
    private lateinit var progressBar: ProgressBar

    private val viewModel: HomeViewModel by viewModels {
        HomeViewModelFactory(HomeFetchRepositoriesUseCase(
            HomeRepositoryImpl(RetrofitService.apiService,
                AppDatabase.getDatabase(this), )
        ))
    }

    private val homeAdapter = HomeAdapter { repo -> navigateToPullRequestPage(repo) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        progressBar = binding.progressBar
        setupUI()
        observeViewModel()
        viewModel.fetchRepositories(REPO_QUERY, SORT_CRITERIA)
    }

    private fun setupUI() {
        setupRecyclerview()
        setupScrollListener()
    }

    private fun toggleProgressBar(isVisible: Boolean) {
        progressBar.visibility = if (isVisible) View.VISIBLE else View.GONE
    }

    private fun setupRecyclerview() {
        binding.homeRecyclerview.let{
            it.layoutManager = LinearLayoutManager(this)
            it.setHasFixedSize(true)
            it.adapter = homeAdapter
        }
    }

    private fun observeViewModel() {
        viewModel.repoList.observe(this) { result ->
            when (result) {
                is ResultWrapper.Success -> homeAdapter.setList(result.data)
                is ResultWrapper.Failure -> showError(result.exception.message)
                else -> {

                }
            }
        }
        viewModel.isLoading.observe(this) { isLoading ->
            toggleProgressBar(isLoading)

        }

    }

    private fun setupScrollListener() {
        binding.homeRecyclerview.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val totalItemCount = layoutManager.itemCount
                val lastVisibleItem = layoutManager.findLastVisibleItemPosition()
                if (!viewModel.isLoading.value!! && lastVisibleItem == totalItemCount - 1) {
                    viewModel.loadNextPage(REPO_QUERY, SORT_CRITERIA)
                }
            }
        })
    }

    private fun showError(message: String?) {
        Toast.makeText(this, "Erro: $message", Toast.LENGTH_LONG).show()
    }

    private fun navigateToPullRequestPage(homeModel: HomeModel) {
        Intent(this, PullRequestActivity::class.java).apply {
            putExtra("full_name", homeModel)
            startActivity(this)
        }
    }

    companion object {
        private const val REPO_QUERY = "language:Java"
        private const val SORT_CRITERIA = "stars"

    }
}

