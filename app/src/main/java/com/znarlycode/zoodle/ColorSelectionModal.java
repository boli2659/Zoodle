package com.znarlycode.zoodle;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;

import androidx.annotation.Nullable;

public class ColorSelectionModal extends LinearLayout {

    private Button doneButton;
    private ColorChangedListener colorChangedListener;
    private ImageView headerImage;
    private SeekBar alpha;
    private SeekBar red;
    private SeekBar green;
    private SeekBar blue;

    private int resolvedColor = 0;

    public ColorSelectionModal(Context context) {
        this(context, null);
    }

    public ColorSelectionModal(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ColorSelectionModal(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        headerImage = findViewById(R.id.stroke_color_image);
        alpha = findViewById(R.id.alpha_seek_bar);
        red = findViewById(R.id.red_seek_bar);
        green = findViewById(R.id.green_seek_bar);
        blue = findViewById(R.id.blue_seek_bar);
        doneButton = findViewById(R.id.stroke_color_done_button);
        alpha.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                onColorUpdated(progress, red.getProgress(), green.getProgress(), blue.getProgress());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        red.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                onColorUpdated(alpha.getProgress(), progress, green.getProgress(), blue.getProgress());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        green.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                onColorUpdated(alpha.getProgress(), red.getProgress(), progress, blue.getProgress());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        blue.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                onColorUpdated(alpha.getProgress(), red.getProgress(), green.getProgress(), progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }

    void setDoneButtonListener(OnClickListener listener) {
        doneButton.setOnClickListener(listener);
    }

    void setInitialColor(int color) {
        alpha.setProgress(Color.alpha(color));
        red.setProgress(Color.red(color));
        green.setProgress(Color.green(color));
        blue.setProgress(Color.blue(color));
    }

    void setColorChangedListener(ColorChangedListener colorChangedListener) {
        this.colorChangedListener = colorChangedListener;
    }

    void onColorUpdated(int alpha, int red, int green, int blue) {
        resolvedColor = Color.argb(alpha, red, green, blue);
        headerImage.setImageDrawable(new ColorDrawable(resolvedColor));
        colorChangedListener.onColorUpdated(resolvedColor);
    }

    interface ColorChangedListener {
        void onColorUpdated(int color);
    }
}
