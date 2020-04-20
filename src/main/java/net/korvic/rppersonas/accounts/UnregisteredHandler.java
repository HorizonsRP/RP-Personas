package net.korvic.rppersonas.accounts;

import net.korvic.rppersonas.RPPersonas;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class UnregisteredHandler {

	private static final String REGISTER_REMINDER = RPPersonas.PRIMARY_DARK + "Please register your forum account with " + RPPersonas.SECONDARY_LIGHT + "/account forumlink Forum_ID" + RPPersonas.PRIMARY_DARK + "!\n" +
													RPPersonas.PRIMARY_DARK + "If you need help, please use " + RPPersonas.SECONDARY_LIGHT + "/request new" + RPPersonas.PRIMARY_DARK + " and someone will swing by to give you a hand.";
	private RPPersonas plugin;
	private BukkitRunnable runnable;

	List<Player> players = new ArrayList<>();

	public UnregisteredHandler(RPPersonas plugin) {
		this.plugin = plugin;
		this.runnable = new BukkitRunnable() {
			@Override
			public void run() {
				pingUnregistered();
			}
		};
		runnable.runTaskTimerAsynchronously(plugin, 0, 300 * 20);
	}

	public void add(Player p) {
		players.add(p);
		ping(p);
	}

	public void remove(Player p) {
		players.remove(p);
	}

	public void pingUnregistered() {
		for (Player p : players) {
			if (p.isOnline()) {
				ping(p);
			} else {
				players.remove(p);
			}
		}
	}

	private void ping(Player p) {
		p.sendMessage(REGISTER_REMINDER);
	}

}