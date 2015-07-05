package com.nolan.learnenglishwords.fragments;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.nolan.learnenglishwords.R;
import com.nolan.learnenglishwords.activities.TrainingActivity;
import com.nolan.learnenglishwords.core.BusinessLogic;
import com.nolan.learnenglishwords.model.Dictionary;

import java.io.Serializable;
import java.sql.SQLException;

public class NavigationFragment extends Fragment {
    private class ListDictionaries extends AsyncTask<Void, Void, Dictionary[]> {
        private Context context;
        public ListDictionaries(@NonNull Context context) {
            this.context = context;
        }
        @Override
        protected Dictionary[] doInBackground(Void... params) {
            Cursor c = null;
            try {
                c = BusinessLogic.GetInstance(context).queryDictionaries();
                Dictionary[] dictionaries = new Dictionary[c.getCount()];
                int i = 0;
                while (c.moveToNext())
                    dictionaries[i++] = new Dictionary(c);
                return dictionaries;
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                if ((null != c) && !c.isClosed())
                    c.close();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Dictionary[] d) {
            super.onPostExecute(d);
            if (null != d) {
                dictionaries = d;
                lvNavigation.setAdapter(new BaseAdapter() {
                    @Override
                    public int getCount() {
                        return dictionaries.length;
                    }

                    @Override
                    public Object getItem(int position) {
                        return dictionaries[position];
                    }

                    @Override
                    public long getItemId(int position) {
                        return dictionaries[position].id;
                    }

                    @Override
                    public View getView(int position, View convertView, ViewGroup parent) {
                        if (null == convertView) {
                            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.simple_navigation_drawer_item, parent, false);
                            convertView.setTag(R.id.text_view, convertView.findViewById(android.R.id.text1));
                        }
                        TextView text = (TextView) convertView.getTag(R.id.text_view);
                        text.setText(dictionaries[position].title);
                        return convertView;
                    }
                });
                lvNavigation.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent intent = new Intent(getActivity().getBaseContext(), TrainingActivity.class);
                        intent.putExtra(TrainingActivity.ARG_DICTIONARY, (Serializable) lvNavigation.getItemAtPosition(position));
                        startActivity(intent);
                    }
                });
            }
        }
    }

    private ListView lvNavigation;
    private Dictionary[] dictionaries;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup view = (ViewGroup) inflater.inflate(R.layout.navigation_drawer, container, false);
        lvNavigation = (ListView) view.findViewById(R.id.lv_navigation);
        new ListDictionaries(getActivity()).execute();
        return view;
    }

    public void updateList() {
        new ListDictionaries(getActivity()).execute();
    }
}
