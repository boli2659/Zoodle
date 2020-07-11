package com.znarlycode.zoodle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.FrameLayout;

public class MainActivity extends AppCompatActivity {

    private DoodleController doodleController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FrameLayout container = (FrameLayout) LayoutInflater.from(this).inflate(R.layout.activity_main, null);
        DoodleView doodleView = container.findViewById(R.id.doodle_view);
        setContentView(container);
        doodleController = new DoodleController(this, doodleView, container);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_clear:
                doodleController.clearDoodle();
                break;
            case R.id.menu_color:
                doodleController.showStrokeColorModal();
                break;
            case R.id.menu_background_color:
                doodleController.showBackgroundColorModal();
                break;
            case R.id.menu_save:
                doodleController.saveImage();
                break;
            case R.id.menu_erase:
                break;
            case R.id.menu_stroke_width:
                doodleController.showStrokeWidthModal();
                break;
            case R.id.menu_style_type:
                item.setTitle(doodleController.setIsFillStyle() ? R.string.fill : R.string.stroke);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (!doodleController.eraseLast()) {
            super.onBackPressed();
        }
    }
}
