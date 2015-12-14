package de.doccrazy.ld34.game.actor;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import de.doccrazy.ld34.core.Resource;
import de.doccrazy.ld34.data.GameRules;
import de.doccrazy.ld34.game.world.GameWorld;
import de.doccrazy.ld34.game.world.HelpEvent;
import de.doccrazy.shared.game.actor.ShapeActor;
import de.doccrazy.shared.game.world.BodyBuilder;
import de.doccrazy.shared.game.world.ShapeBuilder;

public class FussballActor extends ShapeActor<GameWorld> implements Hittable {
    private static final float RADIUS = 0.3f;
    private final Vector2 target;
    private boolean solid;
    private ChildActor childAttracted;

    public FussballActor(GameWorld world, Vector2 spawn, Vector2 target) {
        super(world, spawn, false);
        this.target = target;
        setzOrder(20);
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
            solid = true;
        }
        if (childAttracted != null && childAttracted.isDead()) {
            childAttracted = null;
            stateTime = 0;
        }
        if (childAttracted == null && stateTime > GameRules.FUSSBALL_CHILD_TIME && world.isGameInProgress()) {
            Vector2 cs = world.getLevel().getRandomBorderPoint();
            world.addActor(childAttracted = new ChildActor(world, cs, this));
        }
        if (world.getPlayer().isCaughtInShockwave(body.getPosition())) {
            world.addActor(new SmallFireActor(world, body.getPosition()));
            kill();
        }
        setRotation(getRotation() + body.getLinearVelocity().len()*20);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        drawRegion(batch, Resource.GFX.fussball);
    }

    @Override
    public int getPoints() {
        return GameRules.POINTS_FUSSBALL;
    }

    @Override
    public void runOver() {
        world.getPlayer().startTrail(Resource.GFX.fussballTrailTex);
        kill();
    }
}
