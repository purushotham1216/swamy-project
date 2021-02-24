package geotaglabour.nic.com.geotaglabour;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.kobjects.base64.Base64;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.MarshalBase64;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

import beans.GetAdvtAgencyDetailsBean;
import beans.GetWorkOrderDetailsBean;

import static android.graphics.ImageFormat.JPEG;

public class GetWorkDetails extends Activity implements OnItemSelectedListener {
	String certificatedol="MIIGvDCCBaSgAwIBAgIQJaj7srmeK3FWgl8HGkOxXjANBgkqhkiG9w0BAQsFADB3MQswCQYDVQQGEwJVUzEdMBsGA1UEChMUU3ltYW50ZWMgQ29ycG9yYXRpb24xHzAdBgNVBAsTFlN5bWFudGVjIFRydXN0IE5ldHdvcmsxKDAmBgNVBAMTH1N5bWFudGVjIENsYXNzIDMgRVYgU1NMIENBIC0gRzMwHhcNMTcwODAzMDAwMDAwWhcNMTkwODAzMjM1OTU5WjCBwTETMBEGCysGAQQBgjc8AgEDEwJJTjEaMBgGA1UEDxMRR292ZXJubWVudCBFbnRpdHkxGjAYBgNVBAUTEUdvdmVybm1lbnQgRW50aXR5MQswCQYDVQQGEwJJTjEOMAwGA1UECAwFRGVsaGkxEjAQBgNVBAcMCU5ldyBEZWxoaTEkMCIGA1UECgwbTmF0aW9uYWwgSW5mb3JtYXRpY3MgQ2VudHJlMRswGQYDVQQDDBJhcGJvY3d3Yi5hcC5uaWMuaW4wggEiMA0GCSqGSIb3DQEBAQUAA4IBDwAwggEKAoIBAQDCvVIk6gg6BwcQ5junKu8Q+VB8r$kr2pAx820UpdM1jSwwBdrJe05OFc51T7DTOkFmeMfFdba2SegVcMcO7ts7tvic+RqtjftEVwrrVOcRoVI1jf6eG3VPhR$VguvhjzoYYvvExzPdIm3qp+JNCdnXcBFwtLYr0Fy0itR632avvxk6H$tySZu1i2IVwUx3eKIjtSRwaUj0kYgHLWK5L9$hT4c8TYbT$JN4$kJGvhvfNpyAg13icUCela0LvOn4K5jYzbzsNN$LiBj7$1Dc5kTv4OFKJvBfgS4by7GNwgH5VRR8aWUiVEAzeorlD1Z1oXbABnSrrNtRPGLeX8wr3481AgMBAAGjggL3MIIC8zAdBgNVHREEFjAUghJhcGJvY3d3Yi5hcC5uaWMuaW4wCQYDVR0TBAIwADAOBgNVHQ8BAf8EBAMCBaAwKwYDVR0fBCQwIjAgoB6gHIYaaHR0cDovL3NyLnN5bWNiLmNvbS9zci5jcmwwbwYDVR0gBGgwZjBbBgtghkgBhvhFAQcXBjBMMCMGCCsGAQUFBwIBFhdodHRwczovL2Quc3ltY2IuY29tL2NwczAlBggrBgEFBQcCAjAZDBdodHRwczovL2Quc3ltY2IuY29tL3JwYTAHBgVngQwBATAdBgNVHSUEFjAUBggrBgEFBQcDAQYIKwYBBQUHAwIwHwYDVR0jBBgwFoAUAVmr5906C1mmZGPWzyAHV9WR52owVwYIKwYBBQUHAQEESzBJMB8GCCsGAQUFBzABhhNodHRwOi8vc3Iuc3ltY2QuY29tMCYGCCsGAQUFBzAChhpodHRwOi8vc3Iuc3ltY2IuY29tL3NyLmNydDCCAX4GCisGAQQB1nkCBAIEggFuBIIBagFoAHUA3esdK3oNT6Ygi4GtgWhwfi6OnQHVXIiNPRHEzbbsvswAAAFdpy4f5AAABAMARjBEAiA$COcA6OxyXiATII4cL6kbIThlTe84Z4i+I1Oq7AwH3QIgZ9p9Y$CjJmh+dA3MqermxNq7aHA82pdR$yJ2AXvtt$YAdwCkuQmQtBhYFIe7E6LMZ3AKPDWYBPkb37jjd80OyA3cEAAAAV2nLiAiAAAEAwBIMEYCIQDACoBOp4GFydzjAptn3iaxFranKykliCP$PUwwwAij$QIhALcKJKmo82KsH1MQttvWaR0JOdKyxkjrGhRVnT86qspHAHYA7ku9t3XOYLrhQmkfq+GeZqMPfl+wctiDAMR7iXqo$csAAAFdpy4h5AAABAMARzBFAiEAwuk$vO1ixJ+ltQis4BRT3EY8wmWwCWRAP$vGDiY2scICIFpzTHzv998MMJTtexAuTGskocdadjkSYLVFCD355pMhMA0GCSqGSIb3DQEBCwUAA4IBAQB$XECWxgHhh+FC6s9041WewKQ8vp2zLCCTy+NU3SHPIAwUogWkWQU7Mx+N24vBNWmcWKqekkXgVYF4xJyCzbOs6xKvlbv7bTGnarBXf6ydFXUV+B37NZ4mlRjR0XH7YPxCWuItjJdqrKBSc$KbATojZvxkgzTM5EA1UcxPuwaQekhPEC5SN1842cZFL$K7MxgMR3Ja60lgn0rMFkXZU8lP17gkPzjK$zHdBIqTra9kfM8ywOjUDuN5nJsNpxf$ZpID1LZ2k0PX7rMDVIsnJbWtF539Eq4e3YbtsV9bCjPp1mq7OC7WwDwVLfduRPIXhjlPfK1UQXKhpHeaF4utlClt";
	String certificate="MIIGvDCCBaSgAwIBAgIQJaj7srmeK3FWgl8HGkOxXjANBgkqhkiG9w0BAQsFADB3MQswCQYDVQQGEwJVUzEdMBsGA1UEChMUU3ltYW50ZWMgQ29ycG9yYXRpb24xHzAdBgNVBAsTFlN5bWFudGVjIFRydXN0IE5ldHdvcmsxKDAmBgNVBAMTH1N5bWFudGVjIENsYXNzIDMgRVYgU1NMIENBIC0gRzMwHhcNMTcwODAzMDAwMDAwWhcNMTkwODAzMjM1OTU5WjCBwTETMBEGCysGAQQBgjc8AgEDEwJJTjEaMBgGA1UEDxMRR292ZXJubWVudCBFbnRpdHkxGjAYBgNVBAUTEUdvdmVybm1lbnQgRW50aXR5MQswCQYDVQQGEwJJTjEOMAwGA1UECAwFRGVsaGkxEjAQBgNVBAcMCU5ldyBEZWxoaTEkMCIGA1UECgwbTmF0aW9uYWwgSW5mb3JtYXRpY3MgQ2VudHJlMRswGQYDVQQDDBJhcGJvY3d3Yi5hcC5uaWMuaW4wggEiMA0GCSqGSIb3DQEBAQUAA4IBDwAwggEKAoIBAQDCvVIk6gg6BwcQ5junKu8Q+VB8r/kr2pAx820UpdM1jSwwBdrJe05OFc51T7DTOkFmeMfFdba2SegVcMcO7ts7tvic+RqtjftEVwrrVOcRoVI1jf6eG3VPhR/VguvhjzoYYvvExzPdIm3qp+JNCdnXcBFwtLYr0Fy0itR632avvxk6H/tySZu1i2IVwUx3eKIjtSRwaUj0kYgHLWK5L9/hT4c8TYbT/JN4/kJGvhvfNpyAg13icUCela0LvOn4K5jYzbzsNN/LiBj7/1Dc5kTv4OFKJvBfgS4by7GNwgH5VRR8aWUiVEAzeorlD1Z1oXbABnSrrNtRPGLeX8wr3481AgMBAAGjggL3MIIC8zAdBgNVHREEFjAUghJhcGJvY3d3Yi5hcC5uaWMuaW4wCQYDVR0TBAIwADAOBgNVHQ8BAf8EBAMCBaAwKwYDVR0fBCQwIjAgoB6gHIYaaHR0cDovL3NyLnN5bWNiLmNvbS9zci5jcmwwbwYDVR0gBGgwZjBbBgtghkgBhvhFAQcXBjBMMCMGCCsGAQUFBwIBFhdodHRwczovL2Quc3ltY2IuY29tL2NwczAlBggrBgEFBQcCAjAZDBdodHRwczovL2Quc3ltY2IuY29tL3JwYTAHBgVngQwBATAdBgNVHSUEFjAUBggrBgEFBQcDAQYIKwYBBQUHAwIwHwYDVR0jBBgwFoAUAVmr5906C1mmZGPWzyAHV9WR52owVwYIKwYBBQUHAQEESzBJMB8GCCsGAQUFBzABhhNodHRwOi8vc3Iuc3ltY2QuY29tMCYGCCsGAQUFBzAChhpodHRwOi8vc3Iuc3ltY2IuY29tL3NyLmNydDCCAX4GCisGAQQB1nkCBAIEggFuBIIBagFoAHUA3esdK3oNT6Ygi4GtgWhwfi6OnQHVXIiNPRHEzbbsvswAAAFdpy4f5AAABAMARjBEAiA/COcA6OxyXiATII4cL6kbIThlTe84Z4i+I1Oq7AwH3QIgZ9p9Y/CjJmh+dA3MqermxNq7aHA82pdR/yJ2AXvtt/YAdwCkuQmQtBhYFIe7E6LMZ3AKPDWYBPkb37jjd80OyA3cEAAAAV2nLiAiAAAEAwBIMEYCIQDACoBOp4GFydzjAptn3iaxFranKykliCP/PUwwwAij/QIhALcKJKmo82KsH1MQttvWaR0JOdKyxkjrGhRVnT86qspHAHYA7ku9t3XOYLrhQmkfq+GeZqMPfl+wctiDAMR7iXqo/csAAAFdpy4h5AAABAMARzBFAiEAwuk/vO1ixJ+ltQis4BRT3EY8wmWwCWRAP/vGDiY2scICIFpzTHzv998MMJTtexAuTGskocdadjkSYLVFCD355pMhMA0GCSqGSIb3DQEBCwUAA4IBAQB/XECWxgHhh+FC6s9041WewKQ8vp2zLCCTy+NU3SHPIAwUogWkWQU7Mx+N24vBNWmcWKqekkXgVYF4xJyCzbOs6xKvlbv7bTGnarBXf6ydFXUV+B37NZ4mlRjR0XH7YPxCWuItjJdqrKBSc/KbATojZvxkgzTM5EA1UcxPuwaQekhPEC5SN1842cZFL/K7MxgMR3Ja60lgn0rMFkXZU8lP17gkPzjK/zHdBIqTra9kfM8ywOjUDuN5nJsNpxf/ZpID1LZ2k0PX7rMDVIsnJbWtF539Eq4e3YbtsV9bCjPp1mq7OC7WwDwVLfduRPIXhjlPfK1UQXKhpHeaF4utlClt";
	Spinner spn_WorkOrder, spn_PlaceofAd, spn_Districts, spn_Mandal,spn_ad_code_category,
			spn_Panchayat;
	public static ImageView imgWork;
	EditText edt_location;
	String location;
	Button btn_takepic;
	LinearLayout hide_table_layout_benf_details;
	SharedPreferences sharePref;
	Context context;
	private ProgressDialog progressDialog;

