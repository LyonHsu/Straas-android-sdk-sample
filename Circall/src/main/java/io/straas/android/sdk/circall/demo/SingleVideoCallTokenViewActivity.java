package io.straas.android.sdk.circall.demo;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;

import io.straas.android.sdk.demo.R;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

import static io.straas.android.sdk.circall.demo.SingleVideoCallActivity.INTENT_CIRCALL_TOKEN;

public class SingleVideoCallTokenViewActivity extends TokenViewBaseActivity {

    public static final String[] SINGLE_VIDEO_CALL_PERMISSIONS = {
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO
    };

    // 1 and 2 are used in TokenViewBaseActivity
    private static final int SINGLE_VIDEO_CALL_PERMISSIONS_REQUEST = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding.enter.setText(getResources().getString(R.string.enter_room));
    }

    @Override
    protected void enterRoom() {
        checkSingleVideoCallPermissions();
    }

    @AfterPermissionGranted(SINGLE_VIDEO_CALL_PERMISSIONS_REQUEST)
    private synchronized void checkSingleVideoCallPermissions() {
        if (EasyPermissions.hasPermissions(this, SINGLE_VIDEO_CALL_PERMISSIONS)) {
            Intent intent = new Intent(this, SingleVideoCallActivity.class);
            intent.putExtra(INTENT_CIRCALL_TOKEN, mBinding.circallStreamKey.getText().toString());
            startActivity(intent);
        } else {
            EasyPermissions.requestPermissions(this, getString(R.string.circall_need_permission),
                    SINGLE_VIDEO_CALL_PERMISSIONS_REQUEST, SINGLE_VIDEO_CALL_PERMISSIONS);
        }
    }
}
