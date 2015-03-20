/* RdesktopSwing.java
 * Component: ProperJavaRDP
 * 
 * Revision: $Revision: 1.0 $
 * Author: $Author: pengzhou $
 * Date: $Date: 2009/03/20 $
 *
 * Copyright (c) 2010 pengzhou
 *
 * Purpose: Main class, launches session
 */
package com.lixia.rdp;

import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.*;
import java.util.ArrayList;
import java.util.Timer;
import java.awt.*;

import com.lixia.rdp.Options;
import com.lixia.rdp.RdesktopJPanel;
import com.lixia.rdp.keymapping.KeyCode_FileBased;
import com.lixia.rdp.rdp5.Rdp5JPanel;
import com.lixia.rdp.rdp5.VChannel;
import com.lixia.rdp.rdp5.VChannels;
import com.lixia.rdp.rdp5.cliprdr.ClipChannel;
import com.lixia.rdp.rdp5.keys.KeysChannel;
import com.lixia.rdp.rdp5.rdpdr.Printer;
import com.lixia.rdp.rdp5.rdpdr.Rdpdr;
import com.lixia.rdp.tools.SendEventJPanel;

import org.apache.log4j.*;
import gnu.getopt.*;

public class RdesktopSwing {
    
    /**
     * Translate a disconnect code into a textual description of the reason for the disconnect
     * @param reason Integer disconnect code received from server
     * @return Text description of the reason for disconnection
     */
    static String textDisconnectReason(int reason)
    {
        String text;

        switch (reason)
        {
            case exDiscReasonNoInfo:
                text = "No information available";
                break;

            case exDiscReasonAPIInitiatedDisconnect:
                text = "Server initiated disconnect";
                break;

            case exDiscReasonAPIInitiatedLogoff:
                text = "Server initiated logoff";
                break;

            case exDiscReasonServerIdleTimeout:
                text = "Server idle timeout reached";
                break;

            case exDiscReasonServerLogonTimeout:
                text = "Server logon timeout reached";
                break;

            case exDiscReasonReplacedByOtherConnection:
                text = "Another user connected to the session";
                break;

            case exDiscReasonOutOfMemory:
                text = "The server is out of memory";
                break;

            case exDiscReasonServerDeniedConnection:
                text = "The server denied the connection";
                break;

            case exDiscReasonServerDeniedConnectionFips:
                text = "The server denied the connection for security reason";
                break;

            case exDiscReasonLicenseInternal:
                text = "Internal licensing error";
                break;

            case exDiscReasonLicenseNoLicenseServer:
                text = "No license server available";
                break;

            case exDiscReasonLicenseNoLicense:
                text = "No valid license available";
                break;

            case exDiscReasonLicenseErrClientMsg:
                text = "Invalid licensing message";
                break;

            case exDiscReasonLicenseHwidDoesntMatchLicense:
                text = "Hardware id doesn't match software license";
                break;

            case exDiscReasonLicenseErrClientLicense:
                text = "Client license error";
                break;

            case exDiscReasonLicenseCantFinishProtocol:
                text = "Network error during licensing protocol";
                break;

            case exDiscReasonLicenseClientEndedProtocol:
                text = "Licensing protocol was not completed";
                break;

            case exDiscReasonLicenseErrClientEncryption:
                text = "Incorrect client license enryption";
                break;

            case exDiscReasonLicenseCantUpgradeLicense:
                text = "Can't upgrade license";
                break;

            case exDiscReasonLicenseNoRemoteConnections:
                text = "The server is not licensed to accept remote connections";
                break;

            default:
                if (reason > 0x1000 && reason < 0x7fff)
                {
                    text = "Internal protocol error";
                }
                else
                {
                    text = "Unknown reason";
                }
        }
        return text;
    }

