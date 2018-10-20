package org.boilermake.digger;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2f;
import polaris.okapi.App;
import polaris.okapi.options.Controller;
import polaris.okapi.options.Key;
import polaris.okapi.world.World;

import java.util.*;

/**
 * Created by Killian Le Clainche on 10/20/2018.
 */
public class DiggerWorld extends World {
	
	public DiggerRenderer renderer = new DiggerRenderer(this);
	public List<Terrain> terrainList = new ArrayList<>();
	public List<Grave> graveList = new ArrayList<>();
	public List<Player> playerList = new ArrayList<>();
	public Map<String, Key> inputMap = new TreeMap<>();
	public ArrayList<GameMap> mapList = new ArrayList<>();
	public Controller player2;
	public Controller player3;
	public Controller player4;

	public DiggerWorld(@NotNull App application) {
		super(application);
	}
	
	@Override
	public void init() {
		player2 = (Controller)getSettings().get("p2");
		inputMap.put("rightP1", (Key)getSettings().get("p1:right"));
		inputMap.put("leftP1", (Key)getSettings().get("p1:left"));
		inputMap.put("downP1", (Key)getSettings().get("p1:down"));
		inputMap.put("upP1", (Key)getSettings().get("p1:up"));
		inputMap.put("rightP2", player2.getKeyDPadRight());
		inputMap.put("leftP2", player2.getKeyDPadLeft());
		inputMap.put("downP2", player2.getKeyDPadDown());
		inputMap.put("upP2", player2.getKeyA());

		//terrainList.add(new Terrain());
		graveList.add(new Grave(0, 0, 10, null));

		playerList.add(new Player(new Vector2f(300, 200)));

		playerList.add(new Player(new Vector2f(1000, 200)));


		//renderer.init();

		/*

		Random rangen = new Random();
		ArrayList<Vector2f> graveCoords = new ArrayList<Vector2f>();
		int numBlocks = 0;

		while(numBlocks < 16) {

			int xcoord = rangen.nextInt(1600) + 80;
			int ycoord = rangen.nextInt(980) + 60;
			int xsize = rangen.nextInt(200) + 70;
			int ysize = rangen.nextInt(20) + 9;

			Terrain toAdd = new Terrain(xcoord, ycoord, xsize, ysize);

			boolean overlap = false;

			if(terrainList.isEmpty() == false) {
                for (Terrain t : terrainList) {
                    if(Helper.equals(t, toAdd) == false) {
                        if (Helper.isColliding(t, toAdd) == true) {
                            overlap = true;
                        }
                    }
                }
            }

			if(overlap == false)
			{
				terrainList.add(toAdd);
				numBlocks++;
			}
		}

		int numGraves = 0;

		int xval = rangen.nextInt((int)(terrainList.get(0).getPosition().x + terrainList.get(0).getSize().x)) + 1;
		int yval = (int)(terrainList.get(0).getPosition().y + 1.0);
		int diggingTime = rangen.nextInt(3) + 1;
		int item = rangen.nextInt(2) + 1;
		int xvelocity = rangen.nextInt(21) + 10;
		int yvelocity = rangen.nextInt(21) + 10;

		Grave graveToAdd = new Grave(xval, yval, diggingTime, new Item(xval, yval, xvelocity, yvelocity, item));
		graveList.add(graveToAdd);

		for(int i = 1; i < terrainList.size(); i++)
		{
			Terrain t = terrainList.get(i);
			int decision = rangen.nextInt(2);

			if(decision == 0)
			{
				xval = rangen.nextInt((int)(t.getPosition().x + t.getSize().x)) + 1;
				yval = (int)(t.getPosition().y + 1.0);

				diggingTime = rangen.nextInt(3) + 1;
				item = rangen.nextInt(2) + 1;
				xvelocity = rangen.nextInt(21) + 10;
				yvelocity = rangen.nextInt(21) + 10;

				graveToAdd = new Grave(xval, yval, diggingTime, new Item(xval, yval, xvelocity, yvelocity, item));
				graveList.add(graveToAdd);
			}

		}

		Vector2f PlayerCoord1 = new Vector2f(480, 270);
		Vector2f PlayerCoord2 = new Vector2f(1440, 270);
		Vector2f PlayerCoord3 = new Vector2f(480, 810);
		Vector2f PlayerCoord4 = new Vector2f(1440, 810);
		*/

		GameMap map1 = new GameMap("Map1");
		GameMap map2 = new GameMap("Map2");
		GameMap map3 = new GameMap("Map3");
		GameMap map4 = new GameMap("Map4");
		GameMap map5 = new GameMap("Map5");

		map1.generateMap();
		map2.generateMap();
		map3.generateMap();
		map4.generateMap();
		map5.generateMap();

		mapList.add(map1);
		mapList.add(map2);
		mapList.add(map3);
		mapList.add(map4);
		mapList.add(map5);

		//playerList.add(new Player(PlayerCoord1));
		//playerList.add(new Player(PlayerCoord2));
		//playerList.add(new Player(PlayerCoord3));
		//playerList.add(new Player(PlayerCoord4));

		renderer.init();
	}

