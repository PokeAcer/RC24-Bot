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
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import com.jagrosh.jdautilities.menu.ButtonMenu;
import com.jagrosh.jdautilities.menu.Paginator;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.exceptions.PermissionException;
import xyz.rc24.bot.Bot;
import xyz.rc24.bot.Const;
import xyz.rc24.bot.commands.Categories;
import xyz.rc24.bot.core.entities.CodeType;
import xyz.rc24.bot.core.entities.GuildSettings;
import xyz.rc24.bot.database.CodeDataManager;
import xyz.rc24.bot.utils.FormatUtil;
import xyz.rc24.bot.utils.SearcherUtil;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Artuto
 */

public class CodeCmd extends Command
{
    private final Bot bot;
    private final CodeDataManager dataManager;
    private final EventWaiter waiter;

    private final Pattern FULL_PATTERN = Pattern.compile("(\\w+)\\s+(.+?)\\s+((?:\\d{4}|SW)[-\\s]\\d{4}[-\\s]\\d{4}(?:[-\\s]\\d{4})?|\\w+)$", Pattern.MULTILINE); // thanks Dismissed
    private final Pattern REMOVE_PATTERN = Pattern.compile("(\\w+)\\s+(.+?)$", Pattern.MULTILINE);

    public CodeCmd(Bot bot)
    {
        this.bot = bot;
        this.dataManager = bot.getCodeDataManager();
        this.waiter = bot.waiter;
        this.name = "code";
        this.help = "Manages friend codes for the user.";
        this.children = new Command[]{new AddCmd(), new EditCmd(), new HelpCmd(), new LookupCmd(), new RemoveCmd()};
        this.category = Categories.WII;
        this.guildOnly = false;
    }

    @Override
    protected void execute(CommandEvent event)
    {
        event.replyError("Please enter a valid option for the command.\n" +
                "Valid subcommands: `add`, `edit`, `remove`, `lookup`, `help`.");
    }

    private class AddCmd extends Command
    {
        AddCmd()
        {
            this.name = "add";
            this.help = "Adds a code.";
            this.category = Categories.WII;
        }

        @Override
        protected void execute(CommandEvent event)
        {
            GuildSettings gs = event.getClient().getSettingsFor(event.getGuild());
            List<String> args = parseArgs(FULL_PATTERN, event.getArgs());
            if(args.size() < 3)
            {
                event.replyError("Wrong format! Correct one is `" + bot.getPrefix(event.getGuild()) +
                        "code add <type> <name> <code>`");
                return;
            }

            CodeType type = CodeType.fromCode(args.get(0));
            if(type == CodeType.UNKNOWN)
            {
                event.replyError(FormatUtil.getCodeTypes());
                return;
            }

            Map<String, String> codeTypes = bot.getCore().getCodesForType(type, event.getAuthor().getIdLong());
            if(codeTypes.containsKey(args.get(1)))
            {
                event.replyWarning("You already added this code!");
                return;
            }

            if(dataManager.addCode(type, event.getAuthor().getIdLong(), args.get(2), args.get(1)))
                event.replySuccess("Added a code for `" + args.get(1) + "`");
            else
                event.replyError("Error whilst adding a code! Please contact a developer.");
        }
    }

    private class EditCmd extends Command
    {
        EditCmd()
        {
            this.name = "edit";
            this.help = "Edits codes.";
            this.category = Categories.WII;
            this.botPermissions = new Permission[]{Permission.MESSAGE_EMBED_LINKS};
        }

        @Override
        protected void execute(CommandEvent event)
        {
            GuildSettings gs = event.getClient().getSettingsFor(event.getGuild());
            List<String> args = parseArgs(FULL_PATTERN, event.getArgs());
            if(args.size() < 3)
            {
                event.replyError("Wrong format! Correct one is `" + bot.getPrefix(event.getGuild()) +
                        "code edit <type> <name> <code>`");
                return;
            }

            CodeType type = CodeType.fromCode(args.get(0));
            if(type == CodeType.UNKNOWN)
            {
                event.replyError(FormatUtil.getCodeTypes());
                return;
            }

            Map<String, String> codeTypes = bot.getCore().getCodesForType(type, event.getAuthor().getIdLong());
            if(!(codeTypes.containsKey(args.get(1))))
            {
                event.replyWarning("A code for `" + args.get(1) + "` is not registered.");
                return;
            }

            if(dataManager.editCode(type, event.getAuthor().getIdLong(), args.get(2), args.get(1)))
                event.replySuccess("Edited the code for `" + args.get(1) + "`");
            else
                event.replyError("Error whilst editing a code! Please contact a developer.");
        }
    }

