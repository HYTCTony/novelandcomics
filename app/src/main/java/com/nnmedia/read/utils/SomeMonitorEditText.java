package com.nnmedia.read.utils;

import android.graphics.Color;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;

import com.nnmedia.novel.R;


/**
 * 编辑框的相关工具类
 */
public class SomeMonitorEditText implements TextWatcher {

    private Button button;
    private EditText[] text;


    public SomeMonitorEditText(Button button, EditText... text) {

        this.button = button;
        this.text = text;

        button.setBackgroundResource(R.drawable.shape_btn_bg_disabled);
        button.setTextColor(Color.parseColor("#8B7342"));
        button.setEnabled(false);

        for (int i = 0; i < text.length; i++) {
            if (text[i] != null) {
                text[i].addTextChangedListener(SomeMonitorEditText.this);
            }
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count,
                                  int after) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        // TODO Auto-generated method stub
        for (int i = 0; i < text.length; i++) {

            if (text[i].length() == 0) {
				button.setBackgroundResource(R.drawable.shape_btn_bg_disabled);
                button.setTextColor(Color.parseColor("#8B7342"));
                button.setEnabled(false);
                return;//这句代码值两千万
            } else {
				button.setBackgroundResource(R.drawable.ripple_normal_btn_bg_red);
                button.setTextColor(Color.parseColor("#ffffff"));
                button.setEnabled(true);
            }
        }
    }

}
