package com.nolane.stacks.ui;

import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nolane.stacks.R;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class AboutFragment extends Fragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_about, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @OnClick(R.id.iv_send_email)
    public void sendEmail() {
        Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                "mailto","d3n15.d3v@gmail.com", null));
        startActivity(intent);
    }

    @OnClick(R.id.iv_open_github)
    public void openGitHub() {
        Intent intent = new Intent(Intent.ACTION_VIEW,
                Uri.parse("https://github.com/Nolane/stacks"));
        startActivity(intent);
    }

    @OnClick(R.id.iv_open_google_play)
    public void openGooglePlay() {
        Intent intent = new Intent(Intent.ACTION_VIEW,
                Uri.parse("market://details?id=com.nolane.stacks"));
        startActivity(intent);
    }
}
