package com.nompor.app.object;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;

import com.nompor.app.manager.AppManager;

//ブロックを表すクラス
public class Block{
	private Rectangle rect;
	private Image img;
	public Block(int x, int y) {
		this.img = AppManager.getBlockImage();

		//ブロックの当たり判定用の矩形
		rect = new Rectangle(x, y, 50, 50);
	}

	//ブロックの描画
	public void draw(Graphics g) {
		g.drawImage(img, rect.x, rect.y, null);
	}

	//ブロックとの判定メソッド
	public boolean isHit(Point p) {
		return rect.contains(p);
	}

	//ブロック同士が衝突状態かどうか
	public boolean isConflictBlock(Block b) {
		return rect.intersects(b.rect);
	}
}
