package com.nolane.stacks.ui;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import com.nolane.stacks.utils.RecyclerCursorAdapter;
import com.nolane.stacks.utils.UriUtils;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.nolane.stacks.provider.CardsContract.Stacks;

/**
 * This fragment shows list of all stacks. When user clicks at one of them {@link EditStackActivity}
 * is opened to allow editing.
 */
public class AllStacksFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    /**
     * Adapter for the RecyclerView.
     */
    class StacksAdapter extends RecyclerCursorAdapter<StacksAdapter.ViewHolder> {
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

        public StacksAdapter(@Nullable Cursor query) {
            super(query);
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_stack_1, parent, false);
            return new ViewHolder(view);
        }

        /**
         * Shortens names of languages. <br>
         * Examples: <br>
         *     Russian -> Rus <br>
         *     russian -> Rus <br>
         *     rUssian -> Rus <br>
         *     Ru -> Ru <br>
         *     ru -> Ru <br>
         *     r -> [empty string]
         * @param language Full language name.
         * @return <li>({@code language}.length < 2) returns empty string.
         * <li>({@code language}.length == 2) returns first two characters: first in upper case, second
         * in lower case.
         * <li>Otherwise returns first 3 characters: first in upper case, other in lower case.
         */
        private String shortenLanguage(@NonNull String language) {
            if (language.length() < 2) {
                return "";
            }
            if (language.length() == 2) {
                return String.valueOf(Character.toUpperCase(language.charAt(0))) + Character.toLowerCase(language.charAt(1));
            }
            return Character.toUpperCase(language.charAt(0)) + language.substring(1, 3).toLowerCase();
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            query.moveToPosition(position);
            final long id = query.getLong(StacksQuery.ID);
            final String title = query.getString(StacksQuery.TITLE);
            final String language = query.getString(StacksQuery.LANGUAGE);
            final int count = query.getInt(StacksQuery.COUNT_CARDS);
            final int color = query.getInt(StacksQuery.COLOR);
            final Uri thisStack = ContentUris.withAppendedId(Stacks.CONTENT_URI, id);
            holder.ibAddCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), AddCardActivity.class);
                    Uri data = ContentUris.withAppendedId(Stacks.CONTENT_URI, id);
                    intent.setData(data);
                    startActivity(intent);
                }
            });
            holder.tvTitle.setText(title);
            holder.tvLanguage.setText(shortenLanguage(language));
            holder.tvCountCards.setText(String.valueOf(count));
            holder.ivIcon.getDrawable().setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
            holder.vRoot.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), EditStackActivity.class);
                    Uri data = UriUtils.insertParameter(thisStack, Stacks.STACK_TITLE, title);
                    data = UriUtils.insertParameter(data, Stacks.STACK_LANGUAGE, language);
                    data = UriUtils.insertParameter(data, Stacks.STACK_COLOR, color);
                    intent.setData(data);
                    startActivity(intent);
                }
            });
        }
    }

    // UI elements.
    @Bind(R.id.rv_stacks)
    RecyclerView rvStacks;
    @Bind(R.id.fab)
    FloatingActionButton fab;

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
        so it looks pretty good.
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
                super.onDraw(c, parent, state);
            }
        });
        rvStacks.setAdapter(new StacksAdapter(null));
        return view;
    }

    /**
     * Start CreateStackActivity.
     */
    @OnClick(R.id.fab)
    public void createStack() {
        Intent intent = new Intent(getActivity(), CreateStackActivity.class);
        startActivity(intent);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(StacksQuery._TOKEN, null, this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    private interface StacksQuery {
        int _TOKEN = 0;

        String[] COLUMNS = {
                Stacks.STACK_ID,
                Stacks.STACK_TITLE,
                Stacks.STACK_LANGUAGE,
                Stacks.STACK_COUNT_CARDS,
                Stacks.STACK_COLOR
        };

        int ID = 0;
        int TITLE = 1;
        int LANGUAGE = 2;
        int COUNT_CARDS = 3;
        int COLOR = 4;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getActivity(), Stacks.CONTENT_URI, StacksQuery.COLUMNS, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor query) {
        if (null == query) {
            throw new IllegalArgumentException("Loader was failed. (query = null)");
        }
        ((StacksAdapter) rvStacks.getAdapter()).setCursor(query);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
