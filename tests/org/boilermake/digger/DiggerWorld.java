package org.boilermake.digger;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import polaris.okapi.App;
import polaris.okapi.world.World;

/**
 * Created by Killian Le Clainche on 10/20/2018.
 */
public class DiggerWorld extends World {
	
	public DiggerWorld(@NotNull App application) {
		super(application);
	}
	
	@Override
	public void update() {
		System.out.println("this");
	}
}
