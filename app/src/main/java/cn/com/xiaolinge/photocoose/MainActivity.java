package cn.com.xiaolinge.photocoose;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.hjq.permissions.OnPermission;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;

import java.util.List;

import cn.com.xiaolinge.matisse.Matisse;
import cn.com.xiaolinge.matisse.MimeType;
import cn.com.xiaolinge.matisse.engine.impl.GlideEngine;
import cn.com.xiaolinge.matisse.filter.Filter;
import cn.com.xiaolinge.matisse.internal.entity.CaptureStrategy;
import cn.com.xiaolinge.matisse.internal.ui.widget.CropImageView;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_CHOOSE = 101;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        XXPermissions.with(this)
                .permission(Permission.Group.STORAGE, Permission.Group.CAMERA, Permission.Group.LOCATION) //不指定权限则自动获取清单中的危险权限
                .request(new OnPermission() {
                    @Override
                    public void hasPermission(List<String> granted, boolean isAll) {
                        if (isAll) {
                            Toast.makeText(MainActivity.this, "获取权限成功", Toast.LENGTH_SHORT).show();
                        }else {
                            Toast.makeText(MainActivity.this, "获取权限成功，部分权限未正常授予", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void noPermission(List<String> denied, boolean quick) {
                        if(quick) {
                            Toast.makeText(MainActivity.this, "被永久拒绝授权，请手动授予权限", Toast.LENGTH_SHORT).show();
                            //如果是被永久拒绝就跳转到应用权限系统设置页面
                            XXPermissions.gotoPermissionSettings(MainActivity.this);
                        }else {
                            Toast.makeText(MainActivity.this, "获取权限失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                });


        Matisse.from(MainActivity.this)
                .choose(MimeType.ofAll(), false)      // 展示所有类型文件（图片 视频 gif）
                .capture(true)                        // 可拍照
                .countable(true)                      // 记录文件选择顺序
                .captureStrategy(new CaptureStrategy(true, "cn.com.xiaolinge.photocoose.fileprovider"))
                .maxSelectable(3)                     // 最多选择一张
                .isCrop(true)                         // 开启裁剪
                .cropOutPutX(400)                     // 设置裁剪后保存图片的宽高
                .cropOutPutY(400)                     // 设置裁剪后保存图片的宽高
                .cropStyle(CropImageView.Style.RECTANGLE)   // 方形裁剪CIRCLE为圆形裁剪
                .isCropSaveRectangle(true)                  // 裁剪后保存方形（只对圆形裁剪有效）
                .addFilter(new GifSizeFilter(320, 320, 5 * Filter.K * Filter.K))  // 筛选数据源可选大小限制
                .gridExpectedSize(getResources().getDimensionPixelSize(R.dimen.grid_expected_size))
                .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
                .thumbnailScale(0.8f)
                .imageEngine(new GlideEngine())
                .forResult(REQUEST_CODE_CHOOSE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_CHOOSE && resultCode == RESULT_OK) {
            List<Uri> uris = Matisse.obtainResult(data);
        }
    }
}
