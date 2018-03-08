package com.nompor.app.object;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;

import com.nompor.app.manager.AppManager;
import com.nompor.gtk.animation.FadeAnimation;
import com.nompor.gtk.animation.ImageDrawAnimation;
import com.nompor.gtk.animation.ParallelAnimation;
import com.nompor.gtk.animation.RotateAnimation;
import com.nompor.gtk.animation.ScaleAnimation;
import com.nompor.gtk.draw.GraphicsUtil;
import com.nompor.gtk.geom.Circle;

//的を表すクラス
public class Target{
	private Circle circle;
	private ImageDrawAnimation img;
	private int mx;
	private int my;
	private boolean isAlive=true;
	private int lastScorePoint=-1;
	private int sy;
	private int deleteFrame;
	private static Font font = new Font(Font.MONOSPACED, Font.BOLD, 20);

	//並列アニメーションオブジェクト
	//拡大、フェードアウト、回転を同時実行する
	private ParallelAnimation animation;
	public Target(int x, int y, int mx, int my) {
		//画像アニメーション
		this.img = new ImageDrawAnimation(AppManager.getTargetImage(),100,100,1);
		animation = new ParallelAnimation(
				new ScaleAnimation(img, 0.1)
				, new FadeAnimation(img, -0.12f)
				, new RotateAnimation(img, 10)
		);

		//最後の画像を表示したら終了
		img.setAnimationEndOfLastImageIndex();

		//円オブジェクト（引数x,yは画像の左上座標を想定しているため+50が的の中央
		circle = new Circle(x+50, y+50, 25);

		//移動量
		this.mx = mx;
		this.my = my;
	}

	//的が破壊されていないか
	public boolean isAlive() {
		return isAlive;
	}

	//的を削除するか
	public boolean isDelete() {
		return !isAlive && deleteFrame > 60;
	}

	//破壊処理の要求メソッド
	public void breakProcess() {
		isAlive = false;
	}

	//描画
	public void draw(Graphics g) {


		//破壊されたら破壊アニメーション
		if ( !isAlive ) {
			//アフィン変換アニメーション
			animation.update();

			//画像差し替えアニメーション
			img.update();

			deleteFrame++;
		} else {
			circle.cx+=mx;
			circle.cy+=my;
		}
		//アニメーション結果を適用した描画を実行
		img.draw(g, circle.getICX()-50, circle.getICY()-50);

		if ( lastScorePoint != -1 ) {
			//破壊後の得点の描画
			sy--;
			g.setColor(Color.WHITE);
			g.setFont(font);
			GraphicsUtil.drawCenteringString(g, ""+lastScorePoint, circle.getICX(), circle.getICY()+sy);
		}
	}

	//的との当たり判定用メソッド
	public boolean isHit(Point p, Circle c) {
		return c.contains(p.x, p.y);
	}
	//的との当たり判定用メソッド
	public boolean isHit(Point p) {
		return isHit(p, circle);
	}
	//的と中心点との計算を実行しスコアポイントを計算
	public int getScorePoint(Point p) {
		//中心点への距離が近いほど高得点を返すように処理する
		double r = circle.getRadius();
		r *= r;

		double vx = p.x - circle.cx;
		double vy = p.y - circle.cy;

		double r2 = vx*vx+vy*vy;
		int result = (int)Math.round((1.0 - r2 / r) * 100);

		lastScorePoint = (result < 0 ? 0 : result);
		return lastScorePoint;
	}
	//的の左端座標を返す
	public int getLeft() {
		return circle.getILeft();
	}
	//的の上端座標を返す
	public int getTop() {
		return circle.getITop();
	}
	//的の右端座標を返す
	public int getRight() {
		return circle.getIRight();
	}
	//的の下端座標を返す
	public int getBottom() {
		return circle.getIBottom();
	}
}
