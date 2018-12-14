package com.ao.android.emulation;

import android.content.Intent;
import android.nfc.cardemulation.HostApduService;
import android.os.Bundle;
import android.util.Log;

import java.util.Arrays;
import java.util.UUID;

public class HostCardEmulatorService extends HostApduService {

    private static final String TAG = HostCardEmulatorService.class.getSimpleName();
    private static UUID id;
    public static final byte[] SELECT_AID = new byte[] {
            (byte) 0x00,
            (byte) 0xA4,
            (byte) 0x04,
            (byte) 0x00,
            (byte) 0x07,
            (byte) 0xF0, (byte) 0x39, (byte) 0x41, (byte) 0x48, (byte) 0x14, (byte) 0x81, (byte) 0x00, (byte) 0x00
    };

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        id = UUID.fromString(intent.getStringExtra("APPLICATION_ID"));
        Log.d(TAG, "Application Id: " + id);
        return START_NOT_STICKY;
    }

    @Override
    public byte[] processCommandApdu(byte[] bytes, Bundle bundle) {
        String apduDesc;
        byte[] response;
        Log.d(TAG,"Bytes Array: " + Arrays.toString(bytes));
        if (Arrays.equals(SELECT_AID, bytes)) {
            apduDesc = "App Selected";
            Log.d(TAG, "APDU Description: " + apduDesc);
            byte[] ans = new byte[2];
            ans[0] = (byte) 0x90;
            ans[1] = (byte) 0x00;
            response = ans;
        } else if (selectCheckIdApdu(bytes)) {
            Log.d(TAG, "APDU Check UUID");
            String uid = id.toString();
            Log.d(TAG, "SENT UUID: " + uid);
            byte[] ans = new byte[2];
            ans[0] = (byte) 0x90;
            ans[1] = (byte) 0x00;
            byte[] uidB = uid.getBytes();
            ans = combineArrays(ans, uidB);
            response = ans;
        } else {
            Log.d(TAG, "Unknown Command");
            byte[] ans = new byte[2];
            ans[0] = (byte) 0x6F;
            ans[1] = (byte) 0x00;
            response = ans;
        }
        Log.d(TAG, "Response: " + Arrays.toString(response));
        return response;
    }

    @Override
    public void onDeactivated(int i) {
        Log.d(TAG, "Reason: " + i);
    }

    private boolean selectCheckIdApdu(byte[] apdu) {
        return apdu.length >= 2 && apdu[0] == (byte) 0x80 && apdu[1] == (byte) 0x03
                && apdu[2] == (byte) 0x00 && apdu[3] == (byte) 0x00;
    }

    public static byte[] combineArrays(byte[] a, byte[] b) {
        byte[] c = new byte[a.length + b.length];
        System.arraycopy(a, 0, c, 0, a.length);
        System.arraycopy(b, 0, c, a.length, b.length);
        return c;
    }
}
