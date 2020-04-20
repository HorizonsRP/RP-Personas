package net.korvic.rppersonas.death;

import co.lotc.core.bukkit.util.LocationUtil;
import net.korvic.rppersonas.RPPersonas;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class DeathHandler {

	private Map<Player, DeathRequest> requestMap = new HashMap<>(); // Victim, Request

	private final RPPersonas plugin;

	public DeathHandler(RPPersonas plugin) {
		this.plugin = plugin;
	}

	public void requestExecute(Player killer, Player victim) {
		requestMap.put(victim, new DeathRequest(killer, victim));
		pingRequest(victim);
	}

	public boolean acceptExecute(Player killer, Player victim) {
		boolean output = false;
		if (requestMap.containsKey(victim)) {
			DeathRequest req = requestMap.get(victim);
			if (req.getKiller().equals(killer)) {
				req.complete(false);
				requestMap.remove(victim);
				output = true;
			}
		}
		return output;
	}

	public void forceExecute(Player killer, Player victim) {
		new DeathRequest(killer, victim).complete(true);
	}

	public boolean hasRequest(Player victim) {
		return requestMap.containsKey(victim);
	}

	public void pingRequest(Player victim) {

	}

}