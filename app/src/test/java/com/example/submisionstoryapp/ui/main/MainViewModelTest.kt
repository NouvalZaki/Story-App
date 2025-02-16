import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.PagingData
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListUpdateCallback
import com.example.submisionstoryapp.DataDummy
import com.example.submisionstoryapp.MainDispatchRule
import com.example.submisionstoryapp.api.ListStoryItem
import com.example.submisionstoryapp.data.StoryRepo
import com.example.submisionstoryapp.data.UserRepo
import com.example.submisionstoryapp.data.preference.UserData
import com.example.submisionstoryapp.getOrAwaitValue
import com.example.submisionstoryapp.ui.main.MainViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnit

@ExperimentalCoroutinesApi
class MainViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatchRule()

    @get:Rule
    val mockitoRule = MockitoJUnit.rule()

    @Mock
    private lateinit var userRepository: UserRepo

    @Mock
    private lateinit var storyRepository: StoryRepo

    private lateinit var mainViewModel: MainViewModel

    @Before
    fun setUp() {
        mainViewModel = MainViewModel(userRepository, storyRepository)
    }

    @Test
    fun `when successfully load story should return data`() = runTest {
        val dummyToken = "dummy_token"
        val dummyPagingData = PagingData.from(DataDummy.generateDummyStoryResponse().listStory)

        `when`(userRepository.getSession()).thenReturn(
            flowOf(
                UserData(
                    isLogin = true,
                    token = dummyToken
                )
            )
        )
        `when`(storyRepository.getPagedStories(dummyToken)).thenReturn(flowOf(dummyPagingData))

        val actualStories = mainViewModel.stories.getOrAwaitValue()

        val differ = AsyncPagingDataDiffer(
            diffCallback = StoryDiffCallback(),
            updateCallback = NoopListCallback,
            mainDispatcher = mainDispatcherRule.testDispatcher
        )
        differ.submitData(actualStories)

        Assert.assertNotNull(differ.snapshot())
        Assert.assertEquals(DataDummy.generateDummyStoryResponse().listStory.size, differ.snapshot().size)
        Assert.assertEquals(
            DataDummy.generateDummyStoryResponse().listStory.first().name,
            differ.snapshot()[0]?.name
        )
        Assert.assertEquals(
            DataDummy.generateDummyStoryResponse().listStory.first().description,
            differ.snapshot()[0]?.description
        )
    }

    @Test
    fun `when failed load story should return empty list`() = runTest {
        val dummyToken = "dummy_token"
        val dummyPagingData = PagingData.from(emptyList<ListStoryItem>())

        `when`(userRepository.getSession()).thenReturn(
            flowOf(
                UserData(
                    isLogin = true,
                    token = dummyToken
                )
            )
        )
        `when`(storyRepository.getPagedStories(dummyToken)).thenReturn(flowOf(dummyPagingData))

        val actualStories = mainViewModel.stories.getOrAwaitValue()

        val differ = AsyncPagingDataDiffer(
            diffCallback = StoryDiffCallback(),
            updateCallback = NoopListCallback,
            mainDispatcher = mainDispatcherRule.testDispatcher
        )
        differ.submitData(actualStories)

        Assert.assertEquals(0, differ.snapshot().size)
    }

    private val NoopListCallback = object : ListUpdateCallback {
        override fun onInserted(position: Int, count: Int) {}
        override fun onRemoved(position: Int, count: Int) {}
        override fun onMoved(fromPosition: Int, toPosition: Int) {}
        override fun onChanged(position: Int, count: Int, payload: Any?) {}
    }
}

class StoryDiffCallback : DiffUtil.ItemCallback<ListStoryItem>() {
    override fun areItemsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
        return oldItem == newItem
    }
}
