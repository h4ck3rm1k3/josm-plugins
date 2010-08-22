//License: GPL. For details, see LICENSE file.
// Copyright (c) 2009 / 2010 by Werner Koenig & Malcolm Herring

package toms.seamarks.buoys;

import java.util.Map;

import javax.swing.ImageIcon;

import org.openstreetmap.josm.Main;
import org.openstreetmap.josm.command.ChangePropertyCommand;
import org.openstreetmap.josm.data.osm.Node;

import toms.dialogs.SmpDialogAction;
import toms.seamarks.SeaMark;

public class BuoySpec extends Buoy {
	public BuoySpec(SmpDialogAction dia, Node node) {
		super(dia);

		String str;
		Map<String, String> keys;
		keys = node.getKeys();
		setNode(node);

		dlg.cbM01StyleOfMark.removeAllItems();
		dlg.cbM01StyleOfMark.addItem("Not set");
		dlg.cbM01StyleOfMark.addItem("Pillar Buoy");
		dlg.cbM01StyleOfMark.addItem("Spar Buoy");
		dlg.cbM01StyleOfMark.addItem("Sphere Buoy");
		dlg.cbM01StyleOfMark.addItem("Barrel");
		dlg.cbM01StyleOfMark.addItem("Float");
		dlg.cbM01StyleOfMark.addItem("Beacon");

		dlg.cbM01Kennung.removeAllItems();
		dlg.cbM01Kennung.addItem("Not set");
		dlg.cbM01Kennung.addItem("Fl");
		dlg.cbM01Kennung.addItem("Fl(2)");
		dlg.cbM01Kennung.addItem("Fl(3)");
		dlg.cbM01Kennung.addItem("Fl(4)");
		dlg.cbM01Kennung.addItem("Fl(5)");
		dlg.cbM01Kennung.addItem("Oc(2)");
		dlg.cbM01Kennung.addItem("Oc(3)");
		dlg.cbM01Kennung.addItem("Q");
		dlg.cbM01Kennung.addItem("IQ");
		dlg.cbM01Kennung.setSelectedIndex(0);

		dlg.cM01Fired.setSelected(false);
		dlg.cM01TopMark.setSelected(false);
		dlg.cM01Fired.setEnabled(false);
		dlg.cM01TopMark.setEnabled(false);
		dlg.tbM01Region.setEnabled(false);

		setColour(SeaMark.YELLOW);
		setLightColour("W");
		setBuoyIndex(SPECIAL_PURPOSE);

		if (keys.containsKey("name"))
			setName(keys.get("name"));

		if (keys.containsKey("seamark:name"))
			setName(keys.get("seamark:name"));

		if (keys.containsKey("seamark:buoy_special_purpose:name"))
			setName(keys.get("seamark:buoy_special_purpose:name"));
		else if (keys.containsKey("seamark:beacon_special_purpose:name"))
			setName(keys.get("seamark:beacon_special_purpose:name"));
		else if (keys.containsKey("seamark:light_float:name"))
			setName(keys.get("seamark:light_float:name"));

		if (keys.containsKey("seamark:buoy_special_purpose:shape")) {
			str = keys.get("seamark:buoy_special_purpose:shape");

			if (str.equals("pillar"))
				setStyleIndex(SPEC_PILLAR);
			else if (str.equals("spar"))
				setStyleIndex(SPEC_SPAR);
			else if (str.equals("sphere"))
				setStyleIndex(SPEC_SPHERE);
			else if (str.equals("barrel"))
				setStyleIndex(SPEC_BARREL);
		}

		if ((keys.containsKey("seamark:type") && keys.get("seamark:type").equals(
				"beacon_special_purpose"))
				|| keys.containsKey("seamark:special_purpose_beacon:colour")
				|| keys.containsKey("seamark:special_purpose_beacon:shape"))
			setStyleIndex(SPEC_BEACON);
		else if (keys.containsKey("seamark:light_float:colour")
				&& keys.get("seamark:light_float:colour").equals("yellow"))
			setStyleIndex(SPEC_FLOAT);

		keys = node.getKeys();
		if (keys.containsKey("seamark:topmark:shape")) {
			str = keys.get("seamark:topmark:shape");

			if (str.equals("x-shape")) {
				setTopMark(true);
			}
		}

		if (keys.containsKey("seamark:light:colour")) {
			str = keys.get("seamark:light:colour");

			if (keys.containsKey("seamark:light:character")) {
				setLightGroup(keys);

				String c = keys.get("seamark:light:character");
				if (getLightGroup() != "")
					c = c + "(" + getLightGroup() + ")";

				setLightChar(c);
				setLightPeriod(keys);
			}

			if (str.equals("white")) {
				setFired(true);
				setLightColour("W");
			}
		}
	}

