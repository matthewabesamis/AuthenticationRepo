package com.example.authentication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class Kunekt_Main_Home extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private DrawerLayout myDrawer;

    KunektSavedProfile kunektSavedProfile = new KunektSavedProfile();
    KunektProfile kunektProfile = new KunektProfile();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kunekt__main__home);

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(navListener);

        //First Screen Logged In
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new KunektSavedProfile()).commit();

        // TODO Access myDrawer
        myDrawer = findViewById(R.id.drawer_layout);

        // TODO Access the ActionBar, enable "home" icon
        ActionBar myActionBar = getSupportActionBar();
        myActionBar.setDisplayHomeAsUpEnabled(true);

        // TODO set up the callback method for Navigation View
        NavigationView myNavView = findViewById(R.id.nav_view);
        myNavView.setNavigationItemSelectedListener(this);

        // TODO add an ActionBarDrawerToggle element
        ActionBarDrawerToggle myactionbartiggle = new ActionBarDrawerToggle(this, myDrawer,(R.string.open), (R.string.close));
        myDrawer.addDrawerListener(myactionbartiggle);
        myactionbartiggle.syncState();

        Intent intent = getIntent();
        String about = intent.getStringExtra("about");
        String job = intent.getStringExtra("job");
        String company = intent.getStringExtra("company");
        String school = intent.getStringExtra("school");

        Bundle bundle = new Bundle();
        bundle.putString("about",about);
        bundle.putString("job",job);
        bundle.putString("company",company);
        bundle.putString("school",school);

        KunektSavedProfile savedProfile = new KunektSavedProfile();
        savedProfile.setArguments(bundle);
    }

    // Respond to Navigation Drawer item selected
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        // Show visual for selection
        item.setChecked(true);

        // Close the Drawer
        myDrawer.closeDrawers();

        switch (item.getItemId()) {
            case R.id.nav_exit:
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(Kunekt_Main_Home.this, MainActivity.class));
                break;
        }


        return false;
    }

    // This will respond to selections in the ActionBar
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Find out the current state of the drawer (open or closed)
        boolean isOpen = myDrawer.isDrawerOpen(GravityCompat.START);

        // Handle item selection
        switch (item.getItemId()) {
            case android.R.id.home:
                // Home button - open or close the drawer
                if (isOpen == true) {
                    myDrawer.closeDrawer(GravityCompat.START);
                } else {
                    myDrawer.openDrawer(GravityCompat.START);
                }
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                    Fragment selectedFragment = null;

                    switch ((menuItem.getItemId()))
                    {
                        case R.id.nav_home:
                            selectedFragment = new KunektSavedProfile();
                            break;
                        case R.id.nav_messages:
                            selectedFragment = new KunektMessages();
                            break;
                        case R.id.nav_map:
                            selectedFragment = new KunektMap();
                            break;
                    }
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();

                    return true;
                }
            };
}
