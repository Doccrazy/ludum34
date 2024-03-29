package de.doccrazy.ld34.game.actor;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import de.doccrazy.ld34.core.Resource;
import de.doccrazy.ld34.data.GameRules;
import de.doccrazy.ld34.game.world.GameWorld;
import de.doccrazy.shared.game.actor.SpriterActor;
import de.doccrazy.shared.game.world.BodyBuilder;
import de.doccrazy.shared.game.world.ShapeBuilder;

public class ChildActor extends SpriterActor<GameWorld> implements Hittable {
    private static final float RADIUS = 0.3f;
    private static final float SPEED = 1.5f;
    private FussballActor fussballActor;

    public ChildActor(GameWorld world, Vector2 spawn, FussballActor fussballActor) {
        super(world, spawn, false, Resource.SPRITER.child, Resource.SPRITER::getDrawer);
        this.fussballActor = fussballActor;
        setzOrder(20);
        player.setScale(0.01f);
        player.setAnimation("walk");
        Resource.SOUND.childSpawn.play();
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
            world.addActor(new SmallFireActor(world, body.getPosition()));
            runOver();
        }
        if (!world.isGameInProgress()) {
            return;
        }
        if (fussballActor != null && !fussballActor.isDead()) {
            Vector2 d = fussballActor.getBody().getPosition().cpy().sub(body.getPosition());
            if (d.len() < 0.1f) {
                body.setLinearVelocity(0, 0);
                fussballActor.kill();
                fussballActor = null;
                player.setAnimation("fussball");
                Resource.SOUND.childGetBall.play();
            } else {
                body.setLinearVelocity(d.nor().scl(SPEED));
                setRotation(d.angle());
            }
        } else {
            Vector2 d = spawn.cpy().sub(body.getPosition());
            if (d.len() < 0.1f) {
                kill();
            } else {
                body.setLinearVelocity(d.nor().scl(SPEED));
                setRotation(d.angle());
            }
        }
    }

    @Override
    public int getPoints() {
        return fussballActor == null ? GameRules.POINTS_CHILD_WITH_BALL : GameRules.POINTS_CHILD;
    }

    public void runOver() {
        world.getPlayer().startTrail(Resource.GFX.bloodTrailTex);
        for (int i = 0; i < 20; i++) {
            Vector2 pos = new Vector2(1, 0).rotate(MathUtils.random(360)).scl(MathUtils.random(0.2f, 1f))
                    .add(new Vector2(getX() + getOriginX(), getY() + getOriginY()));
            world.addActor(new BloodActor(world, pos));
        }
        if ("fussball".equals(player.getAnimation().name)) {
            Resource.SOUND.powerup1.play();
        } else {
            Resource.SOUND.powerup2.play();
        }
        kill();
    }
}
