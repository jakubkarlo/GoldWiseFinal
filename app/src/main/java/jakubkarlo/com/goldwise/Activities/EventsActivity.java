package jakubkarlo.com.goldwise.Activities;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.LogOutCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.List;
import java.util.concurrent.ExecutionException;

import jakubkarlo.com.goldwise.Downloaders.EventsDownloader;
import jakubkarlo.com.goldwise.Adapters.EventAdapter;
import jakubkarlo.com.goldwise.R;

public class EventsActivity extends AppCompatActivity {

    private final int ADD_EVENT_REQUEST = 222;
    ParseUser currentUser;

    private List<ParseObject> events;
    RecyclerView eventsGrid;
    EventAdapter eventAdapter;
    TextView noEventsTextView;
    boolean shouldReloadData = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events);
        eventsGrid = (RecyclerView) findViewById(R.id.eventsGrid);
        currentUser = ParseUser.getCurrentUser();
        String phoneNumber = currentUser.getUsername();
        noEventsTextView = (TextView)findViewById(R.id.noEventsTextView);


                FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.addEventFab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EventsActivity.this, AddEventActivity.class);
                startActivityForResult(intent, ADD_EVENT_REQUEST);
            }
        });

        //read the events using the async task
        EventsDownloader eventsDownloader = new EventsDownloader(this);
        try {
            events = eventsDownloader.execute(phoneNumber).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        Log.i("Events elements", String.valueOf(events.size()));
        if (events.size() == 0 ){
            noEventsTextView.setVisibility(View.VISIBLE);
        }
        else {
            noEventsTextView.setVisibility(View.INVISIBLE);


            eventsGrid.setHasFixedSize(true);
            eventsGrid.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
            eventAdapter =  new EventAdapter(events, eventsGrid);
            eventsGrid.setAdapter(eventAdapter);


        }

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public void logOut(View view){
        ParseUser currentUser = ParseUser.getCurrentUser();
        currentUser.logOutInBackground(new LogOutCallback() {
            @Override
            public void done(ParseException e) {

                if (e == null){
                    Toast.makeText(getApplicationContext(), "Logged out!", Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(getApplicationContext(), "Cannot logout, try again later", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }

            }
        });
        finish();
        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

//        if(requestCode == ADD_EVENT_REQUEST && resultCode == RESULT_OK){
//
//            shouldReloadData = true;
//
//        }

    }
}
