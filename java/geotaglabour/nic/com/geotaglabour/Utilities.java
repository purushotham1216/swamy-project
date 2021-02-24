package geotaglabour.nic.com.geotaglabour;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.List;

public class Utilities {
	
	// 0-show, 1-not show
		public static final int showLogs 
		= 0;
	//			= 1;

	// local
	private static String main_url 	
	//= "http://10.160.2.166:8077/LabourDept/services/";
	//= "http://10.160.19.220:8077/LabourDept/services/";
	
	//live
	//= "http://164.100.187.16:8080/LabourDept/services/";
	//http://10.160.2.101:8082/LabourDept/services/LoginCheckLabourDept?wsdl
	
	
	//staging url
	
	//= "http://10.160.2.101:8082/LabourDept/services/";
	
	//OLD Public URL
	//= "http://apbocwwb.ap.nic.in/LabourDept/services/";
	//= "http://10.160.19.220:8081/LabourDept/services/";
	//new local UEL
	= "https://apbocwwb.ap.nic.in/LabourDept/MobileAppsInterface?wsdl";
	
	/****** OLD  Public URLS
	 * public static String WSDL_TARGET_NAMESPACE = "http://geotagging";
	public static String urlLogin = main_url+"LoginCheckLabourDept?wsdl";
	public static String urlGetWorkOrders = main_url+"GetWorkOrders?wsdl";
	public static String urlInsertGeoTagging = main_url+"InsertGeoTagging?wsdl";
	public static String urlGetAdvtAgencyDetails = main_url+"GetAdvtAgencyDetails?wsdl";
	
	public static String urlGetDistricts = main_url+"GetDistricts?wsdl";
	public static String urlGetMandals = main_url+"GetMandals?wsdl";
	public static String urlGetWorkOrderMst = main_url+"GetWorkOrderMst?wsdl";
	
	public static String urlPlaceofAd = main_url+"PlaceofAd?wsdl";
	public static String urlGetPanchayats = main_url+"GetPanchayats?wsdl";
	
	public static String OPERATION_NAME_loginchklabourdept = "loginchklabourdept";
	public static String OPERATION_NAME_getworkorderdetails = "getworkorderdetails";
	public static String OPERATION_NAME_insert_photogeotag = "insert_photogeotag";
	public static String OPERATION_NAME_getadvtagencydetails = "getadvtagencydetails";
	public static String OPERATION_NAME_getworkordermst = "getworkordermst";
	
	
	public static String OPERATION_NAME_getdistdetails = "getdistdetails";
	public static String OPERATION_NAME_getmanddetails = "getmanddetails";
	
	public static String OPERATION_NAME_PlaceofAd = "getplaceofadmst";
	public static String OPERATION_NAME_getpanchayatdetails= "getpanchayatdetails";*/
	
	public static String WSDL_TARGET_NAMESPACE= "http://services.com/";
	public static String urlLogin = main_url;
	public static String urlGetWorkOrders = main_url;
	public static String urlInsertGeoTagging = main_url;
	public static String urlGetAdvtAgencyDetails = main_url;
	
	public static String urlGetDistricts = main_url;
	public static String urlGetMandals = main_url;
	public static String urlGetWorkOrderMst = main_url;
	
	public static String urlPlaceofAd = main_url;
	public static String urlGetPanchayats = main_url;
	
	public static String OPERATION_NAME_loginchklabourdept = "loginchklabourdept";
	public static String OPERATION_NAME_getworkorderdetails = "getworkorderdetails";
	public static String OPERATION_NAME_insert_photogeotag = "insert_photogeotag";
	public static String OPERATION_NAME_getadvtagencydetails = "getadvtagencydetails";
	public static String OPERATION_NAME_getworkordermst = "getworkordermst";
	
	
	public static String OPERATION_NAME_getdistdetails = "getdistdetails";
	public static String OPERATION_NAME_getmanddetails = "getmanddetails";
	
