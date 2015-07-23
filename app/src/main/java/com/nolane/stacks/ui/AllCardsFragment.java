package com.nolane.stacks.ui;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.AsyncTaskLoader;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import com.nolane.stacks.R;
import com.nolane.stacks.utils.ColorUtils;
import com.nolane.stacks.utils.PreferencesUtils;
import com.nolane.stacks.utils.RecyclerCursorAdapter;
import com.nolane.stacks.utils.UriUtils;

import butterknife.Bind;
import butterknife.ButterKnife;

import static com.nolane.stacks.provider.CardsContract.Cards;
import static com.nolane.stacks.provider.CardsContract.Stacks;

/**
 * This fragment shows all cards to user. The user can remove all edit each card.
 * This fragment is used in conjunction with {@link AllCardsActivity}.
 */
public class AllCardsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Object> {
    // Key to put ContentValues inside Bundle.
    public static final String EXTRA_VALUES = "values";
    // Key to deliver query to loader.
    public static final String EXTRA_QUERY = "query";

    private static final String EXTRA_URI = "uri";
    private static final String EXTRA_COUNT_CARDS = "count-cards";
    private static final String EXTRA_COUNT_IN_LEARNING = "count-in-learning";

    /**
     * Adapter for RecyclerView.
     */
    class CardsAdapter extends RecyclerCursorAdapter<CardsAdapter.ViewHolder> {
        public class ViewHolder extends RecyclerView.ViewHolder {
            // UI elements.
            View root;
            @Bind(R.id.v_progress_indicator)
            View vProgressIndicator;
            @Bind(R.id.tv_front)
            TextView tvFront;
            @Bind(R.id.tv_back)
            TextView tvBack;
            @Bind(R.id.ib_remove)
            ImageButton ibRemove;

            public ViewHolder(View itemView) {
                super(itemView);
                root = itemView;
                ButterKnife.bind(this, itemView);
            }
        }

