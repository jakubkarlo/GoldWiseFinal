package jakubkarlo.com.goldwise.Downloaders;

import android.os.AsyncTask;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import org.json.JSONArray;

import jakubkarlo.com.goldwise.Fragments.EventOverviewFragment;

/**
 * Created by Jakub on 28.11.2017.
 */

public class ParticipantsDownloader extends AsyncTask<String, Void, JSONArray> {
    @Override
    protected JSONArray doInBackground(String... objectIDs) {

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Event");

        ParseObject event = null;
        try {
            event = query.get(objectIDs[0]);
        } catch (ParseException e) {
            e.printStackTrace();
        }


        if (event != null) {
            event.pinInBackground("currentEvent");
            return event.getJSONArray("participants");
        }

        // maybe return exception or message?
        return null;

    }
}
