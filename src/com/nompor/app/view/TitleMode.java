package com.nompor.app.view;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseEvent;

import com.nompor.app.manager.AppManager;
import com.nompor.gtk.GTKManager;
import com.nompor.gtk.GameView;
import com.nompor.gtk.draw.DrawLabel;
import com.nompor.gtk.draw.GraphicsUtil;

public class TitleMode extends GameView {

	//タイトル画面の文字列オブジェクト
	DrawLabel title;
	DrawLabel start;
	DrawLabel ranking;
	DrawLabel description;
	DrawLabel end;

	//マウスカーソルの位置
	Point p = new Point();

	//フォントオブジェクトの構築
	Font titleFont = new Font(Font.MONOSPACED,Font.BOLD, 60);
	Font defaultFont = new Font(Font.MONOSPACED,Font.PLAIN, 25);

	int cx;

	//画面状態フラグ
	boolean isDescription=false;

	//ウィンドウにこのオブジェクトが設置されたら最初に呼び出しされる
	public void start() {

		//中心座標の取得
		cx = GTKManager.getWidth()/2;

		//文字列オブジェクトを構築する
		Graphics g = getGraphics();
		title = new DrawLabel(g, "的当てゲーム", titleFont, cx, 100);
		start = new DrawLabel(g, "ゲーム開始", defaultFont, cx, 350);
		ranking = new DrawLabel(g, "ランキング", defaultFont, cx, 400);
		description = new DrawLabel(g, "説明書", defaultFont, cx, 450);
		end = new DrawLabel(g, "終了", defaultFont, cx, 500);
	}

	//描画処理
	public void draw(Graphics g) {
		drawTitle(g);
		if ( isDescription ) {
			drawDescription(g);
		}
	}

	//タイトルの描画
	void drawTitle(Graphics g) {
		//アンチエイリアシングの有効化
		GraphicsUtil.setTextAntialiasing(g, true);

		//背景描画
		g.drawImage(AppManager.getBackImage(), 0, 0, null);

		//的描画（飾り）
		GraphicsUtil.drawCenteringImage(g, AppManager.getTargetImage(), cx, 200);

		//タイトル文字列描画
		g.setColor(Color.WHITE);
		g.setFont(titleFont);
		title.draw(g);

		//選択肢描画
		g.setFont(defaultFont);
		g.setColor(start.contains(p) ? Color.ORANGE : Color.WHITE);
		start.draw(g);
		g.setColor(ranking.contains(p) ? Color.ORANGE : Color.WHITE);
		ranking.draw(g);
		g.setColor(description.contains(p) ? Color.ORANGE : Color.WHITE);
		description.draw(g);
		g.setColor(end.contains(p) ? Color.ORANGE : Color.WHITE);
		end.draw(g);

	}

	//説明の描画
	void drawDescription(Graphics g) {
		g.setColor(Color.BLACK);
		GraphicsUtil.setAlpha(g, 0.95f);
		g.fillRect(0,0,AppManager.getW(),AppManager.getH());
		g.setColor(Color.WHITE);
		GraphicsUtil.setDefaultAlpha(g);
		g.drawString("説明書", 50, 150);
		g.drawString("約30秒間で的を破壊して高得点を目指すゲームです。", 50, 200);
		g.drawString("的をマウスの左ボタンでクリックすると破壊できます。", 50, 240);
		g.drawString("的は中央に近づくほど高得点で、0～100点となります。", 50, 280);
		g.drawString("コンボ数は連続で的を破壊できると増えていきます。", 50, 320);
		g.drawString("コンボ数は破壊した的の得点×コンボ数となります。", 50, 360);
		g.drawString("ランクはG～Sまでの8段階あります。", 50, 400);
		g.drawString("フィールド上にある壁をクリックしても得点は上がりません。", 50, 440);
	}

	//マウスクリック時の処理
	public void mouseClicked(MouseEvent e) {
		Point p = e.getPoint();
		if ( !isDescription && start.contains(p)) {
			//ゲーム画面へ遷移
			setCursor(Cursor.getDefaultCursor());
			AppManager.selectSE();
			AppManager.change(ViewType.GAME);
		} else if ( !isDescription && ranking.contains(p) ) {
			//ランキング画面へ遷移
			setCursor(Cursor.getDefaultCursor());
			AppManager.selectSE();
			AppManager.change(ViewType.RANKING);
		} else if ( !isDescription && description.contains(p) ) {
			//ゲーム説明を表示
			setCursor(Cursor.getDefaultCursor());
			AppManager.selectSE();
			isDescription = true;
		} else if ( !isDescription && end.contains(p) ) {
			//ゲーム終了
			AppManager.end();
		} else if ( isDescription ) {
			//説明表示を終了
			isDescription = false;
		}
	}

	//マウスカーソルの位置を記憶
	public void mouseMoved(MouseEvent e) {
		p = e.getPoint();
		//選択肢にマウスカーソルを持ってきた場合はカーソルを変更する
		if ( start.contains(p) || end.contains(p) || ranking.contains(p) || description.contains(p) ) {
			setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		} else {
			setCursor(Cursor.getDefaultCursor());
		}
	}
}