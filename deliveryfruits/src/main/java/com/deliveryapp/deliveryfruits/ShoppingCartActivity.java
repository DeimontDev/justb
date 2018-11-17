package com.deliveryapp.deliveryfruits;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

public class ShoppingCartActivity extends AppCompatActivity {

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
        Bundle extras = getIntent().getExtras();

        Resources res = getResources();

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
            delete.setOnClickListener(deleteListener(id));
        }

    }

    private View.OnClickListener deleteListener(final int id) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Resources res = getResources();
                ConstraintLayout layout = findViewById(res.getIdentifier(
                        "lay" + String.valueOf(id), "id", getPackageName()));
                layout.setVisibility(View.GONE);
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
