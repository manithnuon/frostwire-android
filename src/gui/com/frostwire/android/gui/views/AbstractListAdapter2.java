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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;

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

    private final WeakReference<Context> contextRef;
    private final int viewItemId;

    private ListAdapterFilter<T> filter;

    protected List<T> list;
    protected List<T> visualList;

    public AbstractListAdapter2(Context context, int viewItemId, List<T> list) {
        this.contextRef = Ref.weak(context);
        this.viewItemId = viewItemId;

        this.list = list.equals(Collections.emptyList()) ? new ArrayList<T>() : list;
        this.visualList = list;
    }

    public AbstractListAdapter2(Context context, int viewItemId) {
        this(context, viewItemId, new ArrayList<T>());
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public int getCount() {
        return visualList == null ? 0 : visualList.size();
    }

    @Override
    public T getItem(int position) {
        return visualList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (Ref.alive(contextRef)) {
            if (convertView == null) {
                convertView = View.inflate(contextRef.get(), viewItemId, null);
            }

            populateView(convertView, getItem(position));
        }

        return convertView;
    }

    public void setList(List<T> list) {
        this.list = list.equals(Collections.emptyList()) ? new ArrayList<T>() : list;
        this.visualList = this.list;
        notifyDataSetInvalidated();
    }

    public void addList(List<T> g) {
        visualList.addAll(g);
        if (visualList != list) {
            list.addAll(g);
        }
        notifyDataSetChanged();
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
        notifyDataSetChanged();
    }

    public void updateList(List<T> g) {
        list = g;
        visualList = g;
        notifyDataSetChanged();
    }

    public void clear() {
        if (list != null) {
            list.clear();
        }
        if (visualList != null) {
            visualList.clear();
        }
        notifyDataSetInvalidated();
    }

    @Override
    public Filter getFilter() {
        return new AbstractListAdapterFilter<T>(this, filter);
    }

    public void setAdapterFilter(ListAdapterFilter<T> filter) {
        this.filter = filter;
    }

    protected abstract void populateView(View view, T data);

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
                List<T> list = adapterRef.get().list;

                if (filter == null) {
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