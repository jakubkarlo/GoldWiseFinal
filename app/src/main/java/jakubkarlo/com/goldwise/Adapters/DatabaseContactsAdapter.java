package jakubkarlo.com.goldwise.Adapters;

/**
 * Created by Jakub on 14.12.2017.
 */

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.os.Build;
import android.provider.ContactsContract;
import android.util.Log;

import static android.Manifest.permission.READ_CONTACTS;

/**
 * Simple database access helper class.
 *
 * @author Dan Breslau
 */
public class DatabaseContactsAdapter {

    private static final String DATABASE_NAME = "contacts";
    private static final String TABLE_NAME = "contacts";
    private static final int DATABASE_VERSION = 1;

    private class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            final String DATABASE_CREATE_CONTACTS =
                    "CREATE TABLE IF NOT EXISTS " + TABLE_NAME
                            + "(_id integer primary key autoincrement"
                            + ", name VARCHAR"
                            + ", phone_number VARCHAR)";

            db.execSQL(DATABASE_CREATE_CONTACTS);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
            onCreate(db);
        }
    }

    private DatabaseHelper mDbHelper;
    private SQLiteDatabase contactsDatabase;
    private static final int REQUEST_READ_CONTACTS = 444;


    /**
     * Constructor - takes the context to allow the database to be
     * opened/created
     *
     * @param activity the Activity that is using the database
     */
    public DatabaseContactsAdapter(Activity activity) {
        mDbHelper = this.new DatabaseHelper(activity);
        contactsDatabase = mDbHelper.getWritableDatabase();
    }

    public void createDatabase(SQLiteDatabase db){

    }

    /**
     * Closes the database.
     */
    public void close() {
        mDbHelper.close();
    }

    /**
     * Return a Cursor that returns all states (and their state capitals) where
     * the state name begins with the given constraint string.
     *
     * @param constraint Specifies the first letters of the states to be listed. If
     *                   null, all rows are returned.
     * @return Cursor managed and positioned to the first state, if found
     * @throws SQLException if query fails
     */
    public Cursor getMatchingStates(String constraint) throws SQLException {

        String queryString =
                "SELECT _id, name, phone_number FROM " + TABLE_NAME;

        if (constraint != null) {
            constraint = "%" + constraint.trim() + "%";
            queryString += " WHERE name LIKE ?";
        }
        String params[] = {constraint};

        if (constraint == null) {
            // If no parameters are used in the query,
            // the params arg must be null.
            params = null;
        }
        try {
            Cursor cursor = contactsDatabase.rawQuery(queryString, params);
            if (cursor != null) {
                cursor.moveToFirst();
                return cursor;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }

        return null;
    }


    private boolean mayRequestContacts(Activity activity) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (activity.checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (activity.shouldShowRequestPermissionRationale(READ_CONTACTS)) {
            activity.requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
        } else {
            activity.requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
        }
        return false;
    }


    public void getContacts(Activity activity) {

        Cursor cursor;

        if (!mayRequestContacts(activity)) {
            return;
        }

        String phoneNumber = null;

        Uri CONTENT_URI = ContactsContract.Contacts.CONTENT_URI;
        String _ID = ContactsContract.Contacts._ID;
        String DISPLAY_NAME = ContactsContract.Contacts.DISPLAY_NAME;
        String HAS_PHONE_NUMBER = ContactsContract.Contacts.HAS_PHONE_NUMBER;

        Uri PhoneCONTENT_URI = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        String Phone_CONTACT_ID = ContactsContract.CommonDataKinds.Phone.CONTACT_ID;
        String NUMBER = ContactsContract.CommonDataKinds.Phone.NUMBER;

        ContentResolver contentResolver = activity.getContentResolver();

        cursor = contentResolver.query(CONTENT_URI, null, null, null, null);

        // Iterate every contact in the phone
        if (cursor.getCount() > 0) {

            contactsDatabase.beginTransaction();
            while (cursor.moveToNext()) {

                String contact_id = cursor.getString(cursor.getColumnIndex(_ID));
                String name = cursor.getString(cursor.getColumnIndex(DISPLAY_NAME));

                int hasPhoneNumber = Integer.parseInt(cursor.getString(cursor.getColumnIndex(HAS_PHONE_NUMBER)));

                if (hasPhoneNumber > 0) {

                    //This is to read multiple phone numbers associated with the same contact
                    Cursor phoneCursor = contentResolver.query(PhoneCONTENT_URI, null, Phone_CONTACT_ID + " = ?", new String[]{contact_id}, null);

                    while (phoneCursor.moveToNext()) {
                        phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex(NUMBER));
                        try {
                            ContentValues values = new ContentValues();
                            values.put("name", name);
                            values.put("phone_number", phoneNumber);

                            contactsDatabase.insert("contacts", null, values);

                        } catch (Exception e) {

                            e.printStackTrace();

                        }

                    }

                    phoneCursor.close();


                }

            }

            contactsDatabase.setTransactionSuccessful();
            contactsDatabase.endTransaction();

        }
    }

}
