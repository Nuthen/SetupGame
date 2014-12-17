package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class MyGdxGame extends ApplicationAdapter {
	SpriteBatch batch;
	Texture img;
    Texture player;
    Texture tile1;
    Texture tile2;

    float startX = 160;

    float playerX = 0;
    float playerY = 500;


    float vx = 0;
    float vy = 0;

    //int rows = 5;
    //int cols = 10;

    boolean airborne = true;
    int jumps = 2;
    int maxJumps = 2;

    private BitmapFont font;

    int[][] mapTiles;

    {
        mapTiles = new int[][]{
                {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0},
                {1, 0, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0},
                {1, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 1, 1, 1, 1, 1, 0, 0, 1, 1, 1, 0, 0, 0},
                {1, 0, 0, 0, 0, 0, 0, 1, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0},
                {1, 0, 0, 0, 0, 0, 0, 1, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
                {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}
        };
    }

    private float rawDeltaTime;

    @Override
	public void create () {
		batch = new SpriteBatch();
		img = new Texture("badlogic.jpg");
        player = new Texture("player.jpg");
        tile1 = new Texture("tile1.jpg");
        tile2 = new Texture("tile2.jpg");

        font = new BitmapFont();
        font.setScale(3);
        font.setColor(Color.RED);
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
            if (!airborne || jumps < maxJumps) {
                vy = 8;
                airborne = true;
                jumps++;
            }
            //Gdx.input.vibrate(100);
        }

        if (airborne) {
            vy += -.3;
        }

        batch.begin();

        for (int r = 0; r < mapTiles.length; r++) {
            for (int c = 0; c < mapTiles[r].length; c++) {
                if (mapTiles[r][c] == 1 || mapTiles[r][c] == 2) {
                    int x = c * 128;
                    int y = r * 128;
                    batch.draw(tile1, x - playerX + startX, y);
                }


            }
        }


        Gdx.gl.glClearColor(0, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        //batch.draw(player, x, y);
        batch.draw(player, startX, playerY);


        if ((vx != 0) || (vy != 0)) {
            //int playerTileX = (int) Math.floor(playerX/128);
            //int playerTileY = (int) Math.floor(playerY/128);

            float nx = playerX + vx;
            float ny = playerY + vy;

            //batch.draw(tile2, playerTileX*128, playerTileY*128);

            boolean collision = false;

            for (int r = 0; r < mapTiles.length; r++) {
                for (int c = 0; c < mapTiles[r].length; c++) {
                    float tileX = c * 128;
                    float tileY = r * 128;

                    if (nx + 64 > tileX && nx < tileX + 128 && ny + 64 > tileY && ny < tileY + 128) {
                        if (mapTiles[r][c] == 1 && ny  > tileY + 128 - 20 ) {
                            //canMove = false;
                            vy = 0;
                            playerY = tileY + 128;
                            vx = 5;
                            airborne = false;
                            jumps = 0;

                            collision = true;
                        } else if (mapTiles[r][c] != 0) {
                            //vx *= -1;

                            // reset
                            playerX = startX;
                            playerY = 500;

                            vx = 0;
                            vy = 0;

                            airborne = true;
                            jumps = 0;
                            collision = true;
                        }
                    }
                }
            }

            playerX += vx;
            playerY += vy;

            if (!collision){
                if (jumps == 0) {
                //    jumps = 1;
                }
                airborne = true;
            }

            if (playerY + 64 < 0) {
                // reset
                playerX = startX;
                playerY = 500;

                vx = 0;
                vy = 0;

                airborne = true;
                jumps = 0;
            }
        }

        font.draw(batch, "" + Gdx.graphics.getFramesPerSecond(), 5, Gdx.graphics.getHeight()-5);

        batch.end();
    }

    public float getRawDeltaTime() {
        return rawDeltaTime;
    }
}
