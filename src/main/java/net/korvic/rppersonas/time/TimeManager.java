package net.korvic.rppersonas.time;

import lombok.Getter;
import lombok.Setter;
import net.korvic.rppersonas.RPPersonas;
import org.bukkit.GameRule;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;

public class TimeManager {
	public static final long WEEK_IN_MILLIS = RPPersonas.DAY_IN_MILLIS * 7;
	public static final long MONTH_IN_MILLIS = RPPersonas.DAY_IN_MILLIS * 30;
	public static final long YEAR_IN_MILLIS = RPPersonas.DAY_IN_MILLIS * 365;

	private static Map<World, TimeManager> managers = new HashMap<>();

	// STATIC CONVERTERS //
	public static long getCurrentTime() {
		return (RPPersonas.BASE_LONG_VALUE + System.currentTimeMillis());
	}

	public static long getMillisFromAge(int ages) {
		return (getCurrentTime() - (ages * 2 * WEEK_IN_MILLIS));
	}

	public static long getMillisFromEra(int eras) {
		return (getCurrentTime() - (eras * 8 * WEEK_IN_MILLIS));
	}

	public static int getRelativeAges(long millis) {
		return (int) (((getCurrentTime() - millis) / WEEK_IN_MILLIS) / 2);
	}

	public static int getRelativeEras(long millis) {
		return (int) (((getCurrentTime() - millis) / WEEK_IN_MILLIS) / 8);
	}

	public static String getRelativeTimeString(long millis) {
		return (getRelativeAges(millis) + " Ages; (" + getRelativeEras(millis) + " Eras)");
	}

	public static TimeManager registerWorld(World world) {
		TimeManager output = new TimeManager(world);
		managers.put(world, output);
		return output;
	}

	public static void unregisterWorld(World world) {
		getManagerOfWorld(world).unregister();
	}

	public static TimeManager getManagerOfWorld(World world) {
		return managers.get(world);
	}

	// INSTANCE //
	@Getter private World world;
	@Setter @Getter private TimeState currentState;
	private BukkitRunnable currentRunnable;

	public TimeManager(World world) {
		world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);

		// TODO - CONFIG SAVING

		this.world = world;
		this.currentState = TimeState.getState((int) world.getTime());
		startRunnable();
	}

	protected void unregister() {
		stopRunnable();
		world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, true);
		managers.remove(this.world);

		// TODO - CONFIG UPDATE
	}

	protected void stopRunnable() {
		currentRunnable.cancel();
		new BukkitRunnable() {
			@Override
			public void run() {
				if (!currentRunnable.isCancelled()) {
					currentRunnable.cancel();
				}
			}
		}.runTaskLaterAsynchronously(RPPersonas.get(), 20);
	}

	protected void startRunnable() {
		TimeManager manager = this;
		this.currentRunnable = new BukkitRunnable() {
			private TimeManager timeManager = manager;

			@Override
			public void run() {
				World world = timeManager.getWorld();
				int newTime = (world.getTime() + 1 >= TimeState.ONE_DAY_TICKS) ? 0 : (int) (world.getTime() + 1);
				world.setTime(newTime);

				TimeState nextState = TimeState.getNext(timeManager.getCurrentState());
				if (world.getTime() >= nextState.getMinecraftTime() || world.getTime() == 0) {
					timeManager.setCurrentState(nextState);
				}

				timeManager.startRunnable();
			}
		};

		// TODO - Process based on season.
		float dayPercentage = (float) currentState.getSummerPrecent();
		dayPercentage /= 100;

		int delay = Math.round((TimeState.CYCLE_TICKS * dayPercentage) / TimeState.getTicksToNextState(currentState));
		currentRunnable.runTaskLater(RPPersonas.get(), delay);
	}
}