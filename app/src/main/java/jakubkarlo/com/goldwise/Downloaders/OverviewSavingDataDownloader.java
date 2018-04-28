package jakubkarlo.com.goldwise.Downloaders;

import android.os.AsyncTask;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

/**
 * Created by Jakub on 05.12.2017.
 */

public class OverviewSavingDataDownloader extends AsyncTask<String, Void, ParseObject> {


    @Override
    protected ParseObject doInBackground(String... params) {

        ParseObject yourSaving = null;
        try {
            ParseObject eventPointer = ParseObject.createWithoutData("Event", params[0]);
            ParseQuery query = ParseQuery.getQuery("Saving");
            // right now get the newest one, let user choose one
            yourSaving = query.include("eventID").orderByDescending("updatedAt").whereEqualTo("eventID", eventPointer).getFirst();

        } catch (ParseException e1) {
            e1.printStackTrace();
        }
        return yourSaving;
    }
}
