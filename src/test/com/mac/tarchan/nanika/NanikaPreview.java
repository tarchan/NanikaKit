/*
 *  Copyright (c) 2009 tarchan. All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *
 *  1. Redistributions of source code must retain the above copyright notice,
 *     this list of conditions and the following disclaimer.
 *  2. Redistributions in binary form must reproduce the above copyright notice,
 *     this list of conditions and the following disclaimer in the documentation
 *     and/or other materials provided with the distribution.
 *
 *  THIS SOFTWARE IS PROVIDED BY TARCHAN ``AS IS'' AND ANY EXPRESS OR
 *  IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 *  MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO
 *  EVENT SHALL TARCHAN OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 *  INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 *  ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 *  THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 *  The views and conclusions contained in the software and documentation are
 *  those of the authors and should not be interpreted as representing official
 *  policies, either expressed or implied, of tarchan.
 */
package test.com.mac.tarchan.nanika;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.GridLayout;
import java.awt.event.ComponentEvent;
import java.io.IOException;

import javax.swing.Box;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mac.tarchan.desktop.DesktopSupport;
import com.mac.tarchan.desktop.SexyControl;
import com.mac.tarchan.desktop.event.EventQuery;
import com.mac.tarchan.nanika.shell.NanikaScope;
import com.mac.tarchan.nanika.shell.NanikaShell;
import com.mac.tarchan.nanika.util.NarFile;
import com.mac.tarchan.nanika.util.NarFile.Type;

/**
 * NanikaPreview
 *
 * @author tarchan
 */
public class NanikaPreview extends Canvas
{
	/** シリアルバージョンID */
	private static final long serialVersionUID = 5016313064569130843L;

	/** ログ */
	private static final Log log = LogFactory.getLog(NanikaPreview.class);

	/** ウインドウ */
	JFrame window;

	/** シェル */
	NanikaShell shell;

	/**
	 * @param args ゴースト名
	 */
	public static void main(String[] args)
	{
		SexyControl.setAppleMenuAboutName("NanikaPreview");
		SexyControl.useScreenMenuBar();
		DesktopSupport.useSystemLookAndFeel();
//		PropertyConfigurator.configure("log4j.properties");
		try
		{
			String zip = "/Users/tarchan/Documents/nanika/nar/akane_v135.nar";
//			String zip = "/Users/tarchan/Documents/nanika/nar/333-2.60.nar";
//			String zip = "/Users/tarchan/Documents/nanika/nar/mayura_v340.zip";
//			String zip = "/Users/tarchan/Documents/nanika/nar/cmd.nar";
//			if (args.length > 0) zip = args[0];
			NanikaPreview app = new NanikaPreview();
//			app.createWindow().setVisible(true);
			DesktopSupport.show(app.createWindow());
			app.init(zip);
		}
		catch (Exception x)
		{
			x.printStackTrace();
		}
	}

	/**
	 * ウインドウを作成します。
	 *
	 * @return ウインドウ
	 */
	JFrame createWindow()
	{
		JFrame window = new JFrame("NanikaPreview");
		window.setName("window");
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setSize(900, 500);
//		window.add(this, BorderLayout.CENTER);
//		window.add(createFooterComponent(), BorderLayout.SOUTH);
		window.setJMenuBar(createJMenuBar());

		EventQuery.from(window).find("preferencesTable").change(this, "selectRow", "").end()
			.find("menubar").button().click(this).end()
			.find("footer").button().click(this).end()
			.find("window").resize(this, "resizeView", "");

//		SexyControl.setWindowShadow(window, false);
//		SexyControl.setWindowAlpha(window, 0.5);
//		window.setBackground(new Color(0, true));
//		window.setUndecorated(true);
//		window.setAlwaysOnTop(true);

		this.window = window;
		return window;
	}

