package com.nompor.app.view;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import com.nompor.app.manager.AppManager;
import com.nompor.app.manager.GameFileManager;
import com.nompor.app.manager.GameFileManager.DataInfo;
import com.nompor.app.object.Block;
import com.nompor.app.object.Target;
import com.nompor.gtk.GTKManager;
import com.nompor.gtk.GameView;
import com.nompor.gtk.animation.AnimationObject;
import com.nompor.gtk.animation.FadeAnimation;
import com.nompor.gtk.animation.ParallelAnimation;
import com.nompor.gtk.animation.ScaleAnimation;
import com.nompor.gtk.animation.SequenceAnimation;
import com.nompor.gtk.draw.DrawLabel;
import com.nompor.gtk.draw.FillRect;
import com.nompor.gtk.draw.GraphicsUtil;
import com.nompor.gtk.file.GTKFileWriteException;

public class GameMode extends GameView {
	//全的のセッティングリスト
	ArrayList<Target> initTargetList = new ArrayList<>();

	//現在画面に表示されている的リスト
	ArrayList<Target> viewTargetList = new ArrayList<>();

	//ブロックリスト
	ArrayList<Block> blockList = new ArrayList<>();

	//開始前カウント
	int prepareCountDown=3;

	//ゲームカウント
	int gameCountDown=30;

	//終了後の遅延カウント
	int endDelayCount = 2;

	//開始カウントダウン用フォント
	Font countDownFont = new Font(Font.MONOSPACED, Font.BOLD, 120);

	//成績を記録
	int combo = 0;
	int maxCombo=0;
	int score = 0;
	int breakNum = 0;

	//ゲームの状態フラグ
	boolean isGameResult;
	boolean isGameEnd;
	boolean isGameStart;



	//STARTアニメーションラベル
	DrawLabel startLabel;
	ParallelAnimation startAnime;

	//リザルトアニメーション
	FillRect fillRect = new FillRect(Color.BLACK, 0, 50, 800, 550);
	AnimationObject resultStringAnimationObject = new AnimationObject();
	SequenceAnimation resultAnimation;

	//上部のステータス用フォント
	Font statusFont = new Font(Font.MONOSPACED, Font.BOLD, 30);

	//結果画面で名前を入力しているかどうか
	boolean isResultNameInputWait;

	//タイマー
	Timer timer = new Timer();
	TimerTask task = new TimerTask() {

		@Override
		public void run() {
			if ( prepareCountDown > 0 ) {
				//ゲーム開始前
				prepareCountDown--;
				if ( prepareCountDown == 0 ) {
					AppManager.startSE();
				} else {
					AppManager.countSE();
				}
			} else if ( gameCountDown > 0 ) {

				//ゲーム中は一秒に1,2個の的を出現させる
				isGameStart = true;
				gameCountDown--;
				if ( initTargetList.size() > 0 ) {
					viewTargetList.add(initTargetList.remove(initTargetList.size() - 1));
				}
				if ( initTargetList.size() > 0 && gameCountDown % 2 == 0 ) {
					viewTargetList.add(initTargetList.remove(initTargetList.size() - 1));
				}
			} else if ( endDelayCount > 0 && initTargetList.size() <= 0 && viewTargetList.size() <= 0 ) {
				//全てのオブジェクトがなくなってから2秒間のインターバルを開ける
				endDelayCount--;
			} else if ( endDelayCount <= 0 ) {
				//2秒インターバルが0になったらリザルトアニメーションの開始
				isGameResult = true;

				//タイマーの終了
				timer.cancel();
			}
		}
	};

	//windowにパネルが配置された後に呼び出される
	public void start() {
		//windowにパネルが配置された後ならGraphicsオブジェクトを取得できる
		startLabel = new DrawLabel(getGraphics(), "START", countDownFont, AppManager.getW()/2, AppManager.getH()/2);
		startAnime = new ParallelAnimation(
				new ScaleAnimation(startLabel, 0.02)
				, new FadeAnimation(startLabel, -0.03f)
		);
	}

