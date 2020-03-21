package com.huli.foxread.ui.pageradapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.request.RequestOptions;
import com.huli.foxread.GlideApp;
import com.huli.foxread.R;
import com.huli.foxread.entity.HpSpecialEntity;
import com.huli.foxread.transforms.CenterCropRoundCornerTransform;
import com.huli.foxread.utils.DensityUtils;

import java.util.List;

import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

public class SpecialTopicPagerAdapter extends PagerAdapter {
    private List<HpSpecialEntity> mList;
    private LayoutInflater layoutInflater;
    private Context context;

    public SpecialTopicPagerAdapter(Context context, List<HpSpecialEntity> list) {
        super();
        this.context = context;
        this.mList = list;
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    /**
     * 页面宽度所占ViewPager测量宽度的权重比例，默认为1
     */
    @Override
    public float getPageWidth(int position) {
        return (float) 0.95;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        View view = (View) object;
        ((ViewPager) container).removeView(view);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        HpSpecialEntity data = mList.get(position);
        // 自己实现
        View inflate = layoutInflater.inflate(R.layout.vp_item_special_topic, container, false);
        TextView tcTitle = inflate.findViewById(R.id.tv_st_title);
        tcTitle.setText(data.getTitle());

        ImageView ivImg = inflate.findViewById(R.id.iv_st_img);
        //设置图片圆角角度
        RequestOptions options = RequestOptions.bitmapTransform(new CenterCropRoundCornerTransform(DensityUtils.dp2px(context, 4)));
        GlideApp.with(context)
                .load(data.getHttp_image())
                .apply(options)
                .placeholder(R.mipmap.img_holder_special_topic)
                .error(R.mipmap.img_holder_special_topic)
                .into(ivImg);

        inflate.setOnClickListener(view -> {
            if (mOnPagerItemClickListener != null) {
                mOnPagerItemClickListener.onItemClick(data.getNovel_id());
            }
        });
        container.addView(inflate);
        return inflate;
    }

    public void setmOnPagerItemClickListener(OnPagerItemClickListener mOnPagerItemClickListener) {
        this.mOnPagerItemClickListener = mOnPagerItemClickListener;
    }

    private OnPagerItemClickListener mOnPagerItemClickListener;

    public interface OnPagerItemClickListener {
        void onItemClick(String bookID);
    }
}