    private class HelpCmd extends Command
    {
        HelpCmd()
        {
            this.name = "help";
            this.help = "Shows help regarding codes.";
            this.category = Categories.WII;
			this.guildOnly = false;
        }

        @Override
        protected void execute(CommandEvent event)
        {
			String prefix = event.isFromType(ChannelType.TEXT) ? bot.getPrefix(event.getGuild()) : "!";
			
            String help = "**__Using the bot__**\n\n" + 
			    "**Adding Wii:**\n" + "`" + prefix + "code add wii Wii Name Goes here 1234-5678-9012-3456`\n" + 
			    "**Adding games:**\n `" + prefix + "code add game Game Name 1234-5678-9012`\n" +
		    	"and many more types! Run `" + prefix + "code add` " +
			    "to see all supported code types right now, such as the 3DS, PlayStation 4 and Switch.\n\n" +
			    "**Editing codes**\n" + "`" + prefix + "code edit type Name 1234-5678-9012-3456`\n\n" +
			    "**Removing codes**\n" + "`" + prefix + "code remove type Name`\n\n" + 
			    "**Looking up codes**\n" + "`" + prefix + "code lookup @user`\n\n" +
			    "**Adding a user's Wii**\n" + "`" + prefix + "add @user`\n" + "This will send you their wii, and then DM them your Wii/game wii.";

            event.replyInDm(help, (success) -> event.reactSuccess(), (failure) ->
                    event.replyError("Hey, " + event.getAuthor().getAsMention() +
                            ": I couldn't DM you. Make sure your DMs are enabled."));
        }
    }

    private class LookupCmd extends Command
    {
        private final String BACK = "↩";

        private final Consumer<Message> finalAction = (message) ->
        {
            try
            {
                message.clearReactions().queue();
            }
            catch(PermissionException ignored)
            {
                message.editMessage(Const.WARN_E + "Unable to remove reactions, make sure I have `Manage Message` permissions!").queue();
            }
        };

        LookupCmd()
        {
            this.name = "lookup";
            this.help = "Displays codes for the user.";
            this.category = Categories.WII;
            this.botPermissions = new Permission[]{Permission.MESSAGE_EMBED_LINKS, Permission.MESSAGE_ADD_REACTION};
        }

        @Override
        protected void execute(CommandEvent event)
        {
            event.getChannel().sendTyping().queue();
            event.async(() ->
            {
                Member member = SearcherUtil.findMember(event, event.getArgs());
                if(member == null)
                    return;

                Paginator.Builder codePaginator = new Paginator.Builder()
                        .setEventWaiter(waiter)
                        .showPageNumbers(true)
                        .setTimeout(5, TimeUnit.MINUTES)
                        .setColumns(2)
                        .setFinalAction(finalAction);

                if(!(displayTypeSelector(event, member, null, codePaginator)))
                    return;

                String flag = bot.getCore().getFlag(member.getUser().getIdLong());
                boolean hasFlag = !(flag.isEmpty());

                if(hasFlag)
                    codePaginator.setTitle("Country: " + flag);
            });
        }

        private boolean displayTypeSelector(CommandEvent event, Member member, Message message, Paginator.Builder codePaginator)
        {
            ButtonMenu.Builder typeMenu = new ButtonMenu.Builder()
                    .setEventWaiter(waiter)
                    .setTimeout(5, TimeUnit.MINUTES)
                    .setDescription("Please select a code type:")
                    .setFinalAction(finalAction)
                    .setUsers(member.getUser(), event.getAuthor())
                    .setColor(member.getColor());

            boolean hasCodes = false;
            Map<CodeType, Map<String, String>> userCodes = bot.getCore().getAllCodes(member.getUser().getIdLong());

            for(Map.Entry<CodeType, Map<String, String>> codeType : userCodes.entrySet())
            {
                Map<String, String> codes = codeType.getValue();
                if(codes.isEmpty())
                    continue;

                // Discord only cares about the emote ID, so we just pass "a" as the name
                typeMenu.addChoice("a:" + codeType.getKey().getEmote());
                hasCodes = true;
            }

            if(!(hasCodes))
            {
                event.replyError("**" + member.getEffectiveName() + "** has not added any codes!");
                return false;
            }

            typeMenu.setAction((msg, emote) ->
            {
                CodeType codeType = CodeType.fromEmote(emote.getId());
                if(codeType == CodeType.UNKNOWN)
                    return;

                codePaginator.setUsers(member.getUser(), event.getAuthor())
                        .setColor(member.getColor());

                displayCodes(event, msg, member, codeType, userCodes.get(codeType), codePaginator);
            });

            if(message == null)
                event.reply("Profile for **" + member.getEffectiveName() + "**", m -> typeMenu.build().display(m));
            else
                typeMenu.build().display(message);

            return true;
        }

