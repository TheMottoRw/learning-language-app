package com.example.cyberawareness;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.learn.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class AdminActivity extends AppCompatActivity {
    private FloatingActionButton fab;

    private BottomNavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_admin);
        navigationView = findViewById(R.id.navigation);
        fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AdminActivity.this, AddLevel.class));
            }
        });
        getSupportFragmentManager().beginTransaction().add(R.id.frameLayout, new LearnersFragment()).commit();


        navigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.action_learners) {
                    navigationView.getMenu().getItem(0).setChecked(true);
                    fab.setVisibility(View.GONE);
                    getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new LearnersFragment()).commit();
                }
                if (itemId == R.id.action_levels) {
                    navigationView.getMenu().getItem(1).setChecked(true);
                    fab.setVisibility(View.VISIBLE);
                    getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new LevelsFragment()).commit();
                }
                if (itemId == R.id.action_report) {
                    navigationView.getMenu().getItem(2).setChecked(true);
                    fab.setVisibility(View.GONE);
                    getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new ReportFragment()).commit();
                }
                if (itemId == R.id.action_logout) {
                    Utils.logout(AdminActivity.this);
                    finish();
                    startActivity(new Intent(AdminActivity.this, Login.class));
                    return true;
                }

                return false;
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        Toast.makeText(AdminActivity.this, "Logout clicked", Toast.LENGTH_SHORT).show();

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        navigationView.getMenu().getItem(1).setChecked(true);
        fab.setVisibility(View.VISIBLE);
        getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new LevelsFragment()).commit();

    }

}