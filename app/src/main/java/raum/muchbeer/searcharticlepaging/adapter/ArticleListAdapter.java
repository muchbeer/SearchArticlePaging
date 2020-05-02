package raum.muchbeer.searcharticlepaging.adapter;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.paging.PagedListAdapter;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import raum.muchbeer.searcharticlepaging.R;
import raum.muchbeer.searcharticlepaging.api.NetworkStatus;
import raum.muchbeer.searcharticlepaging.databinding.ItemArticleBinding;
import raum.muchbeer.searcharticlepaging.databinding.ItemNetworkStateBinding;
import raum.muchbeer.searcharticlepaging.model.Articles;
import raum.muchbeer.searcharticlepaging.util.AppUtils;

public class ArticleListAdapter extends PagedListAdapter<Articles, RecyclerView.ViewHolder> {
    private static final int TYPE_PROGRESS = 0;
    private static final int TYPE_ITEM = 1;

    private Context context;
    private NetworkStatus networkState;

    public ArticleListAdapter(Context context) {
        super(Articles.DIFF_CALLBACK);
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());

        if(viewType == TYPE_PROGRESS) {

            ItemNetworkStateBinding headerBinding = DataBindingUtil.inflate(layoutInflater,
                    R.layout.item_network_state,
                    parent,
                    false);
            NetworkStateItemViewHolder viewHolder = new NetworkStateItemViewHolder(headerBinding);
            return viewHolder;

        } else {
            ItemArticleBinding itemBinding = DataBindingUtil.inflate(layoutInflater,
                    R.layout.item_article,
                    parent,
                    false);
            ArticleItemViewHolder viewHolder = new ArticleItemViewHolder(itemBinding);
            return viewHolder;
        }

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof ArticleItemViewHolder) {
            ((ArticleItemViewHolder)holder).bindTo(getItem(position));
        } else {
            ((NetworkStateItemViewHolder) holder).bindView(networkState);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (hasExtraRow() && position == getItemCount() - 1) {
            return TYPE_PROGRESS;
        } else {
            return TYPE_ITEM;
        }
    }


    public void setNetworkState(NetworkStatus newNetworkState) {
        NetworkStatus previousState = this.networkState;
        boolean previousExtraRow = hasExtraRow();
        this.networkState = newNetworkState;
        boolean newExtraRow = hasExtraRow();
        if (previousExtraRow != newExtraRow) {
            if (previousExtraRow) {
                notifyItemRemoved(getItemCount());
            } else {
                notifyItemInserted(getItemCount());
            }
        } else if (newExtraRow && previousState != newNetworkState) {
            notifyItemChanged(getItemCount() - 1);
        }
    }

    private boolean hasExtraRow() {
        if (networkState != null && networkState != NetworkStatus.LOADED) {
            return true;
        } else {
            return false;
        }
    }
    public class ArticleItemViewHolder extends RecyclerView.ViewHolder {

        private ItemArticleBinding binding;
        public ArticleItemViewHolder(ItemArticleBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bindTo(Articles article) {
            binding.itemImage.setVisibility(View.VISIBLE);
            binding.itemDesc.setVisibility(View.VISIBLE);

            String author = article.getAuthor() == null || article.getAuthor().isEmpty() ? context.getString(R.string.author_name) : article.getAuthor();
            String titleString = String.format(context.getString(R.string.item_title), author, article.getTitle());

            SpannableString spannableString = new SpannableString(titleString);
            spannableString.setSpan(new ForegroundColorSpan(ContextCompat.getColor(context.getApplicationContext(), R.color.secondary_text)),
                    titleString.lastIndexOf(author) + author.length() + 1, titleString.lastIndexOf(article.getTitle()) - 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            binding.itemTitle.setText(spannableString);
            binding.itemTime.setText(String.format(context.getString(R.string.item_date), AppUtils.getDate(article.getPublishedAt()), AppUtils.getTime(article.getPublishedAt())));             binding.itemDesc.setText(article.getDescription());
            Picasso.get().load(article.getUrlToImage()).resize(250, 200).into(binding.itemImage);
        }
    }

    public class NetworkStateItemViewHolder extends RecyclerView.ViewHolder {

        private ItemNetworkStateBinding binding;
        public NetworkStateItemViewHolder(ItemNetworkStateBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bindView(NetworkStatus networkState) {
            if (networkState != null && networkState.getStatus() == NetworkStatus.Status.RUNNING) {
                binding.progressBar.setVisibility(View.VISIBLE);
            } else {
                binding.progressBar.setVisibility(View.GONE);
            }

            if (networkState != null && networkState.getStatus() == NetworkStatus.Status.FAILED) {
                binding.errorMsg.setVisibility(View.VISIBLE);
                binding.errorMsg.setText(networkState.getMsg());
            } else {
                binding.errorMsg.setVisibility(View.GONE);
            }
        }
    }

}
