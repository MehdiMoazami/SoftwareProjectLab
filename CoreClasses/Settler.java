



package CoreClasses;

//import Search.Border;
import java.util.ArrayList;
import java.util.Arrays;
import java.io.IOException;


import View.Menu;

public class Settler extends Traveler {

	private String name;
	private ArrayList<String> minedMinerals; // minedMinerals should be changed to a string ArrayList, explanation below
												// in checkRequiredMaterial
	public  ArrayList<TeleportationGate> gates;
	private long timeOfDeath;
	private boolean dead;
	private Asteroid currentAsteroid;
	private int nGate;


	public Settler(String name) {
		//super(currentAsteroid);

		this.name = name;
		this.timeOfDeath = 0;
		this.dead = false;
		minedMinerals = new ArrayList<String>();
		gates = new ArrayList<TeleportationGate>();
		nGate= 0;
	}

	public void setAsteroid(Asteroid asteroid) {
		this.currentAsteroid = asteroid;
	}

	public Asteroid getAsteroid() {
		return currentAsteroid;
	}
        
        

	public void travel(Asteroid asteroid) {
		
		if(this.getHidden()) {
		unhide();
		currentAsteroid.getUnhide();   
		// if we dont unhide asteroid, it gonna stay hollow false !!
		}
		
		setAsteroid(asteroid);
		Controller.updateAsteroid(this.currentAsteroid);

            
            
	}

	public void putGate() {
		if (!gates.get(0).isDeployed()) {
			gates.get(0).setGate(this.currentAsteroid);
			nGate--;
		} else if ( !gates.get(0).isPaired() && !gates.get(0).getNeighbour().equals(this.getAsteroid()) ) {
			gates.get(0).setGate(this.currentAsteroid, gates.get(1));
			nGate-- ;
			System.out.println(gates.get(0).getNeighbour().getID() + "<------------->" + gates.get(1).getNeighbour().getID()  );
			//gates = new ArrayList<>();
		}else{
			System.out.println("\n\n ============ you connot place a gate here");
		}

	}
	
	
    public void teleport(TeleportationGate tg) {
        if (tg.isPaired()) {
            currentAsteroid = tg.getPairedGate().getNeighbour();
        }
    }


	@Override
	public void drill() {
		this.currentAsteroid.getsDrill();
		Controller.updateAsteroid(this.currentAsteroid);
	}

	public void mine() {
		Mineral m = this.currentAsteroid.getsMine();
		if (m != null && this.getCapacityLeft() > 0) {
			// String pattern =
			this.minedMinerals.add(m.getClass().getSimpleName());
		}
		Controller.updateAsteroid(this.currentAsteroid);
	}

	public void fill(String mineral) {
		for (String m : minedMinerals) {
			if (m.equalsIgnoreCase(mineral)) {
				switch (mineral) {
				case "Uranium":
					this.currentAsteroid.getsFill(new Uranium());
					break;
				case "WaterIce":
					this.currentAsteroid.getsFill(new WaterIce());
					break;
				case "Carbon":
					this.currentAsteroid.getsFill(new Carbon());
					break;
				case "Iron":
					this.currentAsteroid.getsFill(new Iron());
					break;
				}
				this.minedMinerals.remove(m);
				break;
			}
		}
		Controller.updateAsteroid(this.currentAsteroid);
	}

	public boolean revive(Settler S) {
		long elapsedTime = System.currentTimeMillis() - S.getTimeOfDeath();
		if (elapsedTime < 10000) {
			S.setTimeOfDeath(0);
			Controller.updateSettler();
			return true;
		} else {
			return false;
		}
	}

