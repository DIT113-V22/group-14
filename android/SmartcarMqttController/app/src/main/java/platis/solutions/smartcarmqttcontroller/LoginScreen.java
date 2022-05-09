package platis.solutions.smartcarmqttcontroller;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;

public class LoginScreen extends AppCompatActivity {

    boolean passwordVisible;

    @RequiresApi(api = Build.VERSION_CODES.R)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_screen);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        getWindow().getDecorView().getWindowInsetsController().hide(
                android.view.WindowInsets.Type.statusBars()
        );

        Button login = findViewById(R.id.login);
        EditText emailText = (EditText) findViewById(R.id.editTextTextEmailAddress);
        EditText passwordText = (EditText) findViewById(R.id.editTextTextPassword);

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
                            //set deawable image
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

            //login button that checks if password and/or email textfields are empty, if they are shows a popup.
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (emailText.getText().toString().isEmpty() || passwordText.getText().toString().isEmpty()) {
                    startActivity(new Intent(LoginScreen.this,Pop.class));
                } else {
                    Intent intent = new Intent(LoginScreen.this, HomeScreen.class);
                    startActivity(intent);
                    passwordText.setText("");
                }
            }
        });


        Button exit = findViewById(R.id.exit);
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                System.exit(0);
            }
        });
    }
}
