package com.github.carlhmitchell.failsafealert.settings.preferences;

//ViewModel

import android.content.Context;
import android.content.res.TypedArray;
import android.preference.DialogPreference;
import android.text.format.DateFormat;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TimePicker;
import android.widget.Toast;

import com.github.carlhmitchell.failsafealert.utilities.SDLog;
import com.github.carlhmitchell.failsafealert.utilities.ScheduleAlarms;
import com.github.carlhmitchell.failsafealert.utilities.TimeUtilities;
import com.github.carlhmitchell.failsafealert.utilities.ToastHelper;

public class TimePreference extends DialogPreference {
    private final Context context;
    private int lastHour = 0;
    private int lastMinute = 0;
    private TimePicker picker = null;

    public TimePreference(Context context, AttributeSet attrs) {
        super(context, attrs);

        this.context = context;

        setPositiveButtonText("Set");
        setNegativeButtonText("Cancel");
    }

    @Override
    protected View onCreateDialogView() {
        picker = new TimePicker(getContext());
        picker.setIs24HourView(DateFormat.is24HourFormat(getContext()));
        return (picker);
    }

    @Override
    protected void onBindDialogView(View v) {
        super.onBindDialogView(v);

        picker.setCurrentHour(lastHour);
        picker.setCurrentMinute(lastMinute);
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);

        if (positiveResult) {
            lastHour = picker.getCurrentHour();
            lastMinute = picker.getCurrentMinute();

            String time = TimeUtilities.formatTimeFromIntegers(lastHour, lastMinute);
            SDLog.d("TimePreference", "Time set to: " + time);
            ToastHelper.toast(context, "Time set to: " + time, Toast.LENGTH_SHORT);

            if (callChangeListener(time)) {
                persistString(time);
            }

            ScheduleAlarms.run(context);
        }
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return (a.getString(index));
    }

    @Override
    protected void onSetInitialValue(boolean restoreValue, Object defaultValue) {
        String time;

        if (restoreValue) {
            if (defaultValue == null) {
                time = getPersistedString("00:00");
            } else {
                time = getPersistedString(defaultValue.toString());
            }
        } else {
            time = defaultValue.toString();
        }

        lastHour = TimeUtilities.getHour(time);
        lastMinute = TimeUtilities.getMinute(time);
    }
}