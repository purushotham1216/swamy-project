package geotaglabour.nic.com.geotaglabour;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.MarshalBase64;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.util.ArrayList;

public class ImageList extends Activity {

	private ProgressDialog progressDialog;
	private static String SOAP_ACTION = null;
	private static String OPERATION_NAME = null;
	SoapObject soapRequest;
	SoapObject resultas;
	ArrayList<String> img_name_List = null;
	ArrayList<String> img_lat_List = null;
	ArrayList<String> img_lng_List = null;
	ArrayList<String> img_sno_List = null;

	Database dbCls;
	Cursor cur;
	String ad_agency_code = "", ad_work_order_no = "", ad_code = "", imei = "",
			userentered = "", ad_work_order_serial_code = "", location = "",
			distcode = "", mandcode = "", panchayatcode = "",
			ad_place_code = "", versioncheck = "",sno="";
	ListView lvMain;
	Myadpter myadpter;
	Button btn_back;
	SharedPreferences sharePref;
	Context context;
	Bitmap bmp = null;
	byte[] img = null;
	ArrayList<Bitmap> bitmapArray = new ArrayList<Bitmap>();
	public static int str;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.view_list_row);
		dbCls = new Database(this);


		context = ImageList.this.getApplicationContext();
		sharePref = getApplicationContext().getSharedPreferences("geoTag", 0);
		ad_agency_code = sharePref.getString("ad_agency_code", "0");
		ad_work_order_no = sharePref.getString("ad_work_order_no", "0");
		ad_code = sharePref.getString("ad_code", "0");
		userentered = sharePref.getString("userentered", "0");
	//Toast.makeText(this, userentered, Toast.LENGTH_LONG).show();
		ad_work_order_serial_code = sharePref.getString(
				"ad_work_order_serial_code", "0");
		location = sharePref.getString("location", "0");
		distcode = sharePref.getString("distcode", "0");
		mandcode = sharePref.getString("mandcode", "0");
		panchayatcode = sharePref.getString("panchayatcode", "0");
		ad_place_code = sharePref.getString("ad_place_code", "0");
		versioncheck = sharePref.getString("Version", "");
		lvMain = (ListView) findViewById(R.id.lvMain);
		lvMain.setAdapter(myadpter);
		btn_back = (Button) findViewById(R.id.btn_quit);
		btn_back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				startActivity(new Intent(ImageList.this, GetWorkDetails.class));
				finish();
			}
		});
		new GetOfflineList().execute();

	}

	class GetOfflineList extends AsyncTask<String, String, String> {
		public void onPreExecute() {
			progressDialog = new ProgressDialog(getApplicationContext());
			progressDialog.setMessage("Loading..");
			progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			progressDialog.setCancelable(false);
			// progressDialog.show();
		}

		@Override
		protected String doInBackground(String... params) {

			try {

				cur = dbCls.getPhoto_table(sno, "1");
				Log.i("curcount", "" + cur.getCount());
				if (cur.getCount() > 0) {

					img_name_List = new ArrayList<String>();
					img_lat_List = new ArrayList<String>();
					img_lng_List = new ArrayList<String>();
					img_sno_List = new ArrayList<String>();
					if (cur.moveToFirst()) {
						do {
							String img_sno = cur.getString(
									cur.getColumnIndex(cur.getColumnName(0)))
									.trim();
							String img_name = cur.getString(
									cur.getColumnIndex(cur.getColumnName(1)))
									.trim();

							String img_lat = cur.getString(
									cur.getColumnIndex(cur.getColumnName(4)))
									.trim();
							String img_lag = cur.getString(
									cur.getColumnIndex(cur.getColumnName(5)))
									.trim();
							img = cur.getBlob(cur.getColumnIndex("photo"));
							bmp = BitmapFactory.decodeByteArray(img, 0,
									img.length);

							bitmapArray.add(bmp);
							img_sno_List.add(img_sno);
							img_name_List.add(img_name);
							img_lat_List.add(img_lat);
							img_lng_List.add(img_lag);

						} while (cur.moveToNext());
					}
				}

			} catch (Exception exception) {
				exception.toString();
			}
			progressDialog.dismiss();
			return null;
		}

		public void onPostExecute(String params) {
			progressDialog.dismiss();
			try {

				if (cur.getCount() > 0) {
					myadpter = new Myadpter(ImageList.this);
					myadpter.notifyDataSetChanged();
					lvMain.setAdapter(myadpter);
				} else {
					Toast.makeText(getApplicationContext(),
							"No records found...", Toast.LENGTH_LONG).show();
				}
			} catch (NullPointerException e) {
				Toast.makeText(getApplicationContext(),
						"Database Dist not created...", Toast.LENGTH_LONG)
						.show();
			}
		}
	}

	public class Myadpter extends BaseAdapter {
		private Context context;

		public Myadpter(Context c) {
			context = c;

		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return img_name_List.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		View myview;

		public View getView(final int position, View convertView,
				ViewGroup parent) {
			// TODO Auto-generated method stub

			LayoutInflater inflater = getLayoutInflater();
			myview = inflater.inflate(R.layout.list_items, null);

			TextView txt_row_sno = (TextView) myview.findViewById(R.id.sno);
			TextView txt_img_name = (TextView) myview
					.findViewById(R.id.up_imgName);
			ImageView img_photo = (ImageView) myview.findViewById(R.id.up_img);

			txt_row_sno.setText("" + (position + 1));
			txt_img_name.setText(img_name_List.get(position).toString());
			img_photo.setImageBitmap(bitmapArray.get(position));
			Button btn_upload = (Button) myview.findViewById(R.id.btn_upload);
			btn_upload.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					str = position;
					new InsertPhotoGeoTag().execute();
				}
			});

			img_photo.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub

					GetWorkDetails.latitude = Double.parseDouble(img_lat_List
							.get(position));
					GetWorkDetails.longitude = Double.parseDouble(img_lng_List
							.get(position));

					GetWorkDetails.capturedBitmap = bitmapArray.get(position);
					GetWorkDetails.imgWork.setImageBitmap(bitmapArray
							.get(position));
					onBackPressed();

				}
			});
			return myview;
		}

		protected void remove(Object item) {
			// TODO Auto-generated method stub

		}

		@Override
		public void notifyDataSetChanged() {
			// TODO Auto-generated method stub
			super.notifyDataSetChanged();
		}
	}

	Object resultAsObjInsert;
	String resultAsStr = "";

	class InsertPhotoGeoTag extends AsyncTask<String, String, String> {
		public void onPreExecute() {
			progressDialog = new ProgressDialog(ImageList.this);
			progressDialog.setMessage("Loading..");
			progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			progressDialog.setCancelable(false);
			progressDialog.show();
		}

		@Override
		protected String doInBackground(String... params) {
			try {

				SOAP_ACTION = Utilities.WSDL_TARGET_NAMESPACE
						+ Utilities.OPERATION_NAME_insert_photogeotag;

				soapRequest = new SoapObject(Utilities.WSDL_TARGET_NAMESPACE,
						Utilities.OPERATION_NAME_insert_photogeotag);

				soapRequest.addProperty("ad_agency_code", ad_agency_code);

				soapRequest.addProperty("ad_work_order_no", ad_work_order_no);

				soapRequest.addProperty("ad_code", ad_code);

				soapRequest
						.addProperty("lat", img_lat_List.get(str).toString());

				soapRequest.addProperty("longitude", img_lng_List.get(str)
						.toString());

				soapRequest.addProperty("imei", imei);

				soapRequest.addProperty("userentered", userentered);

				soapRequest.addProperty("phototype", ".JPEG");

				soapRequest.addProperty("photo", bitmapArray.get(str)
						.toString());
				soapRequest.addProperty("ad_work_order_serial_code",
						ad_work_order_serial_code);

				soapRequest.addProperty("location", location);
				soapRequest.addProperty("distcode", distcode);
				soapRequest.addProperty("mandcode", mandcode);

				soapRequest.addProperty("panchayatcode", panchayatcode);
				soapRequest.addProperty("ad_place_code", ad_place_code);
				soapRequest.addProperty("versionchk", versioncheck);

				Log.i("request_submit", soapRequest.toString());
				SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
						SoapEnvelope.VER11);
				envelope.dotNet = true;
				new MarshalBase64().register(envelope);

				envelope.setOutputSoapObject(soapRequest);

				HttpTransportSE httpTransport = new HttpTransportSE(
						Utilities.urlInsertGeoTagging);
				httpTransport.call(SOAP_ACTION, envelope);
				resultas = (SoapObject) envelope.bodyIn;
				Log.i("request_submit, result", resultas.toString());

				resultAsObjInsert = envelope.getResponse();
				//
				resultAsStr = resultAsObjInsert.toString();
				Log.i("resultas_submit*******", "" + resultAsStr.toString());
				if (Utilities.showLogs == 0) {
					Log.i("resultas_submit*******", "" + resultAsStr.toString());

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
							ImageList.this);
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
												dbCls.updateurl(sno,
														img_sno_List.get(str)
																.toString(),
														"0");
												startActivity(new Intent(
														ImageList.this,
														ImageList.class));
												finish();
											}

										}
									});
					final AlertDialog alert = builder.create();

					alert.show();
				} else {

					final AlertDialog.Builder builder = new AlertDialog.Builder(
							ImageList.this);
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

	@Override
	public void onBackPressed() {
		// code here to show dialog
		super.onBackPressed(); // optional depending on your needs
	}

	// public boolean onKeyDown(int iKeyCode, KeyEvent event) {
	//
	// if (iKeyCode == KeyEvent.KEYCODE_BACK
	// || iKeyCode == KeyEvent.KEYCODE_HOME) {
	// return true;
	// }
	// return super.onKeyDown(iKeyCode, event);
	// }
}
