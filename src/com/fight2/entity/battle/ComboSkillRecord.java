package com.fight2.entity.battle;

import java.util.ArrayList;
import java.util.List;

public class ComboSkillRecord {
    private int comboId;
    private final List<SkillOperation> operations = new ArrayList<SkillOperation>();

    public int getComboId() {
        return comboId;
    }

    public void setComboId(final int comboId) {
        this.comboId = comboId;
    }

    public List<SkillOperation> getOperations() {
        return operations;
    }

    public enum ComboSkillType {
        BeforeSkill,
        AfterSkill,
        AfterAttack;
    }
}
