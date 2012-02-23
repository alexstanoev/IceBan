package eu.icecraft.iceban.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import eu.icecraft.iceban.IceBan;

public class WarnCommands implements CommandExecutor {
	public IceBan plugin;

	public WarnCommands(IceBan iceBan) {
		this.plugin = iceBan;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String cmdLbl, String[] args) {

		if(cmdLbl.equals("warn")) {
			if(!sender.hasPermission("iceban.warn")) return false;
			
			StringBuilder warn = new StringBuilder();
			boolean first = true;
			for(String part : args) {
				if(first) {
					first = false;
					continue;
				}
				warn.append(part);
			}
			
			//plugin.sql.warn(args[0], warn.toString(), sender.getName());
			
			return true;
		}
		
		if(cmdLbl.equals("warns") || cmdLbl.equals("warnings")) {
			// TODO
		}
		
		return false;
	}

}
