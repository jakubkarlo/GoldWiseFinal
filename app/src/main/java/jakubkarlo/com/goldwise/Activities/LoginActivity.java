package jakubkarlo.com.goldwise.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

import jakubkarlo.com.goldwise.R;

public class LoginActivity extends AppCompatActivity {


    public void goToSignUp(View view){

        Intent intent = new Intent(getApplicationContext(), SignUpActivity.class);
        startActivity(intent);

    }

    public void logUser(View view){

        String phoneNumber, password;
        EditText phoneNumberEditText, passwordEditText;
        phoneNumberEditText = (EditText)findViewById(R.id.loginUsernameTextField);
        passwordEditText = (EditText)findViewById(R.id.loginPasswordTextField);
        phoneNumber = phoneNumberEditText.getText().toString();
        password = passwordEditText.getText().toString();

        if (!phoneNumber.equals("") || !password.equals("")){

            logIn(phoneNumber,password);

        }

        else{
            Toast.makeText(getApplicationContext(), R.string.provideData_toast, Toast.LENGTH_LONG).show();
        }


    }



    public void logIn(String phoneNumber, String password){
        ParseUser.logInInBackground(phoneNumber, password, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {


                if (e == null){
                    Toast.makeText(getApplicationContext(), R.string.loginSuccessful_toast, Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(LoginActivity.this, EventsActivity.class);
                    startActivity(intent);
                }
                else{
                    //add types of errors
                    String error = e.toString();
                    int index = error.lastIndexOf(":");
                    error = error.substring(index +1);
                    Toast.makeText(getApplicationContext(), error, Toast.LENGTH_SHORT).show();


                }

            }
        });
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }
}