        private void displayCodes(CommandEvent event, Message message, Member member, CodeType codeType, Map<String, String> codes, Paginator.Builder codePaginator)
        {
            for(Map.Entry<String, String> entry : codes.entrySet())
                codePaginator.addItems(FormatUtil.getCodeLayout(entry.getKey(), entry.getValue()));

            codePaginator.setText(FormatUtil.sanitize(codeType.getFormattedName() + " codes for **" + member.getEffectiveName() + "**"));
            codePaginator.setAuthor("Profile for " + member.getEffectiveName(), member.getUser().getEffectiveAvatarUrl());
            codePaginator.build().display(message);
            handleBackButton(event, message, event.getMember(), member);
        }

        private void handleBackButton(CommandEvent cevent, Message message, Member... allowed)
        {
            waiter.waitForEvent(GuildMessageReactionAddEvent.class, event ->
            {
                if(!(event.getMessageIdLong() == message.getIdLong()))
                    return false;

                if(!(event.getReactionEmote().isEmoji()))
                    return false;

                if(event.getMember().getUser().isBot())
                    return false;

                if(!(Arrays.asList(allowed).contains(event.getMember())))
                    return false;

                return event.getReactionEmote().getName().equals(BACK);
            }, event ->
            {
                try
                {
                    Member member = allowed[1];
                    message.editMessage(FormatUtil.sanitize("Profile for **" + member.getEffectiveName() + "**")).queue();
                    message.clearReactions().queue(s ->
                    {
                        Paginator.Builder codePaginator = new Paginator.Builder()
                                .setEventWaiter(waiter)
                                .showPageNumbers(true)
                                .setTimeout(5, TimeUnit.MINUTES)
                                .setColumns(2)
                                .setFinalAction(finalAction)
                                .setUsers(member.getUser(), allowed[0].getUser())
                                .setColor(member.getColor());

                        String flag = bot.getCore().getFlag(member.getUser().getIdLong());
                        boolean hasFlag = !(flag.isEmpty());

                        if(hasFlag)
                            codePaginator.setTitle("Country: " + flag);

                        displayTypeSelector(cevent, member, message, codePaginator);
                    }, e -> {});
                }
                catch(PermissionException ignored) {}
            }, 5, TimeUnit.MINUTES, () -> finalAction.accept(message));
            message.addReaction(BACK).queueAfter(1, TimeUnit.SECONDS);
        }
    }

    private class RemoveCmd extends Command
    {
        RemoveCmd()
        {
            this.name = "remove";
            this.help = "Removes a code.";
            this.category = Categories.WII;
        }

        @Override
        protected void execute(CommandEvent event)
        {
            GuildSettings gs = event.getClient().getSettingsFor(event.getGuild());
            List<String> args = parseArgs(REMOVE_PATTERN, event.getArgs());
            if(args.size() < 2)
            {
                event.replyError("Wrong format! Correct one is `" + bot.getPrefix(event.getGuild()) +
                        "code remove <type> <name>`");
                return;
            }

            CodeType type = CodeType.fromCode(args.get(0));
            if(type == CodeType.UNKNOWN)
            {
                event.replyError(FormatUtil.getCodeTypes());
                return;
            }

            Map<String, String> codeTypes = bot.getCore().getCodesForType(type, event.getAuthor().getIdLong());
            if(!(codeTypes.containsKey(args.get(1))))
            {
                event.replyWarning("A code for `" + args.get(1) + "` is not registered.");
                return;
            }

            if(dataManager.removeCode(type, event.getAuthor().getIdLong(), args.get(1)))
                event.replySuccess("Removed the code for `" + args.get(1) + "`");
            else
                event.replyError("Error whilst removing a code! Please contact a developer.");
        }
    }

    private List<String> parseArgs(Pattern pattern, String args)
    {
        Matcher m = pattern.matcher(args);
        List<String> list = new LinkedList<>();

        while(m.find())
        {
            for(int i = 0; i <= m.groupCount(); i++)
            {
                if(!(m.group(i).trim().equals(args)))
                    list.add(m.group(i).trim());
            }
        }

        return list;
    }
}
