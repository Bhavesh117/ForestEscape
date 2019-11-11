import java.awt.*;
import java.awt.geom.AffineTransform;
import java.io.*;
import java.util.ArrayList;
import javax.swing.ImageIcon;


public class ResourceManager {

    private ArrayList tiles;
    private int currentMap;
    private GraphicsConfiguration gc;


    private ArrayList <Sprite> environment;
    private Sprite playerSprite;
    private Sprite goalSprite;
    private Sprite diamondSprite;
    private Sprite grubSprite;


    public ResourceManager(GraphicsConfiguration gc) {
        this.gc = gc;
        loadTileImages();
        loadCreatureSprites();
        loadPowerUpSprites();
        loadEnvTiles();
    }


    public Image loadImage(String name) {
        String filename = "images/" + name;
        return new ImageIcon(filename).getImage();
    }


    public Image getMirrorImage(Image image) {
        return getScaledImage(image, -1, 1);
    }


    public Image getFlippedImage(Image image) {
        return getScaledImage(image, 1, -1);
    }


    private Image getScaledImage(Image image, float x, float y) {


        AffineTransform transform = new AffineTransform();
        transform.scale(x, y);
        transform.translate(
                (x - 1) * image.getWidth(null) / 2,
                (y - 1) * image.getHeight(null) / 2);


        Image newImage = gc.createCompatibleImage(
                image.getWidth(null),
                image.getHeight(null),
                Transparency.BITMASK);


        Graphics2D g = (Graphics2D) newImage.getGraphics();
        g.drawImage(image, transform, null);
        g.dispose();

        return newImage;
    }


    public TileMap loadNextMap() {
        TileMap map = null;
        while (map == null) {
            currentMap++;
            try {
                map = loadMap(
                        "maps/map" + currentMap + ".txt");
            } catch (IOException ex) {
                if (currentMap == 1) {
                    System.out.println("hi1" + currentMap);
                    return null;
                }
                System.out.println("hi2" + currentMap);
                currentMap = 0;
                map = null;
            }
        }
        System.out.println("hi3" + currentMap);

        return map;
    }


