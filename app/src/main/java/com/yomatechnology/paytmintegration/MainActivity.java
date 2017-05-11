package com.yomatechnology.paytmintegration;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.paytm.pgsdk.PaytmOrder;
import com.paytm.pgsdk.PaytmPGService;
import com.paytm.pgsdk.PaytmPaymentTransactionCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    private ProgressDialog progressBar;
    private Button start_transaction;
    private String message, checkSum;
    private boolean success;
    private String orderId, customerId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        initView();

        start_transaction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Connectivity.isConnected(MainActivity.this)) {
                    //generate checksum
                    new GenerateChecksum().execute();
                }
            }
        });

    }

    private void initView() {
        // unique random order id
        Calendar c = Calendar.getInstance();
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmSSSS");
        String order = format.format(c.getTime());
        orderId = String.valueOf("ORDER" + order);
        // unique random customer id
        Random r = new Random(System.currentTimeMillis());
        customerId = "CUST_ID" + (1 + r.nextInt(2)) * 10000
                + r.nextInt(10000);

        start_transaction = (Button) findViewById(R.id.start_transaction);
    }

    //generate checksum
    /*please find backend code for checksum generation according to your backend language code
    from here https://paytm.com/business/payments/developers*/

    private class GenerateChecksum extends AsyncTask<String, String, JSONObject> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar = new ProgressDialog(MainActivity.this);
            progressBar.setCancelable(false);
            progressBar.show();

        }

        @Override
        protected JSONObject doInBackground(String... params) {
            HashMap<String, String> param = new HashMap<>();

            param.put("MID", "Test48224931917259");//your merchant id provided by paytm
            param.put("OrderId", orderId);//unique order id
            param.put("CustomerId", customerId);//unique customer id
            param.put("IndustryType", "Retail");//industry type provided by paytm
            param.put("CHANNEL_ID", "WAP");//channel id provided by paytm
            param.put("TxnAmount", "1");//your transaction amount
            param.put("WEBSITE", "APP_STAGING");//website provided by paytm
            param.put("CallbackUrl", "https://pguat.paytm.com/paytmchecksum/paytmCallback.jsp");//callback url provided by paytm

            JSONParserUpdated jsonParserUpdated = new JSONParserUpdated();
            //required your server url for checksum generation
            JSONObject json = jsonParserUpdated.makeHttpRequest("your_url", "POST", param);

            if (json != null) {

                try {
                    String status = json.getString("Success");
                    message = json.getString("Message");
                    Log.e("message", message);
                    if (status.equals("1")) {
                        success = true;
                        checkSum = json.getString("CheckSum");
                        Log.e("ServiceHandler", "find data from the url");
                    } else {
                        success = false;
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } else {
                Log.e("ServiceHandler", "Couldn't get any data from the url");
                success = false;
                message = "Please check internet connection";
            }

            return null;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);
            if (progressBar.isShowing()) {
                progressBar.dismiss();
            }

            if (success) {
                onStartTransaction();
            } else {
                Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG).show();
            }
        }

    }


    public void onStartTransaction() {
        PaytmPGService Service = PaytmPGService.getStagingService();

        //Kindly create complete Map and checksum on your server side and then put it here in paramMap.
        //please use exactly same parameters ,you used for checksum generation.
        Map<String, String> paramMap = new HashMap<String, String>();
        paramMap.put("MID", "Test48224931917259");//your merchant id provided by paytm
        paramMap.put("ORDER_ID", orderId);//unique order id
        paramMap.put("CUST_ID", customerId);//unique customer id
        paramMap.put("INDUSTRY_TYPE_ID", "Retail");//industry type provided by paytm
        paramMap.put("CHANNEL_ID", "WAP");//channel id provided by paytm
        paramMap.put("TXN_AMOUNT", "1");//your transaction amount
        paramMap.put("WEBSITE", "APP_STAGING");//website provided by paytm
        paramMap.put("CALLBACK_URL", "https://pguat.paytm.com/paytmchecksum/paytmCallback.jsp");//callback url provided by paytm
        paramMap.put("CHECKSUMHASH", checkSum);//generated checksum from your server end
        PaytmOrder Order = new PaytmOrder(paramMap);


        Service.initialize(Order, null);

        Service.startPaymentTransaction(this, true, true,
                new PaytmPaymentTransactionCallback() {

                    @Override
                    public void someUIErrorOccurred(String inErrorMessage) {
                        Log.d("LOG", "UI Error Occur.");
                        Toast.makeText(getApplicationContext(), " UI Error Occur. ", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onTransactionResponse(Bundle inResponse) {
                        Log.d("LOG", "Payment Transaction : " + inResponse);
                        Toast.makeText(getApplicationContext(), "Payment Transaction response " + inResponse.toString(), Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void networkNotAvailable() {
                        Log.d("LOG", "UI Error Occur.");
                        Toast.makeText(getApplicationContext(), " UI Error Occur. ", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void clientAuthenticationFailed(String inErrorMessage) {
                        Log.d("LOG", "UI Error Occur.");
                        Toast.makeText(getApplicationContext(), " Severside Error " + inErrorMessage, Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onErrorLoadingWebPage(int iniErrorCode,
                                                      String inErrorMessage, String inFailingUrl) {

                    }

                    @Override
                    public void onBackPressedCancelTransaction() {
                        // TODO Auto-generated method stub
                    }

                    @Override
                    public void onTransactionCancel(String inErrorMessage, Bundle inResponse) {
                        Log.d("LOG", "Payment Transaction Failed " + inErrorMessage);
                        Toast.makeText(getBaseContext(), "Payment Transaction Failed ", Toast.LENGTH_LONG).show();
                    }
                });
    }
}
