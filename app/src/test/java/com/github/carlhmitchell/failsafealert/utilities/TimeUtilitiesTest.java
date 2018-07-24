package com.github.carlhmitchell.failsafealert.utilities;

import org.junit.Test;

import static org.junit.Assert.*;

public class TimeUtilitiesTest {
    private String time = "00:00";

    @Test
    public void getHour() {
        assert 0 == TimeUtilities.getHour(time);
    }

    @Test
    public void getMinute() {
        assert 0 == TimeUtilities.getMinute(time);
    }
}