	String district = "", mandal = "", panchayat = "", str_edt_location;
	List<String> dist_name_list, dist_id_list, mandal_id_list,
			mandal_name_list, pan_code_list, pan_name_list;
	ArrayList<String>  messageList,
			ad_place_idList, ad_place_nameList,advt_categories,advt_categories_id;
	String deviceIMEI;
	GPSTracker gps;
	private LocationManager locationManager;

	public static double latitude = 0, longitude = 0;
	public static Bitmap capturedBitmap;
	public static String stringbitmap;
	File destination;
	Database db;
	private static final int TAKE_PICTURE = 0;
	String selected_sp_dist_code = "", selected_sp_mandal_code = "",
			selected_sp_pan_code = "", ad_agency_code , versioncheck = "";
	TextView txt_ad_agency_name, txt_ad_authorised_email_id,
			txt_ad_authorised_mobile_no, txt_ad_authorised_person,
			title_agency_details;

	Button btn_quit, btn_submit, btn_cancl, btn_upload;

	String userentered = "";
	private RequestQueue requestQueue;
	public static final String REQUEST_TAG = "GetWorkDetails";
	// newly added constants by saritha on 3rd april
	private static final String TAG = GetWorkDetails.class.getSimpleName();

	private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;

	private static final String ADDRESS_REQUESTED_KEY = "address-request-pending";
	private static final String LOCATION_ADDRESS_KEY = "location-address";



	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_get_work_details);
		db = new Database(this);

		StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
		StrictMode.setVmPolicy(builder.build());
		context = GetWorkDetails.this.getApplicationContext();
		sharePref = getApplicationContext().getSharedPreferences("geoTag", 0);

		ad_agency_code = sharePref.getString("resultcode", "");
		Log.i("ad agency code", ad_agency_code);

		userentered = sharePref.getString("userentered", "");

		Log.i(" userenterd", "");

		versioncheck = sharePref.getString("Version", "");

		Log.i("versoin is", versioncheck);

		spn_WorkOrder = (Spinner) findViewById(R.id.spn_WorkOrder);
		spn_WorkOrder.setOnItemSelectedListener(this);

		spn_ad_code_category = (Spinner) findViewById(R.id.spn_ad_code_category);
		spn_ad_code_category.setOnItemSelectedListener(this);

		spn_PlaceofAd = (Spinner) findViewById(R.id.spn_plc_of_ad);
		spn_PlaceofAd.setOnItemSelectedListener(this);

		spn_Districts = (Spinner) findViewById(R.id.spn_districts);
		spn_Districts.setOnItemSelectedListener(this);

		spn_Mandal = (Spinner) findViewById(R.id.spn_mandal);
		spn_Mandal.setOnItemSelectedListener(this);

		spn_Panchayat = (Spinner) findViewById(R.id.spn_panchayat);
		spn_Panchayat.setOnItemSelectedListener(this);

		imgWork = (ImageView) findViewById(R.id.imgWork);
		imgWork.setVisibility(View.VISIBLE);
		btn_upload = (Button) findViewById(R.id.btn_upload);

		intializeViews();


		checkGps();

	}

	private void checkGps() {
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

		if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) == false) {
			buildAlertMessageNoGps();
		}

	}


	private void checkGpsTakepic() {
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

		if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) == false) {
			buildAlertMessageNoGps();
		} else {
			onTakePic();
		}
	}

	// new location code adding




	private void intializeViews() {

		hide_table_layout_benf_details = (LinearLayout) findViewById(R.id.hide_table_layout_benf_details);

		TelephonyManager tManager = (TelephonyManager) getBaseContext()
				.getSystemService(Context.TELEPHONY_SERVICE);
		deviceIMEI = tManager.getDeviceId();

		edt_location = (EditText) findViewById(R.id.edt_location);
		location = edt_location.getText().toString().trim();

		photoImage = (ImageView) findViewById(R.id.imgWork);
		// SharedPreferences prefs = getSharedPreferences(str_imgName,
		// MODE_PRIVATE);

		txt_ad_agency_name = (TextView) findViewById(R.id.txt_ad_agency_name);
		txt_ad_authorised_email_id = (TextView) findViewById(R.id.txt_ad_authorised_email_id);
		txt_ad_authorised_mobile_no = (TextView) findViewById(R.id.txt_ad_authorised_mobile_no);
		txt_ad_authorised_person = (TextView) findViewById(R.id.txt_ad_authorised_person);
		btn_submit = (Button) findViewById(R.id.btn_submit);
		btn_cancl = (Button) findViewById(R.id.btn_cancel);
		btn_submit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				validateDetails();

				try {
					requestQueue = Volley.newRequestQueue(getApplicationContext(), new HurlStack(null, pinnedSSLSocketFactory()));
				} catch (CertificateException e1) {
					e1.printStackTrace();
				} catch (IOException e1) {
					e1.printStackTrace();
				} catch (KeyStoreException e1) {
					e1.printStackTrace();
				} catch (NoSuchAlgorithmException e1) {
					e1.printStackTrace();
				} catch (KeyManagementException e1) {
					e1.printStackTrace();
				}
				//	insertion();
			}
		});


		btn_upload.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (worko0rderPstn == 0) {
					Utilities.Error_Msg(GetWorkDetails.this,
							"Please Select WorkOrder");

					return;

				}
				if (spn_advt_category_ptn == 0) {
					Utilities.Error_Msg(GetWorkDetails.this,
							"Please Select Advt category");

					return;
				} else if (sp_dist_pstn == 0) {
					Utilities.Error_Msg(GetWorkDetails.this,
							"Please Select District");
					return;
				} else if (sp_mandal_pstn == 0) {
					Utilities.Error_Msg(GetWorkDetails.this,
							"Please Select Mandal");
					return;
				} else if (spn_panch_ptn == 0) {
					Utilities.Error_Msg(GetWorkDetails.this,
							"Please Select Panchayat");
					return;
				} else if (edt_location.getText().toString().trim().length() == 0) {
					Utilities.Error_Msg(GetWorkDetails.this,
							"Please Enter Location");
					return;
				}

				else {

					Editor editor = sharePref.edit();
					editor.putString("ad_work_order_no", selected_spn_WorkOrder);
					editor.putString("ad_code", adcode);
					editor.putString("imei", deviceIMEI);
					editor.putString("ad_work_order_serial_code",
							ad_work_order_serial_code);
					editor.putString("location", edt_location.getText()
							.toString().trim());
					editor.putString("distcode", selected_sp_dist_code);
					editor.putString("mandcode", selected_sp_mandal_code);
					editor.putString("panchayatcode", selected_sp_pan_code);
					editor.putString("ad_place_code",
							selected_spn_advt_category_code);



					editor.commit();
					Intent i = new Intent(GetWorkDetails.this, ImageList.class);
					startActivity(i);


				}
			}
		});

		btn_cancl.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				Intent i = new Intent(GetWorkDetails.this, GetWorkDetails.class);
				startActivity(i);
				finish();
			}
		});

		btn_quit = (Button) findViewById(R.id.btn_quit);
		btn_quit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				final AlertDialog.Builder builder = new AlertDialog.Builder(
						GetWorkDetails.this);

				builder.setMessage("Are you sure to exit?")
						.setCancelable(false)
						.setPositiveButton("Yes",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
														int id) {

										finish();

									}
								})
						.setNegativeButton("No",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
														int id) {
										dialog.dismiss();
									}
								});
				final AlertDialog alert = builder.create();

				alert.show();

			}
		});


		btn_takepic = (Button) findViewById(R.id.btn_takepic);
		btn_takepic.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				checkGpsTakepic();


			}
		});


		title_agency_details = (TextView) findViewById(R.id.title_agency_details);

		title_agency_details.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				if (openCloseCounterValueBenfDetails == 0) {

					drawbleRightImg = getApplicationContext().getResources()
							.getDrawable(R.drawable.listview_open);
					title_agency_details
							.setCompoundDrawablesWithIntrinsicBounds(null,
									null, drawbleRightImg, null);
					hide_table_layout_benf_details.setVisibility(View.VISIBLE);
					openCloseCounterValueBenfDetails = 1;
				} else {

					drawbleRightImg = getApplicationContext().getResources()
							.getDrawable(R.drawable.listview_close);
					title_agency_details
							.setCompoundDrawablesWithIntrinsicBounds(null,
									null, drawbleRightImg, null);
					hide_table_layout_benf_details.setVisibility(View.GONE);
					openCloseCounterValueBenfDetails = 0;
				}

			}
		});


		gps = new GPSTracker(GetWorkDetails.this);

		latitude = gps.getLatitude();
		longitude = gps.getLongitude();

		Handler mHandler1 = new Handler();
		mHandler1.postDelayed(new Runnable() {

			@Override
			public void run() {
				System.out.println(latitude+"..."+longitude);
				String add = getAddress(latitude,longitude);
					System.out.println("..address...."+add);
				if(add!=null){
					edt_location.setText(add);
					edt_location.setFreezesText(true);
				}else{
					edt_location.setText("");
				}


				//getCompleteAddressString(latitude,						longitude)

				if (edt_location.getText().toString().trim().length() > 0) {

					// edt_location.setEnabled(false);
					edt_location.setClickable(false);
					edt_location.setFocusable(false);

				}

			}

		}, 1000);

	}

	private void buildAlertMessageNoGps() {
		// TODO Auto-generated method stub
		final AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
				.setCancelable(false)
				.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
					public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
						startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
						dialog.cancel();
					}
				});

		final AlertDialog alert = builder.create();
		alert.show();
	}

