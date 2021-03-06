package com.happylittlevillage.rituals;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.happylittlevillage.objects.GameObject;
import com.happylittlevillage.gems.Gem;
import com.happylittlevillage.gems.GemBag;
import com.happylittlevillage.gems.GemColour;
import com.happylittlevillage.menu.MenuItem;
import com.happylittlevillage.Assets;
import com.happylittlevillage.village.Village;

import java.util.ArrayList;
import java.util.Collections;

public class RitualAltar extends GameObject implements MenuItem {
	private GemBag gemBag;
	// gonna replace animation with different picture
	private GameObject commenceButton = new GameObject(Assets.getTexture("altar/start_button.png"), position.x - 90, position.y);
	private Rectangle commenceButtonPosition;
	private GameObject removeAllButton = new GameObject(Assets.getTexture("altar/remove_button.png"), position.x - 90, position.y + height - 75);
	private Rectangle removeAllButtonPosition;
	private ArrayList<Ritual> rituals = new ArrayList<Ritual>();
	//new measurements
	public static final int SLOT_SIZE = 86;
	public static final int SPACING = 14;
	private WeeklyRitual weeklyRitual;
	private boolean animating = false;
	private float timer = 0;
	//var for the grid
	private Gem[][] grid; // background stuff
	private int[][] bonus;
	private int[][] addToBonus;
	private Rectangle[][] slots; // render and UI stuff
	private ArrayList<RitualEffect[]> ritualEffects = new ArrayList<RitualEffect[]>();
	private Village village;
	private ArrayList<ArrayList<GridPoint2>> lightUpGrid = new ArrayList<ArrayList<GridPoint2>>();
	private RitualTree ritualTree;

	public RitualAltar(GemBag gemBag, float xPos, float yPos, Village village, RitualTree ritualTree) {
		super(Assets.getTexture("altar/altar.png"), xPos, yPos, 400, 400);
		this.village = village;
		this.ritualTree = ritualTree;
		this.gemBag = gemBag;
		bonus = new int[4][4];
		addToBonus = new int[4][4];
		grid = new Gem[4][4]; // the new ritualAltar, background stuff
		slots = new Rectangle[4][4]; // only for UI. What goes on in the background is handled by grid
		for (int i = 0; i < 4; i++) { // row
			for (int j = 0; j < 4; j++) { //column
				float posX = position.x + SPACING / 2 + (SLOT_SIZE + SPACING) * j;
				float posY = -SLOT_SIZE - SPACING / 2 + height + position.y - ((SPACING + SLOT_SIZE) * i);
				slots[i][j] = new Rectangle(posX, posY, SLOT_SIZE, SLOT_SIZE);
			}
		}
		commenceButtonPosition = new Rectangle(commenceButton.getPosition().x, commenceButton.getPosition().y, commenceButton.getWidth(), commenceButton.getHeight());
		removeAllButtonPosition = new Rectangle(removeAllButton.getPosition().x, removeAllButton.getPosition().y, removeAllButton.getWidth(), removeAllButton.getHeight());
		setWeeklyChosenRitual();
	}

	public void setWeeklyChosenRitual() {
		rituals.clear();
		for (Ritual ritual : ritualTree.getChosenRituals()) {
			rituals.add(ritual);
		}
	}

	//TODO this thing messes up the whole ritual thingy.
	public boolean gainRitual(Ritual ritual) {
		rituals.add(ritual);
		return true;

	}

	public void setGemBag(GemBag gemBag) {
		this.gemBag = gemBag;
	}

	@Override
	public void update(float delta) {
//        if (animating) {
//            int frame = (int) ((timer * 8) % animation.length);
//            texture = animation[frame];
//            timer += delta;
//            if (timer * 8 >= 5) {
//                animating = false;
//                timer = 0;
//                texture = animation[0];
//            }
//        }
	}

