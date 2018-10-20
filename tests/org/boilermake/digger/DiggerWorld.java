package org.boilermake.digger;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2f;
import polaris.okapi.App;
import polaris.okapi.options.Controller;
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
	}

	@Override
	public void render(double delta) {
		renderer.render(delta);
	}
}
