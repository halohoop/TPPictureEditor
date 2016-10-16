package com.halohoop.ipccangetbitmapnull;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "bapo";
    private String path = "/mnt/sdcard/Pictures/Screenshots/Screenshot_2016-10-08-03-11-38.png";
    private ImageView iv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        iv = (ImageView) findViewById(R.id.iv);
        Intent service = new Intent("android.intent.action.Halohoop");
        service.setPackage("com.halohoop.ipccangetbitmapnull");
        this.bindService(service, new MyServiceConnection(), Context.BIND_AUTO_CREATE);
    }

    private IBinder mMyBinder;


//    public void show1(View v) {
//        android.os.Parcel _data = android.os.Parcel.obtain();
//        android.os.Parcel _reply = android.os.Parcel.obtain();
//        String _result = null;
//        try {
//            _data.writeInterfaceToken("IPCService");
//            _data.writeInt(num);
//            mIBinder.transact(0x001, _data, _reply, 0);
//            _reply.readException();
//            _result = _reply.readString();
//            txt_result.setText(_result);
//            edit_num.setText("");
//        } catch (RemoteException e) {
//            e.printStackTrace();
//        } finally {
//            _reply.recycle();
//            _data.recycle();
//        }
//    }

    public void show(View v) {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        String result = null;
        try {
            _data.writeInterfaceToken("Halohoop");
            _data.writeString(path);
            mMyBinder.transact(0, _data, _reply, 0);
            _reply.readException();
            result = _reply.readString();
            Log.i(TAG, "show: "+result);
            Bitmap bitmap = _reply.readParcelable(Bitmap.class.getClassLoader());
            iv.setImageBitmap(bitmap);
            Log.i(TAG, "show:bitmap: "+bitmap);
            Bitmap bitmap1 = _reply.readParcelable(Bitmap.class.getClassLoader());
            Log.i(TAG, "show:bitmap1: "+bitmap1);
            iv.setImageBitmap(bitmap1);
            _data.recycle();
            _reply.recycle();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    class MyServiceConnection implements ServiceConnection {


        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mMyBinder = service;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mMyBinder = null;
        }
    }
}

