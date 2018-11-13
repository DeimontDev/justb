package com.deliveryapp.deliveryfruits;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private static Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FloatingActionButton fab = findViewById(R.id.basket_button);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(intent);
            }
        });

        Resources res = getResources();
        Button toBasket1 = findViewById(R.id.button1);
        Button toBasket2 = findViewById(R.id.button2);
        toBasket1.setOnClickListener(basketListener(1));
        toBasket2.setOnClickListener(basketListener(2));


//        for (int i = 0; i < 20; i++) {
//            int id = i + 1;
//            ImageButton toBasket = findViewById(res.getIdentifier(
//                    "button" + String.valueOf(id), "id", getPackageName()));
//            toBasket.setOnClickListener(basketListener(id));
//        }

        for (int i = 0; i < 20; i++) {
            int id = i + 1;
            ImageButton plus = findViewById(res.getIdentifier(
                    "plus"+ String.valueOf(id), "id", getPackageName()));
            plus.setOnClickListener(plusMinusListener(id, true));
            ImageButton minus = findViewById(res.getIdentifier(
                    "minus"+ String.valueOf(id), "id", getPackageName()));
            minus.setOnClickListener(plusMinusListener(id, false));
        }
    }

    private View.OnClickListener basketListener(final int id) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Resources res = getResources();
                TextView productName = findViewById(res.getIdentifier(
                        "product"+ String.valueOf(id), "id", getPackageName()));

                if (intent == null) {
                    intent = new Intent(MainActivity.this, ShoppingCartActivity.class);
                }

                System.out.println("product" + id);
                intent.putExtra("product" + id, productName.getText().toString());
            }
        };
    }

    private View.OnClickListener plusMinusListener(final int id, final boolean plus) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Resources res = getResources();
                TextView textView = findViewById(res.getIdentifier(
                        "text"+ String.valueOf(id), "id", getPackageName()));
                if (plus) {
                    setPlus(textView);
                } else {
                    setMinus(textView);
                }
            }
        };
    }

    private void setPlus(TextView textView) {
        int newText = Integer.parseInt(textView.getText().toString()) + 1;
        textView.setText(String.valueOf(newText));
    }

    private void setMinus(TextView textView) {
        int text = Integer.parseInt(textView.getText().toString());

        if (text > 0) {
            int newText = text - 1;
            textView.setText(String.valueOf(newText));
        }
    }

}
