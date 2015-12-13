package de.doccrazy.ld34.game.actor;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import de.doccrazy.ld34.core.Resource;
import de.doccrazy.ld34.game.world.GameWorld;
import de.doccrazy.shared.game.actor.SpriterActor;
import de.doccrazy.shared.game.world.BodyBuilder;
import de.doccrazy.shared.game.world.ShapeBuilder;

public class DogActor extends SpriterActor<GameWorld> implements Hittable {
    private static final float RADIUS = 0.3f;

    public DogActor(GameWorld world, Vector2 spawn) {
        super(world, spawn, false, Resource.SPRITER.dog, Resource.SPRITER::getDrawer);
        setzOrder(12);
        player.setScale(0.0025f);
        player.setAnimation("walk");
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
        if (world.getPlayer().isCaughtInShockwave(body.getPosition())) {
            runOver();
        }
    }

    @Override
    public int getPoints() {
        return 200;
    }

    public void runOver() {
        world.getPlayer().startTrail(Resource.GFX.bloodTrailTex);
        for (int i = 0; i < 20; i++) {
            Vector2 pos = new Vector2(1, 0).rotate(MathUtils.random(360)).scl(MathUtils.random(0.2f, 1f))
                    .add(new Vector2(getX() + getOriginX(), getY() + getOriginY()));
            world.addActor(new BloodActor(world, pos));
        }
        kill();
    }
}
