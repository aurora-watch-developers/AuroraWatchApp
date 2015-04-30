package org.aurorawatchdevs.aurorawatch.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.common.AccountPicker;

/**
 * Created by jamesb on 29/04/2015.
 */
public class GoogleUtil {

    static final int REQUEST_CODE_PICK_ACCOUNT = 1000;

    public void pickUserAccount(Activity activity) {
        String[] accountTypes = new String[]{"com.google"};
        Intent intent = AccountPicker.newChooseAccountIntent(null, null,
                accountTypes, false, null, null, null, null);
        activity.startActivityForResult(intent, REQUEST_CODE_PICK_ACCOUNT);
    }

}
