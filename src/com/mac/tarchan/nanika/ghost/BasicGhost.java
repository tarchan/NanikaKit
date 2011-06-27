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
public class BasicGhost implements NanikaGhost
{
	/**
	 * \0 でスコープを sakura 側に移動。\1 でスコープを kero 側に移動。
	 * 
	 * @param scope スコープ
	 */
	public void setScope(int scope)
	{
		// TODO \0 \1
	}

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
	 * @param symbol アンカー
	 */
	public void selectAnchor(String symbol)
	{
		// TODO \_a[symbol]
	}

	/**
	 * 現スコープのバルーンのサーフィスを ID n に切り替え
	 * 
	 * @param n サーフェス ID
	 */
	public void setBalloon(String n)
	{
		// TODO \b[n]
	}

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
	public void setBalloon(String s, int x, int y)
	{
		// TODO \_b[s,x,y]
	}

	/**
	 * 現スコープのバルーンをクリア。カーソルはホームポジションへ
	 */
	public void clearBalloon()
	{
		// TODO \c
	}

	/**
	 * えんいー
	 * 終端記号
	 */
	public void end()
	{
		// TODO \e
	}

	/**
	 * アニメーショングループ n を起動させる。アニメについては SERIKO の項を参照。
	 * 
	 * @param n アニメーショングループ ID
	 */
	public void interval(int n)
	{
		// TODO \i[n]
	}

	/**
	 * s が http:// で始まる文字列だった場合、設定されたブラウザを用いて s で指定された URL を開く
	 * s が file:// で始まる文字列だった場合、OS の拡張子関連付けを用いて s で指定されたファイルを開く
	 * 
	 * @param s URL
	 */
	public void jump(String s)
	{
		// TODO \j[s]
	}

	/**
	 * カーソル位置を絶対座標 x,y に移動
	 * 
	 * @param x X座標
	 * @param y Y座標
	 */
	public void locate(int x, int y)
	{
		// TODO \_l[x,y]
	}

	/**
	 * SSTP が受けた直前の HWnd に対し postmessage(hwnd,wm,w,l) を発行
	 * 
	 * @param wm wm
	 * @param w w
	 * @param l l
	 */
	public void postMessage(int wm, int w,int l)
	{
		// TODO \m[wm,w,l]
	}

	/**
	 * 改行（キャリッジリターン＋ラインフィード）
	 */
	public void crlf()
	{
		// TODO \n
	}

	/**
	 * [half] がつくと半分の高さ改行
	 */
	public void half()
	{
		// TODO \n[half]
	}

	/**
	 * \_n で囲まれたエリアは自動改行が行われない
	 */
	public void nocrlf()
	{
		// TODO \_n
	}

	/**
	 * \_q で囲まれたエリアはノーウエイトで表示される。クイックセクション
	 */
	public void quick()
	{
		// TODO \_q
	}

	/**
	 * 現スコープのサーフィスを ID n に切り替え
	 * 
	 * @param n サーフィス ID
	 */
	public void setSurface(String n)
	{
		// TODO \s[n]
	}

	/**
	 * \_s で囲まれたエリアは両方のキャラクタが同じセリフを喋る。シンクロナイズセクション
	 */
	public void synchronize()
	{
		// TODO \_s
	}

	/**
	 * \t
	 * タイムクリティカルセクション。
	 * タイムクリティカルセクション中は右クリック／ダブルクリックメニューや自動チェック系など状況を破壊するコマンドが来ない。
	 * \e が出るまで有効。
	 */
	public void timeCriticalSection()
	{
		
	}

	/**
	 * s で指定された音声ファイルを再生。再生できるフォーマットは、wav、mp3、wma
	 * 
	 * @param s 音声ファイル
	 */
	public void voice(String s)
	{
		// TODO \_v[s]
	}

	/**
	 * \_v で再生されたファイルの再生完了を待つ
	 */
	public void waitVoice()
	{
		// TODO \_V
	}

	/**
	 * ?*50ms ウエイト。簡易書式。
	 * 
	 * @param n n * 50msのウエイト
	 */
	public void waitTime(int n)
	{
		// TODO \w?
		waitTimeMillis(50l * n);
	}

	/**
	 * n ms ウエイト。高精度。
	 * 
	 * @param n n ms ウエイト
	 */
	public void waitTimeMillis(long n)
	{
		// TODO \_w[n]
	}

	/**
	 * 一時停止状態に入り、ページめくり（バルーンクリック）待機
	 */
	public void halt()
	{
		// TODO \x
	}

	/**
	 * SSTP マーカーを流用した段落点
	 * オフセットコントロールあり。文字扱い
	 */
	public void li()
	{
		// TODO \![*]
	}

	/**
	 * MATERIA システムをシャットダウン
	 */
	public void shutdown()
	{
		// TODO \-
	}

	/**
	 * 現スコープのデスクトップ上での位置アラインメントを識別子 s に従って設定する。
	 * 
	 * @param s 位置アラインメント識別子
	 */
	public void setDesktopAlignment(String s)
	{
		// TODO \![set,desktopalignment,s]
	}

	/**
	 * 選択肢のタイムアウトを n ミリ秒に設定する。
	 * -1 の場合タイムアウトしない。永続。デフォルト値は 16000 ミリ秒
	 * 
	 * @param n タイムアウト時間
	 */
	public void setChoiceTimeout(int n)
	{
		// TODO \![set,choicetimeout,n]
	}

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
	 * @param name ゴーストの名前
	 * @see #system(String[])
	 */
	public void changeGhost(String name)
	{
		// TODO \![change,ghost,name]
	}

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
	public void system(String[] args)
	{
		if ("vanishbymyself".equals(args[0]))
		{
			vanish();
		}
	}

	/**
	 * 自身を Vanish
	 */
	public void vanish()
	{
		// TODO \![vanishbymyself]
	}

	/**
	 * 指定されたメッセージをしゃべります。
	 * 
	 * @param message メッセージ
	 */
	public void talk(String message)
	{
		// TODO 自動生成されたメソッド・スタブ
	}

	/**
	 * {@inheritDoc}
	 */
	public void question(String title, String id)
	{
		// TODO 自動生成されたメソッド・スタブ
	}

	/**
	 * {@inheritDoc}
	 */
	public void moveAway()
	{
		// TODO 自動生成されたメソッド・スタブ
	}

	/**
	 * {@inheritDoc}
	 */
	public void moveNear()
	{
		// TODO 自動生成されたメソッド・スタブ
	}
}
