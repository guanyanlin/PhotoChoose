package cn.com.xiaolinge.matisse.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import java.io.File;

import cn.com.xiaolinge.matisse.R;
import cn.com.xiaolinge.matisse.internal.entity.SelectionSpec;
import cn.com.xiaolinge.matisse.internal.ui.BasePreviewActivity;
import cn.com.xiaolinge.matisse.internal.ui.widget.CropImageView;
import cn.com.xiaolinge.matisse.internal.utils.BitmapUtil;
import cn.com.xiaolinge.matisse.internal.utils.Platform;
import cn.com.xiaolinge.matisse.internal.utils.SizeUtils;
import cn.com.xiaolinge.matisse.internal.utils.UIUtils;

public class ImageCropActivity extends AppCompatActivity implements View.OnClickListener, CropImageView.OnBitmapSaveCompleteListener {

    private CropImageView mCropImageView;
    private Bitmap mBitmap;
    private boolean mIsSaveRectangle;
    private int mOutputX;
    private int mOutputY;
    private SelectionSpec mSpec;
    private String imagePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mSpec = SelectionSpec.getInstance();
        setTheme(mSpec.themeId);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_image_crop);

        if (Platform.hasKitKat()) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        if (mSpec.needOrientationRestriction()) {
            setRequestedOrientation(mSpec.orientation);
        }

        imagePath = getIntent().getStringExtra(MatisseActivity.EXTRA_RESULT_SELECTION_PATH);

        mSpec = SelectionSpec.getInstance();

        //初始化View
        TextView button_preview = (TextView) findViewById(R.id.button_preview);
        button_preview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });

        TextView btn_ok = (TextView) findViewById(R.id.button_apply);
        btn_ok.setText("完成");
        btn_ok.setOnClickListener(this);
        mCropImageView = (CropImageView) findViewById(R.id.cv_crop_image);
        mCropImageView.setOnBitmapSaveCompleteListener(this);

        //获取需要的参数
        mOutputX = mSpec.cropOutPutX;
        mOutputY = mSpec.cropOutPutY;
        mIsSaveRectangle = mSpec.isCropSaveRectangle;
        int cropWidth = mSpec.cropFocusWidth > 0 && mSpec.cropFocusWidth < UIUtils.getScreenWidth(this) - (int) SizeUtils.dp2px(this, 50)
                ? mSpec.cropFocusWidth : UIUtils.getScreenWidth(this) - (int) SizeUtils.dp2px(this, 50);

        int cropHeight = mSpec.cropFocusHeight > 0 && mSpec.cropFocusHeight < UIUtils.getScreenHeight(this) - (int) SizeUtils.dp2px(this, 400)
                ? mSpec.cropFocusHeight : UIUtils.getScreenHeight(this) - (int) SizeUtils.dp2px(this, 400);

        mCropImageView.setFocusStyle(mSpec.cropStyle);
        mCropImageView.setFocusWidth(cropWidth);
        mCropImageView.setFocusHeight(cropHeight);

        //缩放图片
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imagePath, options);
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        options.inSampleSize = calculateInSampleSize(options, displayMetrics.widthPixels, displayMetrics.heightPixels);
        options.inJustDecodeBounds = false;
        mBitmap = BitmapFactory.decodeFile(imagePath, options);
        //设置默认旋转角度
        mCropImageView.setImageBitmap(mCropImageView.rotate(mBitmap, BitmapUtil.getBitmapDegree(imagePath)));
    }

    public int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        int width = options.outWidth;
        int height = options.outHeight;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            if (width > height) {
                inSampleSize = width / reqWidth;
            } else {
                inSampleSize = height / reqHeight;
            }
        }
        return inSampleSize;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.button_back) {
            setResult(RESULT_CANCELED);
            finish();
        } else if (id == R.id.button_apply) {
            mCropImageView.saveBitmapToFile(getCropCacheFolder(this), mOutputX, mOutputY, mIsSaveRectangle);
        }
    }

    File cropCacheFolder;

    public File getCropCacheFolder(Context context) {
        if (cropCacheFolder == null) {
            cropCacheFolder = new File(context.getCacheDir() + "/ImagePicker/cropTemp/");
        }
        return cropCacheFolder;
    }

    @Override
    public void onBitmapSaveSuccess(File file) {
        Intent intent = new Intent();
        intent.putExtra(BasePreviewActivity.EXTRA_RESULT_BUNDLE, file.getAbsolutePath());
        setResult(Activity.RESULT_OK, intent);
        finish();
    }

    @Override
    public void onBitmapSaveError(File file) { }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mCropImageView.setOnBitmapSaveCompleteListener(null);
        if (null != mBitmap && !mBitmap.isRecycled()) {
            mBitmap.recycle();
            mBitmap = null;
        }
    }
}
