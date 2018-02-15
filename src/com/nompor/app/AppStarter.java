package com.nompor.app;

import com.nompor.app.manager.AppManager;
import com.nompor.app.view.ViewType;

//アプリケーションエントリポイント
public class AppStarter{
	public static void main(String[] args) {
		//初期化時はタイトルを表示する
		AppManager.start(ViewType.TITLE);
	}
}