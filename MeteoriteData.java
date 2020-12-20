package project5;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;

/**
 * This class stores data from Meteorites in 3 different BSTs. The first one contains all 
 * valid Meteorite objects. The second one contains all Meteorites with valid mass ordered 
 * by mass (or natural order if mass is the same), the third contains all Meteorites with 
 * valid year ordered by year (or natural order if year is the same).
 * 
 * @author Jonason Wu
 * @version 12/3/2020
 */
public class MeteoriteData {	
	//Stores all the meteorite objects following natural ordering (by name, then by id)
	private BST<Meteorite> meteors;
	
	//Stores meteorite objects that have mass.
	//	Ordered based on mass. If mass is equal, then natural ordering
	private BST<Meteorite> byMass;
	
	//Stores meteorite objects that have year.
	//	Ordered based on year. If year is equal, then natural ordering
	private BST<Meteorite> byYear;
	
	/**
	 * Initializes the superclass and get 3 BST that holds {@link Meteorite} objects. One BST
	 * is sorted by natural ordering, one is sorted by Mass if it exists for the meteorite, 
	 * and one is sorted by Year, if it exists.
	 */
	public MeteoriteData () {
		meteors = new BST<>();
		byMass = new BST<>(new MassComparator());
		byYear = new BST<>(new YearComparator());
	}

	/**
	 * This method should add the given Meteorite object to this collection. This method 
	 * should perform in O(H) in which H is the height of the tree representing this 
	 * collection. It will at most add to 3 BST objects.
	 * 
	 * @param m - Meteorite object to add
	 * @return true if an equal Meteorite object is not already present. False if this 
	 * collection already contains an object equal to {@code m}.
	 * @throws NullPointerException if m is null
	 */
	public boolean add(Meteorite m) throws NullPointerException {
		if (m == null)
			throw new NullPointerException("Parameter passed in should not be null");
		if (meteors.add(m)) {
			//Meteorite is successfully added. Check to see whether to add to other BST's
			if (m.getMass() != -1) 
				//Mass is valid, so add it to the BST for mass
				byMass.add(m);
			if (m.getYear() != 0)
				//Year is valid, so add it to the BST for year
				byYear.add(m);
			return true;
		}
		return false;
	}

	/**
	 * This method should compare this collection to obj. The two collections are equal if 
	 * they are both MeteoriteData objects and if they contain exactly the same elements. 
	 * This method should perform in O(N) in which N is the number of Meteorite objects in 
	 * this collection.
	 * 
	 * @param obj - MeteoriteData object to compare
	 * @return true if the two collections are equal, false otherwise.
	 */
	@Override
	public boolean equals (Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (!(obj instanceof MeteoriteData)) return false;
		
		//Meteorite only needs to exist in both collections. Since meteors has all the 
		//	Meteorite objects of the collection, the other 2 BST objects do not need 
		//	to be checked.
		return this.meteors.equals(((MeteoriteData) obj).meteors);
	}
	
	
	
	/**
	 * This method should return an iterator over all Meteorite objects in this 
	 * collection in order specified by natural ordering of Meteorite objects.
	 * 
	 * @return an iterator over the elements in natural order
	 */
	public Iterator<Meteorite> iterator() {
		return this.meteors.iterator();
	}

	/**
	 * This method should remove an object equal to the given Meteorite object m from 
	 * this collection and return true such an object was present. If m is not in this 
	 * collection, the method should return false. The method should throw an instance 
	 * of NullPointerException if m is null. This method should perform in O(H) in 
	 * which H is the height of the tree representing this collection.
	 * 
	 * @param m - meteorite object to remove
	 * @return true if successfully removed. false if m is not in this collection
	 * @throws NullPointerException if m is null
	 */
	public boolean remove (Meteorite m) throws NullPointerException {
		if (m == null) 
			throw new NullPointerException("Parameter passed in should not be null");
		
		//Should only contain 1 meteorite in the ArrayList
		ArrayList<Meteorite> toRemove = this.meteors.getRange(m, m);
		//Conserve space if wanted:
		//toRemove.trimToSize();
		
		//If element to remove exists
		if (toRemove.size() > 0) {
			//Get the details of the meteorite to remove.
			Meteorite remove = toRemove.get(0);
			
			this.meteors.remove(remove);
			if (remove.getMass() != -1)
				this.byMass.remove(remove);
			if (remove.getYear() != 0)
				this.byYear.remove(remove);
			return true;
		}
		return false;
	}	
	
	
	/**
	 * This method should return a collection of all Meteorite objects with mass within delta 
	 * grams of the specified mass. Both values are specified in grams. The returned 
	 * collection should be organized based on the mass from smallest to largest, and for 
	 * meteorite objects with equal mass according to the natural ordering of the elements 
	 * (i.e., dictated by the compareTo method defined in the Meteorite class). This 
	 * method should perform in O(K+H) in which K is the number of Meteorite objects in the 
	 * returned collection and H is the height of the tree representing this collection 
	 * (not O(N) where N is the total number of all Meteorite objects).
	 * 
	 * @param mass the mass of the meteorite to find.
	 * @param delta the allowed range of error away from the mass.
	 * @return A {@link MeteoriteData} object with a tree inside. The tree contains
	 * all meteorites having mass within the range of {@code mass} plus or minus 
	 * {@code delta}. If tree is empty (no matches found), null is returned.
	 * @throws IllegalArgumentException if {@code mass} or {@code delta} is below 0
	 */
	public MeteoriteData getByMass (int mass, int delta) throws IllegalArgumentException {
		if (mass < 0 || delta < 0) {
			throw new IllegalArgumentException("Mass cannot be less than 0.");
		}
		//If there are no elements in the BST tree, there will not be any to return as well. 
		if (this.meteors.size() == 0)
			return null;
		
		//Lower range of comparing
		int lowRange;
		//Upper range of comparing
		int highRange = mass + delta;
		//The lowest valid value for mass is 1
		if (mass <= delta)
			lowRange = 1;
		else
			lowRange = mass - delta;
		
		//Get references of the first and last meteorite from natural ordering
		Meteorite first = this.meteors.first();
		Meteorite last = this.meteors.last();
		
		//Create new Meteorites and replace masses with the range of the mass
		Meteorite from = new Meteorite(first.getName(), first.getId());
		Meteorite to = new Meteorite(last.getName(), last.getId());
		from.setMass(lowRange);
		to.setMass(highRange);
		
		//Create MeteoriteData object to store the matching meteorites
		MeteoriteData same = new MeteoriteData();
		//Since iterator() iterates meteors BST, just add the BST returned into meteors.
		//	Adding to other BST objects is unneccesary.
		same.meteors = this.byMass.getRangeBST(from, to);
		if (same.meteors.isEmpty())
			return null;
		return same;
	}
	
