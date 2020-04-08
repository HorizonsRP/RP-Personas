package net.korvic.rppersonas.personas.aspects;

public enum PersonaSubRace {

	// HUMANS
	ARMUSIAN("Armusian", 90, PersonaRace.HUMAN),
	CARRIBAR("Carribar", 90, PersonaRace.HUMAN),
	GRAVICAN("Gravican", 90, PersonaRace.HUMAN);

	private String name;
	private int maxAge;
	private PersonaRace parentRace;

	PersonaSubRace(String name, int maxAge, PersonaRace parentRace) {
		this.name = name;
		this.maxAge = maxAge;
		this.parentRace = parentRace;
	}

	public String getName() {
		return name;
	}
	public int getMaxAge() {
		return maxAge;
	}
	public PersonaRace getParentRace() {
		return parentRace;
	}

	public static PersonaSubRace getByName(String name) {
		for (PersonaSubRace subrace : values()) {
			if (subrace.getName().equalsIgnoreCase(name)) {
				return subrace;
			}
		}
		return null;
	}
}