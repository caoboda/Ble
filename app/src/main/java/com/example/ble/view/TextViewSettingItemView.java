package com.example.ble.view;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.RelativeLayout;
import androidx.annotation.DrawableRes;
import androidx.annotation.StringRes;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.cardview.widget.CardView;
import com.example.ble.R;


public class TextViewSettingItemView extends RelativeLayout {

    AppCompatTextView mKeyNameTv;
    AppCompatTextView mValueTv;
    CardView mRootCardView;
    Context mContext;
    private SettingItemClickListener mListener;
    private AppCompatImageView mIconIv;
    private AppCompatImageView mArrowIv;
    private AppCompatImageView mValueAlertIv;
    private AppCompatTextView mRightTextView;
    private ArrowIconClickListener mArrowIconListener;

    public TextViewSettingItemView(Context context) {
        super(context);
        mContext = context;
    }

    public TextViewSettingItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init(attrs, 0);
    }

    public TextViewSettingItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        init(attrs, defStyleAttr);
    }

    private void init(AttributeSet attrs, int defStyle) {
        View.inflate(getContext(), R.layout.view_text_setting_item, this);
        mKeyNameTv = findViewById(R.id.key_name_tv);
        mIconIv = findViewById(R.id.icon_iv);
        mArrowIv = findViewById(R.id.arrow_iv);
        mValueTv = findViewById(R.id.value_tv);
        mValueAlertIv = findViewById(R.id.value_alert_iv);
        mRootCardView = findViewById(R.id.root_card_view);
        mRightTextView = findViewById(R.id.right_textview);
        TypedArray typeValue = mContext.obtainStyledAttributes(attrs, R.styleable.SettingView);
        boolean isIconShow = typeValue.getBoolean(R.styleable.SettingView_isIconShow, false);
        boolean isArrowShow = typeValue.getBoolean(R.styleable.SettingView_isArrowShow, false);
        boolean isLeftTextViewShow = typeValue.getBoolean(R.styleable.SettingView_isLeftTextViewShow, false);
        boolean isRightTextViewShow = typeValue.getBoolean(R.styleable.SettingView_isRightTextViewShow, false);
        String isRightTextViewHint = typeValue.getString(R.styleable.SettingView_setRightTextViewhint);
        Log.d("TextViewSettingItemView", "init: "+isRightTextViewHint);
        String content = typeValue.getString(R.styleable.SettingView_settingTitle);
        if (null!=content){
        mKeyNameTv.setText(content);
        }
        if (null!=isRightTextViewHint){
        mRightTextView.setHint(isRightTextViewHint);
        }
        mRightTextView.setText(typeValue.getString(R.styleable.SettingView_settingRightTextView));
        mIconIv.setVisibility(isIconShow ? VISIBLE : GONE);
        mArrowIv.setVisibility(isArrowShow ? VISIBLE : INVISIBLE);
        mRightTextView.setVisibility(isRightTextViewShow ? VISIBLE : GONE);
        mArrowIv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null!=mArrowIconListener){
                mArrowIconListener.onArrowIconClick(v);
                }
            }
        });
        Drawable background = typeValue.getDrawable(R.styleable.SettingView_background);
        if (background != null) {
            mRootCardView.setBackground(background);
        }
        Drawable background2 = typeValue.getDrawable(R.styleable.SettingView_arrowicon);
        if (background2 != null) {
            mArrowIv.setVisibility(VISIBLE);
            mArrowIv.setImageDrawable(background2);
        }
        Drawable drawable = typeValue.getDrawable(R.styleable.SettingView_settingIcon);
        if (drawable != null) {
            mIconIv.setImageDrawable(drawable);
        }

        boolean isTitleBold = typeValue.getBoolean(R.styleable.SettingView_isTitleTextBold, true);
        ColorStateList colorStateList = typeValue.getColorStateList(R.styleable.SettingView_keyTextColor);
        if (colorStateList != null) {
            mKeyNameTv.setTextColor(colorStateList);
        }
