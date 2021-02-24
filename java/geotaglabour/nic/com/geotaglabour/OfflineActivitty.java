package geotaglabour.nic.com.geotaglabour;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class OfflineActivitty extends Activity {
	Button btn_takepic_ldb, btn_save, btn_quit,btn_exit;
	EditText edt_image_name, edt_location;
	ImageView imgWork;
	Bitmap capturedBitmap;
	// ImageView imgWork_ldb;
	private int year;
	private int month;
	private int day;
	File destination;
	double latitude = 0, longitude = 0;
	private static final int TAKE_PICTURE = 0;
	String str_imgName,userentered="";
	GPSTracker gps;
	private LocationManager locationManager;
	Database dbCls;
	Cursor cur;
	SharedPreferences sharePref;
	Context context;
	String sno = "";
	int tab_id = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_offline_activitty);

		dbCls = new Database(this);

		gps = new GPSTracker(OfflineActivitty.this);
		context = OfflineActivitty.this.getApplicationContext();
		sharePref = getApplicationContext().getSharedPreferences("geoTag", 0);
		userentered = sharePref.getString("userentered", "0");
		btn_takepic_ldb = (Button) findViewById(R.id.btn_takepic_ldb);
		edt_image_name = (EditText) findViewById(R.id.edt_image_name);
		edt_location = (EditText) findViewById(R.id.edt_location);
		imgWork = (ImageView) findViewById(R.id.imgWork);
		btn_save = (Button) findViewById(R.id.btn_save);
		btn_exit = (Button) findViewById(R.id.btn_exit);

		btn_quit = (Button) findViewById(R.id.btn_quit);
		
		btn_quit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				Intent i = new Intent(OfflineActivitty.this, OfflineActivitty.class);
				startActivity(i);
				finish();
			}
		});

		
