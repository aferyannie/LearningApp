package com.example.aferyannie.learningapp;

import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    private DrawerLayout drawer;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /** Initialize Firebase Auth. */
        mAuth = FirebaseAuth.getInstance();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View headerView = navigationView.getHeaderView(0);

        drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu() -> redraw the menu.
            }
            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                loadUserInformation();
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu() -> redraw the menu.
            }
        };
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        // Set home screen.
        if(savedInstanceState == null){
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new HomeActivity()).commit();
        }

    }

    public void loadUserInformation(){
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        CircleImageView imageView = this.findViewById(R.id.displaypicture);
        TextView nickname = this.findViewById(R.id.nickname);
        TextView email = this.findViewById(R.id.email);

        if(currentUser != null) {
            nickname.setText(currentUser.getDisplayName());
            email.setText(currentUser.getEmail());
            /** Load facebook display picture using Picasso library. */
            Picasso.get()
                .load(currentUser.getPhotoUrl().toString())
                .resize(65, 65)
                .centerCrop()
                .into(imageView);
        }
    }
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            // NavigationBar HomeScreen.
            case R.id.nav_home:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new HomeActivity()).commit();
                break;
            // NavigationBar LoginLogout.
            case R.id.nav_login_logout:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new LoginActivity()).commit();
                break;
            // NavigationBar Scoreboard.
            case R.id.nav_scoreboard:
                showFragment(new ScoreboardActivity(),R.id.fragment_container);
                break;
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    public void showFragment(Fragment fragment, int fragmentResourceID) {
        if (fragment != null) {
            FragmentManager fragmentManager = this.getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(fragmentResourceID, fragment);
            fragmentTransaction.detach(fragment);
            fragmentTransaction.attach(fragment);
            fragmentTransaction.commit();
        }
    }

}
