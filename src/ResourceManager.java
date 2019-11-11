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
    private Sprite flySprite;


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

                    return null;
                }
                currentMap = 0;
                map = null;
            }
        }

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
                } else if (ch == '2') {
                    addSprite(newMap, flySprite, x, y);
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
                loadImage("fly1.png"),
                loadImage("fly2.png"),
                loadImage("fly3.png"),
                loadImage("grub1.png"),
                loadImage("grub2.png"),
        };

        images[1] = new Image[images[0].length];
        images[2] = new Image[images[0].length];
        images[3] = new Image[images[0].length];
        for (int i = 0; i < images[0].length; i++) {

            images[1][i] = getMirrorImage(images[0][i]);

            images[2][i] = getFlippedImage(images[0][i]);

            images[3][i] = getFlippedImage(images[1][i]);
        }


        Animation[] playerAnim = new Animation[4];
        Animation[] flyAnim = new Animation[4];
        Animation[] grubAnim = new Animation[4];
        for (int i = 0; i < 4; i++) {
            playerAnim[i] = createPlayerAnim(
                    images[i][0], images[i][1], images[i][2]);
            flyAnim[i] = createFlyAnim(
                    images[i][3], images[i][4], images[i][5]);
            grubAnim[i] = createGrubAnim(
                    images[i][6], images[i][7]);
        }


        playerSprite = new Player(playerAnim[1], playerAnim[0],
                playerAnim[2], playerAnim[3]);
        flySprite = new Fly(flyAnim[0], flyAnim[1],
                flyAnim[2], flyAnim[3]);
        grubSprite = new Grub(grubAnim[0], grubAnim[1],
                grubAnim[2], grubAnim[3]);
    }


    private Animation createPlayerAnim(Image player1,
                                       Image player2, Image player3) {
        Animation anim = new Animation();
        anim.addFrame(player1, 250);
        return anim;
    }


    private Animation createFlyAnim(Image img1, Image img2,
                                    Image img3) {
        Animation anim = new Animation();
        anim.addFrame(img1, 50);
        anim.addFrame(img2, 50);
        anim.addFrame(img3, 50);
        anim.addFrame(img2, 50);
        return anim;
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
