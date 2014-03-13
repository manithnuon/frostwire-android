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
import android.content.res.TypedArray;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.ReplacementTransformationMethod;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.RelativeLayout;

import com.frostwire.android.R;
import com.frostwire.util.Ref;

/**
 * @author gubatron
 * @author aldenml
 *
 */
public class ClearableEditTextView extends RelativeLayout {

    private AutoCompleteTextView input;
    private ImageView imageSearch;
    private ImageButton buttonClear;

    private OnActionListener listener;
    private String hint;

    public ClearableEditTextView(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray arr = context.obtainStyledAttributes(attrs, R.styleable.ClearableEditTextView);
        hint = arr.getString(R.styleable.ClearableEditTextView_clearable_hint);
        arr.recycle();
    }

    public OnActionListener getOnActionListener() {
        return listener;
    }

    public void setOnActionListener(OnActionListener listener) {
        this.listener = listener;
    }

    @Override
    public OnFocusChangeListener getOnFocusChangeListener() {
        return input.getOnFocusChangeListener();
    }

    @Override
    public void setOnFocusChangeListener(OnFocusChangeListener l) {
        input.setOnFocusChangeListener(l);
    }

    @Override
    public void setOnKeyListener(OnKeyListener l) {
        input.setOnKeyListener(l);
    }

    public OnItemClickListener getOnItemClickListener() {
        return input.getOnItemClickListener();
    }

    public void setOnItemClickListener(OnItemClickListener l) {
        input.setOnItemClickListener(l);
    }

    public <T extends ListAdapter & Filterable> void setAdapter(T adapter) {
        input.setAdapter(adapter);
    }

    public String getText() {
        return input.getText().toString();
    }

    public void setText(String text) {
        input.setText(text);
    }

    public int getListSelection() {
        return input.getListSelection();
    }

    public void setListSelection(int position) {
        input.setListSelection(position);
    }

    public void dismissDropDown() {
        input.dismissDropDown();
    }

    public void selectAll() {
        input.selectAll();
    }

    public String getHint() {
        return (String) input.getHint();
    }

    public void setHint(String hint) {
        input.setHint(hint);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        View.inflate(getContext(), R.layout.view_clearable_edittext, this);

        if (isInEditMode()) {
            return;
        }

        input = (AutoCompleteTextView) findViewById(R.id.view_clearable_edit_text_input);
        input.setHint(hint);
        input.setTransformationMethod(new SingleLineTransformationMethod());
        input.addTextChangedListener(new InputTextWatcher(this));

        // workaround for android issue: http://code.google.com/p/android/issues/detail?id=2516
        // the comment http://code.google.com/p/android/issues/detail?id=2516#c9
        // seems a little overkill for this situation, but it could work for a general situation.
        input.setOnTouchListener(new InputTouchListener(input));

        imageSearch = (ImageView) findViewById(R.id.view_clearable_edit_text_image_search);
        imageSearch.setVisibility(RelativeLayout.VISIBLE);

        buttonClear = (ImageButton) findViewById(R.id.view_clearable_edit_text_button_clear);
        buttonClear.setVisibility(RelativeLayout.GONE);
        buttonClear.setOnClickListener(new ClearClickListener(this));
    }

    private void onTextChanged(String str) {
        if (listener != null) {
            listener.onTextChanged(this, str.trim());
        }
    }

    private void onClear() {
        if (listener != null) {
            listener.onClear(this);
        }
    }

    private void setImageSearchVisibility(int visibility) {
        imageSearch.setVisibility(visibility);
    }

    private void setButtonClearVisibility(int visibility) {
        buttonClear.setVisibility(visibility);
    }

    public static interface OnActionListener {

        public void onTextChanged(ClearableEditTextView v, String str);

        public void onClear(ClearableEditTextView v);
    }

    private static final class SingleLineTransformationMethod extends ReplacementTransformationMethod {

        private static char[] ORIGINAL = { '\n', '\r' };
        private static char[] REPLACEMENT = { '\uFEFF', '\uFEFF' };

        protected char[] getOriginal() {
            return ORIGINAL;
        }

        protected char[] getReplacement() {
            return REPLACEMENT;
        }
    }

    private static final class InputTextWatcher implements TextWatcher {

        private final WeakReference<ClearableEditTextView> viewRef;

        public InputTextWatcher(ClearableEditTextView view) {
            this.viewRef = Ref.weak(view);
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (Ref.alive(viewRef)) {
                ClearableEditTextView view = viewRef.get();

                if (s.length() > 0) {
                    view.setImageSearchVisibility(View.GONE);
                    view.setButtonClearVisibility(View.VISIBLE);
                } else {
                    view.setImageSearchVisibility(View.VISIBLE);
                    view.setButtonClearVisibility(View.GONE);
                }
                view.onTextChanged(s.toString());
                view.setListSelection(-1);
            }
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    }

    private static final class InputTouchListener implements OnTouchListener {

        private final WeakReference<AutoCompleteTextView> inputRef;

        public InputTouchListener(AutoCompleteTextView input) {
            this.inputRef = Ref.weak(input);
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (Ref.alive(inputRef)) {
                inputRef.get().requestFocusFromTouch();
            }

            return false;
        }
    }

    private static final class ClearClickListener implements OnClickListener {

        private final WeakReference<ClearableEditTextView> viewRef;

        public ClearClickListener(ClearableEditTextView view) {
            this.viewRef = Ref.weak(view);
        }

        @Override
        public void onClick(View v) {
            if (Ref.alive(viewRef)) {
                viewRef.get().setText("");
                viewRef.get().onClear();
            }
        }
    }
}
