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

package xyz.rc24.bot.commands.tools;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.api.Permission;
import xyz.rc24.bot.commands.Categories;
import xyz.rc24.bot.core.entities.GuildSettings;
import xyz.rc24.bot.database.GuildSettingsDataManager;

/**
 * @author Artuto
 */

public class PrefixCmd extends Command
{
    private final GuildSettingsDataManager dataManager;

    public PrefixCmd(GuildSettingsDataManager dataManager)
    {
        this.dataManager = dataManager;
        this.name = "prefix";
        this.help = "Set and display the bot's prefixes.";
        this.userPermissions = new Permission[]{Permission.MANAGE_SERVER};
        this.category = Categories.TOOLS;
    }

    @Override
    protected void execute(CommandEvent event)
    {
        GuildSettings gs = event.getClient().getSettingsFor(event.getGuild());
        String args = event.getArgs();

        if(args.isEmpty())
        {
            event.reply("ℹ The prefix in this server is: " + gs.getPrefix());
            return;
        }

        if(args.length() > 5)
        {
            event.replyError("The prefix length may not be longer than 5 characters!");
            return;
        }

        if(args.equals("none"))
        {
            if(dataManager.setPrefix(event.getGuild().getIdLong(), null))
                event.replySuccess("Successfully disabled the custom prefix!");
            else
                event.replyError("Error whilst disabling the custom prefix! Please contact a developer.");

            return;
        }

        if(gs.getPrefix().equals(args))
        {
            event.replyError("This prefix is already in use!");
            return;
        }

        if(dataManager.setPrefix(event.getGuild().getIdLong(), args))
            event.replySuccess("Successfully set `" + args + "` as the prefix!");
        else
            event.replyError("Error whilst setting the prefix! Please contact a developer.");
    }
}