    public TileMap reloadMap() {
        try {
            return loadMap(
                    "maps/map" + currentMap + ".txt");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }


    private TileMap loadMap(String filename)
            throws IOException {
        ArrayList lines = new ArrayList();
        int width = 0;
        int height = 0;


        BufferedReader reader = new BufferedReader(
                new FileReader(filename));
        while (true) {
            String line = reader.readLine();

            if (line == null) {
                reader.close();
                break;
            }


            if (!line.startsWith("#")) {
                lines.add(line);
                width = Math.max(width, line.length());
            }
        }


        height = lines.size();
        TileMap newMap = new TileMap(width, height);
        for (int y = 0; y < height; y++) {
            String line = (String) lines.get(y);
            for (int x = 0; x < line.length(); x++) {
                char ch = line.charAt(x);


                int tile = ch - 'A';
                if (tile >= 0 && tile < tiles.size()) {
                    newMap.setTile(x, y, (Image) tiles.get(tile));
                } else if (ch == 'o') {
                    addSprite(newMap, diamondSprite, x, y);
                } else if (ch == '*') {
                    addSprite(newMap, goalSprite, x, y);
                } else if (ch == '1') {
                    addSprite(newMap, grubSprite, x, y);
                } else if (ch == '3') {
                    addSprite(newMap, environment.get(0), x, y);
                } else if (ch == '4') {
                    addSprite(newMap, environment.get(1), x, y);
                } else if (ch == '5') {
                    addSprite(newMap, environment.get(2), x, y);
                } else if (ch == '6') {
                    addSprite(newMap, environment.get(3), x, y);
                } else if (ch == '7') {
                    addSprite(newMap, environment.get(4), x, y);
                } else if (ch == '8') {
                    addSprite(newMap, environment.get(5), x, y);
                } else if (ch == '9') {
                    addSprite(newMap, environment.get(6), x, y);
                }
            }
        }


        Sprite player = (Sprite) playerSprite.clone();
        player.setX(TileMapRenderer.tilesToPixels(3));
        player.setY(0);
        newMap.setPlayer(player);

        return newMap;
    }


    private void addSprite(TileMap map,
                           Sprite hostSprite, int tileX, int tileY) {
        if (hostSprite != null) {

            Sprite sprite = (Sprite) hostSprite.clone();


            sprite.setX(
                    TileMapRenderer.tilesToPixels(tileX) +
                            (TileMapRenderer.tilesToPixels(1) -
                                    sprite.getWidth()) / 2);


            sprite.setY(
                    TileMapRenderer.tilesToPixels(tileY + 1) -
                            sprite.getHeight());


            map.addSprite(sprite);
        }

    }

    public void spawnSprite(TileMap map,
                            Sprite hostSprite, float xPos, float yPos) {
        if (hostSprite != null) {

            Sprite sprite = (Sprite) hostSprite.clone();

            sprite.setX(xPos);

            sprite.setY(yPos);

            map.addSprite(sprite);
        }
    }


    public void loadTileImages() {


        tiles = new ArrayList();
        char ch = 'A';
        while (true) {
            String name = "tile_" + ch + ".png";
            File file = new File("images/" + name);
            if (!file.exists()) {
                break;
            }
            tiles.add(loadImage(name));
            ch++;
        }
    }


    public void loadCreatureSprites() {

        Image[][] images = new Image[4][];


        images[0] = new Image[]{
                loadImage("i1.png"),
                loadImage("i2.png"),
                loadImage("i3.png"),
                loadImage("i4.png"),
                loadImage("i5.png"),
                loadImage("i6.png"),
                loadImage("i7.png"),
                loadImage("i8.png"),
                loadImage("sw1.png"),
                loadImage("sw2.png"),
                loadImage("sw3.png"),
                loadImage("sw4.png"),
                loadImage("sw5.png"),
                loadImage("sw6.png"),
                loadImage("sw7.png"),
                loadImage("sw8.png"),
        };

        images[2] = new Image[]{
                loadImage("d1.png"),
                loadImage("d2.png"),
                loadImage("d3.png"),
                loadImage("d4.png"),
                loadImage("d5.png"),
                loadImage("d6.png"),
                loadImage("d7.png"),
                loadImage("d8.png"),
                loadImage("sd1.png"),
                loadImage("sd2.png"),
                loadImage("sd3.png"),
                loadImage("sd4.png"),
                loadImage("sd5.png"),
                loadImage("sd6.png"),
                loadImage("sd7.png"),
                loadImage("sd8.png"),
        };

        images[1] = new Image[images[0].length];
        images[3] = new Image[images[0].length];
        for (int i = 0; i < images[0].length; i++) {
            images[1][i] = getMirrorImage(images[0][i]);
        }

        for (int i = 0; i < images[2].length; i++) {
            images[3][i] = getMirrorImage(images[2][i]);
        }


        Animation[] playerAnim = new Animation[4];
        Animation[] grubAnim = new Animation[4];
        for (int i = 0; i < 4; i++) {
            Animation anim = new Animation();
            for (int x = 0; x < 8; x++) {
                if(i < 2)
                    anim.addFrame(images[i][x], 200);
                else if (i == 2 || i == 3)
                    if (x != 7)
                        anim.addFrame(images[i][x], 100);
                    else
                        anim.addFrame(images[i][x], 600);
            }

            playerAnim[i] = anim;

            anim = new Animation();

            for (int x = 8; x < 16; x++) {
                if(i < 2)
                    anim.addFrame(images[i][x], 150);
                else
                    if (x != 15)
                        anim.addFrame(images[i][x], 100);
                    else
                        anim.addFrame(images[i][x], 600);
            }

            grubAnim[i] = anim;

        }


        playerSprite = new Player(playerAnim[1], playerAnim[0],
                playerAnim[3], playerAnim[2]);
        grubSprite = new Skeleton(grubAnim[1], grubAnim[0],
                grubAnim[3], grubAnim[2]);
    }



    private Animation createGrubAnim(Image img1, Image img2) {
        Animation anim = new Animation();
        anim.addFrame(img1, 250);
        anim.addFrame(img2, 250);
        return anim;
    }


    private void loadPowerUpSprites() {

        Animation anim = new Animation();
        anim.addFrame(loadImage("castle8.png"), 150);
        goalSprite = new PowerUp.Goal(anim);


        anim = new Animation();
        anim.addFrame(loadImage("Diamond_01.png"), 100);
        anim.addFrame(loadImage("Diamond_04.png"), 100);
        anim.addFrame(loadImage("Diamond_08.png"), 100);
        anim.addFrame(loadImage("Diamond_12.png"), 100);
        anim.addFrame(loadImage("Diamond_16.png"), 100);
        anim.addFrame(loadImage("Diamond_20.png"), 100);
        anim.addFrame(loadImage("Diamond_24.png"), 100);
        anim.addFrame(loadImage("Diamond_28.png"), 100);
        anim.addFrame(loadImage("Diamond_30.png"), 100);
        anim.addFrame(loadImage("Diamond_28.png"), 100);
        anim.addFrame(loadImage("Diamond_24.png"), 100);
        anim.addFrame(loadImage("Diamond_20.png"), 100);
        anim.addFrame(loadImage("Diamond_16.png"), 100);
        anim.addFrame(loadImage("Diamond_12.png"), 100);
        anim.addFrame(loadImage("Diamond_08.png"), 100);
        anim.addFrame(loadImage("Diamond_04.png"), 100);
        diamondSprite = new PowerUp.Diamond(anim);

    }

    private void loadEnvTiles() {

        environment = new ArrayList();
        String name;

        for (int i = 1; i < 8; i++){
            Animation anim = new Animation();
            name = "castle" + i + ".png";
            Image image = loadImage(name);
            anim.addFrame(image, 100);
            environment.add(new Sprite(anim));
        }
    }

    public Sprite getCoinSprite() {
        return diamondSprite;
    }

}
