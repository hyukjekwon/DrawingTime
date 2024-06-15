import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Ellipse2D.Double;
import java.awt.*;

@SuppressWarnings("serial")
public class Strokes {
	private Shape shape;
	private Color brushColor;
	private Color color2;
	private int size;
	private int tool;
	private int x0, y0, x1, y1, x2, y2;
	public Strokes(int x, int y, int s, Color c) { // normal stroke
		shape = new Ellipse2D.Double(x, y, s , s);
		tool = 0;
		brushColor = c;
	}
	public Strokes(int x0, int y0, int l, int h, Color c) { // rectangle
		shape = new Rectangle2D.Double(x0, y0, l, h);
		tool = 1;
		brushColor = c;
	}
	public Strokes(int x0, int y0, int x1, int y1, int s, Color c) { // line
		shape = new Line2D.Double(x0, y0, x1, y1);
		tool = 2;
		size = s;
		brushColor = c;
	}
	public Strokes(int x0, int y0, int x1, int y1, int s, boolean sm, Color c) { // smooth line
		shape = new Line2D.Double(x0, y0, x1, y1);
		tool = 4;
		size = s;
		brushColor = c;
	}
	public Strokes(int x, int y, int xx, int yy, int s, Color c1, Color c2) { // smooth line
		shape = new Line2D.Double(x, y, xx, yy);
		tool = 5;
		size = s;
		brushColor = c1;
		color2 = c2;
		x0 = x;
		y0 = y;
		x1 = xx;
		y1 = yy;
	}
	public Strokes(int sx0, int sy0, int sx1, int sy1, int sx2, int sy2, Color c) { // triangle
		tool = 6;
		brushColor = c;
		x0 = sx0;
		y0 = sy0;
		x1 = sx1;
		y1 = sy1;
		x2 = sx2;
		y2 = sy2;
		shape = new Path2D.Double();
		((Path2D) shape).moveTo(x0, y0);
		((Path2D) shape).lineTo(x1, y1);
		((Path2D) shape).lineTo(x2, y2);
		((Path2D) shape).closePath();
	}
	public int getTool() {
		return tool;
	}
	public int getX0() {
		return x0;
	}
	public int getY0() {
		return y0;
	}
	public int getX1() {
		return x1;
	}
	public int getY1() {
		return y1;
	}
	public Shape getShape() {
		return shape;
	}
	public int getSize() {
		return size;
	}
	public void setColor(Color c) {
		brushColor = c;
	}
	public Color getColor() {
		return brushColor;
	}
	public Color getColor2() {
		return color2;
	}
}