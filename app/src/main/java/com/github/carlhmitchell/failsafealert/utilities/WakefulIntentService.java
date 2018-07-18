package com.github.carlhmitchell.failsafealert.utilities;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.util.Log;


/**
 * An {@link IntentService} that acquires a partial WakeLock, to allow the BackgroundService
 * to keep the CPU alive until its work is done.
 * <p>
 * By inheriting this the BackgroundService can do its work and safely exit while holding a WakeLock
 * by calling onHandleIntent(Intent).
 */
public class WakefulIntentService extends IntentService {
    private static final String LOCK_NAME_STATIC = "com.example.sai.passivewarningalarm.BackgroundService.Static";
    private static final String LOCK_NAME_LOCAL = "com.example.sai.passivewarningalarm.BackgroundService.Local";
    private static PowerManager.WakeLock lockStatic = null;
    private PowerManager.WakeLock lockLocal = null;

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
        getLock(context).acquire();
        Log.d("WakefulIntentService", "Static Lock acquired");
    }

    synchronized private static PowerManager.WakeLock getLock(Context context) {
        if (lockStatic == null) {
            PowerManager mgr = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            lockStatic = mgr.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, LOCK_NAME_STATIC);
            lockStatic.setReferenceCounted(true);
        }
        return (lockStatic);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        PowerManager mgr = (PowerManager) getSystemService(Context.POWER_SERVICE);
        lockLocal = mgr.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, LOCK_NAME_LOCAL);
        lockLocal.setReferenceCounted(true);
    }

    @Override
    public void onStart(Intent intent, final int startID) {
        lockLocal.acquire();
        super.onStart(intent, startID);
        try {
            if (lockLocal.isHeld()) {
                lockLocal.release();
            }
        } catch (Exception e) {
            Log.e("WakefulIntentService", "onStart got exception " + e);
        }
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        try {
            if (lockLocal.isHeld()) {
                lockLocal.release();
            }
        } catch (Exception e) {
            Log.e("WakefulIntentService", "onHandleIntent got exception " + e);
        }
    }

}
