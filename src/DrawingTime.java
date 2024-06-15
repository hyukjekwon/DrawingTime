/**
 * @gooseisloos6, Project Started on 12/21/2021, just fiddling around
 */
import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import javax.swing.event.*;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

@SuppressWarnings("serial")
public class DrawingTime extends JFrame {
	private static int WIDTH = 480;
	private static int HEIGHT = 360;
	private static DrawingBrush canvas;
	private static JSlider red, green, blue;
	private static JSlider hue, sat, val;
	private static JSlider sizeslider, randslider;
	private static final String PRINT_MESSAGE = "";
	private int OS;
	private int x, y; // current dragging position
	private int size;
	private int currTool, lastTool; // 0 for draw, 1 for rect, 2 for line, 3 for eyedrop, 4 for smoothline, 5 for gradientline
	private static Color color;
	private int randScale = 0;
	
	private MouseMotionAdapter mouse = new MouseMotionAdapter() {
		@Override
		public void mouseDragged(MouseEvent e) {
			if (currTool == 3 || currTool == 5) {
				x = e.getX();
				y = e.getY();
				colorpicker();
			}
		}
	};
	private MouseMotionListener automouse = new MouseMotionListener() {
		@Override
		public void mouseDragged(MouseEvent e) {}
		@Override
		public void mouseMoved(MouseEvent e) {
			x = e.getX();
			y = e.getY();
			colorpicker();
		}
		
	};
	private Action changeTool = new AbstractAction() {
		@Override
		public void actionPerformed(ActionEvent e) {
			String key = e.getActionCommand();
//			System.out.println(e);
			switch(key) {
				case "b":
					updateTool(0);
					break;
				case "r":
					updateTool(1);
					break;
				case "l":
					updateTool(2);
					break;
				case "g":
					updateTool(5);
					break;
				case "f":
					updateTool(6);
					break;
				case " ":
					if(currTool != 3) {
						canvas.addMouseMotionListener(automouse);
						updateTool(3);
					}
					break;
				default:
					
			}
		}
	};
	private Action dropCmd = new AbstractAction() {
		@Override
		public void actionPerformed(ActionEvent e) {
			canvas.removeMouseMotionListener(automouse);
			updateTool(lastTool);
		}
	};
	private Action undoCmd = new AbstractAction() {
		@Override
		public void actionPerformed(ActionEvent e) {
			undo();
		}
	};
	private Action saveCmd = new AbstractAction() {
		@Override
		public void actionPerformed(ActionEvent e) {
			saveImage();
		}
	};
	private ChangeListener rgbChangeListener = new ChangeListener() {
		public void stateChanged(ChangeEvent event) {
	        updateColorRGB(new Color(red.getValue(), green.getValue(), blue.getValue()));
	    }
	};
	private ChangeListener hsvChangeListener = new ChangeListener() {
		public void stateChanged(ChangeEvent event) {
	        updateColorHSV(hue.getValue(), sat.getValue(), val.getValue());
	    }
	};
	public DrawingTime() { // main jframe constructor
		super("GOOSEPAINT");
		setOS();
		canvas = new DrawingBrush(WIDTH, HEIGHT);
		JPanel container = new JPanel();
		container.setLayout(new GridBagLayout());
		JPanel controls = new JPanel();
		controls.setLayout(new GridLayout(2, 1));
		JPanel sliders = new JPanel();
		sliders.setLayout(new GridLayout(1, 7));
		JPanel buttons = new JPanel();
		buttons.setLayout(new GridLayout(4, 2));
		JButton draw = newTool("draw", 0);
		JButton rect = newTool("rect", 1);
		JButton line = newTool("line", 2);
		JButton grad = newTool("gradient", 5);
		JButton eyedrop = newTool("eyedrop", 3);
		JButton save = new JButton("save");
		JButton fill = newTool("fill", 6);
		save.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e){
				saveImage();
			}
		});
		JButton undoButt = new JButton("undo");
		undoButt.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e){
				undo();
			}
		});
		JButton swapColor = new JButton("RGB/HSV");
		swapColor.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e){
				undo();
			}
		});
		removeSpace(draw);
		removeSpace(rect);
		removeSpace(line);
		removeSpace(fill);
		removeSpace(eyedrop);
		removeSpace(grad);
		removeSpace(save);
		removeSpace(undoButt);
		removeSpace(swapColor);
		removeSpace(container);

		buttons.add(draw);
		buttons.add(rect);
		buttons.add(line);
		buttons.add(fill);
		buttons.add(grad);
		buttons.add(eyedrop);
		buttons.add(save);
		buttons.add(undoButt);
