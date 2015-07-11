package com.nolane.stacks.ui;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.nolane.stacks.R;
import com.nolane.stacks.provider.CardsContract;
import com.nolane.stacks.utils.MetricsUtils;

/**
 * This fragment shows all cards to user. The user can remove all edit each card.
 * This fragment is used in conjunction with {@link AllCardsActivity}.
 */
public class AllCardsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    /**
     * Adapter for RecyclerView.
     */
    private class CardsAdapter extends RecyclerView.Adapter<CardsAdapter.ViewHolder> {
        public class ViewHolder extends RecyclerView.ViewHolder {
            public View root;
            public TextView tvScrutiny;
            public TextView tvFront;
            public TextView tvBack;
            public ImageButton ibRemove;

            public ViewHolder(View itemView) {
                super(itemView);
                root = itemView;
                tvScrutiny = (TextView) itemView.findViewById(R.id.tv_scrutiny);
                tvFront = (TextView) itemView.findViewById(R.id.tv_front);
                tvBack = (TextView) itemView.findViewById(R.id.tv_back);
                ibRemove = (ImageButton) itemView.findViewById(R.id.ib_remove);
            }
        }

        private Cursor query;

        public CardsAdapter(@NonNull Cursor query) {
            super();
            this.query = query;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View item = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_card, parent, false);
            return new ViewHolder(item);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            query.moveToPosition(position);
            final int scrutiny = query.getInt(CardsQuery.SCRUTINY);
            holder.tvScrutiny.setText(String.valueOf(scrutiny));
            final String front = query.getString(CardsQuery.FRONT);
            holder.tvFront.setText(front);
            final String back = query.getString(CardsQuery.BACK);
            holder.tvBack.setText(back);
            final long id = query.getLong(CardsQuery.ID);
            final long stackId = query.getLong(CardsQuery.STACK_ID);
            holder.ibRemove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            // todo: make snackbar.
                            Uri data = CardsContract.Card.buildUriToCard(stackId, id);
                            getActivity().getContentResolver().delete(data, null, null);
                        }
                    });
                }
            });
            holder.root.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), EditCardActivity.class);
                    Uri data = CardsContract.Card.CONTENT_URI
                            .buildUpon()
                            .appendPath(String.valueOf(stackId))
                            .appendPath(String.valueOf(id))
                            .appendQueryParameter(CardsContract.Card.CARD_FRONT, front)
                            .appendQueryParameter(CardsContract.Card.CARD_BACK, back)
                            .appendQueryParameter(CardsContract.Card.CARD_SCRUTINY, String.valueOf(scrutiny))
                            .build();
                    intent.setData(data);
                    startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return query.getCount();
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
        rvCards.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                int margin = MetricsUtils.convertDpToPx(4);
                outRect.top = margin;
                outRect.right = margin;
                outRect.bottom = margin;
                outRect.left = margin;
            }
        });
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(CardsQuery._TOKEN, null, this);
    }

    private interface CardsQuery {
        int _TOKEN = 0;

        String[] COLUMNS = {
                CardsContract.Card.CARD_ID,
                CardsContract.Card.CARD_FRONT,
                CardsContract.Card.CARD_BACK,
                CardsContract.Card.CARD_SCRUTINY,
                CardsContract.Card.CARD_STACK_ID
        };

        int ID = 0;
        int FRONT = 1;
        int BACK = 2;
        int SCRUTINY = 3;
        int STACK_ID = 4;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getActivity(), CardsContract.Card.CONTENT_URI, CardsQuery.COLUMNS, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor query) {
        if (null == query) {
            throw new IllegalArgumentException("Loader was failed. (query = null)");
        }
        int count = query.getCount();
        rvCards.setAdapter(new CardsAdapter(query));
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

}
