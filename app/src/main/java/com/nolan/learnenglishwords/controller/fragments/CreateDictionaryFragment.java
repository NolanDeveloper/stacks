package com.nolan.learnenglishwords.controller.fragments;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.AsyncTaskLoader;
import android.content.ContentValues;
import android.content.Intent;
import android.content.Loader;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.nolan.learnenglishwords.R;
import com.nolan.learnenglishwords.controller.activities.AddCardActivity;
import com.nolan.learnenglishwords.model.CardsContract;

public class CreateDictionaryFragment extends Fragment implements View.OnClickListener, LoaderManager.LoaderCallbacks<Object> {
    private EditText etTitle;
    private EditText etDescription;
    private Button btnDone;

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
        filterArray[0] = new InputFilter.LengthFilter(CardsContract.Dictionary.MAX_TITLE_LEN);
        etTitle.setFilters(filterArray);

        filterArray = new InputFilter[1];
        filterArray[0] = new InputFilter.LengthFilter(CardsContract.Dictionary.MAX_DESCRIPTION_LEN);
        etDescription.setFilters(filterArray);

        btnDone.setOnClickListener(this);
        return root;
    }

    @Override
    public void onClick(View v) {
        btnDone.setOnClickListener(null);
        String title = etTitle.getText().toString();
        String description = etDescription.getText().toString();

        // That's the easiest way I could find to pass ContentValues through Bundle.
        Bundle args = new Bundle();
        ContentValues values = new ContentValues();
        values.put(CardsContract.Dictionary.DICTIONARY_TITLE, title);
        values.put(CardsContract.Dictionary.DICTIONARY_DESCRIPTION, description);
        args.putParcelable("values", values);
        getLoaderManager().initLoader(0, args, this).forceLoad();
    }

    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        final ContentValues values = args.getParcelable("values");
        return new AsyncTaskLoader(getActivity()) {
            @Override
            public Uri loadInBackground() {
                return getContext().getContentResolver().insert(CardsContract.Dictionary.CONTENT_URI, values);
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<Object> loader, Object data) {
        if (null == data) {
            btnDone.setOnClickListener(this);
            Toast.makeText(getActivity(), getString(R.string.failed), Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent(getActivity().getBaseContext(), AddCardActivity.class);
        intent.setData((Uri) data);
        startActivity(intent);
    }

    @Override
    public void onLoaderReset(Loader<Object> loader) {

    }
}
