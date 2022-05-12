package com.example.ble.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.TouchDelegate;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.example.ble.R;

public class CommonTitle extends FrameLayout implements View.OnClickListener {
    RelativeLayout relCommonLeft, relCommonRight;
    ImageView ivCommonLeft;
    TextView tvCommonCenter;
    TextView tvCommonLeft;
    ImageView ivCommonRight;
    TextView tvCommonRight;

   public FrameLayout frameCommon;

    private OnClickListener listenerLeft, listenerRight;

    public CommonTitle(@NonNull Context context) {
        super(context);
        init(context);
    }

    public CommonTitle(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.layout_title, this);
        relCommonLeft = findViewById(R.id.relCommonLeft);
        relCommonRight = findViewById(R.id.relCommonRight);
        ivCommonLeft = findViewById(R.id.ivCommonLeft);
        tvCommonLeft = findViewById(R.id.tvCommonLeft);
        tvCommonCenter = findViewById(R.id.tvCommonCenter);
        ivCommonRight = findViewById(R.id.ivCommonRight);
        tvCommonRight = findViewById(R.id.tvCommonRight);
        frameCommon = findViewById(R.id.frameCommon);

        relCommonLeft.setOnClickListener(this);
        relCommonRight.setOnClickListener(this);
        setTouchDelegate(ivCommonLeft,60);

    }

    public void setBgColor(int color) {
        if (frameCommon != null) {
            frameCommon.setBackgroundColor(color);
        }
    }

    public void setTitle(String value) {
        tvCommonCenter.setText(value);
    }

    public void setTitleColor(int color) {
        tvCommonCenter.setTextColor(color);
    }

    public void setTitle(int resId) {
        tvCommonCenter.setText(resId);
    }

    public void setLeftVisibility(int visibility) {
        relCommonLeft.setVisibility(visibility);
    }

    public void setRightVisibility(int visibility) {
        relCommonRight.setVisibility(visibility);
    }

    public void setRight(String value) {
        relCommonRight.setVisibility(VISIBLE);
        tvCommonRight.setVisibility(VISIBLE);
        tvCommonRight.setText(value);
        ivCommonRight.setVisibility(GONE);
    }

    public void setRightText(int resId) {
        relCommonRight.setVisibility(VISIBLE);
        tvCommonRight.setVisibility(VISIBLE);
        tvCommonRight.setText(resId);
        ivCommonRight.setVisibility(GONE);
    }

    public TextView getRightText( ){
        return tvCommonRight;
    }


    public void setRightColor(int color) {
        tvCommonRight.setTextColor(color);
    }

    public void setRightTextStyle() {
        tvCommonRight.setTypeface(null, Typeface.BOLD);
    }

    public void setLeftColor(int color) {
        tvCommonLeft.setTextColor(color);
    }


    public ImageView getImgRight() {
        return ivCommonRight;
    }

    public void setLeft(String value) {
        tvCommonLeft.setVisibility(VISIBLE);
        ivCommonLeft.setVisibility(GONE);
        tvCommonLeft.setText(value);
    }

    public void setLeftListener(OnClickListener l) {
        listenerLeft = l;
    }

    public void setRightListener(OnClickListener l) {
        listenerRight = l;
    }

    /*设置左边的图标*/
    public void setLeftIcon(@DrawableRes int resId) {
        ivCommonLeft.setImageResource(resId);
    }

    /*设置左边的图标*/
    public void setHideTitle(int index) {
        tvCommonCenter.setVisibility(View.INVISIBLE);
    }

    /*设置右边的图标*/
    public void setRightIcon(int index) {
        relCommonRight.setVisibility(VISIBLE);
        switch (index) {
            case 1:
              //  ivCommonRight.setImageResource(R.drawable.jiaoyi_icon);
                break;
            case 2:
           //     ivCommonRight.setImageResource(R.drawable.right_to_icon);
                break;

        }
    }

    public void setLeftShow(boolean statu) {
        if (statu) {
            relCommonLeft.setVisibility(View.VISIBLE);
        } else {
            relCommonLeft.setVisibility(View.GONE);
        }

    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.relCommonLeft) {
            if (listenerLeft != null) {
                if (ivCommonLeft.getDrawable() == getResources().getDrawable(R.mipmap.ic_launcher)) {
                    ivCommonRight.setVisibility(VISIBLE);
                    ivCommonRight.setImageDrawable(getResources().getDrawable(R.mipmap.ic_launcher));
                } else {
                    listenerLeft.onClick(view);
                }
            } else {
                ((Activity) getContext()).finish();
            }
        } else if (id == R.id.relCommonRight) {
            if (listenerRight != null)
                listenerRight.onClick(view);
        }
    }

    public static void setTouchDelegate(final View view,
                                        final int expandTouchWidth) {
        final View parentView = (View) view.getParent();
        parentView.post(new Runnable() {
            @Override
            public void run() {
                final Rect rect = new Rect();
                view.getHitRect(rect);
                rect.top -= expandTouchWidth;
                rect.bottom += expandTouchWidth;
                rect.left -= expandTouchWidth;
                rect.right += expandTouchWidth;
                TouchDelegate touchDelegate = new TouchDelegate(rect, view);
                parentView.setTouchDelegate(touchDelegate);
            }
        });
    }
}
