package com.github.carlhmitchell.failsafealert.settings.preferences;

import android.content.Context;
import android.content.res.TypedArray;
import android.preference.DialogPreference;
import android.text.format.DateFormat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.TimePicker;

import com.github.carlhmitchell.failsafealert.utilities.ScheduleAlarms;
import com.github.carlhmitchell.failsafealert.utilities.TimeUtilities;

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
        //picker.setCurrentHour(Calendar.getInstance().get(Calendar.HOUR_OF_DAY));
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

            String time = String.valueOf(lastHour) + ":" + String.valueOf(lastMinute);
            Log.d("TimePreference", "Time set to: " + time);

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