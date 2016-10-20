package com.android.systemui.screenshot.editutils.pages;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.Process;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.MotionEvent;
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

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * @hide
 */
public class EditorActivity extends Activity implements
        PenceilAndRubberView.PenceilOrRubberModeCallBack,
        ShapesChooseView.OnSelectedListener,
        ActionsChooseView.OnSelectedListener, ColorPickerView.ColorPickListener,
        View.OnClickListener, SeekBar.OnSeekBarChangeListener,
        Animator.AnimatorListener,
        DialogInterface.OnClickListener,View.OnTouchListener {

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
    private View mProgressContainer;
    private NotificationManager mNotificationManager;

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
                mProgressContainer.setVisibility(View.GONE);
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
        mProgressContainer = findViewById(R.id.progress_container);
        mProgressContainer.setOnTouchListener(this);
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
        mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
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
        mMarkableimageview.setInWhichMode(MarkableImageView.MODE_SHAPE);
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

    private boolean mIsShapesContainerVisible = false;

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

    private boolean mIsColorPickerViewVisible = false;

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
                    Bitmap[] finalImages = mMarkableimageview.getFinalImage();
                    SaveImageInBackgroundData saveImageInBackgroundData = new
                            SaveImageInBackgroundData();
                    // Get the various target sizes
                    Resources r = getApplicationContext().getResources();
                    int iconSize =
                            r.getDimensionPixelSize(android.R.dimen.notification_large_icon_height);
                    saveImageInBackgroundData.context = getApplicationContext();
                    saveImageInBackgroundData.iconSize = iconSize;
                    saveImageInBackgroundData.images = finalImages;
                    SaveImageInBackgroundTask saveImageInBackgroundTask = new
                            SaveImageInBackgroundTask(getApplicationContext(), ,
                            mNotificationManager, SaveImageInBackgroundTask
                            .SCREENSHOT_NOTIFICATION_ID);
                    saveImageInBackgroundTask.execute();
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

    //just for progress layout touch
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return true;
    }

    class SaveImageInBackgroundData {
        Bitmap[] images;
        Context context;
        Uri imageUri;
        int iconSize;
        int result;
        int previewWidth;
        int previewheight;
        void clearImage() {
            images = null;
            imageUri = null;
            iconSize = 0;
        }
        void clearContext() {
            context = null;
        }
    }

    //edit bitmap save start
    private static boolean mTickerAddSpace;
    class SaveImageInBackgroundTask extends AsyncTask<SaveImageInBackgroundData, Void,
            SaveImageInBackgroundData> {
        protected static final int SCREENSHOT_NOTIFICATION_ID = 789;
        private static final String SCREENSHOTS_DIR_NAME = "Screenshots";
        private static final String SCREENSHOT_FILE_NAME_TEMPLATE = "Screenshot_edit_%s.png";
        private static final String SCREENSHOT_SHARE_SUBJECT_TEMPLATE = "Screenshot (%s)";
        private final String mImageFileName;
        private final File mScreenshotDir;
        private final String mImageFilePath;
        private final int mImageWidth;
        private final int mImageHeight;
        private final long mImageTime;
        private final NotificationManager mNotificationManager;
        private final int mNotificationId;
        private final Notification.Builder mNotificationBuilder;
        private final Notification.BigPictureStyle mNotificationStyle;
        private final Notification.Builder mPublicNotificationBuilder;

        SaveImageInBackgroundTask(Context context, SaveImageInBackgroundData data,
                                  NotificationManager nManager, int nId) {
            Resources r = context.getResources();

            // Prepare all the output metadata
            mImageTime = System.currentTimeMillis();
            String imageDate = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(new Date
                    (mImageTime));
            mImageFileName = String.format(Locale.ENGLISH, SCREENSHOT_FILE_NAME_TEMPLATE,
                    imageDate);

            mScreenshotDir = new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_PICTURES), SCREENSHOTS_DIR_NAME);
            mImageFilePath = new File(mScreenshotDir, mImageFileName).getAbsolutePath();
            // Create the large notification icon
            mImageWidth = data.images[0].getWidth();
            mImageHeight = data.images[0].getHeight();
            int iconSize = data.iconSize;
            int previewWidth = data.previewWidth;
            int previewHeight = data.previewheight;

