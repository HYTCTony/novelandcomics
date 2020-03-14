package com.huli.foxread.ui.imgshowpickerview;

import java.util.List;

public interface ImageShowPickerListener {

    void addOnClickListener(int remainNum);

    void picOnClickListener(List<ImageShowPickerBean> list, int position, int remainNum);

    void delOnClickListener(int position, int remainNum);
}
