/*
 * Copyright (c) 2010 Brigham Young University
 * 
 * This file is part of the BYU RapidSmith Tools.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License..
 * 
 */
package edu.byu.ece.rapidSmith.design.explorer;

import java.io.File;
import java.util.ArrayList;

import com.trolltech.qt.core.Qt.WindowModality;
import com.trolltech.qt.gui.QAction;
import com.trolltech.qt.gui.QApplication;
import com.trolltech.qt.gui.QFileDialog;
import com.trolltech.qt.gui.QIcon;
import com.trolltech.qt.gui.QKeySequence;
import com.trolltech.qt.gui.QLabel;
import com.trolltech.qt.gui.QMainWindow;
import com.trolltech.qt.gui.QMenu;
import com.trolltech.qt.gui.QMessageBox;
import com.trolltech.qt.gui.QProgressDialog;
import com.trolltech.qt.gui.QStatusBar;
import com.trolltech.qt.gui.QTabWidget;
import com.trolltech.qt.gui.QToolBar;
import com.trolltech.qt.gui.QWidget;
import com.trolltech.qt.gui.QKeySequence.StandardKey;

import edu.byu.ece.rapidSmith.design.Design;
import edu.byu.ece.rapidSmith.design.explorer.FilterWindow.FilterType;
import edu.byu.ece.rapidSmith.device.Device;
import edu.byu.ece.rapidSmith.device.WireEnumerator;
import edu.byu.ece.rapidSmith.gui.FileFilters;
import edu.byu.ece.rapidSmith.timing.PathDelay;
import edu.byu.ece.rapidSmith.timing.PathOffset;
import edu.byu.ece.rapidSmith.timing.TraceReportParser;
import edu.byu.ece.rapidSmith.util.FileTools;

/**
 * This class aims to allow a user to examine and modify XDL designs in a much 
 * easier fashion than standard text.
 * @author Chris Lavin
 * Created on: Sep 15, 2010
 */
public class DesignExplorer extends QMainWindow{
	/** Status Bar Label */
	protected QLabel statusLabel;
	/** Path to images in RapidSmith */
	protected static String rsrcPath = FileTools.getRapidSmithPath()+File.separator+"images";
	/** Device of the current design that is open */
	protected Device device;
	/** The design that is current and active */
	protected Design design;
	/** WireEnumerator of the current design that is open */
	protected WireEnumerator we;
	/** Name of the Program */
	protected static String title = "Design Explorer";
	/** File Name of the current design that is open */
	protected String currOpenFileName = null;
	/** Status Bar */
	private QStatusBar statusBar;
	/** Keeps track of the tabs within the application */
	protected QTabWidget tabs;
	/** This is the tile view of the design */
	protected TileWindow tileWindow;
	/** This is the list of nets in the design */
	protected FilterWindow netWindow;
	/** This is the list of instances in the design */
	protected FilterWindow instanceWindow;
	/** This is the list of modules in the design */
	protected FilterWindow moduleWindow;
	/** This is the list of module instances in the design */
	protected FilterWindow moduleInstanceWindow;
	
	/** This is the list of path delays in the design */
	protected FilterWindow delayWindow;
	/** This is the list of path offsets in the design */
	protected FilterWindow offsetWindow;
	/** Optional path delays for the design, loaded from timing report (.TWR) */
	protected ArrayList<PathDelay> delays;
	/** Optional path offsets for the design, loaded from timing report (.TWR) */
	protected ArrayList<PathOffset> offsets;
	
	// Names of the tabs
	protected static final String TILE_LAYOUT = "Tiles";
	protected static final String NETS = "Nets";
	protected static final String INSTANCES = "Instances";
	protected static final String PIPS = "PIPS";
	protected static final String MODULES = "Modules";
	protected static final String MODULE_INSTANCES = "Module Instances";
	protected static final String RESOURCE_REPORT = "Resource Report";
	protected static final String PATH_DELAYS = "Path Delays";
	protected static final String PATH_OFFSETS = "Path Offsets";
	
	
	public static void main(String[] args){
		QApplication.setGraphicsSystem("raster");
		QApplication.initialize(args);

		String fileToOpen = null;
		String traceFileToOpen = null;
		if(args.length > 0){
			fileToOpen = args[0];
		}
		if(args.length > 1){
			traceFileToOpen = args[1];
		}
		
		DesignExplorer designExplorer = new DesignExplorer(null, fileToOpen, traceFileToOpen);

		designExplorer.show();

		QApplication.exec();
	}
	
	/**
	 * Constructor for the design explorer
	 * @param parent Parent QWidget for window hierarchy.
	 * @param fileToOpen The name of the design to open
	 * @param traceFileToOpen Name of the trace report file (TWR) to load 
	 */
	public DesignExplorer(QWidget parent, String fileToOpen, String traceFileToOpen){
		super(parent);
		
		setupFileActions();
		setWindowTitle(title);

		// Setup Windows
		tabs = new QTabWidget();
		setCentralWidget(tabs);
		
		setupTileWindow();
		statusLabel = new QLabel("Status Bar");
		statusLabel.setText("Status Bar");
		statusBar = new QStatusBar();
		statusBar.addWidget(statusLabel);
		setStatusBar(statusBar);
		tileWindow.scene.updateStatus.connect(statusLabel, "setText(String)");
		
		
		netWindow = new FilterWindow(this, FilterType.NETS);
		instanceWindow = new FilterWindow(this, FilterType.INSTANCES);
		moduleWindow = new FilterWindow(this, FilterType.MODULES);
		moduleInstanceWindow = new FilterWindow(this, FilterType.MODULE_INSTANCES);

		tabs.addTab(netWindow, NETS);
		tabs.addTab(instanceWindow, INSTANCES);
		tabs.addTab(moduleWindow, MODULES);
		tabs.addTab(moduleInstanceWindow, MODULE_INSTANCES);
		
		if(fileToOpen != null){
			internalOpenDesign(fileToOpen);
		}
		
		if(traceFileToOpen != null){
			internalLoadDesignTimingInfo(traceFileToOpen);
		}
		
		if(currOpenFileName == null){
        	openDesign();
        }
		// Set the opening default window size to 1024x768 pixels
		resize(1024, 768);
	}

