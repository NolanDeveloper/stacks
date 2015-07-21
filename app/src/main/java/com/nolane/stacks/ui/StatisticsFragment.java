package com.nolane.stacks.ui;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.nolane.stacks.R;

import java.util.ArrayList;
import java.util.TreeMap;

import butterknife.Bind;
import butterknife.ButterKnife;

import static com.nolane.stacks.provider.CardsContract.Answers;

/**
 * This fragment show statistics of user.
 */
public class StatisticsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    @Bind(R.id.lc_graph)
    LineChart lcGraph;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_statistics, container, false);
        ButterKnife.bind(this, view);
        getLoaderManager().initLoader(AnswersQuery._TOKEN, null, this);
        return view;
    }

    private interface AnswersQuery {
        int _TOKEN = 0;

        String[] COLUMNS = {
                Answers.ANSWER_RIGHT,
                "date(" + Answers.ANSWER_TIMESTAMP + ")"
        };

        int RIGHT = 0;
        int DATE = 1;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(
                getActivity(),
                Answers.CONTENT_URI,
                AnswersQuery.COLUMNS,
                Answers.ANSWER_TIMESTAMP + " > date('now', '-1 month')",
                null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor query) {
        if (null == query) {
            throw new IllegalArgumentException("Loader was failed. (query = null)");
        }
        if (0 == query.getCount()) {
            nothingToShow();
            return;
        }
        TreeMap<String, Integer> data = new TreeMap<>();
        while (query.moveToNext()) {
            boolean right = query.getInt(AnswersQuery.RIGHT) != 0;
            String date = query.getString(AnswersQuery.DATE);
            int count = data.containsKey(date) ? data.get(date) : 0;
            data.put(date, count + 1);
        }
        ArrayList<String> xValues = new ArrayList<>(data.keySet());
        ArrayList<Entry> points = new ArrayList<>();
        for (java.util.Map.Entry<String, Integer> entry : data.entrySet()) {
            points.add(new Entry(entry.getValue(), xValues.indexOf(entry.getKey())));
        }
        lcGraph.setData(new LineData(xValues.toArray(new String[xValues.size()]), new LineDataSet(points, "label")));
    }

    private void nothingToShow() {
        // todo: implement
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
