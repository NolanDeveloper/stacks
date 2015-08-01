package com.nolane.stacks.ui;

import android.app.Fragment;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nolane.stacks.R;
import com.nolane.stacks.provider.CardsDAO;
import com.nolane.stacks.provider.Stack;
import com.nolane.stacks.utils.GeneralUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func2;
import rx.schedulers.Schedulers;

/**
 * This fragment finds out which stack user wants to train and then
 * start {@link TrainingActivity} with this stack as data.
 */
public class PickStackFragment extends Fragment {
    /**
     * Adapter for the RecyclerView.
     */
    public class StacksWrapperAdapter extends RecyclerView.Adapter<StacksWrapperAdapter.ViewHolder> {
        public class ViewHolder extends RecyclerView.ViewHolder {
            View vRoot;
            @Bind(R.id.iv_icon)
            ImageView ivIcon;
            @Bind(R.id.tv_title)
            TextView tvTitle;
            @Bind(R.id.tv_language)
            TextView tvLanguage;
            @Bind(R.id.tv_count_cards)
            TextView tvCountCards;

            public ViewHolder(View itemView) {
                super(itemView);
                vRoot = itemView;
                ButterKnife.bind(this, itemView);
            }
        }

        @Nullable
        private List<Pair<Stack, Integer>> data = null;

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View item = inflater.inflate(R.layout.item_stack, parent, false);
            return new ViewHolder(item);
        }

        @SuppressWarnings("ConstantConditions")
        @Override
        public void onBindViewHolder(ViewHolder holder, final int position) {
            Pair<Stack, Integer> dataItem = data.get(position);
            final Stack stack = dataItem.first;
            final int count = null != dataItem.second ? dataItem.second : 0;
            holder.tvTitle.setText(stack.title);
            holder.tvLanguage.setText(GeneralUtils.shortenLanguage(stack.language));
            holder.ivIcon.getDrawable().setColorFilter(stack.color, PorterDuff.Mode.SRC_ATOP);
            holder.vRoot.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (0 != count) {
                        TrainingActivity.start(getActivity(), stack.id);
                    } else {
                        Snackbar.make(
                                getView(),
                                getString(R.string.no_cards_now),
                                Snackbar.LENGTH_SHORT).show();
                    }
                }
            });
            holder.tvCountCards.setText(String.valueOf(count));
        }

        @Override
        public int getItemCount() {
            return null == data ? 0 : data.size();
        }

        public void setData(@Nullable List<Pair<Stack, Integer>> data) {
            this.data = data;
            notifyDataSetChanged();
        }
    }

    // UI elements.
    @Bind(R.id.rv_stacks)
    RecyclerView rvStacks;

    private StacksWrapperAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_pick_stack, container, false);
        ButterKnife.bind(this, view);
        getActivity().setTitle(getString(R.string.choose_stack));
        rvStacks.setLayoutManager(
                new GridLayoutManager(getActivity(),
                        getResources().getInteger(R.integer.pick_stack_columns),
                        GridLayoutManager.VERTICAL, false));
        if (null == adapter) {
            adapter = new StacksWrapperAdapter();
        }
        rvStacks.setAdapter(adapter);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        CardsDAO.getInstance()
                .listStacks()
                .zipWith(CardsDAO.getInstance().countCardsToLearn(),
                        new Func2<List<Stack>, Map<Long,Integer>, List<Pair<Stack, Integer>>>() {
                            @Override
                            public List<Pair<Stack, Integer>> call(List<Stack> stacks, Map<Long, Integer> countCards) {
                                List<Pair<Stack, Integer>> result = new ArrayList<>(stacks.size());
                                for (Stack stack : stacks) {
                                    result.add(new Pair<>(stack, countCards.get(stack.id)));
                                }
                                return result;
                            }
                        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<List<Pair<Stack, Integer>>>() {
                    @Override
                    public void call(List<Pair<Stack, Integer>> pairs) {
                        adapter.setData(pairs);
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        throw new Error(throwable);
                    }
                });
    }
}