    /* RDP5 disconnect PDU */
    public static final int exDiscReasonNoInfo = 0x0000;
    public static final int exDiscReasonAPIInitiatedDisconnect = 0x0001;
    public static final int exDiscReasonAPIInitiatedLogoff = 0x0002;
    public static final int exDiscReasonServerIdleTimeout = 0x0003;
    public static final int exDiscReasonServerLogonTimeout = 0x0004;
    public static final int exDiscReasonReplacedByOtherConnection = 0x0005;
    public static final int exDiscReasonOutOfMemory = 0x0006;
    public static final int exDiscReasonServerDeniedConnection = 0x0007;
    public static final int exDiscReasonServerDeniedConnectionFips = 0x0008;
    public static final int exDiscReasonLicenseInternal = 0x0100;
    public static final int exDiscReasonLicenseNoLicenseServer = 0x0101;
    public static final int exDiscReasonLicenseNoLicense = 0x0102;
    public static final int exDiscReasonLicenseErrClientMsg = 0x0103;
    public static final int exDiscReasonLicenseHwidDoesntMatchLicense = 0x0104;
    public static final int exDiscReasonLicenseErrClientLicense = 0x0105;
    public static final int exDiscReasonLicenseCantFinishProtocol = 0x0106;
    public static final int exDiscReasonLicenseClientEndedProtocol = 0x0107;
    public static final int exDiscReasonLicenseErrClientEncryption = 0x0108;
    public static final int exDiscReasonLicenseCantUpgradeLicense = 0x0109;
    public static final int exDiscReasonLicenseNoRemoteConnections = 0x010a;
    
    static Logger logger = Logger.getLogger("com.lixia.rdp");

	static boolean keep_running;

	static boolean loggedon;

	static boolean readytosend;

	static boolean application=false;
	
	static boolean showTools;

	static final String keyMapPath = "keymaps/";

	static String mapFile = "en-gb";

	static String keyMapLocation = "";

	static SendEventJPanel toolFrame = null;
	
    public static KeysChannel keyChannel=null; 
    public static RdesktopJPanel g_canvas;
    public static KeyCode_FileBased keyMap = null;
    public static VChannel seamlessChannel = null;
    public static Method seamlessSetcursor = null;
    public static Method seamlessRepaint = null;
    public static ClipChannel clipChannel = new ClipChannel();
    
	/**
	 * Outputs version and usage information via System.err
	 * 
	 */
	public static void usage() {
        System.err.println("This project is based on code from the Elusiva Everywhere project, which was based on rdesktop, and properJavaRDP.");
		System.err.println("For some history see... https://web.archive.org/web/20111120182133/http://www.elusiva.com/opensource");
        System.err.println("Version: lixia-javardp " + Version.version);
		System.err.println("Usage: java -jar lixia-javardp.jar [options]");
		System.err.println("	-b ......................... bandwidth saving (good for 56k modem, but higher latency");
		System.err.println("	-c DIR ..................... working directory");
		System.err.println("	-d DOMAIN .................. logon domain");
		System.err.println("	-D is dubug model");
		System.err.println("	-f[s] ...................... full-screen mode [s to enable seamless mode]");
		System.err.println("	-g WxH ..................... desktop geometry");
		System.err.println("	-m MAPFILE ................. keyboard mapping file for terminal server");
		System.err.println("	-l d,i,w,e,f ............. logging level {debug, info, warn, error, fatal}");
		System.err.println("	-n HOSTNAME ................ client hostname");
		System.err.println("	-p PASSWORD ................ password");
		System.err.println("	-s SHELL ................... shell");
		System.err.println("	-t NUM ..................... RDP port (default 3389)");
		System.err.println("	-T TITLE ................... -T 'Production WebServer''");
		System.err.println("	-u USERNAME ................ user name");
		System.err.println("	-o BPP ..................... bits-per-pixel for display");
        System.err.println("    -e path .................... path to load licence from (requests and saves licence from server if not found)");
        System.err.println("	-r device .................. enable specified device redirection (this flag can be repeated)");
        System.err.println("    --save_licence ............. request and save licence from server");
        System.err.println("    --load_licence ............. load licence from file");
        System.err.println("    --console .................. connect to console");
		System.err.println("	--debug_key ................ show scancodes sent for each keypress etc");
		System.err.println("	--debug_hex ................ show bytes sent and received");
		System.err.println("	--no_remap_hash ............ disable hash remapping");
		System.err.println("	--quiet_alt ................ enable quiet alt fix");
		System.err.println("	--no_encryption ............ disable encryption from client to server");
		System.err.println("	--use_rdp4 ................. use RDP version 4");
        System.err.println("    --enable_menu .............. enable menu bar");
        System.err.println("    --overHttp ................. http proxy server address and port(example--192.168.100.100:80)");
        System.err.println("	--log4j_config=FILE ........ use FILE for log4j configuration");
        System.err.println("	--bulk_compression ......... enable bulk compression");
        System.err.println("Example: java -jar lixia-javardp.jar 192.168.1.50 -g 1152x648 -u admin");

		RdesktopSwing.exit(0, null, (RdesktopJFrame)null, true);
	}

