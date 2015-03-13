package jp.tkcdroid.drawapp;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import jp.tkcdroid.drawapp.view.DrawView;
import jp.tkcdroid.drawapp.view.PenPreviewView;

public class MainActivity extends Activity {

    private enum ColorButton {
        BLACK(Color.BLACK),
        RED(Color.RED),
        ORANGE(Color.rgb(243, 152, 0)),
        YELLOW(Color.YELLOW),
        GREEN(Color.rgb(0, 255, 0)),
        BLUE(Color.rgb(0, 0, 255)),
        PURPLE(Color.rgb(167, 87, 168)),
        WHITE(Color.WHITE),
        GRAY(Color.GRAY);
        private int colorCode;

        private ColorButton(int colorCode) {
            this.colorCode = colorCode;
        }

        public int getColorCode() {
            return colorCode;
        }
    }

    Animation inAnimation;
    Animation outAnimation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        DrawView drawView = (DrawView) findViewById(R.id.drawView);
        TextView penSizeTextView = (TextView) findViewById(R.id.pen_size);
        penSizeTextView.setText(String.valueOf(drawView.getPenSize() + 1));
        PenPreviewView penPreviewView = (PenPreviewView) findViewById(R.id.pen_preview);
        penPreviewView.setPenSize(drawView.getPenSize());

        inAnimation = (Animation) AnimationUtils.loadAnimation(this, R.anim.in_animation);
        outAnimation = (Animation) AnimationUtils.loadAnimation(this, R.anim.out_animation);

        // しんき
        findViewById(R.id.btn_reset).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DrawView drawView = (DrawView) findViewById(R.id.drawView);
                drawView.reset();
            }
        });

        // もどす
        findViewById(R.id.btn_undo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DrawView drawView = (DrawView) findViewById(R.id.drawView);
                drawView.undo();
            }
        });

        // ほぞん
        findViewById(R.id.btn_save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                File file = new File(Environment.getExternalStorageDirectory().getPath() + getResources().getString(R.string.directory));
                try {
                    if (!file.exists()) {
                        file.mkdir();
                    }
                } catch (SecurityException e) {
                    Toast.makeText(getApplicationContext(), "ERROR at save picture.", Toast.LENGTH_SHORT).show();
                }

                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(getResources().getString(R.string.pic_name_format));
                String fileName = simpleDateFormat.format(new Date());
                String attachName = file.getAbsolutePath() + "/" + fileName;

                DrawView drawView = (DrawView) findViewById(R.id.drawView);
                drawView.save(attachName);

                ContentValues values = new ContentValues();
                ContentResolver contentResolver = getContentResolver();
                values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
                values.put(MediaStore.Images.Media.TITLE, fileName);
                values.put("_data", attachName);
                contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

                Toast.makeText(getApplicationContext(), getResources().getText(R.string.saved), Toast.LENGTH_SHORT).show();
            }
        });

        // ぺん
        findViewById(R.id.btn_pen).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View penMenu = findViewById(R.id.menu_pen);
                if (penMenu.getVisibility() == View.GONE) {
                    penMenu.setVisibility(View.VISIBLE);
                    penMenu.startAnimation(inAnimation);
                } else {
                    penMenu.startAnimation(outAnimation);
                    penMenu.setVisibility(View.GONE);
                }
            }
        });

        // ぺんサイズ
        SeekBar seekBar = (SeekBar) findViewById(R.id.pen_size_seek);
        seekBar.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                        PenPreviewView penPreviewView = (PenPreviewView) findViewById(R.id.pen_preview);
                        penPreviewView.setPenSize(i);
                        DrawView drawView = (DrawView) findViewById(R.id.drawView);
                        drawView.setPenSize(i);
                        TextView textView = (TextView) findViewById(R.id.pen_size);
                        textView.setText(String.valueOf(i + 1));
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                    }
                }
        );

        // ぺんカラー
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.menu_pen_color);
        RadioGroup radioGroup = new RadioGroup(getApplicationContext());
        radioGroup.setOrientation(LinearLayout.HORIZONTAL);
        linearLayout.addView(radioGroup);
        int buttonSizeMargin = getResources().getDimensionPixelSize(R.dimen.button_size_margin);
        linearLayout.setPadding(buttonSizeMargin, buttonSizeMargin, buttonSizeMargin, buttonSizeMargin);

        int currentColorButtonId = 0;
        for (final ColorButton colorButton : ColorButton.values()) {
            RadioButton radioButton = getNewRadioButton(colorButton);
            radioGroup.addView(radioButton);
            if (drawView.getPenColor() == colorButton.getColorCode()) {
                currentColorButtonId = radioButton.getId();
            }
            //余白
            TextView exTextView = new TextView(getApplicationContext());
            exTextView.setWidth(buttonSizeMargin);
            radioGroup.addView(exTextView);
        }
        radioGroup.check(currentColorButtonId);

    }

    private RadioButton getNewRadioButton(final ColorButton colorButton) {
        RadioButton radioButton = new RadioButton(this);
        radioButton.setText(" ");
        radioButton.setButtonDrawable(android.R.color.transparent);
        StateListDrawable stateListDrawable = new StateListDrawable();
        stateListDrawable.addState(new int[]{android.R.attr.state_focused}, getDrawable(colorButton, true));
        stateListDrawable.addState(new int[]{android.R.attr.state_checked}, getDrawable(colorButton, true));
        stateListDrawable.addState(new int[]{}, getDrawable(colorButton, false));
        radioButton.setBackground(stateListDrawable);
        radioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DrawView drawView = (DrawView) findViewById(R.id.drawView);
                drawView.setPenColor(colorButton.getColorCode());
                PenPreviewView penPreviewView = (PenPreviewView) findViewById(R.id.pen_preview);
                penPreviewView.setPenColor(colorButton.getColorCode());
            }
        });

        return radioButton;
    }

    private GradientDrawable getDrawable(ColorButton colorButton, boolean isSelected) {
        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setColor(colorButton.getColorCode());
        gradientDrawable.setSize(getResources().getDimensionPixelSize(R.dimen.menu_size), getResources().getDimensionPixelSize(R.dimen.menu_size));
        if (isSelected) {
            int strokeSize = getResources().getDimensionPixelSize(R.dimen.button_stroke);
            gradientDrawable.setStroke(strokeSize, getResources().getColor(R.color.main2));
        }
        return gradientDrawable;
    }

    public void hidePenMenu() {
        if (findViewById(R.id.menu_pen).getVisibility() == View.VISIBLE) {
            findViewById(R.id.menu_pen).startAnimation(outAnimation);
            findViewById(R.id.menu_pen).setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
}
