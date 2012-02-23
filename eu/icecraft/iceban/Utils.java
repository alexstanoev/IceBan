package eu.icecraft.iceban;

import java.text.ParseException;
import java.text.SimpleDateFormat;

public class Utils {

	public static String getTimeString(long unixTime) {
		int n = (int)(unixTime / 86400L);
		unixTime -= n * 86400;
		int i = (int)(unixTime / 3600L);
		unixTime -= i * 3600;
		int j = (int)(unixTime / 60L);

		String str = "";
		if (n > 0) str = str + n + "d "; 
		if (i > 0) str = str + i + "h "; 
		if (j > 0) str = str + j + "m";  

		return str;
	}

	public static boolean isInt(String str) {
		try {
			Integer.parseInt(str);
			return true;
		} catch (final NumberFormatException ex) {}
		return false;
	}

	public static int parseTimeSpec(String specStr) { // Thanks, LogBlock
		String[] spec = specStr.split(" ");
		if (spec == null || spec.length < 1 || spec.length > 2)
			return -1;
		if (spec.length == 1 && isInt(spec[0]))
			return Integer.valueOf(spec[0]);
		if (!spec[0].contains(":") && !spec[0].contains("."))
			if (spec.length == 2) {
				if (!isInt(spec[0]))
					return -1;
				int min = Integer.parseInt(spec[0]);
				if (spec[1].startsWith("h"))
					min *= 60;
				else if (spec[1].startsWith("d"))
					min *= 1440;
				return min;
			} else if (spec.length == 1) {
				int days = 0, hours = 0, minutes = 0;
				int lastIndex = 0, currIndex = 1;
				while (currIndex <= spec[0].length()) {
					while (currIndex <= spec[0].length() && isInt(spec[0].substring(lastIndex, currIndex)))
						currIndex++;
					if (currIndex - 1 != lastIndex) {
						final String param = spec[0].substring(currIndex - 1, currIndex).toLowerCase();
						if (param.equals("d"))
							days = Integer.parseInt(spec[0].substring(lastIndex, currIndex - 1));
						else if (param.equals("h"))
							hours = Integer.parseInt(spec[0].substring(lastIndex, currIndex - 1));
						else if (param.equals("m"))
							minutes = Integer.parseInt(spec[0].substring(lastIndex, currIndex - 1));
					}
					lastIndex = currIndex;
					currIndex++;
				}
				if (days == 0 && hours == 0 && minutes == 0)
					return -1;
				return minutes + hours * 60 + days * 1440;
			} else
				return -1;
		final String timestamp;
		if (spec.length == 1) {
			if (spec[0].contains(":"))
				timestamp = new SimpleDateFormat("dd.MM.yyyy").format(System.currentTimeMillis()) + " " + spec[0];
			else
				timestamp = spec[0] + " 00:00:00";
		} else
			timestamp = spec[0] + " " + spec[1];
		try {
			return (int) ((new SimpleDateFormat("dd.MM.yyyy HH:mm:ss").parse(timestamp).getTime()) / 60000);
		} catch (final ParseException ex) {
			return -1;
		}
	}
}
