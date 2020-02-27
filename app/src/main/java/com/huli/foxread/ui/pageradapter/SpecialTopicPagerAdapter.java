package com.huli.foxread.ui.pageradapter;

import android.content.Context;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.request.RequestOptions;
import com.huli.foxread.GlideApp;
import com.huli.foxread.R;
import com.huli.foxread.transforms.CenterCropRoundCornerTransform;
import com.huli.foxread.utils.DensityUtils;

import java.util.List;

public class SpecialTopicPagerAdapter extends PagerAdapter {
    private List<Integer> mList;
    private LayoutInflater layoutInflater;
    private Context context;

    public SpecialTopicPagerAdapter(Context context, List<Integer> list) {
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
        // 自己实现
        View inflate = layoutInflater.inflate(R.layout.vp_item_special_topic, container, false);
        TextView tcTitle = inflate.findViewById(R.id.tv_st_title);
        tcTitle.setText("白金大神全力新作：总裁爹地请接招");

        ImageView ivImg = inflate.findViewById(R.id.iv_st_img);
        //设置图片圆角角度
        RequestOptions options = RequestOptions.bitmapTransform(new CenterCropRoundCornerTransform(DensityUtils.dp2px(context, 4)));
        GlideApp.with(context)
                .load(R.mipmap.banner_place_holder)
                .apply(options)
                .placeholder(R.mipmap.banner_place_holder)
                .error(R.mipmap.banner_place_holder)
                .into(ivImg);

        container.addView(inflate);

        return inflate;
    }
}
