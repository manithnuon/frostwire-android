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

import java.lang.ref.WeakReference;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.frostwire.android.R;
import com.frostwire.android.core.ConfigurationManager;
import com.frostwire.android.core.Constants;
import com.frostwire.android.gui.views.ClearableEditTextView.OnActionListener;
import com.frostwire.util.Ref;
import com.frostwire.uxstats.UXAction;
import com.frostwire.uxstats.UXStats;

/**
 * @author gubatron
 * @author aldenml
 *
 */
public class SearchInputView extends LinearLayout {

    private final SuggestionsAdapter adapter;

    private ClearableEditTextView textInput;

    private View dummyFocusView;

    private OnSearchListener listener;

    private int mediaTypeId;

    public SearchInputView(Context context, AttributeSet set) {
        super(context, set);

        this.adapter = new SuggestionsAdapter(context);
    }

    public OnSearchListener getOnSearchListener() {
        return listener;
    }

    public void setOnSearchListener(OnSearchListener listener) {
        this.listener = listener;
    }

    public boolean isEmpty() {
        return textInput.getText().length() == 0;
    }

    public String getText() {
        return textInput.getText();
    }

    public boolean isFileTypeCountersVisible() {
        RadioGroup radioGroup = (RadioGroup) findViewById(R.id.view_search_input_radiogroup_file_type);
        return radioGroup.getVisibility() == View.VISIBLE;
    }

    public void setFileTypeCountersVisible(boolean fileTypeCountersVisible) {
        RadioGroup radioGroup = (RadioGroup) findViewById(R.id.view_search_input_radiogroup_file_type);
        radioGroup.setVisibility(fileTypeCountersVisible ? View.VISIBLE : View.GONE);
    }

    public void updateFileTypeCounter(byte fileType, int numFiles) {
        try {
            int radioId = Constants.FILE_TYPE_AUDIO;
            switch (fileType) {
            case Constants.FILE_TYPE_AUDIO:
                radioId = R.id.view_search_input_radio_audio;
                break;
            case Constants.FILE_TYPE_VIDEOS:
                radioId = R.id.view_search_input_radio_videos;
                break;
            case Constants.FILE_TYPE_PICTURES:
                radioId = R.id.view_search_input_radio_pictures;
                break;
            case Constants.FILE_TYPE_APPLICATIONS:
                radioId = R.id.view_search_input_radio_applications;
                break;
            case Constants.FILE_TYPE_DOCUMENTS:
                radioId = R.id.view_search_input_radio_documents;
                break;
            case Constants.FILE_TYPE_TORRENTS:
                radioId = R.id.view_search_input_radio_torrents;
                break;
            }

            RadioButton rButton = (RadioButton) findViewById(radioId);
            String numFilesStr = String.valueOf(numFiles);
            if (numFiles > 9999) {
                numFilesStr = "+1k";
            }
            rButton.setText(numFilesStr);
        } catch (Throwable e) {
            // NPE
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        View.inflate(getContext(), R.layout.view_searchinput, this);

        if (isInEditMode()) {
            return;
        }

        mediaTypeId = ConfigurationManager.instance().getLastMediaTypeFilter();

        TextInputListener textInputListener = new TextInputListener(this);
        textInput = (ClearableEditTextView) findViewById(R.id.view_search_input_text_input);
        textInput.setOnKeyListener(textInputListener);
        textInput.setOnActionListener(textInputListener);
        textInput.setOnItemClickListener(textInputListener);
        textInput.setAdapter(adapter);

        updateHint(mediaTypeId);

        initRadioButton(R.id.view_search_input_radio_audio, Constants.FILE_TYPE_AUDIO);
        initRadioButton(R.id.view_search_input_radio_videos, Constants.FILE_TYPE_VIDEOS);
        initRadioButton(R.id.view_search_input_radio_pictures, Constants.FILE_TYPE_PICTURES);
        initRadioButton(R.id.view_search_input_radio_applications, Constants.FILE_TYPE_APPLICATIONS);
        initRadioButton(R.id.view_search_input_radio_documents, Constants.FILE_TYPE_DOCUMENTS);
        initRadioButton(R.id.view_search_input_radio_torrents, Constants.FILE_TYPE_TORRENTS);

        setFileTypeCountersVisible(false);

        dummyFocusView = findViewById(R.id.view_search_input_linearlayout_dummy);
    }

    private void startSearch(View v) {
        hideSoftInput(v);
        textInput.setListSelection(-1);
        textInput.dismissDropDown();
        adapter.discardLastResult();

        String query = textInput.getText().toString().trim();
        if (query.length() > 0) {
            onSearch(query, mediaTypeId);
        }

        dummyFocusView.requestFocus();
    }

    private void onSearch(String query, int mediaTypeId) {
        if (listener != null) {
            listener.onSearch(this, query, mediaTypeId);
        }
    }

    private void onMediaTypeSelected(int mediaTypeId) {
        if (listener != null) {
            listener.onMediaTypeSelected(this, mediaTypeId);
        }
    }

    private void onClear() {
        if (listener != null) {
            listener.onClear(this);
        }
    }

    private void hideSoftInput(View v) {
        InputMethodManager manager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (manager != null) {
            manager.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
    }

    private void updateHint(int fileType) {
        String hint = getContext().getString(R.string.search_label) + " " + getContext().getString(R.string.files);
        textInput.setHint(hint);
    }

    private RadioButton initRadioButton(int viewId, byte fileType) {
        final RadioButton button = (RadioButton) findViewById(viewId);
        button.setOnClickListener(new RadioButtonClickListener(this, fileType));

        if (mediaTypeId == fileType) {
            button.setChecked(true);
        }

        return button;
    }

    private void radioButtonFileTypeClick(final int mediaTypeId) {
        updateHint(mediaTypeId);
        onMediaTypeSelected(mediaTypeId);

        this.mediaTypeId = mediaTypeId;
        ConfigurationManager.instance().setLastMediaTypeFilter(mediaTypeId);
    }

    public static interface OnSearchListener {

        public void onSearch(SearchInputView v, String query, int mediaTypeId);

        public void onMediaTypeSelected(SearchInputView v, int mediaTypeId);

        public void onClear(SearchInputView v);
    }

    private static final class TextInputListener implements OnKeyListener, OnActionListener, OnItemClickListener {

        private final WeakReference<SearchInputView> viewRef;

        public TextInputListener(SearchInputView view) {
            this.viewRef = Ref.weak(view);
        }

        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            if (Ref.alive(viewRef) && keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_UP) {
                viewRef.get().startSearch(v);
                return true;
            }
            return false;
        }

        @Override
        public void onTextChanged(ClearableEditTextView v, String str) {
        }

        @Override
        public void onClear(ClearableEditTextView v) {
            if (Ref.alive(viewRef)) {
                viewRef.get().onClear();
            }
        }

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (Ref.alive(viewRef)) {
                viewRef.get().startSearch(viewRef.get().textInput);
            }
        }
    }

    private static final class RadioButtonClickListener implements OnClickListener {

        private final WeakReference<SearchInputView> viewRef;
        private final byte fileType;

        public RadioButtonClickListener(SearchInputView view, byte fileType) {
            this.viewRef = Ref.weak(view);
            this.fileType = fileType;
        }

        @Override
        public void onClick(View v) {
            if (Ref.alive(viewRef)) {
                viewRef.get().radioButtonFileTypeClick(fileType);
                UXStats.instance().log(UXAction.SEARCH_RESULT_FILE_TYPE_CLICK);
            }
        }
    }
}