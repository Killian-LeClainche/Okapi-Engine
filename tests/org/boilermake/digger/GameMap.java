package org.boilermake.digger;

import java.util.*;
import org.joml.Vector2f;
import org.joml.Vector3f;

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
            int ysize = 40;

            this.terrainList.add(new Terrain(300, 80, xsize, ysize));
            this.terrainList.add(new Terrain(490, 330, xsize, ysize));
            this.terrainList.add(new Terrain(300, 800, xsize, ysize));
            //this.terrainList.add(new Terrain(490, 650, xsize, ysize));

            this.terrainList.add(new Terrain(960, 510, xsize+100, ysize));

            this.terrainList.add(new Terrain(1650, 80, xsize, ysize));
            this.terrainList.add(new Terrain(1420, 330, xsize, ysize));
            this.terrainList.add(new Terrain(1650, 800, xsize, ysize));
            //this.terrainList.add(new Terrain(1420, 650, xsize, ysize));

            Random rangen = new Random();
            Item temp = new Item(360, 130, rangen.nextInt(40), rangen.nextInt(40), rangen.nextInt(7)+1);
            this.graveList.add(new Grave(360, 130, rangen.nextInt(3) + 1, temp));

            temp = new Item(590, 380, rangen.nextInt(40), rangen.nextInt(40), rangen.nextInt(7)+1);
            this.graveList.add(new Grave(590, 380, rangen.nextInt(3) + 1, temp));

            temp = new Item(360, 852, rangen.nextInt(40), rangen.nextInt(40), rangen.nextInt(7)+1);
            this.graveList.add(new Grave(360, 852, rangen.nextInt(3) + 1, temp));

            temp = new Item(960, 560, rangen.nextInt(40), rangen.nextInt(40), rangen.nextInt(7)+1);
            this.graveList.add(new Grave(960, 560, rangen.nextInt(3) + 1, temp));

            temp = new Item(1700, 130, rangen.nextInt(40), rangen.nextInt(40), rangen.nextInt(7)+1);
            this.graveList.add(new Grave(1700, 130, rangen.nextInt(3) + 1, temp));

            temp = new Item(1320, 380, rangen.nextInt(40), rangen.nextInt(40), rangen.nextInt(7)+1);
            this.graveList.add(new Grave(1320, 380, rangen.nextInt(3) + 1, temp));

            temp = new Item(1700, 852, rangen.nextInt(40), rangen.nextInt(40), rangen.nextInt(7)+1);
            this.graveList.add(new Grave(1700, 852, rangen.nextInt(3) + 1, temp));

            playerList.add(new Player(new Vector2f(240, 450), new Vector3f(255, 255, 255)));
            playerList.add(new Player(new Vector2f(1600, 450), new Vector3f(128, 34, 24)));
            playerList.add(new Player(new Vector2f(240, 1080), new Vector3f(44, 117, 51)));
            playerList.add(new Player(new Vector2f(1600, 1080), new Vector3f(0, 0, 160)));
        }

        if(mapName.equals("Map2"))
        {
            int xsize = 1920;
            int ysize = 70;

            for(int i = 0; i < 10; i++)
            {
                this.terrainList.add(new Terrain(960, 620 + (i*50), 50, 25));
            }
            //this.terrainList.add(new Terrain(960, 680, 50, 50));
            this.terrainList.add(new Terrain(480, 550, 780, ysize));
            this.terrainList.add(new Terrain(1440, 550, 780, ysize));
            this.terrainList.add(new Terrain(960, 30, xsize, 50));


            Random rangen = new Random();

            Item temp = new Item(450, 615, rangen.nextInt(40), rangen.nextInt(40), rangen.nextInt(7)+1);
            this.graveList.add(new Grave(450, 615, rangen.nextInt(3) + 1, temp));

            temp = new Item(1440, 615, rangen.nextInt(40), rangen.nextInt(40), rangen.nextInt(7)+1);
            this.graveList.add(new Grave(1440, 615, rangen.nextInt(3) + 1, temp));

            temp = new Item(960, 85, rangen.nextInt(40), rangen.nextInt(40), rangen.nextInt(7)+1);
            this.graveList.add(new Grave(960, 85, rangen.nextInt(3) + 1, temp));

            playerList.add(new Player(new Vector2f(200, 1080), new Vector3f(255, 255, 255)));
            playerList.add(new Player(new Vector2f(1200, 1080), new Vector3f(128, 34, 24)));
            playerList.add(new Player(new Vector2f(700, 1080), new Vector3f(44, 117, 51)));
            playerList.add(new Player(new Vector2f(1700, 1080), new Vector3f(0, 0, 160)));
        }

        if (mapName.equals("Map3"))
        {
            int ysize = 25;
            this.terrainList.add(new Terrain(350, 870, 500, ysize));
            this.terrainList.add(new Terrain(720, 640, 180, ysize));
            this.terrainList.add(new Terrain(240, 510, 320, ysize));
            this.terrainList.add(new Terrain(450, 270, 400, ysize));
            this.terrainList.add(new Terrain(1470, 200, 700, ysize));
            this.terrainList.add(new Terrain(750, 40, 270, ysize));
            this.terrainList.add(new Terrain(1180, 545, 400, ysize));
            this.terrainList.add(new Terrain(1650, 810, 600, ysize));

            Random rangen = new Random();

            Item temp = new Item(780, 84, rangen.nextInt(40), rangen.nextInt(40), rangen.nextInt(7)+1);
            this.graveList.add(new Grave(780, 84, rangen.nextInt(3) + 1, temp));

            temp = new Item(1500, 244, rangen.nextInt(40), rangen.nextInt(40), rangen.nextInt(7)+1);
            this.graveList.add(new Grave(1500, 244, rangen.nextInt(3) + 1, temp));

            temp = new Item(500, 314, rangen.nextInt(40), rangen.nextInt(40), rangen.nextInt(7)+1);
            this.graveList.add(new Grave(500, 314, rangen.nextInt(3) + 1, temp));

            temp = new Item(1100, 590, rangen.nextInt(40), rangen.nextInt(40), rangen.nextInt(7)+1);
            this.graveList.add(new Grave(1100, 590, rangen.nextInt(3) + 1, temp));

            temp = new Item(1710, 854, rangen.nextInt(40), rangen.nextInt(40), rangen.nextInt(7)+1);
            this.graveList.add(new Grave(1710, 854, rangen.nextInt(3) + 1, temp));

            temp = new Item(390, 914, rangen.nextInt(40), rangen.nextInt(40), rangen.nextInt(7)+1);
            this.graveList.add(new Grave(390, 914, rangen.nextInt(3) + 1, temp));

            playerList.add(new Player(new Vector2f(480, 450), new Vector3f(255, 255, 255)));
            playerList.add(new Player(new Vector2f(1440, 270), new Vector3f(128, 34, 24)));
            playerList.add(new Player(new Vector2f(480, 1080), new Vector3f(44, 117, 51)));
            playerList.add(new Player(new Vector2f(1440, 1080), new Vector3f(0, 0, 160)));

        }

        if(mapName.equals("Map4"))
        {
            int ysize = 25;
            this.terrainList.add(new Terrain(200, 100, 400, ysize));
            this.terrainList.add(new Terrain(430, 410, 195, ysize));
            this.terrainList.add(new Terrain(670, 340, 160, ysize));
            this.terrainList.add(new Terrain(500, 800, 600, ysize));
            this.terrainList.add(new Terrain(970, 180, 260, ysize));
            this.terrainList.add(new Terrain(1040, 500, 200, ysize));
            this.terrainList.add(new Terrain(1340, 380, 200, ysize));
            this.terrainList.add(new Terrain(1640, 330, 240, ysize));
            this.terrainList.add(new Terrain(1530, 820, 300, ysize));

            Random rangen = new Random();

            Item temp = new Item(270, 145, rangen.nextInt(40), rangen.nextInt(40), rangen.nextInt(7)+1);
            this.graveList.add(new Grave(270, 145, rangen.nextInt(3) + 1, temp));

            temp = new Item(490, 545, rangen.nextInt(40), rangen.nextInt(40), rangen.nextInt(7)+1);
            this.graveList.add(new Grave(1040, 545, rangen.nextInt(3) + 1, temp));

            temp = new Item(1070, 225, rangen.nextInt(40), rangen.nextInt(40), rangen.nextInt(7)+1);
            this.graveList.add(new Grave(1020, 225, rangen.nextInt(3) + 1, temp));

            temp = new Item(1700, 375, rangen.nextInt(40), rangen.nextInt(40), rangen.nextInt(7)+1);
            this.graveList.add(new Grave(1640, 375, rangen.nextInt(3) + 1, temp));

            temp = new Item(250, 845, rangen.nextInt(40), rangen.nextInt(40), rangen.nextInt(7)+1);
            this.graveList.add(new Grave(250, 845, rangen.nextInt(3) + 1, temp));

            temp = new Item(1600, 865, rangen.nextInt(40), rangen.nextInt(40), rangen.nextInt(7)+1);
            this.graveList.add(new Grave(1600, 865, rangen.nextInt(3) + 1, temp));

            playerList.add(new Player(new Vector2f(220, 450), new Vector3f(255, 255, 255)));
            playerList.add(new Player(new Vector2f(1700, 700), new Vector3f(128, 34, 24)));
            playerList.add(new Player(new Vector2f(200, 1080), new Vector3f(44, 117, 51)));
            playerList.add(new Player(new Vector2f(1660, 1080), new Vector3f(0, 0, 160)));

        }

        if(mapName.equals("Map5"))
        {
            //eye
            this.terrainList.add(new Terrain(590, 690, 300, 120));
            this.terrainList.add(new Terrain(590, 740, 120, 120));

            //right eye
            this.terrainList.add(new Terrain(1290, 690, 300, 120));
            this.terrainList.add(new Terrain(1290, 740, 120, 120));

            //nose
            this.terrainList.add(new Terrain(940, 330, 300, 70));
            this.terrainList.add(new Terrain(940, 370, 120, 190));

            //mouth and jaw
            this.terrainList.add(new Terrain(950, 20, 1000, 100));
            this.terrainList.add(new Terrain(610, 100, 60, 100));
            this.terrainList.add(new Terrain(940, 100, 60, 100));
            this.terrainList.add(new Terrain(1270, 100, 60, 100));
            this.terrainList.add(new Terrain(1427, 120, 45, 150));
            this.terrainList.add(new Terrain(473, 120, 45, 150));
            this.terrainList.add(new Terrain(375, 230, 150, 70));
            this.terrainList.add(new Terrain(1525, 230, 150, 70));

            Random rangen = new Random();

            Item temp = new Item(590, 833, rangen.nextInt(40), rangen.nextInt(40), rangen.nextInt(7)+1);
            this.graveList.add(new Grave(590, 833, rangen.nextInt(3) + 1, temp));

            temp = new Item(1290, 833, rangen.nextInt(40), rangen.nextInt(40), rangen.nextInt(7)+1);
            this.graveList.add(new Grave(1290, 833, rangen.nextInt(3) + 1, temp));

            temp = new Item(940, 497, rangen.nextInt(40), rangen.nextInt(40), rangen.nextInt(7)+1);
            this.graveList.add(new Grave(940, 497, rangen.nextInt(3) + 1, temp));

            temp = new Item(610, 183, rangen.nextInt(40), rangen.nextInt(40), rangen.nextInt(7)+1);
            this.graveList.add(new Grave(610, 183, rangen.nextInt(3) + 1, temp));

            temp = new Item(940, 183, rangen.nextInt(40), rangen.nextInt(40), rangen.nextInt(7)+1);
            this.graveList.add(new Grave(940, 183, rangen.nextInt(3) + 1, temp));

            temp = new Item(1270, 183, rangen.nextInt(40), rangen.nextInt(40), rangen.nextInt(7)+1);
            this.graveList.add(new Grave(1270, 183, rangen.nextInt(3) + 1, temp));

            playerList.add(new Player(new Vector2f(530, 450), new Vector3f(255, 255, 255)));
            playerList.add(new Player(new Vector2f(1370, 450), new Vector3f(128, 34, 24)));
            playerList.add(new Player(new Vector2f(1390, 1080), new Vector3f(44, 117, 51)));
            playerList.add(new Player(new Vector2f(490, 1080), new Vector3f(0, 0, 160)));

        }
    }
}
