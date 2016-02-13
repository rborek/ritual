package com.happylittlevillage;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.TextureLoader.TextureParameter;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;

import java.util.HashMap;

public class Assets {
	private static final AssetManager manager = new AssetManager();
	private static final HashMap<Integer, BitmapFont> fonts = new HashMap<Integer, BitmapFont>();

	// returns the texture of a given file path
	public static Texture getTexture(String path) {
		return manager.get("textures/" + path, Texture.class);
	}

	// returns an array of all the textures listed
	public static Texture[] getTextures(String... paths) {
		Texture[] textures = new Texture[paths.length];
		for (int i = 0; i < paths.length; i++) {
			textures[i] = manager.get("textures/" + paths[i], Texture.class);
		}
		return textures;
	}

	public static void updateFonts() {
		Object[] keys = fonts.keySet().toArray();
		for (int i = 0; i < keys.length; i++) {
			BitmapFont font = generateFont(((Integer) keys[i]).intValue());
			fonts.put((Integer)keys[i], font);
		}
	}

	private static BitmapFont generateFont(int size) {
		FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/palitoon.otf"));
		FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
		float scale = 1.0f * Gdx.graphics.getWidth() / GameScreen.WIDTH * Gdx.graphics.getHeight() / GameScreen.HEIGHT;
		if (scale < 1) {
			scale = 1;
		}
		parameter.borderColor = Color.BLACK;
		parameter.borderWidth = 1.5f * scale;
		parameter.minFilter = Texture.TextureFilter.Linear;
		parameter.magFilter = Texture.TextureFilter.Linear;
		parameter.size = (int) Math.round(size * scale);
		BitmapFont font = generator.generateFont(parameter);
		font.getData().setScale(1 / scale);
		return font;
	}

	public static BitmapFont getFont(int size) {
		if (fonts.containsKey(new Integer(size))) {
			return fonts.get(new Integer(size));
		} else {
			BitmapFont font = generateFont(size);
			fonts.put(new Integer(size), font);
			return font;
		}
	}

	// returns an array of textures given a folder/prefix
	public static Texture[] getTextures(String prefix) {
		FileHandle dir = Gdx.files.internal("prefix");
		if (dir.isDirectory()) {
			Texture[] textures = new Texture[dir.list().length];
			for (int i = 0; i < dir.list().length; i++) {
				textures[i] = manager.get(dir.list()[i].toString(), Texture.class);
			}
			return textures;
		}
		return null;
	}

	private static void loadTextures(TextureParameter param) {
		loadTextures(Gdx.files.internal("textures"), param);
	}

	// recursively goes through every directory, loading all files within them as a Texture
	private static void loadTextures(FileHandle dir, TextureParameter param) {
		for (FileHandle file : dir.list()) {
			if (file.isDirectory()) {
				loadTextures(file, param);
			} else {
				System.out.println("loading " + file);
				manager.load(file.toString(), Texture.class, param);
			}
		}
	}

	public static void load() {
		TextureParameter param = new TextureParameter();
		param.genMipMaps = true;
		param.minFilter = TextureFilter.MipMapLinearLinear;
		param.magFilter = TextureFilter.Linear;
		loadTextures(param);
		manager.finishLoading();
	}

	public static void dispose() {

	}
}
