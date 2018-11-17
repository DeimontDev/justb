package com.deliveryapp.deliveryfruits;

import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;

public class ShoppingCartActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_cart);

        Bundle extras = getIntent().getExtras();
        String product1 = extras != null ? extras.getString("product1") : null;
        String product2 = extras != null ? extras.getString("product2") : null;

        System.out.println(product1);
        System.out.println(product2);

        ImageButton delete1 = findViewById(R.id.delete1);
        delete1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ConstraintLayout lay1 = findViewById(R.id.lay1);
                lay1.setVisibility(View.GONE);
            }
        });

    }

}
