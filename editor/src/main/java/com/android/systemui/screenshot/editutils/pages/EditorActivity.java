package com.android.systemui.screenshot.editutils.pages;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.AnticipateOvershootInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;

import com.android.systemui.screenshot.editutils.presenters.IEditorActivityPresenter;
import com.android.systemui.screenshot.editutils.presenters.IEditorActivityPresenterImpls;
import com.android.systemui.screenshot.editutils.shape.Shape;
import com.android.systemui.screenshot.editutils.utils.BitmapUtils;
import com.android.systemui.screenshot.editutils.widgets.ActionsChooseView;
import com.android.systemui.screenshot.editutils.widgets.AlphaSeekBar;
import com.android.systemui.screenshot.editutils.widgets.ColorPickerView;
import com.android.systemui.screenshot.editutils.widgets.ColorShowView;
import com.android.systemui.screenshot.editutils.widgets.MarkableImageView;
import com.android.systemui.screenshot.editutils.widgets.PenceilAndRubberView;
import com.android.systemui.screenshot.editutils.widgets.ShapesChooseView;
import com.android.systemui.screenshot.editutils.widgets.ThicknessSeekBar;

/**
 * @hide
 */
public class EditorActivity extends Activity implements
        PenceilAndRubberView.PenceilOrRubberModeCallBack,
        ShapesChooseView.OnSelectedListener,
        ActionsChooseView.OnSelectedListener, ColorPickerView.ColorPickListener,
        View.OnClickListener, SeekBar.OnSeekBarChangeListener,
        Animator.AnimatorListener,
        DialogInterface.OnClickListener {

    private static final String TAG = "EditorActivity";
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
    private ThicknessSeekBar mRubberThicknessSeekBar;
    private ThicknessSeekBar mMosaicThicknessSeekBar;
    private AlphaSeekBar mAlphaSeekBar;
    private LinearLayout mShapesContainer;
    private ImageView mIvCancel;
    private FrameLayout mToolsDetailContainer;
    private LinearLayout mPenceilAjustContainer;
    private RelativeLayout mTextDetailContainer;
    private FrameLayout mRubberAjustContainer;
    private FrameLayout mMosaicAjustContainer;

    /**
     * mark that is the animation is end to act sth
     */
    private boolean mIsAnimationEnd = true;
    private ImageView mIvAddText;
    private EditText mEtTextAdd;
    private View mTextAddContainer;
    private String mFilePath;
    private static final int MOSAIC_BITMAP_DONE = 100;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == MOSAIC_BITMAP_DONE) {
                Bitmap bitmap = (Bitmap) msg.obj;
                mMarkableimageview.setImageBitmap(bitmap);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);
        mMarkableimageview = (MarkableImageView) findViewById(R.id.markableimageview);
        handleIntentDataIfExist();
        mIvCancel = (ImageView) findViewById(R.id.iv_cancel);
        findViewById(R.id.iv_stepbackward).setOnClickListener(this);
        findViewById(R.id.iv_stepforward).setOnClickListener(this);
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
        mRubberAjustContainer = (FrameLayout) findViewById(R.id.rubber_ajust_container);
        mRubberThicknessSeekBar = (ThicknessSeekBar) mRubberAjustContainer.findViewById(R.id
        .rubber_thickness_seek_bar);
        mMosaicAjustContainer = (FrameLayout) findViewById(R.id.mosaic_ajust_container);
        mMosaicThicknessSeekBar = (ThicknessSeekBar) mMosaicAjustContainer.findViewById(R.id
        .mosaic_thickness_seek_bar);
        mToolsDetailContainer = (FrameLayout) findViewById(R.id.tools_detail_container);
        mTextAddContainer = findViewById(R.id.text_add_container);
        mIvAddText = (ImageView) findViewById(R.id.iv_add_text);
        mEtTextAdd = (EditText) mTextAddContainer.findViewById(R.id.et_text_add);
        mTextAddContainer.setOnClickListener(this);
        findViewById(R.id.iv_text_ok).setOnClickListener(this);
        mIvAddText.setOnClickListener(this);
        mIvCancel.setOnClickListener(this);
        mColorShowViewInPenceilGroup.setOnClickListener(this);
        mColorShowViewInShapeGroup.setOnClickListener(this);
        mColorShowViewInTextGroup.setOnClickListener(this);
        mColorPickerView.setColorPickListener(this);
        mActionsChooseView.setOnSelectedListener(this);
        mShapesChooseView.setOnSelectedListener(this);
        mPenceilAndRubberView.setPenceilOrRubberModeCallBack(this);
        mIEditorActivityPresenter = new IEditorActivityPresenterImpls();
        mIEditorActivityPresenter.needAnimationEndToAct(mPenceilAndRubberView);
        mActionsChooseView.setAnimationEndMarkHelper(mIEditorActivityPresenter);
        mShapesChooseView.setAnimationEndMarkHelper(mIEditorActivityPresenter);
        mThicknessSeekBar.setOnSeekBarChangeListener(this);
        mAlphaSeekBar.setOnSeekBarChangeListener(this);
        mRubberThicknessSeekBar.setOnSeekBarChangeListener(this);
        mMosaicThicknessSeekBar.setOnSeekBarChangeListener(this);
        initView();
    }

    private void handleIntentDataIfExist() {
        Intent intent = getIntent();
        mFilePath = intent.getStringExtra("EDITOR_TARGET");
        if (TextUtils.isEmpty(mFilePath)) {
            finish();
            return;
        }
        new Thread() {
            @Override
            public void run() {
                Bitmap bitmap = BitmapFactory.decodeFile(mFilePath);
                Bitmap mosaicBitmap = BitmapUtils.mosaicIt(bitmap, 10);
                mMarkableimageview.setMosaicBitmap(mosaicBitmap);

                Message message = new Message();
                message.obj = bitmap;
                message.what = MOSAIC_BITMAP_DONE;
                mHandler.sendMessage(message);
            }
        }.start();
    }

    private void initView() {
        mRubberThicknessSeekBar.setProgressColor("#8e8e8e");
        mMosaicThicknessSeekBar.setProgressColor("#8e8e8e");
        int initColor = Color.parseColor("#FF2968");
        mThicknessSeekBar.setProgressColor(initColor);
        mAlphaSeekBar.setProgressColor(initColor);
        mColorShowViewInPenceilGroup.setColor(initColor);
        mColorShowViewInShapeGroup.setColor(initColor);
        mColorShowViewInTextGroup.setColor(initColor);
        mMarkableimageview.setNowAddingShapeType(Shape.ShapeType.LINE);
        mMarkableimageview.enterEditMode();
        mMarkableimageview.changePaintColor(initColor);
    }

    @Override
    public void onModeSelected(PenceilAndRubberView.MODE mode) {
        if (mode == PenceilAndRubberView.MODE.PENCEILON) {
//            animationToShowToolsDetailVertical();
            showPenceilAjustContainer();
            hideRubberDetailContainer();
        } else if (mode == PenceilAndRubberView.MODE.RUBBERON) {
            hidePenceilAjustContainer();
            showRubberDetailContainer();
//            animationToHideToolsDetailVertical();
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
            mMarkableimageview.setInWhichMode(MarkableImageView.MODE_FREE_DRAW_PEN);
            mPenceilAndRubberView.setVisibility(View.VISIBLE);
            if (mPenceilAndRubberView.getMode() == PenceilAndRubberView.MODE.PENCEILON) {
                showPenceilAjustContainer();
            } else {
//                animationToHideToolsDetailVertical();
            }
        }
        if (index == 1) {
            mMarkableimageview.setInWhichMode(MarkableImageView.MODE_TEXT);
            //animation to show the shape layout
//            animationToShowToolsDetailVertical();
            showTextDetailContainer();
        } else {
            hideTextDetailContainer();
        }
        if (index == 2) {
            mMarkableimageview.setInWhichMode(MarkableImageView.MODE_SHAPE);
            //animation to show the shape layout
//            animationToShowToolsDetailVertical();
            showShapesContainer();
        } else {
            hideShapesContainer();
        }
        if (index == 3) {
            mMarkableimageview.setInWhichMode(MarkableImageView.MODE_MOSAIC_DRAW);
            //mosaic action
//            animationToHideToolsDetailVertical();
            showMosaicDetailContainer();
        } else {
            hideMosaicDetailContainer();
            if (mPenceilAndRubberView.getMode() == PenceilAndRubberView.MODE.PENCEILON) {
//                animationToShowToolsDetailVertical();
            }
        }
    }

    @Override
    public void onShapeSelected(int index) {
        if (index == 0) {
            mMarkableimageview.setNowAddingShapeType(Shape.ShapeType.LINE);
        } else if (index == 1) {
            mMarkableimageview.setNowAddingShapeType(Shape.ShapeType.ARROW);
        } else if (index == 2) {
            mMarkableimageview.setNowAddingShapeType(Shape.ShapeType.RECT);
        } else if (index == 3) {
            mMarkableimageview.setNowAddingShapeType(Shape.ShapeType.CIRCLE);
        } else if (index == 4) {
            mMarkableimageview.setNowAddingShapeType(Shape.ShapeType.ROUNDRECT);
        }
    }

    @Override
    public void onColorPicked(int color) {
        mMarkableimageview.changePaintColor(color);
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

    private boolean mIsTextDetailContainerVisible = false;

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

    private boolean mIsRubberDetailContainerVisible = false;

    private void hideRubberDetailContainer() {
        if (mIsRubberDetailContainerVisible) {
            mIsRubberDetailContainerVisible = false;
            animationToHideToolsDetailChildHorizontal(mRubberAjustContainer);
        }
    }

    private void showRubberDetailContainer() {
        if (!mIsRubberDetailContainerVisible) {
            mIsRubberDetailContainerVisible = true;
            if (mRubberAjustContainer.getVisibility() != View.VISIBLE) {
                mRubberAjustContainer.setVisibility(View.VISIBLE);
            }
            animationToShowToolsDetailChildHorizontal(mRubberAjustContainer);
        }
    }


    private boolean mIsMosaicDetailContainerVisible = false;
    private void hideMosaicDetailContainer() {
        if (mIsMosaicDetailContainerVisible) {
            mIsMosaicDetailContainerVisible = false;
            animationToHideToolsDetailChildHorizontal(mMosaicAjustContainer);
        }
    }

    private void showMosaicDetailContainer() {
        if (!mIsMosaicDetailContainerVisible) {
            mIsMosaicDetailContainerVisible = true;
            if (mMosaicAjustContainer.getVisibility() != View.VISIBLE) {
                mMosaicAjustContainer.setVisibility(View.VISIBLE);
            }
            animationToShowToolsDetailChildHorizontal(mMosaicAjustContainer);
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
                        alertDialog();
                    }
                    break;
                case R.id.iv_save:
                    mMarkableimageview.saveImageToFile(mFilePath);
                    finish();
                    break;
                case R.id.text_add_container:
                    hideSystemKeyBoard(mEtTextAdd);
                    mTextAddContainer.setVisibility(View.GONE);
                    break;
                case R.id.iv_add_text:
                    mTextAddContainer.setVisibility(View.VISIBLE);
                    break;
                case R.id.iv_text_ok:
                    //TODO add text ok
                    break;
                case R.id.iv_stepbackward:
                    mMarkableimageview.undo();
                    break;
                case R.id.iv_stepforward:
                    mMarkableimageview.redo();
                    break;
            }
        }
    }

    private void hideSystemKeyBoard(View v) {
        InputMethodManager imm = (InputMethodManager) this.getSystemService(Context
                .INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

    private void alertDialog() {
        SimpleCustomDialog.Builder dialog = new SimpleCustomDialog.Builder(this);
        dialog.setNegativeButtonClickListener(this)
                .setPositiveButtonClickListener(this)
                .create().show();
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (seekBar == mThicknessSeekBar) {
            mMarkableimageview.changeFreeDrawPaintThickness(progress);
        } else if (seekBar == mAlphaSeekBar) {
            mMarkableimageview.changeFreeDrawPaintAlpha(progress);
        } else if (seekBar == mRubberThicknessSeekBar) {
            mMarkableimageview.changeRubberDrawPaintAlpha(progress);
        } else if (seekBar == mMosaicThicknessSeekBar) {
            mMarkableimageview.changeMosaicDrawPaintAlpha(progress);
        }
    }

    @Override
    public void onBackPressed() {
        if (!mMarkableimageview.isEdited()) {
            finish();
        } else {
            alertDialog();
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

    @Override
    public void onClick(DialogInterface dialog, int which) {
        if (which == DialogInterface.BUTTON_POSITIVE) {
            finish();
        } else if (which == DialogInterface.BUTTON_NEGATIVE) {
            //eat it
        }
    }
}