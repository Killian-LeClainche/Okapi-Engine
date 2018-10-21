package org.boilermake.digger;

public class Helper {

    private static final float tolerance = 2f;
    public static boolean isColliding(Block p1, Block p2) {
        return (p1.getPosition().x - p1.getSize().x/2 < p2.getPosition().x + p2.getSize().x/2 &&
                p1.getPosition().x + p1.getSize().x/2 > p2.getPosition().x - p2.getSize().x/2 &&
                p1.getPosition().y - p1.getSize().y/2 < p2.getPosition().y + p2.getSize().y/2 &&
                p1.getPosition().y + p1.getSize().y/2 > p2.getPosition().y - p2.getSize().y/2);
    }

    public static boolean equals(Block p1, Block p2)
    {
        return (Math.abs(p1.getPosition().x - p2.getPosition().x) < tolerance &&
                Math.abs(p1.getPosition().x + p1.getSize().x - p2.getPosition().x - p2.getSize().x) < tolerance  &&
                Math.abs(p1.getPosition().y - p2.getPosition().y) < tolerance &&
                Math.abs(p1.getPosition().y + p1.getSize().y - p2.getPosition().y - p2.getSize().y) < tolerance);
    }
}
