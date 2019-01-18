package com.example.aferyannie.learningapp;

import android.media.MediaPlayer;
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
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.shashank.sony.fancytoastlib.FancyToast;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private static final int TIME_INTERVAL = 2000; // # milliseconds, desired time passed between two back presses.
    private long mBackPressed;
    private static final String TAG_AUDIO = "AUDIO_LOG";
    private DrawerLayout drawer;
    protected static MediaPlayer main_menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        main_menu = MediaPlayer.create(this, R.raw.main_menu);
        main_menu.setLooping(true);
        main_menu.start();
        Log.d(TAG_AUDIO, "main_menu:onStart");

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        // Set NavigationBar scoreboard invisible.
        navigationView.getMenu().getItem(2).setVisible(false);
        View headerView = navigationView.getHeaderView(0);
        navigationView.getMenu().getItem(2).setVisible(false);


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
        if (savedInstanceState == null) {
//            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
//                    new HomeFragment()).commit();
            showFragment(new HomeFragment(), null, R.id.fragment_container);
        }
    }

    public void loadUserInformation() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        CircleImageView imageView = this.findViewById(R.id.displaypicture);
        TextView nickname = this.findViewById(R.id.nickname);
        TextView email = this.findViewById(R.id.email);
        NavigationView navigationView = this.findViewById(R.id.nav_view);

        if (currentUser != null) {
            // Set NavigationBar scoreboard visible.
            navigationView.getMenu().getItem(2).setVisible(true);
            email.setVisibility(View.VISIBLE);

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
        switch (item.getItemId()) {
            /** NavigationBar HomeScreen. */
            case R.id.nav_home:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new HomeFragment()).commit();
                break;
            /** NavigationBar LoginLogout. */
            case R.id.nav_login_logout:
//                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
//                        new LoginFragment()).commit();
                showFragment(new LoginFragment(), null, R.id.fragment_container);
                break;
            /** NavigationBar Scoreboard. */
            case R.id.nav_scoreboard:
                showFragment(new ScoreboardFragment(), null, R.id.fragment_container);
//                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
//                        new ScoreboardFragment()).addToBackStack(null).commit();
                break;
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            String fragment = currentFragment.toString();
            String fragmentName = fragment.substring(0, fragment.indexOf("{"));
            Log.i("currentFragment", fragmentName);
            switch (fragmentName) {
                case "HomeFragment":
                case "LoginFragment":
                case "LogoutFragment":
                case "ScoreboardFragment":
                    if (mBackPressed + TIME_INTERVAL > System.currentTimeMillis()) {
                        Log.i("waktu", String.valueOf(System.currentTimeMillis()));
                        Log.i("back", String.valueOf(mBackPressed));
                        Log.i("timeinternal", String.valueOf(TIME_INTERVAL));
                        Log.i("total", String.valueOf(mBackPressed + TIME_INTERVAL));
                        super.onBackPressed();
                        return;
                    } else {
                        FancyToast.makeText(getBaseContext(), "Pencet sekali lagi untuk keluar",
                                FancyToast.LENGTH_SHORT, FancyToast.INFO, false).show();
                    }
                    break;
                default:
                    super.onBackPressed();
                    break;
            }
            mBackPressed = System.currentTimeMillis();
        }
    }

    public void showFragment(Fragment fragment, String backStack, int fragmentResourceID) {
        if (fragment != null) {
            FragmentManager fragmentManager = this.getSupportFragmentManager();
            fragmentManager.popBackStack(backStack, fragmentManager.POP_BACK_STACK_INCLUSIVE);
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(fragmentResourceID, fragment);
            fragmentTransaction.commit();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (main_menu.isPlaying()) {
            main_menu.stop();
            main_menu.release();
            Log.d(TAG_AUDIO, "main_menu:onDestroy");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        main_menu.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (main_menu.isPlaying()) {
            main_menu.pause();
            Log.d(TAG_AUDIO, "main_menu:onPause via Home Screen");
        }
    }


}
