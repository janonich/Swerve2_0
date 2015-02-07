public class SwerveDrive {

	/*
	 * Swerve Drive Math Class v2.0
	 * 
	 * Updated since the old one experienced so much confusion and debugging
	 * that we need to clean it up and bad. This math class should be the most
	 * efficient since it takes into account our previous versions and problems.
	 */

	private double width;
	private double length;
	private boolean fieldcentric;

	private double FRS, FLS, BLS, BRS;
	private double FRA, FLA, BLA, BRA;

	// private double oldFRA, oldFLA, oldBLA, oldBRA;

	public enum Out {
		FRS, FLS, BLS, BRS, FRA, FLA, BLA, BRA;
	}

	public SwerveDrive(double length, double width) {
		this.length = length;
		this.width = width;
		fieldcentric = false;
	}
	
	public SwerveDrive(double length, double width, boolean fieldcentric) {
		this(length, width);
		this.fieldcentric = fieldcentric;
	}
	
	public void update(double forward, double strafe, double rot) {

		if (fieldcentric == true){
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
		
		//angles
		FRA = Math.atan2(topX, rightY);
		FLA = Math.atan2(topX, leftY);
		BLA = Math.atan2(bottomX, leftY);
		BRA = Math.atan2(bottomX, rightY);
		
		

	}

	public double output(Out out) {
		switch (out) {
		case FRS:
			return FRS;
		case FLS:
			return FLS;
		case BLS:
			return BLS;
		case BRS:
			return BRS;
		case FRA:
			return FRA;
		case FLA:
			return FLA;
		case BLA:
			return BLA;
		case BRA:
			return BRA;
		default:
			return 0;
		}
	}
}