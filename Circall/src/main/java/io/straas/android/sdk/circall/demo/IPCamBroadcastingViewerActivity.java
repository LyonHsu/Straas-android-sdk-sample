package io.straas.android.sdk.circall.demo;

import android.databinding.ViewDataBinding;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.ActionMenuView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;

import io.straas.android.sdk.circall.CircallManager;
import io.straas.android.sdk.circall.CircallPlayerView;
import io.straas.android.sdk.circall.CircallToken;
import io.straas.android.sdk.demo.R;
import io.straas.android.sdk.demo.databinding.ActivityIpcamBroadcastingBinding;

public class IPCamBroadcastingViewerActivity extends CircallDemoBaseActivity {

    private static final String TAG = IPCamBroadcastingViewerActivity.class.getSimpleName();

    private ActivityIpcamBroadcastingBinding mBinding;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        CircallManager.initialize().continueWithTask(task -> {
            if (!task.isSuccessful()) {
                Log.e(getTag(), "init fail: " + task.getException());
                finish();
                return Tasks.forException(new RuntimeException());
            }

            mCircallManager = task.getResult();
            mCircallManager.addEventListener(this);
            return prepare();
        }).addOnSuccessListener(circallStream -> {
            String token = getIntent().getStringExtra(INTENT_CIRCALL_TOKEN);
            if (!TextUtils.isEmpty(token)) {
                join(new CircallToken(token));
            } else {
                Toast.makeText(getApplicationContext(), "Start circall fails due to empty token",
                        Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    //=====================================================================
    // Abstract methods
    //=====================================================================
    @Override
    protected String getTag() {
        return TAG;
    }

    @Override
    protected int getContentViewLayoutId() {
        return R.layout.activity_ipcam_broadcasting;
    }

    @Override
    protected void setBinding(ViewDataBinding binding) {
        mBinding = (ActivityIpcamBroadcastingBinding) binding;
    }

    @Override
    protected void scaleScreenShotView(float scale) {
        mBinding.screenshot.setScaleX(scale);
        mBinding.screenshot.setScaleY(scale);
    }

    @Override
    protected void setScreenShotView(Bitmap bitmap) {
        mBinding.screenshot.setImageBitmap(bitmap);
    }

    @Override
    protected ActionMenuView getActionMenuView() {
        return mBinding.actionMenuView;
    }

    @Override
    protected Toolbar getToolbar() {
        return mBinding.toolbar;
    }

    @Override
    protected CircallPlayerView getRemoteStreamView() {
        return mBinding.fullscreenVideoView;
    }

    @Override
    protected void setShowActionButtons(boolean show) {
        mBinding.setShowActionButtons(show);
    }

    @Override
    public void onShowActionButtonsToggled(View view) {
        mBinding.setShowActionButtons(!mBinding.getShowActionButtons());
    }

    //=====================================================================
    // Optional implementation
    //=====================================================================
    @Override
    protected void setState(int state) {
        super.setState(state);
        mBinding.setState(state);
    }

    private Task<Void> prepare() {
        if (mCircallManager != null && mCircallManager.getCircallState() == CircallManager.STATE_IDLE) {
            return mCircallManager.prepareForUrl(getApplicationContext())
                    .addOnFailureListener(this, e -> Log.e(getTag(), "Prepare fails " + e));
        }
        return Tasks.forException(new IllegalStateException());
    }

    @Override
    public void onDestroy() {
        destroyCircallManager();

        super.onDestroy();
    }

    private void join(CircallToken token) {
        setState(STATE_CONNECTING);
        mCircallManager.connect(token).addOnSuccessListener(aVoid -> {
            setState(STATE_CONNECTED);
        }).addOnFailureListener(e -> {
            Log.e(getTag(), "join fails: " + e);
            Toast.makeText(getApplicationContext(), "join fails",
                    Toast.LENGTH_SHORT).show();
            finish();
        });
    }

    @Override
    public void onBackPressed() {
        if (mBinding.getState() == STATE_CONNECTED || mBinding.getState() == STATE_SUBSCRIBED) {
            showEndCircallConfirmationDialog();
        } else {
            super.onBackPressed();
        }
    }

    private void showEndCircallConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.CircallDialogTheme);
        builder.setTitle(R.string.end_circall_confirmation_title);
        builder.setMessage(R.string.end_circall_confirmation_message);
        builder.setPositiveButton(android.R.string.ok, (dialog, which) -> {
            finish();
        });
        builder.setNegativeButton(android.R.string.cancel, null);
        final AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void destroyCircallManager() {
        if (mCircallManager == null) {
            return;
        }

        unsubscribe().addOnCompleteListener(task -> {
            mCircallManager.destroy();
            mCircallManager = null;
        });
    }

    private Task<Void> unsubscribe() {
        return (mBinding.getState() == STATE_SUBSCRIBED && mRemoteCircallStream != null)
                ? mCircallManager.unsubscribe(mRemoteCircallStream)
                : Tasks.forException(new IllegalStateException());
    }
}
