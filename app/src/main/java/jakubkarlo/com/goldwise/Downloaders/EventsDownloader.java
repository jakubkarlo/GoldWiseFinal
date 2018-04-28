package jakubkarlo.com.goldwise.Downloaders;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Created by Jakub on 28.11.2017.
 */

public class EventsDownloader extends AsyncTask<String, Void, ArrayList<ParseObject>> {

    private Activity activity;
    private ProgressDialog dialog;

    public EventsDownloader(Activity activity){
        this.activity = activity;
    }


    @Override
    protected ArrayList<ParseObject> doInBackground(final String... params) {

        ArrayList<ParseObject> events = new ArrayList<>();


        ParseQuery<ParseObject> query = ParseQuery.getQuery("Event");


        try {
            events = (ArrayList<ParseObject>)query.find();
        } catch (ParseException e) {
            e.printStackTrace();
        }

//        try {
//            for (ParseObject object : events) {
//                boolean participates = false;
//                JSONArray participants = object.getJSONArray("participants");
//                for (int i = 0; i < participants.length(); i++) {
//                    if (params[0] != null) {
//                        if (participants.getJSONObject(i).getString("phoneNumber").equals(params[0])) {
//                            participates = true;
//                        }
//                    }
//                }
//                if (!participates){
//                    events.remove(object);
//                }
//            }
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }

//        query.findInBackground(new FindCallback<ParseObject>() {
//            @Override
//            public void done(List<ParseObject> objects, ParseException e) {
//
//                if (e == null) {
//
//                    Log.i("FindInBackground", "Success");
//
//                } else {
//                }
//
//                if (objects.size() > 0) {
//
//                    try {
//                        for (ParseObject object : objects) {
//                            boolean participates = false;
//                            JSONArray participants = object.getJSONArray("participants");
//                            for (int i = 0; i < participants.length(); i++) {
//                                if (params[0] != null) {
//                                    if (participants.getJSONObject(i).getString("phoneNumber").equals(params[0])) {
//                                        participates = true;
//                                    }
//                                }
//                            }
//                            if (participates) {
//                                events.add(object);
//                                Log.i("Async events size:", String.valueOf(events.size()));
//                            }
//
//                        }
//                    } catch (JSONException e1) {
//                        e1.printStackTrace();
//                    }
//
//                }
//
//            }
//        });

        // NULL EXCEPTIONS?!
        return events;

    }


    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        Log.i("on Pre", "im thereee");
        dialog = new ProgressDialog(activity);
        dialog.setMessage("Loading events...");
        dialog.show();
    }

    @Override
    protected void onPostExecute(ArrayList<ParseObject> parseObjects) {
        super.onPostExecute(parseObjects);
        Log.i("on Post", "im thereee");
        dialog.dismiss();
    }


}
