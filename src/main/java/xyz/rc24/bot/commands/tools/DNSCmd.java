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
import xyz.rc24.bot.commands.Categories;
import xyz.rc24.bot.commands.RegistrableCommand;

/**
 * @author Spotlight
 */
@RegistrableCommand
public class DNSCmd extends Command
{
    public DNSCmd()
    {
        this.name = "dns";
        this.help = "Lets you know the current DNS settings.";
        this.category = Categories.TOOLS;
        this.guildOnly = false;
    }

    @Override
    protected void execute(CommandEvent event)
    {
        event.reply("`164.132.44.106` should be your primary DNS.\n" +
                "`8.8.8.8` (Google DNS) can be your secondary DNS. " +
                "(Or `1.1.1.1` (Cloudflare DNS), alternatively)\n" +
                "Note that if your ISP blocks the use of custom DNS, you can use a local DNS server like ours: " +
                "<https://github.com/RiiConnect24/DNS-Server>");
    }
}
