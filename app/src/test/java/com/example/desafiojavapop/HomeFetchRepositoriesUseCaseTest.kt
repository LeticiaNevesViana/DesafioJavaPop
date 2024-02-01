import com.example.desafiojavapop.FakeHomeModel
import com.example.desafiojavapop.repository.HomeRepository
import com.example.desafiojavapop.usecase.HomeFetchRepositoriesUseCase
import com.example.desafiojavapop.util.ResultWrapper
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.mockito.ArgumentMatchers.anyString
import org.mockito.ArgumentMatchers.anyInt

class HomeFetchRepositoriesUseCaseTest {

    private lateinit var homeFetchRepositoriesUseCase: HomeFetchRepositoriesUseCase
    private lateinit var repository: HomeRepository

    @Before
    fun setUp() {
        repository = mock(HomeRepository::class.java)//cria um mock de HomeRepository
        homeFetchRepositoriesUseCase = HomeFetchRepositoriesUseCase(repository)
    }

    @Test
    fun `invoke returns list of repositories on success`() = runBlocking {
        // Given
        val fakeHomeModel = FakeHomeModel()//mock do HomeModel
        val expectedRepositories = listOf(fakeHomeModel.createFakeHomeModel())
        `when`(repository.getRepositories(anyString(), anyString(), anyInt()))
            .thenReturn(ResultWrapper.Success(expectedRepositories))

        // When
        val result = homeFetchRepositoriesUseCase.invoke("language:Java", "stars", 1)

        // Then
        assertEquals(ResultWrapper.Success(expectedRepositories), result)
    }

    @Test
    fun `invoke returns failure on error`() = runBlocking {
        // Given
        val query = "language:Java"
        val sort = "stars"
        val page = 1
        val errorMessage = "Falha na chamada da API"
        `when`(repository.getRepositories(query, sort, page))
            .thenReturn(ResultWrapper.Failure(Exception(errorMessage)))

        // When
        val result = homeFetchRepositoriesUseCase.invoke(query, sort, page)

        // Then
        assert(result is ResultWrapper.Failure && result.exception.message == errorMessage)
    }
}
