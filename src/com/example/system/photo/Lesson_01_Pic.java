package com.example.system.photo;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
public class Lesson_01_Pic extends Activity {
	
	File sdcardTempFile;
	
	private final int VERSION = 33;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        sdcardTempFile = new File(Environment.getExternalStorageDirectory(), "tmp_pic" + ".jpg");
//        File f = this.getFilesDir();
//        sdcardTempFile = new File (f, "tmp_pic.jpg");
        if ( !sdcardTempFile.exists() )
			try {
				sdcardTempFile.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Toast.makeText(this, "file create error", Toast.LENGTH_LONG).show();
			}
//        setImage();
        
        Button button = (Button)findViewById(R.id.b01);
        button.setText("拍照");
        button.setOnClickListener(new Button.OnClickListener(){
			@Override
			public void onClick(View v) {
				takePhoto();
			}
        	
        });
    }
    //assets复制到data/files目录下
    private void setImage ()
    {
    	 System.out.println(sdcardTempFile.getAbsolutePath());
         
         if ( sdcardTempFile.exists() )
         {
         	System.out.println(123);
         }
         else
         {
         	System.out.println(321);
         	try {
     			sdcardTempFile.createNewFile();
     		} catch (IOException e) {
     			// TODO Auto-generated catch block
     			e.printStackTrace();
     		}
         }
         
         try {
        	 InputStream is = getResources().getAssets().open("tmp_pic.jpg");
			OutputStream os = new FileOutputStream(sdcardTempFile);
			byte[] b = new byte[is.available()];
			while ( is.read(b) != -1 )
			{
				os.write(b);
			}
			is.close();
			os.close();
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
         
         if ( sdcardTempFile != null )
         {
         	InputStream is = null;
         	try {
 				is = new FileInputStream(sdcardTempFile);
 			} catch (FileNotFoundException e) {
 				// TODO Auto-generated catch block
 				e.printStackTrace();
 			}
         	
         	Bitmap bitmap = BitmapFactory.decodeStream(is);
         	
//         	Bitmap bitmap = BitmapFactory.decodeFile(sdcardTempFile
// 					.getAbsolutePath());
 		ImageView imageView = (ImageView) findViewById(R.id.iv01);
 		/* 将Bitmap设定到ImageView */
 		imageView.setImageBitmap(bitmap);
         }
         else
         {
         	Toast.makeText(this, "file is null", Toast.LENGTH_LONG).show();
         }
    }
    
    public void btn_photo ( View v )
    {
    	pickPhoto();
    }
    
    
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//		doPhoto(requestCode, data);
//	getPhoto(requestCode, resultCode, data);	
		if (resultCode == RESULT_OK) {
			if ( requestCode == 3 )
			{
//				Uri uri = data.getData();
//				Log.e("uri", uri.toString());
//				ContentResolver cr = this.getContentResolver();
				try {
//					Bitmap bitmap = BitmapFactory.decodeStream(cr.openInputStream(uri));
					Bitmap bitmap = BitmapFactory.decodeFile(sdcardTempFile
								.getAbsolutePath());
					ImageView imageView = (ImageView) findViewById(R.id.iv01);
					/* 将Bitmap设定到ImageView */
					imageView.setImageBitmap(bitmap);
				} catch (Exception e) {
					Log.e("Exception", e.getMessage(),e);
				}
			}
			else if ( requestCode == REQUEST_CODE_SELECT_FROM_ALBUM )
			{
				getPhoto(requestCode, resultCode, data);
			}
			else if (requestCode == PHOTOHRAPH) {
				// 设置文件保存路径这里放在跟目录下
//				File picture = new File(Environment.getExternalStorageDirectory()
//						+ "/temp.jpg");
//				System.out.println("------------------------" + picture.getPath());
				startPhotoZoom(Uri.fromFile(sdcardTempFile));
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	public void startPhotoZoom(Uri uri) {
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		intent.putExtra("output", Uri.fromFile(sdcardTempFile));
		intent.putExtra("crop", "true");
		// aspectX aspectY 是宽高的比例
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		// outputX outputY 是裁剪图片宽高
		intent.putExtra("outputX", 164);
		intent.putExtra("outputY", 164);
		intent.putExtra("return-data", false);
		intent.putExtra("url", uri);
		startActivityForResult(intent, 3);
	}
	
	Uri photoUri;
	String picPath;
	String TAG = "MYUTIL";
	/*** 
     * 使用照相机拍照获取图片 
     */  
    public static final int SELECT_PIC_BY_TACK_PHOTO = 1;  
    /** 
     * 拍照获取图片 
     */  
    private void takePhoto() {  
    	Intent intent2 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		intent2.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(sdcardTempFile));
		System.out.println("============="
				+ Environment.getExternalStorageDirectory());
		startActivityForResult(intent2, PHOTOHRAPH);
    }  
  
    /*** 
     * 从相册中取图片 
     */  
    private void pickPhoto() {  
    	
    	Intent intent = new Intent();  
        //根据版本号不同使用不同的Action  
        if (Build.VERSION.SDK_INT < VERSION) {  
            intent.setAction(Intent.ACTION_GET_CONTENT);  
            //这2个都可以
//            intent.setAction(Intent.ACTION_PICK);  
        }  
        else {  
            intent.setAction(Intent.ACTION_OPEN_DOCUMENT);  
            intent.addCategory(Intent.CATEGORY_OPENABLE);  
        }   
        intent.setType("image/*");  
        intent.putExtra("crop", "true");  
        intent.putExtra("output", Uri.fromFile(sdcardTempFile));  
        startActivityForResult(intent, REQUEST_CODE_SELECT_FROM_ALBUM);  
    	
    }  
    final int REQUEST_CODE_SELECT_FROM_ALBUM = 123;
    final int PHOTOHRAPH = 321;
    private void getPhoto (int requestCode, int resultCode, Intent data)
    {
    	 String filePath = null;
                     if(data != null) {  
                         Uri imageUri;  
                         if(data.getData() != null){  
                             imageUri = data.getData();  
                         } else {  
                             imageUri = Uri.parse(sdcardTempFile.getAbsolutePath());  
                         }  
                           
                         if (Build.VERSION.SDK_INT < VERSION) {  
//                             sessionTheme.themePath = imageUri.getPath();  
                             Bitmap bitmap = BitmapFactory.decodeFile(imageUri.getPath());
                     		ImageView imageView = (ImageView) findViewById(R.id.iv01);
                     		/* 将Bitmap设定到ImageView */
                     		imageView.setImageBitmap(bitmap);
                     		startPhotoZoom(imageUri);
                         }  
                         else {  
                             String wholeID = DocumentsContract.getDocumentId(imageUri);  
                             if(!TextUtils.isEmpty(wholeID) && wholeID.contains(":")) {  
                                 // 获得资源唯一ID  
                                 String id = wholeID.split(":")[1];  
                                 // 定义索引字段  
                                 String[] column = { MediaStore.Images.Media.DATA };  
                                 String sel = MediaStore.Images.Media._ID + "=?";  
                                   
                                 Cursor cursor = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, column, sel, new String[] { id }, null);  
                                 int columnIndex = cursor.getColumnIndex(column[0]);  
                                   
                                 if (cursor.moveToFirst()) {  
                                     // DATA字段就是本地资源的全路径  
                                     filePath = cursor.getString(columnIndex);  
                                 }  
                                 // 切记要关闭游标  
                                 cursor.close();  
                             }
                             Bitmap bitmap = BitmapFactory.decodeFile(filePath);
                       		ImageView imageView = (ImageView) findViewById(R.id.iv01);
                       		/* 将Bitmap设定到ImageView */
                       		imageView.setImageBitmap(bitmap);
                       		File f = new File(filePath);
                       		Uri uri = Uri.fromFile(f);
                       		startPhotoZoom(uri);
                         }  
                         
                         
         }  
    }
}