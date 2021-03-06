//    JOSM opendata plugin.
//    Copyright (C) 2011-2012 Don-vip
//
//    This program is free software: you can redistribute it and/or modify
//    it under the terms of the GNU General Public License as published by
//    the Free Software Foundation, either version 3 of the License, or
//    (at your option) any later version.
//
//    This program is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//    GNU General Public License for more details.
//
//    You should have received a copy of the GNU General Public License
//    along with this program.  If not, see <http://www.gnu.org/licenses/>.
package org.openstreetmap.josm.plugins.opendata;

import java.awt.Image;
import java.awt.Toolkit;
import java.io.File;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

import org.openstreetmap.josm.Main;
import org.openstreetmap.josm.actions.ExtensionFileFilter;
import org.openstreetmap.josm.gui.MainMenu;
import org.openstreetmap.josm.gui.MapFrame;
import org.openstreetmap.josm.gui.MenuScroller;
import org.openstreetmap.josm.gui.preferences.PreferenceSetting;
import org.openstreetmap.josm.plugins.Plugin;
import org.openstreetmap.josm.plugins.PluginInformation;
import org.openstreetmap.josm.plugins.opendata.core.OdConstants;
import org.openstreetmap.josm.plugins.opendata.core.actions.DownloadDataAction;
import org.openstreetmap.josm.plugins.opendata.core.actions.DownloadDataTask;
import org.openstreetmap.josm.plugins.opendata.core.actions.OpenPreferencesActions;
import org.openstreetmap.josm.plugins.opendata.core.datasets.AbstractDataSetHandler;
import org.openstreetmap.josm.plugins.opendata.core.datasets.DataSetCategory;
import org.openstreetmap.josm.plugins.opendata.core.gui.OdDialog;
import org.openstreetmap.josm.plugins.opendata.core.gui.OdPreferenceSetting;
import org.openstreetmap.josm.plugins.opendata.core.io.AbstractImporter;
import org.openstreetmap.josm.plugins.opendata.core.io.XmlImporter;
import org.openstreetmap.josm.plugins.opendata.core.io.archive.SevenZipImporter;
import org.openstreetmap.josm.plugins.opendata.core.io.archive.ZipImporter;
import org.openstreetmap.josm.plugins.opendata.core.io.geographic.GmlImporter;
import org.openstreetmap.josm.plugins.opendata.core.io.geographic.KmlKmzImporter;
import org.openstreetmap.josm.plugins.opendata.core.io.geographic.MifTabImporter;
import org.openstreetmap.josm.plugins.opendata.core.io.geographic.ShpImporter;
import org.openstreetmap.josm.plugins.opendata.core.io.tabular.CsvImporter;
import org.openstreetmap.josm.plugins.opendata.core.io.tabular.OdsImporter;
import org.openstreetmap.josm.plugins.opendata.core.io.tabular.XlsImporter;
import org.openstreetmap.josm.plugins.opendata.core.modules.Module;
import org.openstreetmap.josm.plugins.opendata.core.modules.ModuleHandler;
import org.openstreetmap.josm.plugins.opendata.core.modules.ModuleInformation;
import org.openstreetmap.josm.plugins.opendata.core.util.OdUtils;
import org.openstreetmap.josm.tools.Pair;

public final class OdPlugin extends Plugin implements OdConstants {

	private static OdPlugin instance;
	
	public final XmlImporter xmlImporter = new XmlImporter();
	
	private final JMenu menu;
	
	private OdDialog dialog;
	
	public OdPlugin(PluginInformation info) { // NO_UCD
		super(info);
		if (instance == null) {
			instance = this;
		} else {
			throw new IllegalAccessError("Cannot instantiate plugin twice !");
		}
        // Allow JOSM to import more files
		for (AbstractImporter importer : Arrays.asList(new AbstractImporter[]{
				new CsvImporter(), new OdsImporter(), new XlsImporter(), // Tabular file formats
				new KmlKmzImporter(), new ShpImporter(), new MifTabImporter(), new GmlImporter(), // Geographic file formats
				new ZipImporter(), // Zip archive containing any of the others
                new SevenZipImporter(), // 7Zip archive containing any of the others
				xmlImporter // Generic importer for XML files (currently used for Neptune files)
		})) {
			ExtensionFileFilter.importers.add(0, importer);
		}
        // Load modules
        loadModules();
        // Add menu
        
        menu = Main.main.menu.dataMenu;
        buildMenu();
        // Add download task
        Main.main.menu.openLocation.addDownloadTaskClass(DownloadDataTask.class);
        // Delete previous temp dirs if any (old plugin versions did not remove them correctly)
        OdUtils.deletePreviousTempDirs();
	}
	
	public static final OdPlugin getInstance() {
		return instance;
	}
	
	private JMenu getModuleMenu(Module module) {
		String moduleName = module.getDisplayedName();
		if (moduleName == null || moduleName.isEmpty()) {
			moduleName = module.getModuleInformation().getName();
		}
		JMenu moduleMenu = new JMenu(moduleName);
		moduleMenu.setIcon(module.getModuleInformation().getScaledIcon());
		return moduleMenu;
	}
	
