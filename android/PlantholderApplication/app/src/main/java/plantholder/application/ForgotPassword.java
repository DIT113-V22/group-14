package plantholder.application;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class ForgotPassword extends AppCompatActivity {

    FirebaseDatabase firebaseDatabase;
    DatabaseReference firebaseReference;
    TextView userName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forgot_password_screen);

        getWindow().getDecorView().getWindowInsetsController().hide(android.view.WindowInsets.Type.statusBars());

        firebaseDatabase =  FirebaseDatabase.getInstance();

        ImageButton closeIcon = findViewById(R.id.closeButton);
        Button sendPass = findViewById(R.id.sendPassword);
        userName = findViewById(R.id.userName);

        //Close page icon
        closeIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ForgotPassword.this, LoginScreen.class);
                startActivity(intent);
            }
        });

        sendPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String usernameEntered = userName.getText().toString();
                firebaseReference = firebaseDatabase.getReference("Users").child(usernameEntered).child("password");
                retrivePassword();
            }
        });

    }

    //Check if the user is existent and retrieve the password. If not display a message
    public void retrivePassword(){
        firebaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String value = snapshot.getValue(String.class);
                if(snapshot.exists()){
                    Intent intent = new Intent(ForgotPassword.this, LoginScreen.class);
                    intent.putExtra("password", value);
                    startActivity(intent);
                    userName.setText(" ");
                }else{
                    Toast.makeText(ForgotPassword.this, "Enter a valid user name", Toast.LENGTH_SHORT).show();
                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ForgotPassword.this, "Fail to get data", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
