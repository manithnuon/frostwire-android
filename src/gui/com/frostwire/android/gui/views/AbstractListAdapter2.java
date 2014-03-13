/*
 * Created by Angel Leon (@gubatron), Alden Torres (aldenml)
 * Copyright (c) 2011, 2012, FrostWire(TM). All rights reserved.
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.content.Context;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Filter;
import android.widget.Filterable;

import com.frostwire.android.R;
import com.frostwire.util.Ref;

/**
 * We extend from ListAdapter to populate our ListViews.
 * This one allows us to click and long click on the elements of our ListViews.
 * 
 * @author gubatron
 * @author aldenml
 *
 * @param <T>
 */
public abstract class AbstractListAdapter2<T> extends BaseAdapter implements Filterable {

    private static String TAG = "FW.AbstractListAdapter";

    private final WeakReference<Context> contextRef;
    private final int viewItemId;

    private final OnClickListener viewOnClickListener;
    private final ViewOnLongClickListener<T> viewOnLongClickListener;
    private final ViewOnKeyListener<T> viewOnKeyListener;
    private final CheckboxOnCheckedChangeListener<T> checkboxOnCheckedChangeListener;

    private ListAdapterFilter<T> filter;
    private boolean checkboxesVisibility;
    private boolean showMenuOnClick;

    protected List<T> list;
    protected Set<T> checked;
    protected List<T> visualList;

    public AbstractListAdapter2(Context context, int viewItemId, List<T> list, Set<T> checked) {
        this.contextRef = Ref.weak(context);
        this.viewItemId = viewItemId;

        this.viewOnClickListener = new ViewOnClickListener<T>(this);
        this.viewOnLongClickListener = new ViewOnLongClickListener<T>(this);
        this.viewOnKeyListener = new ViewOnKeyListener<T>(this);
        this.checkboxOnCheckedChangeListener = new CheckboxOnCheckedChangeListener<T>(this);

        this.list = list.equals(Collections.emptyList()) ? new ArrayList<T>() : list;
        this.checked = checked;
        this.visualList = list;
    }

    public AbstractListAdapter2(Context context, int viewItemId, List<T> list) {
        this(context, viewItemId, list, new HashSet<T>());
    }

    public AbstractListAdapter2(Context context, int viewItemId) {
        this(context, viewItemId, new ArrayList<T>(), new HashSet<T>());
    }

    public int getViewItemId() {
        return viewItemId;
    }

    public boolean hasStableIds() {
        return true;
    }

    public boolean areAllItemsEnabled() {
        return true;
    }

    public boolean isEnabled(int position) {
        return true;
    }

    public int getItemViewType(int position) {
        return 0;
    }

    public int getViewTypeCount() {
        return 1;
    }

    public boolean isEmpty() {
        return getCount() == 0;
    }

    public Set<T> getChecked() {
        return checked;
    }

    public void clearChecked() {
        if (checked != null && checked.size() > 0) {
            checked.clear();
            notifyDataSetChanged();
        }
    }

    public void checkAll() {
        checked.clear();
        if (visualList != null) {
            checked.addAll(visualList);
        }
        notifyDataSetChanged();
    }

    /** This will return the count for the current file type */
    public int getCount() {
        return visualList == null ? 0 : visualList.size();
    }

    /** Should return the total count for all file types. */
    public int getTotalCount() {
        return list == null ? 0 : list.size();
    }

