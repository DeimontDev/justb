package com.deliveryapp.sdobapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import static com.deliveryapp.sdobapp.GlobalConst.COUNTER;

public class MainActivity extends AppCompatActivity {

    private final int CALL_REQUEST = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView counter = findViewById(R.id.counter);
        counter.setText(String.valueOf(COUNTER));
        findViewById(R.id.call_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callPhoneNumber();
            }
        });

        FloatingActionButton fab = findViewById(R.id.basket_button);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = GlobalConst.intent == null ?
                        new Intent(MainActivity.this, ShoppingCartActivity.class) : GlobalConst.intent;
                startActivity(intent);
            }
        });
        Resources res = getResources();
        processBasketActions(res);
        processPlusMinus(res);
    }

    public void callPhoneNumber() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (ActivityCompat.checkSelfPermission(MainActivity.this,
                        Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling

                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{Manifest.permission.CALL_PHONE}, CALL_REQUEST);
                    return;
                }
            }

            Intent callIntent = new Intent(Intent.ACTION_CALL);
            callIntent.setData(Uri.parse("tel:" + "123456"));
            startActivity(callIntent);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == CALL_REQUEST) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                callPhoneNumber();
            } else {
                Toast.makeText(MainActivity.this, "Error", Toast.LENGTH_SHORT).show();
            }
        }
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
                    toBasket.setText(R.string.added);
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
                TextView counter = findViewById(R.id.counter);

                if (textView.getText().toString().equals("Adaugă in coș")) {
                    textView.setText(R.string.added);
                    Resources res = getResources();
                    String newCount = String.valueOf(Integer.parseInt(counter.getText().toString()) + 1);
                    counter.setText(newCount);
                    COUNTER++;
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
                    String newCount = String.valueOf(Integer.parseInt(counter.getText().toString()) - 1);
                    counter.setText(newCount);
                    COUNTER--;
                    view.setBackgroundColor(Color.parseColor("#FFEFD8CB"));
                    textView.setText(R.string.to_basket);
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

        if (text > 0 && text != 1) {
            int newText = text - 1;
            textView.setText(String.valueOf(newText));
        }
    }

}
