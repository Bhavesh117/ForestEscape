import java.awt.*;
import java.util.Iterator;


public class TileMapRenderer {

    private static final int TILE_SIZE = 64;


    private static final int TILE_SIZE_BITS = 6;

    private Image background;
    private Image backgroundFar;
    private Image backgroundMid;
    private Image backgroundFront;



    public static int pixelsToTiles(float pixels) {
        return pixelsToTiles(Math.round(pixels));
    }


    public static int pixelsToTiles(int pixels) {

        return pixels >> TILE_SIZE_BITS;


    }


    public static int tilesToPixels(int numTiles) {


        return numTiles << TILE_SIZE_BITS;


    }


    public void setBackground(Image background, Image backgroundFar, Image backgroundMid, Image backgroundFront) {
        this.background = background;
        this.backgroundFar = backgroundFar;
        this.backgroundMid = backgroundMid;
        this.backgroundFront = backgroundFront;
    }


    public void draw(Graphics2D g, TileMap map,
                     int screenWidth, int screenHeight) {
        Sprite player = map.getPlayer();
        int mapWidth = tilesToPixels(map.getWidth());


        int offsetX = screenWidth / 2 -
                Math.round(player.getX()) - TILE_SIZE;
        offsetX = Math.min(offsetX, 0);
        offsetX = Math.max(offsetX, screenWidth - mapWidth);


        int offsetY = screenHeight -
                tilesToPixels(map.getHeight());


        if (background == null ||
                screenHeight > background.getHeight(null)) {
            g.setColor(Color.black);
            g.fillRect(0, 0, screenWidth, screenHeight);
        }


        drawBackground(g, background, offsetX, mapWidth, screenWidth, screenHeight);
        drawBackground(g, backgroundFar, offsetX, mapWidth, screenWidth, screenHeight);
        drawBackground(g, backgroundMid, offsetX, mapWidth, screenWidth, screenHeight);
        drawBackground(g, backgroundFront, offsetX, mapWidth, screenWidth, screenHeight);



        int firstTileX = pixelsToTiles(-offsetX);
        int lastTileX = firstTileX +
                pixelsToTiles(screenWidth) + 1;
        for (int y = 0; y < map.getHeight(); y++) {
            for (int x = firstTileX; x <= lastTileX; x++) {
                Image image = map.getTile(x, y);
                if (image != null) {
                    g.drawImage(image,
                            tilesToPixels(x) + offsetX,
                            tilesToPixels(y) + offsetY,
                            null);
                }
            }
        }


        Iterator i = map.getSprites();
        while (i.hasNext()) {
            Sprite sprite = (Sprite) i.next();
            int x = Math.round(sprite.getX()) + offsetX;
            int y = Math.round(sprite.getY()) + offsetY;
            g.drawImage(sprite.getImage(), x, y, null);


            if (sprite instanceof Creature &&
                    x >= 0 && x < screenWidth) {
                ((Creature) sprite).wakeUp();
            }
        }

        g.drawImage(player.getImage(),
                Math.round(player.getX()) + offsetX,
                Math.round(player.getY()) + offsetY,
                null);
    }


    private void drawBackground(Graphics2D g, Image background, int offsetX, int mapWidth, int screenWidth, int screenHeight){
        if (background != null) {
            int x = offsetX *
                    (screenWidth - background.getWidth(null)) /
                    (screenWidth - mapWidth);
            int y = screenHeight - background.getHeight(null);

            g.drawImage(background, x, y, null);
        }
    }

}
