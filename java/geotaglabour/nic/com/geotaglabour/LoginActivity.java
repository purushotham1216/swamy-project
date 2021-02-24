package geotaglabour.nic.com.geotaglabour;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpsTransportSE;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends Activity {
    String certificate="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";
    EditText edt_name, edt_pwd;
    Button btn_sign_in, btn_offline;
    String str_username, str_pswd, pswd_mdf;
    private ProgressDialog progressDialog;
    SoapObject request;
    Object resultas;
    JSONObject jobj;
    private static String SOAP_ACTION = null;
    private static String OPERATION_NAME = null;
    String str_db_scheme, str_db_pswd;
    ArrayList<String> list_username, list_pwd;
    String User_name, Password;
    int tab_id = 1;
    String deviceId, deviceIMEI;
    List<SoapObject> Login_response_list;
    String response = null;
    SharedPreferences sharePref;

    private LocationManager locationManager;

    int MyVersion = Build.VERSION.SDK_INT;
    // Db class
    Database db;
    Cursor c;
    String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // setContentView(R.layout.login);
        setContentView(R.layout.activity_login);
        // int valu = 1/0;

        if (MyVersion > Build.VERSION_CODES.LOLLIPOP_MR1) {
            if (!checkIfAlreadyhavePermission()) {
                requestForSpecificPermission();
                // deviceId = Secure.getString(getContentResolver(),
                // Secure.ANDROID_ID);
                //
                // TelephonyManager tManager = (TelephonyManager)
                // getBaseContext()
                // .getSystemService(Context.TELEPHONY_SERVICE);
                // deviceIMEI = tManager.getDeviceId();
            }
        }
		/*
		 * deviceId = Secure.getString(getContentResolver(), Secure.ANDROID_ID);
		 *
		 * TelephonyManager tManager = (TelephonyManager) getBaseContext()
		 * .getSystemService(Context.TELEPHONY_SERVICE); deviceIMEI =
		 * tManager.getDeviceId();
		 */

        checkGps();

        db = new Database(this);

        //dbInti();

        sharePref = getApplicationContext().getSharedPreferences("geoTag", 0);

        edt_name = (EditText) findViewById(R.id.edt_log_username);

        edt_pwd = (EditText) findViewById(R.id.edt_log_password);

//        if (Utilities.showLogs == 0) {
//            //
//            edt_name.setText("VISWA_SAI_ADS");
//            edt_pwd.setText("VISWA_SAI_ADS$123");
//
//            // edt_name.setText("VISWA_SAI_ADS");
//            // edt_pwd.setText("Test@123");//bocAP@789 Test@123
//        }
        str_pswd = edt_pwd.getText().toString().trim();

        // Log.i("deviceIMEI", deviceIMEI);

        btn_sign_in = (Button) findViewById(R.id.btn_log_submit);
        btn_offline = (Button) findViewById(R.id.btn_go_offline);

        btn_sign_in.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {
                str_username = edt_name.getText().toString().trim();
                str_pswd = edt_pwd.getText().toString().trim();

//                deviceId = Settings.Secure.getString(getContentResolver(),
//                        Settings.Secure.ANDROID_ID);
//
//                TelephonyManager tManager = (TelephonyManager) getBaseContext()
//                        .getSystemService(Context.TELEPHONY_SERVICE);
//                deviceIMEI = tManager.getDeviceId();

                if (str_username.length() == 0) {

                    Utilities.Error_Msg(LoginActivity.this, "Please Enter User ID");
                    return;
                }
                if (str_pswd.length() == 0) {

                    Utilities.Error_Msg(LoginActivity.this, "Please Enter Password");
                    return;
                }
                if (str_username.length() > 0 && str_pswd.length() > 0) {

                    // startActivity(new Intent(Login.this,
                    // GetWorkOrders.class));
                    // finish();

                    // Toast.makeText(LoginActivity.this, "Button clicked", Toast.LENGTH_SHORT).show();
                    callLoginCheck();
                    //callLoginVersionCheck();
//                    Intent in = new Intent(LoginActivity.this, GetWorkDetails.class);
//                    startActivity(in);
//                    finish();


                    //   new loginChkLabourDept().execute();

                }

            }
        });
        btn_offline.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {
                SharedPreferences.Editor editor = sharePref.edit();
                editor.putString("userentered", edt_name.getText().toString()
                        .trim());

                editor.commit();


                String x= sharePref.getString("userentered","");

                Log.i("value is",x);
                Intent i = new Intent(LoginActivity.this, OfflineActivitty.class);
                startActivity(i);
                finish();

            }

        });

    }

    public void checkGps() {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) == false) {
            buildAlertMessageNoGps();
        }

        // Intent i=new Intent(OfflineActivitty.this,Login.class);
        // startActivity(i);

    }

    private void buildAlertMessageNoGps() {
        // TODO Auto-generated method stub
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(
                "Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(
                                    @SuppressWarnings("unused") final DialogInterface dialog,
                                    @SuppressWarnings("unused") final int id) {

                                startActivity(new Intent(
                                        android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                                dialog.cancel();
                            }
                        });
        // .setNegativeButton("No", new DialogInterface.OnClickListener() {
        // public void onClick(final DialogInterface dialog,
        // @SuppressWarnings("unused") final int id) {
        // dialog.cancel();
        // }
        // });
        final AlertDialog alert = builder.create();
        alert.show();
    }



    String verision;

    String str_rescode;
    String str_result;
    private void callLoginCheck() {
        try {

            String resoonce_loan = new CheckLogin().execute().get();
            //  JSONArray jsonarray;

            // System.out.println("ffff--->"+response.getClass());
            // Toast.makeText(LoginActivity.this, "response is"+response.trim(), Toast.LENGTH_SHORT).show();


            try {
                JSONObject j = new JSONObject(resoonce_loan);
                str_result = j.getString("result");

                str_rescode=j.getString("resultcode");

                Log.i("result code is",str_rescode);
                Log.i("result  is",str_result);

                if (str_result.equalsIgnoreCase("Success")) {
                    SharedPreferences.Editor editor = sharePref.edit();
                    editor.putString("userentered", edt_name.getText().toString()
                            .trim());
                    editor.putString("ad_agency_code",str_rescode);
                    //  editor.commit();

                    editor.putString("Version",
                            (Utilities.getVersionNameCode(LoginActivity.this)));
                    editor.commit();

                    String x= sharePref.getString("userentered","");
                    String y=sharePref.getString("ad_agency_code","");
                    String z=sharePref.getString("Version","");
                    Log.i("value is",y);
                    Log.i("value of z is",z);
                    Intent in = new Intent(LoginActivity.this, GetWorkDetails.class);
                    startActivity(in);
                    finish();

                }

                else if(str_result.equalsIgnoreCase("Failure"))
                {
                    Utilities.showAlertDialog(LoginActivity.this,"invalid login","Please check the Details",true);
                }

            }
            catch (Exception e)
            {
                e.printStackTrace();
            }


//
        } catch (InterruptedException | ExecutionException e1) {
            if (Utilities.showLogs == 0) {
                e1.printStackTrace();
            }
        }

    }

    public class CheckLogin extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(LoginActivity.this);
            progressDialog.setMessage("Login...");
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {

            try {


                String password=Utilities.sha256(str_pswd);

                String mainpassword=Utilities.sha256(password);


//
                JSONArray arr = new JSONArray();
                HashMap<String, JSONObject> map = new HashMap<String, JSONObject>();

                JSONObject jsonne = new JSONObject();
                jsonne.put("loginid", str_username);
                //  jsonne.put("randNum", rand);
                jsonne.put("pwd", mainpassword);

                map.put("json" + 0, jsonne);
                arr.put(map.get("json" + 0));

                if (Utilities.showLogs == 0) {

                    Log.d("json req", arr.toString());

                }

                response = WsUtility.executePostHttps("https://apbocwwb.ap.nic.in/restfultest/doj/webservice/json/response/"+str_username+"/"+mainpassword+"/"+certificate+"",
                        arr.toString(), "GET");
              //  System.out.println("response----->"+response);
//                response = WsUtility.executePostHttps("http://10.160.2.89:8080/RESTWebApp_1/doj/webservice/json/response/"+str_username+"/"+mainpassword+"",
//                        arr.toString(), "GET");

//                response = WsUtility.executePostHttps("https://apbocwwb.ap.nic.in/restfultest/doj/webservice/json/testlogin/"+str_username+"/"+mainpassword+"/"+certificate+"",
//                        arr.toString(), "GET");
                progressDialog.dismiss();
            } catch (Exception e) {
                progressDialog.dismiss();
                return null;
            }
            return response;
        }

        @Override
        protected void onPostExecute(final String resp) {

            super.onPostExecute(resp);
            progressDialog.dismiss();
        }
    }
    private void dbInti() {
        // TODO Auto-generated method stub

        try {

            callMethod();

        } catch (Exception e) {
            String destPath = "/data/data/" + getPackageName()
                    + "/databases/GeoDb";
            File f = new File(destPath);
            if (f.exists()) {
                Log.v("Status", "Data path is arranged");

                try {
                    // Toast.makeText(getApplicationContext(), "dbloc",
                    // 5000).show();
                    CopyDB(getBaseContext().getAssets().open(
                            "geolocation.sqlite"), new FileOutputStream(
                            destPath));
                } catch (FileNotFoundException e1) {
                    e1.printStackTrace();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }

            }
        }
        db.close();

    }

    public boolean callMethod() {
        boolean b = false;

        c = db.getPhoto_tbl();
        if (c.getCount() > 0) {
            b = true;
            return b;
        }

        else {
            return b;
        }

    }

    public void CopyDB(InputStream inputStream, OutputStream outputStream)
            throws IOException {

        byte[] buffer = new byte[1024];

        int length;
        while ((length = inputStream.read(buffer)) > 0) {
            outputStream.write(buffer, 0, length);
        }

        Log.v("Status", "Data is copied");
        inputStream.close();
        outputStream.close();

    }

    private boolean checkIfAlreadyhavePermission() {
        int result = ContextCompat.checkSelfPermission(LoginActivity.this,
                Manifest.permission.CAMERA);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    private void requestForSpecificPermission() {
        String[] PERMISSIONS = { Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.READ_PHONE_STATE };

        ActivityCompat.requestPermissions(LoginActivity.this, PERMISSIONS, 101);
    }


}

