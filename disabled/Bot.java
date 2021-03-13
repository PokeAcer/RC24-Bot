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

package xyz.rc24.bot;

import co.aikar.idb.DB;
import co.aikar.idb.DatabaseOptions;
import co.aikar.idb.PooledDatabaseOptions;
import com.jagrosh.jdautilities.command.CommandClientBuilder;
import com.mysql.cj.jdbc.Driver;
import com.mysql.cj.jdbc.MysqlDataSource;
import com.timgroup.statsd.NonBlockingStatsDClientBuilder;
import com.timgroup.statsd.StatsDClient;
import io.sentry.Sentry;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.ShutdownEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import okhttp3.OkHttpClient;
import org.slf4j.Logger;
import xyz.rc24.bot.commands.botadm.Bash;
import xyz.rc24.bot.commands.botadm.Eval;
import xyz.rc24.bot.commands.botadm.Shutdown;
import xyz.rc24.bot.commands.general.BirthdayCmd;
import xyz.rc24.bot.commands.general.FlagCmd;
import xyz.rc24.bot.commands.general.InviteCmd;
import xyz.rc24.bot.commands.general.PingCmd;
import xyz.rc24.bot.commands.general.RiiTagCmd;
import xyz.rc24.bot.commands.general.SetBirthdayCmd;
import xyz.rc24.bot.commands.tools.DefaultAddCmd;
import xyz.rc24.bot.commands.tools.PrefixCmd;
import xyz.rc24.bot.commands.tools.StatsCmd;
import xyz.rc24.bot.commands.wii.AddCmd;
import xyz.rc24.bot.commands.wii.BlocksCmd;
import xyz.rc24.bot.commands.wii.CodeCmd;
import xyz.rc24.bot.commands.wii.DNS;
import xyz.rc24.bot.commands.wii.ErrorInfoCmd;
import xyz.rc24.bot.commands.wii.WadsCmd;
import xyz.rc24.bot.commands.wii.WiiWare;
import xyz.rc24.bot.config.Config;
import xyz.rc24.bot.config.ConfigLoader;
import xyz.rc24.bot.core.BotCore;
import xyz.rc24.bot.core.entities.impl.BotCoreImpl;
import xyz.rc24.bot.database.BirthdayDataManager;
import xyz.rc24.bot.database.CodeDataManager;
import xyz.rc24.bot.database.Database;
import xyz.rc24.bot.database.GuildSettingsDataManager;
import xyz.rc24.bot.listeners.DataDogStatsListener;
import xyz.rc24.bot.managers.BirthdayManager;

import javax.security.auth.login.LoginException;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Add all commands, and start all listeners.
 *
 * @author Spotlight and Artuto
 */

public class Bot extends ListenerAdapter
{
    public BotCore core;
    public Config config;
    public JDA jda;

    // Database & Data managers
    private Database db;
    private BirthdayDataManager birthdayDataManager;
    private CodeDataManager codeDataManager;
    private GuildSettingsDataManager guildSettingsDataManager;

    // Managers
    private BirthdayManager birthdayManager;

    private final Logger logger = RiiConnect24Bot.getLogger();
    private final OkHttpClient httpClient = new OkHttpClient();
    private final ScheduledExecutorService botScheduler = Executors.newScheduledThreadPool(5);
    private final ScheduledExecutorService birthdaysScheduler = Executors.newSingleThreadScheduledExecutor();

    void run() throws LoginException
    {
        RiiConnect24Bot.setInstance(this);
        this.config = ConfigLoader.init();
        this.core = new BotCoreImpl(this);

        // Start database
        this.db = initDatabase();
        this.birthdayDataManager = new BirthdayDataManager(db);
        this.codeDataManager = new CodeDataManager(this);
        this.guildSettingsDataManager = new GuildSettingsDataManager(this);

        // Start managers
        this.birthdayManager = new BirthdayManager(getBirthdayDataManager());

        // Start Sentry (if enabled)
        if(config.sentry && !(config.sentryDSN.isEmpty()))
            Sentry.init(config.sentryDSN);

        DataDogStatsListener dataDogStatsListener = null;

        if(config.datadog)
        {
            StatsDClient statsd = new NonBlockingStatsDClientBuilder()
                    .prefix(config.datadogPrefix)
                    .hostname(config.datadogHost)
                    .port((int) config.datadogPort)
                    .build();

            dataDogStatsListener = new DataDogStatsListener(statsd);
        }

        // Setup Command Client




        // JDA Connection

    }

    @Override
    public void onReady(ReadyEvent event)
    {
        this.jda = event.getJDA();
        logger.info("Done loading!");

        ZonedDateTime zonedNow = OffsetDateTime.now().toZonedDateTime();
        ZonedDateTime zonedNext;

        if(config.birthdays)
        {
            // Every day at 8AM
            zonedNext = zonedNow.withHour(8).withMinute(0).withSecond(0);
            if(zonedNow.compareTo(zonedNext) > 0)
                zonedNext = zonedNext.plusDays(1);

            Duration duration = Duration.between(zonedNow, zonedNext);
            long initialDelay = duration.getSeconds();

            birthdaysScheduler.scheduleWithFixedDelay(() -> getBirthdayManager()
                    .updateBirthdays(event.getJDA(), config.birthdaysChannel, config.owner),
                    initialDelay, 86400, TimeUnit.SECONDS);
        }
    }

    @Override
    public void onShutdown(ShutdownEvent event)
    {
        birthdaysScheduler.shutdown();
        DB.close();
    }

    private Database initDatabase()
    {
        DatabaseOptions options = DatabaseOptions.builder()
                .mysql(config.databaseUser, config.databasePassword, config.database, config.databaseHost)
                .driverClassName(Driver.class.getName() /*"com.mysql.cj.jdbc.Driver"*/)
                .dataSourceClassName(MysqlDataSource.class.getName() /*"com.mysql.cj.jdbc.MysqlDataSource"*/)
                .build();

        Map<String, Object> props = new HashMap<>()
        {{
            put("useSSL", config.useSSL);
            put("verifyServerCertificate", false);
            put("autoReconnect", true);
            put("serverTimezone", "CST"); // Doesn't really matter
            put("characterEncoding", "UTF-8");
        }};

        co.aikar.idb.Database db = PooledDatabaseOptions.builder()
                .dataSourceProperties(props)
                .options(options)
                .createHikariDatabase();

        DB.setGlobalDatabase(db);

        return new Database();
    }

    public BotCore getCore()
    {
        return core;
    }

    public Config getConfig()
    {
        return config;
    }

    public Database getDatabase()
    {
        return db;
    }

    public JDA getJDA()
    {
        return jda;
    }

    public ScheduledExecutorService getBotScheduler()
    {
        return botScheduler;
    }

    // Data managers
    public BirthdayDataManager getBirthdayDataManager()
    {
        return birthdayDataManager;
    }

    public CodeDataManager getCodeDataManager()
    {
        return codeDataManager;
    }

    public GuildSettingsDataManager getGuildSettingsDataManager()
    {
        return guildSettingsDataManager;
    }

    // Managers
    public BirthdayManager getBirthdayManager()
    {
        return birthdayManager;
    }

    // Other
    public OkHttpClient getHttpClient()
    {
        return httpClient;
    }

	public String getPrefix(Guild guild)
	{
		return getCore().getGuildSettings(guild).getPrefix();
	}
}
