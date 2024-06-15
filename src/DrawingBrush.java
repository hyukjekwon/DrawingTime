import java.awt.*;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.event.*;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.awt.geom.AffineTransform;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

@SuppressWarnings("serial")
public class DrawingBrush extends JPanel {
	private Stack<BufferedImage> frames;
	private List<Strokes> strokes, currStroke;
	private List<Integer> indivStrokes;
	private AffineTransform at;
	private int WIDTH, HEIGHT;
	private int indivStroke;
	private int x, y;
	private int x0, y0;
	private int x1, y1;
	private int size;
	private int currTool;
	private Color color, color0, randColor;
	private int randScale = 0;
	private boolean isReleased = true;
	private double zoomfactor = 1;
	private double prevzoomfactor = 1;
	private double xoffset, yoffset = 0;
	private boolean zoomer = false;


	public DrawingBrush(int w, int h) {
		WIDTH = w;
		HEIGHT = h;
		frames = new Stack<BufferedImage>();
		strokes = new ArrayList<Strokes>();
		currStroke = new ArrayList<Strokes>();
		indivStrokes = new ArrayList<Integer>();
		indivStroke = 0;
		MouseMotionAdapter mouse = new MouseMotionAdapter() {
			@Override
			public void mouseDragged(MouseEvent e) {
				x = e.getX();
				y = e.getY();
				switch (currTool) {
					case 0:
						x -= size / 2;
						y -= size / 2;
						smoothing();
						break;
					case 1:
					case 2:
						repaint();
						break;
					case 5:
						color0 = getPixel(x0, y0);
						if(x > 0 && x < WIDTH && y > 0 && y < HEIGHT)
							color = getPixel(x, y);
						repaint();
						break;
					case 6:
						filling();
						break;
					default:
						
				}
			}
		};
		MouseListener mouse2 = new MouseListener() {
			@Override
			public void mouseClicked(MouseEvent e) {
				// TODO Auto-generated method stub
			}
			@Override
			public void mousePressed(MouseEvent e) {
				// TODO Auto-generated method stub
				isReleased = false;
				currStroke = new ArrayList<Strokes>();
				if(randScale != 0) {
					randColor = randomizeColor(color);
				}
				x0 = e.getX();
				y0 = e.getY();
				x1 = -1; // new stroke detected for smoothing
			}
			@Override
			public void mouseReleased(MouseEvent e) {
				isReleased = true;
				x = e.getX();
				y = e.getY();
				switch (currTool) {
					case 0:
						break;
					case 1:
						Strokes rect = new Strokes(Math.min(x0, x), Math.min(y0, y), Math.abs(x - x0), Math.abs(y - y0), randColor);
						currStroke.add(rect);
						indivStroke++;
						break;
					case 2:
						Strokes line = new Strokes(x0, y0, x, y, size, randColor);
						currStroke.add(line);
						indivStroke++;
						break;
					case 5:
						if(x == x0 && y == y0)
							break;
						Strokes gradline = new Strokes(x0, y0, x, y, size, color0, color);
						currStroke.add(gradline);
						indivStroke++;
						break;
				}
				repaint();
				indivStrokes.add(indivStroke);
				indivStroke = 0;
				randColor = color;
			}
			@Override
			public void mouseEntered(MouseEvent e) {}
			@Override
			public void mouseExited(MouseEvent e) {}
		};
		MouseWheelListener mouse3 = new MouseWheelListener() {
			@Override
			public void mouseWheelMoved(MouseWheelEvent e) {
				zoomer = true;
		
				//Zoom in
				if (e.getWheelRotation() < 0) {
					zoomfactor *= 1.1;
					repaint();
				}
				//Zoom out
				if (e.getWheelRotation() > 0) {
					zoomfactor /= 1.1;
					repaint();
				}
			}
		};
		this.addMouseMotionListener(mouse);
		this.addMouseListener(mouse2);
		this.addMouseWheelListener(mouse3);
	}
	public int getRand(int x) {
		return (int)(Math.random() * x) - (x/2);
	}
	public int d2i(double d) {
		return (int)Math.round(d);
	}
	public int[] getOffsets() {
		int[] offsets = new int[3];
		offsets[0] = d2i(xoffset);
		offsets[1] = d2i(yoffset);
		offsets[2] = d2i(zoomfactor);
		return offsets;
	}
	public Color randomizeColor(Color c) {
		int r = c.getRed() + getRand(randScale);
		int g = c.getGreen() + getRand(randScale);
		int b = c.getBlue() + getRand(randScale);
		r = r < 0 ? 0: r > 255 ? 255: r;
		g = g < 0 ? 0: g > 255 ? 255: g;
		b = b < 0 ? 0: b > 255 ? 255: b;
		return new Color(r, g, b);
	}
	public void addToFrames(BufferedImage i) {
		frames.push(i);
	}
	public void filling() {
		if(x1 == -1) { // beginning of new stroke
			x1 = x;
			y1 = y;
		} else {
			Strokes tri = new Strokes(x0, y0, x1, y1, x, y, randColor);
			currStroke.add(tri);
			indivStroke++;
			repaint();
			x1 = x;
			y1 = y;
		}
	}
	public void smoothing() {
		int mid = size/2;
		if(x1 == -1) { // beginning of new stroke
			x1 = x;
			y1 = y;
		} else {
			Strokes line = new Strokes(x1 + mid, y1 + mid, x + mid, y + mid, size, false, randColor);
			currStroke.add(line);
			indivStroke++;
			repaint();
			x1 = x;
			y1 = y;
		}
	}
	public void setBrushColor(Color c) {
		color = c;
		randColor = c;
	}
	public void setBrushSize(int s) {
		size = s;
	}
	public void setRand(int r) {
		randScale = r;
	}
	public void setTool(int t) {
		currTool = t;
	}
	public void undo() {
		if(!currStroke.isEmpty())
			for(int i = 0; i < indivStrokes.get(indivStrokes.size() - 1); i++) {
				currStroke.remove(currStroke.size() - 1);
			}
		if(!frames.isEmpty())
			frames.pop();
		repaint();
	}
	public BufferedImage importImage() {
		try {
            return (BufferedImage)ImageIO.read(new File("a.png"));
        } catch (Exception e) {
            e.printStackTrace();
            return new BufferedImage(HEIGHT, HEIGHT, HEIGHT);
        }
	}
	public Color getPixel(int a, int b) {
		BufferedImage image = null;
		try {
			image = (BufferedImage)this.createImage(WIDTH, HEIGHT);
			Graphics2D graphics = image.createGraphics();
			this.print(graphics);
		} catch(Exception e) {
		}
		return new Color(image.getRGB(a, b));
	}
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;
		g2.setColor(Color.WHITE);
		g2.fillRect(d2i(xoffset), d2i(yoffset), d2i(WIDTH * zoomfactor), d2i(HEIGHT * zoomfactor));
		if(zoomer) {
			at = new AffineTransform();

            double xRel = MouseInfo.getPointerInfo().getLocation().getX() - getLocationOnScreen().getX();
            double yRel = MouseInfo.getPointerInfo().getLocation().getY() - getLocationOnScreen().getY();

            double zoomDiv = zoomfactor / prevzoomfactor;

            xoffset = (zoomDiv) * (xoffset) + (1 - zoomDiv) * xRel;
            yoffset = (zoomDiv) * (yoffset) + (1 - zoomDiv) * yRel;

            at.translate(xoffset, yoffset);
            at.scale(zoomfactor, zoomfactor);
            prevzoomfactor = zoomfactor;
            g2.transform(at);
            zoomer = false;
		}
		if(!frames.isEmpty()) {
			BufferedImage currFrame = frames.lastElement();
			g2.drawImage(currFrame, 0, 0, WIDTH, HEIGHT, this);
		}
		if(!isReleased) { // preview for rect/line/grad
			g2.setColor(color);
			switch(currTool) {
				case 1:
					g2.fillRect(Math.min(x, x0), Math.min(y, y0), Math.abs(x - x0), Math.abs(y - y0));
					break;
				case 2:
					g2.setStroke(new BasicStroke(size, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL));
					g2.drawLine(x0, y0, x, y);
					break;
				case 5:
//					if(color0.getRGB() != color.getRGB()) {
//						float[] frac = {0.0f, 1.0f};
//						Color[] clors = {color0, color};
//						int sx0 = x0;
//						int sy0 = y0;
//						int sx = x;
//						int sy = y;
//						if(x < x0 || y < y0) {
//							sx0 = x;
//							sy0 = y;
//							sx = x0;
//							sy = y0;
//							Color temp = clors[0];
//							clors[0] = clors[1];
//							clors[1] = temp;
//						}
//						LinearGradientPaint grad = new LinearGradientPaint(sx0, sy0, sx, sy, frac, clors);
//						g2.setPaint(grad);
//						System.out.println(color);
//					}
//					g2.setStroke(new BasicStroke(size, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL));
//					g2.drawLine(x0, y0, x, y); 
					break;
				default:
			}
		}
		if(!currStroke.isEmpty())
			for(int i = 0; i < currStroke.size(); i++) {
				Strokes s = currStroke.get(i);
				g2.setColor(s.getColor());
				switch(s.getTool()) {
					case 0: // normal brush
						//g2.fill(s.getShape());
						break;
					case 1: // rectangle
						g2.fill(s.getShape());
						break;
					case 2: // line
						g2.setStroke(new BasicStroke(s.getSize(), BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL));
						g2.draw(s.getShape());
						break;
					case 4: // smooth line
	//					g2.setColor(Color.RED);
						BasicStroke stroke = new BasicStroke(s.getSize(), BasicStroke.CAP_ROUND, BasicStroke.JOIN_BEVEL);
						g2.setStroke(stroke);
						g2.draw(s.getShape());
						break;
					case 5: // gradient line
						float[] frac = {0.0f, 1.0f};
						Color[] clors = {s.getColor(), s.getColor2()};
						int sx0 = s.getX0();
						int sy0 = s.getY0();
						int sx1 = s.getX1();
						int sy1 = s.getY1();
						LinearGradientPaint grad = new LinearGradientPaint(sx0, sy0, sx1, sy1, frac, clors);
						g2.setPaint(grad);
						g2.setStroke(new BasicStroke(s.getSize(), BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL));
						g2.draw(s.getShape());
//						g2.fillRect(0, 0, WIDTH, HEIGHT);
						break;
					case 6:
						g2.fill(s.getShape());
						break;
					default:
						
				}
			}
		g2.dispose();
	}
}