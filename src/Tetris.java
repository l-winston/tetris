import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.function.Function;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextArea;

public class Tetris extends JFrame {
	static final int BOARD_HEIGHT = 20;
	static final int BOARD_WIDTH = 10;
	static final int IMAGE_HEIGHT = 601;
	static final int IMAGE_WIDTH = 301;
	static final int TILE_WIDTH = IMAGE_WIDTH / BOARD_WIDTH;
	static final int TILE_HEIGHT = IMAGE_HEIGHT / BOARD_HEIGHT;

	static int delay = 1000;

	static int rows = 0;
	static JTextArea score = new JTextArea("score");

	static final Random rand = new Random();

	int[][] board = new int[BOARD_HEIGHT][BOARD_WIDTH];
	static Color[] colors = { Color.white, Color.cyan, Color.blue, Color.orange, Color.yellow, Color.green,
			new Color(138, 43, 226), Color.red };
	Shape controlling;
	BufferedImage image = new BufferedImage(IMAGE_WIDTH, IMAGE_HEIGHT, BufferedImage.TYPE_INT_RGB);
	Graphics2D g2d = image.createGraphics();

	BufferedImage preview = new BufferedImage(IMAGE_WIDTH, IMAGE_HEIGHT, BufferedImage.TYPE_INT_RGB);
	Graphics2D preg2d = preview.createGraphics();
	
	int count = 1;
	static Timer timer = new Timer();

	public static void main(String[] args) {
		Tetris t = new Tetris();
		t.initFrame();
		t.run();
	}

	private void run() {
		Tetris tet = this;
		controlling = new Shape(rand.nextInt(7) + 1, tet);
				
		timer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				if (controlling.settled) {
					checkRows();
					controlling = new Shape(rand.nextInt(7) + 1, tet);
				}

				paintBoard();
				if (count++ % 10 == 1) {
					controlling.update();
					count = 0;
				}
			}
		}, 0, delay/10);
	}

	public void checkRows() {
		loop: for (int i = board.length - 1; i >= 0; i--) {
			for (int j = 0; j < board[0].length; j++) {
				if (board[i][j] == 0)
					continue loop;
			}
			deleteRow(i);
			score.setText(rows++ + "");
			score.repaint();
			System.out.println(rows);
		}
	}

	private void deleteRow(int ind) {
		for (int i = ind; i > 0; i--) {
			for (int j = 0; j < board[0].length; j++) {
				board[i][j] = board[i - 1][j];
			}
		}
		board[0] = new int[board[0].length];
		checkRows();
	}

	public void paintBoard() {
		g2d.setColor(Color.white);
		g2d.fillRect(IMAGE_WIDTH / 2, IMAGE_HEIGHT / 2, IMAGE_WIDTH, IMAGE_HEIGHT);

		for (int i = 0; i < board.length; i++) {
			for (int j = 0; j < board[0].length; j++) {
				g2d.setColor(colors[board[i][j]]);
				g2d.fillRect((int) (TILE_WIDTH * (j + 0.5)) - 15, (int) (TILE_HEIGHT * (i + 0.5)) - 15,
						(int) (TILE_WIDTH), (int) (TILE_HEIGHT));
				g2d.setColor(Color.black);

				g2d.drawRect((int) (TILE_WIDTH * (j + 0.5)) - 15, (int) (TILE_HEIGHT * (i + 0.5)) - 15,
						(int) (TILE_WIDTH), (int) (TILE_HEIGHT));
			}
		}
		repaint();
	}

	public void initFrame() {
		setFocusable(true);

		JLabel jl = new JLabel(new ImageIcon(image));
		jl.setFocusable(true);
		JLabel prev = new JLabel(new ImageIcon(preview));
		prev.setLayout(new BoxLayout(prev, 2));
		score.setText(rows + "");
		prev.add(score);

		preg2d.setColor(Color.white);
		preg2d.fillRect(0, 0, IMAGE_WIDTH, IMAGE_HEIGHT);

		this.setLayout(new GridLayout());

		this.add(jl);
		this.add(prev);

		this.setVisible(true);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.pack();

		this.addKeyListener(new java.awt.event.KeyAdapter() {

			@Override
			public void keyTyped(KeyEvent e) {
			}

			@Override
			public void keyPressed(KeyEvent e) {
				if(controlling.settled){
					return;
				}
				if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
					controlling.move(x -> x + 1);
					paintBoard();

				} else if (e.getKeyCode() == KeyEvent.VK_LEFT) {
					controlling.move(x -> x - 1);
					paintBoard();

				} else if (e.getKeyCode() == KeyEvent.VK_UP) {
					controlling.rotate();

				} else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
					controlling.update();

				}
			}

			@Override
			public void keyReleased(KeyEvent e) {
			}
		});

	}
}

class Shape {
	int type;
	boolean settled = false;
	ArrayList<Point> coords;
	Tetris tetris;
	