	/**
	 * メニューバーを作成します。
	 *
	 * @return メニューバー
	 */
	JMenuBar createJMenuBar()
	{
		JMenuItem openNar = new JMenuItem("Open Nar...");
		openNar.setName("openNar");

		JMenu fileMenu = new JMenu("File");
		fileMenu.add(openNar);

		JMenuBar menubar = new JMenuBar();
		menubar.setName("menubar");
		menubar.add(fileMenu);

		return menubar;
	}

	/**
	 * NAR ファイルを開きます。
	 */
	public void openNar()
	{
		JFileChooser fd = new JFileChooser();
		fd.showOpenDialog(window);
	}

	/**
	 * サーフェス一覧の表示領域を変更します。
	 *
	 * @param evt イベント
	 */
	public void resizeView(ComponentEvent evt)
	{
		log.debug("evt=" + evt);
	}

	/**
	 * 初期化します。
	 *
	 * @param zip NAR ファイル名
	 * @throws IOException 初期化できない場合
	 */
	void init(String zip) throws IOException
	{
		NarFile nar = NarFile.load(zip);
		log.info("nar=" + nar);
		nar.list();
		if (nar.getType() == Type.ghost)
		{
			NarFile shell_master = nar.subdir("shell/master");
			shell = new NanikaShell(shell_master);
//			NanikaSurface surface = shell.getSurface(NanikaShell.SAKURA_ID);
//			log.info("shell=" + shell);
//			log.info("surface=" + surface);
//			JPanel main = new JPanel();
//			main.add(surface);
//			window.add(main);
			int rows = (shell.getSurfaceCount() + 4) / 5;
			log.debug("rows=" + rows + ", " + shell.getSurfaceKeys());
			JPanel main = new JPanel(new GridLayout(rows, 5));
//			JPanel main = new JPanel(new FlowLayout());
			for (String id : shell.getSurfaceKeys())
			{
				NanikaScope scope = new NanikaScope();
				scope.setShell(shell);
				scope.setSurface(id);
				JLabel label = new JLabel(id, JLabel.CENTER);
				JPanel koma = new JPanel(new BorderLayout());
				koma.add(scope, BorderLayout.CENTER);
				koma.add(label, BorderLayout.SOUTH);
				main.add(koma);
			}
			Box box = Box.createHorizontalBox();
			box.add(main);
			box.add(Box.createHorizontalGlue());
			box.setOpaque(false);
			JScrollPane scroll = new JScrollPane(main);
			scroll.setName("scroll");
//			main.setPreferredSize(new Dimension(800, 0));
			scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
			scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
			scroll.getVerticalScrollBar().setUnitIncrement(8);
			scroll.getVerticalScrollBar().setBlockIncrement(320);
//			scroll.setBackground(new Color(0, true));
			scroll.setOpaque(false);
//			scroll.getViewport().setScrollMode(JViewport.BLIT_SCROLL_MODE);
			window.add(scroll);
//			window.add(main);
			window.validate();
//			box.setBackground(new Color(0, true));
//			window.setBackground(new Color(0, true));
//			window.setUndecorated(true);
//			window.setAlwaysOnTop(true);

//			NarFile ghost = nar.subdir("ghost/master");
//			log.info("ghost=" + ghost);
		}
		else
		{
		}
	}

//	/**
//	 * {@inheritDoc}
//	 */
//	public void paint2(Graphics g)
//	{
////		log.debug("g=" + g);
//
//		if (shell == null) return;
//
//		Rectangle clip = g.getClipBounds();
//
//		Graphics2D g2 = (Graphics2D)g;
//		NanikaSurface sakura = shell.getSurface(NanikaShell.SAKURA_ID);
//		sakura.x = clip.width - sakura.getWidth();
//		sakura.y = clip.height - sakura.getHeight();
//		sakura.draw(g2);
//		NanikaSurface kero = shell.getSurface(NanikaShell.KERO_ID);
//		kero.y = clip.height - kero.getHeight();
//		kero.draw(g2);
//	}
}