	//初期化コンストラクタ
	public GameMode() {
		Random rand = new Random();
		//ランダムで30このお邪魔ブロックを配置する
		for ( int i = 0,limit=0;i < 30 && limit < 100; limit++) {
			Block block = new Block(rand.nextInt(750), rand.nextInt(500)+50);
			if ( !blockList.stream().anyMatch(b -> b.isConflictBlock(block)) ) {
				blockList.add(block);
				i++;
			}
		}

		//上下にランダムで的をセッティング
		for ( int i = 0;i < 20;i++ ) {
			boolean r = rand.nextBoolean();
			int y = -50;
			int my = 2;
			if ( r ) {
				y = 600;
				my = -2;
			}
			Target t = new Target(rand.nextInt(700), y, 0, my);
			initTargetList.add(t);
		}
		//左右にランダムで的をセッティング
		for ( int i = 0;i < 20;i++ ) {
			boolean r = rand.nextBoolean();
			int x = -50;
			int mx = 2;
			if ( r ) {
				x = 800;
				mx = -2;
			}
			Target t = new Target(x, rand.nextInt(450) + 50, mx, 0);
			initTargetList.add(t);
		}

		//的の順番をシャッフル
		Collections.shuffle(initTargetList);

		//タイマーの起動
		timer.scheduleAtFixedRate(task, 1000, 1000);

		//結果のアニメーションオブジェクト群。完全透明で初期化
		fillRect.setOpacity(0f);
		resultStringAnimationObject.setOpacity(0f);
		FadeAnimation fadeAnimation = new FadeAnimation(fillRect, 0.02f);
		FadeAnimation fadeAnimation2 = new FadeAnimation(resultStringAnimationObject, 0.05f);
		fadeAnimation.setEndFramePoint(25);
		fadeAnimation2.setEndFramePoint(25);
		resultAnimation = new SequenceAnimation(fadeAnimation, fadeAnimation2);
	}

	@Override
	public void draw(Graphics g) {
		int width = AppManager.getW();
		int height = AppManager.getH();

		//アンチエイリアスの有効化
		GraphicsUtil.setTextAntialiasing(g, true);

		//背景の描画
		g.drawImage(AppManager.getBackImage(), 0, 0, null);

		if ( isGameResult ) {
			//ゲーム結果の描画処理

			//ブロックの描画
			for ( Block b : blockList ) {
				b.draw(g);
			}
			fillRect.draw(g);
			resultAnimation.update();

			GraphicsUtil.setAlpha(g, resultStringAnimationObject.getOpacity());

			g.setColor(Color.WHITE);
			g.setFont(statusFont);
			int result = (score + (breakNum * 100) + (maxCombo * 150));
			g.drawString("得点 : "+score, 150, 200);
			g.drawString("破壊数 : "+breakNum+" × 100 = "+(breakNum * 100), 150, 270);
			g.drawString("最大コンボ数 : "+maxCombo+" × 150 = "+(maxCombo * 150), 150, 340);
			g.drawString("合計 : "+(result)+" 点", 150, 410);
			String rank = "G";
			Color c = Color.LIGHT_GRAY;
			if ( result >= 80000 ) {
				rank = "S";
				c = Color.YELLOW;
			} else if ( result >= 70000 ) {
				rank = "A";
				c = Color.MAGENTA;
			} else if ( result >= 60000 ) {
				rank = "B";
				c = Color.RED;
			} else if ( result >= 50000 ) {
				rank = "C";
				c = Color.ORANGE;
			} else if ( result >= 40000 ) {
				rank = "D";
				c = Color.LIGHT_GRAY;
			} else if ( result >= 20000 ) {
				rank = "E";
				c = Color.LIGHT_GRAY;
			} else if ( result >= 10000 ) {
				rank = "F";
				c = Color.LIGHT_GRAY;
			}
			g.drawString("あなたの評価は ", 150, 480);
			g.setColor(c);
			g.setFont(new Font(Font.MONOSPACED, Font.BOLD, 60));
			g.drawString(rank, 400, 480);
			g.setColor(Color.WHITE);
			g.setFont(statusFont);
			g.drawString(" ランクです。", 445, 480);
			GraphicsUtil.setDefaultAlpha(g);
			if ( resultAnimation.isEnd() ) {

				//ランキングに追加すべきなら名前を入れてもらう
				if ( !isResultNameInputWait && GameFileManager.addok(result) ) {
					isResultNameInputWait = true;

					//ゲームループを起動状態だとダイアログの描画に影響がある為ゲームループを一時停止
					GTKManager.stopGameLoop();

					//UIスレッドで動作させる
					SwingUtilities.invokeLater(new Runnable() {

						Component comp;
						String rank;
						int result;
						public Runnable init(Component comp, String rank, int result) {
							this.comp = comp;
							this.rank = rank;
							this.result = result;
							return this;
						}

						@Override
						public void run() {

							try {

								//正確な値入力がされるまで入力要求を続ける
								String name = null;
								while(true){
									name = JOptionPane.showInputDialog(comp,"ランキング登録する名前を10文字以内で入力してください。",GameFileManager.getLastName());
									if ( name == null ) {
										JOptionPane.showMessageDialog(comp,"名前が入力されていません。");
										continue;
									}
									if ( name.length() == 0 ) {
										JOptionPane.showMessageDialog(comp,"名前が入力されていません。");
										continue;
									}
									if ( name.length() > 10 ) {
										JOptionPane.showMessageDialog(comp,"名前は10文字以内で入力してください。");
										continue;
									}
									break;
								}
								DataInfo info = new DataInfo();
								info.setScore(result);
								info.name = name;
								info.rank = rank;
								info.datetime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss"));

								//入力名を保存
								GameFileManager.saveName(name);

								//ランキングに追加
								GameFileManager.saveInfo(info);
							} catch ( GTKFileWriteException e ) {
								e.printStackTrace();
								JOptionPane.showMessageDialog(comp,"ファイルの書き出しに失敗しました。");
							} finally {
								//ゲームループの再開
								isGameEnd = true;
								GTKManager.startGameLoop();
							}
						}

					}.init(this, rank, result));
				} else {
					isGameEnd = true;
				}
			}
		} else if ( isGameStart ) {

			//ゲーム中の描画処理
			for ( int i = viewTargetList.size() - 1;i >= 0;i-- ) {
				Target t = viewTargetList.get(i);

				//Targetオブジェクトの削除フラグがtrueか画面外に出たら削除
				if ( t.isDelete()
					|| t.getRight() < -50
					|| t.getBottom() < -50
					|| t.getLeft() > width+50
					|| t.getTop() > height+50) {

					viewTargetList.remove(i);
				} else {
					//Targetの移動処理と描画
					t.draw(g);
				}
			}

			//ブロックの描画
			for ( Block b : blockList ) {
				b.draw(g);
			}
		} else {
			//ゲーム開始前の描画処理
			for ( Block b : blockList ) {
				b.draw(g);
			}
			g.setColor(Color.WHITE);

			if ( prepareCountDown == 0 ) {
				//STARTアニメーション
				startAnime.update();
				startLabel.draw(g);
			} else {
				//カウントダウンの描画
				g.setFont(countDownFont);
				GraphicsUtil.drawCenteringString(g, ""+prepareCountDown, width / 2, height / 2);
			}
		}

		//ステータスの描画
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, width, 50);

