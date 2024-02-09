import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.desafiojavapop.databinding.ListPullRequestBinding
import com.example.desafiojavapop.model.PullRequestModel
import com.squareup.picasso.Picasso
import java.lang.ref.WeakReference

class PullRequestViewHolder(itemView: View, private val onItemClickedRef: WeakReference<(String) -> Unit>) : RecyclerView.ViewHolder(itemView) {
    private val binding = ListPullRequestBinding.bind(itemView)

    fun bind(item: PullRequestModel) {
        binding.textViewtitleOfPr.text = item.title
        Picasso.get().load(item.user?.avatarUrl).into(binding.imageViewAuthor)
        binding.textViewBodyDescription.maxLines = 2
        binding.textViewBodyDescription.text = item.body
        binding.textViewAuthorName.text = item.user?.login
        binding.textViewDayOfCreate.text = item.getCreatedAtDateString()

        itemView.setOnClickListener {
            item.htmlUrl?.let { url ->
                onItemClickedRef.get()?.invoke(url)
            } ?: run {
                Toast.makeText(itemView.context, "URL não disponível", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
