package com.nompor.app.manager;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Properties;
import java.util.stream.Collectors;

import com.nompor.gtk.file.GTKFileUtil;

//ゲームのファイルの入出力メソッド群
//GTKFileUtilを使用し、ファイルの入出力を行う
//書き出し処理は自動でフォルダが作成されるため自力でフォルダ作成する必要はない
public class GameFileManager {
	private static Path path = Paths.get("data/data.dat");
	private static Path confPath = Paths.get("data/conf.dat");
	private static Properties prop = new Properties();
	private static ArrayList<DataInfo> dataList = new ArrayList<>();
	static {
		//ファイルロード
		if ( Files.exists(confPath) ) {
			prop = GTKFileUtil.loadProperty(confPath.toFile());
		}

		//ランキングファイルロード
		if ( Files.exists(path) ) {
			String[][] data = GTKFileUtil.loadCryptCSV(path.toFile(), "aaaaadaaaacaabaa");
			for ( int i = 0;i < 10;i++ ) {
				String[] d = data == null || data.length <= i ? null : data[i];
				DataInfo info = new DataInfo();
				if ( d != null ) {
					info.no = (i+1)+"";
					info.name = d[0];
					info.score = d[1];
					info.rank = d[2];
					info.datetime = d[3];
					dataList.add(info);
				} else {
					String fill = "";
					info.no = (i+1)+"";
					info.name = fill;
					info.score = fill;
					info.rank = fill;
					info.datetime = fill;
					dataList.add(info);
				}
			}
		} else {
			for ( int i = 0;i < 10;i++ ) {
				DataInfo info = new DataInfo();
				String fill = "";
				info.no = (i+1)+"";
				info.name = fill;
				info.score = fill;
				info.rank = fill;
				info.datetime = fill;
				dataList.add(info);
			}
		}
	}
	//ランキングデータの取得
	public static ArrayList<DataInfo> get(){
		return dataList;
	}
	public static void saveInfo(DataInfo info){
		//追加後にセーブする
		dataList.add(info);

		//スコアの降順にソート
		Collections.sort(dataList, (e1,e2)->e2.getScore()-e1.getScore());
		for ( int i = 0;i < dataList.size();i++ ) {
			dataList.get(i).no = i+1+"";
		}

		//全ランキングデータを二次元配列化
		String[][] result = dataList.stream().map(e ->{
			String[] s = new String[4];
			s[0] = e.name;
			s[1] = e.score;
			s[2] = e.rank;
			s[3] = e.datetime;
			return s;
		}).collect(Collectors.toList()).toArray(new String[0][]);

		//暗号化CSVファイルの書き出し
		GTKFileUtil.saveCryptCSV(path.toFile(), result, "aaaaadaaaacaabaa");
		while ( dataList.size() > 10 ) {
			dataList.remove(dataList.size()-1);
		}
	}
	//ランキングに追加できるかどうかを判定します。
	public static boolean addok(int score){
		if ( dataList.size() <= 0 ) {
			return true;
		}
		return dataList.get(dataList.size()-1).getScore() < score;
	}
	//名前を取得
	public static String getLastName() {
		return prop.getProperty("name");
	}
	//名前を保存
	public static void saveName(String name) {
		prop.setProperty("name", name);
		GTKFileUtil.saveProperty(confPath.toFile(), prop);
	}

	//ランキングの1行分のデータを保持できるクラス
	public static class DataInfo{
		public String no;
		public String score;
		public String name;
		public String datetime;
		public String rank;
		public int getScore() {
			try {
				return Integer.parseInt(score);
			} catch (Exception e) {
				return 0;
			}
		}
		public void setScore(int score) {
			this.score = ""+score;
		}
	}
}