	private void buildMenu() {
        int screenHeight = Toolkit.getDefaultToolkit().getScreenSize().height;
        for (Module module : ModuleHandler.moduleList) {
        	Map<DataSetCategory, JMenu> catMenus = new HashMap<DataSetCategory, JMenu>();
        	JMenu moduleMenu = null;
        	for (AbstractDataSetHandler handler: module.getNewlyInstanciatedHandlers()) {
        	    URL dataURL = handler.getDataURL();
        	    List<Pair<String, URL>> dataURLs = handler.getDataURLs();
        		if (dataURL != null || (dataURLs != null && !dataURLs.isEmpty())) {
        			if (moduleMenu == null) {
        				moduleMenu = getModuleMenu(module);
        			}
        			DataSetCategory cat = handler.getCategory();
        			JMenu endMenu = null;
        			if (cat != null) {
        				if ((endMenu = catMenus.get(cat)) == null) {
        					catMenus.put(cat, endMenu = new JMenu(cat.getName()));
        					setMenuItemIcon(cat.getIcon(), endMenu);
        					moduleMenu.add(endMenu);
        				}
        			}
        			if (endMenu == null) {
        				endMenu = moduleMenu;
        			}
        			String handlerName = handler.getName();
        			if (handlerName == null || handlerName.isEmpty()) {
        				handlerName = handler.getClass().getName();
        			}
        			JMenuItem handlerItem = null;
        			if (dataURL != null) {
        			    handlerItem = endMenu.add(new DownloadDataAction(module, handlerName, dataURL));
        			} else if (dataURLs != null) {
        				JMenu handlerMenu = new JMenu(handlerName);
        				JMenuItem item = null;
        				for (Pair<String, URL> pair : dataURLs) {
        					if (pair != null && pair.a != null && pair.b != null) {
        						item = handlerMenu.add(new DownloadDataAction(module, pair.a, pair.b));
        					}
        				}
        				if (item != null) {
        					MenuScroller.setScrollerFor(handlerMenu, (screenHeight / item.getPreferredSize().height)-3);
        					handlerItem = endMenu.add(handlerMenu);
        				}
        			}
        			if (handlerItem != null) {
        			    setMenuItemIcon(handler.getMenuIcon(), handlerItem);
        			}
        		}
        	}
        	if (moduleMenu != null) {
        		//MenuScroller.setScrollerFor(moduleMenu, screenHeight / moduleMenu.getItem(0).getPreferredSize().height);
        		menu.add(moduleMenu);
        	}
        }
        menu.addSeparator();
        /*JMenuItem itemIcon =*/ MainMenu.add(menu, new OpenPreferencesActions());
        //MenuScroller.setScrollerFor(menu, screenHeight / itemIcon.getPreferredSize().height);
	}
	
	private void setMenuItemIcon(ImageIcon icon, JMenuItem menuItem) {
        if (icon != null) {
            if (icon.getIconHeight() != 16 || icon.getIconWidth() != 16) { 
                icon = new ImageIcon(icon.getImage().getScaledInstance(16, 16, Image.SCALE_DEFAULT));
            }
            menuItem.setIcon(icon);
        }
	}

	/* (non-Javadoc)
	 * @see org.openstreetmap.josm.plugins.Plugin#mapFrameInitialized(org.openstreetmap.josm.gui.MapFrame, org.openstreetmap.josm.gui.MapFrame)
	 */
	@Override
	public void mapFrameInitialized(MapFrame oldFrame, MapFrame newFrame) {
		if (newFrame != null) {
			newFrame.addToggleDialog(dialog = new OdDialog());
		} else {
		    dialog = null;
		}
	}
	
    /* (non-Javadoc)
     * @see org.openstreetmap.josm.plugins.Plugin#getPreferenceSetting()
     */
    @Override
    public PreferenceSetting getPreferenceSetting() {
        return new OdPreferenceSetting();
    }
    
    private final void loadModules() {
        List<ModuleInformation> modulesToLoad = ModuleHandler.buildListOfModulesToLoad(Main.parent);
        if (!modulesToLoad.isEmpty() && ModuleHandler.checkAndConfirmModuleUpdate(Main.parent)) {
            modulesToLoad = ModuleHandler.updateModules(Main.parent, modulesToLoad, null);
        }

        ModuleHandler.installDownloadedModules(true);
    	ModuleHandler.loadModules(Main.parent, modulesToLoad, null);
    }
    
    private final File getSubDirectory(String name) {
    	File dir = new File(getPluginDir()+File.separator+name);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dir;
    }
    
    public final File getModulesDirectory() {
    	return getSubDirectory("modules");
    }

    public final File getResourcesDirectory() {
    	return getSubDirectory("resources");
    }

    public OdDialog getDialog() {
        return dialog;
    }

    /*
    private static final void fixUcDetectorTest() {
    	FilterFactoryImpl n1 = new FilterFactoryImpl();
    	DatumAliases n2 = new DatumAliases();
    	EPSGCRSAuthorityFactory n3 = new EPSGCRSAuthorityFactory();
    	DefaultFunctionFactory n4 = new DefaultFunctionFactory();
    	ShapefileDirectoryFactory n5 = new ShapefileDirectoryFactory();
    	ReferencingObjectFactory n6 = new ReferencingObjectFactory();
    	BufferedCoordinateOperationFactory n7 = new BufferedCoordinateOperationFactory();
    }*/
}
