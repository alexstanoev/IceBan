package eu.icecraft.iceban;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import com.alta189.sqlLibrary.MySQL.mysqlCore;

import eu.icecraft.iceauth.IceAuth;
import eu.icecraft.iceban.BanInfo.BanType;
import eu.icecraft.iceban.commands.BanCommands;
import eu.icecraft.iceban.commands.InfoCommands;

public class IceBan extends JavaPlugin {
	public IceAuth iceAuth;
	public mysqlCore db;
	public SQLUtils sql;

	@Override
	public void onDisable() {
		System.out.println(this + " was disabled!");
	}

	@Override
	public void onEnable() {
		Plugin iceAuthRaw = this.getServer().getPluginManager().getPlugin("IceAuth");
		if(iceAuthRaw != null && iceAuthRaw.isEnabled()) {
			this.iceAuth = (IceAuth) iceAuthRaw;
			System.out.println("["+this+"] Hooked into IceAuth!");
		} else {
			System.err.println("["+this+"] Unable to hook to IceAuth! Disabling.");
			this.getServer().getPluginManager().disablePlugin(this);
			return;
		}

		this.db = iceAuth.manageMySQL;
		if(iceAuth.MySQL == false || db == null) {
			System.err.println("["+this+"] IceAuth not using MySQL! Disabling.");
			this.getServer().getPluginManager().disablePlugin(this);
			return;
		}

		this.sql = new SQLUtils(db);

		this.getServer().getPluginManager().registerEvents(new PlayerListeners(this), this);

		int rowsChanged = sql.rawUpdateQuery("UPDATE bans SET active = 0 WHERE bannedUntil < UNIX_TIMESTAMP(NOW())");
		System.out.println("["+this+"] Cleaned up " + rowsChanged + " expired tempbans.");

		BanCommands banCommands = new BanCommands(this);
		InfoCommands infoCommands = new InfoCommands(this);
		//WarnCommands warnCommands = new WarnCommands(this);

		getCommand("ban").setExecutor(banCommands);
		getCommand("sbh").setExecutor(banCommands);
		getCommand("unban").setExecutor(banCommands);
		getCommand("sunban").setExecutor(banCommands);

		getCommand("sbreason").setExecutor(infoCommands);
		getCommand("banreason").setExecutor(infoCommands);
		getCommand("banhistory").setExecutor(infoCommands);

		//getCommand("warn").setExecutor(warnCommands);
		//getCommand("warnings").setExecutor(warnCommands);

		System.out.println(this + " was enabled!");
	}

	public void banOnAuthLogin(Player p, BanInfo ban) {
		String kickMsg = getKickMessage(ban);
		if(kickMsg == null) return;

		sql.ban(p.getName(), p.getAddress().getAddress().getHostAddress(), ban.getBannedUntil(), ban.getBanMessage(), null);
		p.kickPlayer(kickMsg);
	}

	public BanInfo getNameBan(String p) {
		return sql.getBan(p, BanType.NAME_BAN);
	}

	public BanInfo getIPBan(String ip) {
		return sql.getBan(ip, BanType.IP_BAN);
	}

	public void unban(BanInfo ban) {
		String ip = sql.getIpFromBan(ban.getBanID());
		sql.unbanName(ban.getNick());
		if(ip != null) sql.unbanIp(ip);
	}

	public String getKickMessage(BanInfo ban) {
		if(ban.getBanMessage().length() > 50) ban.setBanMessage(ban.getBanMessage().substring(0, 50) + "..");
		String banMessage = "Banned! Reason: " + ban.getBanMessage() + ". More info at http://icecraft-mc.eu/?b=" + ban.getBanID();
		if(ban.isTempBan()) {
			String tmpBanMessage = ban.isTempBanActive();
			if(tmpBanMessage == null) {
				unban(ban);
				return null;
			} else {
				return "Banned for "+tmpBanMessage+"! Reason: " + ban.getBanMessage() + ". Check http://icecraft-mc.eu/?b=" + ban.getBanID();
			}
		} return banMessage;
	}

}
