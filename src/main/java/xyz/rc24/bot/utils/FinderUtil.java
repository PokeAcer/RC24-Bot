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

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;

import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static net.dv8tion.jda.api.entities.Message.MentionType.USER;

public class FinderUtil
{
    private static final Pattern ID = Pattern.compile("(\\d+)");

    public static List<Member> findMembers(String args, Guild guild)
    {
        // First try with an ID
        long id = parseId(ID, args);
        if(!(id == 0))
            return retrieveMember(id, guild);

        // Then go for a mention
        id = parseId(USER.getPattern(), args);
        if(!(id == 0))
            return retrieveMember(id, guild);

        // Then just search for the name
        return retrieveMembers(args, guild);
    }

    private static List<Member> retrieveMember(long id, Guild guild)
    {
        try {return Collections.singletonList(guild.retrieveMemberById(id).complete());}
        catch(ErrorResponseException ignored) {return emptyList();}
    }

    private static List<Member> retrieveMembers(String name, Guild guild)
    {
        List<Member> members = guild.retrieveMembersByPrefix(removeDiscriminator(name), 6).get();
        String discriminator = splitDiscriminator(name);

        if(!(discriminator == null))
        {
            for(Member member : members)
            {
                if(member.getUser().getDiscriminator().equals(discriminator))
                    return singletonList(member);
            }
        }

        return members;
    }

    private static long parseId(Pattern pattern, String arg)
    {
        Matcher matcher = pattern.matcher(arg);

        if(matcher.matches())
            return Long.parseLong(matcher.group(1));

        return 0;
    }

    private static String removeDiscriminator(String name)
    {
        int index = name.indexOf("#");

        if(!(index == -1))
            return name.substring(0, index - 1);

        return name;
    }

    private static String splitDiscriminator(String name)
    {
        String discriminator = null;
        int index = name.indexOf("#");

        if(!(index == -1))
            discriminator = name.substring(index + 1);

        return discriminator;
    }
}
