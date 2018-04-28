package jakubkarlo.com.goldwise.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.FilterQueryProvider;
import android.widget.ImageView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import com.google.gson.Gson;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ExecutionException;

import jakubkarlo.com.goldwise.Adapters.EventToAddParticipantsAdapter;
import jakubkarlo.com.goldwise.Models.Person;
import jakubkarlo.com.goldwise.R;
import jakubkarlo.com.goldwise.Uploaders.NewObjectUploader;


public class AddEventActivity extends AppCompatActivity {

    private final int CHANGE_PHOTO_REQUEST = 111;

    ImageView eventImage;
    EditText eventTitle, eventBudget;
    Bitmap eventBitmap;


    AutoCompleteTextView participantsAutoCompleteTextView;
    RecyclerView participantsToAddListView;

    Random randomNumber;

    final static int[] to = new int[]{android.R.id.text1};
    final static String[] from = new String[]{"name"};


    ArrayList<Person> people;
    ArrayList<String> namesToDisplay;

    //REMEMBER ABOUT PERMISSIONS

    public void changePhoto(View view) {

        getPhoto();

    }

    public void getPhoto() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, CHANGE_PHOTO_REQUEST);
    }

    public void addEventIfDataProvided(View view){

        if (people.isEmpty()){
            Toast.makeText(this, R.string.provideData_toast, Toast.LENGTH_SHORT).show();
        }

        else if (eventTitle.getText().toString().equals("")){
            Toast.makeText(this, R.string.provideData_toast, Toast.LENGTH_SHORT).show();
        }

        if(eventBudget.getText().toString().equals("")){

            new AlertDialog.Builder(this)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle(R.string.title_dataMissingDialog)
                    .setMessage(R.string.eventWithoutDataMessage_dataMissingDialog)
                    .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // if eventBudget is empty set it to 0 to avoid errors while parsing int
                            if (eventBudget.getText().toString().equals("")) {
                                eventBudget.setText("0");
                            }
                            addEvent();
                        }
                    })
                    .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    })
                    .show();

        }

        else {
            addEvent();
        }




    }


    public void addEvent() {

        Gson gson = new Gson();
        JSONArray participants = new JSONArray();

        //add current user as a participant too, username is a phone num!
        ParseUser currentUser = ParseUser.getCurrentUser();
        people.add(new Person(currentUser.getString("nickname"), currentUser.getUsername(), 0, Color.argb(255, randomNumber.nextInt(255), randomNumber.nextInt(255), randomNumber.nextInt(255))));


            for (Person person : people) {
                String oneGuy = gson.toJson(person);
                JSONObject object = null;
                try {
                    object = new JSONObject(oneGuy);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                participants.put(object);
            }


            ParseObject parseEvent = new ParseObject("Event");
            parseEvent.put("title", eventTitle.getText().toString());
            parseEvent.put("participants", participants);

            parseEvent.put("budget", Integer.parseInt(eventBudget.getText().toString()));

            //process image in order to send to server
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            eventBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] byteArray = stream.toByteArray();
            ParseFile imageFile = new ParseFile("image.png", byteArray);
            parseEvent.put("image", imageFile);

            //upload the object and return message
            NewObjectUploader eventUploader = new NewObjectUploader("event");
            String message = null;
            try {
                message = eventUploader.execute(parseEvent).get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }

            if (message != null) {
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
            }


            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    setResult(RESULT_OK);
                    finish();
                }
            });

        }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);

        eventImage = (ImageView) findViewById(R.id.addEventImage);
        eventTitle = (EditText) findViewById(R.id.eventTitleEditText);
        participantsAutoCompleteTextView = (AutoCompleteTextView) findViewById(R.id.participantsAutoCompleteTextView);
        participantsToAddListView = (RecyclerView) findViewById(R.id.participantsToAddListView);
        eventBudget = (EditText) findViewById(R.id.budgetEditText);

        namesToDisplay = new ArrayList<>();
        people = new ArrayList<>();
        randomNumber = new Random();

        //initiate bitmap to default image
        eventBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.app_logo);
        eventImage.setImageBitmap(eventBitmap);


        participantsToAddListView.setHasFixedSize(true);
        participantsToAddListView.setLayoutManager(new LinearLayoutManager(this));
        EventToAddParticipantsAdapter participantsAdapter = new EventToAddParticipantsAdapter(people, participantsToAddListView);
        participantsToAddListView.setAdapter(participantsAdapter);


        SimpleCursorAdapter autoCompleteAdapter =
                new SimpleCursorAdapter(this,
                        android.R.layout.simple_dropdown_item_1line, null,
                        from, to, 0);

        participantsAutoCompleteTextView.setAdapter(autoCompleteAdapter);


        participantsAutoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                addParticipantButton.setTag(position);
                Cursor cursor = (Cursor) parent.getItemAtPosition(position);
                String phoneNumber = cursor.getString(cursor.getColumnIndexOrThrow("phone_number"));
                String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));

                people.add(new Person(name, phoneNumber, 0, Color.argb(255, randomNumber.nextInt(255), randomNumber.nextInt(255), randomNumber.nextInt(255))));
                participantsToAddListView.getAdapter().notifyDataSetChanged();

            }
        });




        autoCompleteAdapter.setCursorToStringConverter(new SimpleCursorAdapter.CursorToStringConverter() {
            @Override
            public CharSequence convertToString(Cursor cursor) {
                try {
                    final int columnIndex = cursor.getColumnIndexOrThrow("name");
                    final String str = cursor.getString(columnIndex);
                    return str;
                } catch (Exception e){
                    e.printStackTrace();
                }
                return null;
            }
        });

        autoCompleteAdapter.setFilterQueryProvider(new FilterQueryProvider() {
            @Override
            public Cursor runQuery(CharSequence constraint) {
                try {
                    // Search for states whose names begin with the specified letters.
                    Cursor cursor = SplashScreen.databaseHelper.getMatchingStates(
                            (constraint != null ? constraint.toString() : null));
                    return cursor;
                }catch(Exception e){
                    e.printStackTrace();
                }
                return null;
            }
        });

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CHANGE_PHOTO_REQUEST && resultCode == RESULT_OK && data != null) {

            Uri selectedImage = data.getData();

            try {
                eventBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                eventImage.setImageBitmap(eventBitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }
}
