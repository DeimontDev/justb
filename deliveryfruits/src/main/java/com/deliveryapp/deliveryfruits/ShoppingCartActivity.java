package com.deliveryapp.deliveryfruits;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

public class ShoppingCartActivity extends AppCompatActivity {

    private static int TOTAL;
    private Intent extras = GlobalConst.intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_cart);

        FloatingActionButton back = findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ShoppingCartActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        Resources res = getResources();
        TextView amount = findViewById(R.id.amount);
        TOTAL = 0;
        processPrice(res);

        processProduct(res);
        processButtons(res);
        setNewAmount(String.valueOf(TOTAL), amount);
    }

    private void setNewAmount(String newAmount, TextView amount) {
        newAmount += " лей";
        SpannableString ss = new SpannableString(newAmount);
        ss.setSpan(new UnderlineSpan(), 0, newAmount.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        amount.setText(ss);
    }

    private void processPrice(Resources res) {
        for (int i = 1; i <= 20 ; i++) {
            if (extras != null && extras.getStringExtra("product" + i) != null) {
                TextView price = findViewById(res.getIdentifier(
                        "price" + String.valueOf(i), "id", getPackageName()));
                String pric = price.getText().toString().substring(0, price.getText().toString().indexOf(" "));
                String actQuan = extras.getStringExtra("quantity" + i);
                int quantityActual = Integer.parseInt(actQuan);
                int priceActual = Integer.parseInt(pric);

                if (quantityActual > 1) {
                    priceActual *= quantityActual;
                }

                TOTAL = TOTAL + priceActual;

                pric = String.valueOf(priceActual) + " лей";
                price.setText(pric);
            }
        }
    }

    private void processProduct(Resources res) {
        for (int i = 1; i <= 20; i++) {
            if (extras != null && extras.getStringExtra("product" + i) != null) {
                ConstraintLayout lay = findViewById(res.getIdentifier(
                        "lay" + String.valueOf(i), "id", getPackageName()));
                TextView quantity = findViewById(res.getIdentifier(
                        "text" + String.valueOf(i), "id", getPackageName()));
                String actQuan = extras.getStringExtra("quantity" + i);
                quantity.setText(actQuan);

                lay.setVisibility(View.VISIBLE);
            }
        }
    }

    private void processButtons(Resources res) {
        for (int i = 0; i < 20; i++) {
            int id = i + 1;
            ImageButton plus = findViewById(res.getIdentifier(
                    "plus" + String.valueOf(id), "id", getPackageName()));
            plus.setOnClickListener(plusMinusListener(id, true));

            ImageButton minus = findViewById(res.getIdentifier(
                    "minus" + String.valueOf(id), "id", getPackageName()));
            minus.setOnClickListener(plusMinusListener(id, false));

            ImageButton delete = findViewById(res.getIdentifier(
                    "delete" + String.valueOf(id), "id", getPackageName()));
            delete.setOnClickListener(deleteListener(id, extras));
        }
    }

    private View.OnClickListener deleteListener(final int id, final Intent extras) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Resources res = getResources();
                ConstraintLayout layout = findViewById(res.getIdentifier(
                        "lay" + String.valueOf(id), "id", getPackageName()));
                TextView price = findViewById(res.getIdentifier(
                        "price" + String.valueOf(id), "id", getPackageName()));
                String clearPrice = price.getText().toString().substring(0, price.getText().toString().indexOf(" "));
                TOTAL = TOTAL - Integer.parseInt(clearPrice);

                TextView amount = findViewById(R.id.amount);
                setNewAmount(String.valueOf(TOTAL), amount);

                layout.setVisibility(View.GONE);
                extras.removeExtra("product" + id);
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
                TextView price = findViewById(res.getIdentifier(
                        "price" + String.valueOf(id), "id", getPackageName()));
                String actQuan = textView.getText().toString();
                String clearPrice = price.getText().toString().substring(0, price.getText().toString().indexOf(" "));
                int priceEntity = Integer.parseInt(clearPrice) / Integer.parseInt(actQuan);

                if (plus) {
                    setPlus(textView);

                    TOTAL = TOTAL + priceEntity;
                    String newPrice = String.valueOf(Integer.parseInt(clearPrice) + priceEntity) + " лей";
                    price.setText(newPrice);
                } else {
                    setMinus(textView);

                    if (Integer.parseInt(textView.getText().toString()) == 0) {
                        ConstraintLayout layout = findViewById(res.getIdentifier(
                                "lay" + String.valueOf(id), "id", getPackageName()));
                        layout.setVisibility(View.GONE);
                        extras.removeExtra("product" + id);
                    }

                    TOTAL = TOTAL - priceEntity;
                    String newPrice = String.valueOf(Integer.parseInt(clearPrice) - priceEntity) + " лей";
                    price.setText(newPrice);
                }

                TextView amount = findViewById(R.id.amount);
                setNewAmount(String.valueOf(TOTAL), amount);
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
