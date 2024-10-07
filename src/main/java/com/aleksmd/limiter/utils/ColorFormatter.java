package com.aleksmd.limiter.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.md_5.bungee.api.ChatColor;

/**
 * Utility class for formatting color codes in strings.
 */
public class ColorFormatter {

    /**
     * Converts color codes from the format &#RRGGBB to ChatColor format.
     * @param inputText The input string containing the color codes.
     * @return A string with color codes converted to ChatColor format.
     */
    public static String formatColors(String inputText) {
        if (inputText == null) {
            return "";
        }

        Pattern hexPattern = Pattern.compile("&#([A-Fa-f0-9]{6})");
        Matcher matcher = hexPattern.matcher(inputText);
        StringBuffer formattedText = new StringBuffer();

        while (matcher.find()) {
            String hexCode = matcher.group(1);
            String colorCode = ChatColor.of("#" + hexCode).toString();
            matcher.appendReplacement(formattedText, colorCode);
        }

        matcher.appendTail(formattedText);
        return ChatColor.translateAlternateColorCodes('&', formattedText.toString());
    }
}
