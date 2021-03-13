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

package xyz.rc24.bot.commands.wii;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.api.entities.Member;
import xyz.rc24.bot.Bot;
import xyz.rc24.bot.commands.Categories;
import xyz.rc24.bot.core.BotCore;
import xyz.rc24.bot.core.entities.CodeType;
import xyz.rc24.bot.core.entities.GuildSettings;
import xyz.rc24.bot.utils.FormatUtil;
import xyz.rc24.bot.utils.SearcherUtil;

import java.util.Map;

/**
 * @author Artuto
 */

public class AddCmd extends Command
{
    private final BotCore core;

    public AddCmd(Bot bot)
    {
        this.core = bot.getCore();
        this.name = "add";
        this.help = "Sends your friend code to another user.";
        this.category = Categories.WII;
    }

    @Override
    protected void execute(CommandEvent event)
    {
        event.async(() ->
        {
            Member member = SearcherUtil.findMember(event, event.getArgs());
            if(member == null)
                return;

            GuildSettings gs = event.getClient().getSettingsFor(event.getGuild());
            CodeType defaultAddType = gs.getDefaultAddType();

            if(member.equals(event.getMember()))
            {
                event.replyError("You can't add yourself!");
                return;
            }
            if(member.getUser().isBot())
            {
                event.replyError("You can't add bots!");
                return;
            }

            Map<String, String> authorTypeCodes = core.getCodesForType(defaultAddType, event.getAuthor().getIdLong());
            if(authorTypeCodes.isEmpty())
            {
                event.replyError("**" + event.getMember().getEffectiveName() + "** has not added any friend codes!");
                return;
            }

            Map<String, String> targetTypeCodes = core.getCodesForType(defaultAddType, member.getUser().getIdLong());
            if(targetTypeCodes.isEmpty())
            {
                event.replyError("**" + member.getEffectiveName() + "** has not added any friend codes!");
                return;
            }

            // Send target's code to author
            event.replyInDm(getAddMessageHeader(defaultAddType, member, true) +
                            "\n\n" + FormatUtil.getCodeLayout(targetTypeCodes), (success) -> event.reactSuccess(),
                    (failure) -> event.replyError("Hey, " + event.getAuthor().getAsMention() +
                            ": I couldn't DM you. Make sure your DMs are enabled."));

            // Send author's code to target
            member.getUser().openPrivateChannel().queue(pc ->
                    pc.sendMessage(getAddMessageHeader(defaultAddType, event.getMember(),
                            false) + "\n\n" + FormatUtil.getCodeLayout(authorTypeCodes))
                            .queue(null, (failure) -> event.replyError("Hey, " + member.getAsMention() +
                            ": I couldn't DM you. Make sure your DMs are enabled.")));
        });
    }

    private String getAddMessageHeader(CodeType type, Member member, boolean isCommandRunner)
    {
        if(!(isCommandRunner))
            return "**" + member.getEffectiveName() + "** has requested to add your " + type.getDisplayName() + " friend code(s)!";
        else
            return "You have requested to add **" + member.getEffectiveName() + "**'s " + type.getDisplayName() + " friend code(s).";
    }
}