	/**
	 * This method should return a Meteorite object whose landing site is nearest to the 
	 * specified location loc. 
	 * This method should perform in O(N) in which N is the total number of Meteorite 
	 * objects stored in this collection.
	 * 
	 * @param loc The {@link Location} to base on when finding the closest meteorite.
	 * @return the {@link Meteorite} that landed closest to {@code loc}. Returns null if size of 
	 * collection is 0.
	 * @throws IllegalArgumentException {@code loc} is null.
	 */
	public Meteorite getByLocation (Location loc) throws IllegalArgumentException {
		if (loc == null) {
			throw new IllegalArgumentException("Location is undefined.");
		}
		if (this.meteors.isEmpty()) {
			return null;
		}
		
		Iterator<Meteorite> itr = this.meteors.iterator();
		
		//Set up dummy distance
		double distance = -1.0;
		Meteorite closest = null;
		while (itr.hasNext()) {
			Meteorite current = itr.next();
			Location location = current.getLocation();
			//If location is null, skip the comparison
			if (location == null) {
				continue;
			} 
			else if (distance < 0) {
				//The first time a Meteorite has a location
				distance = loc.getDistance(location);
				closest = current;
			} 
			else {
				//Compare distances
				double compDist = loc.getDistance(location);
				if (distance > compDist) {
					distance = compDist;
					closest = current;
				}
			}
		}
		return closest;
	}
	
	/**
	 * This method should return a collection of all Meteorite objects that landed on Earth 
	 * on the year specified. The returned collection should be organized based on the year 
	 * from earliest to most recent, and for Meteorite objects with the same year according 
	 * to the natural ordering of the elements (i.e., dictated by the compareTo method 
	 * defined in the Meteorite class). 
	 * 
	 * This method should perform in O(K+H) in which K is the number of Meteorite objects 
	 * in the returned collection and H is the height of the tree representing this 
	 * collection (not O(N) where N is the total number of all Meteorite objects).
	 * 
	 * @param year the landing year of the meteorites to find.
	 * @return a {@link MeteoriteData} that has all the meteorites that landed on {@code year}
	 * @throws IllegalArgumentException year is less than 0
	 */
	public MeteoriteData getByYear (int year) throws IllegalArgumentException {
		if (year <= 0) {
			//0 is an invalid year for Meteorite, so there will not be any Meteorites to return.
			if (year == 0)
				return new MeteoriteData();
			throw new IllegalArgumentException("The year is invalid. " 
					+ "Need positive integer for year.");
		}
		//If there are no elements in the BST tree, there will not be any to return as well. 
		if (this.meteors.isEmpty())
			return new MeteoriteData();
		
		//Get the first and last meteorites of the collection of Meteorite objects
		Meteorite first = this.meteors.first();
		Meteorite last = this.meteors.last();
		
		//Create new Meteorites with the first and last and set the year to the desired year
		Meteorite from = new Meteorite(first.getName(), first.getId());
		Meteorite to = new Meteorite(last.getName(), last.getId());
		from.setYear(year);
		to.setYear(year);
		
		//Create MeteoriteData object to return
		MeteoriteData same = new MeteoriteData();
		//Get the collection of Meteorite objects that match the given year and place that 
		//	into BST collection named meteors.
		same.meteors = this.byYear.getRangeBST(from, to);
		//If there are no Meteorites that fit the criteria, return empty MeteoriteData() object
		if (same.meteors.isEmpty())
			return new MeteoriteData();
		return same;
	}	
	
	/**
	 * The comparator used for changing the natural ordering to order by mass instead. If mass 
	 * is the same, then order by natural ordering for the object.
	 * 
	 * @author Jonason Wu
	 */
	private class MassComparator implements Comparator<Meteorite> {
		@Override
		public int compare(Meteorite o1, Meteorite o2) {
			if (o1.getMass() > o2.getMass())
				return 1;
			else if (o1.getMass() < o2.getMass())
				return -1;
			else
				//Natural ordering if the Meteorites are equal in mass
				return o1.compareTo(o2);
		}
	}
	
	/**
	 * The comparator used for changing the natural ordering to order by year instead. If
	 * year is the same, then order by natural ordering for that object.
	 * 
	 * @author Jonason Wu
	 */
	private static class YearComparator implements Comparator<Meteorite> {
		@Override
		public int compare(Meteorite o1, Meteorite o2) {
			if (o1.getYear() > o2.getYear())
				return 1;
			else if (o1.getYear() < o2.getYear())
				return -1;
			else
				//Natural ordering if the Meteorites are equal in year
				return o1.compareTo(o2);
		}
	}
}
