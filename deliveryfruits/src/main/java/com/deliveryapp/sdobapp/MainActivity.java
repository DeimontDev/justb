package com.deliveryapp.sdobapp;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.text.InputFilter;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import static com.deliveryapp.sdobapp.GlobalConst.*;

public class MainActivity extends AppCompatActivity {

    private static int TOTAL_MAIN;
    private final int CALL_REQUEST = 100;

    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(
                        Activity.INPUT_METHOD_SERVICE);

        if (inputMethodManager != null) {
            if (activity.getCurrentFocus() != null) {
                inputMethodManager.hideSoftInputFromWindow(
                        activity.getCurrentFocus().getWindowToken(), 0);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        TextView counter = findViewById(R.id.counter);
        counter.setText(String.valueOf(COUNTER));
        findViewById(R.id.call_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callPhoneNumber();
            }
        });

        TOTAL_MAIN = ShoppingCartActivity.TOTAL_SHOPPING;
        TextView laMoment = findViewById(R.id.la_moment);
        String text = String.valueOf(TOTAL_MAIN) + " lei";

        if (TOTAL_MAIN < MINIMAL_AMOUNT) {
            laMoment.setTextColor(Color.parseColor("#FFBE1C27"));
        } else {
            laMoment.setTextColor(Color.parseColor("#FF19BE40"));
        }

