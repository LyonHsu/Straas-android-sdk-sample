package io.straas.android.sdk.streaming.demo.camera;

import android.content.*;
import android.os.*;
import android.support.annotation.*;
import android.support.v7.app.*;
import android.text.*;
import android.view.*;
import android.widget.*;

import io.straas.android.sdk.demo.common.widget.*;
import io.straas.android.sdk.streaming.demo.*;

public class ConfigSettingActivity extends AppCompatActivity {

    public static final String EXTRA_MAX_BITRATE = "EXTRA_MAX_BITRATE";

    RecordTextInputEditText mEditTextMaxBitrate;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_custom_config);

        mEditTextMaxBitrate = findViewById(R.id.streaming_max_bitrate);
    }

    public void startActivity(View view) {
        Intent intent = new Intent();
        try {
            String strMaxBitrate = mEditTextMaxBitrate.getText().toString();
            if (!TextUtils.isEmpty(strMaxBitrate)) {
                intent.putExtra(EXTRA_MAX_BITRATE, Integer.parseInt(strMaxBitrate));
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "max bitrate should be a number", Toast.LENGTH_LONG).show();
            return;
        }

        int id = view.getId();
        if (id == R.id.btn_start_camera) {
            intent.setClass(ConfigSettingActivity.this, CameraCustomConfigActivity.class);
        } else {
            return;
        }

        startActivity(intent);
    }
}
