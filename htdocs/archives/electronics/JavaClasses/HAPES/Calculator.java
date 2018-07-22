/**  * Calculator.java * * Description:	 * @author			Matt * @version			 */  // Capacitance, Speed, Discharge Timepackage HAPES;import java.awt.*;import java.awt.event.*;import java.applet.*;public class Calculator extends java.applet.Applet {// IMPORTANT: Source code between BEGIN/END comment pair will be regenerated// every time the form is saved. All manual changes will be overwritten.// BEGIN GENERATED CODE	// member declarations	java.awt.Label jouleTitle = new java.awt.Label();	java.awt.Label voltageLabel = new java.awt.Label();	java.awt.Label capLabel = new java.awt.Label();	java.awt.Label energyLabelCap = new java.awt.Label();	java.awt.TextField voltageField = new java.awt.TextField();	java.awt.TextField capField = new java.awt.TextField();	java.awt.TextField energyFieldCap = new java.awt.TextField();	java.awt.Label speedTitle = new java.awt.Label();	java.awt.Label massLabel = new java.awt.Label();	java.awt.Label energyLabelProj = new java.awt.Label();	java.awt.Label distanceLabel = new java.awt.Label();	java.awt.Label heightLabel = new java.awt.Label();	java.awt.Label speedLabel = new java.awt.Label();	java.awt.TextField massField = new java.awt.TextField();	java.awt.TextField energyFieldProj = new java.awt.TextField();	java.awt.TextField distanceField = new java.awt.TextField();	java.awt.TextField heightField = new java.awt.TextField();	java.awt.TextField speedField = new java.awt.TextField();	java.awt.Label errorLabel = new java.awt.Label();	java.awt.Label dischargeTitle = new java.awt.Label();	java.awt.Label resistLabel = new java.awt.Label();	java.awt.Label capLabelDischarge = new java.awt.Label();	java.awt.Label timeLabel = new java.awt.Label();	java.awt.TextField resistField = new java.awt.TextField();	java.awt.TextField capFieldDischarge = new java.awt.TextField();	java.awt.TextField timeField = new java.awt.TextField();// END GENERATED CODE	boolean isStandalone = false;	public Calculator() {	}	// Retrieve the value of an applet parameter	public String getParameter(String key, String def) {		return isStandalone ? System.getProperty(key, def) :			(getParameter(key) != null ? getParameter(key) : def);	}	// Get info on the applet parameters	public String[][] getParameterInfo() {		return null;	}	// Get applet information	public String getAppletInfo() {		return "Applet Information";	}		class CapSegment	{		public double CalcVoltage(double c, double j)		{			return Math.sqrt((2*j)/(c*1e-6));		}		public double CalcCap(double j, double v)		{			return ((2*j)/(v*v))*1e6;		}		public double CalcEnergy(double c, double v)		{			return (.5*c*v*v*1e-6);		}	}	CapSegment capCalc = new CapSegment();		class SpeedSegment	{		public double CalcMass(double j, double speed)		{			return (j/(2*speed*speed)) * 1e3;		}		public double CalcEnergy(double mass, double speed)		{			return .5*mass*speed*speed * 1e-3;		}		public double CalcDistance(double height, double speed)		{			return speed/(Math.sqrt(9.756/(2*height)));		}		public double CalcHeight(double distance, double speed)		{			return distance*distance*(9.756/(2*speed*speed));		}		public double CalcSpeedByEnergy(double j, double mass)		{			return Math.sqrt((2*j)/(mass*1e-3));		}		public double CalcSpeedByDistance(double distance, double height)		{			return distance*Math.sqrt(9.756/(2*height));		}	}	SpeedSegment speedCalc = new SpeedSegment();		class TimeSegment	{		public double CalcResistance(double c, double t)		{			return (c*1e-6)/t;		}		public double CalcCap(double r, double t)		{			return (r/t);		}		public double CalcTime(double r, double c)		{			return (c*1e-6)*r;		}	}	TimeSegment timeCalc = new TimeSegment();	// Initialize the applet	public void init() {		try {			initComponents();		}		catch (Exception e) {			e.printStackTrace();		}	}	public void initComponents() throws Exception {// IMPORTANT: Source code between BEGIN/END comment pair will be regenerated// every time the form is saved. All manual changes will be overwritten.// BEGIN GENERATED CODE		// the following code sets the frame's initial state		jouleTitle.setText("Energy in a Capacitor");		jouleTitle.setForeground(new java.awt.Color(125, 0, 0));		jouleTitle.setLocation(new java.awt.Point(8, 8));		jouleTitle.setVisible(true);		jouleTitle.setSize(new java.awt.Dimension(112, 22));		voltageLabel.setText("Voltage (V)");		voltageLabel.setForeground(java.awt.Color.black);		voltageLabel.setLocation(new java.awt.Point(10, 30));		voltageLabel.setVisible(true);		voltageLabel.setSize(new java.awt.Dimension(60, 20));		capLabel.setText("Capacitance (uF)");		capLabel.setLocation(new java.awt.Point(100, 30));		capLabel.setVisible(true);		capLabel.setSize(new java.awt.Dimension(90, 20));		energyLabelCap.setText("Energy (J)");		energyLabelCap.setLocation(new java.awt.Point(210, 30));		energyLabelCap.setVisible(true);		energyLabelCap.setSize(new java.awt.Dimension(70, 20));		voltageField.setText("100");		voltageField.setLocation(new java.awt.Point(10, 50));		voltageField.setVisible(true);		voltageField.setSize(new java.awt.Dimension(70, 20));		capField.setText("1000");		capField.setLocation(new java.awt.Point(100, 50));		capField.setVisible(true);		capField.setSize(new java.awt.Dimension(80, 20));		energyFieldCap.setText("5.00");		energyFieldCap.setLocation(new java.awt.Point(210, 50));		energyFieldCap.setVisible(true);		energyFieldCap.setSize(new java.awt.Dimension(70, 20));		speedTitle.setText("Projectile Speed");		speedTitle.setForeground(new java.awt.Color(125, 0, 0));		speedTitle.setLocation(new java.awt.Point(10, 80));		speedTitle.setVisible(true);		speedTitle.setSize(new java.awt.Dimension(90, 20));		massLabel.setText("Proj. Mass (g)");		massLabel.setLocation(new java.awt.Point(10, 100));		massLabel.setVisible(true);		massLabel.setSize(new java.awt.Dimension(80, 20));		energyLabelProj.setText("Energy (J)");		energyLabelProj.setLocation(new java.awt.Point(110, 100));		energyLabelProj.setVisible(true);		energyLabelProj.setSize(new java.awt.Dimension(60, 20));		distanceLabel.setText("Distance (m)");		distanceLabel.setLocation(new java.awt.Point(210, 100));		distanceLabel.setVisible(true);		distanceLabel.setSize(new java.awt.Dimension(80, 20));		heightLabel.setText("Height (m)");		heightLabel.setLocation(new java.awt.Point(10, 140));		heightLabel.setVisible(true);		heightLabel.setSize(new java.awt.Dimension(80, 20));		speedLabel.setText("Speed (m/s)");		speedLabel.setLocation(new java.awt.Point(110, 140));		speedLabel.setVisible(true);		speedLabel.setSize(new java.awt.Dimension(70, 20));		massField.setText("3.0");		massField.setLocation(new java.awt.Point(10, 120));		massField.setVisible(true);		massField.setSize(new java.awt.Dimension(70, 20));		energyFieldProj.setText("2.50");		energyFieldProj.setLocation(new java.awt.Point(110, 120));		energyFieldProj.setVisible(true);		energyFieldProj.setSize(new java.awt.Dimension(80, 20));		distanceField.setText("20.246");		distanceField.setLocation(new java.awt.Point(210, 120));		distanceField.setVisible(true);		distanceField.setSize(new java.awt.Dimension(70, 20));		heightField.setText("0.10");		heightField.setLocation(new java.awt.Point(10, 160));		heightField.setVisible(true);		heightField.setSize(new java.awt.Dimension(70, 20));		speedField.setText("40.82");		speedField.setLocation(new java.awt.Point(110, 160));		speedField.setVisible(true);		speedField.setSize(new java.awt.Dimension(90, 20));		errorLabel.setForeground(java.awt.Color.red);		errorLabel.setLocation(new java.awt.Point(10, 260));		errorLabel.setVisible(true);		errorLabel.setSize(new java.awt.Dimension(280, 20));		dischargeTitle.setText("Discharge Time");		dischargeTitle.setForeground(new java.awt.Color(125, 0, 0));		dischargeTitle.setLocation(new java.awt.Point(10, 190));		dischargeTitle.setVisible(true);		dischargeTitle.setSize(new java.awt.Dimension(90, 20));		resistLabel.setText("Resistance (ohms)");		resistLabel.setLocation(new java.awt.Point(10, 210));		resistLabel.setVisible(true);		resistLabel.setSize(new java.awt.Dimension(90, 20));		capLabelDischarge.setText("Capacitance (uF)");		capLabelDischarge.setLocation(new java.awt.Point(110, 210));		capLabelDischarge.setVisible(true);		capLabelDischarge.setSize(new java.awt.Dimension(90, 20));		timeLabel.setText("Time (s)");		timeLabel.setLocation(new java.awt.Point(210, 210));		timeLabel.setVisible(true);		timeLabel.setSize(new java.awt.Dimension(60, 20));		resistField.setText("100");		resistField.setLocation(new java.awt.Point(10, 230));		resistField.setVisible(true);		resistField.setSize(new java.awt.Dimension(70, 20));		capFieldDischarge.setText("1000");		capFieldDischarge.setLocation(new java.awt.Point(110, 230));		capFieldDischarge.setVisible(true);		capFieldDischarge.setSize(new java.awt.Dimension(80, 20));		timeField.setText("100");		timeField.setLocation(new java.awt.Point(210, 230));		timeField.setVisible(true);		timeField.setSize(new java.awt.Dimension(70, 20));		setLocation(new java.awt.Point(0, 0));		setLayout(null);		setSize(new java.awt.Dimension(300, 285));		add(jouleTitle);		add(voltageLabel);		add(capLabel);		add(energyLabelCap);		add(voltageField);		add(capField);		add(energyFieldCap);		add(speedTitle);		add(massLabel);		add(energyLabelProj);		add(distanceLabel);		add(heightLabel);		add(speedLabel);		add(massField);		add(energyFieldProj);		add(distanceField);		add(heightField);		add(speedField);		add(errorLabel);		add(dischargeTitle);		add(resistLabel);		add(capLabelDischarge);		add(timeLabel);		add(resistField);		add(capFieldDischarge);		add(timeField);// END GENERATED CODE	}		public boolean action(Event e, Object obj)	{		// Check the CapSegment first		if (e.target == voltageField || e.target == capField || e.target == energyFieldCap)		{			if (NumberFilled(capCalc) < 2)				return true;			if (IsBlank(voltageField))			{				double c = ObtainValue(capField);				double j = ObtainValue(energyFieldCap);				ShowValue(voltageField, capCalc.CalcVoltage(c, j));			}			else if (IsBlank(capField))			{				double v = ObtainValue(voltageField);				double j = ObtainValue(energyFieldCap);				ShowValue(capField, capCalc.CalcCap(j, v));			}			else if (IsBlank(energyFieldCap))			{				double c = ObtainValue(capField);				double v = ObtainValue(voltageField);				ShowValue(energyFieldCap, capCalc.CalcEnergy(c, v));			}			else				ReportError("Nothing is blank!");		}		// Now check the SpeedSegment		else if (e.target == massField || e.target == energyFieldProj || e.target == distanceField ||					e.target == heightField || e.target == speedField)		{			boolean done = false;			int count = 0;			if (NumberFilled(speedCalc) < 2)				return super.action(e, obj);			while (!done)			{				if (IsBlank(speedField))				{					if (IsBlank(distanceField) || IsBlank(heightField))					{						if (IsBlank(massField) || IsBlank(energyFieldProj))							return super.action(e, obj);						double j = ObtainValue(energyFieldProj);						double mass = ObtainValue(massField);						ShowValue(speedField, speedCalc.CalcSpeedByEnergy(j, mass));						if (IsBlank(heightField) && IsBlank(distanceField))							done = true;					}					else					{						double d = ObtainValue(distanceField);						double h = ObtainValue(heightField);						ShowValue(speedField, speedCalc.CalcSpeedByDistance(d, h));						if (IsBlank(massField) && IsBlank(energyFieldProj))							done = true;					}				}				else if (IsBlank(massField))				{					if (IsBlank(energyFieldProj))						return super.action(e, obj);					double j = ObtainValue(energyFieldProj);					double s = ObtainValue(speedField);					ShowValue(massField, speedCalc.CalcMass(j, s));					if (IsBlank(distanceField) && IsBlank(heightField))						done = true;				}				else if (IsBlank(energyFieldProj))				{					double m = ObtainValue(massField);					double s = ObtainValue(speedField);					ShowValue(energyFieldProj, speedCalc.CalcEnergy(m, s));					if (IsBlank(distanceField) && IsBlank(heightField))						done = true;				}				else if (IsBlank(distanceField))				{					if (IsBlank(heightField))						return super.action(e, obj);					double h = ObtainValue(heightField);					double s = ObtainValue(speedField);					ShowValue(distanceField, speedCalc.CalcDistance(h, s));									}				else if (IsBlank(heightField))				{					double d = ObtainValue(distanceField);					double s = ObtainValue(speedField);					ShowValue(heightField, speedCalc.CalcHeight(d, s));									}				else				{					done = true;					if (count == 0)						ReportError("Nothing is blank!");				}				count++;			}		}		else if (e.target == resistField || e.target == capFieldDischarge || e.target == timeField)		{			if (IsBlank(resistField))			{				double c = ObtainValue(capFieldDischarge);				double t = ObtainValue(timeField);				ShowValue(resistField, timeCalc.CalcResistance(c, t));			}			else if (IsBlank(capFieldDischarge))			{				double r = ObtainValue(resistField);				double t = ObtainValue(timeField);				ShowValue(capFieldDischarge, timeCalc.CalcCap(r, t));			}			else if (IsBlank(timeField))			{				double r = ObtainValue(resistField);				double c = ObtainValue(capFieldDischarge);				ShowValue(timeField, timeCalc.CalcTime(r, c));			}			else				ReportError("Nothing is blank!");		}		return super.action(e, obj);	}		public double ObtainValue(TextField field)	{		String s;		double value;				s = field.getText();		try		{			value = (Double.valueOf(s)).doubleValue();		} catch (Exception e) {			value = 0;			// Error reporting goes here		}		   		return value;	}		public boolean IsBlank(TextField field)	{		return ((field.getText()).equals(""));	}		public int NumberFilled(CapSegment s)	{		int blank = 0;		if (IsBlank(voltageField))			blank++;		if (IsBlank(capField))			blank++;		if (IsBlank(energyFieldCap))			blank++;		return 3 - blank; // Number filled	}		public int NumberFilled(SpeedSegment s)	{		int blank = 0;		if (IsBlank(massField))			blank++;		if (IsBlank(energyFieldProj))			blank++;		if (IsBlank(distanceField))			blank++;		if (IsBlank(heightField))			blank++;		if (IsBlank(speedField))			blank++;		return 5 - blank; // Number filled	}		public void ShowValue(TextField field, double value)	{		value = (double)(Math.round(value*100))/100;		String s = new Double((double)value).toString();		field.setText(s);		field.repaint();		ClearError(); // in all cases, ShowValue is only called when no errors are present	}		public void ReportError(String s)	{		errorLabel.setText(s);		errorLabel.repaint();	}		public void ClearError()	{		errorLabel.setText("");		errorLabel.repaint();	}}