//		buttons.add(swapColor);
		controls.add(buttons);
		
		this.setSize(WIDTH, HEIGHT);
		this.setResizable(true);
		this.getContentPane().setBackground(Color.white);
		this.setLocationRelativeTo(null);
		
		JPanel canvcontainer = new JPanel(new GridLayout(1, 1));
		canvcontainer.add(canvas);
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.ipadx = (int) (this.getSize().getWidth());
		c.ipady = HEIGHT;
		c.gridwidth = 1;
 		container.add(canvcontainer, c);
		c.gridx = 1;
		c.ipadx = (int) (25);
		c.ipady = (int) (this.getSize().getHeight() / 2);
		container.add(controls, c);
		
		addKeyBind(container, "B");
		addKeyBind(container, "R");
		addKeyBind(container, "L");
		addKeyBind(container, "G");
		addKeyBind(container, "F");
		addKeyBind(container, "SPACE");
		addDropperBind(container);
		addUndoBind(container);
		addSaveBind(container);
		
		red = new JSlider(JSlider.VERTICAL, 0, 255, 0);
		green = new JSlider(JSlider.VERTICAL, 0, 255, 0);
		blue = new JSlider(JSlider.VERTICAL, 0, 255, 0);
		hue = new JSlider(JSlider.VERTICAL, 0, 359, 0);
		sat = new JSlider(JSlider.VERTICAL, 0, 100, 0);
		val = new JSlider(JSlider.VERTICAL, 0, 100, 0);
		red.setPaintTicks(true);
		red.setMajorTickSpacing(63);
		green.setPaintTicks(true);
		green.setMajorTickSpacing(63);
		blue.setPaintTicks(true);
		blue.setMajorTickSpacing(63);
		hue.setPaintTicks(true);
		hue.setMajorTickSpacing(30);
		sat.setPaintTicks(true);
		sat.setMajorTickSpacing(10);
		val.setPaintTicks(true);
		val.setMajorTickSpacing(10);
		red.addChangeListener(rgbChangeListener);
		green.addChangeListener(rgbChangeListener);
		blue.addChangeListener(rgbChangeListener);
		hue.addChangeListener(hsvChangeListener);
		sat.addChangeListener(hsvChangeListener);
		val.addChangeListener(hsvChangeListener);
		sizeslider = new JSlider(JSlider.VERTICAL, 0, 16, 3);
		sizeslider.setPaintTicks(true);
		sizeslider.setMajorTickSpacing(1);
		sizeslider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent event) {
		        updateSize((int)Math.pow(1.5, sizeslider.getValue()));
			}
		});
		randslider = new JSlider(JSlider.VERTICAL, 0, 16, 0);
		randslider.setPaintTicks(true);
		randslider.setMajorTickSpacing(1);
		randslider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent event) {
		        updateRand((int)Math.pow(1.3, randslider.getValue()));
			}
		});
		sliders.add(red);
		sliders.add(green);
		sliders.add(blue);
		sliders.add(hue);
		sliders.add(sat);
		sliders.add(val);
		sliders.add(sizeslider);
		sliders.add(randslider);
		controls.add(sliders);
		MouseListener mouse2 = new MouseListener() {
			@Override
			public void mouseClicked(MouseEvent e) {
				// TODO Auto-generated method stub
			}
			@Override
			public void mousePressed(MouseEvent e) {
				// TODO Auto-generated method stub
				if(currTool == 3 || currTool == 5) {
					canvas.addMouseMotionListener(mouse);
					colorpicker();
				}
			}
			@Override
			public void mouseReleased(MouseEvent e) {
				x = e.getX();
				y = e.getY();
				if(currTool == 3 || currTool == 5) {
					canvas.removeMouseMotionListener(mouse);
				}
				rasterize();
			}
			@Override
			public void mouseEntered(MouseEvent e) {}
			@Override
			public void mouseExited(MouseEvent e) {}
		};
		canvas.addMouseListener(mouse2);
		canvas.setVisible(true);
		this.add(container);
		this.pack();
		this.setVisible(true);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	public JButton newTool(String s, int t) {
		JButton newButton = new JButton(s);
		newButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e){
				updateTool(t);
			}
		});
		return newButton;
	}
	public void removeSpace(JComponent contentPane) {
		contentPane.getInputMap().put(KeyStroke.getKeyStroke("SPACE"), "none");
		contentPane.getInputMap().put(KeyStroke.getKeyStroke("released SPACE"), "none");
	}
	public void addKeyBind(JComponent contentPane, String key) {
		InputMap iMap = contentPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
		ActionMap aMap = contentPane.getActionMap();
		iMap.put(KeyStroke.getKeyStroke(key), PRINT_MESSAGE);
		aMap.put(PRINT_MESSAGE, changeTool);
	}
	public void addDropperBind(JComponent contentPane) {
		InputMap iMap = contentPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
		ActionMap aMap = contentPane.getActionMap();
		KeyStroke keystroke = KeyStroke.getKeyStroke("released SPACE");
		iMap.put(keystroke, "releasedSPACE");
		aMap.put("releasedSPACE", dropCmd);
	}
	@SuppressWarnings("deprecation")
	public void addUndoBind(JComponent contentPane) {
		InputMap iMap = contentPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
		ActionMap aMap = contentPane.getActionMap();
		if(OS == 1)
			iMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_Z, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()), "undo");
		else
			iMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_Z, KeyEvent.CTRL_DOWN_MASK), "undo");
		aMap.put("undo", undoCmd);
	}
	public void addSaveBind(JComponent contentPane) {
		InputMap iMap = contentPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
		ActionMap aMap = contentPane.getActionMap();
		if(OS == 1)
			iMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_S, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()), "save");
		else
			iMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_DOWN_MASK), "save");
		aMap.put("save", saveCmd);
	}
	public void colorpicker() {
		try {
			BufferedImage image = (BufferedImage)canvas.createImage(WIDTH, HEIGHT);
			Graphics2D graphics = image.createGraphics();
			canvas.print(graphics);
			updateColorRGB(new Color(image.getRGB(x, y)));
		} catch(Exception e) {
		}
	}
	public void displayColor() {
		Graphics display = this.getGraphics();
		display.setColor(color);
		display.fillRect((this.getWidth() - this.getWidth()/10), 0, WIDTH/20, WIDTH/13);
		display.dispose();
	}
	public void updateColorRGB(Color c) {
		color = c;
		updateHSVSliders();
		if(currTool == 3 || currTool == 5)
			updateRGBSliders();
		displayColor();
		((DrawingBrush) canvas).setBrushColor(color);
	}
	public void updateColorHSV(int h, int s, int v) {
		double hh = h / 60.0;
		double sp = s / 100.0;
		double vp = v / 100.0;
		double chroma = vp * sp;
		double x = chroma * (1 - Math.abs((hh % 2) - 1));
		double m = vp - chroma;
		double r = (hh <= 2) ?
				(hh <= 1) ?
					chroma:
					x:
				(hh > 4) ?
					(hh > 5) ?
						chroma:
						x:
					0;
		double g = (hh <= 4) ?
				(hh > 1 && hh <= 3) ?
					chroma:
					x:
				0;
		double b = (hh > 2) ?
				(hh > 3 && hh <= 5) ?
						chroma:
						x:
					0;
		int rv = (int)((r + m) * 255);
		int gv = (int)((g + m) * 255);
		int bv = (int)((b + m) * 255);
		color = new Color(rv, gv, bv);
		displayColor();
		updateRGBSliders();
		((DrawingBrush) canvas).setBrushColor(color);
	}
	public void updateRGBSliders() {
		red.removeChangeListener(rgbChangeListener);
		green.removeChangeListener(rgbChangeListener);
		blue.removeChangeListener(rgbChangeListener);
		int r = color.getRed();
		int g = color.getGreen();
		int b = color.getBlue();
		red.setValue(r);
		green.setValue(g);
		blue.setValue(b);
		red.addChangeListener(rgbChangeListener);
		green.addChangeListener(rgbChangeListener);
		blue.addChangeListener(rgbChangeListener);
		
	}
	public void updateHSVSliders() {
		hue.removeChangeListener(hsvChangeListener);
		sat.removeChangeListener(hsvChangeListener);
		val.removeChangeListener(hsvChangeListener);
		int r = color.getRed();
		int g = color.getGreen();
		int b = color.getBlue();
		double rp = r / 255.0;
		double gp = g / 255.0;
		double bp = b / 255.0;
		double cmax = Math.max(Math.max(rp, gp), bp);
		double cmin = Math.min(Math.min(rp, gp), bp);
		double diff = cmax - cmin;
		int h = (int)((cmax == cmin) ?
					hue.getValue():
					(cmax == rp) ?
						(60 * ((gp - bp)/diff) + 360) % 360:
						(cmax == gp) ?
							(60 * ((bp - rp)/diff) + 120) % 360:
							(cmax == bp) ?
								(60 * ((rp - gp)/diff) + 240) % 360:
								-1.0);
		hue.setValue(h);
		sat.setValue((int)((cmax == 0) ? 0: (diff/cmax)*100));
		val.setValue((int)(cmax * 100.0));
		hue.addChangeListener(hsvChangeListener);
		sat.addChangeListener(hsvChangeListener);
		val.addChangeListener(hsvChangeListener);
	}
	public void updateSize(int s) {
		size = s;
		((DrawingBrush) canvas).setBrushSize(s);
	}
	public void updateRand(int r) {
		randScale = (int)(255 * (r / 66.55));
		((DrawingBrush) canvas).setRand(randScale);
	}
	public void updateTool(int t) {
		lastTool = currTool;
		currTool = t;
		((DrawingBrush) canvas).setTool(t);
		if(t == 5)
			canvas.addMouseMotionListener(automouse);
		else if(t != 3 && canvas.getMouseListeners().length > 0)
			canvas.removeMouseMotionListener(automouse);
	}
	public void undo() {
		((DrawingBrush) canvas).undo();
	}
	public void setBG(Color c) {
		((DrawingBrush)canvas).setBackground(c);
	}
	public void rasterize() {
		// int[] offsets = canvas.getOffsets();
		// BufferedImage raster = new BufferedImage(canvas.d2i(WIDTH * offsets[2]), HEIGHT, BufferedImage.TYPE_INT_ARGB);
		BufferedImage raster = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_ARGB);
		canvas.paint(raster.getGraphics());
		saveImage();
		((DrawingBrush) canvas).addToFrames(raster);
	}
	public void saveImage() {
		try {
			BufferedImage image = (BufferedImage)canvas.createImage(WIDTH, HEIGHT);
			Graphics2D g = image.createGraphics();
			canvas.print(g);
			File f = new File("a.png");
			ImageIO.write(image, "png", f);
		} catch(Exception e) {
			System.out.println("ERROR SAVING IMAGE");
		}
	}
	public void setOS() {
		String os = System.getProperty("os.name").toLowerCase().substring(0, 3);
		switch(os) {
			case "win": // windows
				OS = 0;
				break;
			case "mac": // mac os
				OS = 1;
				break;
			case "nix": // unix
			case "nux":
			case "aix":
				OS = 2;
				break;
			case "sun": // solaris
				OS = 3;
				break;
			default: // no os found
				OS = -1;
		}
	}
}