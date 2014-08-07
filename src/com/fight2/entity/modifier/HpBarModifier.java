package com.fight2.entity.modifier;

import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.SingleValueSpanEntityModifier;

import com.fight2.entity.HpBar;

public class HpBarModifier extends SingleValueSpanEntityModifier {

    public HpBarModifier(final float pDuration, final float pFromValue, final float pToValue) {
        super(pDuration, pFromValue, pToValue);
    }

    @Override
    protected void onSetInitialValue(final IEntity pItem, final float pValue) {
        final HpBar hpBar = (HpBar) pItem;
        hpBar.setCurrentPoint((int) pValue);
    }

    @Override
    protected void onSetValue(final IEntity pItem, final float pPercentageDone, final float pValue) {
        final HpBar hpBar = (HpBar) pItem;
        hpBar.setCurrentPoint((int) pValue);
    }

    protected HpBarModifier(final HpBarModifier modifier) {
        super(modifier);
    }

    @Override
    public HpBarModifier deepCopy() throws org.andengine.util.modifier.IModifier.DeepCopyNotSupportedException {
        return new HpBarModifier(this);
    }

}
