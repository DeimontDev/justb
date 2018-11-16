package com.deliveryapp.deliveryfruits;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

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



    }

}
