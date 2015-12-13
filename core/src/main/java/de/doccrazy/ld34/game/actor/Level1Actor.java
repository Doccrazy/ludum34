package de.doccrazy.ld34.game.actor;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import de.doccrazy.ld34.core.Resource;
import de.doccrazy.ld34.data.GameRules;
import de.doccrazy.ld34.game.world.GameWorld;
import de.doccrazy.shared.game.actor.WorldActor;

import java.util.function.BiFunction;
import java.util.function.Function;

public class Level1Actor extends Level {
    private final Rectangle boundingBox = new Rectangle(0, 0, GameRules.LEVEL_WIDTH, GameRules.LEVEL_HEIGHT);
    private final Rectangle grassBox = new Rectangle(1.4f, 1.1f, GameRules.LEVEL_WIDTH-2.6f, GameRules.LEVEL_HEIGHT-2.4f);

    public Level1Actor(GameWorld world) {
        super(world);
        for (int i = 0; i < 3000; i++) {
            spawnRandomObject(GrassActor::new, false);
        }
        for (int i = 0; i < 20; i++) {
            spawnRandomObject(RockActor::new, true);
        }
        world.addActor(new BarrierActor(world, new Rectangle(0, 0, GameRules.LEVEL_WIDTH, 0.8f)));  //bottom
        world.addActor(new BarrierActor(world, new Rectangle(0, 0, 1f, GameRules.LEVEL_HEIGHT)));  //left
        world.addActor(new BarrierActor(world, new Rectangle(0, GameRules.LEVEL_HEIGHT - 1.1f, GameRules.LEVEL_WIDTH, 1.1f)));  //top
        world.addActor(new BarrierActor(world, new Rectangle(GameRules.LEVEL_WIDTH - 0.8f, 0, 0.8f, GameRules.LEVEL_HEIGHT)));  //right
    }

    private void spawnRandomObject(BiFunction<GameWorld, Vector2, WorldActor<GameWorld>> factory, boolean avoidPlayer) {
        Vector2 spawn;
        do {
            spawn = new Vector2(MathUtils.random(grassBox.getX(), grassBox.getWidth() + grassBox.getX()),
                    MathUtils.random(grassBox.getY(), grassBox.getHeight() + grassBox.getY()));
        } while (avoidPlayer && spawn.dst(getSpawn()) < 2f);
        world.addActor(factory.apply(world, spawn));
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
    public Vector2 getSpawn() {
        return new Vector2(2f, 10f);
    }

    @Override
    public int getScoreGoal() {
        return 99999;
    }

    @Override
    public float getTime() {
        return 99999;
    }

    @Override
    protected void doAct(float delta) {
        if (MathUtils.randomBoolean(delta*20)) {
            spawnRandomObject(GrassActor::new, false);
        }
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        batch.draw(Resource.GFX.level1bg, 0, 0, GameRules.LEVEL_WIDTH, GameRules.LEVEL_HEIGHT);
    }
}
