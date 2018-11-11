package com.deliveryapp.deliveryfruits;

import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

public class ShoppingCartActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_cart);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        String product = getIntent().getStringExtra("product1");
        CoordinatorLayout layout = findViewById(R.id.main_layout);
        TextView textView = new TextView(ShoppingCartActivity.this);
        textView.setText(product);
        CoordinatorLayout.LayoutParams imageViewLayoutParams = new CoordinatorLayout.
                LayoutParams(CoordinatorLayout.LayoutParams.WRAP_CONTENT, CoordinatorLayout.LayoutParams.WRAP_CONTENT);

        textView.setLayoutParams(imageViewLayoutParams);
        layout.addView(textView);
    }

}
