package com.huli.foxread.entity;

import com.huli.foxread.entity.multi.HpBGModuleEntity;

import java.io.Serializable;
import java.util.List;

public class HomePageEntity2 implements Serializable {
    private List<EditorRecoEntity> top;                     //编辑力推
    private List<HpBGModuleEntity> module;

    public List<EditorRecoEntity> getTop() {
        return top;
    }

    public void setTop(List<EditorRecoEntity> top) {
        this.top = top;
    }

    public List<HpBGModuleEntity> getModule() {
        return module;
    }

    public void setModule(List<HpBGModuleEntity> module) {
        this.module = module;
    }
}
