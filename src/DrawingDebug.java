import java.awt.*;
import java.util.*;

public class DrawingDebug {
	public static void main(String[] args) {
		System.out.println("App Opened");
		DrawingTime frame = new DrawingTime();
		frame.updateColorRGB(Color.BLACK);
		frame.setBG(Color.gray);
		frame.updateSize(8);
		Scanner s = new Scanner(System.in);
		if(s.nextInt() == 0)
			System.out.println(frame.getSize());
		s.close();
	}
}