	public void setStyleIndex(int styleIndex) {
		super.setStyleIndex(styleIndex);
		if (styleIndex == SPEC_BARREL) {
			dlg.cM01Fired.setSelected(false);
			dlg.cM01TopMark.setSelected(false);
			dlg.cM01Fired.setEnabled(false);
			dlg.cM01TopMark.setEnabled(false);
		} else {
			dlg.cM01Fired.setEnabled(true);
			dlg.cM01TopMark.setEnabled(true);
		}
	}

	public void paintSign() {
		super.paintSign();

		dlg.sM01StatusBar.setText(getErrMsg());

		dlg.tfM01Name.setEnabled(true);
		dlg.tfM01Name.setText(getName());

		String image = "/images/Special_Purpose";

		switch (getStyleIndex()) {
		case SPEC_PILLAR:
			image += "_Pillar";
			break;
		case SPEC_SPAR:
			image += "_Spar";
			break;
		case SPEC_SPHERE:
			image += "_Sphere";
			break;
		case SPEC_BARREL:
			image += "_Barrel";
			break;
		case SPEC_FLOAT:
			image += "_Float";
			break;
		case SPEC_BEACON:
			image += "_Beacon";
			break;
		default:
		}

		if (!image.equals("/images/Special_Purpose")) {

			if (hasTopMark())
				image += "_CrossY";

			if (isFired()) {
				image += "_Lit";
				if (getLightChar() != "") {
					String c;

					c = getLightChar();

					dlg.cbM01Kennung.setSelectedItem(c);
					if (dlg.cbM01Kennung.getSelectedItem().equals("Not set"))
						c = "";
				}
			}
			image += ".png";

			dlg.lM01Icon01.setIcon(new ImageIcon(getClass().getResource(image)));
		} else
			dlg.lM01Icon01.setIcon(null);
	}

	public void saveSign() {
		Node node = getNode();

		if (node == null) {
			return;
		}

		switch (getStyleIndex()) {
		case SPEC_PILLAR:
			super.saveSign("buoy_special_purpose");
			Main.main.undoRedo.add(new ChangePropertyCommand(node,
					"seamark:buoy_special_purpose:shape", "pillar"));
			Main.main.undoRedo.add(new ChangePropertyCommand(node,
					"seamark:buoy_special_purpose:colour", "yellow"));
			break;
		case SPEC_SPAR:
			super.saveSign("buoy_special_purpose");
			Main.main.undoRedo.add(new ChangePropertyCommand(node,
					"seamark:buoy_special_purpose:shape", "spar"));
			Main.main.undoRedo.add(new ChangePropertyCommand(node,
					"seamark:buoy_special_purpose:colour", "yellow"));
			break;
		case SPEC_SPHERE:
			super.saveSign("buoy_special_purpose");
			Main.main.undoRedo.add(new ChangePropertyCommand(node,
					"seamark:buoy_special_purpose:shape", "sphere"));
			Main.main.undoRedo.add(new ChangePropertyCommand(node,
					"seamark:buoy_special_purpose:colour", "yellow"));
			break;
		case SPEC_BARREL:
			super.saveSign("buoy_special_purpose");
			Main.main.undoRedo.add(new ChangePropertyCommand(node,
					"seamark:buoy_special_purpose:shape", "barrel"));
			Main.main.undoRedo.add(new ChangePropertyCommand(node,
					"seamark:buoy_special_purpose:colour", "yellow"));
			break;
		case SPEC_FLOAT:
			super.saveSign("light_float");
			Main.main.undoRedo.add(new ChangePropertyCommand(node,
					"seamark:light_float:colour", "yellow"));
			break;
		case SPEC_BEACON:
			super.saveSign("beacon_special_purpose");
			Main.main.undoRedo.add(new ChangePropertyCommand(node,
					"seamark:beacon_special_purpose:colour", "yellow"));
			break;
		default:
		}

		saveTopMarkData("x-shape", "yellow");

		saveLightData("white");

	}

	public void setLightColour() {
		super.setLightColour("W");
	}

}