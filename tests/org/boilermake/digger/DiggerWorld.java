package org.boilermake.digger;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2f;
import polaris.okapi.App;
import polaris.okapi.options.Key;
import polaris.okapi.render.Texture;
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

		inputMap.put("rightP1", (Key)getSettings().get("p1:right"));
		inputMap.put("leftP1", (Key)getSettings().get("p1:left"));
		inputMap.put("downP1", (Key)getSettings().get("p1:down"));
		inputMap.put("upP1", (Key)getSettings().get("p1:up"));
		//inputMap.put("p2", (Key)getSettings().get("p2"));

		//terrainList.add(new Terrain());
		graveList.add(new Grave(0, 0, 10, null));
		playerList.add(new Player(new Vector2f(300, 200)));
		
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
