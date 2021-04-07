package fr.uavignon.ceri.tp3;

import android.app.Application;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;

import android.view.Menu;
import android.view.MenuItem;

import fr.uavignon.ceri.tp3.data.WeatherRepository;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_update_all) {
            WeatherRepository newRepo = new WeatherRepository(new Application());
            Thread thread = new Thread(){
                public void run(){
                    newRepo.loadWeatherAllCities();
                }
            };
            thread.start();
            Snackbar.make(getWindow().getDecorView().getRootView()
                    , "Interrogation à faire du service web",
                    Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
            return false;
        }

        return super.onOptionsItemSelected(item);
    }
}