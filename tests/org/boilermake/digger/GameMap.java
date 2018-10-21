package org.boilermake.digger;

import java.util.*;

public class GameMap {

    public List<Terrain> terrainList = new ArrayList<>();
    public List<Grave> graveList = new ArrayList<>();
    public List<Player> playerList = new ArrayList<>();
    public String mapName;

    public GameMap(String name)
    {
        mapName = name;
    }

    public void generateMap()
    {
        if(mapName.equals("Map1"))
        {

            int xsize = 260;
            int ysize = 50;

            this.terrainList.add(new Terrain(100, 180, xsize, ysize));
            this.terrainList.add(new Terrain(280, 360, xsize, ysize));
            this.terrainList.add(new Terrain(1140, 180, xsize, ysize));
            this.terrainList.add(new Terrain(1320, 360, xsize, ysize));

            this.terrainList.add(new Terrain(460, 540, xsize+100, ysize));

            this.terrainList.add(new Terrain(100, 720, xsize, ysize));
            this.terrainList.add(new Terrain(280, 900, xsize, ysize));
            this.terrainList.add(new Terrain(1140, 720, xsize, ysize));
            this.terrainList.add(new Terrain(1320, 900, xsize, ysize));

            Random rangen = new Random();
            Item temp = new Item(180, 180, rangen.nextInt(40), rangen.nextInt(40), rangen.nextInt(11));
            this.graveList.add(new Grave(180, 180, rangen.nextInt(3) + 1, temp));

            temp = new Item(410, 360, rangen.nextInt(40), rangen.nextInt(40), rangen.nextInt(11));
            this.graveList.add(new Grave(410, 360, rangen.nextInt(3) + 1, temp));

            temp = new Item(1270, 180, rangen.nextInt(40), rangen.nextInt(40), rangen.nextInt(11));
            this.graveList.add(new Grave(1270, 180, rangen.nextInt(3) + 1, temp));

            temp = new Item(1460, 360, rangen.nextInt(40), rangen.nextInt(40), rangen.nextInt(11));
            this.graveList.add(new Grave(1460, 360, rangen.nextInt(3) + 1, temp));

            temp = new Item(590, 540, rangen.nextInt(40), rangen.nextInt(40), rangen.nextInt(11));
            this.graveList.add(new Grave(190, 540, rangen.nextInt(3) + 1, temp));

            temp = new Item(180, 720, rangen.nextInt(40), rangen.nextInt(40), rangen.nextInt(11));
            this.graveList.add(new Grave(180, 720, rangen.nextInt(3) + 1, temp));

            temp = new Item(410, 900, rangen.nextInt(40), rangen.nextInt(40), rangen.nextInt(11));
            this.graveList.add(new Grave(410, 900, rangen.nextInt(3) + 1, temp));

            temp = new Item(1270, 720, rangen.nextInt(40), rangen.nextInt(40), rangen.nextInt(11));
            this.graveList.add(new Grave(1270, 720, rangen.nextInt(3) + 1, temp));

            temp = new Item(1460, 900, rangen.nextInt(40), rangen.nextInt(40), rangen.nextInt(11));
            this.graveList.add(new Grave(1460, 900, rangen.nextInt(3) + 1, temp));

        }

        if(mapName.equals("Map2"))
        {
            int xsize = 1500;
            int ysize = 100;

            this.terrainList.add(new Terrain(200, 200, xsize, ysize));
            this.terrainList.add(new Terrain(200, 400, xsize, ysize));
            this.terrainList.add(new Terrain(200, 800, xsize, ysize));

            Random rangen = new Random();

            Item temp = new Item(960, 200, rangen.nextInt(40), rangen.nextInt(40), rangen.nextInt(11));
            this.graveList.add(new Grave(960, 200, rangen.nextInt(3) + 1, temp));

            temp = new Item(960, 400, rangen.nextInt(40), rangen.nextInt(40), rangen.nextInt(11));
            this.graveList.add(new Grave(960, 400, rangen.nextInt(3) + 1, temp));

            temp = new Item(960, 800, rangen.nextInt(40), rangen.nextInt(40), rangen.nextInt(11));
            this.graveList.add(new Grave(960, 800, rangen.nextInt(3) + 1, temp));
        }

        if (mapName.equals("Map3"))
        {
            this.terrainList.add(new Terrain(200, 160, 400, 90));
            this.terrainList.add(new Terrain(720, 340, 180, 70));
            this.terrainList.add(new Terrain(180, 510, 320, 90));
            this.terrainList.add(new Terrain(450, 700, 400, 90));
            this.terrainList.add(new Terrain(1070, 200, 700, 90));
            this.terrainList.add(new Terrain(240, 885, 270, 70));
            this.terrainList.add(new Terrain(910, 615, 400, 90));
            this.terrainList.add(new Terrain(800, 900, 600, 90));

            Random rangen = new Random();

            Item temp = new Item(300, 160, rangen.nextInt(40), rangen.nextInt(40), rangen.nextInt(11));
            this.graveList.add(new Grave(300, 160, rangen.nextInt(3) + 1, temp));

            temp = new Item(800, 340, rangen.nextInt(40), rangen.nextInt(40), rangen.nextInt(11));
            this.graveList.add(new Grave(800, 340, rangen.nextInt(3) + 1, temp));

            temp = new Item(1420, 200, rangen.nextInt(40), rangen.nextInt(40), rangen.nextInt(11));
            this.graveList.add(new Grave(1420, 200, rangen.nextInt(3) + 1, temp));

            temp = new Item(375, 885, rangen.nextInt(40), rangen.nextInt(40), rangen.nextInt(11));
            this.graveList.add(new Grave(375, 885, rangen.nextInt(3) + 1, temp));

            temp = new Item(650, 700, rangen.nextInt(40), rangen.nextInt(40), rangen.nextInt(11));
            this.graveList.add(new Grave(650, 700, rangen.nextInt(3) + 1, temp));

            temp = new Item(1100, 900, rangen.nextInt(40), rangen.nextInt(40), rangen.nextInt(11));
            this.graveList.add(new Grave(1100, 900, rangen.nextInt(3) + 1, temp));
        }

        if(mapName.equals("Map4"))
        {
            this.terrainList.add(new Terrain(200, 100, 400, 90));
            this.terrainList.add(new Terrain(430, 410, 135, 80));
            this.terrainList.add(new Terrain(200, 900, 100, 70));
            this.terrainList.add(new Terrain(500, 800, 600, 90));
            this.terrainList.add(new Terrain(970, 140, 200, 90));
            this.terrainList.add(new Terrain(1040, 500, 140, 80));
            this.terrainList.add(new Terrain(1340, 430, 150, 90));
            this.terrainList.add(new Terrain(1640, 330, 150, 90));
            this.terrainList.add(new Terrain(1530, 820, 300, 90));

            Random rangen = new Random();

            Item temp = new Item(300, 100, rangen.nextInt(40), rangen.nextInt(40), rangen.nextInt(11));
            this.graveList.add(new Grave(300, 100, rangen.nextInt(3) + 1, temp));

            temp = new Item(490, 410, rangen.nextInt(40), rangen.nextInt(40), rangen.nextInt(11));
            this.graveList.add(new Grave(490, 410, rangen.nextInt(3) + 1, temp));

            temp = new Item(1070, 140, rangen.nextInt(40), rangen.nextInt(40), rangen.nextInt(11));
            this.graveList.add(new Grave(1070, 140, rangen.nextInt(3) + 1, temp));

            temp = new Item(1700, 330, rangen.nextInt(40), rangen.nextInt(40), rangen.nextInt(11));
            this.graveList.add(new Grave(1700, 330, rangen.nextInt(3) + 1, temp));

            temp = new Item(250, 900, rangen.nextInt(40), rangen.nextInt(40), rangen.nextInt(11));
            this.graveList.add(new Grave(250, 900, rangen.nextInt(3) + 1, temp));

            temp = new Item(1700, 820, rangen.nextInt(40), rangen.nextInt(40), rangen.nextInt(11));
            this.graveList.add(new Grave(1700, 820, rangen.nextInt(3) + 1, temp));
        }

        if(mapName.equals("Map5"))
        {
            this.terrainList.add(new Terrain(640, 190, 300, 90));
            this.terrainList.add(new Terrain(1280, 190, 300, 90));
            this.terrainList.add(new Terrain(930, 505, 60, 70));
            this.terrainList.add(new Terrain(500, 650, 90, 150));
            this.terrainList.add(new Terrain(500, 800, 1000, 100));
            this.terrainList.add(new Terrain(1410, 650, 90, 150));

            Random rangen = new Random();

            Item temp = new Item(790, 190, rangen.nextInt(40), rangen.nextInt(40), rangen.nextInt(11));
            this.graveList.add(new Grave(790, 190, rangen.nextInt(3) + 1, temp));

            temp = new Item(1430, 190, rangen.nextInt(40), rangen.nextInt(40), rangen.nextInt(11));
            this.graveList.add(new Grave(1430, 190, rangen.nextInt(3) + 1, temp));

            temp = new Item(960, 505, rangen.nextInt(40), rangen.nextInt(40), rangen.nextInt(11));
            this.graveList.add(new Grave(960, 505, rangen.nextInt(3) + 1, temp));

            temp = new Item(545, 650, rangen.nextInt(40), rangen.nextInt(40), rangen.nextInt(11));
            this.graveList.add(new Grave(545, 650, rangen.nextInt(3) + 1, temp));

            temp = new Item(700, 800, rangen.nextInt(40), rangen.nextInt(40), rangen.nextInt(11));
            this.graveList.add(new Grave(700, 800, rangen.nextInt(3) + 1, temp));

            temp = new Item(960, 800, rangen.nextInt(40), rangen.nextInt(40), rangen.nextInt(11));
            this.graveList.add(new Grave(960, 800, rangen.nextInt(3) + 1, temp));

            temp = new Item(1220, 800, rangen.nextInt(40), rangen.nextInt(40), rangen.nextInt(11));
            this.graveList.add(new Grave(1220, 800, rangen.nextInt(3) + 1, temp));

            temp = new Item(1460, 650, rangen.nextInt(40), rangen.nextInt(40), rangen.nextInt(11));
            this.graveList.add(new Grave(1460, 650, rangen.nextInt(3) + 1, temp));

        }
    }
}
