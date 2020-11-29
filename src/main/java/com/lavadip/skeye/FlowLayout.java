package com.lavadip.skeye;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import java.util.Iterator;
import java.util.LinkedList;

public class FlowLayout extends ViewGroup {
    static final /* synthetic */ boolean $assertionsDisabled = (!FlowLayout.class.desiredAssertionStatus());
    private int line_height;
    private final int line_spacing = 1;

    public static class LayoutParams extends ViewGroup.LayoutParams {
        public final int horizontal_spacing;
        public final int vertical_spacing;

        public LayoutParams(int horizontal_spacing2, int vertical_spacing2) {
            super(0, 0);
            this.horizontal_spacing = horizontal_spacing2;
            this.vertical_spacing = vertical_spacing2;
        }
    }

    public FlowLayout(Context context) {
        super(context);
    }

    public FlowLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int childHeightMeasureSpec;
        if ($assertionsDisabled || View.MeasureSpec.getMode(widthMeasureSpec) != 0) {
            int width = (View.MeasureSpec.getSize(widthMeasureSpec) - getPaddingLeft()) - getPaddingRight();
            int height = (View.MeasureSpec.getSize(heightMeasureSpec) - getPaddingTop()) - getPaddingBottom();
            int count = getChildCount();
            int line_height2 = 0;
            int xpos = getPaddingLeft();
            int ypos = getPaddingTop();
            if (View.MeasureSpec.getMode(heightMeasureSpec) == Integer.MIN_VALUE) {
                childHeightMeasureSpec = View.MeasureSpec.makeMeasureSpec(height, Integer.MIN_VALUE);
            } else {
                childHeightMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, 0);
            }
            int numLines = 1;
            for (int i = 0; i < count; i++) {
                View child = getChildAt(i);
                if (child.getVisibility() != 8) {
                    LayoutParams lp = (LayoutParams) child.getLayoutParams();
                    child.measure(View.MeasureSpec.makeMeasureSpec(width, Integer.MIN_VALUE), childHeightMeasureSpec);
                    int childw = child.getMeasuredWidth();
                    line_height2 = Math.max(line_height2, child.getMeasuredHeight() + lp.vertical_spacing);
                    if (xpos + childw > width) {
                        xpos = getPaddingLeft();
                        ypos += line_height2;
                        numLines++;
                    }
                    xpos += lp.horizontal_spacing + childw;
                }
            }
            this.line_height = line_height2;
            int totalSpacing = (numLines - 1) * 1;
            if (View.MeasureSpec.getMode(heightMeasureSpec) == 0) {
                height = ypos + line_height2 + totalSpacing;
            } else if (View.MeasureSpec.getMode(heightMeasureSpec) == Integer.MIN_VALUE && ypos + line_height2 < height) {
                height = ypos + line_height2 + totalSpacing;
            }
            setMeasuredDimension(width, height);
            return;
        }
        throw new AssertionError();
    }

    /* access modifiers changed from: protected */
    public ViewGroup.LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(0, 0);
    }

    /* access modifiers changed from: protected */
    public boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        if (p instanceof LayoutParams) {
            return true;
        }
        return false;
    }

    /* access modifiers changed from: protected */
    public LinkedList<LinkedList<View>> mkRows(int r, int l) {
        int count = getChildCount();
        int width = r - l;
        int padLeft = getPaddingLeft();
        LinkedList<LinkedList<View>> rows = new LinkedList<>();
        LinkedList<View> row = new LinkedList<>();
        rows.add(row);
        int xpos = padLeft;
        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            if (child.getVisibility() != 8) {
                int childw = child.getMeasuredWidth() + ((LayoutParams) child.getLayoutParams()).horizontal_spacing;
                if (xpos + childw > width) {
                    xpos = padLeft;
                    row = new LinkedList<>();
                    rows.add(row);
                }
                row.add(child);
                xpos += childw;
            }
        }
        return rows;
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean changed, int l, int t, int r, int b) {
        LinkedList<LinkedList<View>> rows = mkRows(r, l);
        int width = r - l;
        int padLeft = getPaddingLeft();
        int ypos = getPaddingTop();
        Iterator<LinkedList<View>> it = rows.iterator();
        while (it.hasNext()) {
            LinkedList<View> row = it.next();
            int numChildren = row.size();
            if (numChildren > 0) {
                int rowTotal = padLeft;
                Iterator<View> it2 = row.iterator();
                while (it2.hasNext()) {
                    View child = it2.next();
                    rowTotal += child.getMeasuredWidth() + ((LayoutParams) child.getLayoutParams()).horizontal_spacing;
                }
                int rowExtraPerChild = (width - rowTotal) / numChildren;
                int rowExtraLastChild = width - ((rowExtraPerChild * numChildren) + rowTotal);
                int xpos = padLeft;
                int i = 0;
                while (i < numChildren) {
                    View child2 = row.get(i);
                    int childw = child2.getMeasuredWidth() + rowExtraPerChild + (i == numChildren + -1 ? rowExtraLastChild : 0);
                    child2.layout(xpos, ypos, xpos + childw, Math.max(child2.getMeasuredHeight(), this.line_height) + ypos);
                    xpos += ((LayoutParams) child2.getLayoutParams()).horizontal_spacing + childw;
                    i++;
                }
                ypos += this.line_height + 1;
            }
        }
    }
}