	private void checkKeysP1() {
		if(inputMap.get("rightP1").isPressed()) {
			playerList.get(0).moveRight();
		}
		if(inputMap.get("leftP1").isPressed()) {
			playerList.get(0).moveLeft();
		}
		if(inputMap.get("upP1").isPressed()) {
			playerList.get(0).moveUp();
		}
		if(inputMap.get("downP1").isPressed()) {
			playerList.get(0).moveDown();
		}

		if(!inputMap.get("rightP1").isPressed() && !inputMap.get("leftP1").isPressed()) {
			playerList.get(0).slow();
		}
	}

	private void checkKeysP2() {
		if(inputMap.get("rightP2").isPressed()) {
			playerList.get(1).moveRight();
		}
		if(inputMap.get("leftP2").isPressed()) {
			playerList.get(1).moveLeft();
		}
		if(inputMap.get("upP2").isPressed()) {
			playerList.get(1).moveUp();
		}
		if(inputMap.get("downP2").isPressed()) {
			playerList.get(1).moveDown();
		}

		if(!inputMap.get("rightP2").isPressed() && !inputMap.get("leftP2").isPressed()) {
			playerList.get(1).slow();
		}
	}

	@Override
	public void update() {
		super.update();
		for(Key k : inputMap.values()) {
			k.update();
		}

		player2.update();

		checkKeysP1();
		checkKeysP2();

		for(Player p : playerList) {
			p.update();
		}

		for(Player p1 : playerList) {
			for(Player p2 : playerList) {
				if(!p1.equals(p2) && Helper.isColliding(p1, p2) ) {
					//x-axis collisions
					if(p1.getPosition().x < p2.getPosition().x && p1.getVelocity().x > 0) { // p1 -> p2
						p1.setPosition(p2.getPosition().x - p2.getSize().x, p1.getPosition().y);
						p1.stopX();
						p2.stopX();
					} else if(p1.getPosition().x > p2.getPosition().x && p1.getVelocity().x < 0) { // p2 <- p1
						p1.setPosition(p2.getPosition().x + p2.getSize().x, p1.getPosition().y);
						p1.stopX();
						p2.stopX();
					}

					//y-axis collisions             											 p2
					if(p1.getPosition().y < p2.getPosition().y && p1.getVelocity().y > 0) { // p1
						p1.setPosition(p1.getPosition().x, p2.getPosition().y - p2.getSize().y);
						p1.stopY();
						p2.stopY();
					} else if(p1.getPosition().y > p2.getPosition().y && p1.getVelocity().y < 0) { // p1
						p1.setPosition(p1.getPosition().x, p2.getPosition().y + p2.getSize().y);// p2
						p1.stopY();
						p2.stopY();
					}
				}
			}
		}
	}

	@Override
	public void render(double delta) {
		renderer.render(delta);
	}
}
