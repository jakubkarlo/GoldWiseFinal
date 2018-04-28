package jakubkarlo.com.goldwise.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import jakubkarlo.com.goldwise.R;


public class SignUpActivity extends AppCompatActivity {


    public void signUserUp(View view){

        String nickname, password, repeatPassword, phoneNumber;
        EditText nickNameEditText, passwordEditText, repeatPasswordEditText, phoneNumberEditText;


        nickNameEditText = (EditText)findViewById(R.id.signUpUsernameEditText);
        passwordEditText = (EditText)findViewById(R.id.signUpPasswordEditText);
        repeatPasswordEditText = (EditText)findViewById(R.id.signUpRepeatPasswordEditText);
        phoneNumberEditText = (EditText)findViewById(R.id.signUpPhoneNumEditText);

        nickname = nickNameEditText.getText().toString();
        password = passwordEditText.getText().toString();
        repeatPassword = repeatPasswordEditText.getText().toString();
        phoneNumber = phoneNumberEditText.getText().toString();


        if (!nickname.equals("") || !password.equals("") || !repeatPassword.equals("") || !phoneNumber.equals("")){

            if(repeatPassword.equals(password)) {
                signUp(nickname, phoneNumber, password);
            }

            else {
                Toast.makeText(getApplicationContext(), R.string.passwordsDontMatch_toast, Toast.LENGTH_LONG).show();
            }

        }

        else{
            Toast.makeText(getApplicationContext(), R.string.provideData_toast, Toast.LENGTH_LONG).show();
        }

    }


    public void signUp(String nickname, String phoneNumber, String password){
        ParseUser user = new ParseUser();
        user.put("nickname", nickname);
        user.put("password", password);
        user.put("username", phoneNumber);

        user.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(ParseException e) {

                if (e == null){
                    Toast.makeText(getApplicationContext(), R.string.signUpSuccessful_toast, Toast.LENGTH_SHORT).show();
                }
                else{

                    Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
                    e.printStackTrace();

                }

            }
        });
    }


    public void goBack(){

        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                finish();
            }
        });

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
    }
}
