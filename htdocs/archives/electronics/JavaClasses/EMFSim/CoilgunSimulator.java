//
//  CoilgunSimulator.java
//  CoilgunSimulator
//
//  Created by Matthew Hielscher on Mon Jan 12 2004.
//  Copyright (c) 2004 Matt's Coilgun Center. All rights reserved.
//
//  A Java Swing applet, built to replace the EMF Simulator Applet,
//  which was not complete and whose code was lost in a server change.
//  Based on the EMF Math page and the Coilgun presentation poster,
//  with minor changes.
//
//  The primary equation used here is:
//  Force = (pi*r^2)/(2*mu0) * (B(b)^2 - B(a)^2)

import java.awt.*;
import java.awt.event.*;
import java.applet.*;
import javax.swing.*;

public class CoilgunSimulator extends JApplet implements ActionListener {
	
	// GUI components
	JLabel resLabel;
    JLabel capLabel;
    JLabel voltLabel;
    JTextField resField;
    JTextField capField;
    JTextField voltField;
    JLabel lengthLabel;
    JLabel diLabel;
    JLabel doLabel;
	JLabel clampResLabel;
    JLabel gaugeLabel;
    JTextField lengthField;
    JTextField diField;
    JTextField doField;
	JTextField clampResField;
    JTextField gaugeField;
    JLabel massLabel;
    JLabel distLabel;
    JTextField massField;
    JTextField distField;
    JButton calcButton;
    JLabel errorLabel;
    JLabel diamLabel;
    JTextField diamField;
    JLabel projLenLabel;
    JTextField projLenField;
    JLabel maxVelLabel;
    JLabel finalVelLabel;
    JLabel maxVelField;
    JLabel finalVelField;
    JLabel peakLabel;
    JLabel peakField;
    JCheckBox clampCheckbox;
	
	// coil properties
    double extRes;
    double intRes;
    double cap;
    double volt;
	double len;
    double innerDiameter;
    double outerDiameter;
	double clampRes;
    double innerRadius;
    double outerRadius;
    double turns;
    double layers;
    int wireGauge;
    double wireDiameter;
	
	// misc
    double u;
    double a;
    double b;
    int damped;
    double charge[];
    int zeroChargeIndex;
    double current[];
    double timeStep;
    double maxTime;
    int numberOfSteps;
    double mass;
    double diameter;
    double projLen;
    double position[];
    double velocity[];
    double maxVelocity;
    double maxPosition;
    double maxCurrent;
    double permVelocity;
    double permCurrent;
    int originX;
    int originY;
    int endX;
    int endY;
    int xDist;
    int yDist;
    boolean clampDiode;
    static final double mu0 = 4.E-7*Math.PI;
	
