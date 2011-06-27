/*
 * Copyright (c) 2009 tarchan. All rights reserved.
 */
package com.mac.tarchan.nanika.ghost;

/**
 * NanikaGhost
 * 
 * @since SAKURA Script/2.0
 * @author tarchan
 * @see <a href="http://usada.sakura.vg/contents/specification2.html#script">SAKURA Script/2.0</a>
 */
public interface NanikaGhost
{
	/** sakura 側ゴーストのスコープ */
	public static final int SAKURA_SCOPE = 0;

	/** kero 側ゴーストのスコープ */
	public static final int KERO_SCOPE = 1;

	/**
	 * \0 でスコープを sakura 側に移動。\1 でスコープを kero 側に移動。
	 * 
	 * @param scope
	 */
	public void setScope(int scope);

	/**
	 * 現スコープのキャラクタが離れる方向に一定距離移動。
	 * 主に重なり後の強制排除に使用。
	 */
	public void moveAway();

	/**
	 * 現スコープのキャラクタが接触する距離まで移動。
	 */
	public void moveNear();

	/**
	 * \_a で囲った部分がキャラクタ型ジャンパ、いわゆる「リンク」となる。
	 * キャラクタ型ジャンパはクリックによって OnAnchorSelect イベントを raise する。
	 * Reference0 に symbol で指定した識別子。例示
	 * <pre>
	 * \_a[hoge]ほげ\_a
	 * 
	 * ↓
	 * 
	 * GET Sentence SHIORI/2.2
	 * Event: OnAnchorSelect
	 * Reference0: hoge
	 * </pre>
	 * 
	 * @param symbol
	 */
	public void selectAnchor(String symbol);

	/**
	 * 現スコープのバルーンのサーフィスを ID n に切り替え
	 * 
	 * @param n サーフェス ID
	 */
	public void setBalloon(String n);

	/**
	 * s で指定されたファイルをバルーン内座標 x,y に張り込み
	 * x が数値でなく文字列 centerx だった場合 x センタリング処理
	 * y が数値でなく文字列 centery だった場合 y センタリング処理
	 * s の基準ディレクトリは home\ghost\name\ghost\master
	 * 
	 * @param s ファイル名
	 * @param x X座標
	 * @param y Y座標
	 */
	public void setBalloon(String s, int x, int y);

	/**
	 * 現スコープのバルーンをクリア。カーソルはホームポジションへ
	 */
	public void clearBalloon();

	/**
	 * えんいー
	 * 終端記号
	 */
	public void end();

	/**
	 * アニメーショングループ n を起動させる。アニメについては SERIKO の項を参照。
	 * 
	 * @param n
	 */
	public void interval(int n);

	/**
	 * s が http:// で始まる文字列だった場合、設定されたブラウザを用いて s で指定された URL を開く
	 * s が file:// で始まる文字列だった場合、OS の拡張子関連付けを用いて s で指定されたファイルを開く
	 * 
	 * @param s URL
	 */
	public void jump(String s);

	/**
	 * カーソル位置を絶対座標 x,y に移動
	 * @param x
	 * @param y
	 */
	public void locate(int x, int y);

	/**
	 * SSTP が受けた直前の HWnd に対し postmessage(hwnd,wm,w,l) を発行
	 * 
	 * @param wm
	 * @param w
	 * @param l
	 */
	public void postMessage(int wm, int w,int l);

	/**
	 * 改行（キャリッジリターン＋ラインフィード）
	 */
	public void crlf();

	/**
	 * [half] がつくと半分の高さ改行
	 */
	public void half();

	/**
	 * \_n で囲まれたエリアは自動改行が行われない
	 */
	public void nocrlf();

	/**
	 * \_q で囲まれたエリアはノーウエイトで表示される。クイックセクション
	 */
	public void quick();

	/**
	 * 現スコープのサーフィスを ID n に切り替え
	 * 
	 * @param n
	 */
	public void setSurface(String n);

	/**
	 * \_s で囲まれたエリアは両方のキャラクタが同じセリフを喋る。シンクロナイズセクション
	 */
	public void synchronize();

	/**
	 * s で指定された音声ファイルを再生。再生できるフォーマットは、wav、mp3、wma
	 * 
	 * @param s 音声ファイル
	 */
	public void voice(String s);

	/**
	 * \_v で再生されたファイルの再生完了を待つ
	 */
	public void waitVoice();

	/**
	 * ?*50ms ウエイト。簡易書式。
	 * 
	 * @param n n * 50msのウエイト
	 */
	public void waitTime(int n);

	/**
	 * n ms ウエイト。高精度。
	 * 
	 * @param n n ms ウエイト
	 */
	public void waitTimeMillis(long n);

	/**
	 * 一時停止状態に入り、ページめくり（バルーンクリック）待機
	 */
	public void halt();

	/**
	 * SSTP マーカーを流用した段落点
	 * オフセットコントロールあり。文字扱い
	 */
	public void li();

	/**
	 * MATERIA システムをシャットダウン
	 */
	public void shutdown();

	/**
	 * 現スコープのデスクトップ上での位置アラインメントを識別子 s に従って設定する。
	 * 
	 * @param s 位置アラインメント識別子
	 */
	public void setDesktopAlignment(String s);

	/**
	 * 選択肢のタイムアウトを n ミリ秒に設定する。
	 * -1 の場合タイムアウトしない。永続。デフォルト値は 16000 ミリ秒
	 * 
	 * @param n タイムアウト時間
	 */
	public void setChoiceTimeout(int n);

	/**
	 * ゴースト切り替え。
	 * 
	 * <ul>
	 * <li>name が名指しだとそのゴーストへ。
	 * <li>name が random の場合ランダムチェンジ。
	 * <li>sequential の場合シーケンシャルチェンジ。
	 * <li>名指しした相手がいなかった場合はスルー。
	 * </ul>
	 * 
	 * 例示
	 * <pre>
	 * \![change,ghost,さくら]
	 * </pre>
	 * 
	 * @param name
	 */
	public void changeGhost(String name);

	/**
	 * システムファンクションをコール。
	 * 何が呼ばれるかは概ね見た通り
	 * 
	 * <ul>
	 * <li>\![updatebymyself]
	 * <li>\![executesntp]
	 * <li>\![biff]
	 * <li>\![open,configurationdialog]
	 * <li>\![open,ghostexplorer]
	 * <li>\![open,shellexplorer]
	 * <li>\![open,balloonexplorer]
	 * <li>\![open,headlinesensorexplorer]
	 * <li>\![open,rateofusegraph]
	 * <li>\![open,rateofusegraphballoon]
	 * <li>\![open,rateofusegraphtotal]
	 * </ul>
	 * 
	 * @param args 引数
	 */
	public void system(String[] args);

	/**
	 * 自身を Vanish
	 */
	public void vanish();

	/**
	 * 指定されたメッセージをしゃべります。
	 * 
	 * @param message メッセージ
	 */
	public void talk(String message);

	/**
	 * title で示されるタイトルを持った選択肢を表示。
	 * 選択後 SHIORI に対して OnChoiceSelect イベントが発生し、id で指定された識別子がパラメータとして渡される。
	 * 択一数は最大255択。
	 * 
	 * @param title 選択肢のタイトル
	 * @param id 選択肢のID
	 */
	public void question(String title, String id);
}
