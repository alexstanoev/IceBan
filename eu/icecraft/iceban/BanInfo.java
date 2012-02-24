package eu.icecraft.iceban;

public class BanInfo {
	private int banId;
	private BanType banType = BanType.NOT_BANNED;
	private boolean isTempBan = false;
	private int bannedUntil;
	private String bannedBy;
	private String banMessage;
	private String nick;

	public BanInfo(BanType banType) {
		this.banType = banType;
	}

	public BanInfo(BanType banType, int banId, String nick, String banMessage, String bannedBy) {
		this.banId = banId;
		this.banType = banType;
		this.nick = nick;
		this.banMessage = banMessage;
		this.bannedBy = bannedBy;
	}

	public BanInfo(BanType banType, int banId, String nick, String banMessage, String bannedBy, boolean isTempBan, int bannedUntil) {
		this.banId = banId;
		this.banType = banType;
		this.nick = nick;
		this.isTempBan = isTempBan;
		this.bannedUntil = bannedUntil;
		this.banMessage = banMessage;
		this.bannedBy = bannedBy;
	}

	public int getBanID() {
		return this.banId;
	}

	public String getNick() {
		return this.nick;
	}

	public boolean isIpBanned() {
		return banType == BanType.IP_BAN;
	}

	public boolean isNameBanned() {
		return banType == BanType.NAME_BAN;
	}

	public boolean isTempBan() {
		return this.isTempBan;
	}

	public int getBannedUntil() {
		return this.bannedUntil;
	}

	public String isTempBanActive() {
		if(!this.isTempBan) return null;
		int diff = this.bannedUntil - (int)(System.currentTimeMillis() / 1000L);
		if(diff > 0) return Utils.getTimeString(diff);
		return null;
	}

	public void setBanMessage(String banMessage) {
		if(banMessage.length() > 124) {
			this.banMessage = banMessage.substring(0, 124) + "..";
		} else {
			this.banMessage = banMessage;
		}
	}

	public String getBanMessage() {
		return this.banMessage;
	}

	public String getBannedBy() {
		return this.bannedBy;
	}

	public static enum BanType {
		NOT_BANNED, IP_BAN, NAME_BAN
	}
}