	// minedMinerals needs to be a string arrayList to allow calling method
	// containsAll(), or else this part of the code will be much more complicated
	public boolean checkRequiredMaterial(int craftable) {
		ArrayList<Boolean> flags = new ArrayList<Boolean>();
		boolean canCraft = false;
		ArrayList<String> temp = new ArrayList<String>(minedMinerals);
		switch (craftable) {
		case 0:
			flags.add((temp.indexOf("Uranium")) != -1); // Uranium
			flags.add((temp.indexOf("Carbon")) != -1); // Carbon
			flags.add((temp.indexOf("Iron")) != -1); // Iron

			break;
		case 1:
			flags.add((temp.indexOf("Uranium")) != -1); // Uranium
			flags.add((temp.indexOf("WaterIce")) != -1); // WaterIce
			flags.add((temp.indexOf("Iron")) != -1); // Iron
			temp.remove(temp.indexOf("Iron"));
			flags.add((temp.indexOf("Iron")) != -1); // Iron

			break;
		}
		canCraft = flags.stream().reduce(true, (a, b) -> a && b);
		return canCraft;
	}

	// By making this method return craftable type we can get rid of craftRobot(),
	// craftTeleportationGate(), craftSpaceStation()
	public Craftable craft(int craftable) {
		Craftable c = null;
		//if (checkRequiredMaterial(craftable)) {

			switch (craftable) {
			case 0:
				c = new Robot(this.currentAsteroid);
				minedMinerals.remove(minedMinerals.indexOf("Uranium"));
				minedMinerals.remove(minedMinerals.indexOf("Carbon"));
				minedMinerals.remove(minedMinerals.indexOf("Iron"));
				break;
			case 1:
				if(this.craftGate()){
					this.putGate();
				}
				break;
			case 2:
				if (this.currentAsteroid.getSpaceStation() != null) {
					c = new SpaceStation(this.currentAsteroid);
				}
				break;
			default:
				return null;
			}
		//} else
		//	System.out.println("Not enough materials\n");
		return c;
	}

	public void showCraftMenu() throws IOException {
		ArrayList<String> menuItems = new ArrayList<String>();
		menuItems.add("Robot || Uranium + Carbon + Iron");
		menuItems.add("Teleportation Gate || Uranium + WaterIce + 2 x Iron");
		menuItems.add("SpaceStation || 3xUranium + 3xCarbon + 3xIron + 3xWaterIce");
		Menu menu = new Menu(menuItems);
		Craftable c = craft(menu.display());
		if (c != null)
			System.out.println(c.getClass().getSimpleName() + " Created successfully");
			
	}

	// Removed pairGates() method as gates are paired by default

	private void setTimeOfDeath() {
		this.timeOfDeath = System.currentTimeMillis();
	}

	public void setTimeOfDeath(long time) {
		this.timeOfDeath = time;
	}

	// might be useless
	public long getTimeOfDeath() {
		return timeOfDeath;
	}

	@Override
	public void die() {
		this.dead = true;
	}

	public boolean getDeath() {
		return this.dead;
	}

	// add to mine
	public int getCapacityLeft() {
		return 10 - this.minedMinerals.size();
	}

	public String getName() {
		return this.name;
	}

	public ArrayList<String> getMinerals() {
		return minedMinerals;
	}

	public void dying() {
		this.setTimeOfDeath();
	}

	public void hide() {
		if (this.currentAsteroid.getHide() == true) {
			this.hidden = true;
		}
	}
        

	public String viewInfo() {
		String str = "Name: " + this.name + "\t\tHidden:" + Boolean.toString(hidden) + "\nminedMinerals: "
				+ String.join(" - ", minedMinerals) + "\n#ofTeleportationGate: " + nGate;
		return str;
	}

	
	public boolean craftGate(){
		if(nGate==0){
			gates.add(new TeleportationGate());
			gates.add(new TeleportationGate());
			nGate =2;
			return true ;
		}
		return false;
	}

	public void setPairGate() {
		if(nGate==1)
		this.putGate();
	}

<<<<<<< Updated upstream:CoreClasses/Settler.java
	
	
=======
/////////////////////////////////////

	public int getNumberOfGates(){
		return nGate;
	}

	public void getMatforGate(){

		minedMinerals.add("Iron");
		minedMinerals.add("Iron");
		minedMinerals.add("Uranium");
		minedMinerals.add("WaterIce");

	}

	public void addMineral(String s){
		minedMinerals.add(s);
	}


>>>>>>> Stashed changes:AsteroidMiner/src/main/java/CoreClasses/Settler.java

}