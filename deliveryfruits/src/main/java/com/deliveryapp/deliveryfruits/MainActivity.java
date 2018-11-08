package com.deliveryapp.deliveryfruits;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
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
                Snackbar.make(view, "Snack", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        Resources res = getResources();

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
