package de.doccrazy.ld34.game.actor;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import de.doccrazy.ld34.core.Resource;
import de.doccrazy.ld34.game.world.GameWorld;
import de.doccrazy.shared.game.actor.ShapeActor;
import de.doccrazy.shared.game.world.BodyBuilder;
import de.doccrazy.shared.game.world.ShapeBuilder;

public class RockActor extends ShapeActor<GameWorld> implements Hittable {
    private static final float RADIUS = 0.18f;
    private final Sprite sprite;

    public RockActor(GameWorld world, Vector2 spawn) {
        super(world, spawn, false);
        sprite = Resource.GFX.rocks[MathUtils.random(Resource.GFX.rocks.length-1)];
        setzOrder(11);
        setScale(1.4f);
    }

    @Override
    protected BodyBuilder createBody(Vector2 spawn) {
        return BodyBuilder.forStatic(spawn)
                .fixShape(ShapeBuilder.circle(MathUtils.random(RADIUS*0.8f, RADIUS*1.5f)))
                .noRotate();
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        drawRegion(batch, sprite);
    }

    @Override
    public int getPoints() {
        return 0;
    }

    @Override
    public void runOver() {
        world.getPlayer().damage(0.5f);
        if (!world.getPlayer().isDestroyed()) {
            kill();
        }
    }
}
