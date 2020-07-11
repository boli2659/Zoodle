package com.znarlycode.zoodle;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class DoodleController implements ColorSelectionModal.ColorChangedListener {

    private final Activity activity;
    private final DoodleView doodleView;
    private final FrameLayout container;

    DoodleController(Activity activity, DoodleView doodleView, FrameLayout container) {
        this.activity = activity;
        this.doodleView = doodleView;
        this.container = container;
    }

    void clearDoodle() {
        doodleView.clear();
    }

    boolean setIsFillStyle() {
        return doodleView.setIsFill();
    }

    boolean eraseLast() {
        return doodleView.eraseLast();
    }

    void showStrokeColorModal() {
        final ColorSelectionModal modal = (ColorSelectionModal) LayoutInflater.from(container.getContext())
                .inflate(R.layout.stroke_color_bottom_sheet, container, false);
        modal.setColorChangedListener(this);
        modal.setInitialColor(doodleView.getStrokeColor());
        modal.setDoneButtonListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissModal(modal);
            }
        });
        showModal(modal);
    }

    void showBackgroundColorModal() {
        final ColorSelectionModal modal = (ColorSelectionModal) LayoutInflater.from(container.getContext())
                .inflate(R.layout.stroke_color_bottom_sheet, container, false);
        modal.setColorChangedListener(new ColorSelectionModal.ColorChangedListener() {
            @Override
            public void onColorUpdated(int color) {
                doodleView.setBackgroundColor(color);
            }
        });
        modal.setInitialColor(doodleView.getBackgroundColor());
        modal.setDoneButtonListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissModal(modal);
            }
        });
        showModal(modal);
    }

    void showStrokeWidthModal() {
        final LinearLayout modal = (LinearLayout)
                LayoutInflater.from(container.getContext())
                        .inflate(R.layout.stroke_width_layout,
                                container,
                                false);
        final SeekBar seekBar = modal.findViewById(R.id.stroke_width_seek_bar);
        final ImageView image = modal.findViewById(R.id.stroke_width_image);
        Button doneButton = modal.findViewById(R.id.stroke_width_done_button);
        seekBar.setProgress(doodleView.getStrokeWidth());
        final Bitmap sample = Bitmap.createBitmap(400, 100, Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(sample);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                drawWidthOnModal(progress, sample, image, canvas);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
        showModal(modal);
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doodleView.setStrokeWidth(seekBar.getProgress());
                dismissModal(modal);

            }
        });
    }

    private void drawWidthOnModal(int progress, Bitmap sample, ImageView image, Canvas canvas) {
        doodleView.setStrokeWidth(progress);
        Paint paint = new Paint();
        paint.setColor(doodleView.getStrokeColor());
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeWidth(progress);
        sample.eraseColor(Color.BLACK);
        int endX = 70;
        for (int startX = 0; endX < image.getWidth() - 40; startX += 40) {
            canvas.drawLine(startX, image.getHeight(), endX, 0, paint);
            endX+= 40;
        }
        image.setImageBitmap(sample);
    }

    private void showModal(View modalView) {
        container.addView(modalView);
        Animation slideUp = AnimationUtils.loadAnimation(container.getContext(), R.anim.slide_in_bottom);
        modalView.startAnimation(slideUp);
    }

    private void dismissModal(View modalView) {
        Animation slideOut = AnimationUtils.loadAnimation(container.getContext(), R.anim.slide_out_bottom);
        modalView.startAnimation(slideOut);
        container.removeView(modalView);
    }

    @Override
    public void onColorUpdated(int color) {
        doodleView.setStrokeColor(color);
    }

    void saveImage() {
        ContextWrapper contextWrapper = new ContextWrapper(activity);
        String fileName = "ZoodleIMG_" + System.currentTimeMillis();

        File dir = contextWrapper.getDir("imageDir", Context.MODE_PRIVATE);
        File imagePath = new File(dir, fileName + ".png");

        FileOutputStream fileOutputStream = null;

        try {
            fileOutputStream = new FileOutputStream(imagePath);
            doodleView.getBitmap().compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
        } catch (FileNotFoundException e) {
            Toast.makeText(activity, "Could not save Zoodle", Toast.LENGTH_LONG).show();
        } finally {
            try {
                fileOutputStream.flush();
                fileOutputStream.close();
                Toast.makeText(activity, "Zoodle saved to " + imagePath.getAbsolutePath(), Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(activity, "Oof", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