	public Shape(int type, Tetris tetris) {
		this.tetris = tetris;
		this.type = type;
		coords = new ArrayList<Point>();
		switch (type) {
		case 1: {
			coords.add(new Point(Tetris.BOARD_WIDTH / 2 - 2, 0));
			coords.add(new Point(Tetris.BOARD_WIDTH / 2 - 1, 0));
			coords.add(new Point(Tetris.BOARD_WIDTH / 2, 0));
			coords.add(new Point(Tetris.BOARD_WIDTH / 2 + 1, 0));
			break;
		}
		case 2: {
			coords.add(new Point(Tetris.BOARD_WIDTH / 2 - 1, 0));
			coords.add(new Point(Tetris.BOARD_WIDTH / 2 - 1, 1));
			coords.add(new Point(Tetris.BOARD_WIDTH / 2, 1));
			coords.add(new Point(Tetris.BOARD_WIDTH / 2 + 1, 1));
			break;
		}
		case 3: {
			coords.add(new Point(Tetris.BOARD_WIDTH / 2 - 1, 1));
			coords.add(new Point(Tetris.BOARD_WIDTH / 2, 1));
			coords.add(new Point(Tetris.BOARD_WIDTH / 2 + 1, 1));
			coords.add(new Point(Tetris.BOARD_WIDTH / 2 + 1, 0));
			break;
		}
		case 4: {
			coords.add(new Point(Tetris.BOARD_WIDTH / 2 - 1, 1));
			coords.add(new Point(Tetris.BOARD_WIDTH / 2, 1));
			coords.add(new Point(Tetris.BOARD_WIDTH / 2 - 1, 0));
			coords.add(new Point(Tetris.BOARD_WIDTH / 2, 0));
			break;
		}
		case 5: {
			coords.add(new Point(Tetris.BOARD_WIDTH / 2 - 1, 1));
			coords.add(new Point(Tetris.BOARD_WIDTH / 2, 1));
			coords.add(new Point(Tetris.BOARD_WIDTH / 2, 0));
			coords.add(new Point(Tetris.BOARD_WIDTH / 2 + 1, 0));
			break;
		}
		case 6: {
			coords.add(new Point(Tetris.BOARD_WIDTH / 2 - 1, 1));
			coords.add(new Point(Tetris.BOARD_WIDTH / 2, 1));
			coords.add(new Point(Tetris.BOARD_WIDTH / 2 + 1, 1));
			coords.add(new Point(Tetris.BOARD_WIDTH / 2, 0));
			break;
		}
		case 7: {
			coords.add(new Point(Tetris.BOARD_WIDTH / 2 - 1, 0));
			coords.add(new Point(Tetris.BOARD_WIDTH / 2, 0));
			coords.add(new Point(Tetris.BOARD_WIDTH / 2, 1));
			coords.add(new Point(Tetris.BOARD_WIDTH / 2 + 1, 1));
			break;
		}
		}

		if (gameover()) {
			Tetris.timer.cancel();
			return;
		}

		fill(coords, tetris.board);
	}
	
	private void fill(ArrayList<Point> al, int[][] board){
		for (Point p : al) {
			board[p.y][p.x] = type;
		}
	}
	
	private void empty(ArrayList<Point> al, int[][] board){
		for (Point p : al) {
			board[p.y][p.x] = 0;
		}
	}

	public void rotate() {
		if (coords.size() == 0)
			return;

		Point pivot = new Point(0, 0);

		for (Point p : coords) {
			pivot.x += p.x;
			pivot.y += p.y;
		}

		pivot = new Point((int) Math.round(pivot.x * 1.0 / coords.size()),
				(int) Math.round(pivot.y * 1.0 / coords.size()));

		// change coords (rotate)
		ArrayList<Point> newPt = new ArrayList<Point>();
		for (Point p : coords) {
			Point a = new Point();
			a = new Point(pivot.x + (p.y - pivot.y), pivot.y - (p.x - pivot.x));
			a = new Point(pivot.x - (p.y - pivot.y), pivot.y + (p.x - pivot.x));
			if (a.x < 0 || a.x > tetris.board[0].length - 1 || a.y < 0 || a.y > tetris.board.length - 1) {
				return;
			}
			if (tetris.board[a.y][a.x] != 0 && !coords.contains(a)) {
				return;
			}
		}

		for (Point p : coords) {
			tetris.board[p.y][p.x] = 0;
		}

		for (Point p : coords) {
			Point a = new Point();
			a = new Point(pivot.x + (p.y - pivot.y), pivot.y - (p.x - pivot.x));
			a = new Point(pivot.x - (p.y - pivot.y), pivot.y + (p.x - pivot.x));
			if (a.x < 0 || a.x > tetris.board[0].length || a.y < 0 || a.y > tetris.board.length) {
				return;
			}
			newPt.add(a);
		}

		coords = new ArrayList<Point>();
		coords.addAll(newPt);

		for (Point p : coords) {
			tetris.board[p.y][p.x] = type;
		}

		tetris.paintBoard();
	}

	private boolean bool(int i, int j) {
		// if true, dont move
		if (coords.contains(new Point(j, i)))
			return false;
		else if (tetris.board[i][j] == 0)
			return false;
		return true;
	}

	public void move(Function<Integer, Integer> f) {
		for (Point p : coords) {
			int x1 = f.apply(p.x);
			if (x1 >= tetris.board[0].length || x1 < 0 || (bool(p.y, x1))) {
				return;
			}
		}
		for (Point p : coords) {
			tetris.board[p.y][p.x] = 0;
		}
		for (Point p : coords) {
			p.x = f.apply(p.x);
			tetris.board[p.y][p.x] = type;
		}
	}

	public void drop() {
		if (settled)
			return;
		for (Point p : coords) {
			try {
				if (bool(p.y + 1, p.x)) {
					settled = true;
					return;
				}
			} catch (ArrayIndexOutOfBoundsException e) {
				settled = true;
				return;
			}
		}
		for (Point p : coords) {
			p.y++;
		}
	}

	public void update() {
		if (settled) {
			return;
		}

		for (Point p : coords) {
			tetris.board[p.y][p.x] = 0;
		}

		drop();

		for (Point p : coords) {
			tetris.board[p.y][p.x] = type;
		}
	}

	private boolean gameover() {
		for (Point p : coords) {
			if (tetris.board[p.y][p.x] != 0) {
				return true;
			}
		}
		return false;
	}
}