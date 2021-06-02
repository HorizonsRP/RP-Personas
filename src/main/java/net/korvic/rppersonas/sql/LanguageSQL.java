package net.korvic.rppersonas.sql;

import net.korvic.rppersonas.RPPersonas;
import net.korvic.rppersonas.sql.util.DataMapFilter;
import net.korvic.rppersonas.sql.util.Errors;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

public class LanguageSQL extends BaseSQL {

	private static final String SQL_TABLE_NAME = "rppersonas_language";

	public static final String PERSONAID = "personaid";
	public static final String LANGUAGE = "language";
	public static final String LEVEL = "level";

	public LanguageSQL(RPPersonas plugin) {
		String SQLTable = "CREATE TABLE IF NOT EXISTS " + SQL_TABLE_NAME + " (\n" +
						  "    PersonaID INT NOT NULL,\n" +
						  "    Language TEXT NOT NULL,\n" +
						  "    Level SMALLINT NOT NULL\n" +
						  ");";
		createTable(SQLTable);
	}

	protected void addDataMappings() {
		DataMapFilter.addFilter(PERSONAID, PERSONAID, Integer.class);
		DataMapFilter.addFilter(LANGUAGE, LANGUAGE, String.class);
		DataMapFilter.addFilter(LEVEL, LEVEL, Short.class);
	}

	public Map<String, Short> getLanguages(int personaID) {
		String stmt = "SELECT * FROM " + SQL_TABLE_NAME + " WHERE PersonaID='" + personaID + "';";
		Map<String, Short> languageLevelMap = new HashMap<>();

		try (Connection conn = getSQLConnection();
			 PreparedStatement ps = conn.prepareStatement(stmt);
			 ResultSet rs = ps.executeQuery();) {

			while (rs.next()) {
				languageLevelMap.put(rs.getString("Language"), rs.getShort("Level"));
			}

		} catch (SQLException ex) {
			plugin.getLogger().log(Level.SEVERE, "Unable to retreive connection", ex);
		}

		return languageLevelMap;
	}

	public void registerOrUpdate(DataMapFilter data) {
		unregister(data);
		try (PreparedStatement stmt = getSaveStatement(data);) {
			plugin.getSaveQueue().executeWithNotification(stmt);
		} catch (SQLException ex) {
			plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), ex);
		}
	}

	public void unregister(DataMapFilter data) {
		try (PreparedStatement stmt = getDeleteLanguageStatement(data);) {
			plugin.getSaveQueue().executeWithNotification(stmt);
		} catch (SQLException ex) {
			plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), ex);
		}
	}

	public void purgeAll(int personaID) {
		try (PreparedStatement stmt = getDeleteStatementByPersonaID(personaID);) {
			plugin.getSaveQueue().executeWithNotification(stmt);
		} catch (SQLException ex) {
			plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), ex);
		}
	}

	public PreparedStatement getSaveStatement(DataMapFilter data) throws SQLException {
		if (data.containsKey(PERSONAID) && data.containsKey(LANGUAGE) && data.containsKey(LEVEL)) {
			try (Connection conn = getSQLConnection();) {
				PreparedStatement replaceStatement = conn.prepareStatement("INSERT INTO " + SQL_TABLE_NAME + " (PersonaID,Language,Level) VALUES(?,?,?)");

				replaceStatement.setInt(1, (int) data.get(PERSONAID));
				replaceStatement.setString(2, (String) data.get(LANGUAGE));
				replaceStatement.setShort(3, (short) data.get(LEVEL));
				return replaceStatement;
			} catch (SQLException ex) {
				plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), ex);
			}
		}
		return null;
	}

	public PreparedStatement getDeleteLanguageStatement(DataMapFilter data) {
		if (data.containsKey(PERSONAID) && data.containsKey(LANGUAGE)) {
			try (Connection conn = getSQLConnection();) {
				return conn.prepareStatement("DELETE FROM " + SQL_TABLE_NAME + " WHERE PersonaID='" + data.get(PERSONAID) + "' AND Language='" + data.get(LANGUAGE) + "'");
			} catch (SQLException ex) {
				plugin.getLogger().log(Level.SEVERE, "Unable to retreive connection", ex);
			}
		}
		return null;
	}

	public PreparedStatement getDeleteStatementByPersonaID(int personaID) {
		try (Connection conn = getSQLConnection();) {
			return conn.prepareStatement("DELETE FROM " + SQL_TABLE_NAME + " WHERE PersonaID='" + personaID + "'");
		} catch (SQLException ex) {
			plugin.getLogger().log(Level.SEVERE, "Unable to retreive connection", ex);
		}
		return null;
	}
}