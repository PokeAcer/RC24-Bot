module SerieBot
  module Help
    extend Discordrb::Commands::CommandContainer
    extend Discordrb::EventContainer

    command(:help) do |event|
      help = ''
      help << Helper.get_help
      help << "\n\n"
      help << "**Specific commands**\n"
      help << "`#{Config.prefix}wads` may or may not DM you some WADs.\n"
      help << "`#{Config.prefix}help` or `#{Config.prefix}code help` will send you this help message.\n"
      help << "`#{Config.prefix}gametdb <platform> <title id>` will provide a GameTDB wiki page with the specified Title ID. Valid platforms are Wii, WiiU, PS3, 3DS, and DS.\n"
      help << "`#{Config.prefix}error <error code>` will provide you information about the specified error code from Wiimmfi.\n"
      help << "`#{Config.prefix}instructions` will reply with some setup instructions for RiiConnect24.\n"
      help << "`#{Config.prefix}dns` will reply with the DNS settings for RiiConnect24."
      help << "`#{Config.prefix}about` will tell you information about the bot.\n"
      help << "Need to patch your `nwc24msg.cfg` file? Just DM me the file, and I'll patch it for you."
      extrahelp = ""
      if Helper.is_admin?(event.user) || Helper.is_moderator?(event) || Helper.is_developer?(event)
        extrahelp << "\n\n**__Mod commands__**\n"
        extrahelp << "As this RiiConnect24 bot is a stripped down version of Yuu-Chan/Serie-Bot, you have a limited option of some moderation commands.\n"
        extrahelp << "\n"
        extrahelp << "**General commands**\n"
        extrahelp << "`#{Config.prefix}ignore @user`/`#{Config.prefix}unignore @user` will respectively ignore and unignore the specified user.\n"
        extrahelp << ""
      end
      if Helper.is_admin?(event.user) || Helper.is_bot_helper?(event) || Helper.is_developer?(event)
        extrahelp << "\n\n**Developers:**\n"
        extrahelp << "`#{Config.prefix}setavatar <file/URL>` will change the avatar to the provided URL/image.\n"
        extrahelp << "`#{Config.prefix}status <status>` changes the status of the bot to one of the options of idle, dnd, invisible or online.\n"
        extrahelp << "`#{Config.prefix}dump <id>` will dump all messages from the channel represented by the specified ID.\n"
        extrahelp << "`#{Config.prefix}clear <num>` will clear <num> messages from chat.\n"
        extrahelp << "`#{Config.prefix}kick @user` will kick @user from the server.\n"
        extrahelp << "`#{Config.prefix}ban @user` will ban @user from the server.\n"
        extrahelp << "`#{Config.prefix}lockdown <minutes>` will lockdown the channel. If specified, the channel will unlock after <minutes> or not at all.\n"
        extrahelp << "`#{Config.prefix}unlockdown` will remove the lockdown from the channel.\n"
        extrahelp << "**Bot-specific commands**\n"
        extrahelp << "`#{Config.prefix}wipecodes @user` will wipe all codes the specified user has added.\n"
        extrahelp << "`#{Config.prefix}save` will save the current state of codes to data/codes.yml.\n"
      end
      if Helper.is_admin?(event.user) || Helper.is_bot_helper?(event)
        extrahelp << "\n\n**Admins**\n"
        extrahelp << "`#{Config.prefix}eval <code>` will evaluate the specified Ruby string. !!! USE WITH CARE !!!\n"
        extrahelp << "`#{Config.prefix}bash <command>` will run the specified command in a bash shell. As before, !!! USE WITH CARE !!!\n"
        extrahelp << "`#{Config.prefix}shutdown` will do exactly as the name suggests to the bot.\n"
      end
      begin
        event.user.pm(help)
        if Helper.is_admin?(event.user) || Helper.is_moderator?(event) || Helper.is_developer?(event)
          event.user.pm(extrahelp)
        end
        event.respond("Check your DMs!")
      rescue Discordrb::Errors::NoPermission
        event.respond("❌ Sorry, but it looks like you're blocking DMs.")
      end
    end
  end
end
