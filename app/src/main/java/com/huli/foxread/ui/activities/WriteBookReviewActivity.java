package com.huli.foxread.ui.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.huli.foxread.R;
import com.huli.foxread.cache.UserInfoCache;
import com.huli.foxread.callbacks.ookkggoo.LtbCallback;
import com.huli.foxread.callbacks.ookkggoo.LzyResponse;
import com.huli.foxread.contact.Common;
import com.huli.foxread.contact.Consts;
import com.huli.foxread.ui.base.BaseActivity;
import com.kongzue.dialog.v3.TipDialog;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;

import androidx.appcompat.widget.Toolbar;
import me.zhanghai.android.materialratingbar.MaterialRatingBar;

public class WriteBookReviewActivity extends BaseActivity implements View.OnClickListener {
    public static final int REQCODE_WRITE_REVIEW = 0x1954;

    private static final int MAX_NUM = 2000;

    private TextView btnIssued;
    private MaterialRatingBar ratingBar;
    private TextView tvWordCount;
    private EditText edComment;

    private String novelId;

    public static void start(Context context, String novelId) {
        Intent starter = new Intent(context, WriteBookReviewActivity.class);
        starter.putExtra(Common.KEY_BOOK_ID, novelId);
        context.startActivity(starter);
    }

    public static void start4Result(Activity context, int reqCode, String novelId) {
        Intent starter = new Intent(context, WriteBookReviewActivity.class);
        starter.putExtra(Common.KEY_BOOK_ID, novelId);
        context.startActivityForResult(starter, reqCode);
    }

    @Override
    public void initParms(Bundle parms) {
        novelId = parms.getString(Common.KEY_BOOK_ID);
    }

    @Override
    public View bindView() {
        return null;
    }

    @Override
    public int bindLayout() {
        return R.layout.activity_write_book_review;
    }

    @Override
    public void initView(View view) {
        Toolbar toolbar = $(R.id.toolbar_normal);
        initToolBar(toolbar, R.string.txt_make_comments);
        btnIssued = $(R.id.tv_asBtn_txt_issued_comment);
        ratingBar = $(R.id.materialRatingBar_grade);
        tvWordCount = $(R.id.tv_word_count);
        edComment = $(R.id.ed_comment_content);
        tvWordCount.setText((0 + "/" + MAX_NUM));
    }

    @Override
    public void setListener() {
        btnIssued.setOnClickListener(this);
        edComment.addTextChangedListener(watcher);
//        ratingBar.setOnRatingChangeListener();
    }

    @Override
    public void doBusiness(Context mContext) {
        //需要登录---返回结果BaseActivity处理
        if (UserInfoCache.getIsTourist(mContext)) {
            LoginActivity.start4Result(this, LoginActivity.REQCODE_LOGIN);
            return;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_asBtn_txt_issued_comment:
                String content = edComment.getText().toString();
                if (TextUtils.isEmpty(content)) {
                    Toast.makeText(this, R.string.txt_plz_input_you_comment, Toast.LENGTH_SHORT).show();
                    return;
                }
                float rating = ratingBar.getRating();
                if (rating == 0) {
                    Toast.makeText(this, R.string.txt_plz_select_grade, Toast.LENGTH_SHORT).show();
                    return;
                }
                reqPostComment(novelId, content, rating * 2);
                break;

            default:
                break;
        }
    }

    private TextWatcher watcher = new TextWatcher() {

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            //只要编辑框内容有变化就会调用该方法，s为编辑框变化后的内容
//            Log.i("onTextChanged", s.toString());
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            //编辑框内容变化之前会调用该方法，s为编辑框内容变化之前的内容
//            Log.i("beforeTextChanged", s.toString());
        }

        @Override
        public void afterTextChanged(Editable s) {
            //编辑框内容变化之后会调用该方法，s为编辑框内容变化后的内容
//            Log.i("afterTextChanged", s.toString());
            if (s.length() > MAX_NUM) {
                s.delete(MAX_NUM, s.length());
            }
            int num = MAX_NUM - s.length();
            tvWordCount.setText((s.length() + "/" + MAX_NUM));
        }
    };


    /**
     * 发表评论
     *
     * @param novelId 小说ID
     * @param content
     */
    private void reqPostComment(String novelId, String content, float score) {
        OkGo.<String>get(Consts.APPRAISE_CREATE_API)
                .params(Consts.NOVEL_ID, novelId)
                .params(Consts.CONTENT, content)
                .params(Consts.SCORE, score)
                .execute(new LtbCallback(this, false) {
                    @Override
                    public void onSuccess(Response<String> response) {
                        LzyResponse<String> entity = JSONObject.parseObject(response.body(),
                                new TypeReference<LzyResponse<String>>() {
                                });
                        if (entity.error_code == 0) {
                            TipDialog.show(WriteBookReviewActivity.this, entity.msg, TipDialog.TYPE.SUCCESS).setOnDismissListener(() -> {
                                setResult(RESULT_OK);
                                finish();
                            });
                        } else {
                            TipDialog.show(WriteBookReviewActivity.this, entity.msg, TipDialog.TYPE.ERROR);
                        }
                    }
                });
    }


}
