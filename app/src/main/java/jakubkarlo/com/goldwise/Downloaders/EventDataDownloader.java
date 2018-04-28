package jakubkarlo.com.goldwise.Downloaders;

import android.os.AsyncTask;
import android.util.Log;

import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jakub on 06.12.2017.
 */

public class EventDataDownloader extends AsyncTask<String, Void, ArrayList<ParseObject>>{
    @Override
    protected ArrayList<ParseObject> doInBackground(String... params) {

        ArrayList<ParseObject> returnedObjects =null;

        ParseObject eventPointer = ParseObject.createWithoutData("Event", params[0]);
        ParseQuery<ParseObject> query = ParseQuery.getQuery(params[1]).orderByDescending("updatedAt");

        try {
            returnedObjects = (ArrayList<ParseObject>)
                    query.include("eventID")
                    .whereEqualTo("eventID", eventPointer)
                    .find();

        } catch (ParseException e) {
            e.printStackTrace();
        }

        return returnedObjects;
    }

}
