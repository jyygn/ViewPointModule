package com.viewpoint;

import org.json.JSONException;

import com.le.mobile.R;
import com.letv.mobile.common.ICallback;
import com.letv.mobile.core.config.LeTVConfig;
import com.letv.mobile.core.log.Logger;
import com.letv.mobile.core.utils.ContextProvider;
import com.letv.mobile.core.utils.TimeUtils;
import com.letv.mobile.player.highlight.viewpoint.common.ViewPointConfiguration;
import com.letv.mobile.player.highlight.viewpoint.file.ViewPointsFile;
import com.letv.mobile.player.highlight.viewpoint.reader.ViewPointsInfoReader;
import com.letv.mobile.player.highlight.viewpoint.util.WidgetUtil;
import com.letv.mobile.player.highlight.viewpoint.common.ViewPointConstants;

import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.TextView;

/**
 * Created by yanguannan on 16/8/30.
 */
public class ViewPointControlModule {

    static {
        int size512KB = 512 * 1024;
        String viewPointCacheDirPath = LeTVConfig.getAppRoot() + "/videoPoint/";
        int softCacheNum = 20;
        int maxCacheSize = 3;
        ViewPointConfiguration viewPointConfiguration = new ViewPointConfiguration.Builder()
                .setCachePath(viewPointCacheDirPath).setLruCacheSize(size512KB)
                .setSoftCacheNum(softCacheNum).setMaxCacheSize(maxCacheSize).build();
        ViewPointsModule.init(viewPointConfiguration);
    }
    private String TAG = ViewPointControlModule.class.getSimpleName();
    private String mVideoId;
    private String mFromUrl;
    private boolean mIsIniting = true;
    private View mViewPointContainer;
    private ImageView mIvViewPointDialogView;
    private ViewPointsModule mVideoPointsModel = null;
    private volatile boolean mHasViewPoint = false;// 标记是否有视点图
    private TextView mTxtViewPointTip;
    private int xOffset = 0;
    private PopupWindow mPopupWindow;
    private int mLeftX;
    private int mY;
    private int mDialogViewMarginTop;

    public ViewPointControlModule(String fromUrl, String videoId) {
        // 初始化前确定videoId
        this.mVideoId = videoId;
        ViewPointConstants.createViewPointCacheMkdirByVideoId(videoId);
        this.mFromUrl = fromUrl;
        try {
            new ViewPointsInfoReader().readFileText(fromUrl, new ICallback<String>() {

                @Override
                public void hand(String result) {

                    Message msg = ViewPointControlModule.this.mHandler
                            .obtainMessage(ViewPointControlModule.this.JSON_DATA_LOAD_OVER);
                    msg.obj = result;
                    msg.sendToTarget();
                }
            });
        } catch (Exception e1) {
            e1.printStackTrace();
        }

    }

    /**
     * json data加载完毕消息
     */
    final byte JSON_DATA_LOAD_OVER = 1;

    Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
            case JSON_DATA_LOAD_OVER:

                if (null != msg.obj) {
                    ViewPointControlModule.this.init((String) msg.obj);
                } else {
                    // 没有视点图数据的情况删除视点图ID缓存目录
                    ViewPointConstants
                            .delViewPointCacheMkdirByVideoId(ViewPointControlModule.this.mVideoId);
                }

