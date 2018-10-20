package org.boilermake.digger;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import polaris.okapi.App;
import polaris.okapi.options.Key;
import polaris.okapi.render.Texture;
import polaris.okapi.world.Vector;
import polaris.okapi.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

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
		inputMap.put("rightP1", (Key)getSettings().get("p1:action:right"));
		inputMap.put("leftP1", (Key)getSettings().get("p1:action:left"));
		inputMap.put("downP1", (Key)getSettings().get("p1:action:down"));
		inputMap.put("upP1", (Key)getSettings().get("p1:action:up"));
		inputMap.put("jumpP1", (Key)getSettings().get("p1:action:jump"));
		inputMap.put("rightP2", (Key)getSettings().get("p2:action:right"));
		inputMap.put("leftP2", (Key)getSettings().get("p2:action:left"));
		inputMap.put("downP2", (Key)getSettings().get("p2:action:down"));
		inputMap.put("upP2", (Key)getSettings().get("p2:action:up"));
		inputMap.put("jumpP2", (Key)getSettings().get("p2:action:jump"));

		terrainList.add(new Terrain(0, 0));
		graveList.add(new Grave(0, 0, 10, null));
		playerList.add(new Player(new Vector(300, 200, 30)));
		
		renderer.init();

	}
	
	@Override
	public void update() {
		for(Key k : inputMap.values()) {
			k.update();
		}
	}
	
	@Override
	public void render(double delta) {
		renderer.render(delta);
	}
}
