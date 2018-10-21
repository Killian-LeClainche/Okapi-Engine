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
	public Controller player2C, player3C, player4C;
	public Player player1, player2, player3, player4;
	public Player winner;
	public boolean isEnded;


	public DiggerWorld(@NotNull App application) {
		super(application);
	}
	
	@Override
	public void init() {
	    winner = null;
	    isEnded = false;
		//Player 1
		inputMap.put("rightP1", (Key)getSettings().get("p1:right"));
		inputMap.put("leftP1", (Key)getSettings().get("p1:left"));
		inputMap.put("digP1", (Key)getSettings().get("p1:dig"));
		inputMap.put("upP1", (Key)getSettings().get("p1:up"));
		inputMap.put("itemP1", (Key)getSettings().get("p1:item"));

		//Player 2
		player2C = (Controller)getSettings().get("p2");
		inputMap.put("rightP2", player2C.getKeyDPadRight());
		inputMap.put("leftP2", player2C.getKeyDPadLeft());
		inputMap.put("digP2", player2C.getKeyX());
		inputMap.put("upP2", player2C.getKeyA());
		inputMap.put("itemP2", player2C.getKeyB());

		//Player 3
		player3C = (Controller)getSettings().get("p3");
		inputMap.put("rightP3", player3C.getKeyDPadRight());
		inputMap.put("leftP3", player3C.getKeyDPadLeft());
		inputMap.put("digP3", player3C.getKeyX());
		inputMap.put("upP3", player3C.getKeyA());
		inputMap.put("itemP3", player3C.getKeyB());

		//Player 4 - PS4 getLeftBumper/RightBumper = L1, getRightPad = R3, getUpPad = leftTrigger, getDownPad = X to the left
        //getKeyRightThumb = Options, getKeyLeftThumb = Share, getKeyBack = L2, getKeyStart = R2,
        player4C = (Controller)getSettings().get("p4");
        inputMap.put("rightP4", player4C.getKeyStart());
		inputMap.put("leftP4", player4C.getKeyBack());
		inputMap.put("digP4", player4C.getKeyA()); // square
		inputMap.put("upP4", player4C.getKeyB());
		inputMap.put("itemP4", player4C.getKeyX()); // circle

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
		
		int randomVal = random(0, 4);
		
		terrainList.addAll(mapList.get(randomVal).terrainList);
		graveList.addAll(mapList.get(randomVal).graveList);
		playerList.addAll(mapList.get(randomVal).playerList);

		player1 = playerList.get(0);
		player2 = playerList.get(1);
		player3 = playerList.get(2);
		player4 = playerList.get(3);
		
		renderer.init();
	}

	private void checkKeysP1() {
		if(playerList.size() > 0 && !player1.isDead()) {
			if(inputMap.get("rightP1").isPressed() && (!player1.isAttacking || !player1.isGrounded())) {
				player1.setIsGraveDigging(false);
				player1.moveRight();
			}
			if(inputMap.get("leftP1").isPressed() && (!player1.isAttacking || !player1.isGrounded())) {
                player1.setIsGraveDigging(false);
                player1.moveLeft();
			}
			if(inputMap.get("upP1").isPressed()) {
                player1.setIsGraveDigging(false);
                player1.moveUp();
			}

			if(inputMap.get("itemP1").isClicked()) {
                player1.setIsGraveDigging(false);
                player1.isAttacking = true;
                player1.attackTime = System.currentTimeMillis();
				HitBox h = player1.useItem();
				hitboxList.add(h);
			}

			if(!inputMap.get("rightP1").isPressed() && !inputMap.get("leftP1").isPressed()) {
                player1.slow();
			}
		}
	}

	private void checkKeysP2() {
		if(playerList.size() > 1 && !player2.isDead()) {
			if (inputMap.get("rightP2").isPressed()  && (!player2.isAttacking || !player2.isGrounded())) {
                player2.setIsGraveDigging(false);
                player2.moveRight();
			}
			if (inputMap.get("leftP2").isPressed()  && (!player2.isAttacking || !player2.isGrounded())) {
                player2.setIsGraveDigging(false);
                player2.moveLeft();
			}
			if (inputMap.get("upP2").isPressed()) {
                player2.setIsGraveDigging(false);
                player2.moveUp();
			}

			if(inputMap.get("itemP2").isClicked()) {
                player2.setIsGraveDigging(false);
                player2.isAttacking = true;
                player2.attackTime = System.currentTimeMillis();
				HitBox h = player2.useItem();
				hitboxList.add(h);
			}

			if (!inputMap.get("rightP2").isPressed() && !inputMap.get("leftP2").isPressed()) {
                player2.slow();
			}
		}
	}

	private void checkKeysP3() {
		if(playerList.size() > 2 && !player3.isDead()) {
			if (inputMap.get("rightP3").isPressed()  && (!player3.isAttacking || !player3.isGrounded())) {
                player3.setIsGraveDigging(false);
                player3.moveRight();
			}
			if (inputMap.get("leftP3").isPressed()  && (!player3.isAttacking || !player3.isGrounded())) {
                player3.setIsGraveDigging(false);
                player3.moveLeft();
			}
			if (inputMap.get("upP3").isPressed()) {
                player3.setIsGraveDigging(false);
                player3.moveUp();
			}

			if(inputMap.get("itemP3").isClicked()) {
                player3.setIsGraveDigging(false);
                player3.isAttacking = true;
                player3.attackTime = System.currentTimeMillis();
				HitBox h = player3.useItem();
				hitboxList.add(h);
			}

			if (!inputMap.get("rightP3").isPressed() && !inputMap.get("leftP3").isPressed()) {
                player3.slow();
			}
		}
	}

	private void checkKeysP4() {
		if(playerList.size() > 3 && !player4.isDead()) {
			if (inputMap.get("rightP4").isPressed() && (!player4.isAttacking || !player4.isGrounded())) {
				player4.setIsGraveDigging(false);
                player4.moveRight();
			}
			if (inputMap.get("leftP4").isPressed() && (!player4.isAttacking || !player4.isGrounded())) {
                player4.setIsGraveDigging(false);
                player4.moveLeft();
			}
			if (inputMap.get("upP4").isPressed()) {
                player4.setIsGraveDigging(false);
                player4.moveUp();
			}

			if(inputMap.get("itemP4").isClicked()) {
                player4.setIsGraveDigging(false);
                player4.isAttacking = true;
                player4.attackTime = System.currentTimeMillis();
				HitBox h = player4.useItem();
				hitboxList.add(h);
			}

			if (!inputMap.get("rightP4").isPressed() && !inputMap.get("leftP4").isPressed()) {
                player4.slow();
			}
		}
	}

	@Override
	public void update() {
		super.update();

		player2C.update();
		player3C.update();
		player4C.update();

		checkKeysP1();
		checkKeysP2();
		checkKeysP3();
		checkKeysP4();

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
						player.setPosition(player.getPosition().x, t.getPosition().y - player.getSize().y / 2);
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
				    //TODO: condense this else if into one block (should be easy)
					if (i == 0 && inputMap.get("digP1").isClicked()) {
						grave.dig();
						player.startDig();
                        if(grave.isDug()) {
                            player.setIsGraveDigging(false);
                        } else {
                            player.setIsGraveDigging(true);
                        }
					} else if (i == 1 && inputMap.get("digP2").isClicked()) {
						grave.dig();
						player.startDig();
                        if(grave.isDug()) {
                            player.setIsGraveDigging(false);
                        } else {
                            player.setIsGraveDigging(true);
                        }
					} else if (i == 2 && inputMap.get("digP3").isClicked()) {
						grave.dig();
						player.startDig();
                        if(grave.isDug()) {
                            player.setIsGraveDigging(false);
                        } else {
                            player.setIsGraveDigging(true);
                        }
					} else if (i == 3 && inputMap.get("digP3").isClicked()) {
						grave.dig();
						player.startDig();
                        if(grave.isDug()) {
                            player.setIsGraveDigging(false);
                        } else {
                            player.setIsGraveDigging(true);
                        }
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
					if(h != null && Helper.isColliding(h, p))
					{
						if(h.getOwner().equals(p))
						{
							continue;
						}
						else
						{
							p.setIsDead(true);
							p.update();
							tempPlayerList.remove(p);
							if(tempPlayerList.size() == 1) {
							    winner = tempPlayerList.get(0);
							    isEnded = true;
                            }
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
