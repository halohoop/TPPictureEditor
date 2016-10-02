package com.android.systemui.screenshot.editutils.pages;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.animation.AnticipateOvershootInterpolator;

import com.android.systemui.screenshot.editutils.presenters.IEditorActivityPresenter;
import com.android.systemui.screenshot.editutils.presenters.IEditorActivityPresenterImpls;
import com.android.systemui.screenshot.editutils.shape.Shape;
import com.android.systemui.screenshot.editutils.widgets.ActionsChooseView;
import com.android.systemui.screenshot.editutils.widgets.ColorPickerView;
import com.android.systemui.screenshot.editutils.widgets.ColorShowView;
import com.android.systemui.screenshot.editutils.widgets.MarkableImageView;
import com.android.systemui.screenshot.editutils.widgets.PenceilAndRubberView;
import com.android.systemui.screenshot.editutils.widgets.ShapesChooseView;

public class EditorActivity extends Activity implements
        PenceilAndRubberView.PenceilOrRubberModeCallBack,
        ShapesChooseView.OnSelectedListener,
        ActionsChooseView.OnSelectedListener, ColorPickerView.ColorPickListener,
        View.OnClickListener {

    private MarkableImageView mMarkableimageview;
    private PenceilAndRubberView mPenceilAndRubberView;
    private ActionsChooseView mActionsChooseView;
    private IEditorActivityPresenter mIEditorActivityPresenter;
    private ColorPickerView mColorPickerView;
    private ColorShowView mColorShowView;
    private ColorShowView mColorShowViewInShapeGroup;
    private ShapesChooseView mShapesChooseView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);
        mMarkableimageview = (MarkableImageView) findViewById(R.id.markableimageview);
        mPenceilAndRubberView = (PenceilAndRubberView) findViewById(R.id.penceil_and_rubber_view);
        mActionsChooseView = (ActionsChooseView) findViewById(R.id.actions_choose_view);
        mShapesChooseView = (ShapesChooseView) findViewById(R.id.shapes_choose_view);
        mColorPickerView = (ColorPickerView) findViewById(R.id.color_picker_view);
        mColorShowView = (ColorShowView) findViewById(R.id.color_show_view);
        mColorShowViewInShapeGroup = (ColorShowView) findViewById(R.id.color_show_view_in_shapes_group);
        mColorShowView.setColor(mColorPickerView.getColorSelected());
        mColorShowViewInShapeGroup.setColor(mColorPickerView.getColorSelected());
        mColorShowView.setOnClickListener(this);
        mColorShowViewInShapeGroup.setOnClickListener(this);
        mColorPickerView.setColorPickListener(this);
        mActionsChooseView.setOnSelectedListener(this);
        mShapesChooseView.setOnSelectedListener(this);
        mMarkableimageview.enterEditMode();
        mPenceilAndRubberView.setPenceilOrRubberModeCallBack(this);
        mIEditorActivityPresenter = new IEditorActivityPresenterImpls();
        mIEditorActivityPresenter.needAnimationEndToAct(mPenceilAndRubberView);
        mActionsChooseView.setAnimationEndMarkHelper(mIEditorActivityPresenter);
        mShapesChooseView.setAnimationEndMarkHelper(mIEditorActivityPresenter);
    }

    @Override
    public void onModeSelected(PenceilAndRubberView.MODE mode) {
        if (mode == PenceilAndRubberView.MODE.PENCEILON) {
            Log.i("aaa", "aaa" + "PENCEILON");

        } else if (mode == PenceilAndRubberView.MODE.RUBBERON) {
            Log.i("aaa", "aaa" + "RUBBERON");
        }
    }

    @Override
    public void onActionSelected(int index) {
        Log.i("aaa", "aaa" + index);
        if (index != 0) {
            mPenceilAndRubberView.setVisibility(View.GONE);
        } else {
            mPenceilAndRubberView.setVisibility(View.VISIBLE);
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
        mColorShowView.setColor(color);
        mColorShowViewInShapeGroup.setColor(color);
    }

    @Override
    public void onColorPickedDone() {
        hideColorPickerView();
    }

    private void hideColorPickerView() {
        animationToHideToolsDetail(mColorPickerView);
    }

    private void showColorPickerView() {
        animationToShowToolsDetail(mColorPickerView);
    }

    private void animationToShowToolsDetail(View view) {
        ObjectAnimator translationX = ObjectAnimator.ofFloat(view,
                "translationX", mColorPickerView.getWidth(), 0).setDuration(500);
        translationX.setInterpolator(new AnticipateOvershootInterpolator(0.8f));
        translationX.start();
    }

    private void animationToHideToolsDetail(View view) {
        ObjectAnimator translationX = ObjectAnimator.ofFloat(view,
                "translationX", 0, mColorPickerView.getWidth()).setDuration(500);
        translationX.setInterpolator(new AnticipateOvershootInterpolator(0.8f));
        translationX.start();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.color_show_view:
                showColorPickerView();
                break;
            case R.id.color_show_view_in_shapes_group:
                showColorPickerView();
                break;
        }
    }
}
