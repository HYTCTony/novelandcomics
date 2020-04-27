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
import com.huli.foxread.entity.GemEntity;
import com.huli.foxread.transforms.CenterCropRoundCornerTransform;
import com.huli.foxread.utils.DensityUtils;

import java.util.List;

import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

public class MultiplePagerAdapter2 extends PagerAdapter {

    private List<GemEntity> mList;
    private LayoutInflater layoutInflater;
    private Context context;

    public MultiplePagerAdapter2(Context context, List<GemEntity> list) {
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
        return (float) 0.9;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        View view = (View) object;
        ((ViewPager) container).removeView(view);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View inflate = layoutInflater.inflate(R.layout.vp_item_book_excellent_work, container, false);
        // 自己实现
        GemEntity data = mList.get(position);
        TextView tvBookName = inflate.findViewById(R.id.iv_book_name);
        TextView tvBookGeneric = inflate.findViewById(R.id.tv_book_generic);
        TextView tvIntroduction = inflate.findViewById(R.id.iv_book_introduction);
        TextView tvScore = inflate.findViewById(R.id.tv_book_score);
        tvBookName.setText(data.getName());
        tvIntroduction.setText(data.getIntroduce());
        tvScore.setText((data.getScore() + context.getString(R.string.unit_score)));
        tvBookGeneric.setText(data.getCategoryName());

        ImageView ivImg = inflate.findViewById(R.id.iv_book_cover);
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
                mOnPagerItemClickListener.onItemClick(data.getId());
            }
        });
        container.addView(inflate);
        return inflate;
    }

    public void setmOnPagerItemClickListener(SpecialTopicPagerAdapter.OnPagerItemClickListener mOnPagerItemClickListener) {
        this.mOnPagerItemClickListener = mOnPagerItemClickListener;
    }

    private SpecialTopicPagerAdapter.OnPagerItemClickListener mOnPagerItemClickListener;

    public interface OnPagerItemClickListener {
        void onItemClick(String bookID);
    }
}
