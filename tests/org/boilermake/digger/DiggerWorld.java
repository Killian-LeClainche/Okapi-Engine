package org.boilermake.digger;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2f;
import polaris.okapi.App;
import polaris.okapi.options.Controller;
import polaris.okapi.options.Key;
import polaris.okapi.world.World;

import java.util.*;

import static polaris.okapi.util.MathHelperKt.random;

/**
 * Created by Killian Le Clainche on 10/20/2018.
 */
public class DiggerWorld extends World {
	
	public DiggerRenderer renderer = new DiggerRenderer(this);
	public List<Terrain> terrainList = new ArrayList<>();
	public List<Grave> graveList = new ArrayList<>();
	public List<Item> itemList = new ArrayList<>();
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
		inputMap.put("digP1", (Key)getSettings().get("p1:dig"));
		inputMap.put("upP1", (Key)getSettings().get("p1:up"));
		inputMap.put("rightP2", player2.getKeyDPadRight());
		inputMap.put("leftP2", player2.getKeyDPadLeft());
		inputMap.put("digP2", player2.getKeyDPadDown());
		inputMap.put("upP2", player2.getKeyA());

		playerList.add(new Player(new Vector2f(300, 200)));
		playerList.add(new Player(new Vector2f(1000, 600)));

		Vector2f PlayerCoord1 = new Vector2f(480, 270);
		Vector2f PlayerCoord2 = new Vector2f(1440, 270);
		Vector2f PlayerCoord3 = new Vector2f(480, 810);
		Vector2f PlayerCoord4 = new Vector2f(1440, 810);

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
		
		int randomVal = 1;
		
		terrainList.addAll(mapList.get(randomVal).terrainList);
		graveList.addAll(mapList.get(randomVal).graveList);
		

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
			if(!p.isDead()) {
				p.update();
			}
		}

		for(int i = 0; i < playerList.size(); i++) {
			Player player = playerList.get(i);
			for(Player p : playerList) {
				if (!player.equals(p) && Helper.isColliding(player, p)) {
					//x-axis collisions
					if (player.getPosition().x < p.getPosition().x && player.getVelocity().x > 0) { // player -> p
						player.setPosition(p.getPosition().x - p.getSize().x, player.getPosition().y);
						player.stopX();
						p.stopX();
					} else if (player.getPosition().x > p.getPosition().x && player.getVelocity().x < 0) { // p <- player
						player.setPosition(p.getPosition().x + p.getSize().x, player.getPosition().y);
						player.stopX();
						p.stopX();
					}
					//y-axis collisions             											 p
					else if (player.getPosition().y < p.getPosition().y && player.getVelocity().y > 0) { // player
						player.setPosition(player.getPosition().x, p.getPosition().y - p.getSize().y);
						player.stopY();
						p.stopY();
					} else if (player.getPosition().y > p.getPosition().y && player.getVelocity().y < 0) { // player
						player.setPosition(player.getPosition().x, p.getPosition().y + p.getSize().y);// p
						player.stopY();
						p.stopY();
					}
				}
			}
			for(Terrain t : terrainList) {
				if(!player.equals(t) && Helper.isColliding(player, t) ) {
					//y-axis collisions             											 				           p2
					if (player.getPosition().y < t.getPosition().y - t.getSize().y/2 && player.getVelocity().y > 0) { // p1
						player.setPosition(player.getPosition().x, t.getPosition().y - t.getSize().y/2 - player.getSize().y/2);
						player.stopY();
					} else if (player.getPosition().y > t.getPosition().y + t.getSize().y/2 && player.getVelocity().y < 0) {      // p1
						player.setPosition(player.getPosition().x, t.getPosition().y + t.getSize().y/2 + player.getSize().y/2);// p2
						player.stopY();
						player.resetJumps();
					}
					//x-axis collisions
					else if (player.getPosition().x < t.getPosition().x - t.getSize().x/2 && player.getVelocity().x > 0) { // p1 -> p2
						player.setPosition(t.getPosition().x - t.getSize().x/2 - player.getSize().x/2, player.getPosition().y);
						player.stopX();
					} else if (player.getPosition().x > t.getPosition().x + t.getSize().x/2 && player.getVelocity().x < 0) { // p2 <- p1
						player.setPosition(t.getPosition().x + t.getSize().x/2 + player.getSize().x/2, player.getPosition().y);
						player.stopX();
					}
				}
			}
			List<Grave> tempGraveList = new ArrayList<>(graveList);
			for(Grave grave : graveList) {
				if(Helper.isColliding(player, grave) && player.isGrounded()) {
					if(i == 0 && inputMap.get("digP1").isPressed()) {
						grave.dig();
					}
					else if(i == 0 && inputMap.get("digP2").isPressed()) {
						grave.dig();
					}
					if(grave.isDug()) {
						itemList.add(grave.getReward());
						tempGraveList.remove(grave);
					}
				}
			}
			graveList = tempGraveList;
			List<Item> tempItemList = new ArrayList<>(itemList);
			for(Item item : itemList) {
				if(Helper.isColliding(player, item)) {
					player.setItem(item.getItemType());
					player.setDelay(item.getItemDelay());
					tempItemList.remove(item);
				}
			}
			itemList = tempItemList;
		}
	}

	@Override
	public void render(double delta) {
		renderer.render(delta);
	}
}
