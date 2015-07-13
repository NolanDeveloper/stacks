package com.nolane.stacks.ui;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.nolane.stacks.R;
import com.nolane.stacks.provider.CardsContract;
import com.nolane.stacks.utils.MetricsUtils;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ScheduledExecutorService;

public class AllStacksFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, View.OnClickListener {
    private class StacksAdapter extends RecyclerView.Adapter<StacksAdapter.ViewHolder> {
        public class ViewHolder extends RecyclerView.ViewHolder {
            private TextView tvTitle;
            private TextView tvDescription;
            private Button btnAddCard;
            private Button btnDelete;

            public ViewHolder(View itemView) {
                super(itemView);
                tvTitle = (TextView) itemView.findViewById(R.id.tv_title);
                tvDescription = (TextView) itemView.findViewById(R.id.tv_description);
                btnAddCard = (Button) itemView.findViewById(R.id.btn_add_card);
                btnDelete = (Button) itemView.findViewById(R.id.btn_delete);
            }
        }

        // The query which this adapter represents through recycler view.
        private Cursor query;
        // The element of the list that we want to hide. This is necessary because
        // we must have opportunity to restore all stack after deletion. Instead of
        // deleting we just hide element. And if user will decide to undo deletion
        // we just show this element up. Otherwise if user is going to delete this
        // stack we just do this after the moment the button "undo" will move out
        // of screen. The value -1 means nothing is hidden.
        private int hiddenPosition = -1;
        // Timer and TimerTask that will do actual deletion.
        Timer timer;
        TimerTask timerTask;

        public StacksAdapter(@Nullable Cursor query) {
            super();
            this.query = query;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_stack_1, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            // Make offset to actual position.
            if ((-1 != hiddenPosition) && (hiddenPosition < position)) {
                position += 1;
            }
            query.moveToPosition(position);
            final long id = query.getLong(StacksQuery.ID);
            final String title = query.getString(StacksQuery.TITLE);
            final String description = query.getString(StacksQuery.DESCRIPTION);
            final Uri thisStack = ContentUris.withAppendedId(CardsContract.Stacks.CONTENT_URI, id);
            holder.tvTitle.setText(title);
            holder.tvDescription.setText(description);
            holder.btnAddCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), AddCardActivity.class);
                    intent.setData(thisStack);
                    startActivity(intent);
                }
            });
            final int finalPosition = position;
            holder.btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Here the dancing begin.
                    // Firstly hide element which the user wants to delete.
                    hiddenPosition = finalPosition;
                    // And don't forget about updating list.
                    notifyDataSetChanged();
                    // Then create snackbar and start timer for deletion.
                    int snackDuration = 2000;
                    timer = new Timer();
                    // Here we use explicit duration because we didn't find a way to find out
                    // duration of constant values Snackbar.LENGTH_SHORT and Snackbar.LENGTH_LONG.
                    // I want to meet the man who created these constants and ask him what he
                    // wanted to achieve using them. IMHO they are useless. The better way is
                    // create constant values that represent actual time.
                    //noinspection ResourceType
                    Snackbar.make(getView(), getString(R.string.deleted), snackDuration)
                            .setAction(getString(R.string.undo), new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    // Just show hidden element and stop timer.
                                    timer.cancel();
                                    hiddenPosition = -1;
                                    notifyDataSetChanged();
                                    timer = null;
                                    timerTask = null;
                                }
                            })
                            .show();
                    timerTask = new TimerTask() {
                        @Override
                        public void run() {
                            // Here is actual deletion.
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    getActivity().getContentResolver().delete(thisStack, null, null);
                                }
                            }).run();
                            timer = null;
                            timerTask = null;
                        }
                    };
                    timer.schedule(timerTask, snackDuration);
                }
            });
        }

        @Override
        public int getItemCount() {
            if (null == query) {
                return 0;
            } else {
                return query.getCount() - (-1 == hiddenPosition ? 0 : 1);
            }
        }

        public void setCursor(@Nullable Cursor query) {
            if (this.query == query)
                return;
            hiddenPosition = -1;
            if (null != this.query) {
                this.query.close();
            }
            this.query = query;
            notifyDataSetChanged();
        }
    }

    // UI elements.
    private RecyclerView rvStacks;
    private FloatingActionButton fab;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_all_stacks, container, false);
        rvStacks = (RecyclerView) view.findViewById(R.id.rv_stacks);
        fab = (FloatingActionButton) view.findViewById(R.id.fab);

        rvStacks.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        rvStacks.setAdapter(new StacksAdapter(null));

        fab.setOnClickListener(this);
        getLoaderManager().initLoader(StacksQuery._TOKEN, null, this);
        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        StacksAdapter adapter = (StacksAdapter) rvStacks.getAdapter();
        if (null != adapter.timer) {
            adapter.timer.cancel();
            adapter.timerTask.run();
        }
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(getActivity(), CreateStackActivity.class);
        startActivity(intent);
    }

    private interface StacksQuery {
        int _TOKEN = 0;

        String[] COLUMNS = {
                CardsContract.Stacks.STACK_ID,
                CardsContract.Stacks.STACK_TITLE,
                CardsContract.Stacks.STACK_DESCRIPTION
        };

        int ID = 0;
        int TITLE = 1;
        int DESCRIPTION = 2;
    }

    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getActivity(), CardsContract.Stacks.CONTENT_URI, StacksQuery.COLUMNS, null, null, null);
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
