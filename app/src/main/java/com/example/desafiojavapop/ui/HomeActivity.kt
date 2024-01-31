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
import com.example.desafiojavapop.repository.HomeRepositoryImpl
import com.example.desafiojavapop.rest.RetrofitService
import com.example.desafiojavapop.usecase.HomeFetchRepositoriesUseCase
import com.example.desafiojavapop.util.NetworkUtils
import com.example.desafiojavapop.util.ResultWrapper
import com.example.desafiojavapop.viewmodel.HomeViewModel
import com.example.desafiojavapop.viewmodel.HomeViewModelFactory

class HomeActivity : AppCompatActivity() {

    // Lateinit para inicialização posterior das variáveis
    private lateinit var binding: ActivityHomeBinding
    private lateinit var progressBar: ProgressBar

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
        // Infla o layout usando View Binding e define o conteúdo da view
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        progressBar = binding.progressBarScroll
        setupUI()
        observeViewModel()
        viewModel.fetchRepositories(REPO_QUERY, SORT_CRITERIA)
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
                else -> {}
            }
        }

        // Observadores para os estados de carregamento
        viewModel.isLoading.observe(this) { isLoading ->
            toggleProgressBar(isLoading, isLoadingForRefresh = true)
        }
        viewModel.isLoadingMore.observe(this) { isLoadingMore ->
            toggleProgressBar(isLoadingMore, isLoadingForRefresh = false)
        }

        // Atualiza o adapter quando a lista de repositórios é alterada
        viewModel.currentRepoList.observe(this) { updatedList ->
            homeAdapter.updateList(updatedList)
        }

        // Mostra ou esconde um indicador quando não há mais páginas para carregar
        viewModel.isLastPageLiveData.observe(this) { isLast ->
            binding.noMorpages.visibility = if (isLast) View.VISIBLE else View.GONE
        }
    }

    // Configuração inicial do RecyclerView
    private fun setupRecyclerview() {
        binding.homeRecyclerview.apply {
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(true)
            adapter = homeAdapter
        }
    }

    // Adiciona um listener para implementar a lógica de carregar mais itens ao rolar
    private fun setupScrollListener() {
        binding.homeRecyclerview.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                handleScroll(dy) // Chama handleScroll ao rolar
            }
        })
    }

    private fun handleScroll(dy: Int) {
        if (dy > 0) { // Verifica se a rolagem é para baixo
            val layoutManager = binding.homeRecyclerview.layoutManager as LinearLayoutManager
            val totalItemCount = layoutManager.itemCount
            val lastVisibleItem = layoutManager.findLastVisibleItemPosition()
            val isNotLoadingMore = viewModel.isLoadingMore.value ?: false
            if (!isNotLoadingMore && lastVisibleItem == totalItemCount - 1) {
                viewModel.loadNextPage(REPO_QUERY, SORT_CRITERIA) // Carrega mais dados
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

    // Mostra um Toast com uma mensagem de erro
    private fun showError(message: String?) {
        Toast.makeText(this, "Erro: $message", Toast.LENGTH_LONG).show()
    }

    // Navega para a página de Pull Requests baseado no ID do repositório
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
        private const val REPO_QUERY = "language:Java" // Query de busca por repositórios Java
        private const val SORT_CRITERIA = "stars" // Critério de ordenação por estrelas
    }
}