private String getAddress(double latitude, double longitude){
	Geocoder geocoder = new Geocoder(GetWorkDetails.this, Locale.getDefault());
	try {
		List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
		Address obj = addresses.get(0);
		String add = obj.getAddressLine(0);
		//add = add + "\n" + obj.getCountryName();
		//add = add + "\n" + obj.getCountryCode();
		add = add + "\n" + obj.getAdminArea();
		//add = add + "\n" + obj.getPostalCode();
		add = add + "\n" + obj.getSubAdminArea();
		add = add + "\n" + obj.getLocality();
		//add = add + "\n" + obj.getSubThoroughfare();
		System.out.println("..address...."+add);
		edt_location.setText(add);

		Log.v("IGA", "Address" + add);
		// Toast.makeText(this, "Address=>" + add,
		// Toast.LENGTH_SHORT).show();
		try {

			calladagencydetailsWS();


		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return add;
		// TennisAppActivity.showDialog(add);
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		Toast.makeText(GetWorkDetails.this, e.getMessage(), Toast.LENGTH_SHORT).show();
	}
	return null;
}
	/*private String getCompleteAddressString(double LATITUDE, double LONGITUDE) {
		String strAdd = "";
		Geocoder geocoder = new Geocoder(this, Locale.getDefault());
		try {
			List<Address> addresses = geocoder.getFromLocation(LATITUDE,
					LONGITUDE, 1);
			if (addresses != null) {
				Address returnedAddress = addresses.get(0);
				StringBuilder strReturnedAddress = new StringBuilder("");

				for (int i = 0; i < returnedAddress.getMaxAddressLineIndex(); i++) {
					strReturnedAddress
							.append(returnedAddress.getAddressLine(i)).append(
							"\n");
				}
				strAdd = strReturnedAddress.toString();

			} else {

			}
		} catch (Exception e) {
			e.printStackTrace();

		}

		try {

			calladagencydetailsWS();


		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return strAdd;
	}*/

	int openCloseCounterValueBenfDetails = 1;
	Drawable drawbleRightImg;

	private boolean isNetworkConnectedCallWS() {
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo ni = cm.getActiveNetworkInfo();
		if (ni == null) {
			// // // Log.i("Internet Connection", "*********" + ni);

			Utilities.Error_Msg(GetWorkDetails.this, "No Internet Connection");
			return false;
		} else {

			return true;
		}

	}

	String adagencycode;
	private void calladagencydetailsWS() {
		// TODO Auto-generated method stub
		try {
			String resoonce;
			SharedPreferences.Editor editor = sharePref.edit();

			adagencycode=sharePref.getString("ad_agency_code","");
			Log.i("adagencycode isss",adagencycode);
			String x= sharePref.getString("userentered","");
			Log.i("value is",x);
			Log.i("ad agency code", ad_agency_code);


			resoonce = new GetAdAgencyDetails().execute().get();

			if (Utilities.showLogs == 0) {

				if (resoonce != null) {
					Log.i("resoonce", resoonce);
					System.out.println(resoonce.getClass());
				}

			}
			try
			{
				//System.out.println("hgjhgkgljgufyf");


//               JSONObject j=new JSONObject(resoonce);
//				System.out.println("j isjson object is"+j);
//				String totalString=j.getString("advtagencydetails");
//
//				System.out.println("ad agency details are"+totalString);
				JSONArray jsonArray=new JSONArray(resoonce);
				if(jsonArray.length()>0)
				{
					for (int i = 0; i < jsonArray.length(); i++) {
						JSONObject j2 = jsonArray.getJSONObject(i);

						//JSONObject j2=jsonArray.getString("ad_agency_name");

						String ad_agency_name=j2.getString("ad_agency_name");
						String ad_authorised_email_id=j2.getString("ad_authorised_email_id");
						String ad_authorised_mobile_no=j2.getString("ad_authorised_mobile_no");
						String ad_authorised_person=j2.getString("ad_authorised_person");
						String message=j2.getString("message");


						System.out.println("ad agency "+ad_agency_name);
						System.out.println("ad ad_authorised_email_id "+ad_authorised_email_id);
						System.out.println("ad ad_authorised_mobile_no "+ad_authorised_mobile_no);
						System.out.println("ad ad_authorised_person "+ad_authorised_person);
						System.out.println("ad message "+message);
						System.out.println("ad message "+sharePref.getString("userentered",""));
						ad_agency_code = sharePref.getString("resultcode", "");
						Log.i("ad agency code", ad_agency_code);
						System.out.println("ad agency code is"+sharePref.getString("resultcode", ""));

						userentered = sharePref.getString("userentered", "");

						Log.i(" userenterd", "");

						versioncheck = sharePref.getString("Version", "");

						Log.i("versoin is", versioncheck);
						Log.i("ad_authorised_mobil",ad_agency_name);
						if(message.equalsIgnoreCase("Success"))
						{
							txt_ad_agency_name.setText(ad_agency_name);
							txt_ad_authorised_email_id.setText(ad_authorised_email_id);
							txt_ad_authorised_mobile_no.setText(ad_authorised_mobile_no);
							txt_ad_authorised_person.setText(ad_authorised_person);
							callworkorderinputsWS();
						} else {
							Toast.makeText(getApplicationContext(), "No records",
									Toast.LENGTH_SHORT).show();
						}


					}

				}
			}
//				JSONObject j2=jsonArray.getString("ad_agency_name");
//
//				String ad_agency_name=j2.getString("ad_agency_name");
//				String ad_authorised_email_id=j2.getString("ad_authorised_email_id");
//				String ad_authorised_mobile_no=j2.getString("ad_authorised_mobile_no");
//				String ad_authorised_person=j2.getString("ad_authorised_person");
//				String message=j2.getString("message");
//
//
//				System.out.println("ad agency "+ad_agency_name);
//				System.out.println("ad ad_authorised_email_id "+ad_authorised_email_id);
//				System.out.println("ad ad_authorised_mobile_no "+ad_authorised_mobile_no);
//				System.out.println("ad ad_authorised_person "+ad_authorised_person);
//               	System.out.println("ad message "+message);
//
//
//				Log.i("ad_authorised_mobil",ad_agency_name);
//				if(message.equalsIgnoreCase("Success"))
//				{
//					        txt_ad_agency_name.setText(ad_agency_name);
//							txt_ad_authorised_email_id.setText(ad_authorised_email_id);
//							txt_ad_authorised_mobile_no.setText(ad_authorised_mobile_no);
//							txt_ad_authorised_person.setText(ad_authorised_person);
//					callworkorderinputsWS();
//						} else {
//							Toast.makeText(getApplicationContext(), "No records",
//									Toast.LENGTH_SHORT).show();
//						}
//
//
//			}
			catch(Exception e)
			{
				e.printStackTrace();
			}


		}

		catch(Exception e)
		{
			e.printStackTrace();
		}
	}




	public class GetAdAgencyDetails extends AsyncTask<Void, Void, String> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progressDialog = new ProgressDialog(GetWorkDetails.this);
			progressDialog.setMessage("Loading District data.\nPlease wait...");
			progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			progressDialog.setCancelable(false);
			progressDialog.show();
		}

		@Override
		protected String doInBackground(Void... params) {
			String response = null;
			try {

				JSONArray arr = new JSONArray();
				HashMap<String, JSONObject> map = new HashMap<String, JSONObject>();

				JSONObject jsonne = new JSONObject();


				// jsonne.put("ad_agency_code", ad_agency_code);

				Log.i("ad_agency_code",ad_agency_code);

				map.put("json" + 0, jsonne);
				arr.put(map.get("json" + 0));
				ad_agency_code = sharePref.getString("resultcode", "");
				Log.i("ad agency code", ad_agency_code);

				response=WsUtility.executePostHttps("https://apbocwwb.ap.nic.in/restfultest/doj/webservice/json/advtagencydetails/"+adagencycode+"/"+certificate+"","", "GET");
				//System.out.println("gghgdjgvjkgcvjkgcv;k"+response);

			} catch (Exception e) {
				return null;
			} finally {
				progressDialog.dismiss();
			}
			return response;
		}

		@Override
		protected void onPostExecute(final String resp) {
			super.onPostExecute(resp);
			progressDialog.dismiss();
		}

	}



	List<String> ad_work_order_noList;

	private void callworkorderinputsWS() {
		// TODO Auto-generated method stub
		try {
			String resoonce;
			resoonce = new GetWorkOrderInputs().execute().get();

			if (Utilities.showLogs == 0) {

				if (resoonce != null) {
					Log.i("resoonce", resoonce);

				}

			}

			try {

//				JSONObject j=new JSONObject(resoonce);
//				System.out.println("j isjson object is"+j);
//				String totalString=j.getString("workorderinputs");
//				System.out.println("workorder main resul is"+totalString);
				JSONArray jsonArray=new JSONArray(resoonce);
				if(jsonArray.length()>0) {
					for (int i = 0; i < jsonArray.length(); i++) {


						JSONObject j2 = jsonArray.getJSONObject(i);

						System.out.println("j isjson object is" + j2);
						String ad_work_order_no = j2.getString("ad_work_order_no");
						String message = j2.getString("message");

						System.out.println("ad agency details are" + ad_work_order_no);
						System.out.println("j isjson object is" + j2);
						System.out.println("ad agency details are" + message);

						ad_work_order_noList = new ArrayList<String>();
						ad_work_order_noList.add("Select Workorder");
						messageList = new ArrayList<String>();
						messageList.add("0");


						if (message.equalsIgnoreCase("Success")) {

							ad_work_order_noList.add(ad_work_order_no);
							messageList.add(message);
						}
					}
				}
				ArrayAdapter<String> fcipointAdapter = new ArrayAdapter<String>(
						getApplicationContext(), R.layout.spinner_text,
						ad_work_order_noList) {
					@Override
					public boolean isEnabled(int position) {
						if (position == 0) {
							// Disable the second item from Spinner
							return false;
						} else {
							return true;
						}
					}

					@Override
					public View getDropDownView(int position,
												View convertView, ViewGroup parent) {
						View view = super.getDropDownView(position,
								convertView, parent);
						TextView tv = (TextView) view;
						if (position == 0) {
							// Set the disable item text color
							tv.setTextColor(Color.GRAY);
						} else {
							tv.setTextColor(Color.BLACK);
						}
						return view;
					}
				};
				spn_WorkOrder.setAdapter(fcipointAdapter);

				//	}

			} catch (JSONException e) {
				if (Utilities.showLogs == 0) {
					e.printStackTrace();
				}
			} catch (Exception e) {
				if (Utilities.showLogs == 0) {
					e.printStackTrace();
				}
			}

		} catch (InterruptedException | ExecutionException e1) {
			if (Utilities.showLogs == 0) {
				e1.printStackTrace();
			}
		}

	}


	public class GetWorkOrderInputs extends AsyncTask<Void, Void, String> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progressDialog = new ProgressDialog(GetWorkDetails.this);
			progressDialog.setMessage("Loading District data.\nPlease wait...");
			progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			progressDialog.setCancelable(false);
			progressDialog.show();
		}

		@Override
		protected String doInBackground(Void... params) {
			String response = null;
			try {

				JSONArray arr = new JSONArray();
				HashMap<String, JSONObject> map = new HashMap<String, JSONObject>();

				JSONObject jsonne = new JSONObject();


				jsonne.put("ad_agency_code", ad_agency_code);

				map.put("json" + 0, jsonne);
				arr.put(map.get("json" + 0));

				response=WsUtility.executePostHttps("https://apbocwwb.ap.nic.in/restfultest/doj/webservice/json/workorderinputs/"+adagencycode+"/"+certificate+"",
						"", "GET");


			} catch (Exception e) {
				return null;
			} finally {
				progressDialog.dismiss();
			}
			return response;
		}

		@Override
		protected void onPostExecute(final String resp) {
			super.onPostExecute(resp);
			progressDialog.dismiss();
		}

	}



	private void callplaceofAdWS() {
		// TODO Auto-generated method stub
		try {
			String resoonce;
			resoonce = new PlaceofAdWS().execute().get();

			if (Utilities.showLogs == 0) {

				if (resoonce != null) {
					Log.i("resoonce", resoonce);

				}

			}

			try{

//				JSONObject j = new JSONObject(resoonce);
//				String adplace = j.getString("adplace");
//				System.out.println(adplace);

				JSONArray jsonArray = new JSONArray(resoonce);
				if (jsonArray.length() > 0) {
					ad_place_nameList = new ArrayList<String>();

					ad_place_nameList.add("Select place ");
					ad_place_idList = new ArrayList<String>();
					ad_place_idList.add("0");
//
					for (int i = 0; i < jsonArray.length(); i++) {
						JSONObject jsonobject = jsonArray.getJSONObject(i);

						String ad_place_name = jsonobject.getString("adname");
						String ad_place_id = jsonobject.getString("adcode");

//
						ad_place_nameList.add(ad_place_name);
						ad_place_idList.add(ad_place_id);

					}
				}

				ArrayAdapter<String> fcipointAdapter = new ArrayAdapter<String>(
						getApplicationContext(), R.layout.spinner_text,
						ad_place_nameList) {
					@Override
					public boolean isEnabled(int position) {
						if (position == 0) {
							// Disable the second item from Spinner
							return false;
						} else {
							return true;
						}
					}

					@Override
					public View getDropDownView(int position,
												View convertView, ViewGroup parent) {
						View view = super.getDropDownView(position,
								convertView, parent);
						TextView tv = (TextView) view;
						if (position == 0) {
							// Set the disable item text color
							tv.setTextColor(Color.GRAY);
						} else {
							tv.setTextColor(Color.BLACK);
						}
						return view;
					}
				};
				spn_PlaceofAd.setAdapter(fcipointAdapter);
//
//                }

			} catch(JSONException e){
				if (Utilities.showLogs == 0) {
					e.printStackTrace();
				}
			} catch(Exception e){
				if (Utilities.showLogs == 0) {
					e.printStackTrace();
				}
			}

		} catch (InterruptedException | ExecutionException e1) {
			if (Utilities.showLogs == 0) {
				e1.printStackTrace();
			}
		}

	}
	//added newly based on advt categories on 03/04/18
	private void callAdvtCategories() {
		// TODO Auto-generated method stub
		try {
			String resoonce;
			resoonce = new AdvtCategories().execute().get();

			if (Utilities.showLogs == 0) {

				if (resoonce != null) {
					Log.i("resoonce", resoonce);

				}

			}

			try{

//				JSONObject j = new JSONObject(resoonce);
//				String adplace = j.getString("adplace");
//				System.out.println(adplace);

				JSONArray jsonArray = new JSONArray(resoonce);
				if (jsonArray.length() > 0) {
					advt_categories = new ArrayList<String>();

					advt_categories.add("Select advt category ");
					advt_categories_id = new ArrayList<String>();
					advt_categories_id.add("0");
//
					for (int i = 0; i < jsonArray.length(); i++) {
						JSONObject jsonobject = jsonArray.getJSONObject(i);

						String ad_cat_name = jsonobject.getString("ad_name");
						String ad_cat_id = jsonobject.getString("ad_code");

//
						advt_categories.add(ad_cat_name);
						advt_categories_id.add(ad_cat_id);

					}
				}

				ArrayAdapter<String> fcipointAdapter = new ArrayAdapter<String>(
						getApplicationContext(), R.layout.spinner_text,
						advt_categories) {
					@Override
					public boolean isEnabled(int position) {
						if (position == 0) {
							// Disable the second item from Spinner
							return false;
						} else {
							return true;
						}
					}

					@Override
					public View getDropDownView(int position,
												View convertView, ViewGroup parent) {
						View view = super.getDropDownView(position,
								convertView, parent);
						TextView tv = (TextView) view;
						if (position == 0) {
							// Set the disable item text color
							tv.setTextColor(Color.GRAY);
						} else {
							tv.setTextColor(Color.BLACK);
						}
						return view;
					}
				};
				spn_ad_code_category.setAdapter(fcipointAdapter);
				callplaceofAdWS();
//
//                }

			} catch(JSONException e){
				if (Utilities.showLogs == 0) {
					e.printStackTrace();
				}
			} catch(Exception e){
				if (Utilities.showLogs == 0) {
					e.printStackTrace();
				}
			}

		} catch (InterruptedException | ExecutionException e1) {
			if (Utilities.showLogs == 0) {
				e1.printStackTrace();
			}
		}

	}

	//--------------------------------------------------

	public class PlaceofAdWS extends AsyncTask<Void, Void, String> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progressDialog = new ProgressDialog(GetWorkDetails.this);
			progressDialog.setMessage("Loading District data.\nPlease wait...");
			progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			progressDialog.setCancelable(false);
			progressDialog.show();
		}

		@Override
		protected String doInBackground(Void... params) {
			String response = null;
			try {

				JSONArray arr = new JSONArray();
				HashMap<String, JSONObject> map = new HashMap<String, JSONObject>();

				JSONObject jsonne = new JSONObject();


				jsonne.put("ad_work_order_no", selected_spn_WorkOrder);

				map.put("json" + 0, jsonne);
				arr.put(map.get("json" + 0));

				response=WsUtility.executePostHttps("https://apbocwwb.ap.nic.in/restfultest/doj/webservice/json/adplace/"+selected_spn_WorkOrder+"/"+certificatedol+"",
						"", "GET");

			} catch (Exception e) {
				return null;
			} finally {
				progressDialog.dismiss();
			}
			return response;
		}

		@Override
		protected void onPostExecute(final String resp) {
			super.onPostExecute(resp);
			progressDialog.dismiss();
		}

	}

	//newly added based on ad categories on 03/04/18
	public class AdvtCategories extends AsyncTask<Void, Void, String> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progressDialog = new ProgressDialog(GetWorkDetails.this);
			progressDialog.setMessage("Loading Categories data.\nPlease wait...");
			progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			progressDialog.setCancelable(false);
			progressDialog.show();
		}

		@Override
		protected String doInBackground(Void... params) {
			String response = null;
			try {

				JSONArray arr = new JSONArray();
				HashMap<String, JSONObject> map = new HashMap<String, JSONObject>();

				JSONObject jsonne = new JSONObject();


				jsonne.put("ad_work_order_no", selected_spn_WorkOrder);

				map.put("json" + 0, jsonne);
				arr.put(map.get("json" + 0));

				response=WsUtility.executePostHttps("https://apbocwwb.ap.nic.in/restfultest/doj/webservice/json/ad_categories/"+selected_spn_WorkOrder+"/"+certificatedol+"",
						"", "GET");

			} catch (Exception e) {
				return null;
			} finally {
				progressDialog.dismiss();
			}
			return response;
		}

		@Override
		protected void onPostExecute(final String resp) {
			super.onPostExecute(resp);
			progressDialog.dismiss();
		}

	}


	private void callDistrictDataWS() {
		try {
			String resoonce;
			resoonce = new GetDistDetailsDataWS().execute().get();

			if (Utilities.showLogs == 0) {

				if (resoonce != null) {
					Log.i("resoonce", resoonce);

				}

			}

			try{

//				JSONObject j = new JSONObject(resoonce);
//				String districts = j.getString("districts");
//				System.out.println(districts);

				JSONArray jsonArray = new JSONArray(resoonce);
				if (jsonArray.length() > 0) {
					dist_name_list = new ArrayList<String>();
					dist_name_list.add("Select District ");
					dist_id_list = new ArrayList<String>();
					dist_id_list.add("0");
//
					for (int i = 0; i < jsonArray.length(); i++) {
						JSONObject jsonobject = jsonArray.getJSONObject(i);

						String dname = jsonobject.getString("distname");
						String dcode = jsonobject.getString("distcode");

//
						dist_name_list.add(dname);
						dist_id_list.add(dcode);

					}
				}

				ArrayAdapter<String> fcipointAdapter = new ArrayAdapter<String>(
						getApplicationContext(), R.layout.spinner_text,
						dist_name_list) {
					@Override
					public boolean isEnabled(int position) {
						if (position == 0) {
							// Disable the second item from Spinner
							return false;
						} else {
							return true;
						}
					}

					@Override
					public View getDropDownView(int position,
												View convertView, ViewGroup parent) {
						View view = super.getDropDownView(position,
								convertView, parent);
						TextView tv = (TextView) view;
						if (position == 0) {
							// Set the disable item text color
							tv.setTextColor(Color.GRAY);
						} else {
							tv.setTextColor(Color.BLACK);
						}
						return view;
					}
				};
				spn_Districts.setAdapter(fcipointAdapter);
//
//                }

			} catch(JSONException e){
				if (Utilities.showLogs == 0) {
					e.printStackTrace();
				}
			} catch(Exception e){
				if (Utilities.showLogs == 0) {
					e.printStackTrace();
				}
			}

		} catch (InterruptedException | ExecutionException e1) {
			if (Utilities.showLogs == 0) {
				e1.printStackTrace();
			}
		}

	}




	public class GetDistDetailsDataWS extends AsyncTask<Void, Void, String> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progressDialog = new ProgressDialog(GetWorkDetails.this);
			progressDialog.setMessage("Loading District data.\nPlease wait...");
			progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			progressDialog.setCancelable(false);
			progressDialog.show();
		}

		@Override
		protected String doInBackground(Void... params) {
			String response = null;
			try {

				JSONArray arr = new JSONArray();
				HashMap<String, JSONObject> map = new HashMap<String, JSONObject>();

				JSONObject jsonne = new JSONObject();


				jsonne.put("ad_work_order_no", selected_spn_WorkOrder);

				map.put("json" + 0, jsonne);
				arr.put(map.get("json" + 0));

				response=WsUtility.executePostHttps("https://apbocwwb.ap.nic.in/restfultest/doj/webservice/json/districts/"+selected_spn_WorkOrder+"/"+certificatedol+"",
						arr.toString(),"GET");

			} catch (Exception e) {
				return null;
			} finally {
				progressDialog.dismiss();
			}
			return response;
		}

		@Override
		protected void onPostExecute(final String resp) {
			super.onPostExecute(resp);
			progressDialog.dismiss();
		}

	}



	// mandal ws

	private void callMandalDataWS() {
		try {
			String resoonce;
			resoonce = new GetMandDetailsDataWS().execute().get();

			if (Utilities.showLogs == 0) {

				if (resoonce != null) {
					Log.i("resoonce", resoonce);

				}

			}

			try{

//				JSONObject j = new JSONObject(resoonce);
//				String mandals = j.getString("mandals");
//				System.out.println(mandals);

				JSONArray jsonArray = new JSONArray(resoonce);
				if (jsonArray.length() > 0) {
					mandal_name_list = new ArrayList<String>();
					mandal_name_list.add("Select Mandal ");
					mandal_id_list = new ArrayList<String>();
					mandal_id_list.add("0");
//
					for (int i = 0; i < jsonArray.length(); i++) {
						JSONObject jsonobject = jsonArray.getJSONObject(i);

						String mname = jsonobject.getString("mndname");
						String mdcode = jsonobject.getString("mndcode");

//
						mandal_name_list.add(mname);
						mandal_id_list.add(mdcode);

					}
				}

				ArrayAdapter<String> fcipointAdapter = new ArrayAdapter<String>(
						getApplicationContext(), R.layout.spinner_text,
						mandal_name_list) {
					@Override
					public boolean isEnabled(int position) {
						if (position == 0) {
							// Disable the second item from Spinner
							return false;
						} else {
							return true;
						}
					}

					@Override
					public View getDropDownView(int position,
												View convertView, ViewGroup parent) {
						View view = super.getDropDownView(position,
								convertView, parent);
						TextView tv = (TextView) view;
						if (position == 0) {
							// Set the disable item text color
							tv.setTextColor(Color.GRAY);
						} else {
							tv.setTextColor(Color.BLACK);
						}
						return view;
					}
				};
				spn_Mandal.setAdapter(fcipointAdapter);
//
//                }

			} catch(JSONException e){
				if (Utilities.showLogs == 0) {
					e.printStackTrace();
				}
			} catch(Exception e){
				if (Utilities.showLogs == 0) {
					e.printStackTrace();
				}
			}

		} catch (InterruptedException | ExecutionException e1) {
			if (Utilities.showLogs == 0) {
				e1.printStackTrace();
			}
		}

	}



	public class GetMandDetailsDataWS extends AsyncTask<Void, Void, String> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progressDialog = new ProgressDialog(GetWorkDetails.this);
			progressDialog.setMessage("Loading Mandal data.\nPlease wait...");
			progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			progressDialog.setCancelable(false);
			progressDialog.show();
		}

		@Override
		protected String doInBackground(Void... params) {
			String response = null;
			try {

				JSONArray arr = new JSONArray();
				HashMap<String, JSONObject> map = new HashMap<String, JSONObject>();

				JSONObject jsonne = new JSONObject();
				jsonne.put("distCode", selected_sp_dist_code);
				map.put("json" + 0, jsonne);
				arr.put(map.get("json" + 0));

				if (Utilities.showLogs == 0) {

					Log.d("json req", arr.toString());

				}

//
				response = WsUtility.executePostHttps("https://apbocwwb.ap.nic.in/restfultest/doj/webservice/json/mandals/"+selected_sp_dist_code+"/"+certificate+"",
						"", "GET");
			} catch (Exception e) {
				return null;
			} finally {
				progressDialog.dismiss();
			}
			return response;
		}

		@Override
		protected void onPostExecute(final String resp) {
			super.onPostExecute(resp);
			progressDialog.dismiss();
		}

	}

	// panchayat ws

	private void callpanchayatDataWS() {
		try {
			String resoonce;
			resoonce = new GetpanchayatDetailsDataWS().execute().get();

			if (Utilities.showLogs == 0) {

				if (resoonce != null) {
					Log.i("resoonce", resoonce);

				}

			}

			try{
//
//				JSONObject j = new JSONObject(resoonce);
//				String panchayats = j.getString("panchayats");
//				System.out.println(panchayats);

				JSONArray jsonArray = new JSONArray(resoonce);
				if (jsonArray.length() > 0) {
					pan_name_list = new ArrayList<String>();
					pan_name_list.add("Select panchayat ");
					pan_code_list = new ArrayList<String>();
					pan_code_list.add("0");
//
					for (int i = 0; i < jsonArray.length(); i++) {
						JSONObject jsonobject = jsonArray.getJSONObject(i);

						String panname = jsonobject.getString("panname");
						String pancode = jsonobject.getString("pancode");

//
						pan_name_list.add(panname);
						pan_code_list.add(pancode);

					}
				}

				ArrayAdapter<String> fcipointAdapter = new ArrayAdapter<String>(
						getApplicationContext(), R.layout.spinner_text,
						pan_name_list) {
					@Override
					public boolean isEnabled(int position) {
						if (position == 0) {
							// Disable the second item from Spinner
							return false;
						} else {
							return true;
						}
					}

					@Override
					public View getDropDownView(int position,
												View convertView, ViewGroup parent) {
						View view = super.getDropDownView(position,
								convertView, parent);
						TextView tv = (TextView) view;
						if (position == 0) {
							// Set the disable item text color
							tv.setTextColor(Color.GRAY);
						} else {
							tv.setTextColor(Color.BLACK);
						}
						return view;
					}
				};
				spn_Panchayat.setAdapter(fcipointAdapter);
//
//                }

			} catch(JSONException e){
				if (Utilities.showLogs == 0) {
					e.printStackTrace();
				}
			} catch(Exception e){
				if (Utilities.showLogs == 0) {
					e.printStackTrace();
				}
			}

		} catch (InterruptedException | ExecutionException e1) {
			if (Utilities.showLogs == 0) {
				e1.printStackTrace();
			}
		}

	}


	public class GetpanchayatDetailsDataWS extends
			AsyncTask<Void, Void, String> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progressDialog = new ProgressDialog(GetWorkDetails.this);
			progressDialog
					.setMessage("Loading panchayat data.\nPlease wait...");
			progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			progressDialog.setCancelable(false);
			progressDialog.show();
		}

		@Override
		protected String doInBackground(Void... params) {
			String response = null;
			try {

				JSONArray arr = new JSONArray();
				HashMap<String, JSONObject> map = new HashMap<String, JSONObject>();

				JSONObject jsonne = new JSONObject();


				jsonne.put("distCode", selected_sp_dist_code);
				jsonne.put("mandCode", selected_sp_mandal_code);
				//jsonne.put("crt_usr", pref.getString("Login_userId", ""));

				map.put("json" + 0, jsonne);
				arr.put(map.get("json" + 0));

				Log.d("json req", arr.toString());


				response = WsUtility.executePostHttps("https://apbocwwb.ap.nic.in/restfultest/doj/webservice/json/panchayats/"+selected_sp_dist_code+"/"+selected_sp_mandal_code+"/"+certificate+"",
						arr.toString(), "GET");

			} catch (Exception e) {
				return null;
			} finally {
				progressDialog.dismiss();
			}
			return response;
		}

		@Override
		protected void onPostExecute(final String resp) {
			super.onPostExecute(resp);
			progressDialog.dismiss();
		}

	}
	int sp_dist_pstn = 0;
	int sp_mandal_pstn = 0;
	int spn_PlaceofAd_ptn = 0;
	int spn_advt_category_ptn=0;
	int spn_panch_ptn = 0;
	String selected_spn_PlaceofAd_code = "";
	String selected_spn_advt_category_code = "";
	String selected_spn_WorkOrder = "", strad_code = "",
			selected_spn_Place_of_ad = "";
	int worko0rderPstn = 0;

	public void onItemSelected(AdapterView<?> parent, View view, int position,
							   long id) {

		if (parent == spn_WorkOrder) {

			worko0rderPstn = position;

			if (worko0rderPstn > 0) {

				selected_spn_WorkOrder = spn_WorkOrder.getSelectedItem()
						.toString().trim();
				// new GetWorkOrdersList().execute();
				spn_WorkOrder.setEnabled(false);
				//new GetDistricts().execute();
				//	callDistrictDataWS();
				callAdvtCategories();
				//callplaceofAdWS();
				//callMandalDataWS();
				// callplaceofAdWS();
			} else {

				worko0rderPstn = 0;
			}

		}

		if (parent == spn_Districts) {

			Log.i("sp_dist ", "sp_dist");

			sp_dist_pstn = position;

			if (sp_dist_pstn > 0) {
				district = spn_Districts.getSelectedItem().toString().trim();

				selected_sp_dist_code = dist_id_list.get(sp_dist_pstn)
						.toString();
				Log.i("selected_sp_dist_code ", "sp_dist"
						+ selected_sp_dist_code);
				callMandalDataWS();
				//new GetMandals().execute();
			}


		}

		if (parent == spn_Mandal) {

			Log.i("spn_mandal ", "spn_mandal");

			sp_mandal_pstn = position;

			if (position > 0) {
				mandal = spn_Mandal.getSelectedItem().toString().trim();
				Log.i("spn_mandal onClick ", "sp_mandal" + mandal);

				selected_sp_mandal_code = mandal_id_list.get(sp_mandal_pstn)
						.toString();

				Log.i("selected_code ", "" + selected_sp_mandal_code);

				//new GetPanchayat().execute();
				callpanchayatDataWS();
				// new GetPanchayat().execute();
			}

		}
		if (parent == spn_Panchayat) {

			Log.i("sp_workertype ", "sp_workertype");

			spn_panch_ptn = position;

			if (spn_panch_ptn > 0) {
				panchayat = spn_Panchayat.getSelectedItem().toString().trim();

				Log.i("sp_workertype onClick ", "sp_awc" + panchayat);

				selected_sp_pan_code = pan_code_list.get(spn_panch_ptn)
						.toString();
				Log.i("selected_sp_pan_code ", "" + selected_sp_pan_code);

				//	new GetWorkOrdersList().execute();
				calladCodeDetailsws();
			}

		}
		if (parent == spn_PlaceofAd) {

			Log.i("spn_PlaceofAd ", "spn_PlaceofAd");

			spn_PlaceofAd_ptn = position;

			if (spn_PlaceofAd_ptn > 0) {

				selected_spn_PlaceofAd_code = ad_place_idList.get(
						spn_PlaceofAd_ptn).toString();
				callDistrictDataWS();
			}

		}
		if (parent == spn_ad_code_category) {
			spn_advt_category_ptn = position;

			if (spn_advt_category_ptn > 0) {

				selected_spn_advt_category_code = advt_categories_id.get(
						spn_advt_category_ptn).toString();
				callDistrictDataWS();
			}
//---------commented based on advt select call districts
			/*if (parent == spn_PlaceofAd) {

				Log.i("spn_PlaceofAd ", "spn_PlaceofAd");

				spn_PlaceofAd_ptn = position;

				if (spn_PlaceofAd_ptn > 0) {

					selected_spn_PlaceofAd_code = ad_place_idList.get(
							spn_PlaceofAd_ptn).toString();
					callDistrictDataWS();
				}

			}*/
//----------------------------------------------------------
		}

	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {
		// TODO Auto-generated method stub

	}

	ImageView photoImage;

	private void onTakePic() {

		// selectedView = imageView;

		destination = new File(Environment.getExternalStorageDirectory(),
				"image.jpg");

		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

		intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(destination));
		startActivityForResult(intent, TAKE_PICTURE);
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (requestCode == TAKE_PICTURE && resultCode == Activity.RESULT_OK) {

			try {
				FileInputStream in = new FileInputStream(destination);
				BitmapFactory.Options options = new BitmapFactory.Options();
				options.inSampleSize = 4;
				options.inDither = false;
				options.inPurgeable = true;
				options.inInputShareable = true;
				options.inTempStorage = new byte[16384];

				Bitmap mBitmap = BitmapFactory.decodeStream(in, null, options);
				capturedBitmap = Bitmap.createBitmap(mBitmap.getWidth(),
						mBitmap.getHeight(), Bitmap.Config.ARGB_8888);

				SimpleDateFormat sdf = new SimpleDateFormat(
						"yyyy-MM-dd HH:mm:ss");
				String dateTime = sdf.format(Calendar.getInstance().getTime());
				// create a canvas on which to draw
				Canvas canvas = new Canvas(capturedBitmap);

				Paint paint = new Paint();
				paint.setColor(Color.RED);
				paint.setTextSize(20);
				paint.setFlags(Paint.ANTI_ALIAS_FLAG);

				// if the background image is defined in main.xml, omit this
				// line
				canvas.drawBitmap(mBitmap, 0, 0, null);
				// draw the text and the point
				float fKoordX = 0f, fKoordY = 5f;
				canvas.drawPoint(fKoordX, fKoordY, paint);

				canvas.drawText("N:" + latitude + ", E:" + longitude,
						fKoordX + 3, fKoordY + 30, paint);
				canvas.drawText(dateTime, fKoordX + 3, fKoordY + 55, paint);

				// set the bitmap into the ImageView
				// imgWork.setImageBitmap(capturedBitmap);
				// imgWork.setVisibility(View.VISIBLE);

				if (capturedBitmap != null) {
					photoImage.setImageBitmap(capturedBitmap);
				}

				// new InsertPhotoGeoTag().execute();

			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {

		}

	}

	private void validateDetails() {

		str_edt_location = edt_location.getText().toString().trim();
		if (worko0rderPstn == 0) {
			Utilities.Error_Msg(this, "Please Select WorkOrder");

			return;

		}
		else if (spn_advt_category_ptn == 0) {
			Utilities.Error_Msg(this, "Please Select Advt Category");

			return;
		}
		else if (spn_advt_category_ptn == 0) {
			Utilities.Error_Msg(this, "Please Select Place of ad");

			return;
		} else if (sp_dist_pstn == 0) {
			Utilities.Error_Msg(this, "Please Select District");
			return;
		} else if (sp_mandal_pstn == 0) {
			Utilities.Error_Msg(this, "Please Select Mandal");
			return;
		} else if (spn_panch_ptn == 0) {
			Utilities.Error_Msg(this, "Please Select Panchayat");
			return;
		} else if (edt_location.getText().toString().trim().length() == 0) {
			Utilities.Error_Msg(this, "Please Enter Location");
			return;
		} else if (capturedBitmap == null) {

			Utilities.Error_Msg(this, "Please take Photo");
			return;

		} else {
			// strad_code = getWorkOrderDetailsBeansList.get(0).getAd_code();

			//new InsertPhotoGeoTag().execute();


			insertion();

			//	callinsertionwS();
		}

	}




	public static String getBase64Str(Bitmap bitmap) {

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.JPEG, 70, baos);
		byte[] data = baos.toByteArray();

		String strBase64 = Base64.encode(data);

		return strBase64;
	}

//

	//--------------this service is for ad code--------------------------------------//
	String adcode;
	String ad_work_order_serial_code;
	private void calladCodeDetailsws() {
		// TODO Auto-generated method stub
		try {
			String resoonce;
			resoonce = new GetAdCodeDetailsws().execute().get();

			if (Utilities.showLogs == 0) {

				if (resoonce != null) {
					Log.i("resoonce", resoonce);

				}

			}

			try {
				JSONArray jsonArray=new JSONArray(resoonce);
				if (jsonArray.length() > 0)
				{

					for(int i=0;i<jsonArray.length();i++)
					{
						JSONObject j = jsonArray.getJSONObject(i);

						ad_work_order_serial_code = j.getString("ad_work_order_serial_code");
						adcode = j.getString("ad_code");
						//System.out.println(ad_work_order_serial_code);
						//System.out.println(adcode);
					}
				}


			}
			catch (NullPointerException e) {

			}
		} catch (Exception e) {

		}
	}

	public class GetAdCodeDetailsws extends AsyncTask<Void, Void, String> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progressDialog = new ProgressDialog(GetWorkDetails.this);
			progressDialog.setMessage("Loading District data.\nPlease wait...");
			progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			progressDialog.setCancelable(false);
			progressDialog.show();
		}

		@Override
		protected String doInBackground(Void... params) {
			String response = null;
			try {

				JSONArray arr = new JSONArray();
				HashMap<String, JSONObject> map = new HashMap<String, JSONObject>();

				JSONObject jsonne = new JSONObject();



				jsonne.put("ad_dist_code", selected_sp_dist_code);
				//	jsonne.put("ad_mandal_code", selected_sp_mandal_code);
				jsonne.put("ad_place_code", selected_spn_PlaceofAd_code);
				jsonne.put("ad_agency_code", ad_agency_code);
				jsonne.put("ad_work_order_no", selected_spn_WorkOrder);
				jsonne.put("ad_category_code",selected_spn_advt_category_code);

System.out.println("ad place code 040518--->"+selected_spn_PlaceofAd_code);

				map.put("json" + 0, jsonne);
				arr.put(map.get("json" + 0));


				response=WsUtility.executePostHttps("https://apbocwwb.ap.nic.in/restfultest/doj/webservice/json/workordersdetails/"+selected_sp_dist_code+"/"+selected_spn_PlaceofAd_code+"/"+sharePref.getString("ad_agency_code","1")+"/"+selected_spn_WorkOrder+"/"+certificatedol+"/"+selected_spn_advt_category_code+"",
						arr.toString(), "GET");


			} catch (Exception e) {
				return null;
			} finally {
				progressDialog.dismiss();
			}
			return response;
		}

		@Override
		protected void onPostExecute(final String resp) {
			super.onPostExecute(resp);
			progressDialog.dismiss();
		}

	}

	/************* Insertion Restfull service******************************************/


	private SSLSocketFactory pinnedSSLSocketFactory()throws CertificateException, IOException, KeyStoreException, NoSuchAlgorithmException, KeyManagementException {
		CertificateFactory cf = CertificateFactory.getInstance("X.509");

		// Generate the certificate using the certificate file under res/raw/cert.cer
		InputStream caInput = new BufferedInputStream(getResources().openRawResource(R.raw.cer));
		Certificate ca = cf.generateCertificate(caInput);
		caInput.close();

		// Create a KeyStore containing our trusted CAs
		String keyStoreType = KeyStore.getDefaultType();
		KeyStore trusted = KeyStore.getInstance(keyStoreType);
		trusted.load(null, null);
		trusted.setCertificateEntry("ca", ca);

		// Create a TrustManager that trusts the CAs in our KeyStore
		String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
		TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
		tmf.init(trusted);

		// Create an SSLContext that uses our TrustManager
		SSLContext context = SSLContext.getInstance("TLS");
		context.init(null, tmf.getTrustManagers(), null);

		SSLSocketFactory sf = context.getSocketFactory();

		return sf;
	}




	private void callinsertionwS() {
		// TODO Auto-generated method stub
		try {
			String resoonce;
			resoonce = new InsertionInputs().execute().get();

			if (Utilities.showLogs == 0) {

				if (resoonce != null) {
					Log.i("resoonce", resoonce);

				}

			}

			try {

				JSONObject jobj3=new JSONObject(resoonce);
				String response=jobj3.getString("response");
				String responsecode=jobj3.getString("responsecode");

				if(responsecode.equalsIgnoreCase("01")) {


					Utilities.showAlertDialog(GetWorkDetails.this, "insertion", response, true);
				}
				else {

					Utilities.showAlertDialog(GetWorkDetails.this, "Insertion", response, true);

				}
			} catch (JSONException e) {
				if (Utilities.showLogs == 0) {
					e.printStackTrace();
				}
			}
			catch (Exception e) {
				if (Utilities.showLogs == 0) {
					e.printStackTrace();
				}
			}

		} catch (InterruptedException | ExecutionException e1) {
			if (Utilities.showLogs == 0) {
				e1.printStackTrace();
			}
		}

	}


	public byte[] convert(Bitmap bmp) {
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		bmp.compress(Bitmap.CompressFormat.JPEG, 100, stream);
		byte[] byteArray = stream.toByteArray();
		return byteArray;
	}
	public class InsertionInputs extends AsyncTask<Void, Void, String> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progressDialog = new ProgressDialog(GetWorkDetails.this);
			progressDialog.setMessage("Loading District data.\nPlease wait...");
			progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			progressDialog.setCancelable(false);
			progressDialog.show();
		}

		@Override
		protected String doInBackground(Void... params) {
			String response = null;
			try {

				JSONArray arr = new JSONArray();
				HashMap<String, JSONObject> map = new HashMap<String, JSONObject>();

				JSONObject jsonne = new JSONObject();

				jsonne.put("ad_code", adcode);
				jsonne.put("lat", Double.toString(latitude));
				jsonne.put("longitude", Double.toString(longitude));
				jsonne.put("imei", deviceIMEI);
				jsonne.put("userentered",userentered);
				jsonne.put("phototype", "JPEG");

				jsonne.put("photo",getBase64Str(capturedBitmap));
				//jsonne.put("photo",Base64.encode(getBytes(capturedBitmap)));
				jsonne.put("ad_work_order_serial_code",ad_work_order_serial_code);
				jsonne.put("location", str_edt_location);
				jsonne.put("panchayatcode", selected_sp_pan_code);
				jsonne.put("versionchk",versioncheck);
				jsonne.put("ad_work_order_no",selected_spn_WorkOrder);
				jsonne.put("ad_agency_code", sharePref.getString("ad_agency_code", "1"));
				jsonne.put("distcode", selected_sp_dist_code);
				jsonne.put("mandcode", selected_sp_mandal_code);
				jsonne.put("ad_catgory_code",selected_spn_advt_category_code);
				jsonne.put("ad_place_code", selected_spn_PlaceofAd_code);
				//	jsonne.put("photo",getBase64Str(capturedBitmap));

				Log.i("insertion elements are",arr.toString());

				map.put("json" + 0, jsonne);
				arr.put(map.get("json" + 0));
				Log.i("insertion elements are",arr.toString());
//				"+ad_code+"/"+Double.toString(latitude)+"/"+Double.toString(longitude)+"/"+deviceIMEI+"/"+userentered+"/"+.JPEG+"
//						/"+Base64.encode(getBytes(capturedBitmap))+"/"+ad_work_order_serial_code+"/"+str_edt_location+"/"+selected_sp_pan_code+"
//						/"+versioncheck+"/"+selected_spn_WorkOrder+"/"+ad_agency_code+"/"+selected_sp_dist_code+"/"+selected_sp_mandal_code+"/
//						"+selected_spn_PlaceofAd_code+"
				response=WsUtility.executePostHttps("https://apbocwwb.ap.nic.in/restfultest/doj/webservice/json/post/",arr.toString(), "POST");

				//		response=WsUtility.executePostHttps("https://apbocwwb.ap.nic.in/restfultest/doj/webservice/json/upload/"+ad_code.trim()+"/"+Double.toString(latitude).trim()+"/"+Double.toString(longitude).trim()+"/"+deviceIMEI.trim()+"/"+userentered.trim()+"/"+JPEG+"/"+Base64.encode(getBytes(capturedBitmap))+"/"+ad_work_order_serial_code.trim()+"/"+str_edt_location.trim()+"/"+selected_sp_pan_code.trim()+"/"+versioncheck+"/"+selected_spn_WorkOrder.trim()+"/"+ad_agency_code.trim()+"/"+selected_sp_dist_code.trim()+"/"+selected_sp_mandal_code.trim()+"/"+selected_spn_PlaceofAd_code.trim()+"","", "GET");


				//		response=WsUtility.executePostHttps("http://10.160.2.88:8080/sdnext/doj/webservice/json/upload/"+ad_code.trim()+"/"+Double.toString(latitude).trim()+"/"+Double.toString(longitude).trim()+"/"+deviceIMEI.trim()+"/"+userentered.trim()+"/"+JPEG+"/"+Base64.encode(getBytes(capturedBitmap)).trim()+"/"+ad_work_order_serial_code.trim()+"/"+str_edt_location.trim()+"/"+selected_sp_pan_code.trim()+"/"+versioncheck+"/"+selected_spn_WorkOrder.trim()+"/"+ad_agency_code.trim()+"/"+selected_sp_dist_code.trim()+"/"+selected_sp_mandal_code.trim()+"/"+selected_spn_PlaceofAd_code.trim()+"","", "POST");


				Log.i("response isss",response);
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			} finally {
				progressDialog.dismiss();
			}
			return response;
		}

		@Override
		protected void onPostExecute(final String resp) {
			super.onPostExecute(resp);
			progressDialog.dismiss();
		}

	}

	void insertion() {
//inset working as on 040518
		try {
			RequestQueue requestQueue = Volley.newRequestQueue(this);
			JSONObject params = new JSONObject();
//				jsonne.put("ad_code", ad_code);
//				jsonne.put("lat", Double.toString(latitude));
//				jsonne.put("longitude", Double.toString(longitude));
//				jsonne.put("imei", deviceIMEI);
//				jsonne.put("userentered",userentered);
//				jsonne.put("phototype", "JPEG");
//
//				jsonne.put("photo",getBase64Str(capturedBitmap));
//				//jsonne.put("photo",Base64.encode(getBytes(capturedBitmap)));
//				jsonne.put("ad_work_order_serial_code",ad_work_order_serial_code);
//				jsonne.put("location", str_edt_location);
//				jsonne.put("panchayatcode", selected_sp_pan_code);
//				jsonne.put("versionchk",versioncheck);
//				jsonne.put("ad_work_order_no",selected_spn_WorkOrder);
//				jsonne.put("ad_agency_code", sharePref.getString("ad_agency_code", ad_agency_code));
//				jsonne.put("distcode", selected_sp_dist_code);
//				jsonne.put("mandcode", selected_sp_mandal_code);
//				jsonne.put("ad_place_code",selected_spn_PlaceofAd_code);
			params.put("ad_category_code",selected_spn_advt_category_code);

			params.put("lat", Double.toString(latitude));

			params.put("longitude", Double.toString(longitude));

			params.put("imei", deviceIMEI);
			params.put("userentered",userentered);

			params.put("phototype", "JPEG");
			params.put("photo",getBase64Str(capturedBitmap));  //base64str
//			params.put("photoarray",getBytes(capturedBitmap));  //getbytes
//			params.put("photoarray",Base64.encode(getBytes(capturedBitmap)));   //byte arry format  working with base64format
			//params.put("photo",Base64.encodeToString(convert(capturedBitmap),Base64.NO_WRAP));
//			params.put("photo",capturedBitmap);
//			System.out.println("photo--->");
//			params.put("photo",getBytes(capturedBitmap));
//			params.put("photoarray",capturedBitmap.toString());
			//newly added swamy
			//--------------------------------
//			File f = new File(context.getCacheDir(), filename);
//			f.createNewFile();

//Convert bitmap to byte array
//			Bitmap bitmap =capturedBitmap;
//			ByteArrayOutputStream bos = new ByteArrayOutputStream();
//			bitmap.compress(CompressFormat.PNG, 0 /*ignored for PNG*/, bos);
//			byte[] bitmapdata = bos.toByteArray();
//			params.put("photofile",getBytes(capturedBitmap));
			//-------------
			params.put("ad_work_order_serial_code",ad_work_order_serial_code);

			params.put("location", str_edt_location);

			params.put("panchayatcode", selected_sp_pan_code);

			params.put("versionchk","1.3");
			params.put("ad_work_order_no",selected_spn_WorkOrder);
			params.put("ad_agency_code", adagencycode);
			params.put("distcode", selected_sp_dist_code);
			//Log.i("distcode", selected_sp_dist_code);
			params.put("mandcode", selected_sp_mandal_code);
			params.put("ad_category_code",selected_spn_advt_category_code);
			params.put("ad_place_code",selected_spn_PlaceofAd_code);
			params.put("certificate",certificate);





//
//				Log.i("ad_place_code",selected_spn_PlaceofAd_code);


			CustomRequest jsObjRequest = new CustomRequest(Request.Method.POST,
//					"https://apbocwwb.ap.nic.in/restfultest/doj/webservice/upload/", params,
//					"https://apbocwwb.ap.nic.in/restfultest/doj/webservice/postarry/", params,
// 					"https://apbocwwb.ap.nic.in/restfultest/doj/webservice/postfile/", params,
//                    "http://10.160.2.89:8080/RESTWebApp_1/doj/webservice/post/", params,
					"https://apbocwwb.ap.nic.in/restfultest/doj/webservice/post/", params,
					new Response.Listener<JSONArray>() {
						@Override
						public void onResponse(JSONArray response) {
							System.out.println(response.toString());

							try {
								// JSONArray jsonarray = new JSONArray(response);
								for (int i = 0; i < response.length(); i++) {
									JSONObject jsonobject = response.getJSONObject(i);
									String responses = jsonobject.getString("response");
									String responsecode = jsonobject.getString("responsecode");
									System.out.println(responses + "***" + responsecode);

									System.out.println("response is"+responses);

									System.out.println("userentered is" +userentered);

									//	System.out.println("photo is"+getBase64Str(capturedBitmap));
									//System.out.println("photo tpe is"+JPEG);
									System.out.println("versiioncheck is "+sharePref.getString("Version",""));
									System.out.println("ad_place_code is "+sharePref.getString("ad_place_code",""));
									//System.out.println();
									//System.out.println();

//										Log.i("ad_code", ad_code);
//										Log.i("lat", Double.toString(latitude));
//										Log.i("lat", Double.toString(longitude));
//										Log.i("userentered",sharePref.getString("userentered",""));
//										Log.i("photo","JPEG");
//										Log.i("photo",getBase64Str(capturedBitmap));
//										Log.i("ad_work_order__code",ad_work_order_serial_code);
//										Log.i("location",location);
//										Log.i("panchayatcode", selected_sp_pan_code);
//										Log.i("versionchk", versioncheck);
//										Log.i("ad_work_order_no",selected_spn_WorkOrder);
//										Log.i("distcode",selected_sp_dist_code);
//										Log.i("mandcode", selected_sp_mandal_code);
//										Log.i("ad_place_code",selected_spn_PlaceofAd_code);





									if(responsecode.equalsIgnoreCase("01"))
									{
										Utilities.showAlertDialog(GetWorkDetails.this,"insertion",responses,true);

										Intent i2=new Intent(GetWorkDetails.this,GetWorkDetails.class);
										startActivity(i2);
										finish();
									}
									else
									{
										Utilities.showAlertDialog(GetWorkDetails.this,"insertion",responses,true);
									}

								}
							} catch (JSONException e) {
								e.printStackTrace();
							}
						}
					},
					new Response.ErrorListener() {
						@Override
						public void onErrorResponse(VolleyError error) {
							error.printStackTrace();
						}
					}) {
				//@Override
				public Map<String, String> getHeaders() throws AuthFailureError {
					Map<String, String> params = new HashMap<String, String>();
					params.put("Content-Type", "application/json");
					return params;
				}

				//@Override
				public String getBodyContentType() {
					return "application/json; charset=utf-8";
				}
			};
//VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(jsObjRequest);
			requestQueue.add(jsObjRequest);
		} catch (Exception exception) {
			exception.printStackTrace();
		}


	}
	//@Override
	protected void onStop() {
		super.onStop();
		if (requestQueue != null) {
			requestQueue.cancelAll(REQUEST_TAG);
		}
	}


	public boolean onKeyDown(int iKeyCode, KeyEvent event) {

		if (iKeyCode == KeyEvent.KEYCODE_BACK
				|| iKeyCode == KeyEvent.KEYCODE_HOME) {

			final AlertDialog.Builder builder = new AlertDialog.Builder(
					GetWorkDetails.this);

			builder.setMessage("Are you sure to exit?")
					.setCancelable(false)
					.setPositiveButton("Yes",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
													int id) {

									finish();

								}
							})
					.setNegativeButton("No",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
													int id) {
									dialog.dismiss();
								}
							});
			final AlertDialog alert = builder.create();

			alert.show();

			return true;
		}
		return super.onKeyDown(iKeyCode, event);
	}
	public static byte[] getBytes(Bitmap bitmap) {
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		bitmap.compress(CompressFormat.JPEG, 70, stream);

		Log.i("IMage size", "" + stream.toByteArray().length);
		return stream.toByteArray();
	}
}