	/**
	 * Initializes the tile window
	 */
	private void setupTileWindow(){
        tileWindow = new TileWindow(this);
        int tabIndex = tabs.addTab(tileWindow, TILE_LAYOUT);
        tabs.setCurrentIndex(tabIndex);
	}
	
	/**
	 * Creates the message when choosing about on the menu.
	 */
	protected void about(){
		QMessageBox.information(this, "Info",
				"This is the first try \nat an XDL Design Explorer.");
	}
	
	/**
	 * Opens a file chooser dialog to load an XDL file.
	 */
	protected void openDesign(){
		String fileName = QFileDialog.getOpenFileName(this, "Choose a file...",
				".", FileFilters.xdlFilter);
		if(fileName.endsWith(".xdl")){
			internalOpenDesign(fileName);
		}
	}
	
	/**
	 * Opens a file chooser dialog to load a TWR (timing report) file.
	 */
	protected void loadDesignTimingInfo(){
		String fileName = QFileDialog.getOpenFileName(this, "Choose corresponding timing report...",
				".", FileFilters.twrFilter);
		if(fileName.endsWith(".twr")){
			internalLoadDesignTimingInfo(fileName);
		}
	}
	
	/**
	 * Loads the timing report fileName into the delay and offset windows.
	 * @param fileName Name of the TWR (timing report) file to load.
	 */
	private void internalLoadDesignTimingInfo(String fileName){
		TraceReportParser parser = new TraceReportParser();
		parser.parseTWR(fileName, design);
		delays = parser.getPathDelays();
		offsets = parser.getPathOffsets();

		// Create 2 more tabs for timing information
		delayWindow = new FilterWindow(this, FilterType.DELAYS);
		offsetWindow = new FilterWindow(this, FilterType.OFFSETS);
		tabs.addTab(delayWindow, PATH_DELAYS);
		tabs.addTab(offsetWindow, PATH_OFFSETS);
		
		tileWindow.drawCriticalPaths(delays);
		tileWindow.slider.setDelays();
	}
	
	/**
	 * Loads the XDL design fileName into the design explorer.
	 * @param fileName Name of the XDL file to load.
	 */
	private void internalOpenDesign(String fileName){
		currOpenFileName = fileName;
		String shortFileName = fileName.substring(fileName.lastIndexOf('/')+1);
		QProgressDialog progress = new QProgressDialog("Loading "+currOpenFileName+"...", "", 0, 100, this);
		progress.setWindowTitle("Load Progress");
		progress.setWindowModality(WindowModality.WindowModal);
		progress.setCancelButton(null);
		progress.show();
		progress.setValue(0);	
		progress.setValue(10);
		design = new Design();
		progress.setValue(20);
		design.loadXDLFile(fileName);
		progress.setValue(50);
		device = design.getDevice();
		we = design.getWireEnumerator();
		progress.setValue(70);
		progress.setValue(80);
		progress.setValue(90);
		setWindowTitle(shortFileName + " - " + title);
		progress.setValue(100);
		statusBar().showMessage(currOpenFileName + " loaded.", 2000);
		tileWindow.setDesign(design);
		netWindow.loadCurrentDesignData();
		instanceWindow.loadCurrentDesignData();
		moduleWindow.loadCurrentDesignData();
		moduleInstanceWindow.loadCurrentDesignData();
	}
	
	/**
	 * This method builds the actions associated with the toolbar.
	 * @param name Name of the action
	 * @param image An image/icon associated with the action
	 * @param shortcut A shortcut key to press on the keyboard to activate 
	 * this action.
	 * @param slot The method to call when the action is made.
	 * @param menu Which menu to add the action to.
	 * @param toolBar The toolbar to add the action to.
	 * @return The action object.
	 */
	private QAction action(String name, String image, Object shortcut,
			String slot, QMenu menu, QToolBar toolBar) {
		QAction a = new QAction(name, this);

		if (image != null)
			a.setIcon(new QIcon(rsrcPath + File.separator + image + ".png"));
		if (menu != null)
			menu.addAction(a);
		if (toolBar != null)
			toolBar.addAction(a);
		if (slot != null)
			a.triggered.connect(this, slot);

		if (shortcut instanceof String)
			a.setShortcut((String) shortcut);
		else if (shortcut instanceof QKeySequence.StandardKey)
			a.setShortcuts((QKeySequence.StandardKey) shortcut);

		return a;
	}

	/**
	 * Creates a file menu with actions.  These are also added to the tool bar.
	 */
	private void setupFileActions(){
		QToolBar tb = new QToolBar(this);
		tb.setWindowTitle(tr("File Actions"));
		addToolBar(tb);

		QMenu fileMenu = new QMenu(tr("&File"), this);
		menuBar().addMenu(fileMenu);

		action(tr("Open XDL Design"), "open", StandardKey.Open, "openDesign()",fileMenu, tb);		
		action(tr("Load Timing Info"), "openTimingReport", StandardKey.UnknownKey, "loadDesignTimingInfo()",fileMenu, tb);
		fileMenu.addSeparator();
		action(tr("&Quit"), null, "Ctrl+Q", "close()", fileMenu, null);
	}

}
