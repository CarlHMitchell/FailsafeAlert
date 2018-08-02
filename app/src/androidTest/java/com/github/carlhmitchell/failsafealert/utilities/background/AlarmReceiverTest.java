package com.github.carlhmitchell.failsafealert.utilities.background;

import android.content.Context;

import org.junit.Before;
import org.junit.Test;
import static org.mockito.Mockito.*;


public class AlarmReceiverTest {
    private AlarmReceiver mReceiver;
    private Context mContext;

    @Before
    public void setUp() throws Exception {
        System.setProperty("dexmaker.dexcache", mContext.getCacheDir().toString());
        mReceiver = new AlarmReceiver();
        mContext = mock(Context.class);
    }


    @Test
    public void onReceive() {
    }
}