package com.github.carlhmitchell.failsafealert.utilities;

import org.junit.Assert;
import org.junit.Test;

public class TimeUtilitiesTest {
    private String time = "00:00";

    @Test
    public void getHour() {
        Assert.assertEquals(0, TimeUtilities.getHour(time));
    }

    @Test
    public void getMinute() {
        Assert.assertEquals(0, TimeUtilities.getMinute(time));
    }
}