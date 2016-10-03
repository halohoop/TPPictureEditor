package com.android.systemui.screenshot.editutils.pages;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.AnticipateOvershootInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;

import com.android.systemui.screenshot.editutils.presenters.IEditorActivityPresenter;
import com.android.systemui.screenshot.editutils.presenters.IEditorActivityPresenterImpls;
import com.android.systemui.screenshot.editutils.shape.Shape;
import com.android.systemui.screenshot.editutils.widgets.ActionsChooseView;
import com.android.systemui.screenshot.editutils.widgets.AlphaSeekBar;
import com.android.systemui.screenshot.editutils.widgets.ColorPickerView;
import com.android.systemui.screenshot.editutils.widgets.ColorShowView;
import com.android.systemui.screenshot.editutils.widgets.MarkableImageView;
import com.android.systemui.screenshot.editutils.widgets.PenceilAndRubberView;
import com.android.systemui.screenshot.editutils.widgets.ShapesChooseView;
import com.android.systemui.screenshot.editutils.widgets.ThicknessSeekBar;

public class EditorActivity extends Activity implements
        PenceilAndRubberView.PenceilOrRubberModeCallBack,
        ShapesChooseView.OnSelectedListener,
        ActionsChooseView.OnSelectedListener, ColorPickerView.ColorPickListener,
        View.OnClickListener, SeekBar.OnSeekBarChangeListener,
        Animator.AnimatorListener {

    private MarkableImageView mMarkableimageview;
    private PenceilAndRubberView mPenceilAndRubberView;
    private ActionsChooseView mActionsChooseView;
    private IEditorActivityPresenter mIEditorActivityPresenter;
    private ColorPickerView mColorPickerView;
    private ColorShowView mColorShowViewInPenceilGroup;
    private ColorShowView mColorShowViewInShapeGroup;
    private ColorShowView mColorShowViewInTextGroup;
    private ShapesChooseView mShapesChooseView;
    private ThicknessSeekBar mThicknessSeekBar;
    private AlphaSeekBar mAlphaSeekBar;
    private LinearLayout mShapesContainer;
    private ImageView mIvCancel;
    private FrameLayout mToolsDetailContainer;
    private LinearLayout mPenceilAjustContainer;
    private RelativeLayout mTextDetailContainer;

    /**
     * mark that is the animation is end to act sth
     */
    private boolean mIsAnimationEnd = true;
    private ImageView mIvAddText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);
        mIvCancel = (ImageView) findViewById(R.id.iv_cancel);
        mMarkableimageview = (MarkableImageView) findViewById(R.id.markableimageview);
        mPenceilAndRubberView = (PenceilAndRubberView) findViewById(R.id.penceil_and_rubber_view);
        mActionsChooseView = (ActionsChooseView) findViewById(R.id.actions_choose_view);
        mShapesChooseView = (ShapesChooseView) findViewById(R.id.shapes_choose_view);
        mColorPickerView = (ColorPickerView) findViewById(R.id.color_picker_view);
        mColorShowViewInPenceilGroup = (ColorShowView) findViewById(R.id.color_show_view);
        mColorShowViewInShapeGroup = (ColorShowView) findViewById(R.id
                .color_show_view_in_shapes_group);
        mColorShowViewInTextGroup = (ColorShowView) findViewById(R.id
                .color_show_view_in_text_detail_container);
        mThicknessSeekBar = (ThicknessSeekBar) findViewById(R.id.thickness_seek_bar);
        mAlphaSeekBar = (AlphaSeekBar) findViewById(R.id.alpha_seek_bar);
        mPenceilAjustContainer = (LinearLayout) findViewById(R.id.penceil_ajust_container);
        mTextDetailContainer = (RelativeLayout) findViewById(R.id.text_detail_container);
        mShapesContainer = (LinearLayout) findViewById(R.id.shapes_container);
        mToolsDetailContainer = (FrameLayout) findViewById(R.id.tools_detail_container);
        mIvAddText = (ImageView) findViewById(R.id.iv_add_text);
        mIvAddText.setOnClickListener(this);
        mIvCancel.setOnClickListener(this);
        mColorShowViewInPenceilGroup.setOnClickListener(this);
        mColorShowViewInShapeGroup.setOnClickListener(this);
        mColorShowViewInTextGroup.setOnClickListener(this);
        mColorPickerView.setColorPickListener(this);
        mActionsChooseView.setOnSelectedListener(this);
        mShapesChooseView.setOnSelectedListener(this);
        mMarkableimageview.enterEditMode();
        mPenceilAndRubberView.setPenceilOrRubberModeCallBack(this);
        mIEditorActivityPresenter = new IEditorActivityPresenterImpls();
        mIEditorActivityPresenter.needAnimationEndToAct(mPenceilAndRubberView);
        mActionsChooseView.setAnimationEndMarkHelper(mIEditorActivityPresenter);
        mShapesChooseView.setAnimationEndMarkHelper(mIEditorActivityPresenter);
        mThicknessSeekBar.setOnSeekBarChangeListener(this);
        mAlphaSeekBar.setOnSeekBarChangeListener(this);
        initView();
    }

    private void initView() {
        mColorShowViewInPenceilGroup.setColor(Color.parseColor("#FF2968"));
        mColorShowViewInShapeGroup.setColor(Color.parseColor("#FF2968"));
        mColorShowViewInTextGroup.setColor(Color.parseColor("#FF2968"));
    }

    @Override
    public void onModeSelected(PenceilAndRubberView.MODE mode) {
        if (mode == PenceilAndRubberView.MODE.PENCEILON) {
            animationToShowToolsDetailVertical();
        } else if (mode == PenceilAndRubberView.MODE.RUBBERON) {
            animationToHideToolsDetailVertical();
        }
    }

    private boolean mIsToolsDetailContainerVisible = true;

    @Override
    public void onActionSelected(int index) {
        //hide color picker if needed
        hideColorPickerView();
        //to show penceil and rubber view
        if (index != 0) {
            mPenceilAndRubberView.setVisibility(View.GONE);
            hidePenceilAjustContainer();
        } else {
            mPenceilAndRubberView.setVisibility(View.VISIBLE);
            showPenceilAjustContainer();
        }
        if (index == 1) {
            //animation to show the shape layout
            animationToShowToolsDetailVertical();
            showTextDetailContainer();
        } else {
            hideTextDetailContainer();
        }
        if (index == 2) {
            //animation to show the shape layout
            animationToShowToolsDetailVertical();
            showShapesContainer();
        } else {
            hideShapesContainer();
        }
        if (index == 3) {
            //mosaic action
            animationToHideToolsDetailVertical();
        } else {
            animationToShowToolsDetailVertical();
        }
    }

    @Override
    public void onShapeSelected(int index) {
        if (index == 0) {
            mMarkableimageview.setNowAddingShapeType(Shape.ShapeType.LINE);
        } else if (index == 1) {
            mMarkableimageview.setNowAddingShapeType(Shape.ShapeType.ARROW);
        } else if (index == 3) {
            mMarkableimageview.setNowAddingShapeType(Shape.ShapeType.CIRCLE);
        } else if (index == 4) {
            mMarkableimageview.setNowAddingShapeType(Shape.ShapeType.ROUNDRECT);
        }
    }

    @Override
    public void onColorPicked(int color) {
        mMarkableimageview.setColor(color);
        mColorShowViewInPenceilGroup.setColor(color);
        mColorShowViewInShapeGroup.setColor(color);
        mColorShowViewInTextGroup.setColor(color);
        mThicknessSeekBar.setProgressColor(color);
        mAlphaSeekBar.setProgressColor(color);
    }


    @Override
    public void onColorPickedDone() {
        hideColorPickerView();
    }

    private boolean mIsTextDetailContainerVisible = true;

    private void hideTextDetailContainer() {
        if (mIsTextDetailContainerVisible) {
            mIsTextDetailContainerVisible = false;
            animationToHideToolsDetailChildHorizontal(mTextDetailContainer);
        }
    }

    private void showTextDetailContainer() {
        if (!mIsTextDetailContainerVisible) {
            mIsTextDetailContainerVisible = true;
            if (mTextDetailContainer.getVisibility() != View.VISIBLE) {
                mTextDetailContainer.setVisibility(View.VISIBLE);
            }
            animationToShowToolsDetailChildHorizontal(mTextDetailContainer);
        }
    }

    private boolean mIsShapesContainerVisible = true;

    private void hideShapesContainer() {
        if (mIsShapesContainerVisible) {
            mIsShapesContainerVisible = false;
            animationToHideToolsDetailChildHorizontal(mShapesContainer);
        }
    }

    private void showShapesContainer() {
        if (!mIsShapesContainerVisible) {
            mIsShapesContainerVisible = true;
            if (mShapesContainer.getVisibility() != View.VISIBLE) {
                mShapesContainer.setVisibility(View.VISIBLE);
            }
            animationToShowToolsDetailChildHorizontal(mShapesContainer);
        }
    }

    private boolean mIsPenceilAjustContainerVisible = true;

    private void hidePenceilAjustContainer() {
        if (mIsPenceilAjustContainerVisible) {
            mIsPenceilAjustContainerVisible = false;
            animationToHideToolsDetailChildHorizontal(mPenceilAjustContainer);
        }
    }

    private void showPenceilAjustContainer() {
        if (!mIsPenceilAjustContainerVisible) {
            mIsPenceilAjustContainerVisible = true;
            if (mPenceilAjustContainer.getVisibility() != View.VISIBLE) {
                mPenceilAjustContainer.setVisibility(View.VISIBLE);
            }
            animationToShowToolsDetailChildHorizontal(mPenceilAjustContainer);
        }
    }

    private boolean mIsColorPickerViewVisible = true;

    private void hideColorPickerView() {
        if (mIsColorPickerViewVisible) {
            mIsColorPickerViewVisible = false;
            animationToHideToolsDetailChildHorizontal(mColorPickerView);
        }
    }

    private void showColorPickerView() {
        if (!mIsColorPickerViewVisible) {
            mIsColorPickerViewVisible = true;
            if (mColorPickerView.getVisibility() != View.VISIBLE) {
                mColorPickerView.setVisibility(View.VISIBLE);
            }
            animationToShowToolsDetailChildHorizontal(mColorPickerView);
        }
    }

    private void animationToShowToolsDetailVertical() {
        if (!mIsToolsDetailContainerVisible) {
            mIsToolsDetailContainerVisible = true;
            ValueAnimator valueAnimator = ValueAnimator.ofFloat(0.0f, 1.0f)
                    .setDuration(200);
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    float animatedValue = (float) animation.getAnimatedValue();
                    ViewGroup.LayoutParams layoutParams = mToolsDetailContainer.getLayoutParams();
                    layoutParams.height = (int) (120 * animatedValue);
                    mToolsDetailContainer.setLayoutParams(layoutParams);
                }
            });
            valueAnimator.setInterpolator(new DecelerateInterpolator());
            valueAnimator.start();
        }
    }

    private void animationToHideToolsDetailVertical() {
        if (mIsToolsDetailContainerVisible) {
            mIsToolsDetailContainerVisible = false;
            ValueAnimator valueAnimator = ValueAnimator.ofFloat(1.0f, 0.0f)
                    .setDuration(200);
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    float animatedValue = (float) animation.getAnimatedValue();
                    ViewGroup.LayoutParams layoutParams = mToolsDetailContainer.getLayoutParams();
                    layoutParams.height = (int) (120 * animatedValue);
                    mToolsDetailContainer.setLayoutParams(layoutParams);
                }
            });
            valueAnimator.setInterpolator(new DecelerateInterpolator());
            valueAnimator.start();
        }
    }

    private void animationToShowToolsDetailChildHorizontal(final View view) {
        ObjectAnimator translationX = ObjectAnimator.ofFloat(view,
                "translationX", view.getWidth(), 0).setDuration(200);
        translationX.setInterpolator(new AnticipateOvershootInterpolator(0.8f));
        translationX.addListener(this);
        translationX.start();
    }

    private void animationToHideToolsDetailChildHorizontal(View view) {
        ObjectAnimator translationX = ObjectAnimator.ofFloat(view,
                "translationX", 0, view.getWidth()).setDuration(200);
        translationX.setInterpolator(new AnticipateOvershootInterpolator(0.8f));
        translationX.addListener(this);
        translationX.start();
    }

    @Override
    public void onClick(View v) {
        if (mIsAnimationEnd) {//when animation end to act
            switch (v.getId()) {
                case R.id.color_show_view:
                    showColorPickerView();
                    break;
                case R.id.color_show_view_in_shapes_group:
                    showColorPickerView();
                    break;
                case R.id.color_show_view_in_text_detail_container:
                    showColorPickerView();
                    break;
                case R.id.iv_cancel:
                    if (!mMarkableimageview.isEdited()) {
                        finish();
                    } else {
                        //TODO alert dialog
                    }
                    break;
                case R.id.iv_add_text:
                    //TODO add text
                    break;
            }
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (seekBar == mThicknessSeekBar) {
            mMarkableimageview.setFreeStrokeWidth(progress);
        } else if (seekBar == mAlphaSeekBar) {
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onAnimationStart(Animator animation) {
        mIsAnimationEnd = false;
    }

    @Override
    public void onAnimationEnd(Animator animation) {
        mIsAnimationEnd = true;
    }

    @Override
    public void onAnimationCancel(Animator animation) {

    }

    @Override
    public void onAnimationRepeat(Animator animation) {

    }
}