        public CardsAdapter(@Nullable Cursor query) {
            super(query);
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View item = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_card, parent, false);
            return new ViewHolder(item);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, final int position) {
            query.moveToPosition(position);
            final long id = query.getLong(CardsQuery.ID);
            final String front = query.getString(CardsQuery.FRONT);
            final String back = query.getString(CardsQuery.BACK);
            final int progress = query.getInt(CardsQuery.PROGRESS);
            final long stackId = query.getLong(CardsQuery.STACK_ID);
            final long inLearning = query.getInt(CardsQuery.IN_LEARNING);
            holder.tvFront.setText(front);
            holder.tvBack.setText(back);
            holder.vProgressIndicator.setBackgroundColor(ColorUtils.getColorForProgress(getActivity(), progress));
            holder.ibRemove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle arguments = new Bundle();
                    ContentValues values = new ContentValues();
                    values.put(Cards.CARD_ID, id);
                    values.put(Cards.CARD_STACK_ID, stackId);
                    values.put(Cards.CARD_IN_LEARNING, inLearning);
                    arguments.putParcelable(EXTRA_VALUES, values);
                    getLoaderManager().initLoader(RemoveCardQuery._TOKEN, arguments, AllCardsFragment.this).forceLoad();
                }
            });
            holder.root.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), EditCardActivity.class);
                    Uri data = Cards.uriToCard(stackId, id)
                            .buildUpon()
                            .appendQueryParameter(Cards.CARD_FRONT, front)
                            .appendQueryParameter(Cards.CARD_BACK, back)
                            .appendQueryParameter(Cards.CARD_PROGRESS, String.valueOf(progress))
                            .build();
                    intent.setData(data);
                    startActivity(intent);
                }
            });
        }
    }

    // UI elements.
    @Bind(R.id.fl_no_cards)
    FrameLayout flNoCards;
    @Bind(R.id.rv_cards)
    RecyclerView rvCards;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_all_cards, container, false);
        ButterKnife.bind(this, view);
        getActivity().setTitle(getString(R.string.cards));
        rvCards.setLayoutManager(new GridLayoutManager(getActivity(),
                getResources().getInteger(R.integer.all_cards_columns), GridLayoutManager.VERTICAL, false));
        rvCards.setAdapter(new CardsAdapter(null));
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(CardsQuery._TOKEN, null, this);
        // Catch started loaders.
        if (null != getLoaderManager().getLoader(RemoveCardQuery._TOKEN)) {
            getLoaderManager().initLoader(RemoveCardQuery._TOKEN, null, this);
        }
    }

    private interface CardsQuery {
        int _TOKEN = 0;

        String[] COLUMNS = {
                Cards.CARD_ID,
                Cards.CARD_FRONT,
                Cards.CARD_BACK,
                Cards.CARD_PROGRESS,
                Cards.CARD_STACK_ID,
                Cards.CARD_IN_LEARNING
        };

        int ID = 0;
        int FRONT = 1;
        int BACK = 2;
        int PROGRESS = 3;
        int STACK_ID = 4;
        int IN_LEARNING = 5;
    }

    private interface RemoveCardQuery {
        int _TOKEN = 1;
    }

    @Override
    public Loader onCreateLoader(int id, final Bundle args) {
        switch (id) {
            case CardsQuery._TOKEN:
                String selection = null;
                String[] selectionArgs = null;
                if (null != args) {
                    String query = args.getString(EXTRA_QUERY);
                    if (!TextUtils.isEmpty(query)) {
                        selection = "(" + Cards.CARD_FRONT + " LIKE ?) OR (" + Cards.CARD_BACK + " LIKE ?)";
                        query = "%" + query + "%";
                        selectionArgs = new String[]{ query, query };
                    }
                }
                return new CursorLoader(
                        getActivity(),
                        Cards.CONTENT_URI,
                        CardsQuery.COLUMNS,
                        selection,
                        selectionArgs,
                        null);
            case RemoveCardQuery._TOKEN:
                return new AsyncTaskLoader(getActivity()) {
                    @Override
                    public Object loadInBackground() {
                        ContentValues values = args.getParcelable(EXTRA_VALUES);
                        long stackId = values.getAsLong(Cards.CARD_STACK_ID);
                        long cardId = values.getAsLong(Cards.CARD_ID);
                        boolean inLearning = values.getAsBoolean(Cards.CARD_IN_LEARNING);
                        Uri uriToCard = Cards.uriToCard(stackId, cardId);
                        uriToCard = UriUtils.insertParameter(uriToCard, Cards.CARD_IN_LEARNING, inLearning);
                        values = new ContentValues();
                        values.put(Cards.CARD_DELETED, true);
                        int deleted = getContext().getContentResolver().update(uriToCard, values, null, null);
                        values = new ContentValues();
                        Uri uriToStack = Stacks.uriToStack(stackId);
                        Cursor stackQuery = getContext().getContentResolver().query(
                                uriToStack,
                                new String[]{
                                        Stacks.STACK_COUNT_CARDS,
                                        Stacks.STACK_COUNT_IN_LEARNING
                                },
                                null,
                                null,
                                null);
                        stackQuery.moveToFirst();
                        int stackCountCards = stackQuery.getInt(0);
                        int stackCountInLearning = stackQuery.getInt(0);
                        values.put(Stacks.STACK_COUNT_CARDS, stackCountCards - 1);
                        if (inLearning) {
                            values.put(Stacks.STACK_COUNT_IN_LEARNING, stackCountInLearning - 1);
                        }
                        getContext().getContentResolver().update(uriToStack, values, null, null);
                        PreferencesUtils.notifyDeleted(getActivity());
                        Bundle result = new Bundle();
                        result.putParcelable(EXTRA_URI, uriToCard);
                        result.putInt(EXTRA_COUNT_CARDS, stackCountCards);
                        result.putInt(EXTRA_COUNT_IN_LEARNING, stackCountCards);
                        return result;
                    }
                };
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Object> loader, Object data) {
        if (null == data) {
            throw new IllegalArgumentException("Loader was failed. (data = null)");
        }
        switch (loader.getId()) {
            case CardsQuery._TOKEN:
                Cursor query = (Cursor) data;
                if (0 != query.getCount()) {
                    flNoCards.setVisibility(View.INVISIBLE);
                    ((CardsAdapter) rvCards.getAdapter()).setCursor(query);
                } else {
                    flNoCards.setVisibility(View.VISIBLE);
                }
                break;
            case RemoveCardQuery._TOKEN:
                Bundle result = (Bundle) data;
                final int countCards = result.getInt(EXTRA_COUNT_CARDS);
                final int countInLearning = result.getInt(EXTRA_COUNT_IN_LEARNING);
                getLoaderManager().destroyLoader(RemoveCardQuery._TOKEN);
                final Context context = getActivity().getApplicationContext();
                final Uri uriToCard = result.getParcelable(EXTRA_URI);
                String stackId = uriToCard.getPathSegments().get(1);
                final Uri uriToStack = Stacks.CONTENT_URI.buildUpon().appendPath(stackId).build();
                final boolean inLearning = Boolean.parseBoolean(uriToCard.getQueryParameter(Cards.CARD_IN_LEARNING));
                PreferencesUtils.cardWasDeleted(getActivity());
                Snackbar.make(getView(), getString(R.string.deleted), Snackbar.LENGTH_LONG)
                        .setAction(R.string.undo, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        PreferencesUtils.cardWasAdded(getActivity());
                                        ContentValues values = new ContentValues();
                                        values.put(Cards.CARD_DELETED, false);
                                        context.getContentResolver().update(uriToCard, values, null, null);
                                        values.clear();
                                        values.put(Stacks.STACK_COUNT_CARDS, countCards);
                                        if (inLearning) {
                                            values.put(Stacks.STACK_COUNT_IN_LEARNING, countInLearning);
                                        }
                                        context.getContentResolver().update(uriToStack, values, null, null);
                                    }
                                }).run();
                            }
                        })
                        .setActionTextColor(getResources().getColor(R.color.snack_bar_positive))
                        .show();
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<Object> loader) {

    }

    /**
     * Performs search query that finds all occurrences of {@code query} at the front and
     * back of cards.
     * @param query Line that user is looking for.
     */
    private void querySearch(@NonNull String query) {
        Bundle args = new Bundle();
        args.putString(EXTRA_QUERY, query);
        getLoaderManager().restartLoader(CardsQuery._TOKEN, args, this).forceLoad();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.frag_all_cards, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                querySearch(query);
                return true;
            }
            @Override
            public boolean onQueryTextChange(String query) {
                querySearch(query);
                return true;
            }
        });
    }
}
