package jakubkarlo.com.goldwise.Uploaders;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.SaveCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;

import jakubkarlo.com.goldwise.Activities.AddEventActivity;

/**
 * Created by Jakub on 28.11.2017.
 */

public class NewObjectUploader extends AsyncTask<ParseObject, Void, String> {

    String objectType;

    public NewObjectUploader(String objectType){
        this.objectType = objectType;
    }

    @Override
    protected String doInBackground(ParseObject... params) {

        final ParseObject parseObject = params[0];
        final String[] message = new String[1];
        // add unique?!
        if (!objectType.equals("event")) {
            try {
                parseObject.pin("latestObject");
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        parseObject.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {

                if (e == null){
                    if (objectType.equals("event")) {
                        try {
                            parseObject.pin("latestObject");
                        } catch (ParseException e1) {
                            e1.printStackTrace();
                        }
                    }
                    message[0] = "Uploaded!";

                } else{

                    e.printStackTrace();
                    message[0] =  "Cannot be uploaded";


                }

            }
        });

        return message[0];

    }

}
