package com.huli.foxread.entity;

public class NewBieSignInTaskEntity extends WelfareTaskEntity {
    private int complete_sum;

    public NewBieSignInTaskEntity() {
    }

    public NewBieSignInTaskEntity(String id, String name, int type, String content, int status, String welfare_category_id, int reward, int is_new_man, int frequency, String number, String link, String http_image, int complete_task) {
        super(id, name, type, content, status, welfare_category_id, reward, is_new_man, frequency, number, link, http_image, complete_task);
    }

    public NewBieSignInTaskEntity(String id, String name, int type, String content, int status, String welfare_category_id, int reward, int is_new_man, int frequency, String number, String link, String http_image, int complete_task, int complete_sum) {
        super(id, name, type, content, status, welfare_category_id, reward, is_new_man, frequency, number, link, http_image, complete_task);
        this.complete_sum = complete_sum;
    }

    public int getComplete_sum() {
        return complete_sum;
    }

    public void setComplete_sum(int complete_sum) {
        this.complete_sum = complete_sum;
    }
}
