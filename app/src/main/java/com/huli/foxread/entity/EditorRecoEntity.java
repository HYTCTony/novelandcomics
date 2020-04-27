package com.huli.foxread.entity;

/**
 * 小编力荐
 */
public class EditorRecoEntity {
    private String id;
    private String novelId;
    private String novelName;
    private String httpImage;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNovelId() {
        return novelId;
    }

    public void setNovelId(String novelId) {
        this.novelId = novelId;
    }

    public String getNovelName() {
        return novelName;
    }

    public void setNovelName(String novelName) {
        this.novelName = novelName;
    }

    public String getHttpImage() {
        return httpImage;
    }

    public void setHttpImage(String httpImage) {
        this.httpImage = httpImage;
    }
}
