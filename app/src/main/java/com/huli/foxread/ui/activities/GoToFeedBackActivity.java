package com.huli.foxread.ui.activities;

import android.content.Context;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.huli.foxread.R;
import com.huli.foxread.callbacks.ookkggoo.LtbCallback;
import com.huli.foxread.callbacks.ookkggoo.LzyResponse;
import com.huli.foxread.contact.Consts;
import com.huli.foxread.entity.FeedBackTypeBean;
import com.huli.foxread.entity.ImageBean;
import com.huli.foxread.ui.base.BaseActivity;
import com.huli.foxread.ui.imgshowpickerview.ImageLoader;
import com.huli.foxread.ui.imgshowpickerview.ImageShowPickerBean;
import com.huli.foxread.ui.imgshowpickerview.ImageShowPickerListener;
import com.huli.foxread.ui.imgshowpickerview.ImageShowPickerView;
import com.huli.foxread.utils.DensityUtils;
import com.huli.foxread.utils.GlideUtil;
import com.kongzue.dialog.interfaces.OnDismissListener;
import com.kongzue.dialog.v3.TipDialog;
import com.kongzue.stacklabelview.StackLabel;
import com.kongzue.stacklabelview.interfaces.OnLabelClickListener;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

public class GoToFeedBackActivity extends BaseActivity {

    private TextView btnRecords;

    private StackLabel labelIssueType;

    private TextView etContent;
    private TextView etPhone;
    private TextView tvPhotosMaxNum;
    private Button btncCommit;
    private ImageShowPickerView pickerView;

    List<FeedBackTypeBean> types = new ArrayList<>();

    String phone;
    int opinionId;
    String content;

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

        etContent = $(R.id.et_content);
        etPhone = $(R.id.et_phone);
        labelIssueType = $(R.id.stackLabel_issue_type);
        tvPhotosMaxNum = $(R.id.tv_photos_max_num);
        btncCommit = $(R.id.btn_commit);

        initImgPickerView();
    }

    @Override
    public void setListener() {
        labelIssueType.setOnLabelClickListener(new OnLabelClickListener() {
            @Override
            public void onClick(int index, View v, String s) {
                opinionId = types.get(index).getId();
            }
        });
        btncCommit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                phone = etPhone.getText().toString().trim();
                content = etContent.getText().toString().trim();
                if (TextUtils.isEmpty(content)) {
                    Toast.makeText(GoToFeedBackActivity.this, "请输入内容！", Toast.LENGTH_SHORT).show();
                    return;
                }
                reqFeedBackCreat(phone, opinionId, content);
            }
        });
    }

    @Override
    public void doBusiness(Context mContext) {
        reqFeedBackCategory();

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

    /**
     * 获取反馈分类
     */
    private void reqFeedBackCategory() {
        OkGo.<String>get(Consts.OPINION_CATEGORY_API)
                .execute(new LtbCallback(this, false) {
                    @Override
                    public void onSuccess(Response<String> response) {
                        LzyResponse<List<FeedBackTypeBean>> entity = JSONObject.parseObject(response.body(),
                                new TypeReference<LzyResponse<List<FeedBackTypeBean>>>() {
                                });
                        if (entity.error_code == 0) {
                            types.addAll(entity.getData());
                            String[] labels = new String[types.size()];
                            for (int i = 0; i < types.size(); i++) {
                                labels[i] = types.get(i).getTitle();
                            }
                            labelIssueType.setLabels(labels);
                        }
                    }
                });
    }

    /**
     * 提交反馈
     *
     * @param phone     电话
     * @param opinionId 问题类型
     * @param content   反馈内容
     */
    private void reqFeedBackCreat(String phone, int opinionId, String content) {
        OkGo.<String>post(Consts.OPINION_CREATE_API)
                .params(Consts.PHONE, phone)
                .params(Consts.OPINION_CATEGORY_ID, opinionId)
                .params(Consts.CONTENT, content)
                .execute(new LtbCallback(this, false) {
                    @Override
                    public void onSuccess(Response<String> response) {
                        LzyResponse<String> entity = JSONObject.parseObject(response.body(),
                                new TypeReference<LzyResponse<String>>() {
                                });
                        if (entity.error_code == 0) {
                            TipDialog.show((AppCompatActivity) GoToFeedBackActivity.this, entity.msg, TipDialog.TYPE.SUCCESS).setOnDismissListener(new OnDismissListener() {
                                @Override
                                public void onDismiss() {
                                    finish();
                                }
                            });
                        } else {
                            TipDialog.show((AppCompatActivity) GoToFeedBackActivity.this, entity.msg, TipDialog.TYPE.ERROR);
                        }
                    }
                });
    }

}