//            final int shortSide = mImageWidth < mImageHeight ? mImageWidth : mImageHeight;
            Bitmap preview = Bitmap.createBitmap(previewWidth, previewHeight, finalMergeBitmap
                    .getConfig());
            Canvas c = new Canvas(preview);
            Paint paint = new Paint();
            ColorMatrix desat = new ColorMatrix();
            desat.setSaturation(0.25f);
            paint.setColorFilter(new ColorMatrixColorFilter(desat));
            Matrix matrix = new Matrix();
            matrix.postTranslate((previewWidth - mImageWidth) / 2,
                    (previewHeight - mImageHeight) / 2);
            c.drawBitmap(data.image, matrix, paint);
            c.drawColor(0x40FFFFFF);
            c.setBitmap(null);

            Bitmap croppedIcon = Bitmap.createScaledBitmap(preview, iconSize, iconSize, true);

            // Show the intermediate notification
            mTickerAddSpace = !mTickerAddSpace;
            mNotificationId = nId;
            mNotificationManager = nManager;
            final long now = System.currentTimeMillis();

            mNotificationBuilder = new Notification.Builder(context)
                    .setTicker(r.getString(R.string.screenshot_saving_ticker)
                            + (mTickerAddSpace ? " " : ""))
                    .setContentTitle(r.getString(R.string.screenshot_saving_title))
                    .setContentText(r.getString(R.string.screenshot_saving_text))
                    .setSmallIcon(R.drawable.stat_notify_image)
                    .setWhen(now)
                    .setColor(r.getColor(R.color
                            .system_notification_accent_color));

            mNotificationStyle = new Notification.BigPictureStyle()
                    .bigPicture(preview);
            mNotificationBuilder.setStyle(mNotificationStyle);

            // For "public" situations we want to show all the same info but
            // omit the actual screenshot image.
            mPublicNotificationBuilder = new Notification.Builder(context)
                    .setContentTitle(r.getString(R.string.screenshot_saving_title))
                    .setContentText(r.getString(R.string.screenshot_saving_text))
                    .setSmallIcon(R.drawable.stat_notify_image)
                    .setCategory(Notification.CATEGORY_PROGRESS)
                    .setWhen(now)
                    .setColor(r.getColor(R.color.system_notification_accent_color));

            //mNotificationBuilder.setPublicVersion(mPublicNotificationBuilder.build());

            Notification n = mNotificationBuilder.build();
            //n.flags |= Notification.FLAG_NO_CLEAR;
            mNotificationManager.notify(nId, n);

            // On the tablet, the large icon makes the notification appear as if it is clickable
            // (and
            // on small devices, the large icon is not shown) so defer showing the large icon until
            // we compose the final post-save notification below.
            mNotificationBuilder.setLargeIcon(croppedIcon);
            // But we still don't set it for the expanded view, allowing the smallIcon to show here.
            mNotificationStyle.bigLargeIcon((Bitmap) null);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected SaveImageInBackgroundData doInBackground(SaveImageInBackgroundData... params) {
            if (params.length != 1) return null;
            if (isCancelled()) {
                params[0].clearImage();
                params[0].clearContext();
                return null;
            }
            // By default, AsyncTask sets the worker thread to have background thread priority, so bump
            // it back up so that we save a little quicker.
            Process.setThreadPriority(Process.THREAD_PRIORITY_FOREGROUND);
            Context context = params[0].context;
            Bitmap image = params[0].image;
            Resources r = context.getResources();
            try {
                // Create screenshot directory if it doesn't exist
                mScreenshotDir.mkdirs();

                // media provider uses seconds for DATE_MODIFIED and DATE_ADDED, but milliseconds
                // for DATE_TAKEN
                long dateSeconds = mImageTime / 1000;

                OutputStream out = new FileOutputStream(mImageFilePath);
                image.compress(Bitmap.CompressFormat.PNG, 100, out);
                out.flush();
                out.close();

                // Save the screenshot to the MediaStore
                ContentValues values = new ContentValues();
                ContentResolver resolver = context.getContentResolver();
                values.put(MediaStore.Images.ImageColumns.DATA, mImageFilePath);
                values.put(MediaStore.Images.ImageColumns.TITLE, mImageFileName);
                values.put(MediaStore.Images.ImageColumns.DISPLAY_NAME, mImageFileName);
                values.put(MediaStore.Images.ImageColumns.DATE_TAKEN, mImageTime);
                values.put(MediaStore.Images.ImageColumns.DATE_ADDED, dateSeconds);
                values.put(MediaStore.Images.ImageColumns.DATE_MODIFIED, dateSeconds);
                values.put(MediaStore.Images.ImageColumns.MIME_TYPE, "image/png");
                values.put(MediaStore.Images.ImageColumns.WIDTH, mImageWidth);
                values.put(MediaStore.Images.ImageColumns.HEIGHT, mImageHeight);
                values.put(MediaStore.Images.ImageColumns.SIZE, new File(mImageFilePath).length());
                Uri uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

                String subjectDate = DateFormat.getDateTimeInstance().format(new Date(mImageTime));
                String subject = String.format(Locale.ENGLISH, SCREENSHOT_SHARE_SUBJECT_TEMPLATE,
                        subjectDate);
                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                sharingIntent.setType("image/png");
                sharingIntent.putExtra(Intent.EXTRA_STREAM, uri);
                sharingIntent.putExtra(Intent.EXTRA_SUBJECT, subject);

                Intent chooserIntent = Intent.createChooser(sharingIntent, null);
                chooserIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
                        | Intent.FLAG_ACTIVITY_NEW_TASK);

                mNotificationBuilder.addAction(R.drawable.ic_menu_share,
                        r.getString(com.android.internal.R.string.share),
                        PendingIntent.getActivity(context, 0, chooserIntent,
                                PendingIntent.FLAG_CANCEL_CURRENT));

                Intent deleteIntent = new Intent();
                deleteIntent.setClass(context, DeleteScreenshot.class);
                deleteIntent.putExtra(DeleteScreenshot.SCREENSHOT_URI, uri.toString());

                mNotificationBuilder.addAction(R.drawable.ic_menu_delete,
                        r.getString(com.android.internal.R.string.delete),
                        PendingIntent.getBroadcast(context, 0, deleteIntent,
                                PendingIntent.FLAG_CANCEL_CURRENT));

                params[0].imageUri = uri;
                params[0].image = null;
                params[0].result = 0;
            } catch (Exception e) {
                // IOException/UnsupportedOperationException may be thrown if external storage is not
                // mounted
                params[0].clearImage();
                params[0].result = 1;
            }

            // Recycle the bitmap data
            if (image != null) {
                image.recycle();
            }
            return params[0];
        }

        @Override
        protected void onPostExecute(SaveImageInBackgroundData params) {
            if (isCancelled()) {
                params.clearImage();
                params.clearContext();
                return;
            }

            if (params.result > 0) {
                // Show a message that we've failed to save the image to disk
                //fail to save
            } else {
                // Show the final notification to indicate screenshot saved
                Resources r = params.context.getResources();

                // Create the intent to show the screenshot in gallery
                Intent launchIntent = new Intent(Intent.ACTION_VIEW);
                launchIntent.setDataAndType(params.imageUri, "image/png");
                launchIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                // Extra for Gallery to view more picture in screenshot directory
                launchIntent.putExtra("need_more", true);

                final long now = System.currentTimeMillis();

                mNotificationBuilder
                        .setContentTitle(r.getString(R.string.screenshot_saved_title))
                        .setContentText(r.getString(R.string.screenshot_saved_text))
                        .setContentIntent(PendingIntent.getActivity(params.context, 0, launchIntent, 0))
                        .setWhen(now)
                        .setAutoCancel(true)
                        .setColor(r.getColor(R.color.system_notification_accent_color));;

                // Update the text in the public version as well
                mPublicNotificationBuilder
                        .setContentTitle(r.getString(R.string.screenshot_saved_title))
                        .setContentText(r.getString(R.string.screenshot_saved_text))
                        .setContentIntent(PendingIntent.getActivity(params.context, 0, launchIntent, 0))
                        .setWhen(now)
                        .setAutoCancel(true)
                        .setColor(r.getColor(R.color.system_notification_accent_color));

                //mNotificationBuilder.setPublicVersion(mPublicNotificationBuilder.build());

                Notification n = mNotificationBuilder.build();
                n.flags &= ~Notification.FLAG_NO_CLEAR;
                mNotificationManager.notify(mNotificationId, n);
            }
            params.clearContext();
        }
    }
    //edit bitmap save end
}