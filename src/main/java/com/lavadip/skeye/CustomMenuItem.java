package com.lavadip.skeye;

public class CustomMenuItem {
    public static final int UNDEFINED_RESOURCE = 0;
    private boolean enabled = true;
    private String mCaption = null;
    private int mId = -1;
    private int mImageResourceId = 0;

    public CustomMenuItem() {
    }

    public CustomMenuItem(String caption, int imageResourceId, int id) {
        this.mCaption = caption;
        this.mImageResourceId = imageResourceId;
        this.mId = id;
    }

    public void setCaption(String caption) {
        this.mCaption = caption;
    }

    public String getCaption() {
        return this.mCaption;
    }

    public void setImageResourceId(int imageResourceId) {
        this.mImageResourceId = imageResourceId;
    }

    public int getImageResourceId() {
        return this.mImageResourceId;
    }

    public void setId(int id) {
        this.mId = id;
    }

    public int getId() {
        return this.mId;
    }

    public boolean getEnabled() {
        return this.enabled;
    }

    public void setEnabled(boolean enabled2) {
        this.enabled = enabled2;
    }
}
