package com.starkogi.mamamboga;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.starkogi.mamamboga.Models.Item;
import com.starkogi.mamamboga.MpesaApi.Utils;
import com.starkogi.mamamboga.MpesaApi.api.ApiClient;
import com.starkogi.mamamboga.MpesaApi.api.model.AccessToken;
import com.starkogi.mamamboga.MpesaApi.api.model.STKPush;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;

import static com.starkogi.mamamboga.MpesaApi.AppConstants.BUSINESS_SHORT_CODE;
import static com.starkogi.mamamboga.MpesaApi.AppConstants.CALLBACKURL;
import static com.starkogi.mamamboga.MpesaApi.AppConstants.PARTYB;
import static com.starkogi.mamamboga.MpesaApi.AppConstants.PASSKEY;
import static com.starkogi.mamamboga.MpesaApi.AppConstants.TRANSACTION_TYPE;

public class Cart extends AppCompatActivity {

    ArrayList<Item> items = new ArrayList<>();
    ArrayList<String> items_ = new ArrayList<>();

    private ListView items_list;
    FloatingActionButton fab_pay;
    TextView tv_total;

    private Dialog myDialog;
    private ProgressDialog mProgressDialog;
    private ApiClient mApiClient;

    int total_amount = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);


        items =  getIntent().getParcelableArrayListExtra("extra");

        Log.e("sendt", new Gson().toJson(items));
        for (Item i:items) {
            total_amount += i.getItemPrice();
            items_.add(i.getItemName() + " | Ksh." + Integer.toString(i.getItemPrice()));
        }
        initComponents();
    }

    private void initComponents() {
        items_list = findViewById(R.id.items);
        tv_total = findViewById(R.id.tv_total);
        fab_pay = findViewById(R.id.fab_pay);

        mApiClient = new ApiClient();
        mApiClient.setIsDebug(true); //Set True to enable logging, false to disable.

        getAccessToken();

        myDialog = new Dialog(this);
        mProgressDialog = new ProgressDialog(this);


        ArrayAdapter arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, items_);
        items_list.setAdapter(arrayAdapter);


        tv_total.setText(Integer.toString(0));

        fab_pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowPopup();
            }
        });

        tv_total.setText(Integer.toString(total_amount));

    }

    public void getAccessToken() {

        mApiClient.setGetAccessToken(true);
        mApiClient.mpesaService().getAccessToken().enqueue(new Callback<AccessToken>() {
            @Override
            public void onResponse(Call<AccessToken> call, retrofit2.Response<AccessToken> response) {
                if (response.isSuccessful()) {
                    mApiClient.setAuthToken(response.body().accessToken);
                    //Toast.makeText(getApplicationContext(), response.body().accessToken, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<AccessToken> call, Throwable t) {

            }
        });
    }

    public void ShowPopup() {
        final TextView txtclose, tv_amount_due_pop, tv_prev_reading_pop, tv_current_reading_pop, tv_acc_no_pop;
        final EditText et_amount, et_phone_number;
        Button btn_pay;
        myDialog.setContentView(R.layout.custom_pay_popup);
        myDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT);

        et_amount = myDialog.findViewById(R.id.et_amount);
        tv_amount_due_pop = myDialog.findViewById(R.id.tv_amount_due_pop);
        et_phone_number = myDialog.findViewById(R.id.et_phone_number);

        et_amount.setText(Integer.toString(total_amount));
        tv_amount_due_pop.setText(Integer.toString(total_amount));

        txtclose = myDialog.findViewById(R.id.txtclose);
        btn_pay = (Button) myDialog.findViewById(R.id.btn_pay);
        txtclose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDialog.dismiss();
            }
        });
        btn_pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!TextUtils.isEmpty(et_amount.getText().toString()) && Integer.parseInt(et_amount.getText().toString()) > 0 ){
                    performSTKPush(Integer.parseInt(et_amount.getText().toString()), et_phone_number.getText().toString());
                }else {
                    et_amount.setError("Add Amount to be paid");
                }
            }
        });

        myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        myDialog.show();
    }

    public void performSTKPush(int amount, String phone) {
        mProgressDialog.setMessage("Please Wait");
        mProgressDialog.setTitle("Mpesa Payment");
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.show();
        String timestamp = Utils.getTimestamp();
        STKPush stkPush = new STKPush(
                BUSINESS_SHORT_CODE,
                Utils.getPassword(BUSINESS_SHORT_CODE, PASSKEY, timestamp),
                timestamp,
                TRANSACTION_TYPE,
                String.valueOf(amount),
                Utils.sanitizePhoneNumber(phone),
                PARTYB,
                Utils.sanitizePhoneNumber(phone),
                CALLBACKURL,
                Utils.sanitizePhoneNumber(phone), //The account reference
                "Items Bought. Date : " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date())  //The transaction description
        );

        mApiClient.setGetAccessToken(false);

        mApiClient.mpesaService().sendPush(stkPush).enqueue(new Callback<JsonObject>() {

            @Override
            public void onResponse(Call<JsonObject> call, retrofit2.Response<JsonObject> response) {
                mProgressDialog.dismiss();
                View view = getLayoutInflater().inflate(R.layout.activity_main, null);

                if (response.body().has("ResponseCode")){
                    String message = "Please Record this for future reference \n\n" + "ResponseDescription : " + response.body().get("ResponseDescription").toString() + "\n" +
                            "CheckoutRequestID : " + response.body().get("CheckoutRequestID").toString() + "\n" +
                            "MerchantRequestID : " + response.body().get("MerchantRequestID").toString() + "\n";

                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();

                }else {
                    Toast.makeText(getApplicationContext(), "Error Response " + response.message(), Toast.LENGTH_LONG).show();


                }
//                AccessToken accessToken = new Gson().fromJson(response.toString(),
//                        AccessToken.class);
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                mProgressDialog.dismiss();
//                AccessToken accessToken = new Gson().fromJson(response.toString(),
//                        AccessToken.class);
                Toast.makeText(getApplicationContext(), "Error Response " + t.getMessage(), Toast.LENGTH_LONG).show();
            }


        });
    }


}
