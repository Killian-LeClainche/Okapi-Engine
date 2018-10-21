package org.boilermake.digger;

public class Helper {

    public static boolean isColliding(Block p1, Block p2) {
        return (p1.getPosition().x - p1.getSize().x/2 < p2.getPosition().x + p2.getSize().x/2 &&
                p1.getPosition().x + p1.getSize().x/2 > p2.getPosition().x - p2.getSize().x/2 &&
                p1.getPosition().y - p1.getSize().y/2 < p2.getPosition().y + p2.getSize().y/2 &&
                p1.getPosition().y + p1.getSize().y/2 > p2.getPosition().y - p2.getSize().y/2);
    }

    public static boolean equals(Block p1, Block p2)
    {
        return (p1.getPosition().x == p2.getPosition().x &&
                p1.getPosition().x + p1.getSize().x == p2.getPosition().x + p2.getSize().x &&
                p1.getPosition().y == p2.getPosition().y &&
                p1.getPosition().y + p1.getSize().y == p2.getPosition().y + p2.getSize().y);
    }
}