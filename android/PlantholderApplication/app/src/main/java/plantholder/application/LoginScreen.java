package plantholder.application;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class LoginScreen extends AppCompatActivity {

    private boolean passwordVisible = false;
    private Firebase firebase;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference firebaseReference;
    private EditText passwordText;
    private EditText emailText;
    private static final int SNOW = Color.parseColor("#FFFAFA");

    @RequiresApi(api = Build.VERSION_CODES.R)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_screen);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        firebase = new Firebase();
        firebaseDatabase =  FirebaseDatabase.getInstance();

        //Hide decor view
        getWindow().getDecorView().getWindowInsetsController().hide(android.view.WindowInsets.Type.statusBars());

        //All buttons initialization
        Button login = findViewById(R.id.login);
        emailText = findViewById(R.id.editTextTextEmailAddress);
        passwordText = findViewById(R.id.editTextTextPassword);
        CheckBox rememberMe = findViewById(R.id.checkBox);
        Button join = findViewById(R.id.joinbutton);
        Button forgotPass = findViewById(R.id.forgotPasswordText);

        //Set password to the password box that comes from the forgotten password screen.
        String message = getIntent().getStringExtra("password");
        passwordText.setText(message);

        //Set user name to the email box that comes from the forgotten password screen.
        String message1 = getIntent().getStringExtra("userName");
        emailText.setText(message1);

        //method for hiding and showing password when clicking on the view toggle
        passwordText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                final int Right = 2;
                if (motionEvent.getAction()== MotionEvent.ACTION_UP){
                    if(motionEvent.getRawX()>=passwordText.getRight()-passwordText.getCompoundDrawables()[Right].getBounds().width()){
                        int selection = passwordText.getSelectionEnd();
                        if(passwordVisible){
                            //set drawable image
                            passwordText.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,R.drawable.ic_baseline_visibility_off_24, 0);
                            //hide password
                            passwordText.setTransformationMethod(PasswordTransformationMethod.getInstance());
                            passwordVisible = false;
                        }else{
                            //set drawable image
                            passwordText.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,R.drawable.ic_baseline_visibility_24, 0);
                            //showing password
                            passwordText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                            passwordVisible = true;
                        }
                        passwordText.setSelection(selection);
                        return true;
                    }
                }
                return false;
            }
        });

        //Remember me check box, enter the default user name and password for testing
        rememberMe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.setSelected(!view.isSelected());
                if(view.isSelected()){
                    emailText.setText("Testuser");
                    passwordText.setText("test");
                }else{
                    emailText.setText("");
                    passwordText.setText("");
                }
            }
        });

        //login button that checks if password and/or email textfields are empty, if they are shows a popup.
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String usernameEntered = emailText.getText().toString();
                String enteredPassword = passwordText.getText().toString();
                firebaseReference = firebaseDatabase.getReference("Users").child(usernameEntered).child("password");

                if (usernameEntered.isEmpty() || enteredPassword.isEmpty()) {
                    Toast.makeText(LoginScreen.this, "Please enter email and password", Toast.LENGTH_SHORT).show();
                    return;
                }
                verifyUserCredentials(enteredPassword);
            }
        });

       //Button for joining as a new user
       join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.setBackgroundColor(SNOW);
                Intent intent = new Intent(LoginScreen.this, CreateAccount.class);
                startActivity(intent);
            }
        });

       //Button that sends to the page where the user can retrieve a forgotten password
        forgotPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.setBackgroundColor(SNOW);
                Intent intent = new Intent(LoginScreen.this, ForgotPassword.class);
                startActivity(intent);
            }
        });
    }

    //Verify the user name entered and the password exist in the database
    private void verifyUserCredentials(String password) {
        firebaseReference.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String value = snapshot.getValue(String.class);
                if (Objects.equals(value, password)) {
                    Intent intent = new Intent(LoginScreen.this, HomeScreen.class);
                    startActivity(intent);
                    passwordText.setText("");
                } else {
                    Toast.makeText(LoginScreen.this, "Wrong email or password", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(LoginScreen.this, "Fail to get data", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
