# PhotoChoose

一、通过这种方式添加依赖

dependencies {
    implementation fileTree(include: ['*.jar'], dir: 'libs')
    implementation 'com.android.support:appcompat-v7:28.0.0'
    implementation 'com.android.support.constraint:constraint-layout:1.1.3'
    testImplementation 'junit:junit:4.12'
    androidTestImplementation 'com.android.support.test:runner:1.0.2'
    androidTestImplementation 'com.android.support.test.espresso:espresso-core:3.0.2'
    implementation 'com.github.guanyanlin:PhotoChoose:v1.0'
}


二、然后通过以下方式进行使用
Matisse.from(MjspUploadActivity.this)
                        .choose(MimeType.ofAll())
                        .countable(true)
                        .maxSelectable(10-m_dataList.size())
                        .isCrop(true)
                        .cropStyle(CropImageView.Style.RECTANGLE)
                        .cropFocusHeight(120)
                        .cropFocusWidth(100)
                        .spanCount(3)
                        .capture(true)
                        .captureStrategy(new CaptureStrategy(true, "com.xiaolinge.exer.zhylapp.fileprovider"))
                        .gridExpectedSize(getResources().getDimensionPixelSize(R.dimen.grid_expected_size))
                        .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT )
                        .thumbnailScale(0.85f)
                        .imageEngine(new GlideEngine())
                        .forResult(REQUEST_CODE_CHOOSE);
												
三、记得添加相册和相机权限（特别是高版本的相机拍照的权限问题需要注意），不然的话调用相机就会崩溃，归根结底就是您的相机权限没有设置好导致的，具体问题可以私信我
												
												
												
