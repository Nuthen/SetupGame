package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.PolygonSprite;
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.util.Random;

public class MyGdxGame extends ApplicationAdapter {
	SpriteBatch batch;
	Texture img;
    Texture hex_clear;
    Texture hex_clear_select;
    Texture tile1;
    Texture tile2;

    boolean check = false;

    float startX = 160;

    float playerX = 0;
    float playerY = 500;

    float transitionX = 0;

    float vx = 0;
    float vy = 0;

    boolean editor = true;

    public int lastX = 0;
    public int lastY = 0;

    public int radius = 4;
    float hexRadius = 48;

    private BitmapFont font;

    public int[] selectedTiles = new int[122];
    public int selectedTilesIndex = 0;

    float[][] mapTiles = new float[radius*2+1][radius*2+1];

    {
        for (int x = 0; x < radius*2+1; x++){
            for (int y = 0; y < radius*2+1; y++) {
                int xTile = x - radius;
                int yTile = y - radius;
                if (xTile + yTile < -radius || xTile + yTile > radius || xTile < -radius || xTile > radius || yTile < -radius || yTile > radius) {
                    mapTiles[x][y] = -1;
                } else{
                    Random random = new Random();
                    float value = random.nextInt(5) + 1;
                    mapTiles[x][y] = value;
                }
            }
        }
    }

    private float rawDeltaTime;

    int screenWidth;
    int screenHeight;

    @Override
	public void create () {
		batch = new SpriteBatch();
		img = new Texture("badlogic.jpg");
        hex_clear = new Texture("hex_clear2.png");
        hex_clear_select = new Texture("hex_clear_select.png");
        tile1 = new Texture("tile1.jpg");
        tile2 = new Texture("tile2.jpg");

        font = new BitmapFont();
        font.setScale(3);
        font.setColor(Color.RED);

        int screenWidth = Gdx.graphics.getWidth();
        int screenHeight = Gdx.graphics.getHeight();

	}

    //@Override
    //public boolean touchDown(int screenX, int screenY, int pointer, int button) {
    //    vy = 5;
    //    return true;
    //}

    @Override
    public void render() {
        //float deltaTime = getRawDeltaTime();

        if (Gdx.input.justTouched()) {
            //check = true;
            float touchX = Gdx.input.getX();
            float touchY = Gdx.graphics.getHeight() - Gdx.input.getY();

            //float q = (float) ((1/3*Math.sqrt(3) * touchX - 1./3 * touchY) / hexRadius);
            //float r = 2/3 * touchY / hexRadius;

            int closestX = -1;
            int closestY = -1;
            float closestDist = 10000;

            for (float x = 0; x < radius*2+1; x++) {
                for (float y = 0; y < radius * 2 + 1; y++) {
                    if (mapTiles[(int) x][(int) y] > -1f) {
                        float xTile = x - radius;
                        float yTile = y - radius;

                        float size = hexRadius;

                        float px = (float) ((size * Math.sqrt(3) * (yTile + xTile / 2)) + Gdx.graphics.getWidth() / 2 + size/2);
                        float py = (float) (size * (3 / 2 * xTile * 1.5) + Gdx.graphics.getHeight() / 2 - 46 + Math.cos(Math.toRadians(30))*size);

                        //batch.draw(hex_clear, px + Gdx.graphics.getWidth() / 2, py + Gdx.graphics.getHeight() / 2 - 46);

                        float dist = (float) (Math.sqrt(Math.pow(px - touchX, 2) + Math.pow(py - touchY, 2)));

                        if (dist < closestDist) {
                            closestX = (int) x;
                            closestY = (int) y;
                            closestDist = dist;
                        }
                    }
                }
            }

            if (closestX > -1) {
                selectedTilesIndex = 0;

                int x = closestX;
                int y = closestY;

                lastX = x;
                lastY = y;

                float value = mapTiles[x][y];

                int count = loopHex(x, y, value, 0);

                if (count > 1) {

                    for (int ix = 0; ix < radius * 2 + 1; ix++) {
                        for (int iy = 0; iy < radius * 2 + 1; iy++) {
                            if (mapTiles[ix][iy] == mapTiles[x][y]) {
                                if (ix != x || iy != y) {
                                    mapTiles[ix][iy] = 0;
                                }
                            } else {
                                mapTiles[ix][iy] = (float) Math.floor(mapTiles[ix][iy]);
                            }
                        }
                    }

                    mapTiles[x][y] = value + 1f;

                    for (int iy = 0; iy < radius * 2 + 1; iy++) {
                        for (int ix = 0; ix < radius * 2 + 1; ix++) {
                            if (mapTiles[ix][iy] == 0f) {

                                int i = 1;
                                while (i <= 8) {
                                    if (ix + i <= radius * 2 && mapTiles[ix + i][iy] > 0) {
                                        mapTiles[ix][iy] = mapTiles[ix + i][iy];
                                        mapTiles[ix + i][iy] = 0;
                                        i = 10;
                                    }
                                    i++;
                                }
                            }
                        }
                    }

                    for (int iy = 0; iy < radius * 2 + 1; iy++) {
                        for (int ix = 0; ix < radius * 2 + 1; ix++) {
                            if (mapTiles[ix][iy] == 0) {
                                Random random = new Random();
                                float newValue = random.nextInt(5) + 1;
                                mapTiles[ix][iy] = newValue;
                            }
                        }
                    }
                } else {
                    mapTiles[x][y] = (float) Math.floor(mapTiles[x][y]);
                }
            }
        }


        batch.begin();

        Gdx.gl.glClearColor(0, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);


        for (int x = 0; x < radius*2+1; x++) {
            for (int y = 0; y < radius*2+1; y++) {
                if (mapTiles[x][y] > -1f) {
                    //int z = -(x + y);

                    float xTile = x - radius;
                    float yTile = y - radius;

                    //int q = x;
                    //int r = y;

                    float size = hexRadius;

                    int px = (int) ((size * Math.sqrt(3) * (yTile + xTile / 2)));
                    int py = (int) (size * (3 / 2 * xTile * 1.5));

                    boolean selected = false;

                    //for (int i = 0; i < selectedTiles.length; i += 2) {
                    //    if (selectedTiles[i] == x && selectedTiles[i+1] == y) {
                    //        selected = true;
                    //    }
                    //}
                    if (mapTiles[x][y] - Math.floor(mapTiles[x][y]) == 0.2) {
                        batch.draw(hex_clear_select, px + Gdx.graphics.getWidth() / 2, py + Gdx.graphics.getHeight() / 2 - 46);
                    } else {
                        batch.draw(hex_clear, px + Gdx.graphics.getWidth() / 2, py + Gdx.graphics.getHeight() / 2 - 46);
                    }
                    font.draw(batch, "" + (int) Math.floor(mapTiles[x][y]), px + Gdx.graphics.getWidth() / 2 + hexRadius - 15, py + Gdx.graphics.getHeight() / 2 - 46 + hexRadius + 20);
                }
            }
        }

        //batch.draw(player, x, y);


        font.draw(batch, "" + Gdx.graphics.getFramesPerSecond(), 5, Gdx.graphics.getHeight() - 5);
        font.draw(batch, "" + lastX + ", " + lastY, 5, Gdx.graphics.getHeight() - 45);

        batch.end();
    }

