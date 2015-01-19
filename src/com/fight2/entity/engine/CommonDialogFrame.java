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

public class CommonDialogFrame extends DialogFrame {

    public CommonDialogFrame(final float x, final float y, final float width, final float height, final GameActivity activity, final String title,
            final String content) {
        super(x, y, width, height, activity);
        this.setAlpha(0);

        final Font titleFont = ResourceManager.getInstance().newFont(FontEnum.Default, 28);
        final Text nameText = new Text(width * 0.5f, height - 80, titleFont, title, vbom);
        nameText.setColor(0XFF330504);
        EntityUtils.topAlignEntity(nameText, height - 30);
        this.attachChild(nameText);

        final Font detailFont = ResourceManager.getInstance().newFont(FontEnum.Default, 24);
        final Text contentText = new TickerText(width * 0.5f, height * 0.5f, detailFont, content, new TickerTextOptions(AutoWrap.LETTERS, width - 80,
                HorizontalAlign.LEFT, 18), vbom);
        contentText.setColor(0XFF330504);
        this.attachChild(contentText);
        EntityUtils.leftAlignEntity(contentText, 40);
        EntityUtils.topAlignEntity(contentText, height - 65);

    }

}
