package de.doccrazy.ld34.game.actor;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import de.doccrazy.ld34.core.Resource;
import de.doccrazy.ld34.game.world.GameWorld;
import de.doccrazy.shared.game.actor.ShapeActor;
import de.doccrazy.shared.game.base.CollisionListener;
import de.doccrazy.shared.game.base.KeyboardMovementListener;
import de.doccrazy.shared.game.base.MovementInputListener;
import de.doccrazy.shared.game.world.BodyBuilder;
import de.doccrazy.shared.game.world.ShapeBuilder;

import java.util.HashMap;
import java.util.Map;

public class PlayerActor extends ShapeActor<GameWorld> implements CollisionListener {
    private static final float RADIUS = 0.07f;
    private static final float JUMP_STRENGTH = 0.04f;
    private static final float JUMP_MIN = 0.03f;
    private static final float JUMP_MAX = 0.10f;

    private MovementInputListener movement;
    private boolean jump;
    private Map<Body, Vector2> contacts = new HashMap<>();
    private float stateTime;

    public PlayerActor(GameWorld world, Vector2 spawn) {
        super(world, spawn, false);
        setUseRotation(false);
        setScale(4, 2.5f);
        setzOrder(50);
    }

    @Override
    protected BodyBuilder createBody(Vector2 spawn) {
        return BodyBuilder.forDynamic(spawn).gravityScale(0.5f).noRotate()
                .fixSensor().fixShape(ShapeBuilder.circle(RADIUS)).fixProps(1, 1, 1f);
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
        stateTime += delta;
        super.doAct(delta);
    }

    private void jump(float delta) {
        Vector2 jumpImpulse = getJumpImpulse();
        System.out.println(jumpImpulse.len());
        body.applyLinearImpulse(jumpImpulse, body.getPosition(), true);
        //Resource.SOUND.jump.play();
    }

    private Vector2 getJumpImpulse() {
        Vector2 jumpImpulse = world.getMouseTarget().cpy().sub(body.getPosition());
        jumpImpulse = jumpImpulse.scl(JUMP_STRENGTH).clamp(JUMP_MIN, JUMP_MAX);
        return jumpImpulse;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
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

    public void setJump(boolean jump) {
        this.jump = jump;
    }

    @Override
    public boolean beginContact(Body me, Body other, Vector2 normal, Vector2 contactPoint) {
        contacts.put(other, contactPoint);
        return true;
    }

    @Override
    public void endContact(Body other) {
        contacts.remove(other);
    }

    @Override
    public void hit(float force) {
    }
}
