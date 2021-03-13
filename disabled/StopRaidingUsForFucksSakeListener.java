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

package xyz.rc24.bot.listeners;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

/**
 * Seriously, stop.
 *
 * @author Artuto
 */

public class StopRaidingUsForFucksSakeListener extends ListenerAdapter
{
    private boolean enabled;
    private final List<Long> allowed = new ArrayList<>();

    public StopRaidingUsForFucksSakeListener(boolean enabled)
    {
        this.enabled = enabled;
    }

    @Override
    public void onGuildMemberJoin(GuildMemberJoinEvent event)
    {
        Guild guild = event.getGuild();
        Member member = event.getMember();

        if(!(guild.getIdLong() == 206934458954153984L) || !(enabled) || allowed.contains(member.getUser().getIdLong()))
        {
            // Nothing to see here guys
            return;
        }

        OffsetDateTime now = OffsetDateTime.now();
        long creation = member.getUser().getTimeCreated().until(now, ChronoUnit.SECONDS);

        if(creation > 3600)
            return;

        try
        {
            String dm = "Hello,\n\n" +
                    "For security reasons, you've been kicked from RiiConnect24. " +
                    "If you are a legit user, please contact one of our admins.\n\n" +
                    "Cheers,\n" +
                    "RiiConnect24 Staff";

            member.getUser().openPrivateChannel().queue(pc -> pc.sendMessage(dm)
                    .queue(s -> ban(member), e -> ban(member)));
        }
        catch(Exception ignored) {}
    }

    private void ban(Member member)
    {
        // We set double reason because one is AuditLog reason and other ban reason (really dumb #Discord)
        member.ban(1, "[AutoBan]").reason("[AutoBan]").queue(null, e -> {});

        allowed.add(member.getUser().getIdLong());
    }
}
