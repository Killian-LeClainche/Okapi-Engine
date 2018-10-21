package org.boilermake.digger;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2f;
import org.joml.Vector3f;
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
	public List<GameMap> mapList = new ArrayList<>();
	public List<HitBox> hitboxList = new ArrayList<>();
	public Controller player2;
	public Controller player3;
	public Controller player4;

	public DiggerWorld(@NotNull App application) {
		super(application);
	}
	
	@Override
	public void init() {
		//Player 1
		inputMap.put("rightP1", (Key)getSettings().get("p1:right"));
		inputMap.put("leftP1", (Key)getSettings().get("p1:left"));
		inputMap.put("digP1", (Key)getSettings().get("p1:dig"));
		inputMap.put("upP1", (Key)getSettings().get("p1:up"));
		inputMap.put("itemP1", (Key)getSettings().get("p1:item"));

		//Player 2
		player2 = (Controller)getSettings().get("p2");
		inputMap.put("rightP2", player2.getKeyDPadRight());
		inputMap.put("leftP2", player2.getKeyDPadLeft());
		inputMap.put("digP2", player2.getKeyX());
		inputMap.put("upP2", player2.getKeyA());
		inputMap.put("itemP2", player2.getKeyB());

		//Player 3
		player3 = (Controller)getSettings().get("p3");
		inputMap.put("rightP3", player3.getKeyDPadRight());
		inputMap.put("leftP3", player3.getKeyDPadLeft());
		inputMap.put("digP3", player3.getKeyX());
		inputMap.put("upP3", player3.getKeyA());
		inputMap.put("itemP3", player3.getKeyB());

		//Player 4
		player4 = (Controller)getSettings().get("p4");
		inputMap.put("rightP4", player4.getKeyDPadRight());
		inputMap.put("leftP4", player4.getKeyDPadLeft());
		inputMap.put("digP4", player4.getKeyX());
		inputMap.put("upP4", player4.getKeyA());
		inputMap.put("itemP4", player4.getKeyB());

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
		
		int randomVal = random(0,4);
		
		terrainList.addAll(mapList.get(randomVal).terrainList);
		graveList.addAll(mapList.get(randomVal).graveList);
		playerList.addAll(mapList.get(randomVal).playerList);

/*
		Vector2f PlayerCoord1 = new Vector2f(530, 450);
		Vector2f PlayerCoord2 = new Vector2f(1370, 450);
		Vector2f PlayerCoord3 = new Vector2f(1390, 1080);
		Vector2f PlayerCoord4 = new Vector2f(490, 1080);

		playerList.add(new Player(PlayerCoord1, new Vector3f(255, 255, 255)));
		//playerList.get(0).setItem(2);
		playerList.add(new Player(PlayerCoord2, new Vector3f(128, 34, 24)));
		playerList.add(new Player(PlayerCoord3, new Vector3f(44, 117, 51)));
		playerList.add(new Player(PlayerCoord4, new Vector3f(0, 0, 160)));
*/
		renderer.init();
	}

	private void checkKeysP1() {
		if(playerList.size() > 0) {
			if(inputMap.get("rightP1").isPressed()) {
				playerList.get(0).setIsGraveDigging(false);
				playerList.get(0).moveRight();
			}
			if(inputMap.get("leftP1").isPressed()) {
				playerList.get(0).setIsGraveDigging(false);
				playerList.get(0).moveLeft();
			}
			if(inputMap.get("upP1").isPressed()) {
				playerList.get(0).setIsGraveDigging(false);
				playerList.get(0).moveUp();
			}

			if(inputMap.get("itemP1").isClicked()) {
				playerList.get(0).setIsGraveDigging(false);
				HitBox h = playerList.get(0).useItem();
				hitboxList.add(h);
			}

			if(inputMap.get("digP1").isPressed()) {
				playerList.get(0).setIsGraveDigging(true);
			}

			if(!inputMap.get("rightP1").isPressed() && !inputMap.get("leftP1").isPressed()) {
				playerList.get(0).slow();
			}
		}
	}

	private void checkKeysP2() {
		if(playerList.size() > 1) {
			if (inputMap.get("rightP2").isPressed()) {
				playerList.get(1).setIsGraveDigging(false);
				playerList.get(1).moveRight();
			}
			if (inputMap.get("leftP2").isPressed()) {
				playerList.get(1).setIsGraveDigging(false);
				playerList.get(1).moveLeft();
			}
			if (inputMap.get("upP2").isPressed()) {
				playerList.get(1).setIsGraveDigging(false);
				playerList.get(1).moveUp();
			}

			if(inputMap.get("itemP2").isClicked()) {
				playerList.get(1).setIsGraveDigging(false);
				HitBox h = playerList.get(1).useItem();
				hitboxList.add(h);
			}

			if (inputMap.get("digP2").isPressed()) {
				playerList.get(1).setIsGraveDigging(true);
			}

			if (!inputMap.get("rightP2").isPressed() && !inputMap.get("leftP2").isPressed()) {
				playerList.get(1).slow();
			}
		}
	}

	private void checkKeysP3() {
		if(playerList.size() > 2) {
			if (inputMap.get("rightP3").isPressed()) {
				playerList.get(2).setIsGraveDigging(false);
				playerList.get(2).moveRight();
			}
			if (inputMap.get("leftP3").isPressed()) {
				playerList.get(2).setIsGraveDigging(false);
				playerList.get(2).moveLeft();
			}
			if (inputMap.get("upP3").isPressed()) {
				playerList.get(2).setIsGraveDigging(false);
				playerList.get(2).moveUp();
			}

			if(inputMap.get("itemP3").isClicked()) {
				playerList.get(2).setIsGraveDigging(false);
				HitBox h = playerList.get(2).useItem();
				hitboxList.add(h);
			}

			if (inputMap.get("digP3").isPressed()) {
				playerList.get(2).setIsGraveDigging(true);
			}

			if (!inputMap.get("rightP3").isPressed() && !inputMap.get("leftP3").isPressed()) {
				playerList.get(2).slow();
			}
		}
	}

	private void checkKeysP4() {
		if(playerList.size() > 3) {
			if (inputMap.get("rightP4").isPressed()) {
				playerList.get(3).setIsGraveDigging(false);
				playerList.get(3).moveRight();
			}
			if (inputMap.get("leftP4").isPressed()) {
				playerList.get(3).setIsGraveDigging(false);
				playerList.get(3).moveLeft();
			}
			if (inputMap.get("upP4").isPressed()) {
				playerList.get(3).setIsGraveDigging(false);
				playerList.get(3).moveUp();
			}

			if(inputMap.get("itemP4").isClicked()) {
				playerList.get(3).setIsGraveDigging(false);
				HitBox h = playerList.get(3).useItem();
				hitboxList.add(h);
			}

			if (inputMap.get("digP4").isPressed()) {
				playerList.get(3).setIsGraveDigging(true);
			}

			if (!inputMap.get("rightP4").isPressed() && !inputMap.get("leftP4").isPressed()) {
				playerList.get(3).slow();
			}
		}
	}

	@Override
	public void update() {
		super.update();

		player2.update();

		checkKeysP1();
		checkKeysP2();

		for (Player p : playerList) {
			if (!p.isDead()) {
				p.update();
				if (System.currentTimeMillis() - p.clickGraveTime > 160) {
					p.setHasClickedGrave(false);
				}
			}
		}

		for (int i = 0; i < playerList.size(); i++) {
			Player player = playerList.get(i);
			for (Player p : playerList) {
				if (!player.equals(p) && Helper.equals(player, p)) {
					System.out.println("here");
					if(player.velocity.x > 0) {
						player.getPosition().x -= 10;
						p.getPosition().x += 10;
					} else {
						player.getPosition().x += 10;
						p.getPosition().x -= 10;
					}
				}
			}
			for (Terrain t : terrainList) {
				if (!player.equals(t) && Helper.isColliding(player, t)) {
					//y-axis collisions             											 				           p2
					if (player.getPosition().y < t.getPosition().y - t.getSize().y / 2 && player.getVelocity().y > 0) { // p1
						player.setPosition(player.getPosition().x, t.getPosition().y - t.getSize().y / 2 - player.getSize().y / 2);
						player.stopY();
					} else if (player.getPosition().y > t.getPosition().y + t.getSize().y / 2 && player.getVelocity().y < 0) {      // p1
						player.setPosition(player.getPosition().x, t.getPosition().y + t.getSize().y / 2 + player.getSize().y / 2);// p2
						player.stopY();
						player.resetJumps();
					}
					//x-axis collisions
					else if (player.getPosition().x < t.getPosition().x - t.getSize().x / 2 && player.getVelocity().x > 0) { // p1 -> p2
						player.setPosition(t.getPosition().x - t.getSize().x / 2 - player.getSize().x / 2, player.getPosition().y);
						player.stopX();
					} else if (player.getPosition().x > t.getPosition().x + t.getSize().x / 2 && player.getVelocity().x < 0) { // p2 <- p1
						player.setPosition(t.getPosition().x + t.getSize().x / 2 + player.getSize().x / 2, player.getPosition().y);
						player.stopX();
					}
				}
			}
			List<Grave> tempGraveList = new ArrayList<>(graveList);
			for (Grave grave : graveList) {
				if (Helper.isColliding(player, grave) && player.isGrounded()) {
					if (i == 0 && inputMap.get("digP1").isClicked()) {
						grave.dig();
						player.startDig();
					} else if (i == 1 && inputMap.get("digP2").isClicked()) {
						grave.dig();
						player.startDig();
					}
					if (grave.isDug()) {
						itemList.add(grave.getReward());
						tempGraveList.remove(grave);
					}
				}
			}
			graveList = tempGraveList;
			List<Item> tempItemList = new ArrayList<>(itemList);
			for (Item item : itemList) {
				if (Helper.isColliding(player, item)) {
					player.setItem(item.getItemType());
					player.setDelay(item.getItemDelay());
					tempItemList.remove(item);
				}
			}
			itemList = tempItemList;

			List<HitBox> tempHitBoxList = new ArrayList<>(hitboxList);
			List<Player> tempPlayerList = new ArrayList<>(playerList);
			for(HitBox h : hitboxList)
			{
				for(Player p : playerList)
				{
					if(Helper.isColliding(h, p))
					{
						if(h.getOwner().equals(p))
						{
							continue;
						}

						else
						{
							p.setIsDead(true);
							tempPlayerList.remove(p);
						}
					}
				}
			}
			hitboxList = tempHitBoxList;
			playerList = tempPlayerList;
		}
	}

	@Override
	public void render(double delta) {
		renderer.render(delta);
	}
}
