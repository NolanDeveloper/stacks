package com.nolane.stacks.provider;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.nolane.stacks.utils.PreferencesUtils;

import static com.nolane.stacks.provider.CardsContract.Answers;
import static com.nolane.stacks.provider.CardsContract.Cards;
import static com.nolane.stacks.provider.CardsContract.Stacks;

public class DeleteReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        context.getContentResolver().delete(Stacks.CONTENT_URI, Stacks.STACK_DELETED + " = 1", null);
        context.getContentResolver().delete(Cards.CONTENT_URI, Cards.CARD_DELETED + " = 1", null);
        context.getContentResolver().delete(Answers.CONTENT_URI, Answers.ANSWER_DELETED + " = 1", null);
        PreferencesUtils.deletionDone(context);
    }
}
