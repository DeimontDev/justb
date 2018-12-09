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
import org.json.JSONException;
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

import static com.deliveryapp.sdobapp.GlobalConst.*;
import static com.deliveryapp.sdobapp.MainActivity.hideSoftKeyboard;

public class ShoppingCartActivity extends AppCompatActivity {

    public static int TOTAL_SHOPPING;
    private static boolean FLAG = true;
    private static int ORDER_NUMBER;

    private final int CALL_REQUEST = 100;
    private JSONObject postDataParams;
    private String COMAND_VAL;
    private String DATE_VAL;
    private String PRODUS_VAL;
    private String AMOUNT_VAL;
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
        processTextQuantity();
        setFilters(res);
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

    public void setFilters(Resources res) {
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
    }

    public void processOrder() {
        final Resources res = getResources();

        Thread thread1 = new Thread(new Runnable() {
            @Override
            public void run() {
                Date date = new Date(System.currentTimeMillis());
                DATE_VAL = new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.US).format(date);
                COMAND_VAL = String.valueOf(ORDER_NUMBER);
                AMOUNT_VAL = String.valueOf(TOTAL_SHOPPING);

                postDataParams = new JSONObject();

                for (int i = 1; i <= 20; i++) {
                    String productName = extras != null ? extras.getStringExtra("product" + i) : null;
                    TextView quantity = findViewById(res.getIdentifier(
                            "text" + String.valueOf(i), "id", getPackageName()));
                    String quant = quantity.getText().toString();

                    if (productName != null) {
                        try {
                            postDataParams.put("prod" + i, productName);
                            postDataParams.put("quant" + i, quant);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
                new SendRequest().execute();
            }

        });
        thread1.start();
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

    public void processTextQuantity() {
        final Resources res = getResources();

        ConstraintLayout basketLay = findViewById(R.id.lay_basket);
        basketLay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideSoftKeyboard(ShoppingCartActivity.this);

                for (int i = 1; i <= 20; i++) {
                    if (extras != null && extras.getStringExtra("product" + i) != null) {
                        TextView price = findViewById(res.getIdentifier(
                                "price" + String.valueOf(i), "id", getPackageName()));
                        String pric = price.getText().toString().substring(0, price.getText().toString().indexOf(" "));
                        TextView quantity = findViewById(res.getIdentifier(
                                "text" + String.valueOf(i), "id", getPackageName()));
                        String actQuan = quantity.getText().toString();
                        String oldQuantity = extras.getStringExtra("quantity" + i);

                        if (!actQuan.equals(oldQuantity)) {
                            int priceActual = Integer.parseInt(pric);
                            int priceEntity = priceActual / Integer.parseInt(oldQuantity);

                            if (actQuan.equals("") || actQuan.equals("0")) {
                                priceActual = priceEntity;
                                TOTAL_SHOPPING = TOTAL_SHOPPING - Integer.parseInt(pric);
                                TOTAL_SHOPPING = TOTAL_SHOPPING + priceActual;

                                pric = String.valueOf(priceActual) + " lei";
                                price.setText(pric);
                                intent.putExtra("quantity" + i, "1");

                                TextView amount = findViewById(R.id.amount);
                                setNewAmount(String.valueOf(TOTAL_SHOPPING), amount);
                                quantity.setText(String.valueOf("1"));
                            } else {
                                int quantityActual = Integer.parseInt(actQuan);
                                priceActual = priceEntity * quantityActual;

                                TOTAL_SHOPPING = TOTAL_SHOPPING - Integer.parseInt(pric);
                                TOTAL_SHOPPING = TOTAL_SHOPPING + priceActual;

                                pric = String.valueOf(priceActual) + " lei";
                                price.setText(pric);
                                intent.putExtra("quantity" + i, actQuan);

                                TextView amount = findViewById(R.id.amount);
                                setNewAmount(String.valueOf(TOTAL_SHOPPING), amount);
                            }
                        }
                    }
                }
            }
        });
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

    private void setNewAmount(String newAmount, TextView amount) {
        Button order = findViewById(R.id.order_button);
        TextView msg = findViewById(R.id.suma_minima);

        if (Integer.parseInt(newAmount) != 0) {
            if (Integer.parseInt(newAmount) < GlobalConst.MINIMAL_AMOUNT) {
                order.setVisibility(View.GONE);
                msg.setVisibility(View.VISIBLE);

                newAmount += " lei";
                SpannableString ss = new SpannableString(newAmount);
                ss.setSpan(new UnderlineSpan(), 0, newAmount.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                amount.setText(ss);

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
            delete.setOnClickListener(deleteListener(id));
        }
    }

    private View.OnClickListener deleteListener(final int id) {
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
                FLAG = true;
                Resources res = getResources();
                TextView textView = findViewById(res.getIdentifier(
                        "text" + String.valueOf(id), "id", getPackageName()));
                TextView price = findViewById(res.getIdentifier(
                        "price" + String.valueOf(id), "id", getPackageName()));
                String actQuan = textView.getText().toString();
                String clearPrice = price.getText().toString().substring(0, price.getText().toString().indexOf(" "));
                actQuan = actQuan.equals("0") || actQuan.equals("") ? "1" : actQuan;
                int priceEntity = Integer.parseInt(clearPrice) / Integer.parseInt(actQuan);

                if (plus) {
                    String txt = textView.getText().toString().equals("") ? "1" : textView.getText().toString();
                    int newText = Integer.parseInt(txt);

                    if (newText < 200) {
                        setPlus(textView);

                        TOTAL_SHOPPING = TOTAL_SHOPPING + priceEntity;
                        String newPrice = String.valueOf(Integer.parseInt(clearPrice) + priceEntity) + " lei";
                        price.setText(newPrice);
                    }
                } else {
                    String txt = textView.getText().toString().equals("") ? "1" : textView.getText().toString();
                    int currText = Integer.parseInt(txt);

                    if (currText == 1) {
                        ConstraintLayout layout = findViewById(res.getIdentifier(
                                "lay" + String.valueOf(id), "id", getPackageName()));
                        layout.setVisibility(View.GONE);
                        extras.removeExtra("product" + id);
                        COUNTER--;
                    } else {
                        setMinus(textView);
                    }

                    TOTAL_SHOPPING = TOTAL_SHOPPING - priceEntity;
                    String newPrice = String.valueOf(Integer.parseInt(clearPrice) - priceEntity) + " lei";
                    price.setText(newPrice);
                }

                String updQuantity = textView.getText().toString().equals("") ? "1" : textView.getText().toString();

                if (Integer.parseInt(updQuantity) != 200) {
                    ORDER_NUMBER = (int) (Math.random() * 10000);
                    TextView orderNumber = findViewById(R.id.order_number);
                    orderNumber.setText(String.valueOf(ORDER_NUMBER));

                    TextView amount = findViewById(R.id.amount);
                    setNewAmount(String.valueOf(TOTAL_SHOPPING), amount);

                    GlobalConst.intent.putExtra("quantity" + id, updQuantity);
                }

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

        int newText = text - 1;
        textView.setText(String.valueOf(newText));
    }

    public class SendRequest extends AsyncTask<String, Void, String> {
        protected void onPreExecute() {
        }

        protected String doInBackground(String... arg0) {
            try {
                URL url = new URL("https://script.google.com/macros/s/AKfycbxFZC9YPpG1-A_StZ583S0jtkmApNOEFclM8eKJ3aLB0zwiHN0/exec");
//                JSONObject postDataParams = new JSONObject();

                String id = "1V9XMhpxFsfe8xXgWEfnGIYcPyLWd8wll9evUmRofDxs";

                postDataParams.put("comandName", "Comanda №");
                postDataParams.put("comandVal", ORDER_NUMBER);
                postDataParams.put("dateName", "Data/Ora:");
                postDataParams.put("dateVal", DATE_VAL);
                postDataParams.put("produsName", "Produs");
                postDataParams.put("quantityName", "Cantitatea");
                postDataParams.put("amountName", "Total");
                postDataParams.put("amountVal", TOTAL_SHOPPING);
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
}
