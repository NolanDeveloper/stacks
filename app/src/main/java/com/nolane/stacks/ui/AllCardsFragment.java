package com.nolane.stacks.ui;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
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
import com.nolane.stacks.provider.Card;
import com.nolane.stacks.provider.CardsDAO;
import com.nolane.stacks.provider.CursorWrapper;
import com.nolane.stacks.utils.GeneralUtils;
import com.nolane.stacks.utils.RecyclerCursorWrapperAdapter;

import java.util.ArrayList;
import java.util.SortedSet;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeSet;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * This fragment shows all cards to user. The user can remove all edit each card.
 * This fragment is used in conjunction with {@link AllCardsActivity}.
 */
public class AllCardsFragment extends Fragment {
    /**
     * Adapter for RecyclerView.
     */
    class CardsAdapter extends RecyclerCursorWrapperAdapter<Card, CardsAdapter.ViewHolder> {
        public class ViewHolder extends RecyclerView.ViewHolder {
            // UI elements.
            View vRoot;
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
                vRoot = itemView;
                ButterKnife.bind(this, itemView);
            }
        }

        public CardsAdapter(@Nullable CursorWrapper<Card> query) {
            super(query);
            hiddenPositions = new TreeSet<>();
            hiddenIds = new ArrayList<>();
            timer = new Timer();
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View item = inflater.inflate(R.layout.item_card, parent, false);
            return new ViewHolder(item);
        }

        private Timer timer;
        private SortedSet<Integer> hiddenPositions;
        private ArrayList<Long> hiddenIds;
        private Integer lastAddedPosition;

        private final int UNDO_PERIOD = 4000;

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            final int actualPosition = position + hiddenPositions.headSet(position + 1).size();
            assert query != null;
            final Card card = query.getAtPosition(actualPosition);
            assert null != card.progress;
            holder.vProgressIndicator.setBackgroundColor(GeneralUtils.getColorForProgress(getActivity(), card.progress));
            holder.tvFront.setText(card.front);
            holder.tvBack.setText(card.back);
            holder.ibRemove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    removeItem(actualPosition);
                }
            });
            holder.vRoot.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EditCardActivity.start(getActivity(), card);
                }
            });
        }

        private void removeItem(int position) {
            hideItem(position);
            //noinspection ResourceType
            Snackbar.make(
                    getView(),
                    getString(R.string.deleted),
                    UNDO_PERIOD)
                    .setAction(R.string.undo, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            undo();
                        }
                    }).show();
            restartTimer();
        }

        private void undo() {
            hiddenPositions.remove(lastAddedPosition);
            hiddenIds.remove(hiddenIds.size() - 1);
            if (hiddenPositions.isEmpty()) timer.cancel();
            notifyDataSetChanged();
        }

        public void restartTimer() {
            // get ids for positions
            timer.cancel();
            timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    CardsDAO.getInstance()
                            .deleteCards(hiddenIds)
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Action1<Void>() {
                                @Override
                                public void call(Void aVoid) {
                                    hiddenPositions.clear();
                                    hiddenIds.clear();
                                    notifyDataSetChanged();
                                }
                            });
                }
            }, (long) (1.1 * UNDO_PERIOD));
        }

        @Override
        public int getItemCount() {
            return super.getItemCount() - hiddenPositions.size();
        }

        public void hideItem(int position) {
            lastAddedPosition = position;
            hiddenPositions.add(lastAddedPosition);
            assert null != query;
            hiddenIds.add(query.getAtPosition(position).id);
            notifyDataSetChanged();
        }
    }

    // UI elements.
    @Bind(R.id.fl_no_cards)
    FrameLayout flNoCards;
    @Bind(R.id.rv_cards)
    RecyclerView rvCards;

    private CardsAdapter adapter;
    private Subscription cardsQuery;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_all_cards, container, false);
        ButterKnife.bind(this, view);
        getActivity().setTitle(getString(R.string.cards));
        rvCards.setLayoutManager(new GridLayoutManager(getActivity(),
                getResources().getInteger(R.integer.all_cards_columns), GridLayoutManager.VERTICAL, false));
        if (null == adapter) {
            adapter = new CardsAdapter(null);
        }
        rvCards.setAdapter(adapter);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        queryCards(null);
    }

    private void queryCards(@Nullable String query) {
        if (null != cardsQuery) cardsQuery.unsubscribe();
        cardsQuery = CardsDAO.getInstance()
                .listCards(null, query)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<CursorWrapper<Card>>() {
                    @Override
                    public void call(CursorWrapper<Card> cardCursorWrapper) {
                        if (0 == cardCursorWrapper.getCount()) {
                            flNoCards.setVisibility(View.VISIBLE);
                            ((CardsAdapter) rvCards.getAdapter()).setCursorWrapper(null);
                        } else {
                            if (View.VISIBLE == flNoCards.getVisibility()) {
                                flNoCards.setVisibility(View.INVISIBLE);
                            }
                            ((CardsAdapter) rvCards.getAdapter()).setCursorWrapper(cardCursorWrapper);
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        Log.e("AllCardsFragment", "An error occurred during request of cards.", throwable);
                    }
                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    /**
     * Performs search query that finds all occurrences of {@code query} at the front and
     * back of cards.
     * @param query Line that user is looking for.
     */
    private void querySearch(@NonNull String query) {
        queryCards(query);
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
