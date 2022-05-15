package plantholder.application;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class CreateAccount extends AppCompatActivity {

    private Firebase firebase;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference firebaseRef;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_user_window);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        getWindow().getDecorView().getWindowInsetsController().hide(
                android.view.WindowInsets.Type.statusBars()
        );

        firebase = new Firebase();
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseRef = firebaseDatabase.getReference("Users");

        Button createAccount = findViewById(R.id.createAccount);
        EditText newUser = findViewById(R.id.newUserName);
        EditText newEmail = findViewById(R.id.newEmail);
        EditText newPassword = findViewById(R.id.newPassword);

        createAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //String user = newUser.getText().toString();
                //String email = newEmail.getText().toString();
                //String password = newPassword.getText().toString();
                //User users = new User(user, email, password);
                //firebase.writeNewUser(user, email, password);
                //firebaseRef.child(user).setValue(users);

                Intent intent = new Intent(CreateAccount.this, HomeScreen.class);
                startActivity(intent);
            }
        });
    }
}
