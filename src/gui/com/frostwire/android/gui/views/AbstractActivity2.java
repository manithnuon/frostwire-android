/*
 * Created by Angel Leon (@gubatron), Alden Torres (aldenml)
 * Copyright (c) 2011-2014, FrostWire(R). All rights reserved.
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

package com.frostwire.android.gui.views;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.Window;

/**
 * 
 * 
 * @author gubatron
 * @author aldenml
 * 
 */
public abstract class AbstractActivity2 extends FragmentActivity {

    private final int layoutResId;
    private final boolean title;

    public AbstractActivity2(int layoutResId, boolean title) {
        this.layoutResId = layoutResId;
        this.title = title;
    }

    public AbstractActivity2(int layoutResID) {
        this(layoutResID, false);
    }

    @Override
    protected void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);

        if (!title) {
            requestWindowFeature(Window.FEATURE_NO_TITLE);
        }

        if (layoutResId != 0) {
            setContentView(layoutResId);
            initComponents();
        }
    }

    @SuppressWarnings("unchecked")
    protected final <T extends View> T findView(int id) {
        return (T) super.findViewById(id);
    }

    protected abstract void initComponents();
}
