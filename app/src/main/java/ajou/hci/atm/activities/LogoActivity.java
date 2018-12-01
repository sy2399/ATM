package ajou.hci.atm.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;
import ajou.hci.atm.R;

public class LogoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logo);

        try{
            Handler hand = new Handler();

            hand.postDelayed(new Runnable() {

                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(i);
                    // finish();

                }
            }, 2000);
        }catch (Exception e){
            Toast.makeText(getApplicationContext(), e.getMessage()+ "", Toast.LENGTH_LONG).show();
        }
    }
}
