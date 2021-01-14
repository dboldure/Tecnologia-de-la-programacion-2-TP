package simulator.launcher;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

import javax.swing.SwingUtilities;

/*
 * Examples of command-line parameters:
 * 
 *  -h
 *  -i resources/examples/ex4.4body.txt -s 100
 *  -i resources/examples/ex4.4body.txt -o resources/examples/ex4.4body.out -s 100
 *  -i resources/examples/ex4.4body.txt -o resources/examples/ex4.4body.out -s 100 -gl ftcg
 *  -i resources/examples/ex4.4body.txt -o resources/examples/ex4.4body.out -s 100 -gl nlug
 *
 */

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.json.JSONObject;

import simulator.control.Controller;
import simulator.factories.BasicBodyBuilder;
import simulator.factories.Builder;
import simulator.factories.BuilderBasedFactory;
import simulator.factories.Factory;
import simulator.factories.FallingToCenterGravityBuilder;
import simulator.factories.MassLosingBodyBuilder;
import simulator.factories.NewtonUniversalGravitationBuilder;
import simulator.factories.NoGravityBuilder;
import simulator.model.Body;
import simulator.model.GravityLaws;
import simulator.model.PhysicSimulator;
import simulator.view.ApplicationMainFrame;

public class Main {

	// default values for some parameters
	//
	private final static Double _dtimeDefaultValue = 1.0;
	private final static int _dSteps = 2500;

	// some attributes to stores values corresponding to command-line parameters
	//
	private static Double _dtime = 2500.0;
	private static int _steps = _dSteps;
	private static String _inFile = "file.txt";
	private static JSONObject _gravityLawsInfo = null;
	private static boolean _batchMode = false;
	private static boolean _inFileOption = false;
	private static OutputStream _output;

	// factories
	private static Factory<Body> _bodyFactory;
	private static Factory<GravityLaws> _gravityLawsFactory;

	

	private static void init() {
		// initialize the bodies factory -------------------------------------------------------
		// ...
		//Example:
		ArrayList<Builder<Body>> bodyBuilders = new ArrayList<Builder<Body>>(); 
		bodyBuilders.add(new BasicBodyBuilder()); 
		bodyBuilders.add(new MassLosingBodyBuilder()); 
		_bodyFactory = new BuilderBasedFactory<Body>(bodyBuilders);
		
		// initialize the gravity laws factory --------------------------------------------------------
		// ...
		
		ArrayList<Builder<GravityLaws>> lawBuilder = new ArrayList<>(); 
		lawBuilder.add(new NewtonUniversalGravitationBuilder());
		lawBuilder.add(new NoGravityBuilder());
		lawBuilder.add(new FallingToCenterGravityBuilder());
		_gravityLawsFactory = new BuilderBasedFactory<GravityLaws>(lawBuilder);
		

				
	}

	private static void parseArgs(String[] args) {

		// define the valid command line options
		//
		Options cmdLineOptions = buildOptions();

		// parse the command line as provided in args
		//
		CommandLineParser parser = new DefaultParser();
		try {
			CommandLine line = parser.parse(cmdLineOptions, args);
			parseViewMode(line);
			parseHelpOption(line, cmdLineOptions);
			
			parseDeltaTimeOption(line);
			parseGravityLawsOption(line);
			parseInFileOption(line);
			
			if(_batchMode)
			{
				parseOutFileOption(line);
				parseStepsOption(line);
			}
				
			
			
			// if there are some remaining arguments, then something wrong is
			// provided in the command line!
			//
			String[] remaining = line.getArgs();
			if (remaining.length > 0) {
				String error = "Illegal arguments:";
				for (String o : remaining)
					error += (" " + o);
				throw new ParseException(error);
			}

		} catch (ParseException e) {
			System.err.println(e.getLocalizedMessage());
			System.exit(1);
		}

	}

	private static Options buildOptions() {
		Options cmdLineOptions = new Options();

		// help
		cmdLineOptions.addOption(Option.builder("h").longOpt("help").desc("Print this message.").build());

		// input file
		cmdLineOptions.addOption(Option.builder("i").longOpt("input").hasArg().desc("Bodies JSON input file.").build());

		// delta-time
		cmdLineOptions.addOption(Option.builder("dt").longOpt("delta-time").hasArg()
				.desc("A double representing actual time, in seconds, per simulation step. Default value: "
						+ _dtimeDefaultValue + ".")
				.build());
		
		// output file
		cmdLineOptions.addOption(Option.builder("o").longOpt("output").hasArg().desc("Simulator JSON output file.").build());
		
		// number of steps
		cmdLineOptions.addOption(Option.builder("s").longOpt("steps").hasArg().desc("Number of steps to cycle simulation.").build());
		
		//view model
		cmdLineOptions.addOption(Option.builder("m").longOpt("view mode").hasArg().desc("representation mode of the simulator").build());

		// gravity laws -- there is a workaround to make it work even when
		// _gravityLawsFactory is null. 
		
		String gravityLawsValues = "N/A";
		String defaultGravityLawsValue = "N/A";
		if (_gravityLawsFactory != null) {
			gravityLawsValues = "";
			for (JSONObject fe : _gravityLawsFactory.getInfo()) {
				if (gravityLawsValues.length() > 0) {
					gravityLawsValues = gravityLawsValues + ", ";
				}
				gravityLawsValues = gravityLawsValues + "'" + fe.getString("type") + "' (" + fe.getString("desc") + ")";
			}
			defaultGravityLawsValue = _gravityLawsFactory.getInfo().get(0).getString("type");
		}
		cmdLineOptions.addOption(Option.builder("gl").longOpt("gravity-laws").hasArg()
				.desc("Gravity laws to be used in the simulator. Possible values: " + gravityLawsValues
						+ ". Default value: '" + defaultGravityLawsValue + "'.")
				.build());

		return cmdLineOptions;
	}

