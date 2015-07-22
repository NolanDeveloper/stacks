package com.nolane.stacks.ui;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nolane.stacks.R;
import com.nolane.stacks.utils.PreferencesUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;

import butterknife.Bind;
import butterknife.ButterKnife;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.view.LineChartView;

import static com.nolane.stacks.provider.CardsContract.Answers;

/**
 * This fragment show statistics of user.
 */
public class StatisticsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    // UI elements.
    @Bind(R.id.tv_total_cards)
    TextView tvTotalCards;
    @Bind(R.id.tv_cards_learned)
    TextView tvCardsLearned;
    @Bind(R.id.tv_total_progress)
    TextView tvTotalProgress;
    @Bind(R.id.tv_streak)
    TextView tvStreak;
    @Bind(R.id.tv_best_streak)
    TextView tvBestStreak;
    @Bind(R.id.tv_total_answers)
    TextView tvTotalAnswers;
    @Bind(R.id.lcv_answers_per_day)
    LineChartView lcvGraph;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_statistics, container, false);
        ButterKnife.bind(this, view);
        tvTotalCards.setText(String.valueOf(PreferencesUtils.getTotalCards(getActivity())));
        tvTotalProgress.setText(String.valueOf(PreferencesUtils.getTotalProgress(getActivity())));
        tvCardsLearned.setText(String.valueOf(PreferencesUtils.getCardsLearned(getActivity())));
        tvStreak.setText(String.valueOf(PreferencesUtils.getStreak(getActivity())));
        tvBestStreak.setText(String.valueOf(PreferencesUtils.getBestStreak(getActivity())));
        tvTotalAnswers.setText(String.valueOf(PreferencesUtils.getTotalAnswers(getActivity())));
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
        LineChartData data = new LineChartData();
        ArrayList<PointValue> points = new ArrayList<>();
        Calendar c = Calendar.getInstance();
        c.add(Calendar.MONTH, -1);
        int monthAgo = c.get(Calendar.DAY_OF_YEAR);
        c = Calendar.getInstance();
        int n = 0;
        while (monthAgo != c.get(Calendar.DAY_OF_YEAR)) {
            points.add(new PointValue(n, 0.f));
            n -= 1;
            c.add(Calendar.DATE, -1);
        }
        long now = System.currentTimeMillis();
        try {
            DateFormat SQLiteDate = new SimpleDateFormat("yyyy-MM-dd");
            while (query.moveToNext()) {
                boolean right = query.getInt(AnswersQuery.RIGHT) != 0;
                Date date = SQLiteDate.parse(query.getString(AnswersQuery.DATE));
                long offset = (date.getTime() - now) / DateUtils.DAY_IN_MILLIS;
                int pos = 0;
                while (pos < points.size() && points.get(pos).getX() != offset) {
                    pos++;
                }
                if (pos == points.size()) {
                    continue;
                }
                PointValue point = points.get(pos);
                point.set(offset, point.getY() + 1);
            }
            Line line = new Line(points);
            int color = getResources().getColor(R.color.accent);
            line.setColor(color);
            line.setCubic(true);
            line.setFilled(true);
            line.setHasLines(true);
            line.setHasPoints(false);
            data.setLines(Collections.singletonList(line));

            Axis bottomAxis = new Axis();
            bottomAxis.setName("days ago");
            data.setAxisXBottom(bottomAxis);

            Axis leftAxis = new Axis();
            leftAxis.setName("answers");
            leftAxis.setHasLines(true);
            data.setAxisYLeft(leftAxis);
            lcvGraph.setLineChartData(data);

        } catch (ParseException e) {
            e.printStackTrace();
        } finally {
            query.close();
        }
    }

    private void nothingToShow() {
        // todo: implement
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
