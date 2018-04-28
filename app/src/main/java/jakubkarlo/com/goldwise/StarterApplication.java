package jakubkarlo.com.goldwise;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseACL;


/**
 * Created by Jakub on 06.11.2017.
 */



public class StarterApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        // Enable Database.
        Parse.enableLocalDatastore(this);

        // Add your initialization code here
        Parse.initialize(new Parse.Configuration.Builder(getApplicationContext())
                .applicationId("b1ab37f28d53447d97fe5b7abfd940e695136918")
                .clientKey("6c0772c76eed7bebe0ba705e4cdf222f05600ec3")
                .server("http://18.195.3.45:80/parse")
                .enableLocalDataStore()
                .build()
        );


        ParseACL defaultACL = new ParseACL();
        defaultACL.setPublicReadAccess(true);
        defaultACL.setPublicWriteAccess(true);
        ParseACL.setDefaultACL(defaultACL, true);




    }
}