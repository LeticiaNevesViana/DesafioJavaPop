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
import java.lang.ref.WeakReference

class PullRequestActivity : AppCompatActivity() {

    private val binding: ActivityPullRequestBinding by lazy {
        ActivityPullRequestBinding.inflate(layoutInflater)
    }

    private val viewModel: PullRequestViewModel by viewModels {
        PullRequestViewModelFactory(
            FetchPullRequestsUseCase(PullRequestRepositoryImpl(
                RetrofitService.apiService, // Serviço da API para chamadas de rede
                AppDatabase.getDatabase(this), // Database para persistência de dados
                NetworkUtils(this@PullRequestActivity) // Utilitário de rede para verificar conectividade
            )),
            HomeRepositoryImpl(
                RetrofitService.apiService,
                AppDatabase.getDatabase(this),
                NetworkUtils(this@PullRequestActivity)
            )
        )
    }

    // Adapter para o RecyclerView que exibe os Pull Requests
    private val pullRequestAdapter: PullRequestAdapter by lazy {
        val openLinkRef = WeakReference(this::openLink)
        PullRequestAdapter { url ->
            openLinkRef.get()?.invoke(url.toString())
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setupRecyclerView() // Configura o RecyclerView
        observeViewModel() // Observa mudanças nos dados do ViewModel

        val repoId = intent?.data?.getQueryParameter("repoId")?.toIntOrNull()

        repoId?.let {
            viewModel.loadRepositoryDetailsFromDb(it)
        }
    }

    // Configuração inicial do RecyclerView
    private fun setupRecyclerView() = binding.prRecyclerview.apply {
        layoutManager = LinearLayoutManager(context)
        setHasFixedSize(true)
        adapter = pullRequestAdapter
    }

    // Observa o LiveData do ViewModel e atualiza a UI de acordo com o estado dos dados
    private fun observeViewModel() {
        viewModel.pullRequests.observe(this) { result ->
            when (result) {
                is ResultWrapper.Success -> handleSuccess(result.data) // Em caso de sucesso, exibe os dados
                is ResultWrapper.Failure -> showError() // Em caso de falha, exibe uma mensagem de erro
                is ResultWrapper.NetworkError -> showNetworkError() // Em caso de erro de rede, exibe uma mensagem específica
                else -> showUnexpectedError() // Para qualquer outro caso, exibe uma mensagem de erro genérica
            }
        }
    }

    // Trata o caso de sucesso, verificando se a lista de Pull Requests está vazia ou não
    private fun handleSuccess(data: List<PullRequestModel>) {
        if (data.isEmpty()) {
            showNoPullRequestsView() // Se vazia, exibe uma mensagem indicando que não há Pull Requests
        } else {
            showPullRequestsView(data) // Se não, exibe a lista de Pull Requests
        }
    }

    // Exibe uma mensagem indicando que não há Pull Requests
    private fun showNoPullRequestsView() {
        binding.noPullRequests.visibility = View.VISIBLE
        binding.prRecyclerview.visibility = View.GONE
    }

    // Exibe a lista de Pull Requests
    private fun showPullRequestsView(data: List<PullRequestModel>) {
        binding.noPullRequests.visibility = View.GONE
        binding.prRecyclerview.visibility = View.VISIBLE
        pullRequestAdapter.setList(data) // Atualiza os dados do adapter
    }

    // Exibe uma Snackbar com uma mensagem de erro genérica
    private fun showError() {
        Snackbar.make(binding.root, R.string.erro_pr, Snackbar.LENGTH_LONG).show()
    }

    // Exibe uma Snackbar com uma mensagem de erro de rede
    private fun showNetworkError() {
        Snackbar.make(binding.root, R.string.error_no_internet, Snackbar.LENGTH_LONG).show()
    }

    // Exibe uma Snackbar com uma mensagem de erro inesperado
    private fun showUnexpectedError() {
        Snackbar.make(binding.root, R.string.erro_pr, Snackbar.LENGTH_LONG).show()
    }

    // Abre um link no navegador padrão do dispositivo
    private fun openLink(url: String) {
        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
    }
}
