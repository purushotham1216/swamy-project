package geotaglabour.nic.com.geotaglabour;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

public class Database extends SQLiteAssetHelper {
	private static final String DBname = "geolocation.db";
	private static final int dbversion = 1;

	private SQLiteDatabase sqliteDBInstance = null;

	public Database(Context context) {
		// TODO Auto-generated constructor stub
		super(context, DBname, null, dbversion);

	}


	/*@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("photo_location");
	}*/

	/*@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub

		onCreate(db);
	}*/




	

	public long insrtOflnImg(String sno, 
			String str_imgName, String userentered, byte[] photo, String lat, String lng, String flg) {
		ContentValues cv = new ContentValues();

		cv.put("sno", sno);
		cv.put("username", userentered);
		cv.put("photo_name", str_imgName);
		cv.put("photo", photo);
		cv.put("lat", lat);
		cv.put("lan", lng);
		cv.put("flg", flg);

		Log.i("photo*****", "Inserted");
		return  getWritableDatabase().insert("photo_location", null, cv);

	}

	public Cursor getPhoto_tbl() {
		String qry = "SELECT * FROM  photo_location";

		Cursor cur = getReadableDatabase().rawQuery(qry, null);

		return cur;
	}
	
	
	
	public Cursor getPhoto_table(String username, String flag) {
		String qry = "SELECT * FROM  photo_location where photo_name='"
				+ username + "' and flg='" + flag + "'";

		Cursor cur = getReadableDatabase().rawQuery(qry, null);

		return cur;
	}
	

	public boolean updateurl(String username, String sno, String falg) {
		ContentValues v = new ContentValues();
		v.put("flg", falg);

		return getWritableDatabase().update("photo_location", v, "username" + "='"
				+ username + "' and sno='"+sno+"'", null) > 0;
	}

}
