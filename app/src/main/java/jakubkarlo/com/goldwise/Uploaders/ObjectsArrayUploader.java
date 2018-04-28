package jakubkarlo.com.goldwise.Uploaders;

import android.os.AsyncTask;
import android.util.Log;

import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.SaveCallback;

import java.util.ArrayList;

/**
 * Created by Jakub on 10.12.2017.
 */

public class ObjectsArrayUploader extends AsyncTask<ArrayList<ParseObject>, Void, Void> {
    @Override
    protected Void doInBackground(ArrayList<ParseObject>... params) {

        final ArrayList<ParseObject> objects = params[0];

        ParseObject.create("Debt").saveAllInBackground(objects, new SaveCallback() {
            @Override
            public void done(ParseException e) {

                if (e == null){

                    Log.i("Returned", "Succesfully uploaded");

                }
                else{

                    e.printStackTrace();

                }

            }
        });

        return null;
    }
}