	@Override
	public void render(Batch batch) {
		super.render(batch);
		commenceButton.render(batch);
		removeAllButton.render(batch);
		for (int i = 0; i < grid.length; i++) {
			for (int k = 0; k < grid[0].length; k++) {
				if (grid[i][k] != null) {
					batch.draw(grid[i][k].getTexture(), slots[i][k].x, slots[i][k].y, SLOT_SIZE, SLOT_SIZE);
				}
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
			for (int k = 0; k < slots[0].length; k++) {

				if (slots[i][k].contains(x, y)) {
					if (grid[i][k] != null) {
						Gem gemToReturn = grid[i][k];
						grid[i][k] = null;
						return gemToReturn;
					}
				}
			}

		}
		return null;
	}

	public void startAnimating() {
		animating = true;
	}

	public void useGems() {
		weeklyRitual = village.getWeeklyRitual();
		//print out grid first
		for (int gridRow = 0; gridRow < grid.length; gridRow++) { //row gridRow
			for (int gridColumn = 0; gridColumn < grid[0].length; gridColumn++) { //column gridColumn
				if (grid[gridRow][gridColumn] != null) {
//                    for (int firstRecipePosition = 0; firstRecipePosition < weeklyRitual.getRecipe()[0].length; firstRecipePosition++) {
//                        // get the first non-null colour
//                        if (weeklyRitual.getRecipe()[0][firstRecipePosition] != null) {
//                            //check if the first row's non-null colour matches with the grid
//                            if (grid[gridRow][gridColumn].getColour().equals(weeklyRitual.getRecipe()[0][firstRecipePosition])) {
//                                //start specifically checking one recipe
//                                compareRecipe(weeklyRitual, gridRow, gridColumn, firstRecipePosition);
//                                break;
//                            } else {
//                                break;
//                            }
//                        }
//                    }//end checking for one specific recipe

					//iterate through all known grid recipe
					for (int ritualNumber = 0; ritualNumber < rituals.size(); ritualNumber++) {
						// iterate through the first row of a recipe
						for (int firstRecipePosition = 0; firstRecipePosition < rituals.get(ritualNumber).getRecipe()[0].length; firstRecipePosition++) {
							// get the first non-null colour
							if (rituals.get(ritualNumber).getRecipe()[0][firstRecipePosition] != null) {
								//check if the first row's non-null colour matches with the grid
								if (grid[gridRow][gridColumn].getColour().equals(rituals.get(ritualNumber).getRecipe()[0][firstRecipePosition])) {
									//start specifically checking one recipe
									compareRecipe(rituals.get(ritualNumber), gridRow, gridColumn, firstRecipePosition);
									break;
								} else {
									break;
								}
							}
						}//end checking for one specific recipe
					}
				}
			}
		}
		//make rituals affect the village
		ArrayList<RitualEffect> sortedEffects = new ArrayList<RitualEffect>();
		for (RitualEffect[] effects : ritualEffects) {
			if (effects != null) {
				for (RitualEffect effect : effects) {
					sortedEffects.add(effect);
				}
			}
		}
		Collections.sort(sortedEffects);
		for (RitualEffect effect : sortedEffects) {
			effect.affectVillage(village);
		}
		sortedEffects.clear();
		ritualEffects.clear();
		//TODO figure out something to do with bonuses
		//reset bonus
		for (int a = 0; a < bonus.length; a++) {
			for (int b = 0; b < bonus[0].length; b++) {
				bonus[a][b] = 0;
			}
		}
		removeAllGems();
	}

	// compareRecipe compare the recipe according to ritualNumber,
	// having the position of gridRow and  gridColumn
	// and the  firstRecipePosition that the grid encounters
	private void compareRecipe(Ritual ritual, int gridRow, int gridColumn, int firstRecipePosition) {
		GemColour[][] check = ritual.getRecipe(); // check: just to shorten the path
		boolean match = true;
		ArrayList<GridPoint2> addToLightUpGrid = new ArrayList<GridPoint2>();
		//reset addToBonus
		for (int a = 0; a < bonus.length; a++) {
			for (int b = 0; b < bonus[0].length; b++) {
				addToBonus[a][b] = 0;
			}
		}
		//reset addToLightUpGrid
		addToLightUpGrid.clear();
//        System.out.println("Start checkMatch with recipe:" + rituals.get(ritualNumber).getName());
		checkMatch:
		{
			for (int recipeRow = 0; recipeRow < check.length; recipeRow++) { // the length of recipe-row
				for (int recipeColumn = 0; recipeColumn < check[0].length; recipeColumn++) { // the width of recipe-column
					if (check[recipeRow][recipeColumn] != null) { // for every non-void value of recipe.
						//Recipe is out of bound
						if ((gridRow + recipeRow) > 3 || (gridColumn + recipeColumn - firstRecipePosition) > 3 || (gridColumn + recipeColumn - firstRecipePosition) < 0) {
							match = false;
							break checkMatch;
						}
						//Grid does not match the recipe
						else if (grid[gridRow + recipeRow][gridColumn + recipeColumn - firstRecipePosition] == null || !grid[gridRow + recipeRow][gridColumn + recipeColumn - firstRecipePosition].getColour().equals(check[recipeRow][recipeColumn])) {
							match = false;
//                            if (grid[gridRow + recipeRow][gridColumn + recipeColumn - firstRecipePosition] == null) {
//                                System.out.println("DOES NOT MATCH AT" + recipeRow + recipeColumn);
//                            } else {
//                                System.out.println("DOES NOT MATCH AT" + recipeRow + recipeColumn +
//                                        "colour check is:" + check[recipeRow][recipeColumn] + " colour grid is:" + grid[gridRow + recipeRow][gridColumn + recipeColumn - firstRecipePosition].getColour()
//                                        + " At " + (gridRow + recipeRow) + (gridColumn + recipeColumn - firstRecipePosition));
//                            }
							break checkMatch;
						} else {//if the position passes these 2 conditions +1 for the use of that matched position
							addToBonus[gridRow + recipeRow][gridColumn + recipeColumn - firstRecipePosition]++;
						}
					}
				}
			}
		}
		if (match) {
			// add bonus position to bonus
			for (int c = 0; c < bonus.length; c++) {
				for (int d = 0; d < bonus[0].length; d++) {
					if (addToBonus[c][d] != 0) {
						bonus[c][d] += addToBonus[c][d];
						//set x y coordinates for addPoint then add it to addToLightUpGrid which will then be added to the final LightUpGrid
						addToLightUpGrid.add(new GridPoint2(c, d));
					}
				}
			}
			//add used position to lightUpGrid for rendering
			lightUpGrid.add(addToLightUpGrid);
			//add each effect to the arrayList of ritualEffects
			ritualEffects.add(ritual.getEffects());
			if (ritual == weeklyRitual) {
				((WeeklyRitual) ritual).commence();
			}
		}
	}

	public boolean placeRitual(Gem[][] ritual, float x, float y) {
		//loop through the ritual
		for (int row = 0; row < ritual.length; row++) {
			for (int col = 0; col < ritual[0].length; col++) {
				if (ritual[row][col] != null) {
					Vector2 gridMatch = matchOneGrid(x + SLOT_SIZE + SPACING - (ritual[0].length - col) * (SLOT_SIZE + SPACING), y - (row) * (SLOT_SIZE + SPACING));
					if (gridMatch != null) {
						//if we have a matching case, we loop through the ritual again and place all the gems in their grids
						for (int row2 = 0; row2 < ritual.length; row2++) {
							for (int col2 = 0; col2 < ritual[0].length; col2++) {
								if (ritual[row2][col2] != null) {
									int gridRow = (int) gridMatch.x - row + row2;
									int gridCol = (int) gridMatch.y - col + col2;
									if (gridRow >= 0 && gridRow <= 3 && gridCol >= 0 && gridCol <= 3) {
										if (grid[gridRow][gridCol] != null) {
											gemBag.add(grid[gridRow][gridCol].getColour());
										}
										if (gemBag.getAmount(ritual[row2][col2].getColour()) > 0) {
											grid[gridRow][gridCol] = ritual[row2][col2];
											gemBag.remove(ritual[row2][col2].getColour());
										}
									}
								}
							}
						}
						return true;
					}
				}
			}
		}
		return false;
	}

	public boolean placeGem(Gem gem, float x, float y) {
		Vector2 gridMatch = matchOneGrid(x, y);
		if (gridMatch != null) {
			int gridRow = (int) gridMatch.x;
			int gridCol = (int) gridMatch.y;
			if (grid[gridRow][gridCol] != null) {
				gemBag.add(grid[gridRow][gridCol].getColour());
			}
			grid[gridRow][gridCol] = gem;
			return true;
		}
		return false;
	}

	private Vector2 matchOneGrid(float x, float y) { //this check if one gem match any grid
		//x and y is the center of the gem
		//rectangle has to be from the bottom left, so subtract spacing from both x and y
		Rectangle gemBounds = new Rectangle(x - SLOT_SIZE / 2, y - SLOT_SIZE / 2, SLOT_SIZE, SLOT_SIZE);
		//centerGrid contains center of overlapped grids
		double distance; // distance from center of the gem to the grid
		double minDistance = 100; // very arbitrary number
		Vector2 matchGrid = new Vector2(); // row and column of the matched grid
		Vector2 center = new Vector2();
		for (int i = 0; i < 4; i++) {
			for (int k = 0; k < 4; k++) {
				if (slots[i][k].overlaps(gemBounds)) {
					slots[i][k].getCenter(center);
					distance = Math.sqrt(Math.pow(center.x - x, 2) + Math.pow(center.y - y, 2)); // calculate distance from center of the gem to the specific grid
					if (minDistance > distance) {
						minDistance = distance;
						//i is the row and k is the column
						matchGrid.set(i, k);
					}
				}
			}
		}
		if (minDistance != 100) {
			return matchGrid;
		} else {
			return null;
		}
	}

	public GemColour getColour(int row, int col) {
		if (grid[row][col] != null) {
			return grid[row][col].getColour();
		}
		return null;
	}

	private void removeAllGems() {
		for (int i = 0; i < grid.length; i++)
			for (int k = 0; k < grid[0].length; k++) grid[i][k] = null;
	}

	private void clearAltar() {
		for (int i = 0; i < grid.length; i++)
			for (int k = 0; k < grid[0].length; k++) {
				if (grid[i][k] != null) {
					gemBag.add(grid[i][k].getColour());
					grid[i][k] = null;
				}
			}
	}

	@Override
	public boolean interact(float mouseX, float mouseY) {
		pickUpGem(mouseX, mouseY);
		if (commenceButtonPosition.contains(mouseX, mouseY)) {
			useGems();
			return true;
		} else if (removeAllButtonPosition.contains(mouseX, mouseY)) {
			clearAltar();
			return true;
		}
		return false;
	}
}

