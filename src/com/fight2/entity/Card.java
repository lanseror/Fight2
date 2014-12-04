package com.fight2.entity;

public class Card {
    private int id;
    private String avatar;
    private String image;
    private String name;
    private int star;
    private int level;
    private int tier;// Evolution tier
    private int hp;
    private int atk;// Attack value;
    private int exp;
    private int baseExp;
    private String skill;
    private int skillLevel;
    private int templateId;
    private int version;
    private int amount;
    private Race race;
    private boolean avatarLoaded = false;
    private boolean imageLoaded = false;

    public Card() {
        super();
    }

    public Card(final Card card) {
        super();
        this.id = card.getId();
        this.avatar = card.getAvatar();
        this.image = card.getImage();
        this.name = card.getName();
        this.star = card.getStar();
        this.level = card.getLevel();
        this.tier = card.getTier();
        this.hp = card.getHp();
        this.atk = card.getAtk();
        this.exp = card.getExp();
        this.baseExp = card.getBaseExp();
        this.skill = card.getSkill();
        this.skillLevel = card.getSkillLevel();
        this.templateId = card.getTemplateId();
        this.version = card.getVersion();
        this.amount = card.getAmount();
        this.race = card.getRace();
        this.avatarLoaded = card.isAvatarLoaded();
        this.imageLoaded = card.isImageLoaded();
    }

    public int getId() {
        return id;
    }

    public void setId(final int id) {
        this.id = id;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(final String avatar) {
        this.avatar = avatar;
    }

    public String getImage() {
        return image;
    }

    public void setImage(final String image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public int getStar() {
        return star;
    }

    public void setStar(final int star) {
        this.star = star;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(final int level) {
        this.level = level;
    }

    public int getTier() {
        return tier;
    }

    public void setTier(final int tier) {
        this.tier = tier;
    }

    public int getHp() {
        return hp;
    }

    public void setHp(final int hp) {
        this.hp = hp;
    }

    public int getAtk() {
        return atk;
    }

    public void setAtk(final int atk) {
        this.atk = atk;
    }

    public int getExp() {
        return exp;
    }

    public void setExp(final int exp) {
        this.exp = exp;
    }

    public int getBaseExp() {
        return baseExp;
    }

    public void setBaseExp(final int baseExp) {
        this.baseExp = baseExp;
    }

    public String getSkill() {
        return skill;
    }

    public void setSkill(final String skill) {
        this.skill = skill;
    }

    public int getSkillLevel() {
        return skillLevel;
    }

    public void setSkillLevel(final int skillLevel) {
        this.skillLevel = skillLevel;
    }

    public int getTemplateId() {
        return templateId;
    }

    public void setTemplateId(final int templateId) {
        this.templateId = templateId;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(final int version) {
        this.version = version;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(final int amount) {
        this.amount = amount;
    }

    public Race getRace() {
        return race;
    }

    public void setRace(final Race race) {
        this.race = race;
    }

    public boolean isAvatarLoaded() {
        return avatarLoaded;
    }

    public void setAvatarLoaded(final boolean avatarLoaded) {
        this.avatarLoaded = avatarLoaded;
    }

    public boolean isImageLoaded() {
        return imageLoaded;
    }

    public void setImageLoaded(final boolean imageLoaded) {
        this.imageLoaded = imageLoaded;
    }

    public static enum Race {
        Human,
        Angel,
        Elf,
        Devil;
    }
}
