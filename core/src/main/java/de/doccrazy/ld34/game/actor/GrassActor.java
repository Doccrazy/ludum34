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

public class GrassActor extends ShapeActor<GameWorld> implements Hittable {
    private static final float RADIUS = 0.08f;
    private final Sprite sprite;
    private float growthSpeed;
    private float scale = MathUtils.random(1f, 1.4f);

    public GrassActor(GameWorld world, Vector2 spawn) {
        super(world, spawn, false);
        sprite = Resource.GFX.grass[MathUtils.random(Resource.GFX.grass.length-1)];
        setzOrder(10);
        growthSpeed = MathUtils.random(0.1f, 0.5f);
    }

    @Override
    protected BodyBuilder createBody(Vector2 spawn) {
        return BodyBuilder.forStatic(spawn)
                .fixShape(ShapeBuilder.circle(RADIUS)).noRotate().fixSensor();
    }

    @Override
    protected void doAct(float delta) {
        super.doAct(delta);
        float progress = stateTime * growthSpeed;
        setScaleX(Math.min(progress*2f, 1.0f) * scale);
        setScaleY(Math.min(progress, 1.0f) * scale);
        if (world.getPlayer().isCaughtInShockwave(body.getPosition())) {
            kill();
        }
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        drawRegion(batch, sprite);
    }

    @Override
    public int getPoints() {
        return 10;
    }

    @Override
    public void runOver() {
        kill();
    }
}
