public class SwerveDrive {

	// random main method to test the code
	public static void main(String[] args) {
		SwerveDrive swerve = new SwerveDrive(1, 1);
		long last = System.currentTimeMillis();
		for (int f = -10; f <= 10; f += 1) {
			System.out.println("\nf = " + (double) f / 10.0
					+ ", s in tenths from -1 to 1\n\n\tAngles");
			for (int s = -10; s <= 10; s += 1) {
				swerve.update((double) f / 10.0, (double) s / 10.0, 0);
				System.out.println("\t" + swerve.FRA * swerve.RAD_TO_ROT);
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
	 * INPUTS:
	 * 
	 * forward: [-1,1]; strafe: [-1,1]; Velocity is (+,+) in the first quadrant;
	 * 
	 * rot: [R] rad; heading: [R] rad; Positive is clockwise;
	 * 
	 * NOTE: When field centric, the rotation is field centric (ie, the rotation
	 * stick points you in the direction you want the robot to face), as well as
	 * the velocity. In robocentric, rot refers to the speed of rotation of the
	 * robot.
	 */

	final double RAD_TO_ROT = 1.0 / (2 * Math.PI);

	private double width, length, radius;
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

		radius = Math.sqrt(Math.pow(length, 2) + Math.pow(width, 2));

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

		// intermediates
		double topX = strafe - length * rot / radius;
		double bottomX = strafe + length * rot / radius;
		double rightY = forward + width * rot / radius;
		double leftY = forward - width * rot / radius;

		double tx2 = Math.pow(topX, 2);
		double bx2 = Math.pow(bottomX, 2);
		double ry2 = Math.pow(rightY, 2);
		double ly2 = Math.pow(leftY, 2);

		// speeds
		FRS = Math.sqrt(tx2 + ry2);
		FLS = Math.sqrt(tx2 + ly2);
		BLS = Math.sqrt(bx2 + ly2);
		BRS = Math.sqrt(bx2 + ry2);

		// normalize into [-1,1]
		double max = Math.max(Math.max(Math.abs(FRS), Math.abs(FLS)),
				Math.max(Math.abs(BLS), Math.abs(BRS)));
		if (max > 1) {
			FRS /= max;
			FLS /= max;
			BLS /= max;
			BRS /= max;
		}

		// angles, in rotations
		FRA = Math.atan2(topX, rightY) * RAD_TO_ROT;
		FLA = Math.atan2(topX, leftY) * RAD_TO_ROT;
		BLA = Math.atan2(bottomX, leftY) * RAD_TO_ROT;
		BRA = Math.atan2(bottomX, rightY) * RAD_TO_ROT;

		// make it turn nicely
		int dir;
		// fr
		FRA += (int) (oldFRA - FRA);
		if (oldFRA > FRA)
			dir = 1;
		else
			dir = -1;
		while (Math.abs(oldFRA - FRA) < .25) {
			FRA += dir * .5;
			FRS *= -1;
		}
		// fl
		FLA += (int) (oldFLA - FLA);
		if (oldFLA > FLA)
			dir = 1;
		else
			dir = -1;
		while (Math.abs(oldFLA - FLA) < .25) {
			FLA += dir * .5;
			FLS *= -1;
		}
		// bl
		BLA += (int) (oldBLA - BLA);
		if (oldBLA > BLA)
			dir = 1;
		else
			dir = -1;
		while (Math.abs(oldBLA - BLA) < .25) {
			BLA += dir * .5;
			BLS *= -1;
		}
		// br
		BRA += (int) (oldBRA - BRA);
		if (oldBRA > BRA)
			dir = 1;
		else
			dir = -1;
		while (Math.abs(oldBRA - BRA) < .25) {
			BRA += dir * .5;
			BRS *= -1;
		}

		oldFRA = FRA;
		oldFLA = FLA;
		oldBLA = BLA;
		oldBRA = BRA;

	}

	public void update(double forward, double strafe, double rot, double heading) {
		// field centric heading modifier
		if (fieldcentric == true) {
			double tempF = forward;
			double tempS = strafe;

			forward = tempF * Math.sin(rot) + tempS * Math.cos(rot);
			strafe = -1 * tempF * Math.cos(rot) + tempS * Math.sin(rot);
		}

		this.update(forward, strafe, rot);
	}

	public double output(Out out) {
		// return is in rotations for angles
		// return is normed to [-1,1] for speed
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