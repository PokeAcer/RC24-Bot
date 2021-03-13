/*
 * MIT License
 *
 * Copyright (c) 2017-2021 RiiConnect24 and its contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package xyz.rc24.bot.utils;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;

import java.util.List;
import java.util.Map;

/**
 * @author Artuto
 */

public class FormatUtil
{
    public static String listOfMembers(List<Member> list, String query)
    {
        StringBuilder out = new StringBuilder("Multiple members found matching \"" + query + "\":");
        for(int i = 0; i < 6 && i < list.size(); i++)
        {
            out.append("\n - ").append(list.get(i).getUser().getName()).append("#")
                    .append(list.get(i).getUser().getDiscriminator()).append(" (ID:")
                    .append(list.get(i).getUser().getId()).append(")");
        }

        if(list.size() > 6)
            out.append("\n**And ").append(list.size() - 6).append(" more...**");

        return out.toString();
    }

    public static String listOfTcChannels(List<TextChannel> list, String query)
    {
        StringBuilder out = new StringBuilder("Multiple text channels found matching \"" + query + "\":");
        for(int i = 0; i < 6 && i < list.size(); i++)
        {
            out.append("\n - ").append(list.get(i).getName()).append(" (ID:")
                    .append(list.get(i).getId()).append(")");
        }

        if(list.size() > 6)
            out.append("\n**And ").append(list.size() - 6).append(" more...**");

        return out.toString();
    }

    public static String listOfUsers(List<User> list, String query)
    {
        StringBuilder out = new StringBuilder("Multiple users found matching \"" + query + "\":");
        for(int i = 0; i < 6 && i < list.size(); i++)
        {
            out.append("\n - ").append(list.get(i).getName()).append("#")
                    .append(list.get(i).getDiscriminator()).append(" (ID:")
                    .append(list.get(i).getId()).append(")");
        }

        if(list.size() > 6)
            out.append("\n**And ").append(list.size() - 6).append(" more...**");

        return out.toString();
    }

    /*public static String getCodeTypes()
    {
        StringBuilder response = new StringBuilder("Invalid type! Valid types:\n");
        for(CodeType type : CodeType.values())
        {
            if(type == CodeType.UNKNOWN)
                continue;

            response.append("`").append(type.getName()).append("`, ");
        }

        // Remove leftover comma + space
        return response.substring(0, response.length() - 2);
    }*/

    public static String getCodeLayout(Map<String, String> codes)
    {
        // Create a human-readable format of the user's Wii wii.
        StringBuilder codesString = new StringBuilder();
        for(Map.Entry<String, String> code : codes.entrySet())
            codesString.append("`").append(code.getKey()).append("`:\n").append(code.getValue()).append("\n");

        return codesString.toString();
    }

    public static String getCodeLayout(String name, String code)
    {
        return "`" + name + "`:\n" + code;
    }

    public static String sanitize(String msg)
    {
        return msg.replace("@everyone", "@\u0435veryone")
                .replace("@here", "@h\u0435re");
    }

    public static String secondsToTime(long timeseconds)
    {
        StringBuilder builder = new StringBuilder();

        int years = (int) (timeseconds / (60 * 60 * 24 * 365));
        if(years > 0)
        {
            builder.append("**").append(years).append("** years, ");
            timeseconds = timeseconds % (60 * 60 * 24 * 365);
        }

        int weeks = (int) (timeseconds / (60 * 60 * 24 * 365));
        if(weeks > 0)
        {
            builder.append("**").append(weeks).append("** weeks, ");
            timeseconds = timeseconds % (60 * 60 * 24 * 7);
        }

        int days = (int) (timeseconds / (60 * 60 * 24));
        if(days > 0)
        {
            builder.append("**").append(days).append("** days, ");
            timeseconds = timeseconds % (60 * 60 * 24);
        }

        int hours = (int) (timeseconds / (60 * 60));
        if(hours > 0)
        {
            builder.append("**").append(hours).append("** hours, ");
            timeseconds = timeseconds % (60 * 60);
        }

        int minutes = (int) (timeseconds / (60));
        if(minutes > 0)
        {
            builder.append("**").append(minutes).append("** minutes, ");
            timeseconds = timeseconds % (60);
        }

        if(timeseconds > 0)
            builder.append("**").append(timeseconds).append("** seconds");

        String str = builder.toString();
        if(str.endsWith(", "))
            str = str.substring(0, str.length() - 2);
        if(str.equals(""))
            str = "**No time**";

        return str;
    }
}
