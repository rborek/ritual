package game.ritual;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import game.ritual.gems.GemBook;
import game.ritual.gems.Gem;
import game.ritual.gems.GemBag;
import game.ritual.input.InputHandler;
import game.ritual.messages.*;
import game.ritual.rituals.Ritual;
import game.ritual.rituals.RitualAltar;
import game.ritual.rituals.RitualBook;
import game.ritual.village.Village;
import game.ritual.village.Villager;
import game.ritual.village.VillagerRole;

public class GameHandler {
	private Village village;
	private RitualAltar ritualAltar;
	private Texture background = Assets.getTexture("bg/background.png");
	private GemBag gemBag;
	private InputHandler inputHandler;
	private Gem gem;
	private Texture scroll = Assets.getTexture("ui/scroll.png");
	private boolean paused;
	private GemBook miniBook = new GemBook(this);
	private boolean bookOpen;
	private RitualBook ritualBook = new RitualBook(70, 160);
	private WinMessage winMessage;
	private ShapeRenderer shapeRenderer = new ShapeRenderer();
	private boolean DEBUG = false;
	// all menuitems is put here
	private MessageBox messageBox;
	private GemSummary gemSummary;
	private GodMessage godMessage;
	private GameOver gameOverMessage;
	private boolean win = false;
	private boolean intro = true;
	private boolean gameOver = false;

	public RitualAltar getRitualAltar() {
		return ritualAltar;
	}

	public GemBag getGemBag() {
		return gemBag;
	}

	public MessageBox getMessageBox() {
		return messageBox;
	}

	public RitualBook getRitualBook() {
		return ritualBook;
	}

	public GemBook getMiniBook() {
		return miniBook;
	}


	public GameHandler(InputHandler inputHandler) {
		this.inputHandler = inputHandler;
		init();
	}

	public void init() {
		gemBag = new GemBag(1280 - 420 - 36 - 32, 30 + 35 - 12);
		ritualAltar = new RitualAltar(gemBag, 1280 - 400 - 48 - 30, 720 - 400 - 40 - 12, 2, 2);
		village = new Village();
		for (int i = 0; i < 500; i++) {
			village.addVillager(VillagerRole.CITIZEN);
		}
		gemSummary = new GemSummary(this);
		messageBox = new MessageBox("  Welcome to your happy little village!\n Efficiently maintain your villagers'\n happiness"
				+ " by giving them food and\n water! Combine gems from your bag \n to gain or sacrifice different \n resources and villagers! You can\n combine"
				+ " a maximum of 4 gems\n of any kind! ", this);
		gameOverMessage = new GameOver(this);
		winMessage = new WinMessage(this);
		Ritual.setVillage(village);
		ritualAltar.gainRitual(village.getWeeklyRitual());
		Gdx.input.setInputProcessor(inputHandler);
		pause();

	}

	public void pause() {
		paused = true;
		inputHandler.disable();
	}

	public void unpause() {
		if (messageBox instanceof WeekSummary) {
			messageBox = new GemSummary(gemBag, village, this);
			((GemSummary) messageBox).gemMined();

		} else if (messageBox instanceof GemSummary) {
			messageBox = new GodMessage(gemBag, village, this);
			if (((GodMessage) messageBox).checkRitual()) {
				ritualAltar.removeRitual(village.getWeeklyRitual());
				village.newWeeklyRitual();
				ritualAltar.gainRitual(village.getWeeklyRitual());
			}
			((GodMessage) messageBox).stateRitual();
		} else if (messageBox instanceof MessageBox) {
			messageBox = new WeekSummary(village, this);
			paused = false;
			inputHandler.enable();

		}

	}

	// game logic goes here
	public void update(float delta) {
		if (win == false) {
			if (village.getSize() <= 0) {
				gameOverMessage.setCondition(0);
				gameOver = true;
			} else if (village.getFood() <= 0) {
				gameOverMessage.setCondition(1);
				gameOver = true;
			} else if (village.getWater() <= 0) {
				gameOverMessage.setCondition(2);
				gameOver = true;
			} else if ((!village.getWeeklyRitual().isComplete() && village.getDaysLeft() < 0)) {
				gameOverMessage.setCondition(3);
				gameOver = true;
			}
		}
		if (gameOver == false) {
			if (village.getSize() < -1) {
				winMessage.setCondition(1);
				win = true;
			}
		}

		if (!paused && gameOver == false && win == false) {
			if (village.isNextDay()) {
				pause();
				village.gatheredFood();
				village.gatheredWater();
			}
			village.update(delta);
			ritualAltar.update(delta);
		}

	}

	public Village getVillage() {
		return village;
	}

	public void openBook() {
		bookOpen = true;
	}

	public void closeBook() {
		bookOpen = false;
	}


	// rendering goes here
	public void render(Batch batch) {
		batch.draw(background, 0, 0);
		village.render(batch);
		batch.draw(scroll, 1280 - 550, -12);
		ritualAltar.render(batch);
		gemBag.render(batch);
		inputHandler.renderSelectedGem(batch);
		miniBook.render(batch);
		if (!gameOver && !win) {
			if (bookOpen) {
				ritualBook.render(batch);
			}
			if (paused) {
				messageBox.render(batch);
			} else {
				if (DEBUG) {
					shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
					Villager.renderLines(shapeRenderer);
					shapeRenderer.end();
				}
			}
		} else if (gameOver && !win) {
			gameOverMessage.render(batch);
		} else if (!gameOver && win){
			winMessage.render(batch);
		}
	}

}