//		
//		GPSTracker gs= new GPSTracker(this);
//		Boolean test=false;
//		test=gs.canGetLocation;
//		Location location=gs.getLocation();
//	
//	 
		btn_exit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				final AlertDialog.Builder builder = new AlertDialog.Builder(
						OfflineActivitty.this);

				builder.setMessage("Are you sure to exit?")
						.setCancelable(false)
						.setPositiveButton("Yes",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int id) {
										dialog.dismiss();
										startActivity(new Intent(
												OfflineActivitty.this,
												LoginActivity.class));
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
		btn_save.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				str_imgName = edt_image_name.getText().toString().trim();
				
				if (str_imgName.length()==0)
				{
////					Utilities.Error_Msg(getApplicationContext(),
////							"Please Enter Photo Name");
////					return;
////					
//					Utilities.showAlertDialog(OfflineActivitty.this, "message", "Please Enter Name", false);
					

					final AlertDialog.Builder builder = new AlertDialog.Builder(
							OfflineActivitty.this);

					builder.setMessage("Please Enter Name")
							.setCancelable(false)
							.setPositiveButton("Ok",
									new DialogInterface.OnClickListener() {
										public void onClick(DialogInterface dialog,
												int id) {
											dialog.dismiss();
//											startActivity(new Intent(
//													OfflineActivitty.this,
//													Login.class));
											//finish();

										}
									})
							.setNegativeButton("Cancel",
									new DialogInterface.OnClickListener() {
										public void onClick(DialogInterface dialog,
												int id) {
											dialog.dismiss();
										}
									});
					final AlertDialog alert = builder.create();

					alert.show();

				
				}
				else
					
				if (capturedBitmap== null) {
////					Utilities.Error_Msg(getApplicationContext(),
////							"Please Take Photo");
////					return;
//					
//					Utilities.showAlertDialog(OfflineActivitty.this, "message", "Please take picture ", false);
					

					final AlertDialog.Builder builder = new AlertDialog.Builder(
							OfflineActivitty.this);

					builder.setMessage("Please Take Picture")
							.setCancelable(false)
							.setPositiveButton("Ok",
									new DialogInterface.OnClickListener() {
										public void onClick(DialogInterface dialog,
												int id) {
											dialog.dismiss();
//											startActivity(new Intent(
//													OfflineActivitty.this,
//													Login.class));
											//finish();

										}
									})
							.setNegativeButton("Cancel",
									new DialogInterface.OnClickListener() {
										public void onClick(DialogInterface dialog,
												int id) {
											dialog.dismiss();
										}
									});
					final AlertDialog alert = builder.create();

					alert.show();

					
				} 
				else {
					cur = dbCls.getPhoto_tbl();
					if (cur.getCount() > 0) {
						cur.moveToLast();
						tab_id = Integer.parseInt(cur.getString(cur
								.getColumnIndex(cur.getColumnName(0)))) + 1;
					}
					dbCls.insrtOflnImg(Integer.toString(tab_id), userentered,
							str_imgName, getBytes(capturedBitmap),
							Double.toString(latitude),
							Double.toString(longitude), "1");
					
					Log.i("tab id",Integer.toString(tab_id));
					
					Log.i("userentered ",userentered);
					Log.i("str_imgName",str_imgName);
					
					Log.i("image",capturedBitmap.toString());
					Log.i("latitude id",Double.toString(latitude));
					Log.i("longitude",Double.toString(longitude));
					
					final AlertDialog.Builder builder = new AlertDialog.Builder(
							OfflineActivitty.this);

					builder.setMessage("Photo Saved In Offline Mode")
							.setCancelable(false)
							.setPositiveButton("OK",
									new DialogInterface.OnClickListener() {
										public void onClick(
												DialogInterface dialog, int id) {
											dialog.dismiss();
//											
//										Cursor Cur=	dbCls.getPhoto_tbl();	
//											int i =Cur.getCount();
//											Toast.makeText(context, i+"", 5000).show();
//											
											
											startActivity(new Intent(
													OfflineActivitty.this,
													OfflineActivitty.class));
											finish();

										}
									});
					final AlertDialog alert = builder.create();

					alert.show();

				}
			}

		});
	
		
		checkGps();

		// btn_takepic = (Button) findViewById(R.id.btn_takepic);
		btn_takepic_ldb.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				checkGpsTakepic();

				

			}
		});
	}

	
	
	
	
	private void checkGps(){
		 locationManager = (LocationManager) getSystemService( Context.LOCATION_SERVICE );

		    if ( locationManager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ==false) {
		        buildAlertMessageNoGps();
		    }
		  
//		Intent i=new Intent(OfflineActivitty.this,Login.class);
//		startActivity(i);
		
	   
	}
	
	private void checkGpsTakepic(){
		 locationManager = (LocationManager) getSystemService( Context.LOCATION_SERVICE );

		    if ( locationManager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ==false) {
		        buildAlertMessageNoGps();
		    }
		    else{
		    	latitude = gps.getLatitude();
				longitude = gps.getLongitude();
				System.out.println(latitude);
				onTakePic();
		    }
		  
		
	   
	}
	private void buildAlertMessageNoGps() {
		// TODO Auto-generated method stub
		final AlertDialog.Builder builder = new AlertDialog.Builder(this);
	    builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
	           .setCancelable(false)
	           .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
	               public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
	                 
	                 
	            	   
	            	   startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
	                   dialog.cancel();
	               }
	           });
//	           .setNegativeButton("No", new DialogInterface.OnClickListener() {
//	               public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
//	                    dialog.cancel();
//	               }
//	           });
	    final AlertDialog alert = builder.create();
	    alert.show();
	}

	private void onTakePic() {

		// selectedView = imageView;

		destination = new File(Environment.getExternalStorageDirectory(),
				"image.jpg");

		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

		intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(destination));
		startActivityForResult(intent, TAKE_PICTURE);
		
		
	}

	public static byte[] getBytes(Bitmap bitmap) {
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		bitmap.compress(CompressFormat.JPEG, 70, stream);

		Log.i("IMage size", "" + stream.toByteArray().length);
		return stream.toByteArray();
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
					imgWork.setImageBitmap(capturedBitmap);
				}

				// new InsertPhotoGeoTag().execute();

			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {

		}

		Handler mHandler1 = new Handler();
		
		
		mHandler1.postDelayed(new Runnable() {

			@Override
			public void run() {
				edt_location.setText(getCompleteAddressString(latitude,
						longitude));

				if (edt_location.getText().toString().trim().length() > 0) {

					// edt_location.setEnabled(false);
					edt_location.setClickable(false);
					edt_location.setFocusable(false);

				}

			}

		}, 2000);

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

			}
		} catch (Exception e) {
			e.printStackTrace();

		}

		return strAdd;
	}

	
	
	public boolean onKeyDown(int iKeyCode, KeyEvent event) {

		if (iKeyCode == KeyEvent.KEYCODE_BACK
				|| iKeyCode == KeyEvent.KEYCODE_HOME) {

			final AlertDialog.Builder builder = new AlertDialog.Builder(
					OfflineActivitty.this);

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