	private static void parseViewMode(CommandLine line) throws ParseException {
		String s = line.getOptionValue("m");
		try {
			if(s.equalsIgnoreCase("batch"))
				_batchMode = true;
			else if(s.equalsIgnoreCase("gui"))
				_batchMode = false;
			else
				throw new IllegalArgumentException();
		} catch (Exception e) {
			throw new ParseException("Invalid parameter " + s);
		}
	}
	
	private static void parseHelpOption(CommandLine line, Options cmdLineOptions) {
		if (line.hasOption("h")) {
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp(Main.class.getCanonicalName(), cmdLineOptions, true);
			System.exit(0);
		}
	}
	
	private static void parseStepsOption(CommandLine line) throws ParseException {
		String s = line.getOptionValue("s", String.valueOf(_dSteps));
		try {
			_steps = Integer.parseInt(s);
			assert (_steps > 0);
		} catch (Exception e) {
			throw new ParseException("Invalid delta-time value: " + s);
		}
	}

	private static void parseInFileOption(CommandLine line) throws ParseException {
		_inFile = line.getOptionValue("i");
		if(_inFile == null)
			if(_batchMode)
				throw new ParseException("An input file of bodies is required");
			else
				_inFileOption = false;
		else
			_inFileOption = true;
	}
	
	private static void parseOutFileOption(CommandLine line) throws ParseException{
		try
		{
			String out = line.getOptionValue("o");
			if (out == null) {
				_output = System.out;
			} else
				_output = new FileOutputStream(new File(out));
		}
		catch(FileNotFoundException e)
		{
			throw new ParseException(e.getMessage());
		}
	}

	private static void parseDeltaTimeOption(CommandLine line) throws ParseException {
		String dt = line.getOptionValue("dt", _dtimeDefaultValue.toString());
		try {
			_dtime = Double.parseDouble(dt);
			assert (_dtime > 0);
		} catch (Exception e) {
			throw new ParseException("Invalid delta-time value: " + dt);
		}
	}

	private static void parseGravityLawsOption(CommandLine line) throws ParseException {

		// this line is just a work around to make it work even when _gravityLawsFactory
		// is null, you can remove it when've defined _gravityLawsFactory
		if (_gravityLawsFactory == null)
			return;

		String gl = line.getOptionValue("gl");
		if (gl != null) {
			for (JSONObject fe : _gravityLawsFactory.getInfo()) {
				if (gl.equals(fe.getString("type"))) {
					_gravityLawsInfo = fe;
					//break;
				}
			}
			if (_gravityLawsInfo == null) {
				throw new ParseException("Invalid gravity laws: " + gl);
			}
		} else {
			_gravityLawsInfo = _gravityLawsFactory.getInfo().get(0);
		}
	}

	private static void startBatchMode() throws Exception {
		// create and connect components, then start the simulator
		
		PhysicSimulator sim = new PhysicSimulator(_dtime, _gravityLawsFactory.createInstance(_gravityLawsInfo));
		Controller c = new Controller(sim,  _bodyFactory,_gravityLawsFactory);
		c.loadBodies(new FileInputStream(new File(_inFile)));
		c.run(_steps, _output);
	}
	
	private static void startGUIMode() throws InvocationTargetException, InterruptedException, FileNotFoundException, simulator.control.IllegalArgumentException
	{
		//model + controller -----------------
			PhysicSimulator sim = new PhysicSimulator(_dtime, _gravityLawsFactory.createInstance(_gravityLawsInfo));
			Controller c = new Controller(sim,  _bodyFactory, _gravityLawsFactory);
			if(_inFileOption)
				c.loadBodies(new FileInputStream(new File(_inFile)));
		
		
		//views
		
		SwingUtilities.invokeAndWait(new Runnable() { @Override public void run() { new ApplicationMainFrame(c); } });
	}

	private static void start(String[] args) throws Exception {
		parseArgs(args);
		if(_batchMode)
			startBatchMode();
		else
			startGUIMode();
	}

	public static void main(String[] args) {
		try {
			init();
			start(args);
		} catch (Exception e) {
			System.err.println("Something went wrong ...");
			System.err.println();
			e.printStackTrace();
		}
	}
}
