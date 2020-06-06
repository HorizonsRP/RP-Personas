package net.korvic.rppersonas.sql;

import net.korvic.rppersonas.RPPersonas;
import net.korvic.rppersonas.conversation.RezAppConvo;
import net.korvic.rppersonas.death.Altar;
import net.korvic.rppersonas.sql.util.DataMapFilter;
import net.korvic.rppersonas.sql.util.Errors;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;

public class RezAppSQL extends BaseSQL {

	private static final String SQL_TABLE_NAME = "rppersonas_rezapp";

	public static final String PERSONAID = "personaid";
	public static final String RESPONSES = "responses";
	public static final String KARMA = "karma";
	public static final String KILLS = "kills";
	public static final String DEATHS = "deaths";
	public static final String ALTAR = "altar";
	public static final String DENIED = "denied";

	public RezAppSQL(RPPersonas plugin) {
		if (BaseSQL.plugin == null) {
			BaseSQL.plugin = plugin;
		}

		String SQLTable = "CREATE TABLE IF NOT EXISTS " + SQL_TABLE_NAME + " (\n" +
						  "    KarmaID INT NOT NULL,\n" +
						  "    PersonaID INT NOT NULL,\n" +
						  "    Action TEXT NOT NULL,\n" +
						  "    Modifier REAL NOT NULL\n" +
						  ");";
		load(SQLTable, SQL_TABLE_NAME);
	}

	@Override
	protected boolean customStatement() {
		return false;
	}

	protected void addDataMappings() {
		DataMapFilter.addFilter(PERSONAID, PERSONAID, Integer.class);
		DataMapFilter.addFilter(RESPONSES, RESPONSES, RezAppConvo.RezAppResponses.class);
		DataMapFilter.addFilter(KARMA, KARMA, Integer.class);
		DataMapFilter.addFilter(KILLS, KILLS, Integer.class);
		DataMapFilter.addFilter(DEATHS, DEATHS, Integer.class);
		DataMapFilter.addFilter(ALTAR, ALTAR, Altar.class);
		DataMapFilter.addFilter(DENIED, DENIED, Boolean.class);
	}

	public void registerOrUpdate(DataMapFilter data) {
		if (data.containsKey(PERSONAID)) {
			if (!data.containsKey(KARMAID)) {
				data.put(KARMAID, getMaxKarmaID((int) data.get(PERSONAID)) + 1);
			}
			try {
				plugin.getSaveQueue().addToQueue(getSaveStatement(data));
			} catch (SQLException ex) {
				plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), ex);
			}
		}
	}

	public PreparedStatement getSaveStatement(DataMapFilter data) throws SQLException {
		Connection conn = null;
		PreparedStatement grabStatement = null;
		PreparedStatement replaceStatement = null;
		conn = getSQLConnection();

		grabStatement = conn.prepareStatement("SELECT * FROM " + SQL_TABLE_NAME + " WHERE PersonaID='" + data.get(PERSONAID) + "' AND KarmaID='" + data.get(KARMAID) + "'");
		ResultSet result = grabStatement.executeQuery();
		boolean resultPresent = result.next();

		conn = getSQLConnection();
		replaceStatement = conn.prepareStatement("REPLACE INTO " + SQL_TABLE_NAME + " (PersonaID,KarmaID,Action,Modifier) VALUES(?,?,?,?)");


		// Required
		replaceStatement.setInt(1, (int) data.get(PERSONAID));
		replaceStatement.setInt(2, (int) data.get(KARMAID));

		if (data.containsKey(ACTION)) {
			replaceStatement.setString(3, (String) data.get(ACTION));
		} else if (resultPresent) {
			replaceStatement.setString(3, result.getString("Action"));
		} else {
			replaceStatement.setString(3, null);
		}

		if (data.containsKey(MODIFIER)) {
			replaceStatement.setFloat(4, (float) data.get(MODIFIER));
		} else if (resultPresent) {
			replaceStatement.setFloat(4, result.getFloat("Modifier"));
		} else {
			replaceStatement.setFloat(4, 0);
		}

		grabStatement.close();
		return replaceStatement;
	}

	public void deleteByID(int personaID) {
		Connection conn = getSQLConnection();
		try {
			PreparedStatement statement = conn.prepareStatement("DELETE FROM " + SQL_TABLE_NAME + " WHERE PersonaID='" + personaID + "'");
			plugin.getSaveQueue().addToQueue(statement);
		} catch (SQLException ex) {
			plugin.getLogger().log(Level.SEVERE, "Unable to retreive connection", ex);
		}
	}

	public DataMapFilter getData(int personaID) {
		DataMapFilter data = new DataMapFilter().put(PERSONAID, personaID);
		Connection conn = getSQLConnection();
		try {
			PreparedStatement statement = conn.prepareStatement("SELECT * FROM " + SQL_TABLE_NAME + " WHERE PersonaID='" + personaID + "'");
			ResultSet rs = statement.executeQuery();

			if (rs.next()) {
				// TODO grab data after deciding on table format.
			}
		} catch (SQLException ex) {
			plugin.getLogger().log(Level.SEVERE, "Unable to retreive connection", ex);
		}
		return data;
	}
}