	/**
	 * 
	 * @param args
	 * @throws OrderException
	 * @throws RdesktopException
	 */
	public static void main(String[] args) throws OrderException,
			RdesktopException {
        
        // Ensure that static variables are properly initialised
        keep_running = true;
        loggedon = false;
        readytosend = false;
        showTools = false;
        mapFile = "en-gb";
        keyMapLocation = "";
        toolFrame = null;

		// Attempt to run a native RDP Client

		RDPClientChooser Chooser = new RDPClientChooser();

		if (Chooser.RunNativeRDPClient(args)) {
            if(!Common.underApplet) System.exit(0);
		}

		// Failed to run native client, drop back to Java client instead.

		// parse arguments
		boolean fKdeHack = false;
		int c;
		String arg;
		StringBuffer sb = new StringBuffer();
		LongOpt[] alo = new LongOpt[17];
		alo[0] = new LongOpt("debug_key", LongOpt.NO_ARGUMENT, null, 0);
		alo[1] = new LongOpt("debug_hex", LongOpt.NO_ARGUMENT, null, 0);
		alo[2] = new LongOpt("no_paste_hack", LongOpt.NO_ARGUMENT, null, 0);
		alo[3] = new LongOpt("log4j_config", LongOpt.REQUIRED_ARGUMENT, sb, 0);
		alo[4] = new LongOpt("packet_tools", LongOpt.NO_ARGUMENT, null, 0);
		alo[5] = new LongOpt("quiet_alt", LongOpt.NO_ARGUMENT, sb, 0);
		alo[6] = new LongOpt("no_remap_hash", LongOpt.NO_ARGUMENT, null, 0);
		alo[7] = new LongOpt("no_encryption", LongOpt.NO_ARGUMENT, null, 0);
		alo[8] = new LongOpt("use_rdp4", LongOpt.NO_ARGUMENT, null, 0);
		alo[9] = new LongOpt("use_ssl", LongOpt.NO_ARGUMENT, null, 0);
        alo[10] = new LongOpt("enable_menu", LongOpt.NO_ARGUMENT, null, 0);
        alo[11] = new LongOpt("console", LongOpt.NO_ARGUMENT, null, 0);
        alo[12] = new LongOpt("load_licence", LongOpt.NO_ARGUMENT, null, 0);
        alo[13] = new LongOpt("save_licence", LongOpt.NO_ARGUMENT, null, 0);
        alo[14] = new LongOpt("persistent_caching", LongOpt.NO_ARGUMENT, null, 0);
        alo[15] = new LongOpt("overHttp", LongOpt.REQUIRED_ARGUMENT, null, 0);
        alo[16] = new LongOpt("bulk_compression", LongOpt.NO_ARGUMENT, null, 0);
        
		String progname = "lixia-javardp";

		Getopt g = new Getopt("properJavaRDP", args,
				"b:c:d:f:g:k:l:m:n:p:s:t:T:u:o:r:", alo);

//		ClipChannel clipChannel = new ClipChannel();

		while ((c = g.getopt()) != -1) {
			switch (c) {

			case 0:
				switch (g.getLongind()) {
				case 0:
					Options.debug_keyboard = true;
					break;
				case 1:
					Options.debug_hexdump = true;
					break;
				case 2:
					break;
				case 3:
					arg = g.getOptarg();
					PropertyConfigurator.configure(arg);
					break;
				case 4:
					showTools = true;
					break;
				case 5:
					Options.altkey_quiet = true;
					break;
				case 6:
					Options.remap_hash = false;
					break;
				case 7:
					Options.packet_encryption = false;
					break;
				case 8:
					Options.use_rdp5 = false;
					//Options.server_bpp = 8;
					Options.set_bpp(8);
					break;
				case 9:
					Options.use_ssl = true;
					break;
                case 10:
                    Options.enable_menu = true;
                    break;
                case 11:
                    Options.console_session = true;
                    break;
                case 12:
                    Options.load_licence = true;
                    break;
                case 13:
                    Options.save_licence = true;
                    break;
                case 14:
                    Options.persistent_bitmap_caching = true;
                    break;
                case 15:
                	Options.http_mode = true;
                	arg = g.getOptarg();
                	Options.http_server = arg;
					logger.info("remote http proxy server " + arg);
                	break;
                case 16:
                	Options.bulk_compression = true;
                	break;
				default:
					usage();
				}
				break;

			case 'o':
				Options.set_bpp(Integer.parseInt(g.getOptarg()));
				break;
			case 'b':
				Options.low_latency = false;
				break;
			case 'm':
				mapFile = g.getOptarg();
				break;
			case 'c':
				Options.directory = g.getOptarg();
				break;
			case 'd':
				Options.domain = g.getOptarg();
				break;
			case 'f':
				Dimension screen_size = Toolkit.getDefaultToolkit()
						.getScreenSize();
				Frame frame = new Frame();
				Insets   screenInsets=Toolkit.getDefaultToolkit().getScreenInsets(frame.getGraphicsConfiguration());
				// ensure width a multiple of 4
				Options.width = screen_size.width & ~3;
				
				if(application){					
					Options.height = screen_size.height - screenInsets.bottom;
//					Options.width = screen_size.width;
				}else
					Options.height = screen_size.height;
				Options.fullscreen = true;
				arg = g.getOptarg();
				if (arg != null) {
					if(arg.charAt(0) == 's')
						Options.seamless_active = true;
					else {
						System.err.println(progname
								+ ": Invalid fullscreen option '" + arg + "'");
						usage();
					}
				}
				break;
			case 'g':
				arg = g.getOptarg();
				int cut = arg.indexOf("x", 0);
				if (cut == -1) {
					System.err.println(progname + ": Invalid geometry: " + arg);
					usage();
				}
				Options.width = Integer.parseInt(arg.substring(0, cut)) & ~3;
				Options.height = Integer.parseInt(arg.substring(cut + 1));
				break;
			case 'l':
				arg = g.getOptarg();
				switch (arg.charAt(0)) {
				case 'd':
					logger.setLevel(Level.DEBUG);
					break;
				case 'i':
					logger.setLevel(Level.INFO);
					break;
				case 'w':
					logger.setLevel(Level.WARN);
					break;
				case 'e':
					logger.setLevel(Level.ERROR);
					break;
				case 'f':
					logger.setLevel(Level.FATAL);
					break;
				default:
					System.err.println(progname + ": Invalid debug level: "
							+ arg.charAt(0));
					usage();
				}
				break;
			case 'n':
				Options.hostname = g.getOptarg();
				break;
			case 'p':
				Options.password = g.getOptarg();
				Options.autologin = true;
				break;
			case 's':
				Options.command = g.getOptarg();
				application=true;
				break;
			case 'u':
				String uname=g.getOptarg();
				Options.username =uname;
				break;
			case 't':
				arg = g.getOptarg();
				try {
					Options.port = Integer.parseInt(arg);
				} catch (NumberFormatException nex) {
					System.err.println(progname + ": Invalid port number: "
							+ arg);
					usage();
				}
				break;
			case 'T':
				Options.windowTitle = g.getOptarg().replace('_', ' ');
				break;
            case 'e':
                Options.licence_path = g.getOptarg();
                break;
            case 'D':
				Options.is_debug = true; 
				break;
            case 'k':
            	break;
			case '?':
			default:
				usage();
				break;

			}
		}

		if (fKdeHack) {
//			Options.height -= 46;
		}

		String server = null;

		if (g.getOptind() < args.length) {
			int colonat = args[args.length - 1].indexOf(":", 0);
			if (colonat == -1) {
				server = args[args.length - 1];
			} else {
				server = args[args.length - 1].substring(0, colonat);
				Options.port = Integer.parseInt(args[args.length - 1]
						.substring(colonat + 1));
			}
		} else {
			System.err.println(progname + ": A server name is required!");
			usage();
		}
        
        VChannels channels = new VChannels();
        
		// Initialise all RDP5 channels
		if (Options.use_rdp5) {
			if (Options.map_clipboard)
				channels.register(clipChannel);

			//init keys channels
			if(Options.keys_register){
				keyChannel=new KeysChannel();
				channels.register(keyChannel);
			}
			
			Rdpdr rdpdrChannel = new Rdpdr();
			channels.register(rdpdrChannel);
			
			Printer printerDevice = new Printer();
			rdpdrChannel.deviceRegister(printerDevice);
		}
		
		if (Options.seamless_active){
			try {
				@SuppressWarnings("rawtypes")
				Class seamlessClass = Class.forName("com.lixia.rdp.seamless.SeamlessChannel");
				@SuppressWarnings("unchecked")
				Method getInstance = seamlessClass.getDeclaredMethod("getInstance", null);
				seamlessChannel = (VChannel)getInstance.invoke(null, null);
				channels.register(seamlessChannel);
				Class params[] = {Class.forName("java.awt.Cursor")};
				seamlessSetcursor = seamlessClass.getDeclaredMethod("setSubCursor", params);
				seamlessRepaint = seamlessClass.getDeclaredMethod("repaintAll", null);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
				
		logger.info("lixia-javardp version is: " + Version.version);

		if (args.length == 0)
			usage();

		String java = System.getProperty("java.specification.version");
		logger.info("Java version is: " + java);

		String os = System.getProperty("os.name");
		String osvers = System.getProperty("os.version");

		if (os.equals("Windows 2000") || os.equals("Windows XP"))
			Options.built_in_licence = true;

		logger.info("Operating System is " + os + " version " + osvers);

		if (os.startsWith("Linux"))
			Constants.OS = Constants.LINUX;
		else if (os.startsWith("Windows"))
			Constants.OS = Constants.WINDOWS;
		else if (os.startsWith("Mac"))
			Constants.OS = Constants.MAC;

		if (Constants.OS == Constants.MAC)
			Options.caps_sends_up_and_down = false;

		Rdp5JPanel RdpLayer = null;
		Common.rdp = RdpLayer;
		//RdesktopFrame window = new RdesktopFrame_Localised();
		//window.setClip(clipChannel);
		RdesktopJFrame window = new RdesktopJFrame_Localised();
		if (Options.windowTitle == "") {
            Options.windowTitle = "lixia-javardp:" + " " + server;
            window.setTitle(Options.windowTitle);
        }
        else {
            window.setTitle(Options.windowTitle);
        }

		
		RdesktopJPanel canvas = (RdesktopJPanel)window.getContentPane();
		canvas.addFocusListener(clipChannel);
		g_canvas = canvas;
		// Configure a keyboard layout
//		KeyCode_FileBased keyMap = null;
		try {
			// logger.info("looking for: " + "/" + keyMapPath + mapFile);
			InputStream istr = RdesktopSwing.class.getResourceAsStream("/" + keyMapPath + mapFile);
			// logger.info("istr = " + istr);
			if (istr == null) {
                logger.debug("Loading keymap from filename");
				keyMap = new KeyCode_FileBased_Localised(keyMapPath + mapFile);
			} else {
                logger.debug("Loading keymap from InputStream");
				keyMap = new KeyCode_FileBased_Localised(istr);
			}
            if(istr != null) istr.close();
			Options.keylayout = keyMap.getMapCode();
		} catch (Exception kmEx) {
			String[] msg = { (kmEx.getClass() + ": " + kmEx.getMessage()) };
			window.showErrorDialog(msg);
            kmEx.printStackTrace();
			RdesktopSwing.exit(0, null, (RdesktopJFrame)null, true);
		}

        logger.debug("Registering keyboard...");
		if (keyMap != null)
			canvas.registerKeyboard(keyMap);

        boolean[] deactivated = new boolean[1];
        int[] ext_disc_reason = new int[1];
        
        logger.debug("keep_running = " + keep_running);        
		while (keep_running) {
            logger.debug("Initialising RDP layer...");
			RdpLayer = new Rdp5JPanel(channels);
			Common.rdp = RdpLayer;
            logger.debug("Registering drawing surface...");
            RdpLayer.registerDrawingSurface(window);
            
            logger.debug("Registering comms layer...");
            window.registerCommLayer(RdpLayer);
            
			loggedon = false;
			readytosend = false;
			logger.info("Connecting to " + server + ":" + Options.port + " ...");

            if(server.equalsIgnoreCase("localhost")) server = "127.0.0.1";

			if (RdpLayer != null) {
				// Attempt to connect to server on port Options.port
				try {
					RdpLayer.connect(Options.username, InetAddress.getByName(server), Options.domain, Options.password, Options.command, Options.directory);
				// Remove to get rid of sendEvent tool
				if (showTools) {
					toolFrame = new SendEventJPanel(RdpLayer);
					toolFrame.setVisible(true);
				}
				// End

				if (keep_running) {

					/*
					 * By setting encryption to False here, we have an encrypted
					 * login packet but unencrypted transfer of other packets
					 */
					if (!Options.packet_encryption)
						Options.encryption = false;

					logger.info("Connection successful");
					// now show window after licence negotiation
						RdpLayer.mainLoop(deactivated, ext_disc_reason);
                                           
                        if (deactivated[0])
                        {
                            /* clean disconnect */
                            RdesktopSwing.exit(0, RdpLayer, window, true);
                            // return 0;
                        }
                        else
                        {
                            if (ext_disc_reason[0] == exDiscReasonAPIInitiatedDisconnect
                                || ext_disc_reason[0] == exDiscReasonAPIInitiatedLogoff)
                            {
                                /* not so clean disconnect, but nothing to worry about */
                                RdesktopSwing.exit(0, RdpLayer, window, true);
                                //return 0;
                            }
                            
                            if(ext_disc_reason[0] >= 2){
                                String reason = textDisconnectReason(ext_disc_reason[0]);
                                String msg[] = { "Connection terminated", reason};
                                window.showErrorDialog(msg);
                                logger.warn("Connection terminated: " + reason);
                                RdesktopSwing.exit(0, RdpLayer, window, true);
                            }
                            
                        }
                        
						keep_running = false; // exited main loop
						if (!readytosend) {
							// maybe the licence server was having a comms
							// problem, retry?
							String msg1 = "The terminal server disconnected before licence negotiation completed.";
							String msg2 = "Possible cause: terminal server could not issue a licence.";
							String[] msg = { msg1, msg2 };
							logger.warn(msg1);
							logger.warn(msg2);
							window.showErrorDialog(msg);
						}
				} // closing bracket to if(running)

				// Remove to get rid of tool window
				if (showTools)
					toolFrame.dispose();
				// End
                
                }catch(ConnectionException e){
                    String msg[] = { "Server can not be reached!","Please try again later.", e.getMessage() };
                    window.showErrorDialog(msg);
                    RdesktopSwing.exit(0, RdpLayer, window, true);
                } catch (UnknownHostException e) {
                    error(e,RdpLayer,window,true);
                }catch(SocketException s){
                    if(RdpLayer.isConnected()){
                        logger.fatal(s.getClass().getName() + " " + s.getMessage());
                        //s.printStackTrace();
                        error(s, RdpLayer, window, true);
                        RdesktopSwing.exit(0, RdpLayer, window, true);
                    }
                }catch (RdesktopException e) {
                    String msg1 = e.getClass().getName();
                    String msg2 = e.getMessage();
                    logger.fatal(msg1 + ": " + msg2);

                    e.printStackTrace(System.err);

                    if (!readytosend) {
                        // maybe the licence server was having a comms
                        // problem, retry?
                        String msg[] = {
                                "The terminal server reset connection before licence negotiation completed.",
                                "Possible cause: terminal server could not connect to licence server.",
                                "Retry?" };
                        boolean retry = window.showYesNoErrorDialog(msg);
                        if (!retry) {
                            logger.info("Selected not to retry.");
                            RdesktopSwing.exit(0, RdpLayer, window, true);
                        } else {
                            if (RdpLayer != null && RdpLayer.isConnected()) {
                                logger.info("Disconnecting ...");
                                RdpLayer.disconnect();
                                logger.info("Disconnected");
                            }
                            logger.info("Retrying connection...");
                            keep_running = true; // retry
                            continue;
                        }
                    } else {
                        String msg[] = { e.getMessage() };
                        window.showErrorDialog(msg);
                        RdesktopSwing.exit(0, RdpLayer, window, true);
                    }
                }catch (Exception e) {
                    logger.warn(e.getClass().getName() + " " + e.getMessage());
                    e.printStackTrace();
                    error(e, RdpLayer, window, true);
                }
			} 
		}
		RdesktopSwing.exit(0, RdpLayer, window, true);
	}

	/**
	 * Disconnects from the server connected to through rdp and destroys the
	 * RdesktopFrame window.
	 * <p>
	 * Exits the application iff sysexit == true, providing return value n to
	 * the operating system.
	 * 
	 * @param n
	 * @param rdp
	 * @param window
	 * @param sysexit
	 */
	public static void exit(int n, RdpJPanel rdp, RdesktopJFrame window, boolean sysexit) {
		keep_running = false;

		// Remove to get rid of tool window
		if ((showTools) && (toolFrame != null))
			toolFrame.dispose();
		// End

		if (rdp != null && rdp.isConnected()) {
			logger.info("Disconnecting ...");
			rdp.disconnect();
			logger.info("Disconnected");
		}
		if (window != null) {
			window.setVisible(false);
			window.dispose();
		}
		
        System.gc();
        
		if (sysexit && Constants.SystemExit){
            if(!Common.underApplet) System.exit(n);
        }
	}


	/**
	 * Displays an error dialog via the RdesktopFrame window containing the
	 * customised message emsg, and reports this through the logging system.
	 * <p>
	 * The application then exits iff sysexit == true
	 * 
	 * @param emsg
	 * @param RdpLayer
	 * @param window
	 * @param sysexit
	 */
	public static void customError(String emsg, RdpJPanel RdpLayer,
			RdesktopJFrame window, boolean sysexit) {
		logger.fatal(emsg);
		String[] msg = { emsg };
		window.showErrorDialog(msg);
		RdesktopSwing.exit(0, RdpLayer, window, true);
	}

	/**
	 * Displays details of the Exception e in an error dialog via the
	 * RdesktopFrame window and reports this through the logger, then prints a
	 * stack trace.
	 * <p>
	 * The application then exits iff sysexit == true
	 * 
	 * @param e
	 * @param RdpLayer
	 * @param window
	 * @param sysexit
	 */
	public static void error(Exception e, RdpJPanel RdpLayer, RdesktopJFrame window, boolean sysexit) {
		try {

			String msg1 = e.getClass().getName();
			String msg2 = e.getMessage();

			logger.fatal(msg1 + ": " + msg2);

			String[] msg = { msg1, msg2 };
			window.showErrorDialog(msg);

			//e.printStackTrace(System.err);
		} catch (Exception ex) {
            logger.warn("Exception in Rdesktop.error: " + ex.getClass().getName() + ": " + ex.getMessage() );
		}

		RdesktopSwing.exit(0, RdpLayer, window, sysexit);
	}

}
