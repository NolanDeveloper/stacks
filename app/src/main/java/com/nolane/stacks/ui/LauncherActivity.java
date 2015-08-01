package com.nolane.stacks.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

import com.nolane.stacks.R;
import com.nolane.stacks.provider.CardsDAO;
import com.nolane.stacks.provider.Stack;

import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * This activity is required to start different activities on launch by condition.
 * <p/>
 * Another useful future function of this activity is initializing and downloading
 * some information on the start and showing app logo over float screen during this
 * process.
 */
public class LauncherActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_launcher);
        ActionBar actionBar = getSupportActionBar();
        if (null != actionBar && actionBar.isShowing()) actionBar.hide();
        CardsDAO.getInstance()
                .listStacks()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<List<Stack>>() {
                    @Override
                    public void call(List<Stack> data) {
                        if (0 == data.size()) {
                            Intent intent = new Intent(getBaseContext(),
                                                       CreateFirstStackActivity.class);
                            startActivity(intent);
                        } else {
                            Intent intent = new Intent(getBaseContext(), PickStackActivity.class);
                            startActivity(intent);
                        }
                        finish();
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        throwable.printStackTrace();
                        finish();
                    }
                });
    }
}
