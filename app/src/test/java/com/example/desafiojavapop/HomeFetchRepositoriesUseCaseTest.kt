package com.example.desafiojavapop

import com.example.desafiojavapop.model.HomeResponse
import com.example.desafiojavapop.repository.HomeRepositoryImpl
import com.example.desafiojavapop.usecase.HomeFetchRepositoriesUseCase
import com.example.desafiojavapop.util.ResultWrapper
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.runBlocking
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.never
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner
import retrofit2.Response

@RunWith(MockitoJUnitRunner::class)
class HomeFetchRepositoriesUseCaseTest {

    @Mock
    lateinit var repository: HomeRepositoryImpl

    @InjectMocks
    lateinit var useCase: HomeFetchRepositoriesUseCase

    @Test
    fun `invoke returns cached repositories when available`() = runBlocking {
        val fakeCachedRepos = listOf(FakeHomeModel().fakeHomeModel)
        `when`(repository.getRepositoriesFromDb(QUERY, PAGE)).thenReturn(fakeCachedRepos)

        val result = useCase.invoke(QUERY, SORT, PAGE)
        verify(repository, never()).getRepositoriesFromApi(QUERY, SORT, PAGE)
        assertEquals(ResultWrapper.Success(fakeCachedRepos), result)
    }

    @Test
    fun `invoke returns failure when API call fails`() = runBlocking {
        `when`(repository.getRepositoriesFromDb(QUERY, PAGE)).thenReturn(emptyList())
        val errorString = "Erro na API"
        val responseBody = errorString.toResponseBody("text/plain".toMediaTypeOrNull())
        val apiError = Response.error<HomeResponse>(404, responseBody)
        `when`(repository.getRepositoriesFromApi(QUERY, SORT, PAGE)).thenReturn(apiError)

        val result = useCase.invoke(QUERY, SORT, PAGE)

        assertTrue(result is ResultWrapper.Failure)
    }


    companion object {
        private const val QUERY = "language:Java"
        private const val SORT = "stars"
        private const val PAGE = 1
    }

    }