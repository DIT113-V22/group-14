package plantholder.application;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class CreateAccount extends AppCompatActivity {

    private Firebase firebase;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_user_window);

        getWindow().getDecorView().getWindowInsetsController().hide(android.view.WindowInsets.Type.statusBars());

        firebase = new Firebase();

        Button createAccount = findViewById(R.id.createAccount);
        EditText newUser = findViewById(R.id.newUserName);
        EditText newEmail = findViewById(R.id.newEmail);
        EditText newPassword = findViewById(R.id.newPassword);
        EditText confirmPassword = findViewById(R.id.confirmPassword);
        ImageButton closeIcon = findViewById(R.id.closeButton);

        closeIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CreateAccount.this, LoginScreen.class);
                startActivity(intent);
            }
        });

        createAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String user = newUser.getText().toString();
                String email = newEmail.getText().toString();
                String password = newPassword.getText().toString();
                String confirmPass = confirmPassword.getText().toString();

                if (password.equals(confirmPass) ){
                    createUser(user, email, password);
                }else{
                    Toast.makeText(CreateAccount.this, "Password entered and confirmed are not the same", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void createUser(String user, String email, String password) {
        DatabaseReference plantRef = FirebaseDatabase.getInstance().getReference("Users/" + user);
        plantRef.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Toast.makeText(CreateAccount.this, "Username already exists", Toast.LENGTH_SHORT).show();
                } else {
                    firebase.writeNewUser(user, email, password);
                    Intent intent = new Intent(CreateAccount.this, HomeScreen.class);
                    startActivity(intent);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
            }
        });
    }
}