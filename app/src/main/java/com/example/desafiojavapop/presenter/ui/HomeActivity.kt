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
import com.example.desafiojavapop.R
import com.example.desafiojavapop.adapters.HomeAdapter
import com.example.desafiojavapop.database.AppDatabase
import com.example.desafiojavapop.databinding.ActivityHomeBinding
import com.example.desafiojavapop.repository.HomeRepositoryImpl
import com.example.desafiojavapop.rest.RetrofitService
import com.example.desafiojavapop.usecase.HomeFetchRepositoriesUseCase
import com.example.desafiojavapop.util.LoadType
import com.example.desafiojavapop.util.NetworkUtils
import com.example.desafiojavapop.util.ResultWrapper
import com.example.desafiojavapop.viewmodel.HomeViewModel
import com.example.desafiojavapop.viewmodel.HomeViewModelFactory
import com.google.android.material.snackbar.Snackbar

class HomeActivity : AppCompatActivity() {

    private val binding: ActivityHomeBinding by lazy {
        ActivityHomeBinding.inflate(layoutInflater)
    }
    private val progressBar: ProgressBar by lazy {
        binding.progressBarScroll
    }

    // Adapter para o RecyclerView com um lambda para o clique nos itens
    private val homeAdapter = HomeAdapter { repo ->
        navigateToPullRequestPage(repo.id)
    }

    // ViewModel da HomeActivity, inicializado com uma fábrica para injeção de dependências
    private val viewModel: HomeViewModel by viewModels {
        HomeViewModelFactory(
            HomeFetchRepositoriesUseCase(
                HomeRepositoryImpl(
                    RetrofitService.apiService, // API service para chamadas de rede
                    AppDatabase.getDatabase(applicationContext), // Database para persistência de dados
                    NetworkUtils(applicationContext) // Utilitário de rede para verificar conectividade
                )
            )
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setupUI()
        observeViewModel()
        viewModel.fetchData(REPO_QUERY, SORT_CRITERIA, LoadType.INITIAL)
    }

    private fun setupUI() {
        setupRecyclerview() // Configura o RecyclerView
        setupScrollListener() // Adiciona um listener para a rolagem do RecyclerView
        pullToRefresh() // Configura a ação de pull to refresh
    }

    // Observa LiveData do ViewModel e atualiza a UI de acordo
    private fun observeViewModel() {
        viewModel.repoList.observe(this) { result ->
            when (result) {
                is ResultWrapper.Success -> homeAdapter.updateList(result.data) // Atualiza os dados no adapter
                is ResultWrapper.Failure -> showError(result.exception.message) // Mostra um erro
                is ResultWrapper.NetworkError -> showNetworkError() // Em caso de erro de rede, exibe uma mensagem específica
                else -> showUnexpectedError()

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
        if (dy > 0 && shouldLoadMoreItems()) {
            viewModel.fetchData(REPO_QUERY, SORT_CRITERIA, LoadType.MORE)
        }
    }
    private fun shouldLoadMoreItems(): Boolean {
        val layoutManager = binding.homeRecyclerview.layoutManager as LinearLayoutManager
        val totalItemCount = layoutManager.itemCount
        val lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition()
        return viewModel.isLoadingMore.value?.let{
            !it && lastVisibleItemPosition == totalItemCount - 1 } ?: false
    }

    private fun pullToRefresh() {
        binding.pullRefresh.setOnRefreshListener {
            viewModel.fetchData(REPO_QUERY, SORT_CRITERIA, loadType = LoadType.REFRESH)
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
        Snackbar.make(binding.root, "Erro: $message", Toast.LENGTH_LONG).show()
    }

    private fun showNetworkError() {
        Snackbar.make(binding.root, R.string.error_no_internet, Snackbar.LENGTH_LONG).show()
    }
    private fun showUnexpectedError() {
        Snackbar.make(binding.root, R.string.erro_pr, Snackbar.LENGTH_LONG).show()
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
