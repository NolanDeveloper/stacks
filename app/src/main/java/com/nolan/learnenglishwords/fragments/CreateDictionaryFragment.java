package com.nolan.learnenglishwords.fragments;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.AsyncTaskLoader;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.nolan.learnenglishwords.R;
import com.nolan.learnenglishwords.activities.AddCardActivity;
import com.nolan.learnenglishwords.core.BusinessLogic;

import java.sql.SQLException;

public class CreateDictionaryFragment extends Fragment implements View.OnClickListener, LoaderManager.LoaderCallbacks<Long> {
    private static class CreateDictionaryLoader extends AsyncTaskLoader<Long> {
        public static final int ID = 0;

        public static final String ARG_TITLE = "title";
        public static final String ARG_DESCRIPTION = "description";

        private String title;
        private String description;

        public CreateDictionaryLoader(@NonNull Context context, @NonNull String title, @NonNull String description) {
            super(context);
            this.title = title;
            this.description = description;
        }

        @Override
        public Long loadInBackground() {
            try {
                return BusinessLogic.GetInstance(getContext()).addDictionary(title, description);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    private EditText etTitle;
    private EditText etDescription;
    private Button btnDone;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Long> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case 0:
                return new CreateDictionaryLoader(getActivity(),
                        args.getString(CreateDictionaryLoader.ARG_TITLE),
                        args.getString(CreateDictionaryLoader.ARG_DESCRIPTION));
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Long> loader, Long data) {
        Intent intent = new Intent(getActivity().getBaseContext(), AddCardActivity.class);
        intent.putExtra(AddCardActivity.ARG_DICTIONARY_ID, data);
        startActivity(intent);
    }

    @Override
    public void onLoaderReset(Loader<Long> loader) { }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.create_dictionary_fragment, container, false);

        etTitle = (EditText) root.findViewById(R.id.et_title);
        assert null != etTitle;
        etDescription = (EditText) root.findViewById(R.id.et_description);
        assert null != etDescription;
        btnDone = (Button) root.findViewById(R.id.btn_done);
        assert null != btnDone;

        InputFilter[] filterArray = new InputFilter[1];
        filterArray[0] = new InputFilter.LengthFilter(BusinessLogic.MAX_DICTIONARY_TITLE_LEN);
        etTitle.setFilters(filterArray);

        filterArray = new InputFilter[1];
        filterArray[0] = new InputFilter.LengthFilter(BusinessLogic.MAX_DICTIONARY_DESCRIPTION_LEN);
        etDescription.setFilters(filterArray);

        btnDone.setOnClickListener(this);
        return root;
    }

    @Override
    public void onClick(View v) {
        btnDone.setOnClickListener(null);
        String title = etTitle.getText().toString();
        String description = etDescription.getText().toString();
        Bundle arguments = new Bundle();
        arguments.putString(CreateDictionaryLoader.ARG_TITLE, title);
        arguments.putString(CreateDictionaryLoader.ARG_DESCRIPTION, description);
        if (null == getLoaderManager().getLoader(CreateDictionaryLoader.ID))
            getLoaderManager().initLoader(CreateDictionaryLoader.ID, arguments, this).forceLoad();
        else
            getLoaderManager().restartLoader(CreateDictionaryLoader.ID, arguments, this).forceLoad();
    }
}
