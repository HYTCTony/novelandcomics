package com.huli.foxread.ui.activities;

import android.content.Context;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.huli.foxread.R;
import com.huli.foxread.entity.ImageBean;
import com.huli.foxread.ui.base.BaseActivity;
import com.huli.foxread.ui.imgshowpickerview.ImageLoader;
import com.huli.foxread.ui.imgshowpickerview.ImageShowPickerBean;
import com.huli.foxread.ui.imgshowpickerview.ImageShowPickerListener;
import com.huli.foxread.ui.imgshowpickerview.ImageShowPickerView;
import com.huli.foxread.utils.DensityUtils;
import com.huli.foxread.utils.GlideUtil;
import com.kongzue.stacklabelview.StackLabel;
import com.kongzue.stacklabelview.interfaces.OnLabelClickListener;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

public class GoToFeedBackActivity extends BaseActivity {

    private TextView btnRecords;

    private StackLabel labelIssueType;

    private TextView tvPhotosMaxNum;
    private ImageShowPickerView pickerView;

    @Override
    public void initParms(Bundle parms) {

    }

    @Override
    public View bindView() {
        return null;
    }

    @Override
    public int bindLayout() {
        return R.layout.activity_go2_feedbask;
    }

    @Override
    public void initView(View view) {
        Toolbar toolbar = $(R.id.toolbar_normal);
        initToolBar(toolbar, R.string.txt_i_want_2_feedback);

        btnRecords = $(R.id.tv_asBtn_feedback_records);
        btnRecords.setVisibility(View.GONE);

        labelIssueType = $(R.id.stackLabel_issue_type);
        tvPhotosMaxNum = $(R.id.tv_photos_max_num);

        initImgPickerView();
    }

    @Override
    public void setListener() {
        labelIssueType.setOnLabelClickListener(new OnLabelClickListener() {
            @Override
            public void onClick(int index, View v, String s) {
                Log.e(TAG, "点击Label===" + index);
            }
        });
    }

    @Override
    public void doBusiness(Context mContext) {

        labelIssueType.setLabels(new String[]{"产品建议","产品建议","产品建议","产品建议","产品建议","产品建议","产品建议","产品建议","产品建议","产品建议","产品建议"});


        int num = 3;
        SpannableString spannableString = new SpannableString(String.format(getString(R.string.txt_at_most_sheet_x), num));
        spannableString.setSpan(new ForegroundColorSpan(ContextCompat.getColor(this, R.color.txt_red)),
                4, 4 + String.valueOf(num).length(), SpannableString.SPAN_INCLUSIVE_EXCLUSIVE);
        tvPhotosMaxNum.setText(spannableString);
    }

    private void initImgPickerView() {
        pickerView = findViewById(R.id.it_picker_view);
        pickerView.setImageLoaderInterface(new ImageLoader() {
            @Override
            public void displayImage(Context context, String path, ImageView imageView) {
                GlideUtil.loadRoundSquare(context, imageView, path);
            }

            @Override
            public void displayImage(Context context, Integer resId, ImageView imageView) {
                GlideUtil.loadRoundSquare(context, imageView, resId);
            }
        });
        List<ImageBean> list = new ArrayList<>();
        list.add(new ImageBean(1, "http://pic78.huitu.com/res/20160604/1029007_20160604114552332126_1.jpg"));
        list.add(new ImageBean(2, "http://pic78.huitu.com/res/20160604/1029007_20160604114552332126_1.jpg"));
        pickerView.setNewData(list);
        pickerView.setPickerListener(new ImageShowPickerListener() {
            @Override
            public void addOnClickListener(int remainNum) {
                Log.e("sssssssssssss", "add----remainNum===" + remainNum);
//                pickerView.addData(new ImageBean(3, "http://pic78.huitu.com/res/20160604/1029007_20160604114552332126_1.jpg"));
            }

            @Override
            public void picOnClickListener(List<ImageShowPickerBean> list, int position, int remainNum) {
                Log.e("sssssssssssss", "pic----remainNum===" + remainNum);
            }

            @Override
            public void delOnClickListener(int position, int remainNum) {
                Log.e("sssssssssssss", "del----remainNum===" + remainNum);
                list.remove(position);
            }
        });
        DisplayMetrics outMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(outMetrics);
        int widthPixels = outMetrics.widthPixels;
        pickerView.show(widthPixels, DensityUtils.dp2px(this, 16), true);
    }


}
