package com.github.book000.FirstPlayerNotify.Event;

import java.awt.Color;
import java.util.Collection;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import com.github.book000.FirstPlayerNotify.FirstPlayerNotify;

import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.util.EmbedBuilder;
import sx.blah.discord.util.RequestBuffer;

public class Event_FirstJoin implements Listener {
	JavaPlugin plugin;
	public Event_FirstJoin(JavaPlugin plugin){
		plugin = this.plugin;
	}
	@EventHandler
	public void OnEvent_FirstJoin(PlayerJoinEvent event){
		Player player = event.getPlayer();
		if(player.hasPlayedBefore()){
			return; // すでにログインしたことがある
		}
		// 初めてログイン

		IDiscordClient client = FirstPlayerNotify.client;
		if(client == null){
			return;
		}
		long channel_id = FirstPlayerNotify.channel_id;
		if(channel_id == -1){
			return;
		}
		IChannel channel = client.getChannelByID(channel_id);
		if(channel == null){
			plugin.getLogger().warning("指定されたIDのチャンネルを取得できませんでした。");
			return;
		}

		 EmbedBuilder builder = new EmbedBuilder();
		 builder.withTitle("プレイヤーが初めてログインしました！");
		 builder.withDescription("以下のプレイヤーが初めてサーバにログインしました。");
		 builder.withColor(Color.YELLOW);
		 builder.withFooterIcon("https://i.imgur.com/I132BaO.png");
		 builder.withFooterText("book000/FirstPlayerNotify");
		 builder.withAuthorIcon(client.getOurUser().getAvatarURL());
		 builder.withAuthorName(client.getOurUser().getName());
		 builder.withTimestamp(System.currentTimeMillis());

		 builder.appendField("プレイヤー名", "`" + player.getName() + "`", false);
		 builder.appendField("NameMC", "https://ja.namemc.com/profile/" + player.getUniqueId().toString(), false);
		 builder.appendField("プレイヤー数", Bukkit.getServer().getOnlinePlayers().size() + "人", false);
		 builder.appendField("オンラインプレイヤー", "`" + implode(Bukkit.getServer().getOnlinePlayers(), ", ") + "`", false);

		 RequestBuffer.request(() -> channel.sendMessage(builder.build()));
	}
	public static <T> String implode(Collection<? extends Player> list, String glue) {
	    StringBuilder sb = new StringBuilder();
	    for (Player e : list) {
	        sb.append(glue).append(e.getName());
	    }
	    return sb.substring(glue.length());
	}
}
