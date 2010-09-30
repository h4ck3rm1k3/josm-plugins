package oseam;

import javax.swing.JComponent;

import oseam.dialogs.OSeaMAction;

import smed.plug.ifc.SmedPluggable;
import smed.plug.ifc.SmedPluginManager;

public class OSeaM implements SmedPluggable {

	private OSeaMAction osm = null;
	
	@Override
	public JComponent getComponent() {
		osm = new OSeaMAction();
		osm.init();
		return osm.getPM01SeaMap();
	}

	@Override
	public String getInfo() {return "mapping seamarks"; }

	@Override
	public String getName() {return "Seamarks"; }

	@Override
	public void setPluginManager(SmedPluginManager manager) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean start() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean stop() {
		osm.closePanel();
		return true;
	}

	@Override
	public String getFileName() {
		return "OSeaM.jar";
	
	}


}