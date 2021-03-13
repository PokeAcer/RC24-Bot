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
import xyz.rc24.bot.Bot;
import xyz.rc24.bot.commands.Categories;
import xyz.rc24.bot.core.entities.Flag;

public class FlagCmd extends Command
{
    private final Bot bot;

    public FlagCmd(Bot bot)
    {
        this.bot = bot;
        this.name = "flag";
        this.help = "Sets your flag to show in your code lookup.";
        this.aliases = new String[]{"setflag", "setcountry", "country"};
        this.category = Categories.GENERAL;
        this.guildOnly = false;
    }

    @Override
    protected void execute(CommandEvent event)
    {
        long id = event.getAuthor().getIdLong();
        String args = event.getArgs();

        if(args.isEmpty())
        {
            event.replyError("You must provide a country!");
            return;
        }

        Flag flag = Flag.fromName(args);
        if(flag == Flag.UNKNOWN)
        {
            event.replyError("Unknown country!");
            return;
        }

        boolean success = bot.getCodeDataManager().setFlag(id, flag.getEmote());

        if(success)
            event.replySuccess("Updated successfully!");
        else
            event.replyError("Error whilst updating your flag! Please contact a developer.");
    }
}
