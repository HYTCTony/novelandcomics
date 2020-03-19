package com.huli.foxread.ui.adapters;

import com.chad.library.adapter.base.BaseSectionQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.huli.foxread.R;
import com.huli.foxread.entity.SysAvatarEntity;
import com.huli.foxread.entity.sections.AvatarSection;
import com.huli.foxread.utils.GlideUtil;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatCheckBox;

public class SectionAvatarAdapter extends BaseSectionQuickAdapter<AvatarSection<SysAvatarEntity>, BaseViewHolder> {

    private String inUseAvatarUrl;
    private int checkPos = 0;
    private boolean isInit = true;

    public SectionAvatarAdapter(String inUseAvatarUrl, OnRecyAvatarCheckListener mOnRecyAvatarCheckListener) {
        super(R.layout.recy_section_head_view_sys_avatar);
        setNormalLayout(R.layout.recy_section_grid_item_sys_avatar);
        this.inUseAvatarUrl = inUseAvatarUrl;
        this.mOnRecyAvatarCheckListener = mOnRecyAvatarCheckListener;
    }

    @Override
    protected void convertHeader(@NonNull BaseViewHolder holder, AvatarSection<SysAvatarEntity> data) {
        holder.setText(R.id.tv_section_group_title, data.getTitle());
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, AvatarSection<SysAvatarEntity> data) {
        GlideUtil.loadCircle(getContext(), helper.getView(R.id.iv_sys_avatar), data.getObject().getHttp_image());

        int position = helper.getLayoutPosition();
        if (data.getObject().getHttp_image().equals(inUseAvatarUrl)) {
            if (isInit) {
                checkPos = position;
                isInit = false;
            }
            helper.setVisible(R.id.iv_in_use_avatar_sign, true);
        } else {
            helper.setGone(R.id.iv_in_use_avatar_sign, true);
        }

        AppCompatCheckBox checkBox = helper.getView(R.id.cb_cur_check_avatar);
        checkBox.setEnabled(false);

        if (checkPos == position) {
            checkBox.setChecked(true);
        } else {
            checkBox.setChecked(false);
        }

        helper.itemView.setOnClickListener(v -> {
            if (checkPos == position) {
                checkBox.setClickable(false);
            } else {
                checkPos = position;
                checkBox.setChecked(true);
                if (mOnRecyAvatarCheckListener != null) {
                    mOnRecyAvatarCheckListener.OnAvatarCheck(position, data.getObject().getHttp_image(), data.getObject().getImage());
                }
            }
            notifyDataSetChanged();
        });
    }


    private OnRecyAvatarCheckListener mOnRecyAvatarCheckListener;

    public void setmOnRecyAvatarCheckListener(OnRecyAvatarCheckListener mOnRecyAvatarCheckListener) {
        this.mOnRecyAvatarCheckListener = mOnRecyAvatarCheckListener;
    }

    public interface OnRecyAvatarCheckListener {
        void OnAvatarCheck(int pos, String avatarUrl, String paramValue);
    }

}
