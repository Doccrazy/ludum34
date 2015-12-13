package de.doccrazy.ld34.game.actor;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import de.doccrazy.ld34.core.Resource;
import de.doccrazy.ld34.game.world.GameWorld;
import de.doccrazy.shared.game.actor.ShapeActor;
import de.doccrazy.shared.game.world.BodyBuilder;
import de.doccrazy.shared.game.world.ShapeBuilder;

public class FussballActor extends ShapeActor<GameWorld> implements Hittable {
    private static final float RADIUS = 0.3f;
    private final Vector2 target;
    private boolean solid;

    public FussballActor(GameWorld world, Vector2 spawn, Vector2 target) {
        super(world, spawn, false);
        this.target = target;
        setzOrder(12);
    }

    @Override
    protected void init() {
        super.init();
        float impulse = 5f;
        body.applyLinearImpulse(target.cpy().sub(body.getPosition()).nor().scl(impulse), Vector2.Zero, true);
    }

    @Override
    protected BodyBuilder createBody(Vector2 spawn) {
        return BodyBuilder.forDynamic(spawn)
                .fixShape(ShapeBuilder.circle(RADIUS))
                .fixProps(0.5f, 0.9f, 1)
                .damping(1.5f, 0)
                .fixSensor().noRotate();
    }

    @Override
    protected void doAct(float delta) {
        super.doAct(delta);
        if (stateTime > 0.5f && !solid) {
            body.getFixtureList().first().setSensor(false);
        }
        if (world.getPlayer().isCaughtInShockwave(body.getPosition())) {
            kill();
        }
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        drawRegion(batch, Resource.GFX.fussball);
    }

    @Override
    public int getPoints() {
        return 100;
    }

    @Override
    public void runOver() {
        world.getPlayer().startTrail(Resource.GFX.fussballTrailTex);
        kill();
    }
}