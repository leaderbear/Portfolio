package Transport;

public abstract class Vehicule {

	private String nom;
	private String id;
	private Section[] sectionListe;
	
	public Vehicule(String id, String nom) {
		this.id = id;
		this.nom = nom;
	}

	/**
	 * 
	 * @param t
	 * @param rang�eD
	 * @param rang�eF
	 * @param prix
	 */
	public int createSection(String t, int rang�eD, int rang�eF, int prix) {
		// TODO - implement Vehicule.createSection
		throw new UnsupportedOperationException();
	}

	/**
	 * 
	 * @param type
	 * @param p
	 */
	public void editSection(String type, int prix) {
		// TODO - implement Vehicule.editSection
		throw new UnsupportedOperationException();
	}

	/**
	 * 
	 * @param type
	 */
	public void deleteSection(String type) {
		// TODO - implement Vehicule.deleteSection
		throw new UnsupportedOperationException();
	}

	/**
	 * 
	 * @param numeroSection
	 */
	public Section readSection(int numeroSection) {
		// TODO - implement Vehicule.readSection
		throw new UnsupportedOperationException();
	}

}