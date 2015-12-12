package com.example.lazyyeah.openimage;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.io.ByteArrayOutputStream;
import java.io.File;


public class MainActivity extends Activity {

    public final static int PHOTO_ZOOM = 0;
    public final static int TAKE_PHOTO = 1;
    public final static int PHOTO_RESULT = 2;
    public static final String IMAGE_UNSPECIFIED = "image/*";
    private String imageDir;
    private ImageView avatar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        avatar = (ImageView) findViewById(R.id.avatar);
        // 本地图库选择按钮
        LinearLayout upload =
                (LinearLayout) findViewById(R.id.local_select_button);
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType(IMAGE_UNSPECIFIED);
                Intent wrapperIntent=Intent.createChooser(intent, null);
                startActivityForResult(wrapperIntent, PHOTO_ZOOM);
            }
        });
        // 拍照按钮
        LinearLayout takePhoto=
                (LinearLayout)findViewById(R.id.take_photo_button);
        takePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageDir = "temp.jpg";
                Intent intent=
                        new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(new File(Environment.getExternalStorageDirectory(), imageDir)));
                startActivityForResult(intent, TAKE_PHOTO);
            }
        });
    }

    // 图片缩放
    public void photoZoom(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, IMAGE_UNSPECIFIED);
        intent.putExtra("crop", "true"); //设置了参数，就会调用裁剪，如果不设置，就会跳过裁剪的过程。
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);//这个是裁剪时候的 裁剪框的 X 方向的比例。
        intent.putExtra("aspectY", 1); //同上Y 方向的比例. (注意： aspectX, aspectY ，两个值都需要为 整数，如果有一个为浮点数，就会导致比例失效。)
//设置aspectX 与 aspectY 后，裁剪框会按照所指定的比例出现，放大缩小都不会更改。如果不指定，那么 裁剪框就可以随意调整了。
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", 250); //返回数据的时候的 X 像素大小。
        intent.putExtra("outputY", 250); //返回的时候 Y 的像素大小。
        //intent.putExtra("noFaceDetection", true); 是否去除面部检测， 如果你需要特定的比例去裁剪图片，那么这个一定要去掉，因为它会破坏掉特定的比例。
        intent.putExtra("return-data", true);//是否要返回值。 一般都要。
        startActivityForResult(intent, PHOTO_RESULT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == PHOTO_ZOOM) {
                photoZoom(data.getData());
            }
            if (requestCode == TAKE_PHOTO) {
                File picture = new File(Environment.getExternalStorageDirectory() + "/" + imageDir);
                photoZoom(Uri.fromFile(picture));
            }

            if (requestCode == PHOTO_RESULT) {
                Bundle extras = data.getExtras();
                if (extras != null) {
                    Bitmap photo = extras.getParcelable("data");
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    photo.compress(Bitmap.CompressFormat.JPEG, 75, stream);
                    avatar.setImageBitmap(photo);
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

}