package geotaglabour.nic.com.geotaglabour;

import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
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
import android.provider.MediaStore;
import android.provider.Settings;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import org.kobjects.base64.Base64;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.MarshalBase64;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import beans.GetAdvtAgencyDetailsBean;
import beans.GetWorkOrderDetailsBean;

public class GetWorkOrders extends Activity implements OnItemSelectedListener {
	Spinner spn_WorkOrder;
	ImageView imgWork;
	String selected_spn_WorkOrder = "", strad_code = "", ad_agency_code = "";
	int worko0rderPstn = 0;
	SharedPreferences sharePref;
	Context context;
	private ProgressDialog progressDialog;
	private static String SOAP_ACTION = null;
	private static String OPERATION_NAME = null;
	SoapObject soapRequest;
	SoapObject resultas;

	ArrayList<String> ad_codeList, ad_work_order_dateList, messageList;
	String deviceId, deviceIMEI;
	GPSTracker gps;
	private LocationManager locationManager;
	String bestProvider;
	double latitude = 0, longitude = 0;
	Bitmap capturedBitmap;
	private int year;
	private int month;
	private int day;
	File destination;
	private static final int TAKE_PICTURE = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		gps = new GPSTracker(GetWorkOrders.this);
		deviceId = Secure.getString(getContentResolver(), Secure.ANDROID_ID);

		intializeViews();

		TelephonyManager tManager = (TelephonyManager) getBaseContext()
				.getSystemService(Context.TELEPHONY_SERVICE);
		deviceIMEI = tManager.getDeviceId();
		context = GetWorkOrders.this.getApplicationContext();

		sharePref = getApplicationContext().getSharedPreferences("geoTag", 0);

		ad_agency_code = sharePref.getString("ad_agency_code", "0");
		destination = new File(Environment.getExternalStorageDirectory(),
				"image.jpg");
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

		final Calendar c = Calendar.getInstance();
		year = c.get(Calendar.YEAR);
		month = c.get(Calendar.MONTH);
		day = c.get(Calendar.DAY_OF_MONTH);

		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
		String dateTime = sdf.format(Calendar.getInstance().getTime());

		spn_WorkOrder = (Spinner) findViewById(R.id.spn_WorkOrder);
		spn_WorkOrder.setOnItemSelectedListener(this);

		imgWork = (ImageView) findViewById(R.id.imgWork);
		imgWork.setVisibility(View.GONE);

		isNetworkConnectedCallWS();

		latitude = gps.getLatitude();
		longitude = gps.getLongitude();

		// getGeocoderValues(latitude, longitude);

