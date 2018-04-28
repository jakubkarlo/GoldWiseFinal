package jakubkarlo.com.goldwise.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import jakubkarlo.com.goldwise.Adapters.DebtorsAdapter;
import jakubkarlo.com.goldwise.Adapters.ParticipantsSpinnerAdapter;
import jakubkarlo.com.goldwise.Models.DebtState;
import jakubkarlo.com.goldwise.Models.Person;
import jakubkarlo.com.goldwise.R;
import jakubkarlo.com.goldwise.Uploaders.NewObjectUploader;
import jakubkarlo.com.goldwise.Uploaders.ObjectsArrayUploader;

public class AddExpenseActivity extends AppCompatActivity {


//    private final String eventID = getIntent().getStringExtra("eventID");

    EditText expenseTitleEditText, expenseAmountEditText;
    Spinner whoPaidSpinner;
    ListView debtorsListView;
    TextView whoPaidTextView, whoOwesTextView;
    DebtorsAdapter debtorsListAdapter;
    ParticipantsSpinnerAdapter spinnerAdapter;
    CheckBox checkAllCheckBox;

    ArrayList<Person> participants;

//    boolean expenseIsNull;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_expense);

        expenseTitleEditText = (EditText) findViewById(R.id.expenseTitleEditText);
        expenseAmountEditText = (EditText) findViewById(R.id.expenseAmountEditText);
        whoPaidSpinner = (Spinner) findViewById(R.id.whoPaidSpinner);
        debtorsListView = (ListView) findViewById(R.id.whoOwesListView);
        whoPaidTextView = (TextView) findViewById(R.id.whoPaidTextView);
        whoOwesTextView = (TextView) findViewById(R.id.whoOwesTextView);
//        checkAllCheckBox = (CheckBox) findViewById(R.id.checkAllParticipantsCheckBox);

//        expenseIsNull = (getIntent().getStringExtra("expenseID") == null);


        participants = new ArrayList<>();

        ParseObject object = ParseObject.createWithoutData("Event", getIntent().getStringExtra("eventID"));
        try {
            //get the object with that id from local datastore
            object.fetchFromLocalDatastore();
            JSONArray temp = object.getJSONArray("participants");
            for (int i = 0; i < temp.length(); i++) {

                Gson gson = new Gson();
                Person person = gson.fromJson(String.valueOf(temp.getJSONObject(i)), Person.class);

                participants.add(person);
            }

            //different label later
//            object.unpin("currentEvent");
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }


        //adapter for users (
        spinnerAdapter = new ParticipantsSpinnerAdapter(this, participants);
        whoPaidSpinner.setAdapter(spinnerAdapter);
        whoPaidSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // TODO: Add the code for hiding the one who paid in the listview
//                for (int i=0; i<debtorsListView.getChildCount(); i++){
//                    debtorsListView.getChildAt(i).setBackgroundColor(getResources().getColor(R.color.appBackground));
//                }
                //actually it should be checking the name
//                debtorsListView.getChildAt(position).setBackgroundColor(getResources().getColor(R.color.red));
//                debtorsListView.getChildAt(position).setEnabled(false);
            }


            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        try {
            debtorsListAdapter = new DebtorsAdapter(this, participants);
            debtorsListView.setAdapter(debtorsListAdapter);
        } catch (Exception e) {
            e.printStackTrace();
        }

//        checkAllCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                if (isChecked) {
//                    for (int i = 0; i < debtorsListView.getCount(); i++) {
//                        debtorsListView.setItemChecked(i, true);
//                    }
////                   Log.i("amount",String.valueOf(Double.valueOf(expenseAmountEditText.getText().toString())/debtorsListView.getCheckedItemCount()));
//
//                } else {
//                    for (int i = 0; i < debtorsListView.getCount(); i++) {
//                        debtorsListView.setItemChecked(i, false);
//                    }
//                }
////                debtorsListAdapter.notifyDataSetChanged();
//            }
//        });


