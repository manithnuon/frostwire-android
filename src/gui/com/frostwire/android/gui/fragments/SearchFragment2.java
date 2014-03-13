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

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.frostwire.android.R;
import com.frostwire.android.gui.adapters.SearchResultsAdapter;
import com.frostwire.android.gui.views.AbstractFragment2;
import com.frostwire.android.gui.views.SearchInputView;
import com.frostwire.android.gui.views.SearchInputView.OnSearchListener;

/**
 * @author gubatron
 * @author aldenml
 *
 */
public final class SearchFragment2 extends AbstractFragment2 {

    private SearchInputView searchInput;
    private ProgressBar deepSearchProgress;
    private ListView resultsList;

    private SearchResultsAdapter adapter;

    public SearchFragment2() {
        super(R.layout.fragment_search2);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        adapter = new SearchResultsAdapter(getActivity());
        resultsList.setAdapter(adapter);
    }

    @Override
    protected void initComponents(View rootView) {

        searchInput = findView(rootView, R.id.fragment_search_input);
        searchInput.setOnSearchListener(new SearchInputListener());

        deepSearchProgress = findView(rootView, R.id.fragment_search_deepsearch_progress);
        deepSearchProgress.setVisibility(View.GONE);

        resultsList = findView(rootView, R.id.fragment_search_results_list);
    }

    private static final class SearchInputListener implements OnSearchListener {

        @Override
        public void onSearch(SearchInputView v, String query, int mediaTypeId) {
            //performSearch(query, mediaTypeId);
        }

        @Override
        public void onMediaTypeSelected(SearchInputView v, int mediaTypeId) {
            //adapter.setFileType(mediaTypeId);
            //showSearchView(view);
        }

        @Override
        public void onClear(SearchInputView v) {
            //cancelSearch(view);
        }
    }
}