package eu.icecraft.iceban;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;

import eu.icecraft.iceauth.AuthPlayerLoginEvent;

public class PlayerListeners implements Listener {
	public IceBan plugin;

	public PlayerListeners(IceBan iceBan) {
		this.plugin = iceBan;
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerLogin(PlayerLoginEvent event) {
		if (event.getResult() != Result.ALLOWED) return;
		String ip = event.getKickMessage();
		BanInfo ban = plugin.getIPBan(ip);
		if(ban == null) return;
		if(ban.isIpBanned()) {
			String kickMessage = plugin.getKickMessage(ban);
			if(kickMessage == null) return; // Expired tempban
			event.disallow(Result.KICK_BANNED, kickMessage);
		}
	}

	@EventHandler
	public void onAuthPlayerLogin(AuthPlayerLoginEvent event) {
		if(event.isOnRegister()) return;
		Player p = event.getPlayer();
		BanInfo ban = plugin.getNameBan(p.getName());
		if(ban == null) return;
		if(ban.isNameBanned()) {
			plugin.banOnAuthLogin(p, ban);
		}
	}
}