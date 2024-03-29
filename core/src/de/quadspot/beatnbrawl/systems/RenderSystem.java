package de.quadspot.beatnbrawl.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.ComponentType;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Bits;
import de.quadspot.beatnbrawl.components.AnimationComponent;
import de.quadspot.beatnbrawl.components.MapComponent;
import de.quadspot.beatnbrawl.components.PositionComponent;
import de.quadspot.beatnbrawl.components.RenderComponent;
import java.util.Comparator;

/**
 * Created by goetsch on 05.08.14.
 */
public class RenderSystem extends EntitySystem {
    private ImmutableArray<Entity> entities;
    private Entity mapEntity;
    private float elapsedTime = 0;


    private OrthographicCamera camera;
    private SpriteBatch batch;

    public RenderSystem(OrthographicCamera camera, SpriteBatch batch){
        this.camera = camera;
        this.batch = batch;
    }

    @Override
    public void addedToEngine(Engine engine) {
        entities = engine.getEntitiesFor(Family.getFor(ComponentType.getBitsFor(PositionComponent.class, RenderComponent.class, AnimationComponent.class), new Bits(), new Bits()));
        mapEntity = engine.getEntitiesFor(Family.getFor(ComponentType.getBitsFor(MapComponent.class), new Bits(), new Bits())).first();
        ComponentMapper <PositionComponent> pcm = ComponentMapper.getFor(PositionComponent.class);
        ComponentMapper <MapComponent> mapcm = ComponentMapper.getFor(MapComponent.class);
        camera.position.set(Gdx.graphics.getWidth()/2, mapcm.get(mapEntity).getMapHeight()/2, 0);
        camera.update();
    }

    @Override
    public void removedFromEngine(Engine engine) {
        super.removedFromEngine(engine);
    }

    @Override
    public void update(float deltaTime) {
        ComponentMapper <PositionComponent> pcm = ComponentMapper.getFor(PositionComponent.class);
        ComponentMapper <RenderComponent> rcm = ComponentMapper.getFor(RenderComponent.class);
        ComponentMapper <MapComponent> mapcm = ComponentMapper.getFor(MapComponent.class);
        ComponentMapper <AnimationComponent> acm = ComponentMapper.getFor(AnimationComponent.class);


        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.setProjectionMatrix(camera.combined);


        if ((pcm.get(entities.first()).getPosition().x > camera.viewportWidth/2) && (pcm.get(entities.first()).getPosition().x < mapcm.get(mapEntity).getMapWidth()-camera.viewportWidth/2)){
            camera.position.set(pcm.get(entities.first()).getPosition().x, mapcm.get(mapEntity).getMapHeight()/2, 0);
        }

        camera.update();
        mapcm.get(mapEntity).getTiledMapRenderer().setView(camera);
        mapcm.get(mapEntity).getTiledMapRenderer().render();

        
        //Z-Sort versuch
        
        /*
        Array<Sprite> sort = new Array();
        
        for(int i = 0; i < entities.size(); ++i){
            Entity entity = entities.get(i);
            elapsedTime += deltaTime;
            
            sort.add(new Sprite(acm.get(entity).getCurrentAnimation().getKeyFrame(elapsedTime))); 
            sort.get(i).setPosition(pcm.get(entity).getPosition().x, pcm.get(entity).getPosition().y+pcm.get(entity).getPosition().z);
            sort.get(i).setRegionWidth(acm.get(entity).getWidth(elapsedTime));
            sort.get(i).setRegionHeight(acm.get(entity).getHeight(elapsedTime));
            
            sort.get(i).setScale(4, 4);
                    
        }
        //@TODO: implemet Comperator;
        sort.sort(new Comparator<Sprite>() {
                    public int compare(Sprite s0, Sprite s1) {
                    return s0.getRegionY() - s1.getRegionY();
                    }
                });
        
        batch.begin();

        for (Sprite sprite : sort) {
            sprite.draw(batch);
        }
        */
        //-------------------------
        
        
        batch.begin();

        for(int i = 0; i < entities.size(); ++i){
            Entity entity = entities.get(i);
            elapsedTime += deltaTime;
            //batch.draw(acm.get(entity).getCurrentAnimation().getKeyFrame(elapsedTime), pcm.get(entity).getPosition().x, pcm.get(entity).getPosition().y+pcm.get(entity).getPosition().z);
            batch.draw(acm.get(entity).getCurrentAnimation().getKeyFrame(elapsedTime), pcm.get(entity).getPosition().x, pcm.get(entity).getPosition().y+pcm.get(entity).getPosition().z,
                    0,0,acm.get(entity).getWidth(elapsedTime),acm.get(entity).getHeight(elapsedTime),4,4,0);
            //batch.draw(render.getImg(), 300, 300);
            //System.out.println(deltaTime);
            //System.out.println(acm.get(entity).getWalkRightAnimation().getKeyFrames().length);
            // getKeyFrameIndex(deltaTime));
        }

        batch.end();
    }
}

