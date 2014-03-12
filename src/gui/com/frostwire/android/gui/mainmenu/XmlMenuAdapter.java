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

package com.frostwire.android.gui.mainmenu;

import java.lang.ref.WeakReference;
import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.frostwire.android.R;
import com.frostwire.util.Ref;

/**
 * 
 * @author gubatron
 * @author aldenml
 *
 */
public final class XmlMenuAdapter extends BaseAdapter {

    private final WeakReference<Context> contextRef;
    private final List<XmlMenuItem> items;

    public XmlMenuAdapter(Context context) {
        this.contextRef = Ref.weak(context);
        this.items = new XmlMenuLoader().load(context);
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public XmlMenuItem getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (contextRef.get() != null) {
            if (convertView == null) {
                convertView = View.inflate(contextRef.get(), R.layout.mainmenu_listitem, null);
            }

            populateView(convertView, getItem(position));
        }

        return convertView;
    }

    private void populateView(View view, XmlMenuItem item) {
        ImageView icon = (ImageView) view.findViewById(R.id.mainmenu_listitem_icon);
        TextView label = (TextView) view.findViewById(R.id.mainmenu_listitem_label);

        icon.setImageResource(item.iconResId);
        label.setText(item.titleResId);
    }
}
