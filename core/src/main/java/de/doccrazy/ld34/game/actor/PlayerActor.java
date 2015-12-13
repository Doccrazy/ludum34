package de.doccrazy.ld34.game.actor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.CatmullRomSpline;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.RayCastCallback;
import com.badlogic.gdx.physics.box2d.joints.DistanceJointDef;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import de.doccrazy.ld34.core.Resource;
import de.doccrazy.ld34.game.world.GameWorld;
import de.doccrazy.shared.game.actor.ShapeActor;
import de.doccrazy.shared.game.base.CollisionListener;
import de.doccrazy.shared.game.base.KeyboardMovementListener;
import de.doccrazy.shared.game.base.MovementInputListener;
import de.doccrazy.shared.game.base.PolyLineRenderer;
import de.doccrazy.shared.game.world.BodyBuilder;
import de.doccrazy.shared.game.world.GameState;
import de.doccrazy.shared.game.world.ShapeBuilder;

import java.util.*;

public class PlayerActor extends ShapeActor<GameWorld> implements CollisionListener {
    private static final float RADIUS = 0.5f;
    private static final float SPEED = 2.5f;
    private static final float TURN_SPEED = 1f;
    private static final float TURN_MIN = -0.3f;
    private static final float TURN_MAX = 0.3f;

    private MovementInputListener movement;
    private final ParticleEffectPool.PooledEffect exhaust1, exhaust2, grass, smoke;
    private final ParticleEffectPool.PooledEffect[] fire = new ParticleEffectPool.PooledEffect[3];
    private final List<Trail> trails = new ArrayList<>();
    private float health = 1;

    public PlayerActor(GameWorld world, Vector2 spawn) {
        super(world, spawn, false);
        setzOrder(50);
        setScaleX(Resource.GFX.mower.getWidth() / Resource.GFX.mower.getHeight());
        exhaust1 = Resource.GFX.partExhaust.obtain();
        exhaust1.start();
        exhaust2 = Resource.GFX.partExhaust.obtain();
        exhaust2.start();
        grass = Resource.GFX.partGrass.obtain();
        grass.start();
        fire[0] = Resource.GFX.partFire.obtain();
        fire[1] = Resource.GFX.partFire.obtain();
        fire[2] = Resource.GFX.partFire.obtain();
        smoke = Resource.GFX.partSmoke.obtain();
    }

    @Override
    protected void init() {
        super.init();
        setOriginX(getOriginX() + 0.1f);
        startTrail(Resource.GFX.bloodTrailTex);
    }

    @Override
    protected BodyBuilder createBody(Vector2 spawn) {
        return BodyBuilder.forDynamic(spawn)
                .fixShape(ShapeBuilder.circle(RADIUS)).fixSensor();
    }

    public void setupKeyboardControl() {
        movement = new KeyboardMovementListener();
        addListener((InputListener)movement);
    }

    public void setupController(MovementInputListener movement) {
        this.movement = movement;
    }

    @Override
    protected void doAct(float delta) {
        super.doAct(delta);
        if (world.getGameState() == GameState.GAME && health > 0) {
            Vector2 mv = movement.getMovement();
            body.setAngularVelocity(-mv.x * TURN_SPEED);
            body.setLinearVelocity(new Vector2(SPEED, 0).rotateRad(body.getAngle()));
            setOriginY(MathUtils.random(RADIUS - 0.03f * (1 - health) - 0.008f, RADIUS + 0.03f * (1 - health) + 0.008f));
        } else {
            body.setAngularVelocity(0);
            body.setLinearVelocity(0, 0);
        }
        updateTrails();
    }

    private void startTrail(Texture tex) {
        Trail t = new Trail();
        t.tex = tex;
        t.localPoint = new Vector2(getOriginX() - RADIUS/3f, getOriginY() + (MathUtils.randomBoolean() ? RADIUS : -RADIUS));
        t.startTime = stateTime;
        t.lastTime = 0;
        trails.add(t);
    }

    private void updateTrails() {
        for (Trail trail : trails) {
            if (stateTime - trail.startTime > trail.duration) {
                trail.active = false;
            } else if (trail.active && (trail.lastTime == 0 || stateTime - trail.lastTime > 0.2f)) {
                trail.points.add(localToStageCoordinates(trail.localPoint.cpy()));
            }
        }
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        grass.setPosition(getX() + getOriginX(), getY() + getOriginY());
        grass.update(Gdx.graphics.getDeltaTime());
        grass.draw(batch);
        batch.end();
        for (Trail trail : trails) {
            if (trail.points.size() > 1) {
                PolyLineRenderer.drawLine(trail.points, 0.1f, batch.getProjectionMatrix(), trail.tex);
            }
        }
        batch.begin();
        drawRegion(batch, Resource.GFX.mower);
        drawParticle(batch, exhaust1, new Vector2(-0.3f, 0), 30);
        drawParticle(batch, exhaust2, new Vector2(-0.3f, 0), -30);
        if (health < 0.5f) {
            drawParticle(batch, smoke, new Vector2(-0.1f, 0), 0);
        }
        if (health <= 0) {
            drawParticle(batch, fire[0], new Vector2(0.5f, 0), 50);
            drawParticle(batch, fire[1], new Vector2(0.3f, 0), 150);
            drawParticle(batch, fire[2], new Vector2(0.7f, 0), 210);
        }
        /*Animation anim = attachJoints.isEmpty() ? Resource.GFX.spiderJump: Resource.GFX.spiderIdle;
        TextureRegion frame = anim.getKeyFrame(stateTime);
        drawRegion(batch, frame);

        if (world.isGameInProgress() && world.getMouseTarget() != null) {
            Vector2 impulse = getJumpImpulse();
            float targetLen = impulse.len() * 20;
            float targetAngle = impulse.angle();
            batch.draw(Resource.GFX.target, body.getPosition().x, body.getPosition().y - 0.1f, 0, 0.1f, targetLen, 0.2f,
                    1, 1, targetAngle,
                    0, 0, Resource.GFX.target.getWidth(), Resource.GFX.target.getHeight(), false, false);
        }*/
    }

    private void drawParticle(Batch batch, ParticleEffectPool.PooledEffect effect, Vector2 attach, float rotation) {
        Vector2 center = new Vector2(getX() + getOriginX(), getY() + getOriginY());
        float r = getRotation() + rotation;
        Vector2 p = attach.rotate(r).add(center);
        effect.setPosition(p.x, p.y);
        effect.getEmitters().first().getAngle().setHigh(190 + r, 170 + r);
        effect.update(Gdx.graphics.getDeltaTime());
        effect.draw(batch);
    }

    @Override
    public boolean beginContact(Body me, Body other, Vector2 normal, Vector2 contactPoint) {
        if (other.getUserData() instanceof GrassActor) {
            ((GrassActor) other.getUserData()).kill();
        } else if (other.getUserData() instanceof RockActor) {
            damage(0.51f);
            if (health > 0) {
                ((RockActor) other.getUserData()).kill();
            }
        } else if (other.getUserData() instanceof BarrierActor) {
            damage(1);
        }
        return false;
    }

    private void damage(float amount) {
        health -= amount;
        if (health <= 0) {
            health = 0;
            exhaust1.allowCompletion();
            exhaust2.allowCompletion();
            grass.allowCompletion();
        }
    }

    @Override
    public void endContact(Body other) {

    }

    @Override
    public void hit(float force) {

    }
}

class Trail {
    Texture tex;
    Vector2 localPoint;
    float startTime, lastTime;
    List<Vector2> points = new ArrayList<>();
    float duration = 1.5f;
    boolean active = true;
}
