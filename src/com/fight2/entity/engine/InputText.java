package com.fight2.entity.engine;

import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.AutoWrap;
import org.andengine.entity.text.Text;
import org.andengine.entity.text.TextOptions;
import org.andengine.opengl.font.Font;
import org.andengine.util.adt.color.Color;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnShowListener;
import android.text.InputFilter;
import android.text.InputType;
import android.view.Gravity;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.fight2.GameActivity;
import com.fight2.constant.TextureEnum;
import com.fight2.entity.engine.F2ButtonSprite.F2OnClickListener;
import com.fight2.scene.BaseScene;

public class InputText extends Rectangle {
    private static int DEFAULT_SIZE = 6;
    private final String title;
    private final Text text;
    private boolean mIsPassword;
    private String value;
    private final GameActivity activity;
    private final BaseScene scene;
    private final int size;
    private final int border;
    private final int inputType;
    private final boolean multiLine;
    private OnConfirmListener confirmListener;

    public InputText(final float pX, final float pY, final String value, final String title, final Font font, final BaseScene scene) {
        this(pY, pY, 1, 1, value, title, DEFAULT_SIZE, InputType.TYPE_CLASS_TEXT, font, scene, false, false, 0);
    }

    public InputText(final float pX, final float pY, final float width, final float height, final String value, final String title, final int size,
            final int inputType, final Font font, final BaseScene scene, final boolean showBorder, final boolean multiLine, final int border) {
        super(pX, pY, width, height, scene.getVbom());
        this.setColor(Color.BLACK);
        if (showBorder) {
            this.setAlpha(0.4f);
        } else {
            this.setAlpha(0);
        }
        this.scene = scene;
        this.value = value;
        this.title = title;
        this.size = size;
        this.border = border;
        this.inputType = inputType;
        this.multiLine = multiLine;
        this.activity = scene.getActivity();

        if (multiLine) {
            final TextOptions textOptions = new TextOptions(AutoWrap.LETTERS, width - 2 * border);
            this.text = new Text(width * 0.5f, height * 0.5f, font, value, 256, textOptions, scene.getVbom());
            scene.topAlignEntity(text, height - border);
            scene.leftAlignEntity(text, border);
        } else {
            this.text = new Text(width * 0.5f, height * 0.5f, font, value, 256, scene.getVbom());
        }
        this.attachChild(this.text);

        final TextureEnum iconEnum = TextureEnum.COMMON_INPUT_ICON;
        final F2ButtonSprite touchSprite = scene.createALBF2ButtonSprite(iconEnum, iconEnum, width + 10, 0);
        scene.registerTouchArea(touchSprite);
        touchSprite.setOnClickListener(new F2OnClickListener() {
            @Override
            public void onClick(final Sprite pButtonSprite, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
                showTextInput();
            }
        });
        this.attachChild(touchSprite);
    }

    public String getText() {
        return this.value;
    }

    public boolean isPassword() {
        return this.mIsPassword;
    }

    public void setPassword(final boolean isPassword) {
        this.mIsPassword = isPassword;
    }

    public void setText(String textString) {
        this.value = textString;

        if (isPassword() && textString.length() > 0)
            textString = String.format("%0" + textString.length() + "d", 0).replace("0", "*");

        this.text.setText(textString);
        if (multiLine) {
            scene.topAlignEntity(text, this.getHeight() - border);
            scene.leftAlignEntity(text, border);
        }
    }

    public void showTextInput() {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                final AlertDialog.Builder alert = new AlertDialog.Builder(activity);

                alert.setTitle(InputText.this.title);

                final EditText editText = new EditText(activity);
                editText.setTextSize(20f);
                editText.setFilters(new InputFilter[] { new InputFilter.LengthFilter(size) });
                editText.setGravity(Gravity.CENTER_HORIZONTAL);
                if (isPassword()) {
                    editText.setInputType(inputType | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                } else if (multiLine) {
                    editText.setInputType(inputType | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
                } else {
                    editText.setInputType(inputType);
                }
                editText.append(InputText.this.value);
                alert.setView(editText);

                alert.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialog, final int whichButton) {
                        setText(editText.getText().toString());
                        if (confirmListener != null) {
                            confirmListener.onConfirm();
                        }
                    }
                });

                alert.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialog, final int whichButton) {

                    }
                });

                final AlertDialog dialog = alert.create();
                dialog.setOnShowListener(new OnShowListener() {
                    @Override
                    public void onShow(final DialogInterface dialog) {
                        editText.requestFocus();
                        final InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
                    }
                });
                dialog.show();
            }
        });
    }

    public OnConfirmListener getConfirmListener() {
        return confirmListener;
    }

    public void setConfirmListener(final OnConfirmListener confirmListener) {
        this.confirmListener = confirmListener;
    }

    public static interface OnConfirmListener {
        public void onConfirm();
    }

}
