package de.doccrazy.ld34.game.actor;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import de.doccrazy.ld34.core.Resource;
import de.doccrazy.ld34.data.GameRules;
import de.doccrazy.ld34.game.world.GameWorld;
import de.doccrazy.shared.game.actor.SpriterActor;
import de.doccrazy.shared.game.world.BodyBuilder;
import de.doccrazy.shared.game.world.ShapeBuilder;

public class DogActor extends SpriterActor<GameWorld> implements Hittable {
    private static final float RADIUS = 0.3f;
    private static final float SPEED = 1f;
    private int state = 0;
    private Vector2 target;

    public DogActor(GameWorld world, Vector2 spawn) {
        super(world, spawn, false, Resource.SPRITER.dog, Resource.SPRITER::getDrawer);
        setzOrder(21);
        player.setScale(0.0025f);
        player.setAnimation("walk");
        Resource.SOUND.wauwau.play();
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
        handleState();
        if (world.getPlayer().isCaughtInShockwave(body.getPosition())) {
            world.addActor(new SmallFireActor(world, body.getPosition()));
            runOver();
        }
    }

    private void handleState() {
        if (!world.isGameInProgress()) {
            body.setLinearVelocity(0, 0);
            return;
        }
        switch (state) {
            case 0:
                target = world.getLevel().getRandomPoint(false);
                state = 1;
                break;
            case 1:
                Vector2 d = target.cpy().sub(body.getPosition());
                if (d.len() < 0.25f) {
                    body.setLinearVelocity(0, 0);
                    state = 2;
                    stateTime = 0;
                    player.setAnimation("crap");
                    return;
                }
                body.setLinearVelocity(d.nor().scl(SPEED));
                setRotation(d.angle() + 180);
                break;
            case 2:
                if (stateTime > 1f) {
                    Vector2 c = localToStageCoordinates(new Vector2(getOriginX() + 0.5f, getOriginY()));
                    world.addActor(new CrapActor(world, c));
                    state = 3;
                    stateTime = 0;
                    Resource.SOUND.crap.play();
                }
                break;
            case 3:
                if (stateTime > 1f) {
                    Resource.SOUND.wauwau.play();
                    player.setAnimation("walk");
                    state = 0;
                }
        }
    }

    @Override
    public int getPoints() {
        return GameRules.POINTS_DOG;
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
