package plantholder.application;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class CreateAccount extends AppCompatActivity {

    private Firebase firebase;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_user_window);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        getWindow().getDecorView().getWindowInsetsController().hide(
                android.view.WindowInsets.Type.statusBars()
        );

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
                if(password.equals(confirmPass)){
                    firebase.writeNewUser(user, email, password);
                    Intent intent = new Intent(CreateAccount.this, HomeScreen.class);
                    startActivity(intent);
                }else{
                    Toast.makeText(CreateAccount.this, "Wrong password", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
