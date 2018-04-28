package jakubkarlo.com.goldwise.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.ParseObject;

import java.util.concurrent.ExecutionException;

import jakubkarlo.com.goldwise.R;
import jakubkarlo.com.goldwise.Uploaders.NewObjectUploader;

public class AddSavingActivity extends AppCompatActivity {

    EditText savingTitleEditText, savingAmountEditText;

    private final double initialState = 0;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_saving);

        savingTitleEditText = (EditText) findViewById(R.id.savingTitleEditText);
        savingAmountEditText = (EditText) findViewById(R.id.savingAmountEditText);

    }


    public void addSaving(View view){

        ParseObject saving = ParseObject.create("Saving");
        ParseObject event = ParseObject.createWithoutData("Event", getIntent().getStringExtra("eventID"));
        //eventID, title, current state, goal

        String title = savingTitleEditText.getText().toString();
        String goalAmount = savingAmountEditText.getText().toString();

        if (goalAmount.equals("") || title.equals("")){
            Toast.makeText(this, "Please enter all the data", Toast.LENGTH_SHORT).show();
        }
        else {
            saving.put("eventID", event);
            saving.put("title", title);
            saving.put("currentState", initialState);
            saving.put("goal", Double.parseDouble(goalAmount));

            NewObjectUploader savingUploader = new NewObjectUploader("saving");

            String message = null;
            try {
                message = savingUploader.execute(saving).get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
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

    }

}