//
//        if (!expenseIsNull){
//
//            ParseObject expenseToUpdate = ParseObject.createWithoutData("Expense", getIntent().getStringExtra("expenseID"));
//            expenseToUpdate.fetchFromLocalDatastoreInBackground(new GetCallback<ParseObject>() {
//                @Override
//                public void done(ParseObject object, ParseException e) {
//                    expenseTitleEditText.setText(object.getString("title"));
//                    expenseAmountEditText.setText(String.valueOf(object.getDouble("amount")));
//                }
//            });
//
//
//        }



    }


    public ParseObject createExpense(String eventID, String title, JSONObject whoPaid, double amount, JSONArray debtors) {


        ParseObject expense = new ParseObject("Expense");
        //we need a pointer to Event
        ParseObject event = ParseObject.createWithoutData("Event", eventID);


        expense.put("eventID", event);
        expense.put("title", title);
        expense.put("whoPaid", whoPaid);
        expense.put("amount", amount);
        expense.put("participants", debtors);


        return expense;
    }

    public ArrayList<ParseObject> createDebts(ParseObject expense, String eventID, JSONObject toWhom, int status, JSONArray debtors) {

        ArrayList<ParseObject> debts = new ArrayList<>();
        ParseObject event = ParseObject.createWithoutData("Event", eventID);

        for (int i = 0; i < debtors.length(); i++) {

            ParseObject debt = new ParseObject("Debt");
            debt.put("eventID", event);
            debt.put("expenseID", expense);
            debt.put("title", expense.get("title"));
            debt.put("toWhom", toWhom);
            try {
                debt.put("who", debtors.getJSONObject(i));
                debt.put("status", status);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            //for now debtors length, later on values defined by user
            double amount = expense.getDouble("amount") / (double) debtors.length();
            debt.put("amount", (double) Math.round(amount * 100) / 100 );

            debts.add(debt);

        }

        return debts;
    }


    public void addExpense(View view) {

        Gson gson = new Gson();
        JSONObject whoPaid = null;
        JSONArray debtors = new JSONArray();
        try {
            for (int i = 0; i < debtorsListView.getCount(); i++) {
                if (debtorsListView.isItemChecked(i)) {
                    String fromGson = gson.toJson(debtorsListAdapter.getItem(i));
                    debtors.put(new JSONObject(fromGson));

                }
            }
            Person selectedPerson = (Person)whoPaidSpinner.getSelectedItem();
            String gsonFormat = gson.toJson(selectedPerson);
            whoPaid = new JSONObject(gsonFormat);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        NewObjectUploader expenseUploader = new NewObjectUploader("expense");
        ObjectsArrayUploader debtsUploader = new ObjectsArrayUploader();

        String message = null;

        if (whoPaid != null && debtors != null) {

            ParseObject expense;
            expense = createExpense(getIntent().getStringExtra("eventID"), expenseTitleEditText.getText().toString(), whoPaid, Double.parseDouble(expenseAmountEditText.getText().toString()), debtors);
            ParseObject latestExpense = null;
            try {
                //to be sure if there is only one object pinned
                ParseObject.unpinAll("latestObject");

                message = expenseUploader.execute(expense).get();
                ParseQuery query = ParseQuery.getQuery("Expense");
                query.fromPin("latestObject");
                latestExpense = query.orderByDescending("updatedAt").getFirst();
                latestExpense.unpin("latestObject");
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            } catch (ParseException e) {
                e.printStackTrace();
            }


            ArrayList<ParseObject> debts = null;

//                if (!expenseIsNull){
//
//                    debts = updateDebts(getIntent().getStringExtra("expenseID"), getIntent().getStringExtra("eventID"), whoPaid.getString("name"), debtors);


                debts = createDebts(latestExpense, getIntent().getStringExtra("eventID"), whoPaid, DebtState.DEBT_NOT_PAID, debtors);



            debtsUploader.execute(debts);


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


    }

}
