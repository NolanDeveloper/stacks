package com.nolane.stacks.ui;

import android.app.Fragment;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
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
import butterknife.OnClick;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * This fragment shows list of all stacks. When user clicks at one of them {@link EditStackActivity}
 * is opened to allow editing.
 */
public class AllStacksFragment extends Fragment {
    /**
     * Adapter for the RecyclerView.
     */
    class StacksWrapperAdapter extends RecyclerCursorWrapperAdapter<Stack, StacksWrapperAdapter.ViewHolder> {
        public class ViewHolder extends RecyclerView.ViewHolder {
            View vRoot;
            @Bind(R.id.ib_add_card)
            ImageButton ibAddCard;
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
            View view = inflater.inflate(R.layout.item_stack_1, parent, false);
            return new ViewHolder(view);
        }

        @SuppressWarnings("ConstantConditions")
        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            final Stack stack = query.getAtPosition(position);
            holder.tvTitle.setText(stack.title);
            holder.tvCountCards.setText(String.valueOf(stack.countCards));
            holder.ivIcon.getDrawable().setColorFilter(stack.color, PorterDuff.Mode.SRC_ATOP);
            holder.tvLanguage.setText(GeneralUtils.shortenLanguage(stack.language));
            holder.ibAddCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AddCardActivity.start(getActivity(), stack.id);
                }
            });
            holder.vRoot.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EditStackActivity.start(getActivity(), stack);
                }
            });
        }
    }

    // UI elements.
    @Bind(R.id.rv_stacks)
    RecyclerView rvStacks;
    @Bind(R.id.fab)
    FloatingActionButton fab;

    private StacksWrapperAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_all_stacks, container, false);
        ButterKnife.bind(this, view);

        rvStacks.setLayoutManager(new GridLayoutManager(getActivity(),
                getResources().getInteger(R.integer.all_stacks_columns), LinearLayoutManager.VERTICAL, false));
        /*
        The following item decoration draws narrow border lines.
        Each look like this ____|
        And in grid they look like this
        ___|___|___|
        ___|___|___|
        ___|___|___|
        ___|___|___|
        But the most right is too narrow to be visible
        so it looks appropriate.
         */
        rvStacks.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
                Paint paint = new Paint();
                paint.setColor(Color.BLACK);
                for (int i = 0; i < parent.getChildCount(); i++) {
                    View item = parent.getChildAt(i);
                    float[] points = {
                            item.getX(), item.getY() + item.getHeight(),
                            item.getX() + item.getWidth(), item.getY() + item.getHeight(),
                            item.getX() + item.getWidth(), item.getY() + item.getHeight(),
                            item.getX() + item.getWidth(), item.getY()
                    };
                    c.drawLines(points, paint);
                }
            }
        });
        if (null == adapter) {
            adapter = new StacksWrapperAdapter(null);
        }
        rvStacks.setAdapter(adapter);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
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
                        throwable.printStackTrace();
                        getActivity().finish();
                    }
                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    /**
     * Start CreateStackActivity.
     */
    @OnClick(R.id.fab)
    public void createStack() {
        startActivity(new Intent(getActivity(), CreateStackActivity.class));
    }
}
