package com.example.admin;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.admin.ui.BerandaFragment;
import com.example.admin.ui.PesananFragment;
import com.example.admin.ui.ProfileFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.core.splashscreen.SplashScreen;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private FrameLayout frameLayout;

    private final Handler debounceHandler = new Handler(Looper.getMainLooper());
    private Runnable debounceRunnable;
    private static final long DEBOUNCE_DELAY = 300;
    private int currentFragmentId = -1;

    private final Map<Integer, Fragment> fragmentMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SplashScreen.installSplashScreen(this);
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.main_bottom_nav);
        frameLayout = findViewById(R.id.fragment_container);

        fragmentMap.put(R.id.home, new BerandaFragment());
        fragmentMap.put(R.id.pesanan, new PesananFragment());
//        fragmentMap.put(R.id.profile, new ProfileFragment());

        if (savedInstanceState == null) {
            loadFragment(R.id.home, false);
            currentFragmentId = R.id.home;
            bottomNavigationView.setSelectedItemId(R.id.home);
            updateActiveIcon(R.id.home);
        } else {
            currentFragmentId = savedInstanceState.getInt("CURRENT_FRAGMENT_ID", R.id.home);
            bottomNavigationView.setSelectedItemId(currentFragmentId);
            updateActiveIcon(currentFragmentId);
        }

        bottomNavigationView.setOnItemSelectedListener(this::onNavigationItemSelected);
    }

    private boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == currentFragmentId) {
            return false;
        }

        debounceHandler.removeCallbacks(debounceRunnable);
        debounceRunnable = () -> loadFragment(itemId, true);
        debounceHandler.postDelayed(debounceRunnable, DEBOUNCE_DELAY);

        return true;
    }

    private void loadFragment(int fragmentId, boolean useAnimation) {
        Fragment fragment = fragmentMap.get(fragmentId);
        if (fragment != null && !getSupportFragmentManager().isStateSaved()) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();

            if (useAnimation) {
                setFragmentTransactionAnimation(transaction, fragmentId);
            }

            transaction.replace(R.id.fragment_container, fragment);
            transaction.commit();
            currentFragmentId = fragmentId;
            updateActiveIcon(fragmentId);
        }
    }

    private void setFragmentTransactionAnimation(FragmentTransaction transaction, int newFragmentId) {
        if (newFragmentId > currentFragmentId) {
            transaction.setCustomAnimations(
                    R.anim.slide_in_right,
                    R.anim.slide_out_left,
                    R.anim.slide_in_left,
                    R.anim.slide_out_right
            );
        } else if (newFragmentId < currentFragmentId) {
            transaction.setCustomAnimations(
                    R.anim.slide_in_left,
                    R.anim.slide_out_right,
                    R.anim.slide_in_right,
                    R.anim.slide_out_left
            );
        }
    }

    private void updateActiveIcon(int itemId) {
        bottomNavigationView.getMenu().findItem(R.id.home)
                .setIcon(itemId == R.id.home ? R.drawable.ic_home_fill : R.drawable.ic_home_line);

        bottomNavigationView.getMenu().findItem(R.id.pesanan)
                .setIcon(itemId == R.id.pesanan ? R.drawable.ic_order_fill : R.drawable.ic_order_line);

        bottomNavigationView.getMenu().findItem(R.id.profile)
                .setIcon(itemId == R.id.profile ? R.drawable.ic_user_fill : R.drawable.ic_user_line);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("CURRENT_FRAGMENT_ID", currentFragmentId);
    }
}