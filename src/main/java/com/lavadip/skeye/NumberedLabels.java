package com.lavadip.skeye;

import android.graphics.Paint;

public final class NumberedLabels {
    private static final float LABEL_OFFSET = 0.0f;
    private static int MAX_COMPONENT = 100;
    private int baseLabelId;
    private final float[] componentWidth = new float[MAX_COMPONENT];
    private final float prefixHeight;
    private final int prefixId;
    private final float prefixWidth;
    private final int startingObjNum;

    public NumberedLabels(String prefix, int startingObjNum2, LabelMaker labelMaker, Paint p) {
        this.startingObjNum = startingObjNum2;
        this.prefixId = labelMaker.add(prefix, p);
        this.prefixWidth = labelMaker.getWidth(this.prefixId);
        this.prefixHeight = labelMaker.getHeight(this.prefixId);
        for (int i = 0; i < MAX_COMPONENT; i++) {
            int labelId = labelMaker.add(new StringBuilder().append(i).toString(), p);
            this.componentWidth[i] = labelMaker.getWidth(labelId);
            if (i == 0) {
                this.baseLabelId = labelId;
            }
        }
    }

    public void drawLabelES20(float x, float y, int num, LabelMaker labelMaker, boolean centerLabelHoriz, boolean centerLabelVert, MyShadyRenderer r, float labelAlpha) {
        float currY;
        float currX;
        int num2 = num + this.startingObjNum;
        short s = NumComponents.offsets[num2 - 1];
        byte b = NumComponents.lengths[num2 - 1];
        int orientation = labelMaker.getOrientation();
        float signO = (float) (orientation == 3 ? -1 : 1);
        if (centerLabelHoriz) {
            float width = forEachComponent(r, y, labelMaker, s, b, 0.0f, false, 0, 1.0f, labelAlpha);
            if (orientation == 0) {
                currX = x - ((this.prefixWidth + width) / 2.0f);
                currY = y;
            } else {
                currY = y + (((this.prefixWidth + width) * signO) / 2.0f);
                currX = x;
            }
        } else {
            currY = y;
            currX = x;
        }
        if (centerLabelVert) {
            if (orientation == 0) {
                currY -= this.prefixHeight / 2.0f;
            } else {
                currX -= (this.prefixHeight / 2.0f) * signO;
            }
        }
        labelMaker.drawES20(r, currX, currY, this.prefixId, false, false, 0.0f, labelAlpha);
        if (orientation == 0) {
            currX += this.prefixWidth;
        } else {
            currY -= this.prefixWidth * signO;
        }
        forEachComponent(r, currY, labelMaker, s, b, currX, true, orientation, signO, labelAlpha);
    }

    private float forEachComponent(MyShadyRenderer r, float startY, LabelMaker labelMaker, int offset, int numComponents, float startX, boolean draw, int orientation, float signO, float labelAlpha) {
        for (int i = numComponents - 1; i >= 0; i--) {
            byte b = NumComponents.components[offset + i];
            if (i == 0 && numComponents > 1 && b < MAX_COMPONENT / 10) {
                int componentCopy = b;
                if (componentCopy == 0) {
                    componentCopy++;
                }
                while (componentCopy < MAX_COMPONENT / 10) {
                    componentCopy *= 10;
                    if (draw) {
                        labelMaker.drawES20(r, startX, startY, this.baseLabelId + 0, false, false, 0.0f, labelAlpha);
                    }
                    if (orientation == 0) {
                        startX += this.componentWidth[0];
                    } else {
                        startY -= this.componentWidth[0] * signO;
                    }
                }
            }
            if (draw) {
                labelMaker.drawES20(r, startX, startY, this.baseLabelId + b, false, false, 0.0f, labelAlpha);
            }
            if (orientation == 0) {
                startX += this.componentWidth[b];
            } else {
                startY -= this.componentWidth[b] * signO;
            }
        }
        return startX;
    }
}
