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
import com.android.systemui.screenshot.editutils.widgets.ActionsChooseView;
import com.android.systemui.screenshot.editutils.widgets.ColorPickerView;
import com.android.systemui.screenshot.editutils.widgets.ColorShowView;
import com.android.systemui.screenshot.editutils.widgets.MarkableImageView;
import com.android.systemui.screenshot.editutils.widgets.PenceilAndRubberView;

public class EditorActivity extends Activity implements
        PenceilAndRubberView.PenceilOrRubberModeCallBack,
        ActionsChooseView.OnSelectedListener, ColorPickerView.ColorPickListener,
        View.OnClickListener {

    private MarkableImageView mMarkableimageview;
    private PenceilAndRubberView mPenceilAndRubberView;
    private ActionsChooseView mActionsChooseView;
    private IEditorActivityPresenter mIEditorActivityPresenter;
    private ColorPickerView mColorPickerView;
    private ColorShowView mColorShowView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);
        mMarkableimageview = (MarkableImageView) findViewById(R.id.markableimageview);
        mPenceilAndRubberView = (PenceilAndRubberView) findViewById(R.id.penceil_and_rubber_view);
        mActionsChooseView = (ActionsChooseView) findViewById(R.id.actions_choose_view);
        mColorPickerView = (ColorPickerView) findViewById(R.id.color_picker_view);
        mColorShowView = (ColorShowView) findViewById(R.id.color_show_view);
        mColorShowView.setColor(mColorPickerView.getColorSelected());
        mColorShowView.setOnClickListener(this);
        mColorPickerView.setColorPickListener(this);
        mActionsChooseView.setOnSelectedListener(this);
        mMarkableimageview.enterEditMode();
        mPenceilAndRubberView.setPenceilOrRubberModeCallBack(this);
        mIEditorActivityPresenter = new IEditorActivityPresenterImpls();
        mIEditorActivityPresenter.needAnimationEndToAct(mPenceilAndRubberView);
        mActionsChooseView.setAnimationEndMarkHelper(mIEditorActivityPresenter);
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
    public void onActionSelected(final int index) {
        Log.i("aaa", "aaa" + index);
        if (index != 0) {
            mPenceilAndRubberView.setVisibility(View.GONE);
        } else {
            mPenceilAndRubberView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onColorPicked(int color) {
        mMarkableimageview.setColor(color);
        mColorShowView.setColor(color);
    }

    @Override
    public void onColorPickedDone() {
        hideColorPickerView();
    }

    private void hideColorPickerView() {
        ObjectAnimator translationX = ObjectAnimator.ofFloat(mColorPickerView,
                "translationX", 0, mColorPickerView.getWidth()).setDuration(500);
        translationX.setInterpolator(new AnticipateOvershootInterpolator(0.8f));
        translationX.start();
    }

    private void showColorPickerView() {
        ObjectAnimator translationX = ObjectAnimator.ofFloat(mColorPickerView,
                "translationX", mColorPickerView.getWidth(), 0).setDuration(500);
        translationX.setInterpolator(new AnticipateOvershootInterpolator(0.8f));
        translationX.start();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.color_show_view:
                showColorPickerView();
                break;
        }
    }
}
