package org.boilermake.digger;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2f;
import polaris.okapi.App;
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

	public DiggerWorld(@NotNull App application) {
		super(application);
	}
	
	@Override
	public void init() {

		inputMap.put("rightP1", (Key)getSettings().get("p1:right"));
		inputMap.put("leftP1", (Key)getSettings().get("p1:left"));
		inputMap.put("downP1", (Key)getSettings().get("p1:down"));
		inputMap.put("upP1", (Key)getSettings().get("p1:up"));
		//inputMap.put("p2", (Key)getSettings().get("p2"));

		//terrainList.add(new Terrain());
		graveList.add(new Grave(0, 0, 10, null));
		playerList.add(new Player(new Vector2f(300, 200)));


		Random rangen = new Random();
		ArrayList<Vector2f> graveCoords = new ArrayList<Vector2f>();
		int numBlocks = 0;

		while(numBlocks < 16) {

			int xcoord = rangen.nextInt(1800) + 80;
			int ycoord = rangen.nextInt(980) + 60;
			int xsize = rangen.nextInt(40) + 10;
			int ysize = rangen.nextInt(20) + 9;

			Terrain toAdd = new Terrain(xcoord, ycoord, xsize, ysize);

			boolean overlap = false;
			for (Terrain t : terrainList) {
				if (Helper.isColliding(t, toAdd) == true) {
					overlap = true;
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

		Grave toAdd = new Grave(xval, yval, diggingTime, new Item(xval, yval, xvelocity, yvelocity, item));
		Vector2f graveCoord = new Vector2f(xval * 16, yval * 9);

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

				Grave graveToAdd = new Grave(xval, yval, diggingTime, new Item(xval, yval, xvelocity, yvelocity, item));
				graveList.add(graveToAdd);
			}

		}

		Vector2f PlayerCoord1 = new Vector2f(480, 270);
		Vector2f PlayerCoord2 = new Vector2f(1440, 270);
		Vector2f PlayerCoord3 = new Vector2f(480, 810);
		Vector2f PlayerCoord4 = new Vector2f(1440, 810);

		playerList.add(new Player(PlayerCoord1));
		playerList.add(new Player(PlayerCoord2));
		playerList.add(new Player(PlayerCoord3));
		playerList.add(new Player(PlayerCoord4));

		renderer.init();
	}
	
	@Override
	public void update() {

		super.update();
		for(Key k : inputMap.values()) {
			k.update();
		}

		for(Player p : playerList) {
			p.update();
		}

			if(inputMap.get("rightP1").isPressed()) {
				playerList.get(0).moveRight();
			}
			if(inputMap.get("leftP1").isPressed()) {
				playerList.get(0).moveLeft();
			}
			if(!inputMap.get("rightP1").isPressed() && !inputMap.get("leftP1").isPressed()) {
				playerList.get(0).slow();
			}
	}
	
	@Override
	public void render(double delta) {
		renderer.render(delta);
	}
}
