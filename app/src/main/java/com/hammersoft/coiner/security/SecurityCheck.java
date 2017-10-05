package com.hammersoft.coiner.security;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.util.Base64;
import android.util.Log;

import java.security.MessageDigest;

/**
 * Created by JoãoMarcelo on 04/04/2017.
 */

public class SecurityCheck {

    private static final String PLAY_STORE_APP_ID = "com.android.vending";

    public static final int VALID = 0;
    public static final int INVALID = 1;

    private static final String SIGNATURE = "igViOvCYs/jNCUPnYBJ4WIgm+VU=";

    public static int checkAppSignature(Context context) {
        return VALID;
/*
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(),
                            PackageManager.GET_SIGNATURES);
            for (Signature signature : packageInfo.signatures) {
                byte[] signatureBytes = signature.toByteArray();
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                final String currentSignature = Base64.encodeToString(md.digest(), Base64.DEFAULT);
                if (SIGNATURE.equals(currentSignature.replace("\n", ""))){
                    System.out.println("VALIDO");
                    return VALID;
                }
            }
        } catch (Exception e) {
        }
        return INVALID;
        */
    }

    public static boolean verifyInstaller(final Context context) {
        final String installer = context.getPackageManager()
                .getInstallerPackageName(context.getPackageName());
        return installer != null
                && installer.startsWith(PLAY_STORE_APP_ID);
    }
}
