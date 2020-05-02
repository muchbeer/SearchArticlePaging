package raum.muchbeer.searcharticlepaging;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;

import raum.muchbeer.searcharticlepaging.adapter.ArticleListAdapter;
import raum.muchbeer.searcharticlepaging.databinding.ActivityMainBinding;
import raum.muchbeer.searcharticlepaging.viewmodel.ArticleViewModel;

public class MainActivity extends AppCompatActivity {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private ArticleListAdapter adapter;
    private ArticleViewModel articleViewModel;
    private ActivityMainBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

      //  articleViewModel = new ArticleViewModel(AppController.create(this));
        articleViewModel = ViewModelProviders.of(this).get(ArticleViewModel.class);

        getListArticle();
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
       binding.recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
       binding.recyclerView.setItemAnimator(new DefaultItemAnimator());

        adapter = new ArticleListAdapter(getApplicationContext());

        binding.recyclerView.setAdapter(adapter);

        articleViewModel.filterArticle.setValue("");

        binding.searchArticle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                //just set the current value to search.
                articleViewModel.filterArticle.
                        setValue("%" + editable.toString() + "%");
            }
        });
    }

    private void getListArticle() {
        articleViewModel.getArticleLiveData().observe(this, pagedList -> {
            Log.d(LOG_TAG, "list of all page number " + pagedList.size());
            Log.d(LOG_TAG, "list of food are " + pagedList);
            adapter.submitList(pagedList);
        });

        articleViewModel.getNetworkState().observe(this, networkState -> {
            adapter.setNetworkState(networkState);
        });

    }
}
