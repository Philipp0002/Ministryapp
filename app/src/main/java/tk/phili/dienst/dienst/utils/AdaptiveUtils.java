/*
 * Copyright 2021 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package tk.phili.dienst.dienst.utils;

import tk.phili.dienst.dienst.R;

import android.app.Activity;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.navigation.NavigationView.OnNavigationItemSelectedListener;
import com.google.android.material.navigationrail.NavigationRailView;

/** Utility class for the Adaptive package. */
public class AdaptiveUtils {

    static final int MEDIUM_SCREEN_WIDTH_SIZE = 600;
    static final int LARGE_SCREEN_WIDTH_SIZE = 1240;

    private AdaptiveUtils() {}

    /**
     * Updates the visibility of the main navigation view components according to screen size.
     *
     * <p>The small screen layout should have a bottom navigation and optionally a fab. The medium
     * layout should have a navigation rail with a fab, and the large layout should have a navigation
     * drawer with an extended fab.
     */
    public static void updateNavigationViewLayout(
            int screenWidth,
            @NonNull DrawerLayout drawerLayout,
            @NonNull NavigationView modalNavDrawer,
            @Nullable FloatingActionButton fab,
            @NonNull NavigationRailView navRail,
            @NonNull NavigationView navDrawer,
            @NonNull Toolbar toolbar,
            @NonNull Activity activity) {

        setNavRailButtonOnClickListener(
                drawerLayout, navRail.getHeaderView().findViewById(R.id.nav_button), modalNavDrawer);

        modalNavDrawer.setNavigationItemSelectedListener(
                item -> {
                    modalNavDrawer.setCheckedItem(item);
                    drawerLayout.closeDrawer(modalNavDrawer);
                    return true;
                });

        if (screenWidth < AdaptiveUtils.MEDIUM_SCREEN_WIDTH_SIZE) {
            // Small screen
            if (fab != null) {
                fab.setVisibility(View.VISIBLE);
            }
            navRail.setVisibility(View.GONE);
            navDrawer.setVisibility(View.GONE);

            ActionBarDrawerToggle actionBarDrawerToggle =
                    new ActionBarDrawerToggle(activity, drawerLayout, toolbar, R.string.openDrawer, R.string.closeDrawer) {

                        @Override
                        public void onDrawerClosed(View drawerView) {
                            super.onDrawerClosed(drawerView);
                        }

                        @Override
                        public void onDrawerOpened(View drawerView) {
                            super.onDrawerOpened(drawerView);
                        }
                    };
            drawerLayout.addDrawerListener(actionBarDrawerToggle);
            actionBarDrawerToggle.syncState();
            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
            Toast.makeText(drawerLayout.getContext(), "SML", Toast.LENGTH_SHORT).show();
        } else if (screenWidth < AdaptiveUtils.LARGE_SCREEN_WIDTH_SIZE) {
            // Medium screen
            if (fab != null) {
                fab.setVisibility(View.GONE);
            }
            navRail.setVisibility(View.VISIBLE);
            navDrawer.setVisibility(View.GONE);

            // Set navigation menu button to show a modal navigation drawer in medium screens.
            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        } else {
            // Large screen
            if (fab != null) {
                fab.setVisibility(View.GONE);
            }
            navRail.setVisibility(View.GONE);
            navDrawer.setVisibility(View.VISIBLE);

            // Set navigation menu button to show a modal navigation drawer in medium screens.
            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        }
    }

    /* Sets navigation rail's header button to open the modal navigation drawer. */
    private static void setNavRailButtonOnClickListener(
            @NonNull DrawerLayout drawerLayout,
            @NonNull View navButton,
            @NonNull NavigationView modalDrawer) {
        navButton.setOnClickListener(
                new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        drawerLayout.openDrawer(modalDrawer);
                    }
                });
    }

    /* Sets modal navigation drawer's header button to close the drawer. */
    private static void setModalDrawerButtonOnClickListener(
            @NonNull DrawerLayout drawerLayout,
            @NonNull View button,
            @NonNull NavigationView modalDrawer) {
        button.setOnClickListener(
                new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        drawerLayout.closeDrawer(modalDrawer);
                    }
                });
    }
}