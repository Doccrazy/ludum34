package de.doccrazy.ld34.game.actor;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import de.doccrazy.ld34.core.Resource;
import de.doccrazy.ld34.data.GameRules;
import de.doccrazy.ld34.game.world.GameWorld;
import de.doccrazy.ld34.game.world.RandomEvent;
import de.doccrazy.shared.game.world.GameState;

public class Level2Actor extends Level {
    public static final float LEVEL_WIDTH = 32;
    public static final float LEVEL_HEIGHT = LEVEL_WIDTH*9f/16f;

    public static final float GRASS_PER_SEC = 25f;
    public static final RandomEvent FUSSBALL_PER_SEC = new RandomEvent(2.5f, 7.5f);
    public static final float DOG_PER_SEC_PER_FUSSBALL = 0.05f;

    private final Rectangle boundingBox = new Rectangle(0, 0, LEVEL_WIDTH, LEVEL_HEIGHT);
    private final Rectangle grassBox = new Rectangle(2.0f, 1.6f, LEVEL_WIDTH-3.9f, LEVEL_HEIGHT-3.5f);
    private final Rectangle exclude1 = new Rectangle(1.1f, 14.2f, 16.1f, 2.7f);
    private final Rectangle exclude2 = new Rectangle(18.9f, 6.0f, 4.0f, 2.2f);

    public Level2Actor(GameWorld world) {
        super(world);
        grassPerSec = GRASS_PER_SEC;
        fussballPerSec = FUSSBALL_PER_SEC;
        dogPerSecPerFussball = DOG_PER_SEC_PER_FUSSBALL;

        for (int i = 0; i < 200; i++) {
            spawnRandomObject(GrassActor::new, false);
        }
        for (int i = 0; i < 20; i++) {
            spawnRandomObject(RockActor::new, true);
        }
        world.addActor(new BarrierActor(world, new Rectangle(0, 0, LEVEL_WIDTH, 1.3f)));  //bottom
        world.addActor(new BarrierActor(world, new Rectangle(0, 0, 1.8f, LEVEL_HEIGHT)));  //left
        world.addActor(new BarrierActor(world, new Rectangle(0, LEVEL_HEIGHT - 1.7f, LEVEL_WIDTH, 1.7f)));  //top
        world.addActor(new BarrierActor(world, new Rectangle(LEVEL_WIDTH - 1.5f, 0, 1.5f, LEVEL_HEIGHT)));  //right
        world.addActor(new BarrierActor(world, exclude1));  //terrace
        world.addActor(new BarrierActor(world, exclude2));  //pool
        Resource.MUSIC.music2.play();
    }

    @Override
    public Rectangle getBoundingBox() {
        return boundingBox;
    }

    @Override
    public Rectangle getGrassBox() {
        return grassBox;
    }

    @Override
    protected boolean excluded(Vector2 pos) {
        return exclude1.contains(pos) || exclude2.contains(pos);
    }

    @Override
    public Vector2 getSpawn() {
        return new Vector2(3f, 12f);
    }

    @Override
    public int getScoreGoal() {
        return 15000;
    }

    @Override
    public float getTime() {
        return 99999;
    }

    @Override
    protected void doAct(float delta) {
        if (world.getGameState() != GameState.GAME) {
            return;
        }
        int ballCount = 0;
        for (Actor actor : world.stage.getActors()) {
            if (actor instanceof FussballActor) {
                ballCount++;
            }
        }
        fussballPerSec.setMaxTime(ballCount > 1 ? 12.5f : 7.5f);
        spawnRandomStuff(delta);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        batch.draw(Resource.GFX.level2bg, 0, 0, LEVEL_WIDTH, LEVEL_HEIGHT);
    }
}
