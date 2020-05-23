package net.korvic.rppersonas.commands;

import co.lotc.core.bukkit.menu.Menu;
import co.lotc.core.bukkit.menu.MenuAction;
import co.lotc.core.bukkit.menu.MenuAgent;
import co.lotc.core.bukkit.menu.MenuUtil;
import co.lotc.core.bukkit.menu.icon.Button;
import co.lotc.core.bukkit.menu.icon.Icon;
import co.lotc.core.command.annotate.Cmd;
import net.korvic.rppersonas.RPPersonas;
import net.korvic.rppersonas.personas.Persona;
import net.korvic.rppersonas.statuses.Status;
import net.korvic.rppersonas.statuses.StatusEntry;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class StatusCommands extends BaseCommand {

	private RPPersonas plugin;

	public StatusCommands(RPPersonas plugin) {
		this.plugin = plugin;
	}

	@Cmd(value="Open the status management menu.")
	public void menu(CommandSender sender) {
		if (sender instanceof Player) {
			Player p = (Player) sender;
			if (plugin.getPersonaHandler().getLoadedPersona(p) != null) {
				openStatusMenu(p);
			} else {
				msg(RPPersonas.PRIMARY_DARK + "You need to register a persona first! Be sure to link your account to get started.");
			}
		} else {
			msg(PersonaCommands.NO_CONSOLE);
		}
	}

	private void openStatusMenu(Player p) {
		Persona pers = plugin.getPersonaHandler().getLoadedPersona(p);
		buildMainMenu(null, pers).openSession(p);

	}

	// MAIN MENU //
	public static Menu buildMainMenu(Menu menu, Persona pers) {
		List<Icon> icons = new ArrayList<>();

		// Available Button
		icons.add(new Button() {
			@Override
			public ItemStack getItemStack(MenuAgent menuAgent) {
				ItemStack item = new ItemStack(Material.ENDER_EYE);
				ItemMeta meta = item.getItemMeta();
				meta.setDisplayName(RPPersonas.PRIMARY_DARK + "" + ChatColor.BOLD + "Add Status");

				List<String> lore = new ArrayList<>();
				lore.add(RPPersonas.SECONDARY_DARK + "Click here to browse all statuses.");

				meta.setLore(lore);
				item.setItemMeta(meta);

				return item;
			}

			@Override
			public void click(MenuAction menuAction) {
				buildAvailableStatusMenu(menuAction.getMenuAgent().getMenu(), pers).openSession(menuAction.getPlayer());
			}
		});

		// Active Button
		icons.add(new Button() {
			@Override
			public ItemStack getItemStack(MenuAgent menuAgent) {
				ItemStack item = new ItemStack(Material.TOTEM_OF_UNDYING);
				ItemMeta meta = item.getItemMeta();
				meta.setDisplayName(RPPersonas.PRIMARY_DARK + "" + ChatColor.BOLD + "Current Statuses");

				List<String> lore = new ArrayList<>();
				lore.add(RPPersonas.SECONDARY_DARK + "Click here to browse active statuses.");

				meta.setLore(lore);
				item.setItemMeta(meta);

				return item;
			}

			@Override
			public void click(MenuAction menuAction) {
				buildActiveStatusMenu(menuAction.getMenuAgent().getMenu(), pers).openSession(menuAction.getPlayer());
			}
		});

		// Build Menu
		if (menu != null) {
			return Menu.fromIcons(menu, "Status Effects", icons);
		} else {
			return Menu.fromIcons("Status Effects", icons);
		}
	}

	// AVAILABLE STATUSES //
	private static Menu buildAvailableStatusMenu(Menu menu, Persona pers) {
		List<Icon> icons = new ArrayList<>();
		for (Status status : Status.getStatuses()) {
			icons.add(buildAvailableStatusIcon(status, pers));
		}
		return MenuUtil.createMultiPageMenu(menu, ChatColor.BOLD + "Available Statuses", icons).get(0);
	}

	private static Icon buildAvailableStatusIcon(Status status, Persona pers) {
		return new Button() {
			@Override
			public ItemStack getItemStack(MenuAgent menuAgent) {
				ItemStack item = new ItemStack(status.getMaterial());
				ItemMeta meta = item.getItemMeta();
				meta.setDisplayName(status.getColor() + "" + status.getIcon() + " " + RPPersonas.SECONDARY_DARK + status.getName());

				List<String> lore = new ArrayList<>();

				lore.add(RPPersonas.SECONDARY_DARK + "Click to apply this to your persona.");

				lore.add("");

				// Add description broken down into 35 width pieces.
				double charsPerLine = 35d;
				int pages = (int) Math.ceil(status.getDescription().length()/charsPerLine);
				for (int i = 1; i <= pages; i++) {
					int j = (int) ((i - 1) * charsPerLine);
					int k = (int) (i * charsPerLine);

					if (status.getDescription().length() < k) {
						k = status.getDescription().length();
					}
					lore.add(status.getDescription().substring(j, k));
				}

				meta.setLore(lore);
				item.setItemMeta(meta);
				return item;
			}

			@Override
			public void click(MenuAction menuAction) {
				pers.addStatus(status, (byte) 1, 1000 * 15);
			}
		};
	}

	// ACTIVE STATUSES //
	private static Menu buildActiveStatusMenu(Menu menu, Persona pers) {
		List<Icon> icons = new ArrayList<>();
		for (StatusEntry entry : pers.getActiveStatuses()) {
			icons.add(buildActiveStatusIcon(menu, pers, entry));
		}
		return MenuUtil.createMultiPageMenu(menu, ChatColor.BOLD + "Active Statuses", icons).get(0);
	}

	private static Icon buildActiveStatusIcon(Menu menu, Persona pers, StatusEntry entry) {
		Status status = entry.getStatus();
		return new Button() {
			@Override
			public ItemStack getItemStack(MenuAgent menuAgent) {
				ItemStack item = new ItemStack(status.getMaterial());
				ItemMeta meta = item.getItemMeta();
				meta.setDisplayName(status.getColor() + "" + status.getIcon() + " " + RPPersonas.SECONDARY_DARK + status.getName());

				List<String> lore = new ArrayList<>();

				// Toggleable text
				if (status.isToggleable()) {
					String active = "";
					if (entry.isEnabled()) {
						active += ChatColor.GREEN + "" + ChatColor.BOLD + "Active " + RPPersonas.SECONDARY_DARK;
					} else {
						active += ChatColor.RED + "" + ChatColor.BOLD + "Inactive " + RPPersonas.SECONDARY_DARK;
					}
					lore.add(active + "Click to toggle this status on or off.");
				}

				lore.add("");

				// Add description broken down into 40 width pieces.
				double charsPerLine = 40d;
				int pages = (int) Math.ceil(status.getDescription().length()/charsPerLine);
				for (int i = 1; i <= pages; i++) {
					int j = (int) ((i - 1) * charsPerLine);
					int k = (int) (i * charsPerLine);

					if (status.getDescription().length() < k) {
						k = status.getDescription().length();
					}
					lore.add(RPPersonas.SECONDARY_DARK + status.getDescription().substring(j, k));
				}

				meta.setLore(lore);
				item.setItemMeta(meta);
				return item;
			}

			@Override
			public void click(MenuAction menuAction) {
				if (status.isToggleable()) {
					entry.setEnabled(!entry.isEnabled());
				}

				if (entry.isEnabled()) {
					status.applyEffect(menuAction.getPlayer(), entry.getSeverity());
				} else {
					pers.disableStatus(status);
				}

				buildActiveStatusMenu(menu, pers);
			}
		};
	}

}