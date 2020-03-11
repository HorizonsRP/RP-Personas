package net.korvic.rppersonas.accounts;

import net.korvic.rppersonas.RPPersonas;

import java.util.HashMap;
import java.util.Map;

public class AccountHandler {

	private RPPersonas plugin;
	private Map<Integer, Account> loadedAccounts;

	private static final String MULTIPLE_ACCOUNTS_WARN = "Found multiple accounts with the ID ";

	public AccountHandler(RPPersonas plugin) {
		this.plugin = plugin;
		loadedAccounts = new HashMap<>();
	}

	public Account getAccount(int id) {
		return loadedAccounts.get(id);
	}

	public Account loadAccount(int accountID, int activePersonaID) {
		Account a = Account.createActiveAccount(accountID, activePersonaID);
		if (a != null) {
			loadedAccounts.put(a.getAccountID(), a);
		}
		return a;
	}

	public void unloadAccount(int id) {
		loadedAccounts.remove(id);
	}

}
