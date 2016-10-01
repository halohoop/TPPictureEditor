package com.android.systemui.screenshot.editutils.pages;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;

import com.android.systemui.screenshot.editutils.widgets.ActionsChooseView;
import com.android.systemui.screenshot.editutils.widgets.MarkableImageView;
import com.android.systemui.screenshot.editutils.widgets.PenceilAndRubberView;

public class EditorActivity extends Activity implements
        PenceilAndRubberView.PenceilOrRubberModeCallBack,
        ActionsChooseView.OnSelectedListener {

    private MarkableImageView mMarkableimageview;
    private PenceilAndRubberView mPenceilAndRubberView;
    private ActionsChooseView mActionsChooseView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);
        mMarkableimageview = (MarkableImageView) findViewById(R.id.markableimageview);
        mPenceilAndRubberView = (PenceilAndRubberView) findViewById(R.id.penceil_and_rubber_view);
        mActionsChooseView = (ActionsChooseView) findViewById(R.id.actions_choose_view);
        mActionsChooseView.setOnSelectedListener(this);
        mMarkableimageview.enterEditMode();
        mPenceilAndRubberView.setPenceilOrRubberModeCallBack(this);
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
    public void onSelected(int index) {
        Log.i("aaa", "aaa" + index);
    }
}