    public T getItem(int position) {
        return visualList.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public void setList(List<T> list) {
        this.list = list.equals(Collections.emptyList()) ? new ArrayList<T>() : list;
        this.visualList = this.list;
        this.checked.clear();
        notifyDataSetInvalidated();
    }

    public void addList(List<T> g, boolean checked) {
        visualList.addAll(g);
        if (visualList != list) {
            list.addAll(g);
        }
        if (checked) {
            this.checked.addAll(g);
        }
        notifyDataSetChanged();
    }

    /**
     * Adds new results to the existing list.
     * @param g
     */
    public void addList(List<T> g) {
        addList(g, false);
    }

    public void addItem(T item) {
        addItem(item, true);
    }

    public void addItem(T item, boolean visible) {
        if (visible) {
            visualList.add(item);
            if (visualList != list) {
                list.add(item);
            }
        } else {
            if (visualList == list) {
                visualList = new ArrayList<T>(list);
            }
            list.add(item);
        }
        notifyDataSetChanged();
    }

    public void deleteItem(T item) {
        visualList.remove(item);
        if (visualList != list) {
            list.remove(item);
        }
        if (checked.contains(item)) {
            checked.remove(item);
        }
        notifyDataSetChanged();
    }

    public void updateList(List<T> g) {
        list = g;
        visualList = g;
        checked.clear();
        notifyDataSetChanged();
    }

    public void clear() {
        if (list != null) {
            list.clear();
        }
        if (visualList != null) {
            visualList.clear();
        }
        if (checked != null) {
            checked.clear();
        }
        notifyDataSetInvalidated();
    }

    public List<T> getList() {
        return list;
    }

    /** 
     * Inflates the view out of the XML.
     * 
     * Sets click and long click listeners in case you need them. (Override onItemClicked and onItemLongClicked)
     * 
     * Let's the adapter know that the view has been created, in case you need to go deeper and create
     * more advanced click behavior or even add new Views during runtime.
     * 
     * It will also bind the data to the view, you can refer to it if you need it by doing a .getTag()
     * 
     */
    public View getView(int position, View view, ViewGroup parent) {
        if (Ref.alive(contextRef)) {
            T item = getItem(position);

            if (view == null) {
                // every list view item is wrapped in a generic container which has a hidden checkbox on the left hand side.
                view = View.inflate(contextRef.get(), viewItemId, null);
            }

            try {

                initTouchFeedback(view, item);
                initCheckBox(view, item);

                populateView(view, item);

            } catch (Throwable e) {
                Log.e(TAG, "Fatal error getting view: " + e.getMessage(), e);
            }
        }

        return view;
    }

    public Filter getFilter() {
        return new AbstractListAdapterFilter<T>(this, filter);
    }

    /**
     * So that results can be filtered. This discriminator should define which fields of T are the ones eligible for filtering.
     * @param discriminator
     */
    public void setAdapterFilter(ListAdapterFilter<T> filter) {
        this.filter = filter;
    }

    public boolean getCheckboxesVisibility() {
        return checkboxesVisibility;
    }

    public void setCheckboxesVisibility(boolean checkboxesVisibility) {
        this.checkboxesVisibility = checkboxesVisibility;
        notifyDataSetChanged();
    }

    public boolean getShowMenuOnClick() {
        return showMenuOnClick;
    }

    public void setShowMenuOnClick(boolean showMenuOnClick) {
        this.showMenuOnClick = showMenuOnClick;
    }

    /**
     * Implement this method to refresh the UI contents of the List Item with the data.
     * @param view
     * @param data
     */
    protected abstract void populateView(View view, T data);

    /**
     * Override this method if you want to do something when the overall List Item is clicked.
     * @param v
     */
    protected void onItemClicked(View v) {
    }

    /**
     * Override this method if you want to do something when the overall List Item is long clicked.
     * @param v
     */
    protected boolean onItemLongClicked(View v) {
        return false;
    }

    /**
     * Override this method if you want to do something when the DPAD or ENTER key is pressed and released.
     * This is some sort of master click.
     * 
     * @param v
     * @return if handled
     */
    protected boolean onItemKeyMaster(View v) {
        return false;
    }

    protected void onItemChecked(View v, boolean isChecked) {
    }

    /**
     * Helper function.
     * 
     * @param <TView>
     * @param view
     * @param id
     * @return
     */
    @SuppressWarnings("unchecked")
    protected static <TView extends View> TView findView(View view, int id) {
        return (TView) view.findViewById(id);
    }

    /**
     * If you want to create a menu per item, return here the menu adapter.
     * The menu will be created automatically and the vent long click will be eaten.
     */
    protected MenuAdapter getMenuAdapter(View view) {
        return null;
    }

    /**
     * Sets up the behavior of a possible checkbox to check this item.
     * 
     * Takes in consideration:
     * - Only so many views are created and reused by the ListView
     * - Setting the correct checked/unchecked value without triggering the onCheckedChanged event.
     * 
     * @see getChecked()
     * 
     * @param view
     * @param item
     */
    private void initCheckBox(View view, T item) {

        CheckBox checkbox = findView(view, R.id.view_selectable_list_item_checkbox);

        if (checkbox != null) {
            checkbox.setVisibility((checkboxesVisibility) ? View.VISIBLE : View.GONE);

            // so we won't re-trigger a onCheckedChangeListener, we do this because views are re-used.
            checkbox.setOnCheckedChangeListener(null);
            checkbox.setChecked(checkboxesVisibility && checked.contains(item));

            checkbox.setTag(item);
            checkbox.setOnCheckedChangeListener(checkboxOnCheckedChangeListener);
        }
    }

    private void initTouchFeedback(View v, T item) {
        if (v instanceof CheckBox) {
            return;
        }

        v.setOnClickListener(viewOnClickListener);
        v.setOnLongClickListener(viewOnLongClickListener);
        v.setOnKeyListener(viewOnKeyListener);
        v.setTag(item);

        if (v instanceof ViewGroup) {
            ViewGroup vg = (ViewGroup) v;
            int count = vg.getChildCount();
            for (int i = 0; i < count; i++) {
                View child = vg.getChildAt(i);
                initTouchFeedback(child, item);
            }
        }
    }

    private static final class ViewOnClickListener<T> implements OnClickListener {

        private final WeakReference<AbstractListAdapter2<T>> adapterRef;

        public ViewOnClickListener(AbstractListAdapter2<T> adapter) {
            this.adapterRef = Ref.weak(adapter);
        }

        @Override
        public void onClick(View v) {
            //            if (showMenuOnClick) {
            //                MenuAdapter adapter = getMenuAdapter(v);
            //                if (adapter != null) {
            //                    trackDialog(new MenuBuilder(adapter).show());
            //                    return;
            //                }
            //            }
            if (Ref.alive(adapterRef)) {
                adapterRef.get().onItemClicked(v);
            }
        }
    }

    private static final class ViewOnLongClickListener<T> implements OnLongClickListener {

        private final WeakReference<AbstractListAdapter2<T>> adapterRef;

        public ViewOnLongClickListener(AbstractListAdapter2<T> adapter) {
            this.adapterRef = Ref.weak(adapter);
        }

        @Override
        public boolean onLongClick(View v) {
            //            MenuAdapter adapter = getMenuAdapter(v);
            //            if (adapter != null) {
            //                trackDialog(new MenuBuilder(adapter).show());
            //                return true;
            //            }
            if (Ref.alive(adapterRef)) {
                return adapterRef.get().onItemLongClicked(v);
            } else {
                return false;
            }

        }
    }

    private static final class ViewOnKeyListener<T> implements OnKeyListener {

        private final WeakReference<AbstractListAdapter2<T>> adapterRef;

        public ViewOnKeyListener(AbstractListAdapter2<T> adapter) {
            this.adapterRef = Ref.weak(adapter);
        }

        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            boolean handled = false;

            if (Ref.alive(adapterRef)) {
                switch (keyCode) {
                case KeyEvent.KEYCODE_DPAD_CENTER:
                case KeyEvent.KEYCODE_ENTER:
                    if (event.getAction() == KeyEvent.ACTION_UP) {
                        handled = adapterRef.get().onItemKeyMaster(v);
                    }
                }
            }

            return handled;
        }
    }

