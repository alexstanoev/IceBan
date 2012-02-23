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
			if(ban.getBanMessage().length() > 50) ban.setBanMessage(ban.getBanMessage().substring(0, 50) + "..");
			String banMessage = "Banned! Reason: " + ban.getBanMessage() + ". More info at http://icecraft-mc.eu/?b=" + ban.getBanID();
			if(ban.isTempBan()) {
				String tmpBanMessage = ban.isTempBanActive();
				if(tmpBanMessage == null) {
					plugin.unban(ban);
					return;
				} else {
					ban.setBanMessage("Banned for "+tmpBanMessage+"! Reason: " + ban.getBanMessage() + ". Check http://icecraft-mc.eu/?b=" + ban.getBanID());
				}
			} else ban.setBanMessage(banMessage);

			event.disallow(Result.KICK_BANNED, ban.getBanMessage());
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