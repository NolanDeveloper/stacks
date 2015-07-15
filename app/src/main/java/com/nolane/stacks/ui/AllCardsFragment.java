package com.nolane.stacks.ui;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.AsyncTaskLoader;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.nolane.stacks.R;
import com.nolane.stacks.utils.ColorUtils;
import com.nolane.stacks.utils.RecyclerCursorAdapter;

import static com.nolane.stacks.provider.CardsContract.Cards;

/**
 * This fragment shows all cards to user. The user can remove all edit each card.
 * This fragment is used in conjunction with {@link AllCardsActivity}.
 */
public class AllCardsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Object> {
    // Key to put ContentValues inside Bundle.
    public static final String EXTRA_VALUES = "values";

    /**
     * Adapter for RecyclerView.
     */
    private class CardsAdapter extends RecyclerCursorAdapter<CardsAdapter.ViewHolder> {
        public class ViewHolder extends RecyclerView.ViewHolder {
            // UI elements.
            public View root;
            public View vProgressIndicator;
            public TextView tvFront;
            public TextView tvBack;
            public ImageButton ibRemove;

            public ViewHolder(View itemView) {
                super(itemView);
                root = itemView;
                vProgressIndicator = itemView.findViewById(R.id.v_progress_indicator);
                tvFront = (TextView) itemView.findViewById(R.id.tv_front);
                tvBack = (TextView) itemView.findViewById(R.id.tv_back);
                ibRemove = (ImageButton) itemView.findViewById(R.id.ib_remove);
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
        public void onBindViewHolder(ViewHolder holder, int position) {
            query.moveToPosition(position);
            final long id = query.getLong(CardsQuery.ID);
            final String front = query.getString(CardsQuery.FRONT);
            final String back = query.getString(CardsQuery.BACK);
            final int progress = query.getInt(CardsQuery.PROGRESS);
            final long lastSeen = query.getLong(CardsQuery.LAST_SEEN);
            final long stackId = query.getLong(CardsQuery.STACK_ID);
            final long inLearning = query.getInt(CardsQuery.IN_LEARNING);
            holder.tvFront.setText(front);
            holder.tvBack.setText(back);
            holder.vProgressIndicator.setBackgroundColor(ColorUtils.getColorForProgress(getActivity(), progress));
            holder.ibRemove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            // We need to save all information about card that we want to remove to
                            // be able to restore it in the future.
                            Bundle arguments = new Bundle();
                            ContentValues values = new ContentValues();
                            values.put(Cards.CARD_ID, id);
                            values.put(Cards.CARD_FRONT, front);
                            values.put(Cards.CARD_BACK, back);
                            values.put(Cards.CARD_PROGRESS, progress);
                            values.put(Cards.CARD_LAST_SEEN, lastSeen);
                            values.put(Cards.CARD_STACK_ID, stackId);
                            values.put(Cards.CARD_IN_LEARNING, inLearning);
                            arguments.putParcelable(EXTRA_VALUES, values);
                            getLoaderManager().initLoader(RemoveCardQuery._TOKEN, arguments, AllCardsFragment.this).forceLoad();
                        }
                    }).run();
                }
            });
            holder.root.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), EditCardActivity.class);
                    Uri data = Cards.CONTENT_URI
                            .buildUpon()
                            .appendPath(String.valueOf(stackId))
                            .appendPath(String.valueOf(id))
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
    private RecyclerView rvCards;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_all_cards, container, false);
        rvCards = (RecyclerView) view.findViewById(R.id.rv_cards);
        rvCards.setLayoutManager(new GridLayoutManager(getActivity(), 2, GridLayoutManager.VERTICAL, false));
        rvCards.setAdapter(new CardsAdapter(null));
        getActivity().setTitle(getString(R.string.cards));
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(CardsQuery._TOKEN, null, this);
        // Catch started loader.
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
                Cards.CARD_LAST_SEEN,
                Cards.CARD_STACK_ID,
                Cards.CARD_IN_LEARNING
        };

        int ID = 0;
        int FRONT = 1;
        int BACK = 2;
        int PROGRESS = 3;
        int LAST_SEEN = 4;
        int STACK_ID = 5;
        int IN_LEARNING = 6;
    }

    private interface RemoveCardQuery {
        int _TOKEN = 1;
    }

    @Override
    public Loader onCreateLoader(int id, final Bundle args) {
        switch (id) {
            case CardsQuery._TOKEN:
                return new CursorLoader(getActivity(), Cards.CONTENT_URI, CardsQuery.COLUMNS, null, null, null);
            case RemoveCardQuery._TOKEN:
                return new AsyncTaskLoader(getActivity()) {
                    @Override
                    public Object loadInBackground() {
                        ContentValues values = args.getParcelable(EXTRA_VALUES);
                        long stackId = values.getAsLong(Cards.CARD_STACK_ID);
                        long cardId = values.getAsLong(Cards.CARD_ID);
                        Uri data = Cards.buildUriToCard(stackId, cardId);
                        getActivity().getContentResolver().delete(data, null, null);
                        return values;
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
                    ((CardsAdapter) rvCards.getAdapter()).setCursor(query);
                } else {
                    getLoaderManager().destroyLoader(CardsQuery._TOKEN);
                    ViewGroup root = (ViewGroup) getView();
                    if (null != root) {
                        root.removeAllViews();
                        ImageView imageView = new ImageView(getActivity());
                        imageView.setImageResource(R.drawable.no_cards);
                        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                        root.addView(imageView);
                    }
                }
                break;
            case RemoveCardQuery._TOKEN:
                getLoaderManager().destroyLoader(RemoveCardQuery._TOKEN);
                final ContentValues values = (ContentValues) data;
                Snackbar.make(getView(), getString(R.string.deleted), Snackbar.LENGTH_LONG)
                        .setAction(R.string.undo, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Uri uri = Cards.CONTENT_URI;
                                        getActivity().getContentResolver().insert(uri, values);
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
}
