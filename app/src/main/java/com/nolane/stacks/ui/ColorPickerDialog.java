package com.nolane.stacks.ui;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;

import com.nolane.stacks.R;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * This dialog allows user to pick color from one of nine that are defined in resources.
 */
public class ColorPickerDialog extends Dialog implements View.OnClickListener {
    public interface OnPickedColorListener {
        // Called when color is picked.
        void onPickedColor(int color);
    }

    @NonNull
    private OnPickedColorListener listener;

    public ColorPickerDialog(@NonNull Context context, @NonNull OnPickedColorListener listener) {
        super(context);
        this.listener = listener;
    }

    // UI elements.
    @Bind(R.id.gl_root)
    GridLayout glRoot;
    @Bind(R.id.btn_cancel)
    Button btnCancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_color_picker);
        setTitle(getContext().getString(R.string.pick_color));
        ButterKnife.bind(this);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancel();
            }
        });
        for (int i = 0; i < 9; i++) {
            View colorView = glRoot.getChildAt(i);
            colorView.setOnClickListener(this);
        }
    }

    @Override
    public void onClick(View v) {
        ColorDrawable colorDrawable = (ColorDrawable) v.getBackground();
        listener.onPickedColor(colorDrawable.getColor());
        cancel();
    }
}
