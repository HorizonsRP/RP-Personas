package net.korvic.rppersonas.sql;

import net.korvic.rppersonas.RPPersonas;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.*;
import java.util.*;
import java.util.logging.Level;

public class UUIDAccountMapSQL {

	public static Connection connection;

	private RPPersonas plugin;
	private String SQLTable;
	private String SQLTableName = "rppersonas_uuid_account_map";

	public UUIDAccountMapSQL(RPPersonas plugin) {
		this.plugin = plugin;
		SQLTable = "CREATE TABLE IF NOT EXISTS " + SQLTableName + " (\n" +
				   "    UUID VARCHAR(255) NOT NULL PRIMARY KEY,\n" +
				   "    AccountID INT NOT NULL\n" +
				   ");";
	}

	public void load() {
		connection = getSQLConnection();
		try {
			Statement s = connection.createStatement();
			s.execute(SQLTable);
			s.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		initialize();
		runConnectionMaintainer();
	}

	public void initialize(){
		connection = getSQLConnection();
		try {
			String stmt;
			stmt = "SELECT * FROM " + SQLTableName + ";";
			PreparedStatement ps = connection.prepareStatement(stmt);
			ResultSet rs = ps.executeQuery();
			close(ps, rs);
		} catch (SQLException ex) {
			plugin.getLogger().log(Level.SEVERE, "Unable to retreive connection", ex);
		}
	}

	public Connection getSQLConnection() {
		String host = plugin.getConfig().getString("mysql.host");
		String port = plugin.getConfig().getString("mysql.port");
		String database = plugin.getConfig().getString("mysql.database");
		String user = plugin.getConfig().getString("mysql.user");
		String password = plugin.getConfig().getString("mysql.password");

		try {
			if (connection != null && !connection.isClosed()) {
				return connection;
			}
			String url = "jdbc:mysql://" + host + ":" + port + "/" + database;
			return DriverManager.getConnection(url, user, password);
		} catch (SQLException ex) {
			if (RPPersonas.DEBUGGING) {
				plugin.getLogger().log(Level.SEVERE, "MySQL exception on initialize", ex);
			}
		}

		return null;
	}

	private void runConnectionMaintainer() {
		(new BukkitRunnable() {
			@Override
			public void run() {
				try {
					if (connection != null && !connection.isClosed()) {
						connection.createStatement().execute("SELECT 1");
					}
				} catch (SQLException e) {
					connection = getSQLConnection();
				}
			}
		}).runTaskTimerAsynchronously(plugin, 60 * 20, 60 * 20);
	}

	private void close(PreparedStatement ps, ResultSet rs){
		try {
			if (ps != null)
				ps.close();
			if (rs != null)
				rs.close();
		} catch (SQLException ex) {
			Errors.close(plugin, ex);
		}
	}

	// Retrieves the amount of tokens a player has, as per our database.
	public List<UUID> getUUIDsOf(int accountID) {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			conn = getSQLConnection();
			String stmt;
			stmt = "SELECT * FROM " + SQLTableName + " WHERE AccountID='" + accountID + "';";

			ps = conn.prepareStatement(stmt);
			rs = ps.executeQuery();

			List<UUID> result = new ArrayList<>();
			while (rs.next()) {
				result.add(UUID.fromString(rs.getString("UUID")));
			}
			return result;
		} catch (SQLException ex) {
			plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), ex);
		} finally {
			try {
				if (ps != null)
					ps.close();
			} catch (SQLException ex) {
				plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionClose(), ex);
			}
		}
		return null;
	}

}
