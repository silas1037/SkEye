package com.lavadip.skeye;

import android.app.Activity;
import android.graphics.PorterDuff;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.Iterator;

public class AnimatedSimpleCustomMenu {
    private final boolean mAnimate;
    private boolean mHideOnSelect = true;
    private boolean mIsShowing = false;
    private int mItemsPerLineInLandscapeOrientation = 6;
    private int mItemsPerLineInPortraitOrientation = 3;
    private final LayoutInflater mLayoutInflater;
    private final OnMenuItemSelectedListener mListener;
    private final int mMenuAnchorResId;
    private ArrayList<CustomMenuItem> mMenuItems;

    public interface OnMenuItemSelectedListener {
        void MenuItemSelectedEvent(CustomMenuItem customMenuItem);
    }

    public boolean isShowing() {
        return this.mIsShowing;
    }

    public void setHideOnSelect(boolean doHideOnSelect) {
        this.mHideOnSelect = doHideOnSelect;
    }

    public void setItemsPerLineInPortraitOrientation(int count) {
        this.mItemsPerLineInPortraitOrientation = count;
    }

    public void setItemsPerLineInLandscapeOrientation(int count) {
        this.mItemsPerLineInLandscapeOrientation = count;
    }

    public synchronized void setMenuItems(ArrayList<CustomMenuItem> items) {
        if (!this.mIsShowing) {
            this.mMenuItems = items;
        }
    }

    public AnimatedSimpleCustomMenu(int menuAnchorResId, OnMenuItemSelectedListener listener, LayoutInflater lo, boolean animate) {
        this.mMenuAnchorResId = menuAnchorResId;
        this.mListener = listener;
        this.mMenuItems = new ArrayList<>();
        this.mLayoutInflater = lo;
        this.mAnimate = animate;
    }

    public synchronized void show(final Activity activity) {
        int menuRows;
        this.mIsShowing = true;
        boolean isLandscape = false;
        int itemCount = this.mMenuItems.size();
        if (itemCount >= 1) {
            Display display = ((WindowManager) activity.getSystemService("window")).getDefaultDisplay();
            if (display.getWidth() > display.getHeight()) {
                isLandscape = true;
            }
            View menuView = this.mLayoutInflater.inflate(C0031R.layout.custom_menu, (ViewGroup) null);
            int divisor = isLandscape ? this.mItemsPerLineInLandscapeOrientation : this.mItemsPerLineInPortraitOrientation;
            if (itemCount < divisor) {
                menuRows = 1;
            } else {
                menuRows = itemCount / divisor;
                if (itemCount % divisor != 0) {
                    menuRows++;
                }
            }
            TableLayout table = (TableLayout) menuView.findViewById(C0031R.C0032id.custom_menu_table);
            table.removeAllViews();
            int i = 0;
            while (i < menuRows) {
                TableRow row = new TableRow(activity);
                row.setLayoutParams(new WindowManager.LayoutParams(-1, -2));
                int j = 0;
                while (j < divisor && (i * divisor) + j < itemCount) {
                    final CustomMenuItem cmi = this.mMenuItems.get((i * divisor) + j);
                    boolean itemEnabled = cmi.getEnabled();
                    View itemLayout = this.mLayoutInflater.inflate(C0031R.layout.custom_menu_item, (ViewGroup) null);
                    TextView tv = (TextView) itemLayout.findViewById(C0031R.C0032id.custom_menu_item_caption);
                    tv.setEnabled(itemEnabled);
                    tv.setText(cmi.getCaption());
                    ImageView iv = (ImageView) itemLayout.findViewById(C0031R.C0032id.custom_menu_item_icon);
                    iv.setImageResource(cmi.getImageResourceId());
                    iv.setEnabled(itemEnabled);
                    if (!itemEnabled) {
                        iv.setColorFilter(-13421773, PorterDuff.Mode.SRC_ATOP);
                    }
                    if (itemEnabled) {
                        itemLayout.setOnClickListener(new View.OnClickListener() {
                            /* class com.lavadip.skeye.AnimatedSimpleCustomMenu.View$OnClickListenerC00021 */

                            public void onClick(View v) {
                                AnimatedSimpleCustomMenu.this.mListener.MenuItemSelectedEvent(cmi);
                                if (AnimatedSimpleCustomMenu.this.mHideOnSelect) {
                                    AnimatedSimpleCustomMenu.this.hide(activity);
                                }
                            }
                        });
                    }
                    itemLayout.setEnabled(itemEnabled);
                    row.addView(itemLayout);
                    j++;
                }
                table.addView(row);
                i++;
            }
            final ViewGroup menuAnchor = (ViewGroup) activity.findViewById(this.mMenuAnchorResId);
            menuAnchor.removeAllViews();
            menuAnchor.addView(menuView);
            if (!this.mAnimate) {
                menuAnchor.setVisibility(0);
            } else {
                Animation fadeInAnimation = AnimationUtils.makeInChildBottomAnimation(activity);
                fadeInAnimation.setAnimationListener(new Animation.AnimationListener() {
                    /* class com.lavadip.skeye.AnimatedSimpleCustomMenu.animationAnimation$AnimationListenerC00032 */

                    public void onAnimationEnd(Animation animation) {
                    }

                    public void onAnimationRepeat(Animation animation) {
                    }

                    public void onAnimationStart(Animation animation) {
                        menuAnchor.setVisibility(0);
                    }
                });
                menuAnchor.startAnimation(fadeInAnimation);
            }
        }
    }

    public synchronized void hide(Activity activity) {
        this.mIsShowing = false;
        final ViewGroup menuAnchor = (ViewGroup) activity.findViewById(this.mMenuAnchorResId);
        if (!this.mAnimate) {
            menuAnchor.setVisibility(8);
        } else {
            Animation fadeOutAnimation = AnimationUtils.makeOutAnimation(activity, true);
            fadeOutAnimation.setAnimationListener(new Animation.AnimationListener() {
                /* class com.lavadip.skeye.AnimatedSimpleCustomMenu.animationAnimation$AnimationListenerC00043 */

                public void onAnimationEnd(Animation animation) {
                    menuAnchor.setVisibility(8);
                }

                public void onAnimationRepeat(Animation animation) {
                }

                public void onAnimationStart(Animation animation) {
                }
            });
            menuAnchor.startAnimation(fadeOutAnimation);
        }
    }

    public void refreshView(Activity a) {
        if (isShowing()) {
            show(a);
        } else {
            hide(a);
        }
    }

    public void setEnabled(int itemId, boolean enable) {
        Iterator<CustomMenuItem> it = this.mMenuItems.iterator();
        while (it.hasNext()) {
            CustomMenuItem item = it.next();
            if (item.getId() == itemId) {
                item.setEnabled(enable);
            }
        }
    }
}
