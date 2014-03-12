/*
 * Created by Angel Leon (@gubatron), Alden Torres (aldenml)
 * Copyright (c) 2011-2013, FrostWire(R). All rights reserved.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.frostwire.android.gui.activities;

import java.lang.ref.WeakReference;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import com.frostwire.android.R;
import com.frostwire.android.gui.views.AbstractActivity2;
import com.frostwire.util.Ref;

/**
 * 
 * @author gubatron
 * @author aldenml
 *
 */
public final class MainActivity2 extends AbstractActivity2 {

    private DrawerLayout drawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle drawerToggle;

    public MainActivity2() {
        super(R.layout.activity_main2);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
    
        switch (item.getItemId()) {
        //        case R.id.action_websearch:
        //            // create intent to perform web search for this planet
        //            Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
        //            intent.putExtra(SearchManager.QUERY, getActionBar().getTitle());
        //            // catch event that there's no activity to handle intent
        //            if (intent.resolveActivity(getPackageManager()) != null) {
        //                startActivity(intent);
        //            } else {
        //                Toast.makeText(this, R.string.app_not_available, Toast.LENGTH_LONG).show();
        //            }
        //            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    protected void initComponents(Bundle savedInstanceState) {

        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);

        setupDrawer();

        if (savedInstanceState == null) {
            //selectItem(0);
        }
    }

    private void setupDrawer() {
        //mTitle = mDrawerTitle = getTitle();
        //mPlanetTitles = getResources().getStringArray(R.array.planets_array);
        drawerLayout = (DrawerLayout) findViewById(R.id.activity_main_drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        // set up the drawer's list view with items and click listener
        //mDrawerList.setAdapter(new ArrayAdapter<String>(this,
        //        R.layout.drawer_list_item, mPlanetTitles));
        //mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        drawerToggle = new MenuDrawerToggle(this, drawerLayout);
        drawerLayout.setDrawerListener(drawerToggle);
    }

    private static final class MenuDrawerToggle extends ActionBarDrawerToggle {

        private WeakReference<MainActivity2> activityRef;

        public MenuDrawerToggle(MainActivity2 activity, DrawerLayout drawerLayout) {
            super(activity, drawerLayout, R.drawable.ic_drawer, R.string.drawer_open, R.string.drawer_close);

            // aldenml: even if the parent class hold a strong reference, I decided to keep a weak one
            this.activityRef = Ref.weak(activity);
        }

        public void onDrawerClosed(View view) {
            if (activityRef.get() != null) {
                //getActionBar().setTitle(mTitle);
                activityRef.get().invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        }

        public void onDrawerOpened(View drawerView) {
            if (activityRef.get() != null) {
                //getActionBar().setTitle(mDrawerTitle);
                activityRef.get().invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        }

    }
}
