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

package xyz.rc24.bot.commands.general;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.api.entities.Member;
import xyz.rc24.bot.Bot;
import xyz.rc24.bot.commands.Categories;
import xyz.rc24.bot.utils.SearcherUtil;

/**
 * @author Artuto
 */

public class BirthdayCmd extends Command
{
    private final Bot bot;

    public BirthdayCmd(Bot bot)
    {
        this.bot = bot;
        this.name = "birthday";
        this.help = "View the birthday of you/someone else.";
        this.category = Categories.GENERAL;
    }

    @Override
    protected void execute(CommandEvent event)
    {
        event.getChannel().sendTyping().queue();
        event.async(() ->
        {
            Member target = SearcherUtil.findMember(event, event.getArgs());
            if(target == null)
                return;

            String date = bot.getBirthdayDataManager().getBirthday(target.getUser().getIdLong());

            if(date == null)
            {
                if(target.equals(event.getMember()))
                {
                    event.replyError("You haven't have set your birthday!" +
                            " Set it using  `" + bot.getPrefix(event.getGuild()) + "setbirthday`!");
                }
                else
                    event.replyError("**" + target.getEffectiveName() + "** has not set their birthday!");

                return;
            }

            if(target.equals(event.getMember()))
                event.reply("<a:birthdaycake:576200303662071808> Your birthday is set to **" + date + "**");
            else
                event.reply("<a:birthdaycake:576200303662071808> **" + target.getEffectiveName() + "**'s birthday is set to **" + date + "**");
        });
    }
}
