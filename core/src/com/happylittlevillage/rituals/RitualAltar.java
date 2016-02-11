package com.happylittlevillage.rituals;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Rectangle;
import com.happylittlevillage.Assets;
import com.happylittlevillage.GameObject;
import com.happylittlevillage.gems.Gem;
import com.happylittlevillage.gems.GemBag;
import com.happylittlevillage.gems.GemColour;
import com.happylittlevillage.menu.MenuItem;

import java.util.ArrayList;

public class RitualAltar extends GameObject implements MenuItem {
	private Gem[] gems;
	private GemBag gemBag;
	private Rectangle[] slots;
	private Texture[] animation = Assets.getTextures("altar/altar1.png", "altar/altar2.png", "altar/altar3.png", "altar/altar2.png", "altar/altar1.png");
	private Texture button = Assets.getTexture("altar/button.png");
	private Rectangle commenceButton;
	private ArrayList<Ritual> rituals = new ArrayList<Ritual>();
	private static final int spacingX = 136;
	private static final int spacingY = 121;
	private static final int paddingX = 60;
	private static final int paddingY = 67;
	private static final int slotSize = 64;
	private boolean animating = false;
	private float timer = 0;

	public RitualAltar(GemBag gemBag, float xPos, float yPos) {
		super(Assets.getTexture("altar/altar1.png"), xPos, yPos);
		gems = new Gem[4];
		slots = new Rectangle[4];
		this.gemBag = gemBag;
		slots[0] = new Rectangle(paddingX, paddingY + 64 + spacingY, 64, 64);
		slots[1] = new Rectangle(paddingX + 64 + spacingX, paddingY + 64 + spacingY, 64, 64);
		slots[2] = new Rectangle(paddingX, paddingY, 64, 64);
		slots[3] = new Rectangle(paddingX + 64 + spacingX, paddingY, 64, 64);
		for (int i = 0; i < slots.length; i++) {
			slots[i].x += position.x;
			slots[i].y += position.y;
		}
		commenceButton = new Rectangle(position.x + (width / 2) - (button.getWidth() / 2), position.y, button.getWidth(), button.getHeight() + 30);
		gainStartingRituals();
	}

	private void gainStartingRituals() {
		gainRitual(new AddFoodRitual());
		gainRitual(new AddFoodLoseWaterRitual());
		gainRitual(new AddFoodLoseWaterRitual());
		gainRitual(new AddVillagerRitual());
		gainRitual(new AddWaterRemoveFoodRitual());
		gainRitual(new AddWaterRitual());
		gainRitual(new ToExplorerRitual());
		gainRitual(new ToFarmerRitual());
		gainRitual(new ToMinerRitual());
		gainRitual(new RemoveVillagerRitual());
	}


	public boolean gainRitual(Ritual ritual) {
//		String ritualName = ritual.getName();
//		for (Ritual ritualToCheck : rituals) {
//			if (ritualName.equals(ritualToCheck.getName())) {
//				return false;
//			}
//		}
		rituals.add(ritual);
		return true;

	}

	public void setGemBag(GemBag gemBag) {
		this.gemBag = gemBag;
	}

	@Override
	public void update(float delta) {
		if (animating) {
			int frame = (int) ((timer * 8) % animation.length);
			texture = animation[frame];
			timer += delta;
			if (timer * 8 >= 5) {
				animating = false;
				timer = 0;
				texture = animation[0];
			}
		}
	}

	@Override
	public void render(Batch batch) {
		batch.draw(texture, position.x, position.y);
		batch.draw(button, position.x + (width / 2) - (button.getWidth() / 2), position.y + 30);
		for (int i = 0; i < gems.length; i++) {
			if (gems[i] != null) {
				batch.draw(gems[i].getTexture(), slots[i].x + 8, slots[i].y + 8);
			}
		}
	}

	public void removeRitual(Ritual ritual) {
		for (int i = 0; i < rituals.size(); i++) {
			if (ritual.getName().equals(rituals.get(i).getName())) {
				rituals.remove(i);
			}
		}
	}

	public Gem pickUpGem(float x, float y) {
		for (int i = 0; i < slots.length; i++) {
			if (slots[i].contains(x, y)) {
				if (gems[i] != null) {
					Gem gemToReturn = gems[i];
					gems[i] = null;
					return gemToReturn;
				}
			}
		}
		return null;
	}

	public void startAnimating() {
		animating = true;
	}

	// TODO Duke - use ritual.getRecipe() and ritual.getEffects()
	// add each successful Ritual's effects to an arrayList of RitualEffects
	//
	// once it is done checking, call affectVillage(village) from every RitualEffect
	public void useGems() {
		for (Ritual ritual : rituals) {
			if (ritual.attempt(gems)) {
				startAnimating();
				break;
			}
		}
		removeAllGems();
	}

	public boolean add(Gem gem, float x, float y) {
		Rectangle gemBounds = new Rectangle(x - 32, y - 32, 64, 64);
		for (int i = 0; i < slots.length; i++) {
			if (slots[i].overlaps(gemBounds)) {
				if (gems[i] != null) {
					gemBag.add(gems[i].getColour());
				}
				gems[i] = gem;
				return true;
			}
		}
		return false;
	}


	public GemColour getColour(int index) {
		if (gems[index] != null) {
			return gems[index].getColour();
		}
		return null;
	}

	private void removeAllGems() {
		for (int i = 0; i < gems.length; i++) {
			gems[i] = null;
		}
	}

	@Override
	public boolean interact(float mouseX, float mouseY) {
		if (commenceButton.contains(mouseX, mouseY)) {
			useGems();
			return true;
		}
		return false;
	}
}
