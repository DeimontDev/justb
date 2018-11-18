package com.deliveryapp.deliveryfruits;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FloatingActionButton fab = findViewById(R.id.basket_button);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(GlobalConst.intent);
            }
        });

        Resources res = getResources();
        processBasketActions(res);
        processPlusMinus(res);
    }

    public void processBasketActions(Resources res) {
        for (int i = 0; i < 20; i++) {
            int id = i + 1;
            AppCompatButton toBasket = findViewById(res.getIdentifier(
                    "button" + String.valueOf(id), "id", getPackageName()));
            toBasket.setOnClickListener(basketListener(id));

            if (GlobalConst.intent != null) {
                String product = GlobalConst.intent.getStringExtra("product" + id);

                if (product != null) {
                    toBasket.setBackgroundColor(Color.parseColor("#FFB77E5E"));
                    toBasket.setText("Добавлено");
                    String quant = GlobalConst.intent.getStringExtra("quantity" + id);
                    TextView quantity = findViewById(res.getIdentifier(
                            "text" + String.valueOf(id), "id", getPackageName()));
                    quantity.setText(quant);
                }
            }
        }
    }

    public void processPlusMinus(Resources res) {
        for (int i = 0; i < 20; i++) {
            int id = i + 1;
            ImageButton plus = findViewById(res.getIdentifier(
                    "plus" + String.valueOf(id), "id", getPackageName()));
            plus.setOnClickListener(plusMinusListener(id, true));

            ImageButton minus = findViewById(res.getIdentifier(
                    "minus" + String.valueOf(id), "id", getPackageName()));
            minus.setOnClickListener(plusMinusListener(id, false));
        }
    }

    private View.OnClickListener basketListener(final int id) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.setBackgroundColor(Color.parseColor("#FFB77E5E"));
                TextView textView = (TextView) view;

                if (textView.getText().toString().equals("В корзину")) {
                    textView.setText("Добавлено");
                    Resources res = getResources();
                    TextView productName = findViewById(res.getIdentifier(
                            "product" + String.valueOf(id), "id", getPackageName()));
                    TextView quantity = findViewById(res.getIdentifier(
                            "text" + String.valueOf(id), "id", getPackageName()));
                    if (GlobalConst.intent == null) {
                        GlobalConst.intent = new Intent(MainActivity.this, ShoppingCartActivity.class);
                    }

                    GlobalConst.intent.putExtra("product" + id, productName.getText().toString());
                    GlobalConst.intent.putExtra("quantity" + id, quantity.getText().toString());
                } else {
                    view.setBackgroundColor(Color.parseColor("#FFEFD8CB"));
                    textView.setText("В корзину");
                    GlobalConst.intent.removeExtra("product" + id);
                    GlobalConst.intent.removeExtra("price" + id);
                }
            }
        };
    }

    private View.OnClickListener plusMinusListener(final int id, final boolean plus) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Resources res = getResources();
                TextView textView = findViewById(res.getIdentifier(
                        "text" + String.valueOf(id), "id", getPackageName()));
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
