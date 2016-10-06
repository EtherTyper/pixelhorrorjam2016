package com.kowaisugoi.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.kowaisugoi.game.audio.AudioManager;
import com.kowaisugoi.game.audio.MusicId;
import com.kowaisugoi.game.control.flags.FlagManager;
import com.kowaisugoi.game.graphics.PlacementRectangle;
import com.kowaisugoi.game.graphics.Transition;
import com.kowaisugoi.game.player.Player;
import com.kowaisugoi.game.player.inventory.PlayerInventory;
import com.kowaisugoi.game.rooms.Room;
import com.kowaisugoi.game.rooms.RoomId;
import com.kowaisugoi.game.rooms.RoomManager;

import static com.kowaisugoi.game.player.Player.InteractionMode.*;

/**
 * Top level entity responsible for rendering the visible world
 */
public class PlayGame implements Screen {
    // 640x360
    public static final float GAME_WIDTH = 160;
    public static final float GAME_HEIGHT = 90;

    private OrthographicCamera _camera;
    private Viewport _viewport;
    private SpriteBatch _batch;
    private ShapeRenderer _shapeRenderer;
    private static boolean _debug = false;
    private static boolean _placing = false;
    private static Transition _transition;

    // Global state
    private static Player _player;
    private static RoomManager _roomManager;
    private static FlagManager _flagManager;

    public static Player getPlayer() {
        return _player;
    }

    public static RoomManager getRoomManager() {
        return _roomManager;
    }

    public static FlagManager getFlagManager() {
        return _flagManager;
    }

    @Override
    public void show() {
        _roomManager = new RoomManager();
        _flagManager = new FlagManager();

        PlayerInventory inventory = new PlayerInventory();

        _batch = new SpriteBatch();
        _shapeRenderer = new ShapeRenderer();
        _camera = new OrthographicCamera();
        _camera.translate(GAME_WIDTH / 2, GAME_HEIGHT / 2);
        _viewport = new StretchViewport(GAME_WIDTH, GAME_HEIGHT, _camera);

        _player = new Player(this, inventory);
        _player.startGame(RoomId.SHED_INTERIOR);

        Gdx.input.setInputProcessor(_player);
    }

    @Override
    public void render(float delta) {
        updateGame(delta);
        renderGame();
    }

    private void updateGame(float delta) {
        Vector3 position = screenToWorldPosition(Gdx.input.getX(), Gdx.input.getY());
        getPlayer().updateCursor(position);
        getPlayer().update(delta);

        if (_transition != null) {
            _transition.update(delta);
        }
    }

    private void renderCurrentRoom() {
        Gdx.gl.glClearColor(1, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        // Draw room sprites
        _camera.update();
        _batch.setProjectionMatrix(_camera.combined);
        _batch.begin();
        getPlayer().getCurrentRoom().draw(_batch);
        getPlayer().getInventory().drawInventory(_batch);
        _batch.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);

        // Draw room shapes
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        _shapeRenderer.setProjectionMatrix(_camera.combined);
        _shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        getPlayer().getCurrentRoom().draw(_shapeRenderer);
        _shapeRenderer.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);
    }

    private void renderFX() {
        // Draw room FX
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_DST_COLOR, GL20.GL_ONE);
        _batch.begin();
        getPlayer().getCurrentRoom().drawFx(_batch);
        _batch.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);
    }

    private void renderInventory() {
        getPlayer().getInventory().drawSelectedItemSprite(_batch);
    }

    private void renderTransitions() {
        // Draw transitions, if applicable
        if (_transition == null) {
            return;
        }

        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        _shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        _transition.draw(_shapeRenderer);
        _shapeRenderer.end();
        _batch.begin();
        _transition.draw(_batch);
        _batch.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);
    }

    private void renderHUD() {
        // Draw player thoughts over everything
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        _shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        getPlayer().getThought().draw(_shapeRenderer);
        _shapeRenderer.end();
        _batch.begin();
        getPlayer().getThought().draw(_batch);
        _batch.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);
    }

    private void renderPlacementHelper() {
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        _shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        _player.getPlacementRectangle().draw(_shapeRenderer);
        _shapeRenderer.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);
    }

    /**
     * Breaks down rendering of game world into different layers
     */
    private void renderGame() {
        renderCurrentRoom();
        renderFX();
        renderPlacementHelper();

        // Draw inventory, if applicable
        _batch.begin();
        if (getPlayer().getInteractionMode() == ITEM_INTERACTION) {
            renderInventory();
        }
        _batch.end();

        renderTransitions();
        renderHUD();
    }

    @Override
    public void resize(int width, int height) {
        _viewport.update(width, height);
    }

    @Override
    public void pause() {
        // TODO Auto-generated method stub

    }

    @Override
    public void resume() {
        // TODO Auto-generated method stub

    }

    @Override
    public void hide() {
        // TODO Auto-generated method stub
    }

    @Override
    public void dispose() {
    }

    public Vector3 screenToWorldPosition(int screenX, int screenY) {
        Vector3 vecCursorPos = new Vector3(screenX, screenY, 0);
        _camera.unproject(vecCursorPos);
        return vecCursorPos;
    }

    /**
     * Play transition animation on transition layer
     *
     * @param t The transition animation to play
     */
    public static void playTransition(Transition t) {
        _transition = t;
        _transition.play();
    }

    public static void setDebug(boolean debug) {
        _debug = debug;
    }

    public static boolean getDebug() {
        return _debug;
    }

    public static void setPlacementMode(boolean mode) {
        _player.think("Placement mode: " + (mode ? "ON" : "OFF"));
        _placing = mode;
    }

    public static boolean getPlacementMode() {
        return _placing;
    }
}