    private int loopHex(int x, int y, float value, int count) {
        //if (selectedTilesIndex < selectedTiles.length) {
            //selectedTiles[selectedTilesIndex] = x;
            //selectedTiles[selectedTilesIndex + 1] = y;

            //selectedTilesIndex += 2;
        if (x >= 0 && y >= 0 && x <= radius*2 && y <= radius*2 && mapTiles[x][y] > 0 && mapTiles[x][y] == value) {
            if (mapTiles[x][y] == value) {
                mapTiles[x][y] += .2;
                count++;

                int x1 = x + 1;
                int y1 = y;

                int x2 = x + 1;
                int y2 = y - 1;

                int x3 = x;
                int y3 = y - 1;

                int x4 = x - 1;
                int y4 = y;

                int x5 = x - 1;
                int y5 = y + 1;

                int x6 = x;
                int y6 = y + 1;

                boolean selected1 = false;
                boolean selected2 = false;
                boolean selected3 = false;
                boolean selected4 = false;
                boolean selected5 = false;
                boolean selected6 = false;


                if (x1 >= 0 && y1 >= 0 && x1 <= radius*2 && y1 <= radius*2 && mapTiles[x1][y1] > 0 && (float) Math.floor(mapTiles[x1][y1]) != mapTiles[x1][y1]) {
                    selected1 = true;
                }
                if (x2 >= 0 && y2 >= 0 && x2 <= radius*2 && y2 <= radius*2 && mapTiles[x2][y2] > 0 && (float) Math.floor(mapTiles[x2][y2]) != mapTiles[x2][y2]) {
                    selected2 = true;
                }
                if (x3 >= 0 && y3 >= 0 && x3 <= radius*2 && y3 <= radius*2 && mapTiles[x3][y3] > 0 && (float) Math.floor(mapTiles[x3][y3]) != mapTiles[x3][y3]) {
                    selected3 = true;
                }
                if (x4 >= 0 && y4 >= 0 && x4 <= radius*2 && y4 <= radius*2 && mapTiles[x4][y4] > 0 && (float) Math.floor(mapTiles[x4][y4]) != mapTiles[x4][y4]) {
                    selected4 = true;
                }
                if (x5 >= 0 && y5 >= 0 && x5 <= radius*2 && y5 <= radius*2 && mapTiles[x5][y5] > 0 && (float) Math.floor(mapTiles[x5][y5]) != mapTiles[x5][y5]) {
                    selected5 = true;
                }
                if (x6 >= 0 && y6 >= 0 && x6 <= radius*2 && y6 <= radius*2 && mapTiles[x6][y6] > 0 && (float) Math.floor(mapTiles[x6][y6]) != mapTiles[x6][y6]) {
                    selected6 = true;
                }

                if (!selected1) {
                    count = loopHex(x1, y1, value, count);
                }
                if (!selected2) {
                    count = loopHex(x2, y2, value, count);
                }
                if (!selected3) {
                    count = loopHex(x3, y3, value, count);
                }
                if (!selected4) {
                    count = loopHex(x4, y4, value, count);
                }
                if (!selected5) {
                    count = loopHex(x5, y5, value, count);
                }
                if (!selected6) {
                    count = loopHex(x6, y6, value, count);
                }

            } else {
                mapTiles[x][y] += .1;
            }
        }

        return count;
       // }
    }


    public float getRawDeltaTime() {
        return rawDeltaTime;
    }
}
