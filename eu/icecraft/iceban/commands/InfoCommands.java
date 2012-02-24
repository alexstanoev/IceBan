package eu.icecraft.iceban.commands;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import eu.icecraft.iceban.IceBan;
import eu.icecraft.iceban.Utils;

public class InfoCommands implements CommandExecutor {
	public IceBan plugin;

	public InfoCommands(IceBan iceBan) {
		this.plugin = iceBan;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String cmdLbl, String[] args) {
		if(!sender.hasPermission("iceban." + cmdLbl)) return false;

		if(cmdLbl.equals("banreason")) {
			if(args.length != 1) return false;
			ResultSet result = null;
			try {
				Connection connection = plugin.db.getConnection();

				PreparedStatement regQ = connection.prepareStatement("SELECT * FROM bans WHERE nick = ? AND bannedBy IS NOT NULL AND active = 1 ORDER BY id DESC LIMIT 1");
				regQ.setString(1, args[0].toLowerCase());
				result = regQ.executeQuery();

				while(result.next()) {
					sender.sendMessage(ChatColor.GOLD + "Player " + ChatColor.DARK_AQUA + args[0] + ChatColor.GOLD + " is banned:");

					String msg = "";
					msg = msg + ChatColor.GRAY;

					if(result.getInt("bannedUntil") == 0) msg = msg + ChatColor.GOLD + "Perm" + ChatColor.GRAY + " ban ";
					else msg = msg +  ChatColor.GOLD + "Temp" + ChatColor.GRAY + " ban for " + ChatColor.GOLD + Utils.getTimeString(result.getInt("bannedUntil") - result.getInt("bannedOn")) + ChatColor.GRAY;

					msg = msg + "by " + ChatColor.GOLD + result.getString("bannedBy") + ChatColor.GRAY;

					Date bannedOn = new Date(result.getLong("bannedOn") * 1000L);
					SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm");
					msg = msg + " on " + ChatColor.GOLD + sdf.format(bannedOn) + ChatColor.GRAY;

					sender.sendMessage(msg);
					sender.sendMessage(ChatColor.DARK_AQUA + "Reason: " + ChatColor.GOLD + result.getString("reason"));
					return true;
				}

				sender.sendMessage(ChatColor.GOLD + "Player " + ChatColor.DARK_AQUA + args[0] + ChatColor.GOLD + " isn't banned.");

			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					result.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			return true;
		}

		if(cmdLbl.equals("banhistory")) {
			if(args.length != 1) return false;
			ResultSet result = null;
			try {
				Connection connection = plugin.db.getConnection();

				PreparedStatement regQ = connection.prepareStatement("SELECT * FROM bans WHERE nick = ? AND bannedBy IS NOT NULL ORDER BY id ASC");
				regQ.setString(1, args[0].toLowerCase());
				result = regQ.executeQuery();

				sender.sendMessage(ChatColor.GOLD + "Ban history for player " + ChatColor.DARK_AQUA + args[0]);
				sender.sendMessage(ChatColor.YELLOW + "----------");

				while(result.next()) {
					String msg = "";

					if(result.getInt("active") == 1) msg = msg + ChatColor.RED + "[Active] ";
					else msg = msg + ChatColor.GRAY + "[Past] ";

					msg = msg + ChatColor.GRAY;

					if(result.getInt("bannedUntil") == 0) msg = msg + ChatColor.GOLD + "Perm" + ChatColor.GRAY + " ban ";
					else msg = msg +  ChatColor.GOLD + "Temp" + ChatColor.GRAY + " ban for " + ChatColor.GOLD + Utils.getTimeString(result.getInt("bannedUntil") - result.getInt("bannedOn")) + ChatColor.GRAY;

					msg = msg + "by " + ChatColor.GOLD + result.getString("bannedBy") + ChatColor.GRAY;

					Date bannedOn = new Date(result.getLong("bannedOn") * 1000L);
					SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm");
					msg = msg + " on " + ChatColor.GOLD + sdf.format(bannedOn) + ChatColor.GRAY;

					sender.sendMessage(msg);
					sender.sendMessage(ChatColor.DARK_AQUA + "Reason: " + ChatColor.GOLD + result.getString("reason"));
					sender.sendMessage(ChatColor.YELLOW + "----------");
				}

			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					result.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			return true;
		}

		return false;
	}

}
