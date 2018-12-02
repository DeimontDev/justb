package com.deliveryapp.sdobapp;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.InputFilter;
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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;

import static com.deliveryapp.sdobapp.GlobalConst.COUNTER;
import static com.deliveryapp.sdobapp.GlobalConst.PHONE_NUMBER;
import static com.deliveryapp.sdobapp.MainActivity.hideSoftKeyboard;

public class ShoppingCartActivity extends AppCompatActivity {

    private static boolean FLAG = true;

    private final int CALL_REQUEST = 100;
    public static int TOTAL_SHOPPING;
    private static int ORDER_NUMBER;
    private static String TOTAL2;
    private static String PRODUCT_NAME;
    private static String QUANTITY;
    private Intent extras = GlobalConst.intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_cart);

        ORDER_NUMBER = (int) (Math.random() * 10000);
        TextView orderNumber = findViewById(R.id.order_number);
        orderNumber.setText(String.valueOf(ORDER_NUMBER));

        findViewById(R.id.call_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callPhoneNumber();
            }
        });

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
        TOTAL_SHOPPING = 0;
        processPrice(res);

        processProduct(res);
        processButtons(res);
        processTextQuantity(res);
        setNewAmount(String.valueOf(TOTAL_SHOPPING), amount);

        Button order = findViewById(R.id.order_button);
        order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final ConnectivityManager conMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                final NetworkInfo activeNetwork = conMgr.getActiveNetworkInfo();

                if (activeNetwork != null && activeNetwork.isConnected()) {
                    // notify user you are online
                    if (FLAG) {
                        FLAG = false;
                        final Thread call = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                callPhoneNumber();
                            }
                        });
                        call.start();

                        Thread order = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                processOrder();
                            }
                        });
                        order.start();
                    } else {
                        Toast.makeText(ShoppingCartActivity.this, "Comanda e acceptată", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // notify user you are not online
                    Toast.makeText(ShoppingCartActivity.this, "Nu e acces la internet", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void processOrder() {
        Resources res = getResources();
        try {
            PRODUCT_NAME = "Comanda №";
            QUANTITY = String.valueOf(ORDER_NUMBER);
            TOTAL2 = " ";
            new SendRequest().execute();
            Thread.sleep(1000);

            PRODUCT_NAME = "Data/Ora:";

            Date date = new Date();
            date.setTime(System.currentTimeMillis());

            QUANTITY = new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.US).format(date);
            TOTAL2 = " ";
            new SendRequest().execute();
            Thread.sleep(1500);

            PRODUCT_NAME = "Produs";
            QUANTITY = "Cantitatea";
            TOTAL2 = "Total";
            new SendRequest().execute();
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

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
            TOTAL2 = String.valueOf(TOTAL_SHOPPING);
            new SendRequest().execute();
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        PRODUCT_NAME = "end";
        new SendRequest().execute();
    }

    public void callPhoneNumber() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (ActivityCompat.checkSelfPermission(ShoppingCartActivity.this,
                        Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling

                    ActivityCompat.requestPermissions(ShoppingCartActivity.this,
                            new String[]{Manifest.permission.CALL_PHONE}, CALL_REQUEST);
                    return;
                }
            }

            Intent callIntent = new Intent(Intent.ACTION_CALL);
            callIntent.setData(Uri.parse("tel:" + PHONE_NUMBER));
            startActivity(callIntent);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void processTextQuantity(final Resources res) {
        for (int i = 0; i < 20; i++) {
            int id = i + 1;
            final TextView quantity = findViewById(res.getIdentifier(
                    "text" + String.valueOf(id), "id", getPackageName()));
            quantity.setFilters(new InputFilter[]{new MinMaxFilter("1", "200")});

            quantity.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    quantity.setCursorVisible(true);
                }
            });
        }

        ConstraintLayout cont = findViewById(R.id.bottom_lay);
        cont.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                processOnClickAnotherPart(res);
            }
        });
    }

    public void processOnClickAnotherPart(Resources res) {
        hideSoftKeyboard(ShoppingCartActivity.this);
        for (int i = 0; i < 20; i++) {
            int id = i + 1;
            final TextView quantity = findViewById(res.getIdentifier(
                    "text" + String.valueOf(id), "id", getPackageName()));

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                if (quantity.isCursorVisible()) {
                    quantity.setCursorVisible(false);
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == CALL_REQUEST) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                callPhoneNumber();
            } else {
                Toast.makeText(ShoppingCartActivity.this, "Error", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public String getPostDataString(JSONObject params) throws Exception {
        StringBuilder result = new StringBuilder();
        boolean first = true;

        Iterator<String> itr = params.keys();

        while (itr.hasNext()) {

            String key = itr.next();
            Object value = params.get(key);

            if (first) {
                first = false;
            } else {
                result.append("&");
            }

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

        }
    }

    private void setNewAmount(String newAmount, TextView amount) {
        Button order = findViewById(R.id.order_button);
        TextView msg = findViewById(R.id.suma_minima);

        if (Integer.parseInt(newAmount) != 0) {
            if (Integer.parseInt(newAmount) < GlobalConst.MINIMAL_AMOUNT) {
                order.setVisibility(View.GONE);
                msg.setVisibility(View.VISIBLE);

                return;
            }
            newAmount += " lei";
            SpannableString ss = new SpannableString(newAmount);
            ss.setSpan(new UnderlineSpan(), 0, newAmount.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            amount.setText(ss);
            order.setVisibility(View.VISIBLE);
            msg.setVisibility(View.GONE);
        } else {
            amount.setVisibility(View.GONE);
            order.setVisibility(View.GONE);
            msg.setVisibility(View.VISIBLE);
        }
    }

    private void processPrice(Resources res) {
        for (int i = 1; i <= 20; i++) {
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

                TOTAL_SHOPPING = TOTAL_SHOPPING + priceActual;

                pric = String.valueOf(priceActual) + " lei";
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
                FLAG = true;
                ORDER_NUMBER = (int) (Math.random() * 10000);
                TextView orderNumber = findViewById(R.id.order_number);
                orderNumber.setText(String.valueOf(ORDER_NUMBER));

                Resources res = getResources();
                ConstraintLayout layout = findViewById(res.getIdentifier(
                        "lay" + String.valueOf(id), "id", getPackageName()));
                TextView price = findViewById(res.getIdentifier(
                        "price" + String.valueOf(id), "id", getPackageName()));
                String clearPrice = price.getText().toString().substring(0, price.getText().toString().indexOf(" "));
                TOTAL_SHOPPING = TOTAL_SHOPPING - Integer.parseInt(clearPrice);

                TextView amount = findViewById(R.id.amount);
                setNewAmount(String.valueOf(TOTAL_SHOPPING), amount);

                layout.setVisibility(View.GONE);
                extras.removeExtra("product" + id);
                COUNTER--;
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

                    TOTAL_SHOPPING = TOTAL_SHOPPING + priceEntity;
                    String newPrice = String.valueOf(Integer.parseInt(clearPrice) + priceEntity) + " lei";
                    price.setText(newPrice);
                } else {
                    setMinus(textView);

                    if (Integer.parseInt(textView.getText().toString()) == 0) {
                        ConstraintLayout layout = findViewById(res.getIdentifier(
                                "lay" + String.valueOf(id), "id", getPackageName()));
                        layout.setVisibility(View.GONE);
                        extras.removeExtra("product" + id);
                        COUNTER--;
                    }

                    TOTAL_SHOPPING = TOTAL_SHOPPING - priceEntity;
                    String newPrice = String.valueOf(Integer.parseInt(clearPrice) - priceEntity) + " lei";
                    price.setText(newPrice);
                }

                ORDER_NUMBER = (int) (Math.random() * 10000);
                TextView orderNumber = findViewById(R.id.order_number);
                orderNumber.setText(String.valueOf(ORDER_NUMBER));

                TextView amount = findViewById(R.id.amount);
                setNewAmount(String.valueOf(TOTAL_SHOPPING), amount);

                GlobalConst.intent.putExtra("quantity" + id, textView.getText().toString());
            }
        };
    }

    private void setPlus(TextView textView) {
        FLAG = true;
        int newText = Integer.parseInt(textView.getText().toString()) + 1;
        textView.setText(String.valueOf(newText));
    }

    private void setMinus(TextView textView) {
        FLAG = true;
        int text = Integer.parseInt(textView.getText().toString());

        if (text > 0) {
            int newText = text - 1;
            textView.setText(String.valueOf(newText));
        }
    }
}