		// getCompleteAddressString(latitude, longitude);

	}

	ListView listview_work_details;

	TextView txt_ad_agency_name, txt_ad_authorised_email_id,
			txt_ad_authorised_mobile_no, txt_ad_authorised_person,
			title_agency_details;

	Button btn_quit;

	TableLayout hide_table_layout_benf_details;

	private void intializeViews() {

		listview_work_details = (ListView) findViewById(R.id.listview_work_details);

		txt_ad_agency_name = (TextView) findViewById(R.id.txt_ad_agency_name);
		txt_ad_authorised_email_id = (TextView) findViewById(R.id.txt_ad_authorised_email_id);
		txt_ad_authorised_mobile_no = (TextView) findViewById(R.id.txt_ad_authorised_mobile_no);
		txt_ad_authorised_person = (TextView) findViewById(R.id.txt_ad_authorised_person);

		btn_quit = (Button) findViewById(R.id.btn_quit);
		btn_quit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				final AlertDialog.Builder builder = new AlertDialog.Builder(
						GetWorkOrders.this);

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

		listview_work_details.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

			}
		});

		title_agency_details = (TextView) findViewById(R.id.title_agency_details);

		hide_table_layout_benf_details = (TableLayout) findViewById(R.id.hide_table_layout_benf_details);
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
	}

	int openCloseCounterValueBenfDetails = 1;
	Drawable drawbleRightImg;

	private boolean isNetworkConnectedCallWS() {
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo ni = cm.getActiveNetworkInfo();
		if (ni == null) {
			// // // Log.i("Internet Connection", "*********" + ni);

			Utilities.Error_Msg(GetWorkOrders.this, "No Internet Connection");
			return false;
		} else {
			// new GetWorkOrdersList().execute();

			new GetAdvtAgencyDetails().execute();
			return true;
		}

	}

	Object resultGetAdvtAgencyDetailsAsObj;

	List<GetAdvtAgencyDetailsBean> getAdvtAgencyDetailsBeanList;

	List<SoapObject> responsegetAdvtAgencyDetailsList;

	class GetAdvtAgencyDetails extends AsyncTask<String, String, String> {
		public void onPreExecute() {
			progressDialog = new ProgressDialog(GetWorkOrders.this);
			progressDialog.setMessage("Loading..");
			progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			progressDialog.setCancelable(false);
			progressDialog.show();
		}

		@Override
		protected String doInBackground(String... params) {
			try {

				String result_str;

				// SOAP_ACTION = "http://geotagging/GetWorkOrders";
				// OPERATION_NAME = "GetWorkOrders";

				// soapRequest = new SoapObject(Utilities.WSDL_TARGET_NAMESPACE,
				// OPERATION_NAME);

				SOAP_ACTION = Utilities.WSDL_TARGET_NAMESPACE
						+ Utilities.OPERATION_NAME_getadvtagencydetails;

				soapRequest = new SoapObject(Utilities.WSDL_TARGET_NAMESPACE,
						Utilities.OPERATION_NAME_getadvtagencydetails);

				soapRequest.addProperty("ad_agency_code", ad_agency_code);

				if (Utilities.showLogs == 0) {
					Log.i("request", soapRequest.toString());
				}

				SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
						SoapEnvelope.VER11);
				envelope.dotNet = true;

				new MarshalBase64().register(envelope);

				envelope.setOutputSoapObject(soapRequest);

				HttpTransportSE httpTransport = new HttpTransportSE(
						Utilities.urlGetAdvtAgencyDetails);

				httpTransport.call(SOAP_ACTION, envelope);
				// resultas = (SoapObject) envelope.getResponse();

				resultGetAdvtAgencyDetailsAsObj = envelope.bodyIn;

				if (Utilities.showLogs == 0) {
					Log.i("result*******", ""
							+ resultGetAdvtAgencyDetailsAsObj);
				}
				result_str = resultGetAdvtAgencyDetailsAsObj.toString();

				if (((SoapObject) resultGetAdvtAgencyDetailsAsObj)
						.getPropertyCount() > 0) {

					responsegetAdvtAgencyDetailsList = new ArrayList<SoapObject>();
					for (int i = 0; i < ((SoapObject) resultGetAdvtAgencyDetailsAsObj)
							.getPropertyCount(); i++) {

						SoapObject soapObject = (SoapObject) ((SoapObject) resultGetAdvtAgencyDetailsAsObj)
								.getProperty(i);
						responsegetAdvtAgencyDetailsList.add(soapObject);
					}

					if (Utilities.showLogs == 0) {
						Log.i("response", ""
								+ responsegetAdvtAgencyDetailsList);
					}

				}

			} catch (Exception exception) {

				exception.toString();

			} finally {
				progressDialog.dismiss();
			}
			return null;
		}

		public void onPostExecute(String params) {
			progressDialog.dismiss();
			try {

				if (((SoapObject) resultGetAdvtAgencyDetailsAsObj)
						.getPropertyCount() > 0) {

					if (responsegetAdvtAgencyDetailsList != null) {

						getAdvtAgencyDetailsBeanList = new ArrayList<GetAdvtAgencyDetailsBean>();

						for (int i = 0; i < responsegetAdvtAgencyDetailsList
								.size(); i++) {

							String ad_agency_name = responsegetAdvtAgencyDetailsList
									.get(i).getProperty("ad_agency_name")
									.toString().trim();

							if (ad_agency_name.equals("anyType{}")) {

								ad_agency_name = "";
							}
							if (Utilities.showLogs == 0) {
								Log.i("ad_agency_name*******", ""
										+ ad_agency_name);
							}

							String ad_authorised_email_id = responsegetAdvtAgencyDetailsList
									.get(i)
									.getProperty("ad_authorised_email_id")
									.toString().trim();

							if (ad_authorised_email_id.equals("anyType{}")) {

								ad_authorised_email_id = "";
							}
							if (Utilities.showLogs == 0) {
								Log.i("ad_email_id*******", ""
										+ ad_authorised_email_id);
							}

							String ad_authorised_mobile_no = responsegetAdvtAgencyDetailsList
									.get(i)
									.getProperty("ad_authorised_mobile_no")
									.toString().trim();
							if (ad_authorised_mobile_no.equals("anyType{}")) {
								ad_authorised_mobile_no = "";

							}
							if (Utilities.showLogs == 0) {
								Log.i("ad_mobile_no*******", ""
										+ ad_authorised_mobile_no);
							}

							String ad_authorised_person = responsegetAdvtAgencyDetailsList
									.get(i).getProperty("ad_authorised_person")
									.toString().trim();
							if (ad_authorised_mobile_no.equals("anyType{}")) {
								ad_authorised_mobile_no = "";

							}
							if (Utilities.showLogs == 0) {
								Log.i("ad_person*******", ""
										+ ad_authorised_person);
							}

							String message = responsegetAdvtAgencyDetailsList
									.get(i).getProperty("message").toString()
									.trim();
							if (message.equals("anyType{}")) {

								message = "";
							}
							if (Utilities.showLogs == 0) {
								Log.i("message*******", "" + message);
							}

							if (message.equalsIgnoreCase("Success")) {

								GetAdvtAgencyDetailsBean getWorkOrderDetailsBean = new GetAdvtAgencyDetailsBean(
										ad_agency_name, ad_authorised_email_id,
										ad_authorised_mobile_no,
										ad_authorised_person);

								getAdvtAgencyDetailsBeanList
										.add(getWorkOrderDetailsBean);

							}

						}

						if (getAdvtAgencyDetailsBeanList != null
								|| getAdvtAgencyDetailsBeanList.size() > 0) {

							txt_ad_agency_name
									.setText(getAdvtAgencyDetailsBeanList
											.get(0).getAd_agency_name());
							txt_ad_authorised_email_id
									.setText(getAdvtAgencyDetailsBeanList
											.get(0).getAd_authorised_email_id());
							txt_ad_authorised_mobile_no
									.setText(getAdvtAgencyDetailsBeanList
											.get(0)
											.getAd_authorised_mobile_no());
							txt_ad_authorised_person
									.setText(getAdvtAgencyDetailsBeanList
											.get(0).getAd_authorised_person());

							new GetWorkOrderMstList().execute();

						}

					} else {
						Toast.makeText(getApplicationContext(), "No records",
								Toast.LENGTH_SHORT).show();
					}
				} else {
					Utilities.Error_Msg(GetWorkOrders.this, "No Data found");
				}
			} catch (NullPointerException e) {

			}
		}
	}

	List<String> ad_work_order_noList;

	class GetWorkOrderMstList extends AsyncTask<String, String, String> {
		public void onPreExecute() {
			progressDialog = new ProgressDialog(GetWorkOrders.this);
			progressDialog.setMessage("Loading..");
			progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			progressDialog.setCancelable(false);
			progressDialog.show();
		}

		Object resultAsObj;

		List<SoapObject> responseWorkOrderList;

		@Override
		protected String doInBackground(String... params) {
			try {

				String result_str;

				// SOAP_ACTION = "http://geotagging/GetWorkOrders";
				// OPERATION_NAME = "GetWorkOrders";

				// soapRequest = new SoapObject(Utilities.WSDL_TARGET_NAMESPACE,
				// OPERATION_NAME);

				SOAP_ACTION = Utilities.WSDL_TARGET_NAMESPACE
						+ Utilities.OPERATION_NAME_getworkordermst;

				soapRequest = new SoapObject(Utilities.WSDL_TARGET_NAMESPACE,
						Utilities.OPERATION_NAME_getworkordermst);

				soapRequest.addProperty("ad_agency_code", ad_agency_code);

				if (Utilities.showLogs == 0) {
					Log.i("request", soapRequest.toString());
				}

				SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
						SoapEnvelope.VER11);
				envelope.dotNet = true;

				new MarshalBase64().register(envelope);

				envelope.setOutputSoapObject(soapRequest);

				HttpTransportSE httpTransport = new HttpTransportSE(
						Utilities.urlGetWorkOrderMst);

				httpTransport.call(SOAP_ACTION, envelope);
				// resultas = (SoapObject) envelope.getResponse();

				resultAsObj = envelope.bodyIn;

				if (Utilities.showLogs == 0) {
					Log.i("resultAsObj*******", "" + resultAsObj);
				}
				result_str = resultAsObj.toString();

				if (((SoapObject) resultAsObj).getPropertyCount() > 0) {

					responseWorkOrderList = new ArrayList<SoapObject>();
					for (int i = 0; i < ((SoapObject) resultAsObj)
							.getPropertyCount(); i++) {

						SoapObject soapObject = (SoapObject) ((SoapObject) resultAsObj)
								.getProperty(i);
						responseWorkOrderList.add(soapObject);
					}

					if (Utilities.showLogs == 0) {
						Log.i("response*******", ""
								+ responseWorkOrderList);
					}

				}

			} catch (Exception exception) {

				exception.toString();

			} finally {
				progressDialog.dismiss();
			}
			return null;
		}

		public void onPostExecute(String params) {
			progressDialog.dismiss();
			try {

				if (((SoapObject) resultAsObj).getPropertyCount() > 0) {

					if (responseWorkOrderList != null) {

						// // // Log.i("mother record count", "" +
						// response_list.size());

						ad_work_order_noList = new ArrayList<String>();
						ad_work_order_noList.add("Select work order");

						messageList = new ArrayList<String>();

						for (int i = 0; i < responseWorkOrderList.size(); i++) {

							String ad_work_order_no = responseWorkOrderList
									.get(i).getProperty("ad_work_order_no")
									.toString().trim();

							if (ad_work_order_no.equals("anyType{}")) {

								ad_work_order_no = "";
							}
							if (Utilities.showLogs == 0) {
								Log.i("ad_work_order_no*******", ""
										+ ad_work_order_no);
							}

							String message = responseWorkOrderList.get(i)
									.getProperty("message").toString().trim();
							if (message.equals("anyType{}")) {

								message = "";
							}
							if (Utilities.showLogs == 0) {
								Log.i("message*******", "" + message);
							}

							if (message.equalsIgnoreCase("Success")) {

								ad_work_order_noList.add(ad_work_order_no);
								messageList.add(message);

							}

						}

						if (Utilities.showLogs == 0) {
							Log.i("ad_noList*******", ""
									+ ad_work_order_noList);
						}

						Utilities.assignArrayAdpToSpin(GetWorkOrders.this,
								ad_work_order_noList, spn_WorkOrder);

						// ArrayAdapter<String> fcipointAdapter = new
						// ArrayAdapter<String>(
						// getApplicationContext(), R.layout.spinner_text,
						// ad_work_order_no_List);
						//
						// spn_WorkOrder.setAdapter(fcipointAdapter);
						// fcipointAdapter.notifyDataSetChanged();

					} else {
						Toast.makeText(getApplicationContext(), "No records",
								Toast.LENGTH_SHORT).show();
					}
				} else {
					Utilities.Error_Msg(GetWorkOrders.this, "No WorkOrders");
				}
			} catch (Exception e) {

				e.printStackTrace();

			}
		}
	}

	List<GetWorkOrderDetailsBean> getWorkOrderDetailsBeansList;

	class GetWorkOrdersList extends AsyncTask<String, String, String> {
		public void onPreExecute() {
			progressDialog = new ProgressDialog(GetWorkOrders.this);
			progressDialog.setMessage("Loading..");
			progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			progressDialog.setCancelable(false);
			progressDialog.show();
		}

		Object resultAsObj;
		List<SoapObject> responseWorkOrderList;

		@Override
		protected String doInBackground(String... params) {
			try {

				String result_str;

				// SOAP_ACTION = "http://geotagging/GetWorkOrders";
				// OPERATION_NAME = "GetWorkOrders";

				// soapRequest = new SoapObject(Utilities.WSDL_TARGET_NAMESPACE,
				// OPERATION_NAME);

				SOAP_ACTION = Utilities.WSDL_TARGET_NAMESPACE
						+ Utilities.OPERATION_NAME_getworkorderdetails;

				soapRequest = new SoapObject(Utilities.WSDL_TARGET_NAMESPACE,
						Utilities.OPERATION_NAME_getworkorderdetails);

				soapRequest.addProperty("ad_agency_code", ad_agency_code);

				soapRequest.addProperty("ad_work_order_no",
						selected_spn_WorkOrder);

				if (Utilities.showLogs == 0) {
					Log.i("request", soapRequest.toString());
				}

				SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
						SoapEnvelope.VER11);
				envelope.dotNet = true;

				new MarshalBase64().register(envelope);

				envelope.setOutputSoapObject(soapRequest);

				HttpTransportSE httpTransport = new HttpTransportSE(
						Utilities.urlGetWorkOrders);

				httpTransport.call(SOAP_ACTION, envelope);
				// resultas = (SoapObject) envelope.getResponse();

				resultAsObj = envelope.bodyIn;

				if (Utilities.showLogs == 0) {
					Log.i("resultAsObj*******", "" + resultAsObj);
				}
				result_str = resultAsObj.toString();

				if (((SoapObject) resultAsObj).getPropertyCount() > 0) {

					responseWorkOrderList = new ArrayList<SoapObject>();
					for (int i = 0; i < ((SoapObject) resultAsObj)
							.getPropertyCount(); i++) {

						SoapObject soapObject = (SoapObject) ((SoapObject) resultAsObj)
								.getProperty(i);
						responseWorkOrderList.add(soapObject);
					}

					if (Utilities.showLogs == 0) {
						Log.i("response*******", ""
								+ responseWorkOrderList);
					}

				}

			} catch (Exception exception) {

				exception.toString();

			} finally {
				progressDialog.dismiss();
			}
			return null;
		}

		public void onPostExecute(String params) {
			progressDialog.dismiss();
			try {

				if (((SoapObject) resultAsObj).getPropertyCount() > 0) {

					if (responseWorkOrderList != null) {

						// // // Log.i("mother record count", "" +
						// response_list.size());

						ad_codeList = new ArrayList<String>();
						ad_work_order_dateList = new ArrayList<String>();
						messageList = new ArrayList<String>();
						ad_work_order_serial_codeList = new ArrayList<String>();

						getWorkOrderDetailsBeansList = new ArrayList<GetWorkOrderDetailsBean>();

						for (int i = 0; i < responseWorkOrderList.size(); i++) {

							String ad_code = responseWorkOrderList.get(i)
									.getProperty("ad_code").toString().trim();

							if (ad_code.equals("anyType{}")) {

								ad_code = "";
							}
							if (Utilities.showLogs == 0) {
								Log.i("ad_code*******", "" + ad_code);
							}

							String ad_work_order_date = responseWorkOrderList
									.get(i).getProperty("ad_work_order_date")
									.toString().trim();

							if (ad_work_order_date.equals("anyType{}")) {

								ad_work_order_date = "";
							}
							if (Utilities.showLogs == 0) {
								Log.i("ad_date*******", ""
										+ ad_work_order_date);
							}

							// String ad_work_order_no = responseWorkOrderList
							// .get(i).getProperty("ad_work_order_no")
							// .toString().trim();
							// if (ad_work_order_no.equals("anyType{}")) {
							// ad_work_order_no = "";
							//
							// }
							// if (Utilities.showLogs == 0) {
							// Log.i("ad_work_order_no*******", ""
							// + ad_work_order_no);
							// }

							String ad_work_order_serial_code = responseWorkOrderList
									.get(i)
									.getProperty("ad_work_order_serial_code")
									.toString().trim();
							if (ad_work_order_serial_code.equals("anyType{}")) {
								ad_work_order_serial_code = "";

							}
							if (Utilities.showLogs == 0) {
								Log.i("ad_serial_code*******", ""
										+ ad_work_order_serial_code);
							}

							String distcode = responseWorkOrderList.get(i)
									.getProperty("distcode").toString().trim();
							if (distcode.equals("anyType{}")) {
								distcode = "";

							}
							if (Utilities.showLogs == 0) {
								Log.i("distcode*******", "" + distcode);
							}

							String distname = responseWorkOrderList.get(i)
									.getProperty("distname").toString().trim();
							if (distname.equals("anyType{}")) {
								distname = "";

							}
							if (Utilities.showLogs == 0) {
								Log.i("distname*******", "" + distname);
							}

							String message = responseWorkOrderList.get(i)
									.getProperty("message").toString().trim();
							if (message.equals("anyType{}")) {

								message = "";
							}
							if (Utilities.showLogs == 0) {
								Log.i("message*******", "" + message);
							}

							if (message.equalsIgnoreCase("Success")) {

								// if (i == 0) {
								// ad_codeList.add("0");
								// ad_work_order_dateList.add("0");
								// ad_work_order_no_List
								// .add("Select work order no");
								// ad_work_order_serial_codeList.add("0");
								// }

								ad_codeList.add(ad_code);
								ad_work_order_dateList.add(ad_work_order_date);
								// ad_work_order_no_List.add(ad_work_order_no);
								messageList.add(message);
								ad_work_order_serial_codeList
										.add(ad_work_order_serial_code);

								// for (int j = 0; j < 5; j++) {
								GetWorkOrderDetailsBean getWorkOrderDetailsBean = new GetWorkOrderDetailsBean(
										ad_code, ad_work_order_date,
										ad_work_order_serial_code, distcode,
										distname);

								getWorkOrderDetailsBeansList
										.add(getWorkOrderDetailsBean);
								// }

							}

						}

						if (getWorkOrderDetailsBeansList != null
								|| getWorkOrderDetailsBeansList.size() > 0) {

							dataAdapter = new WorkOrderDataCustomAdapter(
									GetWorkOrders.this,
									R.layout.work_order_details_listview_items,
									getWorkOrderDetailsBeansList);

							// callListviewFilterRadio(text);
							listview_work_details.setAdapter(dataAdapter);
							dataAdapter.notifyDataSetChanged();

						}

						// Utilities.assignArrayAdpToSpin(GetWorkOrders.this,
						// ad_work_order_no_List, spn_WorkOrder);

						// new GetDistrictWS().execute();

						// ArrayAdapter<String> fcipointAdapter = new
						// ArrayAdapter<String>(
						// getApplicationContext(), R.layout.spinner_text,
						// ad_work_order_no_List);
						//
						// spn_WorkOrder.setAdapter(fcipointAdapter);
						// fcipointAdapter.notifyDataSetChanged();

					} else {
						Toast.makeText(getApplicationContext(), "No records",
								Toast.LENGTH_SHORT).show();
					}
				} else {
					Utilities.Error_Msg(GetWorkOrders.this, "No WorkOrders");
				}
			} catch (Exception e) {

				e.printStackTrace();
			}
		}
	}

	WorkOrderDataCustomAdapter dataAdapter;

	String selected_ad_work_order_serial_code = "",
			listView_selected_dist_codeStr = "",
			listView_selected_dist_NameStr = "",
			listView_selected_work_order_dateStr = "";

	List<String> ad_work_order_serial_codeList;

	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {
		// TODO Auto-generated method stub
		if (arg0 == spn_WorkOrder) {

			worko0rderPstn = arg2;

			if (arg2 > 0) {

				selected_spn_WorkOrder = ad_work_order_noList.get(arg2)
						.toString();

				new GetWorkOrdersList().execute();

				// selected_ad_work_order_serial_code =
				// ad_work_order_serial_codeList
				// .get(arg2).toString();
				//
				// strad_code = ad_codeList.get(arg2).toString();

				// latitude = gps.getLatitude();
				// longitude = gps.getLongitude();
				// onTakePic();

			}

		}
	}

	ImageView selectedView;

	private void onTakePic() {

		// selectedView = imageView;

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

				submitWorkOrderDetails();

				// new InsertPhotoGeoTag().execute();

			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {

			// startActivity(new Intent(GetWorkOrders.this,
			// GetWorkOrders.class));
			// finish();
		}

	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub

	}

	Object resultAsObjInsert;
	String resultAsStr = "";

	class InsertPhotoGeoTag extends AsyncTask<String, String, String> {
		public void onPreExecute() {
			progressDialog = new ProgressDialog(GetWorkOrders.this);
			progressDialog.setMessage("Loading..");
			progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			progressDialog.setCancelable(false);
			progressDialog.show();
		}

		@Override
		protected String doInBackground(String... params) {
			try {

				// SOAP_ACTION = "http://geotagging/insert_photogeotag";
				// OPERATION_NAME = "insert_photogeotag";

				// soapRequest = new SoapObject(Utilities.WSDL_TARGET_NAMESPACE,
				// OPERATION_NAME);

				SOAP_ACTION = Utilities.WSDL_TARGET_NAMESPACE
						+ Utilities.OPERATION_NAME_insert_photogeotag;

				soapRequest = new SoapObject(Utilities.WSDL_TARGET_NAMESPACE,
						Utilities.OPERATION_NAME_insert_photogeotag);

				soapRequest.addProperty("ad_agency_code",
						sharePref.getString("ad_agency_code", ""));
				soapRequest.addProperty("ad_work_order_no",
						selected_spn_WorkOrder);

				soapRequest.addProperty("ad_code", strad_code);

				soapRequest.addProperty("lat", Double.toString(latitude));

				soapRequest
						.addProperty("longitude", Double.toString(longitude));

				soapRequest.addProperty("imei", deviceIMEI);

				soapRequest.addProperty("userentered",
						sharePref.getString("userentered", ""));

				soapRequest.addProperty("phototype", ".JPEG");
				soapRequest.addProperty("photo", getBase64Str(capturedBitmap));
				soapRequest.addProperty("ad_work_order_serial_code",
						selected_ad_work_order_serial_code);
				soapRequest.addProperty("location", edtLocationStr);
				soapRequest.addProperty("distcode",
						listView_selected_dist_codeStr);
				soapRequest.addProperty("mandcode", selected_spi_mandalCode);

				if (Utilities.showLogs == 0) {
					Log.i("request", soapRequest.toString());
				}

				SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
						SoapEnvelope.VER11);
				envelope.dotNet = true;
				new MarshalBase64().register(envelope);

				envelope.setOutputSoapObject(soapRequest);

				HttpTransportSE httpTransport = new HttpTransportSE(
						Utilities.urlInsertGeoTagging);
				httpTransport.call(SOAP_ACTION, envelope);

				httpTransport.debug = true;

				SoapPrimitive resultsRequestSOAP = (SoapPrimitive) envelope
						.getResponse();

				resultAsStr = resultsRequestSOAP.toString().trim();

				// resultAsObjInsert = envelope.bodyIn;
				//
				// resultAsStr = resultAsObjInsert.toString();

				if (Utilities.showLogs == 0) {
					// Log.i("resultas*******", "" +
					// resultAsObjInsert.toString());
					Log.i("resultas*******", "" + resultAsStr);

					Log.i("responseDump*******", ""
							+ httpTransport.responseDump);

				}

			} catch (Exception exception) {

				exception.toString();

			} finally {
				progressDialog.dismiss();
			}
			return null;
		}

		public void onPostExecute(String params) {
			try {

				// Utilities.Error_Msg(GetWorkOrders.this, resultas.toString()
				// .trim());

				// final String msg = resultAsObjInsert.toString().trim();
				final String msg = resultAsStr;

				if (Utilities.showLogs == 0) {
					// Log.i("resultas*******", "" +
					// resultAsObjInsert.toString());
					Log.i("msg*******", "" + msg);
				}

				if (msg.length() > 0) {
					final AlertDialog.Builder builder = new AlertDialog.Builder(
							GetWorkOrders.this);
					builder.setTitle("Info");
					builder.setIcon(R.drawable.success);
					builder.setMessage(msg)
							.setCancelable(false)
							.setPositiveButton("Ok",
									new DialogInterface.OnClickListener() {
										public void onClick(
												DialogInterface dialog, int id) {

											if (msg.equalsIgnoreCase("Details Not Inserted")) {
												dialog.dismiss();
											} else {

												startActivity(new Intent(
														GetWorkOrders.this,
														GetWorkOrders.class));
												finish();
											}

										}
									});
					final AlertDialog alert = builder.create();

					alert.show();
				} else {

					final AlertDialog.Builder builder = new AlertDialog.Builder(
							GetWorkOrders.this);
					builder.setTitle("Info");
					builder.setIcon(R.drawable.success);
					builder.setMessage("Something went wrong...")
							.setCancelable(false)
							.setPositiveButton("Ok",
									new DialogInterface.OnClickListener() {
										public void onClick(
												DialogInterface dialog, int id) {

											dialog.dismiss();

										}
									});
					final AlertDialog alert = builder.create();

					alert.show();
				}

			} catch (Exception e) {

				// //Log.i("request result @@ post", e.toString());
				Toast.makeText(getApplicationContext(),
						"Network Problem Please Try Again...",
						Toast.LENGTH_SHORT).show();

			}
		}
	}

	int selected_item_ptn = 0;
	String edtLocationStr = "";

	int spi_districtPtn = 0, spi_mandalPtn = 0;
	String selected_spi_districtCode = "", selected_spi_mandalCode = "";
	Spinner spi_district, spi_mandal;

	public void submitWorkOrderDetails() {

		final Dialog dialog = new Dialog(GetWorkOrders.this);
		// dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setCancelable(false);
		dialog.setContentView(R.layout.custom_dialog);
		dialog.setTitle("Info (Work Order)");

		Window window = dialog.getWindow();
		window.setLayout(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);

		TextView txt_ad_agency_name1, txt_ad_authorised_email_id1, txt_ad_authorised_mobile_no1, txt_ad_authorised_person1;

		txt_ad_agency_name1 = (TextView) dialog
				.findViewById(R.id.txt_ad_agency_name1);
		txt_ad_authorised_email_id1 = (TextView) dialog
				.findViewById(R.id.txt_ad_authorised_email_id1);
		txt_ad_authorised_mobile_no1 = (TextView) dialog
				.findViewById(R.id.txt_ad_authorised_mobile_no1);
		txt_ad_authorised_person1 = (TextView) dialog
				.findViewById(R.id.txt_ad_authorised_person1);

		// if (getAdvtAgencyDetailsBeanList.size() > 0) {
		txt_ad_agency_name1.setText(txt_ad_agency_name.getText().toString()
				.trim());
		txt_ad_authorised_email_id1.setText(txt_ad_authorised_email_id
				.getText().toString().trim());
		txt_ad_authorised_mobile_no1.setText(txt_ad_authorised_mobile_no
				.getText().toString().trim());
		txt_ad_authorised_person1.setText(txt_ad_authorised_person.getText()
				.toString().trim());
		// }

		TextView txt_work_order_date1, txt_work_order_no1, txt_dist_work_order1;

		txt_work_order_date1 = (TextView) dialog
				.findViewById(R.id.txt_work_order_date1);

		// txt_work_order_no1 = (TextView) dialog
		// .findViewById(R.id.txt_work_order_no1);

		txt_dist_work_order1 = (TextView) dialog
				.findViewById(R.id.txt_dist_work_order);

		ImageView imgWorkAlert = (ImageView) dialog.findViewById(R.id.imgWork1);

		final EditText edt_location = (EditText) dialog
				.findViewById(R.id.edt_location);

		latitude = gps.getLatitude();
		longitude = gps.getLongitude();
		
		
		Handler mHandler1 = new Handler();
		mHandler1.postDelayed(new Runnable() {

			@Override
			public void run() {
				edt_location.setText(getCompleteAddressString(latitude, longitude));

				if (edt_location.getText().toString().trim().length() > 0) {

					// edt_location.setEnabled(false);
					edt_location.setClickable(false);
					edt_location.setFocusable(false);

				}
			}

		}, 2000);

		

		// if (getWorkOrderDetailsBeansList1.size() > 0) {
		txt_work_order_date1.setText(listView_selected_work_order_dateStr);
		// txt_work_order_no1.setText(getWorkOrderDetailsBeansList1.get(
		// selected_item_ptn).getAd_work_order_no());

		txt_dist_work_order1.setText(listView_selected_dist_NameStr);
		// }

		if (capturedBitmap != null) {
			imgWorkAlert.setImageBitmap(capturedBitmap);
		}

		spi_district = (Spinner) dialog.findViewById(R.id.spi_district);
		spi_mandal = (Spinner) dialog.findViewById(R.id.spi_mandal);

		// Utilities.assignArrayAdpToSpin(GetWorkOrders.this, distNameList,
		// spi_district);

		spi_district.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				spi_districtPtn = position;
				if (spi_districtPtn > 0) {

					selected_spi_districtCode = distCodeList.get(position);

					new GetMandalWS().execute();

				} else {
					spi_districtPtn = 0;
				}

			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {

			}
		});

		spi_mandal.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				spi_mandalPtn = position;
				if (spi_mandalPtn > 0) {

					selected_spi_mandalCode = mandalCodeList.get(position);

				} else {
					spi_mandalPtn = 0;
				}

			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {

			}
		});

		Handler mHandler = new Handler();
		mHandler.postDelayed(new Runnable() {

			@Override
			public void run() {
				new GetMandalWS().execute();
			}

		}, 1000);

		Button btn_use_submit = (Button) dialog.findViewById(R.id.btn_submit);

		Button btn_cancel = (Button) dialog.findViewById(R.id.btn_cancel);

		btn_cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});

		btn_use_submit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				// if (spi_districtPtn == 0) {
				// Utilities.Error_Msg(GetWorkOrders.this,
				// "Please Select District");
				//
				// } else

				if (edt_location.getText().toString().trim().length() == 0) {

					Utilities.Error_Msg(GetWorkOrders.this,
							"Please enter location");

					edt_location.setClickable(true);
					edt_location.setFocusable(true);

				} else if (spi_mandalPtn == 0) {
					Utilities.Error_Msg(GetWorkOrders.this,
							"Please Select Mandal");
				} else {

					edtLocationStr = edt_location.getText().toString().trim();

					dialog.dismiss();

					new InsertPhotoGeoTag().execute();

				}

			}

		});

		dialog.show();

	}

	public static String getBase64Str(Bitmap bitmap) {

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.JPEG, 70, baos);
		byte[] data = baos.toByteArray();

		String strBase64 = Base64.encode(data);

		return strBase64;
	}

	Boolean checkBoxFlag;

	String mgsChildDataStr = "";

	String childDOBStr = "";

	// private class WorkOrderDataCustomAdapter extends
	// ArrayAdapter<GetWorkOrderDetailsBean> {
	private class WorkOrderDataCustomAdapter extends
			ArrayAdapter<GetWorkOrderDetailsBean> {

		private ArrayList<GetWorkOrderDetailsBean> countryList;
		private List<GetWorkOrderDetailsBean> worldpopulationlist = null;

		ArrayList<GetWorkOrderDetailsBean> forCheckBoxList;

		Typeface face;

		Integer selected_position = 0;

		boolean array[];

		private RadioButton listRadioButton = null;
		int listIndex = -1;

		private int mSelectedPosition = -1;
		private RadioButton mSelectedRB;
		private String mUserApllication = "";
		private List<GetWorkOrderDetailsBean> mList;

		public WorkOrderDataCustomAdapter(Context context,
				int textViewResourceId,
				List<GetWorkOrderDetailsBean> countryList) {
			super(context, textViewResourceId, countryList);
			this.worldpopulationlist = countryList;
			this.countryList = new ArrayList<GetWorkOrderDetailsBean>();
			this.countryList.addAll(countryList);

			// for check boxes
			array = new boolean[countryList.size()];

			mList = countryList;

			mUserApllication = Settings.System.getString(
					context.getContentResolver(),
					Settings.System.PARENTAL_CONTROL_ENABLED);

		}

		private class ViewHolder {
			CheckBox childDataSelectionChxbox;
			TextView txt_work_order_date;
			TextView txt_work_order_no, txt_dist_work_order;
			TextView txt_work_order_no_serial_no;

			ImageView imgWork;

			TableRow hide_row_work_order_no_serial;

			RadioButton radio_btn_child_data_select;
		}

		@Override
		public int getCount() {
			return worldpopulationlist.size();
		}

		@Override
		public GetWorkOrderDetailsBean getItem(int position) {
			return worldpopulationlist.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {

			ViewHolder holder = null;
			Log.v("ConvertView", String.valueOf(position));

			if (convertView == null) {
				LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				convertView = vi.inflate(
						R.layout.work_order_details_listview_items, null);

				holder = new ViewHolder();

				holder.hide_row_work_order_no_serial = (TableRow) convertView
						.findViewById(R.id.hide_row_work_order_no_serial);

				holder.hide_row_work_order_no_serial.setVisibility(View.GONE);

				holder.radio_btn_child_data_select = (RadioButton) convertView
						.findViewById(R.id.radio_btn_extent);

				holder.childDataSelectionChxbox = (CheckBox) convertView
						.findViewById(R.id.chxbox_select_child);

				holder.txt_work_order_date = (TextView) convertView
						.findViewById(R.id.txt_work_order_date);

				holder.txt_work_order_date = (TextView) convertView
						.findViewById(R.id.txt_work_order_date);

				holder.txt_dist_work_order = (TextView) convertView
						.findViewById(R.id.txt_dist_work_order);

				holder.txt_work_order_no_serial_no = (TextView) convertView
						.findViewById(R.id.txt_work_order_no_serial_no);

				// holder.txt_work_order_no.setVisibility(View.GONE);
				// holder.txt_work_order_no_serial_no.setVisibility(View.GONE);

				holder.imgWork = (ImageView) convertView
						.findViewById(R.id.imgWork);

				convertView.setTag(holder);

			} else {
				holder = (ViewHolder) convertView.getTag();
				// holder.childDataSelectionChxbox.setTag(worldpopulationlist
				// .get(position));

				holder.radio_btn_child_data_select.setTag(worldpopulationlist
						.get(position));
			}

			selected_position = position;

			// holder.radio_btn_child_data_select.setChecked(array[position]);
			holder.radio_btn_child_data_select
					.setOnClickListener(new OnClickListener() {
						public void onClick(View v) {

							// if(position != selected_position &&
							// listRadioButton != null){
							// listRadioButton.setChecked(false);
							// }
							//
							//
							//
							// selected_position = position;
							// listRadioButton = (RadioButton)v;

							// for (int i = 0; i < array.length; i++) {
							// if (i == position) {
							// array[i] = true;
							// } else {
							// array[i] = false;
							// }
							// }
							// notifyDataSetChanged();

							// if(position != mSelectedPosition && mSelectedRB
							// != null){
							// mSelectedRB.setChecked(false);
							// }
							// mUserApllication ="";
							// mSelectedPosition = position;
							// mSelectedRB = (RadioButton) v;

							View vMain = ((View) v.getParent());
							// getParent() must be added 'n' times,
							// where 'n' is the number of RadioButtons'
							// nested parents
							// in your case is one.

							// uncheck previous checked button.
							if (listRadioButton != null) {
								listRadioButton.setChecked(false);
							}

							// assign to the variable the new one
							listRadioButton = (RadioButton) v;

							// find if the new one is checked or not, and
							// set "listIndex"
							if (listRadioButton.isChecked()) {

								listIndex = ((ViewGroup) vMain.getParent())
										.indexOfChild(vMain);

								Log.i("listIndex: ", "" + listIndex);

								strad_code = getWorkOrderDetailsBeansList.get(
										position).getAd_code();

								selected_ad_work_order_serial_code = getWorkOrderDetailsBeansList
										.get(position)
										.getAd_work_order_serial_code();

								listView_selected_dist_codeStr = getWorkOrderDetailsBeansList
										.get(position).getDistcode();

								listView_selected_dist_NameStr = getWorkOrderDetailsBeansList
										.get(position).getDistname();

								listView_selected_work_order_dateStr = getWorkOrderDetailsBeansList
										.get(position).getAd_work_order_date();

								selected_item_ptn = position;

								latitude = gps.getLatitude();
								longitude = gps.getLongitude();

								// Toast.makeText(
								// GetWorkOrders.this,
								// "selected_item_ptn" + selected_item_ptn,
								// 300).show();

								onTakePic();

							} else {
								listRadioButton = null;
								listIndex = -1;
							}

						}
					});

			// String userApp = mList.get(position).packageName;
			// if(mUserApllication.equals(userApp)) {
			// mSelectedPosition = position;
			// }

			// if (mSelectedPosition != position) {
			// holder.radio_btn_child_data_select.setChecked(false);
			// } else {
			// holder.radio_btn_child_data_select.setChecked(true);
			// mSelectedRB = holder.radio_btn_child_data_select;
			// }

			// if(selected_position != position){
			// holder.radio_btn_child_data_select.setChecked(false);
			//
			// }else{
			// holder.radio_btn_child_data_select.setChecked(true);
			//
			// strad_code = getWorkOrderDetailsBeansList.get(
			// selected_position).getAd_code();
			// strad_work_order_no = getWorkOrderDetailsBeansList
			// .get(selected_position)
			// .getAd_work_order_no();
			// selected_ad_work_order_serial_code = getWorkOrderDetailsBeansList
			// .get(selected_position)
			// .getAd_work_order_serial_code();
			//
			// selected_item_ptn = selected_position;
			//
			// latitude = gps.getLatitude();
			// longitude = gps.getLongitude();
			//
			// // onTakePic();
			//
			// if(listRadioButton != null && holder.radio_btn_child_data_select
			// != listRadioButton){
			// listRadioButton = holder.radio_btn_child_data_select;
			// }
			// }

			GetWorkOrderDetailsBean country = getItem(position);

			try {
				// holder.attendence.setChecked(country.isSelected());
				holder.txt_work_order_date.setText(country
						.getAd_work_order_date());

				holder.txt_dist_work_order.setText(country.getDistname());

				// holder.txt_work_order_no.setText(country.getAd_work_order_no());

				// holder.txt_work_order_no_serial_no.setText(country
				// .getAd_work_order_serial_code());
				// holder.childName.setText(country.getName_of_the_Child());
				//
				// String childDobStr = country.getDate_of_Birth();
				// if (childDobStr.length() != 0) {
				// holder.childDob.setText(childDobStr.substring(0, 10));
				// } else {
				// holder.childDob.setText("Not found");
				// }
				//
				// holder.childGender.setText(country.getGender());
				//
				// String childRegDateStr = country
				// .getRegistration_Date_with_AWC();
				// if (childRegDateStr.length() != 0) {
				// holder.childRegDate.setText(childRegDateStr
				// .substring(0, 10));
				// } else {
				// holder.childRegDate.setText("Not found");
				// }
				// holder.childRegDate.setText(childRegDateStr);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return convertView;

		}

	}

	ArrayList<String> distCodeList, distNameList;

	class GetDistrictWS extends AsyncTask<String, String, String> {
		public void onPreExecute() {
			progressDialog = new ProgressDialog(GetWorkOrders.this);
			progressDialog.setMessage("Loading Districts data...");
			progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			progressDialog.setCancelable(false);
			progressDialog.show();
		}

		Object resultAsObj;

		List<SoapObject> responseList;

		@Override
		protected String doInBackground(String... params) {
			try {

				String result_str;

				// SOAP_ACTION = "http://geotagging/GetWorkOrders";
				// OPERATION_NAME = "GetWorkOrders";

				// soapRequest = new SoapObject(Utilities.WSDL_TARGET_NAMESPACE,
				// OPERATION_NAME);

				SOAP_ACTION = Utilities.WSDL_TARGET_NAMESPACE
						+ Utilities.OPERATION_NAME_getdistdetails;

				soapRequest = new SoapObject(Utilities.WSDL_TARGET_NAMESPACE,
						Utilities.OPERATION_NAME_getdistdetails);

				soapRequest.addProperty("testid", "0");

				if (Utilities.showLogs == 0) {
					Log.i("request", soapRequest.toString());
				}

				SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
						SoapEnvelope.VER11);
				envelope.dotNet = true;

				new MarshalBase64().register(envelope);

				envelope.setOutputSoapObject(soapRequest);

				HttpTransportSE httpTransport = new HttpTransportSE(
						Utilities.urlGetDistricts);

				httpTransport.call(SOAP_ACTION, envelope);
				// resultas = (SoapObject) envelope.getResponse();

				resultAsObj = envelope.bodyIn;

				if (Utilities.showLogs == 0) {
					Log.i("resultAsObj*******", "" + resultAsObj);
				}
				result_str = resultAsObj.toString();

				if (((SoapObject) resultAsObj).getPropertyCount() > 0) {

					responseList = new ArrayList<SoapObject>();
					for (int i = 0; i < ((SoapObject) resultAsObj)
							.getPropertyCount(); i++) {

						SoapObject soapObject = (SoapObject) ((SoapObject) resultAsObj)
								.getProperty(i);
						responseList.add(soapObject);
					}

					if (Utilities.showLogs == 0) {
						Log.i("responseList*******", "" + responseList);
					}

				}

			} catch (Exception exception) {

				exception.toString();

			} finally {
				progressDialog.dismiss();
			}
			return null;
		}

		public void onPostExecute(String params) {
			progressDialog.dismiss();
			try {

				if (((SoapObject) resultAsObj).getPropertyCount() > 0) {

					if (responseList != null) {

						distCodeList = new ArrayList<String>();
						distNameList = new ArrayList<String>();

						distCodeList.add("0");
						distNameList.add("Select District");

						for (int i = 0; i < responseList.size(); i++) {

							String distcodeStr = responseList.get(i)
									.getProperty("distcode").toString().trim();

							if (distcodeStr.equals("anyType{}")) {

								distcodeStr = "";
							}
							if (Utilities.showLogs == 0) {
								Log.i("distcode*******", "" + distcodeStr);
							}

							String distnameStr = responseList.get(i)
									.getProperty("distname").toString().trim();

							if (distnameStr.equals("anyType{}")) {

								distnameStr = "";
							}
							if (Utilities.showLogs == 0) {
								Log.i("distname*******", "" + distnameStr);
							}

							distCodeList.add(distcodeStr);
							distNameList.add(distnameStr);

						}

						// new GetMandalWS().execute();

					} else {
						Toast.makeText(getApplicationContext(), "No records",
								Toast.LENGTH_SHORT).show();
					}
				} else {
					Utilities.Error_Msg(GetWorkOrders.this, "No Data found");
				}
			} catch (NullPointerException e) {

			}
		}
	}

	ArrayList<String> mandalCodeList, mandalNameList;

	class GetMandalWS extends AsyncTask<String, String, String> {
		public void onPreExecute() {
			progressDialog = new ProgressDialog(GetWorkOrders.this);
			progressDialog.setMessage("Loading Mandal data...");
			progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			progressDialog.setCancelable(false);
			progressDialog.show();
		}

		Object resultAsObj;

		List<SoapObject> responseList;

		@Override
		protected String doInBackground(String... params) {
			try {

				String result_str;

				// SOAP_ACTION = "http://geotagging/GetWorkOrders";
				// OPERATION_NAME = "GetWorkOrders";

				// soapRequest = new SoapObject(Utilities.WSDL_TARGET_NAMESPACE,
				// OPERATION_NAME);

				SOAP_ACTION = Utilities.WSDL_TARGET_NAMESPACE
						+ Utilities.OPERATION_NAME_getmanddetails;

				soapRequest = new SoapObject(Utilities.WSDL_TARGET_NAMESPACE,
						Utilities.OPERATION_NAME_getmanddetails);

				// soapRequest.addProperty("distcode",
				// selected_spi_districtCode);
				soapRequest.addProperty("distcode",
						listView_selected_dist_codeStr);

				if (Utilities.showLogs == 0) {
					Log.i("request", soapRequest.toString());
				}

				SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
						SoapEnvelope.VER11);
				envelope.dotNet = true;

				new MarshalBase64().register(envelope);

				envelope.setOutputSoapObject(soapRequest);

				HttpTransportSE httpTransport = new HttpTransportSE(
						Utilities.urlGetMandals);

				httpTransport.call(SOAP_ACTION, envelope);
				// resultas = (SoapObject) envelope.getResponse();

				resultAsObj = envelope.bodyIn;

				if (Utilities.showLogs == 0) {
					Log.i("resultAsObj*******", "" + resultAsObj);
				}
				result_str = resultAsObj.toString();

				if (((SoapObject) resultAsObj).getPropertyCount() > 0) {

					responseList = new ArrayList<SoapObject>();
					for (int i = 0; i < ((SoapObject) resultAsObj)
							.getPropertyCount(); i++) {

						SoapObject soapObject = (SoapObject) ((SoapObject) resultAsObj)
								.getProperty(i);
						responseList.add(soapObject);
					}

					if (Utilities.showLogs == 0) {
						Log.i("responseList*******", "" + responseList);
					}

				}

			} catch (Exception exception) {

				exception.toString();

			} finally {
				progressDialog.dismiss();
			}
			return null;
		}

		public void onPostExecute(String params) {
			progressDialog.dismiss();
			try {

				if (((SoapObject) resultAsObj).getPropertyCount() > 0) {

					if (responseList != null) {

						mandalCodeList = new ArrayList<String>();
						mandalNameList = new ArrayList<String>();

						mandalCodeList.add("0");
						mandalNameList.add("Select Mandal");

						for (int i = 0; i < responseList.size(); i++) {

							String mndcodeStr = responseList.get(i)
									.getProperty("mndcode").toString().trim();

							if (mndcodeStr.equals("anyType{}")) {

								mndcodeStr = "";
							}
							if (Utilities.showLogs == 0) {
								Log.i("mndcode*******", "" + mndcodeStr);
							}

							String mndnameStr = responseList.get(i)
									.getProperty("mndname").toString().trim();

							if (mndnameStr.equals("anyType{}")) {

								mndnameStr = "";
							}
							if (Utilities.showLogs == 0) {
								Log.i("mndname*******", "" + mndnameStr);
							}

							mandalCodeList.add(mndcodeStr);
							mandalNameList.add(mndnameStr);

						}

						Utilities.assignArrayAdpToSpin(GetWorkOrders.this,
								mandalNameList, spi_mandal);

					} else {
						Toast.makeText(getApplicationContext(), "No records",
								Toast.LENGTH_SHORT).show();
					}
				} else {
					Utilities.Error_Msg(GetWorkOrders.this, "No Data found");
				}
			} catch (NullPointerException e) {

			}
		}
	}

	Geocoder geocoder;
	List<Address> addresses;

	private void getGeocoderValues(double LATITUDE, double LONGITUDE) {

		geocoder = new Geocoder(this, Locale.getDefault());

		try {
			// Here 1 represent max location result to returned, by documents it
			// recommended 1 to 5
			addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1);

			String address = addresses.get(0).getAddressLine(0); // If any
																	// additional
																	// address
																	// line
																	// present
																	// than
																	// only,
																	// check
																	// with max
																	// available
																	// address
																	// lines by
																	// getMaxAddressLineIndex()
			String city = addresses.get(0).getLocality();
			String state = addresses.get(0).getAdminArea();
			String country = addresses.get(0).getCountryName();
			String postalCode = addresses.get(0).getPostalCode();
			String knownName = addresses.get(0).getFeatureName(); // Only if
																	// available
																	// else
																	// return
																	// NULL

			String getAddressLine = addresses.get(0).getAddressLine(2);

			Log.i("address", address);
			Log.i("city", city);
			Log.i("state", state);
			Log.i("country", country);
			Log.i("postalCode", postalCode);
			Log.i("knownName", knownName);
			Log.i("getAddressLine", getAddressLine);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private String getCompleteAddressString(double LATITUDE, double LONGITUDE) {
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
				Log.w("My Current loction", "No Address returned!");
			}
		} catch (Exception e) {
			e.printStackTrace();
			Log.w("My Current loction", "Canont get Address!");
		}

		return strAdd;
	}

	public boolean onKeyDown(int iKeyCode, KeyEvent event) {

		if (iKeyCode == KeyEvent.KEYCODE_BACK
				|| iKeyCode == KeyEvent.KEYCODE_HOME) {

			final AlertDialog.Builder builder = new AlertDialog.Builder(
					GetWorkOrders.this);

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

}
