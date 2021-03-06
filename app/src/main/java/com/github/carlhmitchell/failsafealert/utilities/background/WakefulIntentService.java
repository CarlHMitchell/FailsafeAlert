package com.github.carlhmitchell.failsafealert.utilities.background;

//Model?

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;

import com.github.carlhmitchell.failsafealert.utilities.SDLog;

import java.util.Objects;

import static com.github.carlhmitchell.failsafealert.utilities.AppConstants.WAKELOCK_TIMEOUT;


/**
 * An {@link IntentService} that acquires a partial WakeLock, to allow the BackgroundService
 * to keep the CPU alive until its work is done.
 * <p>
 * By inheriting this the BackgroundService can do its work and safely exit while holding a WakeLock
 * by calling onHandleIntent(Intent).
 */
public class WakefulIntentService extends IntentService {
    private static final String LOCK_NAME_STATIC = "com.github.carlhmitchell.failsafealert.BackgroundService.Static";
    private static final String LOCK_NAME_LOCAL = "com.github.carlhmitchell.failsafealert.BackgroundService.Local";
    private static PowerManager.WakeLock lockStatic = null;
    private PowerManager.WakeLock lockLocal = null;

    @SuppressWarnings("unused") // This default constructor is needed by the Manifest to start the
                                //  service. It's not used elsewhere, which produces a warning.
    public WakefulIntentService() {
        super("WakefulIntentService");
    }

    public WakefulIntentService(String name) {
        super(name);
    }

    /**
     * Acquire a partial static WakeLock, you must call this within the class that
     * calls startService()
     *
     * @param context context to acquire lock for.
     */
    public static void acquireStaticLock(Context context) {
        getLock(context).acquire(WAKELOCK_TIMEOUT /*10 minutes*/);
        SDLog.d("WakefulIntentService", "Static Lock acquired");
    }

    synchronized private static PowerManager.WakeLock getLock(Context context) {
        if (lockStatic == null) {
            PowerManager mgr = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            lockStatic = Objects.requireNonNull(mgr).newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, LOCK_NAME_STATIC);
            lockStatic.setReferenceCounted(true);
        }
        return (lockStatic);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        PowerManager mgr = (PowerManager) getSystemService(Context.POWER_SERVICE);
        lockLocal = Objects.requireNonNull(mgr).newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, LOCK_NAME_LOCAL);
        lockLocal.setReferenceCounted(true);
    }

    @Override
    public void onStart(Intent intent, final int startID) {
        lockLocal.acquire(24*60*60*1000L /*1 day*/);
        super.onStart(intent, startID);
        try {
            if (lockLocal.isHeld()) {
                lockLocal.release();
            }
        } catch (Exception e) {
            SDLog.e("WakefulIntentService", "onStart got exception " + e);
        }
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        try {
            if (lockLocal.isHeld()) {
                lockLocal.release();
            }
        } catch (Exception e) {
            SDLog.e("WakefulIntentService", "onHandleIntent got exception " + e);
        }
    }

}
