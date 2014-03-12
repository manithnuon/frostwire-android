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

package com.frostwire.android.gui.fragments;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

import org.apache.commons.io.IOUtils;

import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.TextView;

import com.frostwire.android.R;
import com.frostwire.android.core.Constants;
import com.frostwire.android.gui.views.AbstractFragment2;

/**
 * @author gubatron
 * @author aldenml
 * 
 */
public class AboutFragment2 extends AbstractFragment2 {

    public AboutFragment2() {
        super(R.layout.fragment_about2);
    }

    @Override
    protected void initComponents(View rootView) {
        TextView title = findView(rootView, R.id.fragment_about_title);
        title.setText("FrostWire v" + Constants.FROSTWIRE_VERSION_STRING + " build " + Constants.FROSTWIRE_BUILD);

        TextView content = findView(rootView, R.id.fragment_about_content);
        content.setText(Html.fromHtml(getAboutText()));
        content.setMovementMethod(LinkMovementMethod.getInstance());
    }

    private String getAboutText() {
        InputStream raw = null;

        try {
            raw = getResources().openRawResource(R.raw.about);
            return IOUtils.toString(raw, Charset.forName("UTF-8"));
        } catch (IOException e) {
            return "";
        } finally {
            IOUtils.closeQuietly(raw);
        }
    }
}