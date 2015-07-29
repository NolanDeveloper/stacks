package com.nolane.stacks.ui;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nolane.stacks.R;

import butterknife.ButterKnife;

/**
 * This fragment show statistics of user.
 */
public class StatisticsFragment extends Fragment {
//    // UI elements.
//    @Bind(R.id.tv_total_cards)
//    TextView tvTotalCards;
//    @Bind(R.id.tv_cards_learned)
//    TextView tvCardsLearned;
//    @Bind(R.id.tv_total_progress)
//    TextView tvTotalProgress;
//    @Bind(R.id.tv_streak)
//    TextView tvStreak;
//    @Bind(R.id.tv_best_streak)
//    TextView tvBestStreak;
//    @Bind(R.id.tv_total_answers)
//    TextView tvTotalAnswers;
//    @Bind(R.id.lcv_answers_per_day)
//    LineChartView lcvGraph;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_statistics, container, false);
        ButterKnife.bind(this, view);
        getActivity().setTitle(R.string.statistics);
        return view;
    }
}
