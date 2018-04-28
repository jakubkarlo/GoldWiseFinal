package jakubkarlo.com.goldwise.Activities;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.parse.ParseUser;

import jakubkarlo.com.goldwise.Adapters.DatabaseContactsAdapter;
import jakubkarlo.com.goldwise.R;

import static android.Manifest.permission.READ_CONTACTS;

public class SplashScreen extends AppCompatActivity {


    private final int SPLASH_TIME_OUT = 500;
    private static final int REQUEST_READ_CONTACTS = 444;
    static DatabaseContactsAdapter databaseHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

//        it's important if you change the server instance etc
//        ParseUser currentUser = ParseUser.getCurrentUser();
//        currentUser.logOut();

        databaseHelper = new DatabaseContactsAdapter(this);



        // make logo to pulse
        ImageView logo = (ImageView) findViewById(R.id.appLogo);
        Animation pulse = AnimationUtils.loadAnimation(this, R.anim.pulse);
        logo.startAnimation(pulse);

        new Thread(new Runnable() {
            @Override
            public void run() {
                SharedPreferences sharedPreferences = getSharedPreferences("jakubkarlo.com.goldwise", Context.MODE_PRIVATE);
                boolean wasDataRead = sharedPreferences.getBoolean("wasDataRead", false);

                if (wasDataRead) {
                    return;
                }
                else {

                    databaseHelper.getContacts(SplashScreen.this);
                    sharedPreferences.edit().putBoolean("wasDataRead", true).apply();
                }
            }
        }).start();




        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                ParseUser currentUser = ParseUser.getCurrentUser();
                if (currentUser != null) {
                    Intent eventsIntent = new Intent(SplashScreen.this, EventsActivity.class);
                    startActivity(eventsIntent);
                    finish();
                }
                else{
                    Intent homeIntent = new Intent(SplashScreen.this, LoginActivity.class);
                    startActivity(homeIntent);
                    finish();
                }
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        }, SPLASH_TIME_OUT);

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        if (requestCode == REQUEST_READ_CONTACTS) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                databaseHelper.getContacts(SplashScreen.this);
            }
        }
    }


}