                break;
            }
        }

    };

    /**
     * 初始化视点图数据
     * @param jsonContent
     */
    void init(String jsonContent) {
        try {
            // sample json
            // String jsonName =
            // "http://i1.letvimg.com/yunzhuanma/201411/03/ed4a1c8558587c0d0cc73f34088ea978/viewpoint/";//
            // desc_mp.json
            Logger.d(this.TAG, "jsonContent " + jsonContent);
            ViewPointsFile viewPointsFile = ViewPointsFile.createNewFile(jsonContent,
                    this.mVideoId, this.mFromUrl);
            this.mVideoPointsModel = new ViewPointsModule(viewPointsFile);
            this.mHasViewPoint = this.mVideoPointsModel.getViewPointInfo().getThumImgTotalCount() > 0;
            Logger.d(this.TAG, "mVideoPoints.getThumImgTotalCount() "
                    + this.mVideoPointsModel.getViewPointInfo().getThumImgTotalCount());
            this.mIsIniting = false;

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 初始化视点图对话框
     */
    private void initDialogView() {
        this.mViewPointContainer = LayoutInflater.from(ContextProvider.getApplicationContext())
                .inflate(R.layout.player_full_view_points_thumbnai_layout, null);
        this.mIvViewPointDialogView = (ImageView) this.mViewPointContainer
                .findViewById(R.id.iv_view_point_img);
        this.mTxtViewPointTip = (TextView) this.mViewPointContainer
                .findViewById(R.id.txt_view_point_time);
        WidgetUtil.measureView(this.mViewPointContainer);

        this.xOffset = this.mViewPointContainer.getMeasuredWidth() / 2;

        this.mPopupWindow = new PopupWindow(this.mViewPointContainer);
        this.mPopupWindow.setFocusable(false);
        this.mDialogViewMarginTop = (int) ContextProvider.getApplicationContext().getResources()
                .getDimension(R.dimen.letv_dimens_7);
    }

    /**
     * 初始化视图
     * @param seekBar
     */
    public void initView(SeekBar seekBar) {
        initDialogView();
        WidgetUtil.measureView(seekBar);
        this.mY = WidgetUtil.getViewOfY(seekBar) - this.mViewPointContainer.getMeasuredHeight();
        Logger.d(
                TAG,
                "initView mY " + this.mY + " WidgetUtil.getViewOfY(seekBar) "
                        + WidgetUtil.getViewOfY(seekBar)
                        + " mViewPointContainer.getMeasuredHeight() "
                        + this.mViewPointContainer.getMeasuredHeight());
        Logger.d(TAG, "initDialogView xOffset " + this.xOffset);
    }

    /**
     * 显示视点图对话框
     * @param seekBar
     */
    private void showViewPointDialog(SeekBar seekBar) {
        if (!this.mPopupWindow.isShowing()) {
            this.mPopupWindow.showAsDropDown(seekBar.getRootView());
            this.mPopupWindow.update(getThumbMiddleX(seekBar) - this.xOffset, this.mY
                    - this.mDialogViewMarginTop, this.mViewPointContainer.getMeasuredWidth(),
                    this.mViewPointContainer.getMeasuredHeight());
        }
    }

    public boolean onTouch(View v, MotionEvent event, float progress) {
        if (!isCanDisplay()) {
            return false;
        }
        SeekBar seekBar = (SeekBar) v;

        int rawX = (int) event.getRawX() - xOffset + mLeftX;
        int rawY = (int) event.getRawY();

        Logger.d(
                TAG,
                "progress " + progress + " v.getY() " + v.getY() + " getThumbMiddleX "
                        + this.getThumbMiddleX(seekBar) + "  rawX " + rawX + " rawY " + rawY
                        + " seekBar.getLeft " + mLeftX + " mY " + mY);
        switch (event.getAction()) {
        case MotionEvent.ACTION_DOWN:
            if (mLeftX == 0) {
                int seekBarXY[] = WidgetUtil.getViewOfXY(seekBar);
                this.mLeftX = seekBarXY[0];
                this.mY = seekBarXY[1] - this.mViewPointContainer.getMeasuredHeight();
            }
            this.showViewPointDialog(seekBar);
            break;
        case MotionEvent.ACTION_MOVE:
            this.mTxtViewPointTip.setText(TimeUtils.getDuration((int) progress));
            this.mPopupWindow.update(rawX, rawY - this.mDialogViewMarginTop,
                    this.mViewPointContainer.getMeasuredWidth(),
                    this.mViewPointContainer.getMeasuredHeight());
            this.refreshViewPointData(progress);
            break;
        case MotionEvent.ACTION_UP:
            this.mPopupWindow.dismiss();
            break;
        }
        return false;
    }

    private Drawable mThumbDrawable;

    private void refreshViewPointData(float progress) {
        if (this.mIsIniting) {
            this.mIvViewPointDialogView.setImageResource(R.drawable.default_img);
        } else {
            if (progress < this.mVideoPointsModel.getViewPointInfo().getTotalTime()) {
                // TODO point time to long
                this.mVideoPointsModel.setPointImg(this.mIvViewPointDialogView,
                        (int) progress * 1000);
            }
        }
    }

    public int getThumbMiddleX(SeekBar seekBar) {
        if (null == this.mThumbDrawable) {
            this.mThumbDrawable = seekBar.getThumb();
        }
        int middleX = this.mThumbDrawable.getBounds().left
                + (this.mThumbDrawable.getBounds().right - this.mThumbDrawable.getBounds().left)
                / 2;
        return middleX;
    }

    /**
     * 模拟触摸按下事件
     * @param view
     * @param progress
     * @return
     */
    public boolean performTouchDown(View view, float progress) {
        SeekBar seekBar = (SeekBar) view;
        int x = this.getThumbMiddleX(seekBar);
        MotionEvent downEvent = MotionEvent.obtain(MotionEvent.obtain(SystemClock.uptimeMillis(),
                SystemClock.uptimeMillis(), MotionEvent.ACTION_DOWN, x, 0, 0));
        return this.onTouch(view, downEvent, progress);
    }

    /**
     * 模拟触摸松手事件
     * @param view
     * @param progress
     * @return
     */
    public boolean performTouchUp(View view, float progress) {
        SeekBar seekBar = (SeekBar) view;
        int x = getThumbMiddleX(seekBar);
        MotionEvent downEvent = MotionEvent.obtain(MotionEvent.obtain(SystemClock.uptimeMillis(),
                SystemClock.uptimeMillis(), MotionEvent.ACTION_UP, x, 0, 0));
        return this.onTouch(view, downEvent, progress);
    }

    /**
     * 模拟触摸移动事件
     * @param view
     * @param progress
     * @return
     */
    public boolean performTouchMove(View view, float progress) {
        SeekBar seekBar = (SeekBar) view;
        int x = this.getThumbMiddleX(seekBar);
        MotionEvent downEvent = MotionEvent.obtain(MotionEvent.obtain(SystemClock.uptimeMillis(),
                SystemClock.uptimeMillis(), MotionEvent.ACTION_MOVE, x, mY, 0));
        return this.onTouch(view, downEvent, progress);
    }

    /**
     * 是否存在视点图
     * @return
     */
    public boolean hasViewPoint() {
        return this.mHasViewPoint;
    }

    public void clean() {
        if (mIsIniting) {
            return;
        }
        mVideoPointsModel.clearCache();
    }

    private boolean mIsCanDisplay = false;

    public boolean isCanDisplay() {
        return mIsCanDisplay;
    }

    public void setIsCanDisplay(boolean isCanDisplay) {
        mIsCanDisplay = isCanDisplay;
    }

}