//            mKeyNameTv.setTextSize(COMPLEX_UNIT_DIP,16);
        ColorStateList contentColorStateList = typeValue.getColorStateList(R.styleable.SettingView_contentColor);
        if (contentColorStateList != null) {
            mValueTv.setTextColor(contentColorStateList);
        }

        mKeyNameTv.setTypeface(isTitleBold ? Typeface.DEFAULT_BOLD : Typeface.DEFAULT);

        typeValue.recycle();
        mRootCardView.setOnClickListener(v -> {
            if (mListener != null) {
                mListener.onClick(v);
            }
        });
    }

    public void setValue(@StringRes int resid) {
        mValueTv.setText(mContext.getResources().getText(resid));
    }

    public void setTitle(String title) {
        mKeyNameTv.setText(title);
    }

    public void setTitle(@StringRes int resid) {
        mKeyNameTv.setText(mContext.getResources().getText(resid));
    }

    public void setmRightTextView(String str){
        mRightTextView.setText(str);
    }
    public String getmRightTextView(){
       return mRightTextView.getText().toString();
    }
    public void setmRightTextViewVisiblity(int visibility){
        mRightTextView.setVisibility(visibility);
    }

    public void setmRightTextViewTextColor(int color){
        mRightTextView.setTextColor(color);
    }

    public void setHint(String valueHint) {
        mValueTv.setHint(valueHint);
    }

    public void setValue(String valueContent) {
        mValueTv.setText(valueContent);
    }

    public void setValueIconVisible(boolean show) {
        mValueAlertIv.setVisibility(show ? VISIBLE : GONE);
    }

    public void setValueIcon(@DrawableRes int resDrawble) {
        mValueAlertIv.setVisibility(VISIBLE);
        mValueAlertIv.setImageResource(resDrawble);
    }

    public void setEnabled(boolean status) {
        mRootCardView.setEnabled(status);
        mRootCardView.setClickable(status);
        mKeyNameTv.setEnabled(status);
    }

    public void setIconIvDrawable(@DrawableRes int resDrawble){
        mIconIv.setImageResource(resDrawble);
    }

    public void setEnabled(boolean status, boolean isClickAble){
        if(isClickAble && !status){
            mKeyNameTv.setTextColor(getResources().getColor(R.color.gray));
        }
        else{
            mRootCardView.setEnabled(status);
            mRootCardView.setClickable(status);
            mKeyNameTv.setEnabled(status);
            mKeyNameTv.setTextColor(getResources().getColor(R.color.mid_gray));
        }
    }

    public void setSelect(boolean status) {
        setSelected(status);
        mKeyNameTv.setSelected(status);
    }

    public String getValue() {
        return mValueTv.getText().toString();
    }

    public void setIconIvDrawableVisiable( Boolean isshow){
        if(isshow){
            mIconIv.setVisibility(VISIBLE);
        }else {
            mIconIv.setVisibility(GONE);
        }
    }
    public void setArrowVisiable(boolean isshow) {
        if(isshow){
            mArrowIv.setVisibility(VISIBLE);
        }else {
            mArrowIv.setVisibility(GONE);
        }
    }
        public void setArrowIcon(@DrawableRes int resDrawble) {
            mArrowIv.setVisibility(VISIBLE);
            mArrowIv.setImageResource(resDrawble);
        }
    //点击事件接口
    public interface SettingItemClickListener {
        void onClick(View v);
    }
    public interface ArrowIconClickListener {
        void onArrowIconClick(View v);
    }
    public void setOnArrowIconClickListener(ArrowIconClickListener listener) {
        mArrowIconListener = listener;

    }
    public void setOnSettingItemListener(SettingItemClickListener listener) {
        mListener = listener;
//        TypedValue outValue = new TypedValue();
//        getContext().getTheme().resolveAttribute(android.R.attr.selectableItemBackground, outValue, true);
//        mRootCardView.setBackgroundResource(outValue.resourceId);

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                TypedValue outValue = new TypedValue();
                getContext().getTheme().resolveAttribute(android.R.attr.selectableItemBackground, outValue, true);
                mRootCardView.setForeground(getContext().getDrawable(outValue.resourceId));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
