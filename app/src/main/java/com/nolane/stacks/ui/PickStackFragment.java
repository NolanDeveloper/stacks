package com.nolane.stacks.ui;

import android.app.Fragment;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nolane.stacks.R;
import com.nolane.stacks.provider.CardsDAO;
import com.nolane.stacks.provider.CursorWrapper;
import com.nolane.stacks.provider.Stack;
import com.nolane.stacks.utils.GeneralUtils;
import com.nolane.stacks.utils.RecyclerCursorWrapperAdapter;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * This fragment finds out which stack user wants to train and then
 * start {@link TrainingActivity} with this stack as data.
 */
public class PickStackFragment extends Fragment {
    /**
     * Adapter for the RecyclerView.
     */
    public class StacksWrapperAdapter extends RecyclerCursorWrapperAdapter<Stack, StacksWrapperAdapter.ViewHolder> {
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

        public StacksWrapperAdapter(@Nullable CursorWrapper<Stack> query) {
            super(query);
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View item = inflater.inflate(R.layout.item_stack, parent, false);
            return new ViewHolder(item);
        }

        @SuppressWarnings("ConstantConditions")
        @Override
        public void onBindViewHolder(ViewHolder holder, final int position) {
            final Stack stack = query.getAtPosition(position);
            holder.tvTitle.setText(stack.title);
            holder.tvCountCards.setText(String.valueOf(stack.countCards));
            holder.tvLanguage.setText(GeneralUtils.shortenLanguage(stack.language));
            holder.ivIcon.getDrawable().setColorFilter(stack.color, PorterDuff.Mode.SRC_ATOP);
            holder.vRoot.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TrainingActivity.start(getActivity(), stack.id);
                }
            });
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
            adapter = new StacksWrapperAdapter(null);
            CardsDAO.getInstance()
                    .listStacks()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Action1<CursorWrapper<Stack>>() {
                        @Override
                        public void call(CursorWrapper<Stack> stackCursorWrapper) {
                            adapter.setCursorWrapper(stackCursorWrapper);
                        }
                    }, new Action1<Throwable>() {
                        @Override
                        public void call(Throwable throwable) {
                            throw new Error(throwable);
                        }
                    });
        }
        rvStacks.setAdapter(adapter);
        return view;
    }
}
