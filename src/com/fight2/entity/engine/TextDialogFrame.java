package com.fight2.entity.engine;

import org.andengine.entity.text.AutoWrap;
import org.andengine.entity.text.Text;
import org.andengine.entity.text.TickerText;
import org.andengine.entity.text.TickerText.TickerTextOptions;
import org.andengine.opengl.font.Font;
import org.andengine.util.adt.align.HorizontalAlign;

import com.fight2.GameActivity;
import com.fight2.constant.FontEnum;
import com.fight2.util.EntityUtils;
import com.fight2.util.ResourceManager;

public class TextDialogFrame extends DialogFrame {

    public TextDialogFrame(final float x, final float y, final float width, final float height, final GameActivity activity, final String content) {
        super(x, y, width, height, activity);
        this.setAlpha(0);

        final Font detailFont = ResourceManager.getInstance().newFont(FontEnum.Default, 24);
        final Text contentText = new TickerText(width * 0.5f, height * 0.5f, detailFont, content, new TickerTextOptions(AutoWrap.LETTERS, width - 80,
                HorizontalAlign.LEFT, 25), vbom);
        contentText.setColor(0XFF330504);
        this.attachChild(contentText);
        EntityUtils.leftAlignEntity(contentText, 40);
        EntityUtils.topAlignEntity(contentText, height - 65);

    }

}
