package de.doccrazy.ld34.game.actor;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import de.doccrazy.ld34.data.GameRules;
import de.doccrazy.ld34.game.world.GameWorld;

public class DummyLevelActor extends Level {
    private final Rectangle boundingBox = new Rectangle(0, 0, GameRules.LEVEL_WIDTH, GameRules.LEVEL_HEIGHT);

    public DummyLevelActor(GameWorld world) {
        super(world);
    }

    @Override
    public Rectangle getBoundingBox() {
        return boundingBox;
    }

    @Override
    public Vector2 getSpawn() {
        return new Vector2(1.5f, 5f);
    }

    @Override
    public int getScoreGoal() {
        return 0;
    }

    @Override
    public float getTime() {
        return 0;
    }

    @Override
    protected void doAct(float delta) {

    }
}
