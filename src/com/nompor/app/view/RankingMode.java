package com.nompor.app.view;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import com.nompor.app.manager.AppManager;
import com.nompor.app.manager.GameFileManager;
import com.nompor.app.manager.GameFileManager.DataInfo;
import com.nompor.gtk.GameView;
import com.nompor.gtk.draw.GraphicsUtil;

public class RankingMode extends GameView{

	Font f = new Font(Font.MONOSPACED, Font.BOLD, 20);

	//マウスクリック時の処理
	public void mouseClicked(MouseEvent e) {
		AppManager.change(ViewType.TITLE);
	}

	public void draw(Graphics g) {
		//ランキングを描画
		GraphicsUtil.setTextAntialiasing(g, true);
		g.setColor(Color.BLACK);
		g.fillRect(0,0,AppManager.getW(),AppManager.getH());
		g.setColor(Color.WHITE);
		g.setFont(f);
		ArrayList<DataInfo> dataList = GameFileManager.get();
		g.drawString("名前", 150, 30);
		g.drawString("スコア", 300, 30);
		g.drawString("ランク", 420, 30);
		g.drawString("日時", 600, 30);
		for ( int i = 0;i < dataList.size();i++ ) {
			DataInfo r = dataList.get(i);
			g.drawString(r.no, 30, 50*i+80);
			g.drawString(r.name, 70, 50*i+80);
			g.drawString(r.score, 305, 50*i+80);
			g.drawString(r.rank, 445, 50*i+80);
			g.drawString(r.datetime, 520, 50*i+80);
		}
	}
}
