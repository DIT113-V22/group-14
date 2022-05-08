package platis.solutions.smartcarmqttcontroller;

import android.app.Activity;
import android.os.Bundle;
import android.util.DisplayMetrics;

public class Pop extends Activity {

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.popwindow);
        DisplayMetrics displayMetricsVar = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetricsVar);

        int width = displayMetricsVar.widthPixels;
        int height = displayMetricsVar.heightPixels;

        getWindow().setLayout((int)(width*.4), (int)(height*.35));
    }

}
