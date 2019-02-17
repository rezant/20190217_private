package com.example.v.a20190210_camera;
//http://yonayona.biz/yonayona/blog/archives/camera_1.html
//http://yonayona.biz/yonayona/blog/archives/camera_5.html

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements FileSelectionDialog.OnFileSelectListener {

    private List<Camera.CameraInfo> mCameraList;
    // path変数
    //private String m_strInitialDir = Environment.getExternalStorageDirectory().getPath();    // 初期フォルダ
    private String m_strInitialDir = "/data/data/com.example.v.a20190210_camera/files";
    private String targetFilePath = null;
    // View変数
    private TextView textViewHelloWorld ;
    private ImageView imageViewCheck;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


    }

    // 初回表示時、および、ポーズからの復帰時
    @Override
    protected void onResume(){
        super.onResume();

        // パーミッション要求
        requestPermission(REQUEST_PERMISSION_READ_EXTERNAL_STORAGE);
        requestPermission(REQUEST_PERMISSION_WRITE_EXTERNAL_STORAGE);
        requestPermission(REQUEST_PERMISSION_CAMERA);
        requestPermission(REQUEST_PERMISSION_RECORD_AUDIO);

        createCameraList();
        addSelectCameraButton();
    }

    // オプション(ハンバーガーメニュー)ID
    private static final int MENUID_FILE                              = 0;// ファイルメニューID
    // オプションメニュー生成
    @Override
    public boolean onCreateOptionsMenu( Menu menu )    {
        super.onCreateOptionsMenu( menu );
        menu.add( 0, MENUID_FILE, 0, "Select File..." );
        //TODO 追加OPTION_MENU
        //menu.add( 0, MENUID_FILE, 0, "Select File..." );//menu.add(ID,ID,表示順,"コメント")
        return true;
    }
    // オプションメニュー選択時
    @Override
    public boolean onOptionsItemSelected( MenuItem item )
    {
        switch( item.getItemId() )
        {
            case MENUID_FILE:
                // ダイアログオブジェクト
                FileSelectionDialog dlg = new FileSelectionDialog( this, this ,"jpg;png");
                dlg.show( new File( m_strInitialDir ) );
                return true;
        }
        return false;
    }
    // ファイルが選択されたときに呼び出される関数
    public void onFileSelect( File file )
    {
        Toast.makeText( this, "File Selected : " + file.getPath(), Toast.LENGTH_SHORT ).show();
        m_strInitialDir = file.getParent();
        targetFilePath=file.getPath();

        //TODO action
        textViewHelloWorld = findViewById(R.id.textView_HelloWorld);
        textViewHelloWorld.setText(targetFilePath);

        imageViewCheck = findViewById(R.id.imageView_Sunrise);

        File tmpImageFile = new File(targetFilePath);
        try(InputStream inputStream0 = new FileInputStream(tmpImageFile) ) {
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream0);
            imageViewCheck.setImageBitmap(bitmap);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * カメラ一覧を作る
     */
    private void createCameraList() {
        mCameraList = new ArrayList<>();
        // カメラの個数を取得する。
        int numberOfCameras = Camera.getNumberOfCameras();
        for (int i = 0; i < numberOfCameras; i++) {
            Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
            Camera.getCameraInfo(i , cameraInfo);
            mCameraList.add(cameraInfo);
        }
    }
    /**
     * カメラ選択ボタンを作る
     */
    private void addSelectCameraButton() {
        LinearLayout linearLayout = (LinearLayout)findViewById(R.id.linear_layout_camera_select);
        for (final Camera.CameraInfo cameraInfo : mCameraList) {
            Button button = new Button(this);
            button.setGravity(Gravity.LEFT);
            StringBuilder buttonText = new StringBuilder();
            // カメラの設置箇所を判断する
            buttonText.append("カメラの設置箇所 : ");
            if(Camera.CameraInfo.CAMERA_FACING_FRONT == cameraInfo.facing) {
                buttonText.append("フロントカメラ");
            } else {
                buttonText.append("バックカメラ");
            }

            buttonText.append("\n");
            buttonText.append("カメラの向き : ");
            buttonText.append(cameraInfo.orientation);
            buttonText.append("\n");
            buttonText.append("シャッター音 : ");
            if (cameraInfo.canDisableShutterSound) {
                buttonText.append("無効化できます。");
            } else {
                buttonText.append("無効化できません。");
            }
            button.setText(buttonText.toString());
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MainActivity.this,CameraActivity.class);
                    intent.putExtra(CameraActivity.CAMERA_ID , mCameraList.indexOf(cameraInfo));
                    startActivity(intent);
                }
            });
            linearLayout.addView(button);
        }
    }



    // パーミッションコード
    private static final int REQUEST_PERMISSION_READ_EXTERNAL_STORAGE    = 0; // 外部ストレージ読み込みパーミッション要求時の識別コード
    private static final int REQUEST_PERMISSION_WRITE_EXTERNAL_STORAGE   = 1; // 外部ストレージ書き込みパーミッション要求時の識別コード
    private static final int REQUEST_PERMISSION_CAMERA                   = 2; // カメラパーミッション要求時の識別コード
    private static final int REQUEST_PERMISSION_RECORD_AUDIO             = 3; // 音声記録パーミッション要求時の識別コード
    //パーミッションリクエスト
    //リクエスト項目ごとに、パーミッション取得を実施し、その結果を受け取る必要がある。
    //switch文にてコードごとに実施
    private void requestPermission(int requestCode){
        switch( requestCode ) {
            case REQUEST_PERMISSION_READ_EXTERNAL_STORAGE:
                if (PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    // パーミッションは付与されている
                    return;
                }
                // パーミッションは付与されていない。
                // パーミッションリクエスト
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},REQUEST_PERMISSION_READ_EXTERNAL_STORAGE);
                break;
            case REQUEST_PERMISSION_WRITE_EXTERNAL_STORAGE:
                if (PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    // パーミッションは付与されている
                    return;
                }
                // パーミッションは付与されていない。
                // パーミッションリクエスト
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},REQUEST_PERMISSION_WRITE_EXTERNAL_STORAGE);
                break;
            case REQUEST_PERMISSION_CAMERA:
                if (PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)) {
                    // パーミッションは付与されている
                    return;
                }
                // パーミッションは付与されていない。
                // パーミッションリクエスト
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA},REQUEST_PERMISSION_CAMERA);
                break;
            case REQUEST_PERMISSION_RECORD_AUDIO:
                if (PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)) {
                    // パーミッションは付与されている
                    return;
                }
                // パーミッションは付与されていない。
                // パーミッションリクエスト
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO},REQUEST_PERMISSION_RECORD_AUDIO);
                break;
        }

    }
    // パーミッション要求ダイアログの操作結果
    @Override
    public void onRequestPermissionsResult( int requestCode, String[] permissions, int[] grantResults ){
        switch( requestCode ){
            case REQUEST_PERMISSION_READ_EXTERNAL_STORAGE:
                if( grantResults.length == 0 || grantResults[0] != PackageManager.PERMISSION_GRANTED ){
                    // 許可されなかった場合
                    Toast.makeText( this, "Permission denied.", Toast.LENGTH_SHORT ).show();
                    finish();    // アプリ終了宣言
                    return;
                }
                break;
            case REQUEST_PERMISSION_WRITE_EXTERNAL_STORAGE:
                if( grantResults.length == 0 || grantResults[0] != PackageManager.PERMISSION_GRANTED ){
                    // 許可されなかった場合
                    Toast.makeText( this, "Permission denied.", Toast.LENGTH_SHORT ).show();
                    finish();    // アプリ終了宣言
                    return;
                }
                break;
            case REQUEST_PERMISSION_CAMERA:
                if( grantResults.length == 0 || grantResults[0] != PackageManager.PERMISSION_GRANTED ){
                    // 許可されなかった場合
                    Toast.makeText( this, "Permission denied.", Toast.LENGTH_SHORT ).show();
                    finish();    // アプリ終了宣言
                    return;
                }
                break;
            case REQUEST_PERMISSION_RECORD_AUDIO:
                if( grantResults.length == 0 || grantResults[0] != PackageManager.PERMISSION_GRANTED ){
                    // 許可されなかった場合
                    Toast.makeText( this, "Permission denied.", Toast.LENGTH_SHORT ).show();
                    finish();    // アプリ終了宣言
                    return;
                }
                break;
            default:
                break;
        }
    }

}
