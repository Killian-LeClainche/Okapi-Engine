package org.boilermake.digger;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import polaris.okapi.App;
import polaris.okapi.world.World;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Killian Le Clainche on 10/20/2018.
 */
public class DiggerWorld extends World {
	
	DiggerRenderer renderer = new DiggerRenderer(this);
	List<Terrain> terrainList = new ArrayList<>();
	List<Grave> graveList = new ArrayList<>();
	List<Player> playerList = new ArrayList<>();
	
	public DiggerWorld(@NotNull App application) {
		super(application);
	}
	
	@Override
	public void init() {
		terrainList.add(new Terrain());
		graveList.add(new Grave());
		playerList.add(new Player());
	}
	
	@Override
	public void update() {
	
	}
	
	@Override
	public void render(double delta) {
		renderer.render(delta);
	}
}