		g.setColor(Color.WHITE);
		g.setFont(statusFont);
		GraphicsUtil.drawCenteringString(g, "破壊数:"+breakNum, 400, 25);
		GraphicsUtil.drawCenteringString(g, "得点:"+score, 200, 25);
		GraphicsUtil.drawCenteringString(g, "コンボ:"+combo, 600, 25);
	}

	//マウスボタンを押し下げたときの処理
	//反応速度が要求されるためmousePressedで処理する
	public void mousePressed(MouseEvent e) {
		if ( isGameResult ) return;

		//当たり判定
		int hit = 0;
		switch ( e.getButton() ) {
			case MouseEvent.BUTTON1:
				//ブロックに当たっていたら的の当たり判定はしない
				if ( !blockList.stream().anyMatch(b -> b.isHit(e.getPoint())) ) {
					for ( int i = viewTargetList.size() - 1;i >= 0;i-- ) {
						Target t = viewTargetList.get(i);

						//ターゲットが存在していてマウス座標があっていたら破壊アニメーションを実行
						if ( t.isAlive() && t.isHit(e.getPoint()) ) {
							//マウス座標を引き渡し、得点を取得
							int point = t.getScorePoint(e.getPoint());
							combo++;
							score+=point*combo;
							breakNum++;
							maxCombo = Math.max(combo, maxCombo);

							//Targetへ破壊を通知
							t.breakProcess();
							hit++;
						}
					}
				}
				if ( hit > 0 ) {
					AppManager.breakSE();
				} else {
					combo = 0;
				}
				break;
		}
	}

	//クリックされた時
	public void mouseClicked(MouseEvent e) {
		if ( isGameEnd ) {
			AppManager.change(ViewType.TITLE);
		}
	}
}
