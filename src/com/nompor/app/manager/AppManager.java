package com.nompor.app.manager;

import java.awt.Image;

import com.nompor.app.view.GameMode;
import com.nompor.app.view.RankingMode;
import com.nompor.app.view.TitleMode;
import com.nompor.app.view.ViewType;
import com.nompor.gtk.GTKManager;
import com.nompor.gtk.GameView;

//ゲームの画像や音声再生、画面遷移を制御するメソッド群
public class AppManager {

	//ゲームBGM開始
	public static void gameBGMStart() {
		GTKManager.loopBGM("bgm/bgm.wav");
	}

	//ゲームBGMの停止
	public static void gameBGMStop() {
		GTKManager.stopBGM();
	}

	//的破壊効果音を鳴らす
	public static void breakSE() {
		GTKManager.playSE("se/break.wav");
	}

	//メニュー選択効果音を鳴らす
	public static void selectSE() {
		GTKManager.playSE("se/select.wav");
	}

	//カウントダウン効果音を鳴らす
	public static void countSE() {
		GTKManager.playSE("se/count.wav");
	}

	//ゲーム開始効果音を鳴らす
	public static void startSE() {
		GTKManager.playSE("se/start.wav");
	}

	//ゲーム終了効果音を鳴らす
	public static void endSE() {
		GTKManager.playSE("se/end.wav");
	}

	//的画像を取得
	public static Image getTargetImage() {
		return GTKManager.getImage("img/mato.png");
	}

	//ブロック画像を取得
	public static Image getBlockImage() {
		return GTKManager.getImage("img/kabe.png");
	}

	//背景画像を取得
	public static Image getBackImage() {
		return GTKManager.getImage("img/back.png");
	}

	//ゲームプログラムを開始する
	public static void start(ViewType type) {
		//ウィンドウの表示
		GTKManager.start("的当てゲーム","img/icon.png",800, 600, get(type));

		//事前にロードしなくても良いが、使用する時にロードすると遅くなるかもしれないので、事前にロードする

		//初期化時に全画像ファイルをメモリ上に展開する
		GTKManager.loadImage("img/mato.png");
		GTKManager.loadImage("img/kabe.png");
		GTKManager.loadImage("img/back.png");

		//初期化時に全効果音ファイルをメモリ上に展開する
		GTKManager.loadSE("se/break.wav");
		GTKManager.loadSE("se/start.wav");
		GTKManager.loadSE("se/select.wav");
		GTKManager.loadSE("se/count.wav");

		//BGMの再生
		AppManager.gameBGMStart();
	}

	//ゲーム画面を遷移する
	public static void change(ViewType type) {
		GTKManager.changeView(get(type));
	}

	//ウィンドウ横幅取得
	public static int getW() {
		return GTKManager.getWidth();
	}

	//ウィンドウ縦幅取得
	public static int getH() {
		return GTKManager.getHeight();
	}

	//ゲーム画面を取得する
	private static GameView get(ViewType type) {
		switch(type) {
		case TITLE:
			return new TitleMode();
		case GAME:
			return new GameMode();
		case RANKING:
			return new RankingMode();
		default:
			break;
		}
		return null;
	}

	//アプリケーションを終了する
	public static void end() {
		GTKManager.end();
	}
}
