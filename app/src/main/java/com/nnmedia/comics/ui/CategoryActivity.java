package com.nnmedia.comics.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.nnmedia.comics.R;

/**
 * 漫画分类页面Activity
 */
public class CategoryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mkz_fragment_category_new);

        // 加载Fragment
        if (savedInstanceState == null) {
            CategoryFragment fragment = new CategoryFragment();
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.coordinatorLayout, fragment)
                    .commit();
        }
    }
}
