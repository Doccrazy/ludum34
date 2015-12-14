package de.doccrazy.ld34.resources;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import de.doccrazy.shared.core.ResourcesBase;

public class GfxResources extends ResourcesBase {
    public Texture plantTex = textureWrap("plantTex.png");
    public Texture bloodTrailTex = textureWrap("bloodTrailTex.png");
    public Texture fussballTrailTex = textureWrap("fussballTrailTex.png");
    public Texture level1bg = texture("bg.png");
    public Texture level2bg = texture("bg2.png");
    public Texture shockwave = texture("shockwave.png");
    public TextureRegion introFull = new TextureRegion(texture("intro-full.png"));
    public TextureRegion introSplash = new TextureRegion(texture("intro2.png"));
    public TextureRegion intermezzo = new TextureRegion(texture("intermezzo.png"));
    public TextureRegion continueTx = new TextureRegion(texture("continue.png"));
    public TextureRegion thanksTx = new TextureRegion(texture("thanks.png"));
    public TextureRegion defeat = new TextureRegion(texture("defeat.png"));

    public Sprite[] leaves = new Sprite[]{atlas.createSprite("leaf1")};
    public Sprite[] rocks = new Sprite[]{atlas.createSprite("rock")};
    public Sprite[] grass = new Sprite[]{atlas.createSprite("grass1"), atlas.createSprite("grass2"), atlas.createSprite("grass3"), atlas.createSprite("grass4")};
    public Sprite mower = atlas.createSprite("mower");
    public Sprite fussball = atlas.createSprite("fussball");
    public Sprite[] blood = new Sprite[]{atlas.createSprite("blood1"), atlas.createSprite("blood2"), atlas.createSprite("blood3"), atlas.createSprite("blood4")};

    public ParticleEffectPool partExhaust = particle("exhaust.p", 0.01f);
    public ParticleEffectPool partGrass = particle("grass.p", 0.018f);
    public ParticleEffectPool partFire = particle("fire.p", 0.01f);
    public ParticleEffectPool partSmoke = particle("smoke.p", 0.008f);

    /*public Texture threadStructure = textureWrap("thread-structure.png");
    public Texture threadSticky = textureWrap("thread-sticky.png");
    public Texture threadCounter = textureWrap("thread-counter.png");
    public Texture target = texture("target.png");
    public Texture level1fg = texture("level1-fg.png");
    public Texture level2fg = texture("level2.png");
    public Texture level2Tutorial = texture("level2-tutorial.png");
    //public Sprite[] fly = createBlurLevels(atlas.createSprite("fly"), 16);
    public Sprite dummy = atlas.createSprite("dummy");
    public Sprite counterweight = atlas.createSprite("counterweight");
    public Sprite dustMote = atlas.createSprite("dustMote");
    public Animation spiderIdle = new Animation(0.016f, atlas.findRegions("spider_idle/spider_idle"), PlayMode.LOOP);
    public Animation spiderJump = new Animation(0.016f, atlas.findRegions("spider_jump/spider_jump"), PlayMode.LOOP);
    public Animation[] fly1 = createBlurLevels(new Animation(0.010f, atlas.findRegions("fly_anim/fly"), PlayMode.LOOP), 4, 6);
    public Sprite iconNone = atlas.createSprite("icon/none");
    public Map<ThreadType, Sprite> icons = new HashMap<ThreadType, Sprite>();
    public Sprite selection = atlas.createSprite("selection");*/


    public GfxResources() {
        super("game.atlas");
    }

}
