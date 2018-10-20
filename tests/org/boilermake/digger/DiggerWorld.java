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
	Terrain[][] tmap = new Terrain[120][120];
	Grave[][] gmap = new Grave[120][120];

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

			int xcoord = rangen.nextInt(120);
			int ycoord = rangen.nextInt(120);
			int xsize = rangen.nextInt(10);
			int ysize = rangen.nextInt(6);
			Vector2f terrainCoord = new Vector2f(xcoord, ycoord);
			Vector2f terrainSize = new Vector2f(xcoord + xsize, ycoord + ysize);

			Terrain toAdd = new Terrain(xcoord, ycoord, xsize, ysize);

			boolean overlap = false;
			for (Terrain t : terrainList) {
				if (overlapping(toAdd, t) == true) {
					overlap = true;
					break;
				}
			}
		}


			int xval = rangen.nextInt(119) + 1;
			int yval = rangen.nextInt(119) + 1;

			gmap[xval][yval] = graveList.remove(0);
			Vector2f graveCoord = new Vector2f(xval * 16, yval * 9);

			Vector2f PlayerCoord1 = new Vector2f(480, 270);
			Vector2f PlayerCoord2 = new Vector2f(1440, 270);
			Vector2f PlayerCoord3 = new Vector2f(480, 810);
			Vector2f PlayerCoord4 = new Vector2f(1440, 810);


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
