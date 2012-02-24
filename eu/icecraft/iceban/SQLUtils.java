package eu.icecraft.iceban;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.alta189.sqlLibrary.MySQL.mysqlCore;

import eu.icecraft.iceban.BanInfo.BanType;

public class SQLUtils {

	public mysqlCore db;

	public SQLUtils(mysqlCore mysqlCore) {
		this.db = mysqlCore;
	}

	public BanInfo getBan(String field, BanType banType) {
		ResultSet result = null;
		try {
			Connection connection = db.getConnection();
			if(banType == BanType.IP_BAN) {
				PreparedStatement regQ = connection.prepareStatement("SELECT * FROM bans WHERE ip = ? AND active = 1 ORDER BY id DESC LIMIT 1");
				regQ.setString(1, field.toLowerCase());

				result = regQ.executeQuery();
			} else {
				PreparedStatement regQ = connection.prepareStatement("SELECT * FROM bans WHERE nick = ? AND active = 1 ORDER BY id DESC LIMIT 1");
				regQ.setString(1, field.toLowerCase());

				result = regQ.executeQuery();
			}

			while(result.next()) {
				int bannedUntil = result.getInt("bannedUntil");
				return new BanInfo(banType, result.getInt("id"), result.getString("reason"), result.getString("bannedBy"), bannedUntil == 0 ? false : true, bannedUntil);
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
		return new BanInfo(BanType.NOT_BANNED);
	}

	public void ban(String name, String hostAddress, int bannedUntil, String banMessage, String bannedBy) {
		Connection connection = null;
		try {
			connection = db.getConnection();
			PreparedStatement regQupd = connection.prepareStatement("INSERT INTO bans (ip, nick, reason, bannedOn, bannedUntil, bannedBy, active) VALUES(?, ?, ?, ?, ?, ?, 1)");
			regQupd.setString(1, hostAddress);
			regQupd.setString(2, name.toLowerCase());
			regQupd.setString(3, banMessage);
			regQupd.setInt(4, (int) (System.currentTimeMillis() / 1000L));
			regQupd.setInt(5, bannedUntil);
			regQupd.setString(6, bannedBy);
			regQupd.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void unban(int banID) {
		Connection connection = null;
		try {
			connection = db.getConnection();
			PreparedStatement regQupd = connection.prepareStatement("UPDATE bans SET active = 0 WHERE id = ?");
			regQupd.setInt(1, banID);
			regQupd.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void unbanIp(String ip) {
		Connection connection = null;
		try {
			connection = db.getConnection();
			PreparedStatement regQupd = connection.prepareStatement("UPDATE bans SET active = 0 WHERE ip = ?");
			regQupd.setString(1, ip);
			regQupd.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String getIpFromBan(int banID) {
		ResultSet result = null;
		try {
			Connection connection = db.getConnection();

			PreparedStatement regQ = connection.prepareStatement("SELECT ip FROM bans WHERE id = ? AND bannedBy IS NOT NULL AND active = 1 ORDER BY id DESC LIMIT 1");
			regQ.setInt(1, banID);
			result = regQ.executeQuery();

			while(result.next()) {
				return result.getString("ip");
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
		return null;
	}

	public String getIpFromBan(String nick) {
		ResultSet result = null;
		try {
			Connection connection = db.getConnection();

			PreparedStatement regQ = connection.prepareStatement("SELECT ip FROM bans WHERE nick = ? AND bannedBy IS NOT NULL AND active = 1 ORDER BY id DESC LIMIT 1");
			regQ.setString(1, nick.toLowerCase());
			result = regQ.executeQuery();

			while(result.next()) {
				return result.getString("ip");
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
		return null;
	}

	public void warn(String nick, String warn, String warnedBy) {
		Connection connection = null;
		try {
			connection = db.getConnection();
			PreparedStatement regQupd = connection.prepareStatement("INSERT INTO warns (nick, warn, warnedOn, warnedBy) VALUES(?, ?, ?, ?)");
			regQupd.setString(1, nick.toLowerCase());
			regQupd.setString(2, warn);
			regQupd.setInt(3, (int) (System.currentTimeMillis() / 1000L));
			regQupd.setString(4, warnedBy);
			regQupd.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String getLastWarn(String nick) {
		ResultSet result = null;
		try {
			Connection connection = db.getConnection();

			PreparedStatement regQ = connection.prepareStatement("SELECT * FROM warns WHERE nick = ? ORDER BY id DESC LIMIT 1");
			regQ.setString(1, nick.toLowerCase());
			result = regQ.executeQuery();

			while(result.next()) {
				return result.getString("warn");
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
		return null;
	}

	public int rawUpdateQuery(String query) {
		Connection connection = null;
		try {
			connection = db.getConnection();
			PreparedStatement regQupd = connection.prepareStatement(query);
			return regQupd.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

}
