package com.fight2.scene;

import java.io.IOException;

import org.andengine.entity.IEntity;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.scene.background.EntityBackground;
import org.andengine.entity.sprite.Sprite;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.util.adt.color.Color;

import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.fight2.GameActivity;
import com.fight2.constant.SceneEnum;
import com.fight2.constant.TextureEnum;
import com.fight2.entity.ChatBoxCloseButton;
import com.fight2.util.ResourceManager;
import com.fight2.util.TextureFactory;

public class ChatScene extends BaseScene {
    private final ChatBoxCloseButton closeButton;

    public ChatScene(final GameActivity activity) throws IOException {
        super(activity);
        closeButton = createCloseButton(TextureEnum.CHAT_INPUT_CLOSE, this.simulatedRightX - 50, this.cameraHeight - 50);
        final IEntity closeTouchArea = new Rectangle(this.simulatedRightX - 75, this.cameraHeight - 75, 150, 150, vbom) {
            @Override
            public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
                if (pSceneTouchEvent.isActionUp()) {
                    goBack();
                    return true;
                }
                return false;

            }
        };
        closeTouchArea.setAlpha(0);
        closeButton.setUserData(closeTouchArea);
        this.attachChild(closeButton);
        this.attachChild(closeTouchArea);
        this.registerTouchArea(closeTouchArea);
        this.setTouchAreaBindingOnActionDownEnabled(true);
        this.setTouchAreaBindingOnActionMoveEnabled(true);
        init();
    }

    private void goBack() {
        final EditText editText = activity.getChatText();
        final InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
        ResourceManager.getInstance().setCurrentScene(SceneEnum.Main);
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                editText.setVisibility(View.INVISIBLE);
                closeButton.toggle();

            }

        });
    }

    @Override
    protected void init() throws IOException {
        final IEntity bgEntity = new Rectangle(cameraCenterX, cameraCenterY, cameraWidth, cameraHeight, vbom);
        bgEntity.setColor(Color.BLACK);
        bgEntity.setAlpha(0.5f);
        final Background background = new EntityBackground(bgEntity);
        this.setBackground(background);

        final IEntity leftCloseTouchArea = new Rectangle(this.simulatedLeftX + 60, 40, 120, 80, vbom) {
            @Override
            public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
                if (pSceneTouchEvent.isActionUp()) {
                    goBack();
                    return true;
                }
                return false;

            }
        };
        leftCloseTouchArea.setAlpha(0);
        this.attachChild(leftCloseTouchArea);
        this.registerTouchArea(leftCloseTouchArea);

        final Sprite chatInputSprite = createALBImageSprite(TextureEnum.CHAT_INPUT_BG, this.simulatedLeftX, 0);
        this.attachChild(chatInputSprite);
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                closeButton.initChatTextY();
            }

        });
    }

    @Override
    public void updateScene() {
        activity.runOnUpdateThread(new Runnable() {
            @Override
            public void run() {
                closeButton.toggle();
            }

        });

    }

    private ChatBoxCloseButton createCloseButton(final TextureEnum normalTextureEnum, final float x, final float y) {
        final TextureFactory textureFactory = TextureFactory.getInstance();
        final ITextureRegion normalTexture = textureFactory.getAssetTextureRegion(normalTextureEnum);
        final float width = normalTextureEnum.getWidth();
        final float height = normalTextureEnum.getHeight();
        final float pX = x + width * 0.5f;
        final float pY = y + height * 0.5f;
        final ChatBoxCloseButton sprite = new ChatBoxCloseButton(pX, pY, normalTexture, activity);
        return sprite;
    }

    @Override
    public void leaveScene() {
        // TODO Auto-generated method stub
        
    }

}
