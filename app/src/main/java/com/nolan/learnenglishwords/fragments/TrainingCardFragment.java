package com.nolan.learnenglishwords.fragments;

import android.app.Fragment;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.AsyncTaskLoader;
import android.content.Context;
import android.content.Loader;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.nolan.learnenglishwords.R;
import com.nolan.learnenglishwords.core.BusinessLogic;
import com.nolan.learnenglishwords.model.Dictionary;
import com.nolan.learnenglishwords.utils.AsyncResult;

public class TrainingCardFragment extends Fragment implements View.OnClickListener, LoaderCallbacks<Object> {
    private static class StartLoader extends AsyncTaskLoader<Object> {
        public static final int ID = 0;

        public static final String ARG_DICTIONARY_ID = "dictionary id";

        private final long dictionaryId;

        private BusinessLogic businessLogic;

        public StartLoader(@NonNull Context context, long id) {
            super(context);
            businessLogic = BusinessLogic.GetInstance(context);
            dictionaryId = id;
        }

        @Override
        public Throwable loadInBackground() {
            try {
                businessLogic.startCardTraining(dictionaryId);
                return null;
            } catch (Throwable e) {
                return e;
            }
        }
    }

    private static class NextCardLoader extends AsyncTaskLoader<Object> {
        public static final int ID = 1;

        private BusinessLogic businessLogic;

        public NextCardLoader(@NonNull Context context) {
            super(context);
            businessLogic = BusinessLogic.GetInstance(context);
        }

        @Override
        public AsyncResult<String> loadInBackground() {
            try {
                return new AsyncResult<>(businessLogic.nextCard());
            } catch (Throwable e) {
                return new AsyncResult<>(e);
            }
        }
    }

    private static class GuessLoader extends AsyncTaskLoader<Object> {
        public static final int ID = 2;

        public static final String ARG_BACK = "back";

        private final String back;

        private BusinessLogic businessLogic;

        public GuessLoader(@NonNull Context context, @NonNull String back) {
            super(context);
            businessLogic = BusinessLogic.GetInstance(context);
            this.back = back;
        }

        @Override
        public AsyncResult<Boolean> loadInBackground() {
            try {
                return new AsyncResult<>(businessLogic.guess(back));
            } catch (Throwable e) {
                return new AsyncResult<>(e);
            }
        }
    }

    public static final String ARG_DICTIONARY = "dictionary";

    private Dictionary dictionary;

    private TextView tvTitle;
    private TextView tvFront;
    private EditText etBack;
    private Button btnDone;

    private BusinessLogic businessLogic;

    public static TrainingCardFragment GetInstance(Dictionary dictionary) {
        TrainingCardFragment fragment = new TrainingCardFragment();
        Bundle arguments = new Bundle();
        arguments.putSerializable(ARG_DICTIONARY, dictionary);
        fragment.setArguments(arguments);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dictionary = (Dictionary) getArguments().getSerializable(ARG_DICTIONARY);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        businessLogic = BusinessLogic.GetInstance(getActivity());
        Bundle arguments = new Bundle();
        arguments.putLong(StartLoader.ARG_DICTIONARY_ID, dictionary.id);
        getLoaderManager().initLoader(StartLoader.ID, arguments, this).forceLoad();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.training_card, container, false);
        tvTitle = (TextView) view.findViewById(R.id.tv_title);
        tvFront = (TextView) view.findViewById(R.id.tv_front);
        etBack = (EditText) view.findViewById(R.id.et_back);
        btnDone = (Button) view.findViewById(R.id.btn_done);

        tvTitle.setText(dictionary.title);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (businessLogic.isInTraining())
            businessLogic.stopCardTraining();
    }

    @Override
    public Loader<Object> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case StartLoader.ID:
                return new StartLoader(getActivity(),
                        args.getLong(StartLoader.ARG_DICTIONARY_ID));
            case NextCardLoader.ID:
                return new NextCardLoader(getActivity());
            case GuessLoader.ID:
                return new GuessLoader(getActivity(),
                        args.getString(GuessLoader.ARG_BACK));
            default:
                throw new UnsupportedOperationException("There is no loader for such id. (id = " + Long.toString(id) + ")");
        }
    }

    @Override
    public void onLoadFinished(Loader<Object> loader, Object data) {
        try {
            switch (loader.getId()) {
                case StartLoader.ID:
                    if (null != data) {
                        if (data instanceof BusinessLogic.NoCardsException) {
                            Toast.makeText(getActivity(), getString(R.string.no_cards), Toast.LENGTH_SHORT).show();
                            getFragmentManager().popBackStack();
                            return;
                        } else
                            throw (Throwable) data;
                    } else {
                        if (null == getLoaderManager().getLoader(NextCardLoader.ID))
                            getLoaderManager().initLoader(NextCardLoader.ID, null, this).forceLoad();
                        else
                            getLoaderManager().restartLoader(NextCardLoader.ID, null, this).forceLoad();
                    }
                    break;
                case NextCardLoader.ID: {
                    AsyncResult<String> result = (AsyncResult<String>) data;
                    result.throwIfHasException();
                    String front = result.result;
                    tvFront.setText(front);
                    btnDone.setOnClickListener(this);
                    break;
                }
                case GuessLoader.ID: {
                    AsyncResult<Boolean> result = (AsyncResult<Boolean>) data;
                    result.throwIfHasException();
                    if (result.result) // successfully guessed
                        Toast.makeText(getActivity(), getString(R.string.right), Toast.LENGTH_SHORT).show();
                    else
                        Toast.makeText(getActivity(), getString(R.string.wrong), Toast.LENGTH_SHORT).show();
                    if (null == getLoaderManager().getLoader(NextCardLoader.ID))
                        getLoaderManager().initLoader(NextCardLoader.ID, null, this).forceLoad();
                    else
                        getLoaderManager().restartLoader(NextCardLoader.ID, null, this).forceLoad();
                    break;
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
            getActivity().finish();
        }
    }

    @Override
    public void onLoaderReset(Loader<Object> loader) {
        if (NextCardLoader.ID == loader.getId())
            tvFront.setText("");
    }

    @Override
    public void onClick(View v) {
        if (businessLogic.isInTraining()) {
            Bundle arguments = new Bundle();
            arguments.putString(GuessLoader.ARG_BACK, etBack.getText().toString());
            if (null == getLoaderManager().getLoader(NextCardLoader.ID))
                getLoaderManager().initLoader(GuessLoader.ID, arguments, this).forceLoad();
            else
                getLoaderManager().restartLoader(GuessLoader.ID, arguments, this).forceLoad();
            btnDone.setOnClickListener(null);
        }
    }
}
