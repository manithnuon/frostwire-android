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

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.frostwire.android.R;
import com.frostwire.android.gui.fragments.AboutFragment2;
import com.frostwire.android.gui.fragments.SearchFragment2;
import com.frostwire.android.gui.mainmenu.XmlMenuAdapter;
import com.frostwire.android.gui.mainmenu.XmlMenuItem;
import com.frostwire.android.gui.views.AbstractActivity2;
import com.frostwire.util.Ref;

/**
 * 
 * @author gubatron
 * @author aldenml
 *
 */
public final class MainActivity2 extends AbstractActivity2 {

    private ListView drawerList;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;

    private int lastSelectedItem = -1;

    public MainActivity2() {
        super(R.layout.activity_main2);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        switch (item.getItemId()) {
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
            selectItem(0, false);
        }
    }

    private void setupDrawer() {
        drawerList = findView(R.id.activity_main_left_drawer);
        drawerList.setAdapter(new XmlMenuAdapter(this));
        drawerList.setOnItemClickListener(new MenuItemClickListener(this));

        drawerLayout = findView(R.id.activity_main_drawer_layout);
        drawerToggle = new MenuDrawerToggle(this, drawerLayout);
        drawerLayout.setDrawerListener(drawerToggle);
    }

    private void selectItem(int position, boolean stack) {
        if (lastSelectedItem != position) {
            lastSelectedItem = position;

            XmlMenuItem menuItem = (XmlMenuItem) drawerList.getItemAtPosition(position);
            Fragment f = createFragmentByMenuId(menuItem.id);
            FragmentTransaction t = getFragmentManager().beginTransaction();
            t.replace(R.id.activity_main_content_frame, f);

            if (stack && t.isAddToBackStackAllowed()) {
                t.addToBackStack(null);
            }

            t.commit();

            drawerList.setItemChecked(position, true);
        }

        drawerLayout.closeDrawer(drawerList);
    }

    private Fragment createFragmentByMenuId(int id) {
        switch (id) {
        case R.id.menu_main_search:
            return new SearchFragment2();
            //        case R.id.menu_main_library:
            //            return library;
            //        case R.id.menu_main_transfers:
            //            return transfers;
            //        case R.id.menu_main_peers:
            //            return getWifiSharingFragment();
        case R.id.menu_main_about:
            return new AboutFragment2();
        default:
            throw new RuntimeException("No fragment for the menu id");
        }
    }

    private static final class MenuDrawerToggle extends ActionBarDrawerToggle {

        private final WeakReference<MainActivity2> activityRef;

        public MenuDrawerToggle(MainActivity2 activity, DrawerLayout drawerLayout) {
            super(activity, drawerLayout, R.drawable.ic_drawer, R.string.drawer_open, R.string.drawer_close);

            // aldenml: even if the parent class hold a strong reference, I decided to keep a weak one
            this.activityRef = Ref.weak(activity);
        }

        public void onDrawerClosed(View view) {
            if (activityRef.get() != null) {
                activityRef.get().invalidateOptionsMenu();
            }
        }

        public void onDrawerOpened(View drawerView) {
            if (activityRef.get() != null) {
                activityRef.get().invalidateOptionsMenu();
            }
        }
    }

    private static final class MenuItemClickListener implements ListView.OnItemClickListener {

        private final WeakReference<MainActivity2> activityRef;

        public MenuItemClickListener(MainActivity2 activity) {
            this.activityRef = Ref.weak(activity);
        }

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (activityRef.get() != null) {
                activityRef.get().selectItem(position, true);
            }
        }
    }
}