	public static String OPERATION_NAME_PlaceofAd = "getplaceofadmst";
	public static String OPERATION_NAME_getpanchayatdetails= "getpanchayatdetails";
	


	public static void Error_Msg(Context context, String Message) {
		// TODO Auto-generated method stub

		final AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle("Warning");
		builder.setIcon(R.drawable.warning);
		builder.setMessage(Message).setCancelable(false)
				.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						// //Log.i("alert id : ", "" + id);
						dialog.dismiss();

					}
				});
		final AlertDialog alert = builder.create();

		alert.show();

	}
	
	public static void showAlertDialog(Context context, String title,
			String message, Boolean status) {

		final AlertDialog.Builder builder = new AlertDialog.Builder(context);

		builder.setTitle(title);

		if (status == true) {
			builder.setIcon(R.drawable.success);
		} else {
			builder.setIcon(R.drawable.fail);
		}

		builder.setMessage(message).setCancelable(false)
				.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {

						dialog.cancel();

					}
				});
		final AlertDialog alert = builder.create();

		alert.show();

		// AlertDialog alertDialog = new AlertDialog.Builder(context).create();
		//
		// // Setting Dialog Title
		// alertDialog.setTitle(title);
		//
		// // Setting Dialog Message
		// alertDialog.setMessage(message);
		//
		// if (status != null)
		// // Setting alert dialog icon
		// alertDialog
		// .setIcon((status) ? R.drawable.success : R.drawable.fail);
		//
		// // Setting OK Button
		// alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
		// public void onClick(DialogInterface dialog, int which) {
		// }
		// });
		//
		// // Showing Alert Message
		// alertDialog.show();
	}

	static String versionNameOrCodeStr = "";

	public static String getVersionNameCode(Context _context) {

		try {
			PackageInfo pinfo = _context.getPackageManager().getPackageInfo(
					_context.getPackageName(), 0);
			
			if (Utilities.showLogs == 0) {
				Log.d("pinfoCode", "" + pinfo.versionCode);
				Log.d("pinfoName", pinfo.versionName);
			}

			//versionCodeStr=String.valueOf(pinfo.versionCode);
			versionNameOrCodeStr = pinfo.versionName;

		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return versionNameOrCodeStr;
	}
	
	public static void assignArrayAdpToSpin(Context context,List<String> array,
			Spinner spin) {

		ArrayAdapter<String> arrayAdp = new ArrayAdapter<String>(context,
				R.layout.spinner_text, array);
		spin.setAdapter(arrayAdp);
		arrayAdp.notifyDataSetChanged();

	}
	
	public static String getRandomString(int lengthh) {
		int length=10;
		char[] charsetArray = "0123456789abcdefghijklmnopqrstuvwxyz"
		.toCharArray();
		SecureRandom random = new SecureRandom();
		char[] result = new char[length];
		for (int i = 0; i < result.length; i++) {
		int randomPick = random.nextInt(charsetArray.length);
		result[i] = charsetArray[randomPick];
		}
		return new String(result);
		}
	
	public static String sha256(String input) throws NoSuchAlgorithmException {
		MessageDigest mDigest = MessageDigest.getInstance("SHA-256");
		byte[] result = mDigest.digest(input.getBytes());
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < result.length; i++) {
			sb.append(Integer.toString((result[i] & 0xff) + 0x100, 16)
					.substring(1));
		}
		String sha256Hash = sb.toString();
		return sha256Hash;
	}

	public static String md5(String passwd) throws NoSuchAlgorithmException {
		MessageDigest md = MessageDigest.getInstance("MD5");
		md.update(passwd.getBytes());
		byte[] digest = md.digest();
		StringBuffer hexString = new StringBuffer();
		for (int i = 0; i < digest.length; i++) {
			passwd = Integer.toHexString(0xFF & digest[i]);
			if (passwd.length() < 2) {
				passwd = "0" + passwd;
			}
			hexString.append(passwd);
		}
		String md5Hash = hexString.toString();
		return md5Hash;
	}

}
