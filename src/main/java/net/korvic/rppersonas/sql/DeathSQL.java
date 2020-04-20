package net.korvic.rppersonas.sql;

import net.korvic.rppersonas.RPPersonas;
import net.korvic.rppersonas.sql.extras.DataBuffer;
import net.korvic.rppersonas.sql.extras.Errors;
import org.bukkit.Location;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

public class DeathSQL extends BaseSQL {

	private static final String SQL_TABLE_NAME = "rppersonas_deaths";

	public static final String DEATHID = "deathid";

	public static final String VICTIM_PERSONAID = "victimpersonaid";
	public static final String VICTIM_ACCOUNTID = "victimaccountid";
	public static final String VICTIM_UUID = "victimuuid";

	public static final String KILLER_PERSONAID = "killerpersonaid";
	public static final String KILLER_ACCOUNTID = "killeraccountid";
	public static final String KILLER_UUID = "killeruuid";

	public static final String LOCATION = "location";
	public static final String CREATED = "created";
	public static final String STAFF = "staff";
	public static final String REFUNDER = "refunder";

	public DeathSQL(RPPersonas plugin) {
		if (BaseSQL.plugin == null) {
			BaseSQL.plugin = plugin;
		}

		String SQLTable = "CREATE TABLE IF NOT EXISTS " + SQL_TABLE_NAME + " (\n" +
						  "    DeathID INT NOT NULL PRIMARY KEY,\n" +
						  "    VictimPersona INT NOT NULL,\n" +
						  "    VictimAccount INT NOT NULL,\n" +
						  "    VictimUUID TEXT NOT NULL,\n" +
						  "    KillerPersona INT NOT NULL,\n" +
						  "    KillerAccount INT NOT NULL,\n" +
						  "    KillerUUID TEXT NOT NULL,\n" +
						  "    World TEXT NOT NULL,\n" +
						  "    LocationX INT NOT NULL,\n" +
						  "    LocationY INT NOT NULL,\n" +
						  "    LocationZ INT NOT NULL,\n" +
						  "    Time BIGINT NOT NULL,\n" +
						  "    StaffInflicted BIT NOT NULL,\n" +
						  "    Refunder TEXT\n" +
						  ");";
		load(SQLTable, SQL_TABLE_NAME);
	}

	@Override
	protected boolean customStatement() {
		return false;
	}

	protected void addDataMappings() {
		DataBuffer.addMapping(DEATHID, DEATHID, Integer.class);

		DataBuffer.addMapping(VICTIM_PERSONAID, VICTIM_PERSONAID, Integer.class);
		DataBuffer.addMapping(VICTIM_ACCOUNTID, VICTIM_ACCOUNTID, Integer.class);
		DataBuffer.addMapping(VICTIM_UUID, VICTIM_UUID, UUID.class);

		DataBuffer.addMapping(KILLER_PERSONAID, KILLER_PERSONAID, Integer.class);
		DataBuffer.addMapping(KILLER_ACCOUNTID, KILLER_ACCOUNTID, Integer.class);
		DataBuffer.addMapping(KILLER_UUID, KILLER_UUID, UUID.class);

		DataBuffer.addMapping(LOCATION, LOCATION, Location.class);
		DataBuffer.addMapping(CREATED, CREATED, Long.class);
		DataBuffer.addMapping(STAFF, STAFF, Boolean.class);
		DataBuffer.addMapping(REFUNDER, REFUNDER, UUID.class);
	}

	// Updates or Inserts a new mapping for an account.
	public void registerOrUpdate(Map<Object, Object> data) {
		if (data.containsKey(DEATHID)) {
			try {
				plugin.getSaveQueue().addToQueue(getSaveStatement(data));
			} catch (SQLException ex) {
				plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), ex);
			}
		}
	}

	public PreparedStatement getSaveStatement(Map<Object, Object> data) throws SQLException {
		Connection conn = null;
		PreparedStatement replaceStatement = null;
		conn = getSQLConnection();

		conn = getSQLConnection();
		replaceStatement = conn.prepareStatement("REPLACE INTO " + SQL_TABLE_NAME + " (DeathID,VictimPersona,VictimAccount,VictimUUID,KillerPersona,KillerAccount,KillerUUID,World,LocationX,LocationY,LocationZ,Time,StaffInflicted,Refunder) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?)");


		// Required
		replaceStatement.setInt(1, (int) data.get(DEATHID));

		if (data.containsKey(VICTIM_PERSONAID)) {
			replaceStatement.setInt(2, (int) data.get(VICTIM_PERSONAID));
		} else {
			replaceStatement.setInt(2, 0);
		}

		if (data.containsKey(VICTIM_ACCOUNTID)) {
			replaceStatement.setInt(3, (int) data.get(VICTIM_ACCOUNTID));
		} else {
			replaceStatement.setInt(3, 0);
		}

		if (data.containsKey(VICTIM_UUID)) {
			replaceStatement.setString(4, data.get(VICTIM_UUID).toString());
		} else {
			replaceStatement.setString(4, null);
		}

		if (data.containsKey(KILLER_PERSONAID)) {
			replaceStatement.setInt(5, (int) data.get(KILLER_PERSONAID));
		} else {
			replaceStatement.setInt(5, 0);
		}

		if (data.containsKey(KILLER_ACCOUNTID)) {
			replaceStatement.setInt(6, (int) data.get(KILLER_ACCOUNTID));
		} else {
			replaceStatement.setInt(6, 0);
		}

		if (data.containsKey(KILLER_UUID)) {
			replaceStatement.setString(7, data.get(KILLER_UUID).toString());
		} else {
			replaceStatement.setString(7, null);
		}

		if (data.containsKey(LOCATION)) {
			Location loc = (Location) data.get(LOCATION);
			replaceStatement.setString(8, loc.getWorld().getName());
			replaceStatement.setInt(8, loc.getBlockX());
			replaceStatement.setInt(10, loc.getBlockY());
			replaceStatement.setInt(11, loc.getBlockZ());
		} else {
			replaceStatement.setString(8, null);
			replaceStatement.setInt(9, 0);
			replaceStatement.setInt(10, 0);
			replaceStatement.setInt(11, 0);
		}

		if (data.containsKey(CREATED)) {
			replaceStatement.setLong(12, (long) data.get(CREATED));
		} else {
			replaceStatement.setLong(12, System.currentTimeMillis());
		}

		if (data.containsKey(STAFF)) {
			replaceStatement.setBoolean(13, (boolean) data.get(STAFF));
		} else {
			replaceStatement.setBoolean(13, false);
		}

		if (data.containsKey(REFUNDER)) {
			replaceStatement.setString(14, data.get(REFUNDER).toString());
		} else {
			replaceStatement.setString(14, null);
		}

		return replaceStatement;
	}

}
