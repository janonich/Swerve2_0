public class SwerveDrive {

	// random main method to test the code
	public static void main(String[] args) {
		SwerveDrive swerve = new SwerveDrive(1, 1);
		long last = System.currentTimeMillis();
		for (double f = -1; f <= 1; f += .1) {
			System.out.println("\n" + f + "\n");
			for (double s = -1; s <= 1; s += .1) {
				swerve.update(f, s, 0);
				System.out.println(swerve.FRA * swerve.RAD_TO_ROT + ","
						+ swerve.FLA * swerve.RAD_TO_ROT + "," + swerve.BLA
						* swerve.RAD_TO_ROT + "," + swerve.BRA
						* swerve.RAD_TO_ROT);
			}
		}
		System.out.println("\nRuns in (ms) "
				+ (System.currentTimeMillis() - last));
	}

	/*
	 * Swerve Drive Math Class v2.0
	 * 
	 * Updated since the old one experienced so much confusion and debugging
	 * that we need to clean it up and bad. This math class should be the most
	 * efficient since it takes into account our previous versions and problems.
	 * 
	 * When field centric, the rotation is field centric (ie, the rotation stick
	 * points you in the direction you want the robot to face), as well as the
	 * velocity. In robocentric, rot refers to the speed of rotation of the robot.
	 */

	final double RAD_TO_ROT = 1.0 / (2 * Math.PI);

	private double width;
	private double length;
	private boolean fieldcentric;

	public double FRS, FLS, BLS, BRS;
	public double FRA, FLA, BLA, BRA;

	private double oldFRA, oldFLA, oldBLA, oldBRA;

	public enum Out {
		oFRS, oFLS, oBLS, oBRS, oFRA, oFLA, oBLA, oBRA;
	}

	public SwerveDrive(double length, double width) {
		this.length = length;
		this.width = width;

		oldFRA = 0;
		oldFLA = 0;
		oldBLA = 0;
		oldBRA = 0;

		fieldcentric = false;
	}

	public SwerveDrive(double length, double width, boolean fieldcentric) {
		this(length, width);
		this.fieldcentric = fieldcentric;
	}

	public void update(double forward, double strafe, double rot) {

		if (forward == 0 && strafe == 0 && rot == 0) {
			FRS = 0;
			FLS = 0;
			BLS = 0;
			BRS = 0;

			FRA = oldFRA;
			FLA = oldFLA;
			BLA = oldBLA;
			BRA = oldBRA;
		}

		if (fieldcentric == true) {
			double tempF = forward;
			double tempS = strafe;

			forward = tempF * Math.sin(rot) + tempS * Math.cos(rot);
			strafe = tempF * Math.cos(rot) + tempS * Math.sin(rot);
			rot = 0;
		}

		// intermediates
		double topX = strafe - length * rot / 2;
		double bottomX = strafe + length * rot / 2;
		double rightY = forward + width * rot / 2;
		double leftY = forward - width * rot / 2;

		double tx2 = Math.pow(topX, 2);
		double bx2 = Math.pow(bottomX, 2);
		double ry2 = Math.pow(rightY, 2);
		double ly2 = Math.pow(leftY, 2);

		// speeds
		FRS = Math.sqrt(tx2 + ry2);
		FLS = Math.sqrt(tx2 + ly2);
		BLS = Math.sqrt(bx2 + ly2);
		BRS = Math.sqrt(bx2 + ry2);

		// angles
		FRA = Math.atan2(topX, rightY);
		FLA = Math.atan2(topX, leftY);
		BLA = Math.atan2(bottomX, leftY);
		BRA = Math.atan2(bottomX, rightY);

		// shortest path calc
		FRA %= 180;
		if (((int) (oldFRA - FRA) / 180) % 2 != 0)
			FRS *= -1;
		FLA %= 180;
		if (((int) (oldFRA - FRA) / 180) % 2 != 0)
			FRS *= -1;
		FRA %= 180;
		if (((int) (oldFRA - FRA) / 180) % 2 != 0)
			FRS *= -1;
		FRA %= 180;
		if (((int) (oldFRA - FRA) / 180) % 2 != 0)
			FRS *= -1;

	}

	public double output(Out out) {
		switch (out) {
		case oFRS:
			return FRS;
		case oFLS:
			return FLS;
		case oBLS:
			return BLS;
		case oBRS:
			return BRS;
		case oFRA:
			return FRA;
		case oFLA:
			return FLA;
		case oBLA:
			return BLA;
		case oBRA:
			return BRA;
		default:
			return 0;
		}
	}
}