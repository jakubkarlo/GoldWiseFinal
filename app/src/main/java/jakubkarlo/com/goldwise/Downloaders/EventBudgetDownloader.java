package jakubkarlo.com.goldwise.Downloaders;

import android.os.AsyncTask;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseQuery;

/**
 * Created by Jakub on 05.12.2017.
 */

public class EventBudgetDownloader extends AsyncTask<String, Void, Double> {
    @Override
    protected Double doInBackground(String... params) {

        double eventBudget = 0;
        try {
            eventBudget = ParseQuery.getQuery("Event").get(params[0]).getDouble("budget");
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return eventBudget;
    }
}