        laMoment.setText(text);
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
        processTextQuantity(res);
        processImages(res);
    }

    public void processImages(final Resources res) {
        for (int i = 0; i < 20; i++) {
            int id = i + 1;
            final ImageButton img = findViewById(res.getIdentifier(
                    "img" + String.valueOf(id), "id", getPackageName()));
            img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    hideSoftKeyboard(MainActivity.this);

                    //TODO   Below is handled condition when user added some product to basket,
                    //TODO   modified manually quantity, and clicks on some image. At the moment it's not
                    //TODO   working, need to set correctly 'la_moment' value

//                    for (int j = 1; j <= 20 ; j++) {
//                        TextView price = findViewById(res.getIdentifier(
//                                "price" + String.valueOf(j), "id", getPackageName()));
//                        String pric = price.getText().toString().substring(0, price.getText().toString().indexOf(" "));
//                        TextView quantity = findViewById(res.getIdentifier(
//                                "text" + String.valueOf(j), "id", getPackageName()));
//                        String actQuan = quantity.getText().toString();
//                        String oldQuantity = intent == null ? "1" : intent.getStringExtra("quantity" + j);
//                        AppCompatButton toBasket = findViewById(res.getIdentifier(
//                                "button" + String.valueOf(j), "id", getPackageName()));
//                        String addedToBasket = toBasket.getText().toString();
//                        String added = getString(R.string.added);
//
//                        if (!actQuan.equals(oldQuantity) && addedToBasket.equals(added)) {
//                            int priceEntity = Integer.parseInt(pric);
//
//                            if (actQuan.equals("") || actQuan.equals("0")) {
//                                int priceActual = Integer.parseInt(oldQuantity) * priceEntity;
//                                TOTAL_MAIN = TOTAL_MAIN - priceActual;
//
//                                intent.putExtra("quantity" + j, "1");
//
//                                TextView laMoment = findViewById(R.id.la_moment);
//                                String text = String.valueOf(TOTAL_MAIN) + " lei";
//                                laMoment.setText(text);
//                                quantity.setText(String.valueOf("1"));
//                            } else {
//                                int quantityActual = Integer.parseInt(actQuan);
//                                priceEntity = priceEntity * quantityActual;
//
//                                TOTAL_MAIN = TOTAL_MAIN - Integer.parseInt(pric);
//                                TOTAL_MAIN = TOTAL_MAIN + priceEntity;
//
//                                intent.putExtra("quantity" + j, actQuan);
//
//                                TextView laMoment = findViewById(R.id.la_moment);
//                                String text = String.valueOf(TOTAL_MAIN) + " lei";
//                                laMoment.setText(text);
//                            }
//                        }
//                    }
                }
            });
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

        ConstraintLayout cont = findViewById(R.id.constr_layout);
        cont.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideSoftKeyboard(MainActivity.this);
            }
        });
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
            callIntent.setData(Uri.parse("tel:" + PHONE_NUMBER));
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

    /**
     * Process plus and minus buttons, set listeners
     *
     * @param res Resources
     */
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

    /**
     * Process actions with adding and removing products from shopping basket
     *
     * @param id Product id
     * @return On click listener object
     */
    private View.OnClickListener basketListener(final int id) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.setBackgroundColor(Color.parseColor("#FFB77E5E"));
                TextView textView = (TextView) view;
                TextView counter = findViewById(R.id.counter);
                Resources res = getResources();

                TextView laMoment = findViewById(R.id.la_moment);
                TextView price = findViewById(res.getIdentifier(
                        "price" + String.valueOf(id), "id", getPackageName()));
                String clearPrice = price.getText().toString().substring(0, price.getText().toString().indexOf(" "));
                int intPrice = Integer.parseInt(clearPrice);

                if (textView.getText().toString().equals("Adaugă in coș")) {
                    TextView quantity = findViewById(res.getIdentifier(
                            "text" + String.valueOf(id), "id", getPackageName()));
                    String actQuan = quantity.getText().toString();

                    if (actQuan.equals("")) {
                        quantity.setText("1");
                    }

                    actQuan = quantity.getText().toString();
                    textView.setText(R.string.added);

                    int totalPrice = intPrice * Integer.parseInt(actQuan);

                    TOTAL_MAIN += totalPrice;
                    String text = String.valueOf(TOTAL_MAIN) + " lei";
                    laMoment.setText(text);

                    String newCount = String.valueOf(Integer.parseInt(counter.getText().toString()) + 1);
                    counter.setText(newCount);
                    COUNTER++;
                    TextView productName = findViewById(res.getIdentifier(
                            "product" + String.valueOf(id), "id", getPackageName()));

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

                    TextView quantity = findViewById(res.getIdentifier(
                            "text" + String.valueOf(id), "id", getPackageName()));
                    String oldQuant = intent.getStringExtra("quantity" + id);
                    String actQuan = quantity.getText().toString();

                    //If user deleted all digits, set default value
                    if (actQuan.equals("")) {
                        quantity.setText("1");
                    }

                    //Calculate total price for given product
                    actQuan = quantity.getText().toString();
                    int totalPrice = intPrice * Integer.parseInt(actQuan);

                    //This if is true only when user selected some product with some quantity,
                    //then modified quant value, and deselect product.
                    if (Integer.parseInt(oldQuant) != Integer.parseInt(actQuan)) {
                        totalPrice = intPrice * Integer.parseInt(oldQuant);
                    }

                    TOTAL_MAIN = TOTAL_MAIN - totalPrice;

                    String text = String.valueOf(TOTAL_MAIN) + " lei";
                    laMoment.setText(text);
                }

                if (TOTAL_MAIN < MINIMAL_AMOUNT) {
                    laMoment.setTextColor(Color.parseColor("#FFBE1C27"));
                } else {
                    laMoment.setTextColor(Color.parseColor("#FF19BE40"));
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
                AppCompatButton toBasket = findViewById(res.getIdentifier(
                        "button" + String.valueOf(id), "id", getPackageName()));

                TextView laMoment = findViewById(R.id.la_moment);
                TextView price = findViewById(res.getIdentifier(
                        "price" + String.valueOf(id), "id", getPackageName()));
                String clearPrice = price.getText().toString().substring(0, price.getText().toString().indexOf(" "));
                int priceEntity = Integer.parseInt(clearPrice);

                if (plus) {
                    int newText = Integer.parseInt(textView.getText().toString());

                    if (newText < 200) {
                        setPlus(textView);

                        if (!toBasket.getText().toString().equals("Adaugă in coș")) {
                            TOTAL_MAIN = TOTAL_MAIN + priceEntity;
                            String newPrice = String.valueOf(TOTAL_MAIN) + " lei";
                            laMoment.setText(newPrice);
                        }
                    }
                } else {
                    int text = Integer.parseInt(textView.getText().toString());
                    setMinus(textView);

                    if (!toBasket.getText().toString().equals("Adaugă in coș")) {
                        if (text > 1) {
                            TOTAL_MAIN = TOTAL_MAIN - priceEntity;
                            String newPrice = String.valueOf(TOTAL_MAIN) + " lei";
                            laMoment.setText(newPrice);
                        }
                    }
                }

                if (GlobalConst.intent == null) {
                    GlobalConst.intent = new Intent(MainActivity.this, ShoppingCartActivity.class);
                }

                intent.putExtra("quantity" + id, textView.getText().toString());

                if (TOTAL_MAIN < MINIMAL_AMOUNT) {
                    laMoment.setTextColor(Color.parseColor("#FFBE1C27"));
                } else {
                    laMoment.setTextColor(Color.parseColor("#FF19BE40"));
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