    public void init() {
        // set the default look and feel
        String laf = UIManager.getSystemLookAndFeelClassName();
        try {
            UIManager.setLookAndFeel(laf);
        } catch (UnsupportedLookAndFeelException exc) {
            System.err.println ("Warning: UnsupportedLookAndFeel: " + laf);
        } catch (Exception exc) {
            System.err.println ("Error loading " + laf + ": " + exc);
        }
            getContentPane().setLayout (null);
		
		setFont(new Font("Arial", Font.PLAIN, 10));
		
		resLabel = new JLabel();
        capLabel = new JLabel();
        voltLabel = new JLabel();
        resField = new JTextField();
        capField = new JTextField();
        voltField = new JTextField();
        lengthLabel = new JLabel();
        diLabel = new JLabel();
        doLabel = new JLabel();
		clampResLabel = new JLabel();
        gaugeLabel = new JLabel();
        lengthField = new JTextField();
        diField = new JTextField();
        doField = new JTextField();
		clampResField = new JTextField();
        gaugeField = new JTextField();
        massLabel = new JLabel();
        distLabel = new JLabel();
        massField = new JTextField();
        distField = new JTextField();
        calcButton = new JButton();
        errorLabel = new JLabel();
        diamLabel = new JLabel();
        diamField = new JTextField();
        projLenLabel = new JLabel();
        projLenField = new JTextField();
        maxVelLabel = new JLabel();
        finalVelLabel = new JLabel();
        maxVelField = new JLabel();
        finalVelField = new JLabel();
        peakLabel = new JLabel();
        peakField = new JLabel();
        clampCheckbox = new JCheckBox();
		
		try
        {
            initComponents();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
		
		clampDiode = false;
        extRes = 0.25D;
        cap = 0.005D;
        volt = 200D;
        len = 0.04D;
        innerDiameter = 0.005D;
        outerDiameter = 0.02D;
        wireGauge = 18;
        timeStep = 3E-5D;
        maxTime = 0.015D;
        numberOfSteps = 500;
        mass = 0.001D;
        diameter = 0.004D;
        projLen = 0.04D;
        CalculateRLCMacros(CalculateInductance(0));
        CalculateCoilDimensions();
        originX = 35;
        originY = 420;
        endX = 480;
        endY = 185;
        xDist = endX - originX;
        yDist = originY - endY;
        current = new double[numberOfSteps];
        charge = new double[numberOfSteps];
        position = new double[numberOfSteps];
        position[0] = -0.03D;
        velocity = new double[numberOfSteps];
        velocity[0] = 0.0D;
        zeroChargeIndex = 0;
        CalculateVelocityWave();
        repaint();
    }
	
	public void initComponents()
        throws Exception
    {
		Font defaultFont = new Font("Arial", Font.PLAIN, 11);
        resLabel.setText("Ext. Resistance (ohms)");
        resLabel.setLocation(new Point(10, 10));
        resLabel.setVisible(true);
        resLabel.setSize(new Dimension(120, 20));
		resLabel.setFont(defaultFont);
        capLabel.setText("Capacitance (uF)");
        capLabel.setLocation(new Point(140, 10));
        capLabel.setVisible(true);
        capLabel.setSize(new Dimension(90, 20));
		capLabel.setFont(defaultFont);
        voltLabel.setText("Voltage (V)");
        voltLabel.setLocation(new Point(250, 10));
        voltLabel.setVisible(true);
        voltLabel.setSize(new Dimension(60, 20));
		voltLabel.setFont(defaultFont);
        resField.setText("0.25");
        resField.setLocation(new Point(10, 30));
        resField.setVisible(true);
        resField.setSize(new Dimension(60, 20));
		resField.setFont(defaultFont);
        capField.setText("5000");
        capField.setLocation(new Point(140, 30));
        capField.setVisible(true);
        capField.setSize(new Dimension(60, 20));
		capField.setFont(defaultFont);
        voltField.setText("200");
        voltField.setLocation(new Point(250, 30));
        voltField.setVisible(true);
        voltField.setSize(new Dimension(60, 20));
		voltField.setFont(defaultFont);
        lengthLabel.setText("Coil Length (mm)");
        lengthLabel.setLocation(new Point(10, 50));
        lengthLabel.setVisible(true);
        lengthLabel.setSize(new Dimension(90, 20));
		lengthLabel.setFont(defaultFont);
        diLabel.setText("Inner Diam. (mm)");
        diLabel.setLocation(new Point(130, 50));
        diLabel.setVisible(true);
        diLabel.setSize(new Dimension(90, 20));
		diLabel.setFont(defaultFont);
        doLabel.setText("Outer Diam. (mm)");
        doLabel.setLocation(new Point(250, 50));
        doLabel.setVisible(true);
        doLabel.setSize(new Dimension(90, 20));
		doLabel.setFont(defaultFont);
		clampResLabel.setText("Clamp Resistance (ohms)");
        clampResLabel.setLocation(new Point(380, 50));
        clampResLabel.setVisible(true);
        clampResLabel.setSize(new Dimension(120, 20));
		clampResLabel.setFont(defaultFont);
        gaugeLabel.setText("Wire Gauge (AWG)");
        gaugeLabel.setLocation(new Point(380, 10));
        gaugeLabel.setVisible(true);
        gaugeLabel.setSize(new Dimension(100, 20));
		gaugeLabel.setFont(defaultFont);
        lengthField.setText("40.0");
        lengthField.setLocation(new Point(10, 70));
        lengthField.setVisible(true);
        lengthField.setSize(new Dimension(60, 20));
		lengthField.setFont(defaultFont);
        diField.setText("5.00");
        diField.setLocation(new Point(130, 70));
        diField.setVisible(true);
        diField.setSize(new Dimension(60, 20));
		diField.setFont(defaultFont);
        doField.setText("20.0");
        doField.setLocation(new Point(250, 70));
        doField.setVisible(true);
        doField.setSize(new Dimension(60, 20));
		doField.setFont(defaultFont);
		clampResField.setText("0.01");
        clampResField.setLocation(new Point(380, 70));
        clampResField.setVisible(true);
        clampResField.setSize(new Dimension(60, 20));
		clampResField.setFont(defaultFont);
        gaugeField.setText("18");
        gaugeField.setLocation(new Point(380, 30));
        gaugeField.setVisible(true);
        gaugeField.setSize(new Dimension(70, 20));
		gaugeField.setFont(defaultFont);
        massLabel.setText("Proj. Mass (g)");
        massLabel.setLocation(new Point(130, 90));
        massLabel.setVisible(true);
        massLabel.setSize(new Dimension(80, 20));
		massLabel.setFont(defaultFont);
        distLabel.setText("Initial Position (mm)");
        distLabel.setLocation(new Point(10, 90));
        distLabel.setVisible(true);
        distLabel.setSize(new Dimension(110, 20));
		distLabel.setFont(defaultFont);
        massField.setText("1.00");
        massField.setLocation(new Point(130, 110));
        massField.setVisible(true);
        massField.setSize(new Dimension(60, 20));
		massField.setFont(defaultFont);
        distField.setText("30.0");
        distField.setLocation(new Point(10, 110));
        distField.setVisible(true);
        distField.setSize(new Dimension(60, 20));
		distField.setFont(defaultFont);
        calcButton.setLocation(new Point(10, 140));
        calcButton.setLabel("Calculate");
        calcButton.setVisible(true);
        calcButton.setSize(new Dimension(110, 30));
		calcButton.addActionListener(this);
		calcButton.setFont(defaultFont);
        errorLabel.setForeground(Color.red);
        errorLabel.setLocation(new Point(10, 450));
        errorLabel.setVisible(true);
        errorLabel.setSize(new Dimension(480, 20));
		errorLabel.setFont(defaultFont);
        diamLabel.setText("Proj. Diameter (mm)");
        diamLabel.setLocation(new Point(250, 90));
        diamLabel.setVisible(true);
        diamLabel.setSize(new Dimension(110, 20));
		diamLabel.setFont(defaultFont);
        diamField.setText("4.00");
        diamField.setLocation(new Point(250, 110));
        diamField.setVisible(true);
        diamField.setSize(new Dimension(60, 20));
		diamField.setFont(defaultFont);
        projLenLabel.setText("Proj. Length (mm)");
        projLenLabel.setLocation(new Point(380, 90));
        projLenLabel.setVisible(true);
        projLenLabel.setSize(new Dimension(100, 20));
		projLenLabel.setFont(defaultFont);
        projLenField.setText("40.0");
        projLenField.setLocation(new Point(380, 110));
        projLenField.setVisible(true);
        projLenField.setSize(new Dimension(70, 20));
		projLenField.setFont(defaultFont);
        maxVelLabel.setText("Maximum Velocity:");
        maxVelLabel.setForeground(Color.red);
        maxVelLabel.setLocation(new Point(300, 140));
        //maxVelLabel.setAlignment(2);
        maxVelLabel.setVisible(true);
        maxVelLabel.setSize(new Dimension(100, 20));
		maxVelLabel.setFont(defaultFont);
        finalVelLabel.setText("Final Velocity:");
        finalVelLabel.setForeground(Color.red);
        finalVelLabel.setLocation(new Point(320, 160));
        //finalVelLabel.setAlignment(2);
        finalVelLabel.setVisible(true);
        finalVelLabel.setSize(new Dimension(80, 20));
		finalVelLabel.setFont(defaultFont);
        maxVelField.setText("??");
        maxVelField.setForeground(Color.red);
        maxVelField.setLocation(new Point(410, 140));
        maxVelField.setVisible(true);
        maxVelField.setSize(new Dimension(50, 20));
		maxVelField.setFont(defaultFont);
        finalVelField.setText("??");
        finalVelField.setForeground(Color.red);
        finalVelField.setLocation(new Point(410, 160));
        finalVelField.setVisible(true);
        finalVelField.setSize(new Dimension(50, 20));
		finalVelField.setFont(defaultFont);
        peakLabel.setText("Peak Current:");
        peakLabel.setForeground(Color.blue);
        peakLabel.setLocation(new Point(170, 160));
        //peakLabel.setAlignment(2);
        peakLabel.setVisible(true);
        peakLabel.setSize(new Dimension(70, 20));
		peakLabel.setFont(defaultFont);
        peakField.setText("??");
        peakField.setForeground(Color.blue);
        peakField.setLocation(new Point(250, 160));
        peakField.setVisible(true);
        peakField.setSize(new Dimension(50, 20));
		peakField.setFont(defaultFont);
        clampCheckbox.setLocation(new Point(140, 140));
        clampCheckbox.setLabel("Snubber Diode");
        clampCheckbox.setVisible(true);
        clampCheckbox.setSize(new Dimension(120, 20));
		clampCheckbox.addActionListener(this);
		clampCheckbox.setFont(defaultFont);
        setLocation(new Point(0, 0));
        getContentPane().setLayout(null);
        setSize(new Dimension(500, 470));
        getContentPane().add(resLabel);
        getContentPane().add(capLabel);
        getContentPane().add(voltLabel);
        getContentPane().add(resField);
        getContentPane().add(capField);
        getContentPane().add(voltField);
        getContentPane().add(lengthLabel);
        getContentPane().add(diLabel);
        getContentPane().add(doLabel);
		getContentPane().add(clampResLabel);
        getContentPane().add(gaugeLabel);
        getContentPane().add(lengthField);
        getContentPane().add(diField);
        getContentPane().add(doField);
		getContentPane().add(clampResField);
        getContentPane().add(gaugeField);
        getContentPane().add(massLabel);
        getContentPane().add(distLabel);
        getContentPane().add(massField);
        getContentPane().add(distField);
        getContentPane().add(calcButton);
        getContentPane().add(errorLabel);
        getContentPane().add(diamLabel);
        getContentPane().add(diamField);
        getContentPane().add(projLenLabel);
        getContentPane().add(projLenField);
        getContentPane().add(maxVelLabel);
        getContentPane().add(finalVelLabel);
        getContentPane().add(maxVelField);
        getContentPane().add(finalVelField);
        getContentPane().add(peakLabel);
        getContentPane().add(peakField);
		getContentPane().add(clampCheckbox);
    }
	
	public void CalculateRLCMacros(double l)
    {
        double res = intRes + extRes;
        if(res * res < (4D * l) / cap)
        {
            damped = -1;
            u = Math.sqrt(1.0D / (l * cap) - (res * res) / (2D * l * (2D * l)));
            a = volt / (u * l);
            b = -res / (2D * l);
        } else
        if(res * res > (4D * l) / cap)
        {
            damped = 1;
            u = Math.sqrt((res * res) / (2D * l * (2D * l)) - 1.0D / (l * cap));
            a = volt / (u * l);
            b = -res / (2D * l);
        } else
        {
            damped = 0;
            u = 0.0D;
            a = volt / l;
            b = -res / (2D * l);
        }
    }

    public void CalculateCoilDimensions()
    {
        double FEET_PER_METER = 3.2808399200439453D;
        double resPerMeter;
        switch(wireGauge)
        {
        case 10: // '\n'
            wireDiameter = 0.0025883D;
            resPerMeter = 0.0009989000391215086D * FEET_PER_METER;
            break;

        case 11: // '\013'
            wireDiameter = 0.0023037999999999999D;
            resPerMeter = 0.0012600000482052565D * FEET_PER_METER;
            break;

        case 12: // '\f'
            wireDiameter = 0.0020523D;
            resPerMeter = 0.001588999992236495D * FEET_PER_METER;
            break;

        case 13: // '\r'
            wireDiameter = 0.0018288D;
            resPerMeter = 0.0020029998850077391D * FEET_PER_METER;
            break;

        case 14: // '\016'
            wireDiameter = 0.0016280999999999999D;
            resPerMeter = 0.0025239998940378428D * FEET_PER_METER;
            break;

        case 15: // '\017'
            wireDiameter = 0.0014503000000000001D;
            resPerMeter = 0.0031840000301599503D * FEET_PER_METER;
            break;

        case 16: // '\020'
            wireDiameter = 0.0012903000000000001D;
            resPerMeter = 0.0040190001018345356D * FEET_PER_METER;
            break;

        case 17: // '\021'
            wireDiameter = 0.0011506000000000001D;
            resPerMeter = 0.0050639999099075794D * FEET_PER_METER;
            break;

        case 18: // '\022'
            wireDiameter = 0.0010235999999999999D;
            resPerMeter = 0.0063860001973807812D * FEET_PER_METER;
            break;

        case 19: // '\023'
            wireDiameter = 0.00091186000000000001D;
            resPerMeter = 0.008050999604165554D * FEET_PER_METER;
            break;

        case 20: // '\024'
            wireDiameter = 0.00081280000000000002D;
            resPerMeter = 0.010127999819815159D * FEET_PER_METER;
            break;

        case 21: // '\025'
            wireDiameter = 0.00072389999999999998D;
            resPerMeter = 0.0012799999676644802D * FEET_PER_METER;
            break;

        case 22: // '\026'
            wireDiameter = 0.00064262D;
            resPerMeter = 0.01620200090110302D * FEET_PER_METER;
            break;

        case 23: // '\027'
            wireDiameter = 0.00057404000000000005D;
            resPerMeter = 0.0020359999034553766D * FEET_PER_METER;
            break;

        case 24: // '\030'
            wireDiameter = 0.00051053999999999997D;
            resPerMeter = 0.025669999420642853D * FEET_PER_METER;
            break;

        case 25: // '\031'
            wireDiameter = 0.00045466000000000001D;
            resPerMeter = 0.0032369999680668116D * FEET_PER_METER;
            break;

        case 26: // '\032'
            wireDiameter = 0.00040386000000000003D;
            resPerMeter = 0.041023001074790955D * FEET_PER_METER;
            break;

        case 27: // '\033'
            wireDiameter = 0.00036068D;
            resPerMeter = 0.0051469998434185982D * FEET_PER_METER;
            break;

        case 28: // '\034'
            wireDiameter = 0.00032004000000000001D;
            resPerMeter = 0.065324999392032623D * FEET_PER_METER;
            break;

        case 29: // '\035'
            wireDiameter = 0.00028702000000000003D;
            resPerMeter = 0.0081829996779561043D * FEET_PER_METER;
            break;

        case 30: // '\036'
            wireDiameter = 0.00025399999999999999D;
            resPerMeter = 0.010320000350475311D * FEET_PER_METER;
            break;

        default:
            wireDiameter = 1.0D;
            resPerMeter = 1.0D;
            ReportError("Bad wire gauge!");
            break;
        }
        innerRadius = innerDiameter / 2D;
        outerRadius = outerDiameter / 2D;
        turns = Math.round(len / wireDiameter);
        double r2 = outerRadius - innerRadius;
        layers = Math.round(r2 / wireDiameter);
        double r1 = innerRadius + r2 / 2D;
        double wireLength = turns * layers * 2D * r1 * 3.1415926535897931D;
        intRes = wireLength * resPerMeter;
    }

	// deprecated - will use max point of current to determine snub point
    public double CalculateCharge(double l, double t)
    {
        double q = cap * volt;
        double chrg;
        if(zeroChargeIndex != 0 && clampDiode)
            chrg = 0.0D;
        else
            chrg = q * Math.exp(-((intRes + extRes) * t) / (2D * l)) * Math.cos(u * t);
        if(chrg <= 0.0D && zeroChargeIndex == 0)
            zeroChargeIndex = (int)(t / timeStep);
        return chrg;
    }

    public double CalculatePower(double l, double t, int j)
    {
        if(clampDiode && charge[j] <= 0.0D)
        {
            return current[j] * current[j] * (intRes + extRes);
        } else
        {
            double q = cap * volt;
            double C = Math.pow((q / cap) * Math.exp((-(intRes + extRes) * t) / (2D * l)), 2D);
            double power = C * Math.cos(u * t) * (Math.cos(u * t) + u * Math.sin(u * t));
            return power;
        }
    }

    public double CalculateRLC(double l, double t)
    {
        double curr=0;
		double clampTime = zeroChargeIndex*timeStep;
		
		if (zeroChargeIndex!=0)
            curr = current[zeroChargeIndex] * Math.exp(-(t - clampTime) / (l / (intRes+clampRes)));
        else
        if(damped == -1)
            curr = a * Math.exp(b * t) * Math.sin(u * t);
        else
        if(damped == 1)
            curr = a * Math.exp(b * t) * sinh(u * t);
        else
            curr = a * t * Math.exp(b * t);
		
		if(clampDiode && zeroChargeIndex==0 && t>0 && curr<current[(int)((t/timeStep)-1)])
			zeroChargeIndex = (int)(t/timeStep);
		
        return curr;
    }

    public double IntegrateBField(double left, double right)
    {
        double x = left;
        double B = (x + len / 2D) / Math.sqrt((x + len / 2D) * (x + len / 2D) + innerRadius * innerRadius) - (x - len / 2D) / Math.sqrt((x - len / 2D) * (x - len / 2D) + innerRadius * innerRadius);
        double step = (right - left) / 100D;
        for(x = left + step; x <= right - step; x += step)
            B += 2D * ((x + len / 2D) / Math.sqrt((x + len / 2D) * (x + len / 2D) + innerRadius * innerRadius) - (x - len / 2D) / Math.sqrt((x - len / 2D) * (x - len / 2D) + innerRadius * innerRadius));

        x = right;
        B += (x + len / 2D) / Math.sqrt((x + len / 2D) * (x + len / 2D) + innerRadius * innerRadius) - (x - len / 2D) / Math.sqrt((x - len / 2D) * (x - len / 2D) + innerRadius * innerRadius);
        B *= (right - left) / 200D;
        return B;
    }

    public double CalculateInductance(double B)
    {
        double fluxPercent = 1.0D; // unity until I can figure the correct way to determine the flux
        double perm = 1.0D;
        double N = turns * layers;
        double r2 = outerRadius - innerRadius;
        double r1 = innerRadius + r2 / 2D;
        double inductance = ((31.5D * N * N * r1 * r1) / (6D * r1 + 9D * len + 10D * r2)) * (perm * fluxPercent);
        return inductance / 1000000D;
    }
	
	public double muRel(double B)
	{
		// relative permeability comes in here - dependent on B (bad thing, must integrate?)
		return 1;
	}
	
	public void CalculateVelocityWave()
    {
        velocity[0] = 0.0D;
        zeroChargeIndex = 0;
        double inductance = CalculateInductance(0.0D);
        CalculateRLCMacros(inductance);
        charge[0] = CalculateCharge(inductance, 0.0D);
        current[0] = CalculateRLC(inductance, 0.0D);
        for(int j = 1; j < numberOfSteps; j++)
        {
            double i = current[j - 1];
            double p = position[j - 1];
            double ri = innerRadius;
            double n = turns * layers;
            double l = len;
            double pl = projLen;
            double x1 = p - pl / 2D;
            double x2 = p + pl / 2D;
            double B1 = ((mu0 * n * i) / (2D * l)) * ((x1 + len / 2D) / Math.sqrt((x1 + len / 2D) * (x1 + len / 2D) + innerRadius * innerRadius) - (x1 - len / 2D) / Math.sqrt((x1 - len / 2D) * (x1 - len / 2D) + innerRadius * innerRadius));
            double B2 = ((mu0 * n * i) / (2D * l)) * ((x2 + len / 2D) / Math.sqrt((x2 + len / 2D) * (x2 + len / 2D) + innerRadius * innerRadius) - (x2 - len / 2D) / Math.sqrt((x2 - len / 2D) * (x2 - len / 2D) + innerRadius * innerRadius));
            double area = Math.PI * (diameter / 2D) * (diameter / 2D);
            //double c = -9823.0430876317805D;
            //double force = (area / 2*mu0) * (((B2 - B1 - ((2D / c) * Math.exp(c * B2) - (2D / c) * Math.exp(c * B1))) + (1.0D / (2D * c)) * Math.exp(2D * c * B2)) - (1.0D / (2D * c)) * Math.exp(2D * c * B1));
            double force = muRel((B1+B2)/2)*(area/(2*mu0)) * (B2*B2 - B1*B1);
			if(i < 0.0D)
                force *= -1D;
            double accel = force / mass;
            velocity[j] = velocity[j - 1] + accel * timeStep;
            position[j] = position[j - 1] + velocity[j] * timeStep;
            inductance = CalculateInductance(IntegrateBField(x1, x2) / (x2 - x1));
            CalculateRLCMacros(inductance);
            //charge[j] = CalculateCharge(inductance, (double)j * timeStep);
            current[j] = CalculateRLC(inductance, (double)j * timeStep);
        }

        CalculateMaximums();
    }

    public void CalculateMaximums()
    {
        maxVelocity = 0.0D;
        maxPosition = 0.0D;
        maxCurrent = 0.0D;
        for(int i = 0; i < numberOfSteps; i++)
        {
            if(velocity[i] > maxVelocity)
                maxVelocity = velocity[i];
            if(position[i] > maxPosition)
                maxPosition = position[i];
            if(current[i] > maxCurrent)
                maxCurrent = current[i];
        }

        if(maxVelocity > permVelocity)
            permVelocity = maxVelocity;
        if(maxCurrent > permCurrent)
            permCurrent = maxCurrent;
        maxVelField.setText((new Double((double)Math.round(maxVelocity * 10D) / 10D)).toString());
        finalVelField.setText((new Double((double)Math.round(velocity[numberOfSteps - 1] * 10D) / 10D)).toString());
        peakField.setText((new Double((double)Math.round(maxCurrent * 10D) / 10D)).toString());
    }

    static final double sinh(double x)
    {
        return 0.5D * (Math.exp(x) - Math.exp(-x));
    }

    public void ReportError(String s)
    {
        errorLabel.setText(s);
        errorLabel.repaint();
    }
	
	public void InputValues()
    {
        ReportError("");
        extRes = Math.abs(ObtainValue(resField));
        cap = Math.abs(ObtainValue(capField)) * 1E-6D;
        volt = Math.abs(ObtainValue(voltField));
        len = Math.abs(ObtainValue(lengthField)) * 0.001D;
        innerDiameter = Math.abs(ObtainValue(diField)) * 0.001D;
        outerDiameter = Math.abs(ObtainValue(doField)) * 0.001D;
		clampRes = Math.abs(ObtainValue(clampResField));
        wireGauge = (int)Math.abs(ObtainValue(gaugeField));
        mass = Math.abs(ObtainValue(massField)) * 0.001D;
        position[0] = Math.abs(ObtainValue(distField)) * -0.001D;
        diameter = Math.abs(ObtainValue(diamField)) * 0.001D;
        projLen = Math.abs(ObtainValue(projLenField)) * 0.001D;
    }
	
	public void actionPerformed(ActionEvent e)
    {
        if(e.getSource() == calcButton)
        {
            InputValues();
            CalculateCoilDimensions();
            CalculateVelocityWave();
            repaint();
        } else if(e.getSource() == clampCheckbox)
            clampDiode = clampDiode ^ true;
    }
	
	public double ObtainValue(JTextField field)
    {
        String s = field.getText();
        double value;
        try
        {
            value = Double.valueOf(s).doubleValue();
        }
        catch(Exception _ex)
        {
            value = 0.0D;
            ReportError("Bad input value: " + s);
        }
        return value;
    }
	
    public void paint (Graphics g) {
        super.paint(g);
        int timeInterval = (int)((maxTime / 10D) * 1000D);
        int xInterval = xDist / 10;
        for(int x = originX; x <= endX; x += xInterval)
        {
            g.setColor(Color.black);
            g.drawLine(x, originY - 5, x, originY + 5);
            g.setColor(Color.darkGray);
            g.drawString((new Double((double)Math.round(timeInterval * ((x - originX) / xInterval) * 10) / 10D)).toString(), x - 6, originY + 15);
        }

        for(int y = originY; y > endY; y -= yDist / 10)
        {
            g.setColor(Color.black);
            g.drawLine(originX - 5, y, originX + 5, y);
            g.setColor(Color.lightGray);
            g.drawLine(originX + 5, y, endX, y);
            g.setColor(Color.blue);
            g.drawString((new Double((double)Math.round((permCurrent / 10D) * (double)((originY - y) / (yDist / 10)) * 10D) / 10D)).toString(), 1, y - 4);
            g.setColor(Color.red);
            g.drawString((new Double((double)Math.round((permVelocity / 10D) * (double)((originY - y) / (yDist / 10)) * 10D) / 10D)).toString(), 1, y + 6);
        }

        g.setColor(Color.black);
        g.drawLine(originX, originY + 10, originX, endY);
        g.drawLine(originX - 10, originY, endX, originY);
        GraphVelocity(g);
        GraphRLC(g);
    }
	
	public void GraphVelocity(Graphics g)
    {
        g.setColor(Color.red);
        double stepsPerPixel = (double)numberOfSteps / (double)xDist;
        double pixelsPerVelocity = (double)yDist / permVelocity;
        int px = originX;
        int pgy = originY;
        int x = originX;
        for(double s = 0.0D; s < (double)numberOfSteps; s += stepsPerPixel)
        {
            int gy = -(int)(velocity[(int)s] * pixelsPerVelocity) + originY;
            g.drawLine(px, pgy, x, gy);
            px = x;
            pgy = gy;
            x++;
        }

    }

    public void GraphRLC(Graphics g)
    {
        g.setColor(Color.blue);
        double stepsPerPixel = (double)numberOfSteps / (double)xDist;
        double pixelsPerAmp = (double)yDist / permCurrent;
        int px = originX;
        int pgy = originY;
        int x = originX;
        for(double s = 0.0D; s < (double)numberOfSteps; s += stepsPerPixel)
        {
            int gy = -(int)(current[(int)s] * pixelsPerAmp) + originY;
            g.drawLine(px, pgy, x, gy);
            px = x;
            pgy = gy;
            x++;
        }
/*
        g.setColor(Color.green);
        double pixelsPerCoulomb = (double)yDist / (cap * volt);
        px = originX;
        pgy = originY;
        x = originX;
        for(double s = 0.0D; s < (double)numberOfSteps; s += stepsPerPixel)
        {
            int gy = -(int)(charge[(int)s] * pixelsPerCoulomb) + originY;
            g.drawLine(px, pgy, x, gy);
            px = x;
            pgy = gy;
            x++;
        }*/
	}
}
