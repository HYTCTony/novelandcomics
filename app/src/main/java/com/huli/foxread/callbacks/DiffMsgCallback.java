package com.huli.foxread.callbacks;

import com.huli.foxread.entity.SMsgBean;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;

/**
 * 项目名称：FoxRead
 * 创建人：Bill
 * 创建时间：2020/6/22  14:12
 * 备注：
 */
public class DiffMsgCallback extends DiffUtil.ItemCallback<SMsgBean> {

    /**
     * 判断是否是同一个item
     *
     * @param oldItem New data
     * @param newItem old Data
     * @return
     */
    @Override
    public boolean areItemsTheSame(@NonNull SMsgBean oldItem, @NonNull SMsgBean newItem) {
        return oldItem.getId() == newItem.getId();
    }

    /**
     * 当是同一个item时，再判断内容是否发生改变
     *
     * @param oldItem New data
     * @param newItem old Data
     * @return
     */
    @Override
    public boolean areContentsTheSame(@NonNull SMsgBean oldItem, @NonNull SMsgBean newItem) {
        return oldItem.getMessage_id().equals(newItem.getMessage_id())
                && oldItem.getStatus() == (newItem.getStatus())
                && oldItem.getUser_id().equals(newItem.getUser_id());
    }

    /**
     * 可选实现
     * 如果需要精确修改某一个view中的内容，请实现此方法。
     * 如果不实现此方法，或者返回null，将会直接刷新整个item。
     *
     * @param oldItem Old data
     * @param newItem New data
     * @return Payload info. if return null, the entire item will be refreshed.
     */
    @Override
    public Object getChangePayload(@NonNull SMsgBean oldItem, @NonNull SMsgBean newItem) {
        return null;
    }
}
