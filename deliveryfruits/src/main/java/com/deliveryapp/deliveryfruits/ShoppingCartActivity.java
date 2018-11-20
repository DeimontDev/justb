package com.deliveryapp.deliveryfruits;

import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONObject;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedWriter;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Iterator;

public class ShoppingCartActivity extends AppCompatActivity {

    private static int TOTAL;
    private static String TOTAL2;
    private static String PRODUCT_NAME;
    private static String QUANTITY;
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

        Button order = findViewById(R.id.order_button);
        order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Resources res = getResources();

                for (int i = 1; i <= 20; i++) {
                    ConstraintLayout lay = findViewById(res.getIdentifier(
                            "lay" + String.valueOf(i), "id", getPackageName()));
                    if (lay != null && lay.getVisibility() == View.VISIBLE) {
                        TextView product = findViewById(res.getIdentifier(
                                "name" + String.valueOf(i), "id", getPackageName()));
                        TextView quantity = findViewById(res.getIdentifier(
                                "text" + String.valueOf(i), "id", getPackageName()));
                        String productName = product.getText().toString();
                        String quan = quantity.getText().toString();
                        PRODUCT_NAME = productName;
                        QUANTITY = quan;
                        TOTAL2 = "";
                        try {
                            new SendRequest().execute();
                            Thread.sleep(2000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
                try {
                    PRODUCT_NAME = " ";
                    QUANTITY = " ";
                    TOTAL2 = String.valueOf(TOTAL);
                    new SendRequest().execute();
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                PRODUCT_NAME = "end";
                new SendRequest().execute();
            }
        });
    }

    public String getPostDataString(JSONObject params) throws Exception {

        StringBuilder result = new StringBuilder();
        boolean first = true;

        Iterator<String> itr = params.keys();

        while (itr.hasNext()) {

            String key = itr.next();
            Object value = params.get(key);

            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(key, "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(value.toString(), "UTF-8"));

        }
        return result.toString();
    }

    public class SendRequest extends AsyncTask<String, Void, String> {
        protected void onPreExecute() {
        }

        protected String doInBackground(String... arg0) {
            try {
                URL url = new URL("https://script.google.com/macros/s/AKfycbxFZC9YPpG1-A_StZ583S0jtkmApNOEFclM8eKJ3aLB0zwiHN0/exec");
                JSONObject postDataParams = new JSONObject();

                String id = "1V9XMhpxFsfe8xXgWEfnGIYcPyLWd8wll9evUmRofDxs";

                postDataParams.put("Product", PRODUCT_NAME);
                postDataParams.put("cantitatea", QUANTITY);
                postDataParams.put("total", TOTAL2);
                postDataParams.put("id", id);


                Log.e("params", postDataParams.toString());

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(15000 /* milliseconds */);
                conn.setConnectTimeout(15000 /* milliseconds */);
                conn.setRequestMethod("POST");
//                conn.setDoInput(true);
                conn.setDoOutput(true);

                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write(getPostDataString(postDataParams));

                writer.flush();
                writer.close();
                os.close();

                int responseCode = conn.getResponseCode();

                if (responseCode == HttpsURLConnection.HTTP_OK) {
                    return "Ваш заказ принят";
                } else {
                    return "false : " + responseCode;
                }
            } catch (Exception e) {
                return "Exception: " + e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(String result) {
            Toast.makeText(getApplicationContext(), result,
                    Toast.LENGTH_LONG).show();

        }
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
