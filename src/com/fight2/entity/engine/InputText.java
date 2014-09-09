package com.fight2.entity.engine;

import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.ui.activity.BaseGameActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnShowListener;
import android.text.InputType;
import android.view.Gravity;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.fight2.entity.engine.F2ButtonSprite.F2OnClickListener;

public class InputText extends F2ButtonSprite implements F2OnClickListener {

    private final String mTitle;
    private final String mMessage;
    private final Text mText;
    private boolean mIsPassword;
    private String mValue;
    private final BaseGameActivity mContext;

    public InputText(final float pX, final float pY, final String title, final String message, final ITextureRegion texture, final Font font,
            final int textOffsetX, final int textOffsetY, final VertexBufferObjectManager vbo, final BaseGameActivity context) {
        super(pX, pY, texture, texture, vbo);

        this.mMessage = message;
        this.mTitle = title;
        this.mContext = context;
        this.mText = new Text(textOffsetX, textOffsetY, font, "", 256, vbo);
        attachChild(this.mText);
        setOnClickListener(this);
    }

    public String getText() {
        return this.mValue;
    }

    public boolean isPassword() {
        return this.mIsPassword;
    }

    public void setPassword(final boolean isPassword) {
        this.mIsPassword = isPassword;
    }

    public void setText(String text) {
        this.mValue = text;

        if (isPassword() && text.length() > 0)
            text = String.format("%0" + text.length() + "d", 0).replace("0", "*");

        this.mText.setText(text);
    }

    public void showTextInput() {
        mContext.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                final AlertDialog.Builder alert = new AlertDialog.Builder(mContext);

                alert.setTitle(InputText.this.mTitle);
                alert.setMessage(InputText.this.mMessage);

                final EditText editText = new EditText(mContext);
                editText.setTextSize(20f);
                editText.setText(InputText.this.mValue);
                editText.setGravity(Gravity.CENTER_HORIZONTAL);
                if (isPassword())
                    editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

                alert.setView(editText);

                alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialog, final int whichButton) {
                        setText(editText.getText().toString());
                    }
                });

                alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialog, final int whichButton) {

                    }
                });

                final AlertDialog dialog = alert.create();
                dialog.setOnShowListener(new OnShowListener() {
                    @Override
                    public void onShow(final DialogInterface dialog) {
                        editText.requestFocus();
                        final InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
                    }
                });
                dialog.show();
            }
        });
    }

    @Override
    public void onClick(final Sprite pButtonSprite, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
        showTextInput();

    }

}
