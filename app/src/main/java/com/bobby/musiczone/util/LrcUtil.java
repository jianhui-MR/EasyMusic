package com.bobby.musiczone.util;

import android.text.TextUtils;
import android.text.format.DateUtils;

import com.bobby.musiczone.entry.Lrc;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LrcUtil {
    public static List<Lrc> ParseLrc(String lrcContent) {
        if (TextUtils.isEmpty(lrcContent))
            return null;
        List<Lrc> LrcList = new ArrayList<>();
        String[] array = lrcContent.split("\\n");
        for (String line : array) {
            Lrc lrc = ParseLine(line);
            if (lrc!=null)
                LrcList.add(lrc);
        }
        return LrcList;
    }

    private static Lrc ParseLine(String line) {
        Lrc lrc = new Lrc();
        Matcher lineMatcher = Pattern.compile("(\\d\\d:\\d\\d.\\d+)(\\])(.+)").matcher(line);
        if (lineMatcher.find()) {
            lrc.text = lineMatcher.group(3);
            String time = lineMatcher.group(1);
            Matcher timeMatcher = Pattern.compile("(\\d\\d):(\\d\\d).(\\d\\d)").matcher(time);
            if (timeMatcher.find()) {
                long min = Long.parseLong(timeMatcher.group(1));
                long sec = Long.parseLong(timeMatcher.group(2));
                long mil = Long.parseLong(timeMatcher.group(3));
                long totalTime = min * DateUtils.MINUTE_IN_MILLIS + sec * DateUtils.SECOND_IN_MILLIS + mil * 10;
                lrc.time = totalTime;
                return lrc;
            }
        }
        return null;
    }
}
