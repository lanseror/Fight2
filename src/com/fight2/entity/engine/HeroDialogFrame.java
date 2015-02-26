package com.fight2.entity.engine;

import org.andengine.entity.text.AutoWrap;
import org.andengine.entity.text.Text;
import org.andengine.entity.text.TickerText;
import org.andengine.entity.text.TickerText.TickerTextOptions;
import org.andengine.opengl.font.Font;
import org.andengine.util.adt.align.HorizontalAlign;

import com.fight2.GameActivity;
import com.fight2.constant.FontEnum;
import com.fight2.entity.Card;
import com.fight2.util.EntityUtils;
import com.fight2.util.ResourceManager;

public class HeroDialogFrame extends DialogFrame {
    public HeroDialogFrame(final float x, final float y, final float width, final float height, final GameActivity activity, final Card card,
            final String content) {
        this(x, y, width, height, activity, card, card.getName(), content);
    }

    public HeroDialogFrame(final float x, final float y, final float width, final float height, final GameActivity activity, final Card card,
            final String title, final String content) {
        super(x, y, width, height, activity);
        this.setAlpha(0);

        final CardAvatar avatar = new CardAvatar(100, height - 80, 80, 80, card, activity);
        this.attachChild(avatar);

        final Font titleFont = ResourceManager.getInstance().newFont(FontEnum.Default, 28);
        final Text nameText = new Text(250, height - 80, titleFont, title, vbom);
        nameText.setColor(0XFF330504);
        EntityUtils.leftAlignEntity(nameText, 160);
        EntityUtils.topAlignEntity(nameText, height - 30);
        this.attachChild(nameText);

        final Font detailFont = ResourceManager.getInstance().newFont(FontEnum.Default, 24);
        final Text contentText = new TickerText(width * 0.5f, height * 0.5f, detailFont, content, new TickerTextOptions(AutoWrap.LETTERS, width - 185,
                HorizontalAlign.LEFT, 25), vbom);
        contentText.setColor(0XFF330504);
        this.attachChild(contentText);
        EntityUtils.leftAlignEntity(contentText, 160);
        EntityUtils.topAlignEntity(contentText, height - 65);

    }

}
