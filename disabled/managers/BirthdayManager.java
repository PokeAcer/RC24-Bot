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

package xyz.rc24.bot.managers;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import org.slf4j.Logger;
import xyz.rc24.bot.RiiConnect24Bot;
import xyz.rc24.bot.database.BirthdayDataManager;

import java.awt.Color;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.List;

import static xyz.rc24.bot.Const.ERROR_E;

/**
 * Manager for birthdays.
 *
 * @author Artuto
 */

public class BirthdayManager
{
    private final BirthdayDataManager dataManager;
    private final Logger logger = RiiConnect24Bot.getLogger(BirthdayManager.class);
    private final EmbedBuilder baseEmbed = new EmbedBuilder()
            .setTitle("Happy birthday! \uD83C\uDF82")
            .setDescription("Please send them messages wishing them a happy birthday here on" +
                    " Discord and/or birthday mail on their Wii if you've registered them!")
            .setColor(Color.decode("#00a6e9"));

    public BirthdayManager(BirthdayDataManager dataManager)
    {
        this.dataManager = dataManager;
    }

    public void updateBirthdays(JDA jda, long birthdayChannelId, long ownerId)
    {
        TextChannel tc = jda.getTextChannelById(birthdayChannelId);
        if(tc == null || !(tc.canTalk()))
            return;

        LocalDate date = OffsetDateTime.now().atZoneSameInstant(ZoneId.of("UTC-6")).toLocalDate();
        String today = date.getDayOfMonth() + "/" + date.getMonthValue();

        List<Long> ids = dataManager.getPeopleWithDate(today);
        tc.getGuild().retrieveMembersByIds(ids).onSuccess(members ->
        {
            for(Member member : members)
            {
                baseEmbed.setAuthor("It's " + member.getEffectiveName() + "'s birthday!",
                        "https://rc24.xyz/", member.getUser().getEffectiveAvatarUrl());

                tc.sendMessage(baseEmbed.build()).queue();
            }
        }).onError(error ->
        {
            tc.sendMessage(ERROR_E + " Could not retrieve members from Discord, <@" + ownerId + ">").queue();
            logger.error("Could not retrieve members from with Discord with IDs {}:", ids, error);
        });
    }
}
