package com.github.book000.FirstPlayerNotify;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import com.github.book000.FirstPlayerNotify.Event.Event_FirstJoin;

import sx.blah.discord.api.ClientBuilder;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.events.EventDispatcher;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.ReadyEvent;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.util.DiscordException;

public class FirstPlayerNotify extends JavaPlugin {
	public static IDiscordClient client = null;
	public static Long channel_id = null;
	/**
	 * プラグインが起動したときに呼び出し
	 * @author mine_book000
	 * @since 2019/03/28
	 */
	@Override
	public void onEnable() {
		getLogger().info("FirstPlayerNotify created by Tomachi (mine_book000)");
		getLogger().info("ProjectPage: https://github.com/book000/FirstPlayerNotify");

		getConfig().options().copyDefaults(true);
		saveDefaultConfig();
		FileConfiguration conf = getConfig();
		if(!conf.contains("DiscordAPITOKEN") || !conf.isString("DiscordAPITOKEN")){
			getLogger().warning("コンフィグにDiscordAPITOKENが定義されていないか、String型ではありません。");
			getLogger().warning("https://github.com/book000/FirstPlayerNotify/wiki/Setup で初期設定の方法を確認できます。");
			getLogger().warning("FirstPlayerNotifyプラグインを終了します。");
			getServer().getPluginManager().disablePlugin(this);
			return;
		}
		String token = conf.getString("DiscordAPITOKEN");
		if(token.equals("ENTER-YOUR-APITOKEN-HERE")){
			// ENTER-YOUR-APIKEY-HERE
			getLogger().warning("コンフィグにDiscordAPITOKENが設定されておらず、初期設定のままです。");
			getLogger().warning("https://github.com/book000/FirstPlayerNotify/wiki/Setup で初期設定の方法を確認できます。");
			getLogger().warning("FirstPlayerNotifyプラグインを終了します。");
			getServer().getPluginManager().disablePlugin(this);
			return;
		}
		getLogger().warning("DiscordAPITOKEN: " + token);

		if(!conf.contains("DiscordNoticeChannelID") || !conf.isLong("DiscordNoticeChannelID")){
			getLogger().warning("コンフィグにDiscordNoticeChannelIDが定義されていないか、Long型ではありません。");
			getLogger().warning("https://github.com/book000/FirstPlayerNotify/wiki/Setup で初期設定の方法を確認できます。");
			getLogger().warning("FirstPlayerNotifyプラグインを終了します。");
			getServer().getPluginManager().disablePlugin(this);
			return;
		}
		channel_id = conf.getLong("DiscordNoticeChannelID");
		if(channel_id.equals("ENTER-NOTICE-CHANNEL-ID-HERE")){
			// ENTER-YOUR-APIKEY-HERE
			getLogger().warning("コンフィグにDiscordNoticeChannelIDが設定されておらず、初期設定のままです。");
			getLogger().warning("https://github.com/book000/FirstPlayerNotify/wiki/Setup で初期設定の方法を確認できます。");
			getLogger().warning("FirstPlayerNotifyプラグインを終了します。");
			getServer().getPluginManager().disablePlugin(this);
			return;
		}
		getLogger().warning("DiscordNoticeChannelID: " + channel_id);
		client = createClient(token, true);
		if(client == null){
			getLogger().warning("Discordへのログインに失敗したか、接続できません。");
			getLogger().warning("FirstPlayerNotifyプラグインを終了します。");
			getServer().getPluginManager().disablePlugin(this);
			return;
		}

		EventDispatcher dispatcher = client.getDispatcher();
		dispatcher.registerListener(this);
		getServer().getPluginManager().registerEvents(new Event_FirstJoin(this), this);
	}
	@EventSubscriber
	public void onReadyEvent(ReadyEvent event) {
		getLogger().info("FirstPlayerNotifyプラグインの起動に成功しました。");
		getLogger().info("Botユーザー: " + event.getClient().getOurUser().getName() + "#" + event.getClient().getOurUser().getDiscriminator());

		IChannel channel = client.getChannelByID(channel_id);
		if(channel == null){
			getLogger().warning("指定されたIDのチャンネルを取得できませんでした。");
			getLogger().warning("FirstPlayerNotifyプラグインを終了します。");
			getServer().getPluginManager().disablePlugin(this);
			return;
		}
		getLogger().info("通知先チャンネル: " + channel.getName() + " (" + channel.getGuild().getName() + ")");
	}
	public static IDiscordClient createClient(String token, boolean login) {
		ClientBuilder clientBuilder = new ClientBuilder();
		clientBuilder.withToken(token);
		try {
			if (login) {
				return clientBuilder.login();
			} else {
				return clientBuilder.build();
			}
		} catch (DiscordException e) {
			e.printStackTrace();
			return null;
		}
	}
}
