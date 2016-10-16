/*
 * Copyright (C) 2016, TP-LINK TECHNOLOGIES CO., LTD.
 *
 * ServerService.java
 *
 * asd
 *
 * Author huanghaiqi, Created at 2016-10-09
 *
 * Ver 1.0, 2016-10-09, huanghaiqi, Create file.
 */

package com.halohoop.ipccangetbitmapnull;

import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Binder;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.util.Log;

import static android.content.ContentValues.TAG;

public class ServerService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new MyBinder();
    }

    class MyBinder extends Binder {
        @Override
        protected boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws
                RemoteException {
            if (code == 0) {
                data.enforceInterface("Halohoop");
                String path = data.readString();
                Log.i(TAG, "onTransact: " + path);
                Bitmap bitmap = BitmapFactory.decodeFile(path);
                reply.writeNoException();
                reply.writeString("hahaha");
                reply.writeParcelable(null, 0);
                reply.writeParcelable(bitmap, 0);
                return true;
            } else if (code == 1) {

            }
//            return super.onTransact(code, data, reply, flags);
            return true;
        }
    }

}