    private static final class CheckboxOnCheckedChangeListener<T> implements OnCheckedChangeListener {

        private final WeakReference<AbstractListAdapter2<T>> adapterRef;

        public CheckboxOnCheckedChangeListener(AbstractListAdapter2<T> adapter) {
            this.adapterRef = Ref.weak(adapter);
        }

        @SuppressWarnings("unchecked")
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (Ref.alive(adapterRef)) {
                AbstractListAdapter2<T> adapter = adapterRef.get();

                T item = (T) buttonView.getTag();

                if (isChecked && !adapter.checked.contains(item)) {
                    adapter.checked.add(item);
                } else {
                    adapter.checked.remove(item);
                }

                adapterRef.get().onItemChecked(buttonView, isChecked);
            }
        }
    }

    private static final class AbstractListAdapterFilter<T> extends Filter {

        private final WeakReference<AbstractListAdapter2<T>> adapterRef;
        private final ListAdapterFilter<T> filter;

        public AbstractListAdapterFilter(AbstractListAdapter2<T> adapter, ListAdapterFilter<T> filter) {
            this.adapterRef = Ref.weak(adapter);
            this.filter = filter;
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            FilterResults result = new FilterResults();

            if (Ref.alive(adapterRef)) {
                List<T> list = adapterRef.get().getList();

                if (filter == null) {
                    /** || StringUtils.isNullOrEmpty(constraint.toString(), true)) { */
                    result.values = list;
                    result.count = list.size();
                } else {
                    List<T> filtered = new ArrayList<T>();
                    int size = list.size();
                    for (int i = 0; i < size; i++) {
                        T obj = list.get(i);
                        if (filter.accept(obj, constraint)) {
                            filtered.add(obj);
                        }
                    }
                    result.values = filtered;
                    result.count = filtered.size();
                }
            } else {
                result.values = Collections.emptyList();
                result.count = 0;
            }

            return result;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            if (Ref.alive(adapterRef)) {
                adapterRef.get().visualList = (List<T>) results.values;
                adapterRef.get().notifyDataSetInvalidated();
            }
        }

    }
}