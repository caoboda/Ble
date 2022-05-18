package com.me.blelib.widget.dialog;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.me.blelib.R;

public class LoadingProgressDialog extends Dialog implements IDiaLog {
    private ImageView loading_quan_img;
    private TextView loading_text_tv;
    private ValueAnimator animator;

    public LoadingProgressDialog(Context context) {
        this(context, R.style.loading_dialog);
    }

    public LoadingProgressDialog(Context context, int attrs) {
        super(context, attrs);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loading_dialog);
        setCanceledOnTouchOutside(false);

        loading_quan_img = findViewById(R.id.loading_quan_img);
        loading_text_tv = findViewById(R.id.loading_text_tv);
        animator = ObjectAnimator.ofFloat(loading_quan_img, "rotation", 0, 360);
        animator.setRepeatCount(ValueAnimator.INFINITE);//无限循环
        animator.setRepeatMode(ValueAnimator.RESTART);
        animator.setInterpolator(new LinearInterpolator());//不卡顿
        animator.setDuration(500);
    }

    @Override
    public void show() {
        super.show();
        if (animator != null) {
            animator.start();
        }
    }

    @Override
    public void hide() {
        super.hide();
        if (animator != null) {
            animator.end();
        }
    }

    @Override
    public void dismiss() {
        super.dismiss();
        if (animator != null) {
            animator.end();
        }
    }

    @Override
    public void clear() {
        dismiss();
        if (animator != null) {
            animator.cancel();
        }
    }

    public void setLoadingText(String title){
        loading_text_tv.setText(title);
    }

}

 interface IDiaLog{
    void clear();
}