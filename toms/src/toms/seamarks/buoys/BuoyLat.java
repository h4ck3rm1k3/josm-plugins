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

public class BuoyLat extends Buoy {
	public BuoyLat(SmpDialogAction dia, Node node) {
		super(dia);

		String str;
		Map<String, String> keys;
		keys = node.getKeys();
		setNode(node);

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

		setStyleIndex(0);
		setLightColour();
		setFired(false);
		setTopMark(false);
		setRegion(Main.pref.get("tomsplugin.IALA").equals("B"));

		dlg.cM01Fired.setSelected(false);
		dlg.cM01TopMark.setSelected(false);
		dlg.tbM01Region.setEnabled(true);

		if (keys.containsKey("name"))
			setName(keys.get("name"));

		if (keys.containsKey("seamark:name"))
			setName(keys.get("seamark:name"));

		if (keys.containsKey("seamark:buoy_lateral:name"))
			setName(keys.get("seamark:buoy_lateral:name"));
		else if (keys.containsKey("seamark:beacon_lateral:name"))
			setName(keys.get("seamark:beacon_lateral:name"));
		else if (keys.containsKey("seamark:light_float:name"))
			setName(keys.get("seamark:light_float:name"));

		String cat = "";
		String col = "";

		if (keys.containsKey("seamark:buoy_lateral:category"))
			cat = keys.get("seamark:buoy_lateral:category");
		else if (keys.containsKey("seamark:beacon_lateral:category"))
			cat = keys.get("seamark:beacon_lateral:category");

		if (keys.containsKey("seamark:buoy_lateral:colour"))
			col = keys.get("seamark:buoy_lateral:colour");
		else if (keys.containsKey("seamark:beacon_lateral:colour"))
			col = keys.get("seamark:beacon_lateral:colour");
		else if (keys.containsKey("seamark:light_float:colour"))
			col = keys.get("seamark:light_float:colour");

		if (cat.equals("")) {
			if (col.equals("red")) {
				setColour(RED);
				if (getRegion() == IALA_A)
					setBuoyIndex(PORT_HAND);
				else
					setBuoyIndex(STARBOARD_HAND);
			} else if (col.equals("green")) {
				setColour(GREEN);
				if (getRegion() == IALA_A)
					setBuoyIndex(STARBOARD_HAND);
				else
					setBuoyIndex(PORT_HAND);
			} else if (col.equals("red;green;red")) {
				setColour(RED_GREEN_RED);
				if (getRegion() == IALA_A)
					setBuoyIndex(PREF_PORT_HAND);
				else
					setBuoyIndex(PREF_STARBOARD_HAND);
			} else if (col.equals("green;red;green")) {
				setColour(GREEN_RED_GREEN);
				if (getRegion() == IALA_A)
					setBuoyIndex(PREF_STARBOARD_HAND);
				else
					setBuoyIndex(PREF_PORT_HAND);
			}
		} else if (cat.equals("port")) {

			setBuoyIndex(PORT_HAND);

			if (col.equals("red")) {
				setRegion(SeaMark.IALA_A);
				setColour(SeaMark.RED);
			} else if (col.equals("green")) {
				setRegion(SeaMark.IALA_B);
				setColour(SeaMark.GREEN);
			} else {
				if (getRegion() == IALA_A)
					setColour(SeaMark.RED);
				else
					setColour(SeaMark.GREEN);
			}
		} else if (cat.compareTo("starboard") == 0) {

			setBuoyIndex(STARBOARD_HAND);

			if (col.compareTo("green") == 0) {
				setRegion(SeaMark.IALA_A);
				setColour(SeaMark.GREEN);
			} else if (col.equals("red")) {
				setRegion(SeaMark.IALA_B);
				setColour(SeaMark.RED);
			} else {
				if (getRegion() == IALA_A)
					setColour(SeaMark.GREEN);
				else
					setColour(SeaMark.RED);
			}
		} else if (cat.compareTo("preferred_channel_port") == 0) {

			setBuoyIndex(PREF_PORT_HAND);

			if (col.compareTo("red;green;red") == 0) {
				setRegion(SeaMark.IALA_A);
				setColour(SeaMark.RED_GREEN_RED);
			} else if (col.compareTo("green;red;green") == 0) {
				setRegion(SeaMark.IALA_B);
				setColour(SeaMark.GREEN_RED_GREEN);
			} else {
				if (getRegion() == IALA_A)
					setColour(SeaMark.RED_GREEN_RED);
				else
					setColour(SeaMark.GREEN_RED_GREEN);
			}

		} else if (cat.compareTo("preferred_channel_starboard") == 0) {

			setBuoyIndex(PREF_STARBOARD_HAND);

			if (col.compareTo("green;red;green") == 0) {
				setRegion(SeaMark.IALA_A);
				setColour(SeaMark.GREEN_RED_GREEN);
			} else if (col.compareTo("red;green;red") == 0) {
				setRegion(SeaMark.IALA_B);
				setColour(SeaMark.RED_GREEN_RED);
			} else {
				if (getRegion() == IALA_A)
					setColour(SeaMark.GREEN_RED_GREEN);
				else
					setColour(SeaMark.RED_GREEN_RED);
			}
		}

		refreshStyles();

		if (keys.containsKey("seamark:buoy_lateral:shape")) {
			str = keys.get("seamark:buoy_lateral:shape");

			switch (getBuoyIndex()) {
			case PORT_HAND:
				if (str.compareTo("can") == 0)
					setStyleIndex(LAT_CAN);
				else if (str.compareTo("pillar") == 0)
					setStyleIndex(LAT_PILLAR);
				else if (str.compareTo("spar") == 0)
					setStyleIndex(LAT_SPAR);
				break;

			case PREF_PORT_HAND:
				if (str.compareTo("can") == 0)
					setStyleIndex(LAT_CAN);
				else if (str.compareTo("pillar") == 0)
					setStyleIndex(LAT_PILLAR);
				else if (str.compareTo("spar") == 0)
					setStyleIndex(LAT_SPAR);
				break;

			case STARBOARD_HAND:
				if (str.compareTo("conical") == 0)
					setStyleIndex(LAT_CONE);
				else if (str.compareTo("pillar") == 0)
					setStyleIndex(LAT_PILLAR);
				else if (str.compareTo("spar") == 0)
					setStyleIndex(LAT_SPAR);
				break;

			case PREF_STARBOARD_HAND:
				if (str.compareTo("conical") == 0)
					setStyleIndex(LAT_CONE);
				else if (str.compareTo("pillar") == 0)
					setStyleIndex(LAT_PILLAR);
				else if (str.compareTo("spar") == 0)
					setStyleIndex(LAT_SPAR);
				break;
			}
		} else if (keys.containsKey("seamark:beacon_lateral:colour")) {
			if (keys.containsKey("seamark:beacon_lateral:shape")) {
				str = keys.get("seamark:beacon_lateral:shape");
				if (str.compareTo("tower") == 0)
					setStyleIndex(LAT_TOWER);
				else
					setStyleIndex(LAT_BEACON);
			} else
				setStyleIndex(LAT_BEACON);
		} else if (keys.containsKey("seamark:type")
				&& (keys.get("seamark:type").equals("light_float"))) {
			setStyleIndex(LAT_FLOAT);
		}

		if (keys.containsKey("seamark:topmark:shape")) {
			str = keys.get("seamark:topmark:shape");

			switch (getBuoyIndex()) {
			case PORT_HAND:
			case PREF_PORT_HAND:
				if (str.equals("cylinder")) {
					setTopMark(true);
					setRegion(IALA_A);
				} else if (str.equals("cone, point up")) {
					setTopMark(true);
					setRegion(IALA_B);
				} else {
					setTopMark(false);
				}
				break;

			case STARBOARD_HAND:
			case PREF_STARBOARD_HAND:
				if (str.equals("cone, point up")) {
					setTopMark(true);
					setRegion(IALA_A);
				} else if (str.equals("cylinder")) {
					setTopMark(true);
					setRegion(IALA_B);
				} else {
					setTopMark(false);
				}
				break;
			}
		}

		if (keys.containsKey("seamark:light:colour")) {
			setLightColour(keys.get("seamark:light:colour"));
			setFired(true);
		}

		if (keys.containsKey("seamark:light:character")) {
			setLightGroup(keys);
			setLightChar(keys.get("seamark:light:character"));
			setLightPeriod(keys);
		}
	}

	public void refreshStyles() {
		int type = getBuoyIndex();
		int style = getStyleIndex();

		dlg.cbM01StyleOfMark.removeAllItems();
		dlg.cbM01StyleOfMark.addItem("Not set");

		switch (type) {
		case PORT_HAND:
			dlg.cbM01StyleOfMark.addItem("Can Buoy");
			dlg.cbM01StyleOfMark.addItem("Pillar Buoy");
			dlg.cbM01StyleOfMark.addItem("Spar Buoy");
			dlg.cbM01StyleOfMark.addItem("Beacon");
			dlg.cbM01StyleOfMark.addItem("Tower");
			dlg.cbM01StyleOfMark.addItem("Float");
			dlg.cbM01StyleOfMark.addItem("Perch");
			break;

		case STARBOARD_HAND:
			dlg.cbM01StyleOfMark.addItem("Cone Buoy");
			dlg.cbM01StyleOfMark.addItem("Pillar Buoy");
			dlg.cbM01StyleOfMark.addItem("Spar Buoy");
			dlg.cbM01StyleOfMark.addItem("Beacon");
			dlg.cbM01StyleOfMark.addItem("Tower");
			dlg.cbM01StyleOfMark.addItem("Float");
			dlg.cbM01StyleOfMark.addItem("Perch");
			break;

		case PREF_PORT_HAND:
			dlg.cbM01StyleOfMark.addItem("Can Buoy");
			dlg.cbM01StyleOfMark.addItem("Pillar Buoy");
			dlg.cbM01StyleOfMark.addItem("Spar Buoy");
			dlg.cbM01StyleOfMark.addItem("Beacon");
			dlg.cbM01StyleOfMark.addItem("Tower");
			dlg.cbM01StyleOfMark.addItem("Float");
			break;

		case PREF_STARBOARD_HAND:
			dlg.cbM01StyleOfMark.addItem("Cone Buoy");
			dlg.cbM01StyleOfMark.addItem("Pillar Buoy");
			dlg.cbM01StyleOfMark.addItem("Spar Buoy");
			dlg.cbM01StyleOfMark.addItem("Beacon");
			dlg.cbM01StyleOfMark.addItem("Tower");
			dlg.cbM01StyleOfMark.addItem("Float");
			break;

		default:
		}

		if (style >= dlg.cbM01StyleOfMark.getItemCount())
			style = 0;
		setStyleIndex(style);
		dlg.cbM01StyleOfMark.setSelectedIndex(style);

	}

	public void paintSign() {
		super.paintSign();

		dlg.sM01StatusBar.setText(getErrMsg());

		dlg.tfM01Name.setEnabled(true);
		dlg.tfM01Name.setText(getName());
		dlg.cM01Fired.setEnabled(true);
		dlg.cM01TopMark.setEnabled(true);

		String image = "/images/Lateral";

		int cat = getBuoyIndex();
		boolean region = getRegion();
		int style = getStyleIndex();

		switch (getBuoyIndex()) {
		case SeaMark.PORT_HAND:
			if (region != SeaMark.IALA_B)
				switch (style) {
				case LAT_CAN:
					image += "_Can_Red";
					break;
				case LAT_PILLAR:
					image += "_Pillar_Red";
					break;
				case LAT_SPAR:
					image += "_Spar_Red";
					break;
				case LAT_BEACON:
					image += "_Beacon_Red";
					break;
				case LAT_TOWER:
					image += "_Tower_Red";
					break;
				case LAT_FLOAT:
					image += "_Float_Red";
					break;
				case LAT_PERCH:
					image += "_Perch_Port";
					break;
				default:
				}
			else
				switch (style) {
				case LAT_CAN:
					image += "_Can_Green";
					break;
				case LAT_PILLAR:
					image += "_Pillar_Green";
					break;
				case LAT_SPAR:
					image += "_Spar_Green";
					break;
				case LAT_BEACON:
					image += "_Beacon_Green";
					break;
				case LAT_TOWER:
					image += "_Tower_Green";
					break;
				case LAT_FLOAT:
					image += "_Float_Green";
					break;
				case LAT_PERCH:
					image += "_Perch_Port";
					break;
				default:
				}
			break;

		case SeaMark.STARBOARD_HAND:
			if (region != SeaMark.IALA_B)
				switch (style) {
				case LAT_CONE:
					image += "_Cone_Green";
					break;
				case LAT_PILLAR:
					image += "_Pillar_Green";
					break;
				case LAT_SPAR:
					image += "_Spar_Green";
					break;
				case LAT_BEACON:
					image += "_Beacon_Green";
					break;
				case LAT_TOWER:
					image += "_Tower_Green";
					break;
				case LAT_FLOAT:
					image += "_Float_Green";
					break;
				case LAT_PERCH:
					image += "_Perch_Starboard";
					break;
				default:
				}
			else
				switch (style) {
				case LAT_CONE:
					image += "_Cone_Red";
					break;
				case LAT_PILLAR:
					image += "_Pillar_Red";
					break;
				case LAT_SPAR:
					image += "_Spar_Red";
					break;
				case LAT_BEACON:
					image += "_Beacon_Red";
					break;
				case LAT_TOWER:
					image += "_Tower_Red";
					break;
				case LAT_FLOAT:
					image += "_Float_Red";
					break;
				case LAT_PERCH:
					image += "_Perch_Starboard";
					break;
				default:
				}
			break;

		case SeaMark.PREF_PORT_HAND:
			if (region != SeaMark.IALA_B)
				switch (style) {
				case LAT_CAN:
					image += "_Can_Red_Green_Red";
					break;
				case LAT_PILLAR:
					image += "_Pillar_Red_Green_Red";
					break;
				case LAT_SPAR:
					image += "_Spar_Red_Green_Red";
					break;
				case LAT_BEACON:
					image += "_Beacon_Red_Green_Red";
					break;
				case LAT_TOWER:
					image += "_Tower_Red_Green_Red";
					break;
				case LAT_FLOAT:
					image += "_Float_Red_Green_Red";
					break;
				default:
				}
			else
				switch (style) {
				case LAT_CAN:
					image += "_Can_Green_Red_Green";
					break;
				case LAT_PILLAR:
					image += "_Pillar_Green_Red_Green";
					break;
				case LAT_SPAR:
					image += "_Spar_Green_Red_Green";
					break;
				case LAT_BEACON:
					image += "_Beacon_Green_Red_Green";
					break;
				case LAT_TOWER:
					image += "_Tower_Green_Red_Green";
					break;
				case LAT_FLOAT:
					image += "_Float_Green_Red_Green";
					break;
				default:
				}
			break;

		case SeaMark.PREF_STARBOARD_HAND:
			if (region != SeaMark.IALA_B)
				switch (style) {
				case LAT_CONE:
					image += "_Cone_Green_Red_Green";
					break;
				case LAT_PILLAR:
					image += "_Pillar_Green_Red_Green";
					break;
				case LAT_SPAR:
					image += "_Spar_Green_Red_Green";
					break;
				case LAT_BEACON:
					image += "_Beacon_Green_Red_Green";
					break;
				case LAT_TOWER:
					image += "_Tower_Green_Red_Green";
					break;
				case LAT_FLOAT:
					image += "_Float_Green_Red_Green";
					break;
				default:
				}
			else
				switch (style) {
				case LAT_CONE:
					image += "_Cone_Red_Green_Red";
					break;
				case LAT_PILLAR:
					image += "_Pillar_Red_Green_Red";
					break;
				case LAT_SPAR:
					image += "_Spar_Red_Green_Red";
					break;
				case LAT_BEACON:
					image += "_Beacon_Red_Green_Red";
					break;
				case LAT_TOWER:
					image += "_Tower_Red_Green_Red";
					break;
				case LAT_FLOAT:
					image += "_Float_Red_Green_Red";
					break;
				default:
				}
			break;

		default:
		}

		if (!image.equals("/images/Lateral")) {

			if (hasTopMark()) {
				if (cat == PORT_HAND || cat == PREF_PORT_HAND)
					image += "_Can";
				else
					image += "_Cone";
			}

			if (isFired())
				image += "_Lit";
			if (getLightChar() != "") {
				String c;

				c = getLightChar();
				if (getLightGroup() != "")
					c = c + "(" + getLightGroup() + ")";

				dlg.cbM01Kennung.setSelectedItem(c);
				if (dlg.cbM01Kennung.getSelectedItem().equals("Not set"))
					c = "";
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

		int cat = getBuoyIndex();
		String shape = "";
		String colour = "";

		switch (cat) {

		case PORT_HAND:
			switch (getStyleIndex()) {
			case LAT_CAN:
				super.saveSign("buoy_lateral");
				Main.main.undoRedo.add(new ChangePropertyCommand(node,
						"seamark:buoy_lateral:shape", "can"));
				break;
			case LAT_PILLAR:
				super.saveSign("buoy_lateral");
				Main.main.undoRedo.add(new ChangePropertyCommand(node,
						"seamark:buoy_lateral:shape", "pillar"));
				break;
			case LAT_SPAR:
				super.saveSign("buoy_lateral");
				Main.main.undoRedo.add(new ChangePropertyCommand(node,
						"seamark:buoy_lateral:shape", "spar"));
				break;
			case LAT_BEACON:
				super.saveSign("beacon_lateral");
				break;
			case LAT_TOWER:
				super.saveSign("beacon_lateral");
				Main.main.undoRedo.add(new ChangePropertyCommand(node,
						"seamark:beacon_lateral:shape", "tower"));
				break;
			case LAT_FLOAT:
				super.saveSign("light_float");
				break;
			case LAT_PERCH:
				super.saveSign("buoy_lateral");
				Main.main.undoRedo.add(new ChangePropertyCommand(node,
						"seamark:buoy_lateral:shape", "perch"));
				break;
			default:
			}
			switch (getStyleIndex()) {
			case LAT_CAN:
			case LAT_PILLAR:
			case LAT_SPAR:
			case LAT_PERCH:
				Main.main.undoRedo.add(new ChangePropertyCommand(node,
						"seamark:buoy_lateral:category", "port"));
				if (getRegion() != SeaMark.IALA_B) {
					Main.main.undoRedo.add(new ChangePropertyCommand(node,
							"seamark:buoy_lateral:colour", "red"));
					colour = "red";
				} else {
					Main.main.undoRedo.add(new ChangePropertyCommand(node,
							"seamark:buoy_lateral:colour", "green"));
					colour = "green";
				}
				break;
			case LAT_BEACON:
			case LAT_TOWER:
				Main.main.undoRedo.add(new ChangePropertyCommand(node,
						"seamark:beacon_lateral:category", "port"));
				if (getRegion() != SeaMark.IALA_B) {
					Main.main.undoRedo.add(new ChangePropertyCommand(node,
							"seamark:beacon_lateral:colour", "red"));
					colour = "red";
				} else {
					Main.main.undoRedo.add(new ChangePropertyCommand(node,
							"seamark:beacon_lateral:colour", "green"));
					colour = "green";
				}
				break;
			case LAT_FLOAT:
				if (getRegion() != SeaMark.IALA_B) {
					Main.main.undoRedo.add(new ChangePropertyCommand(node,
							"seamark:light_float:colour", "red"));
					colour = "red";
				} else {
					Main.main.undoRedo.add(new ChangePropertyCommand(node,
							"seamark:light_float:colour", "green"));
					colour = "green";
				}
				break;
			}
			shape = "cylinder";
			break;

		case PREF_PORT_HAND:
			switch (getStyleIndex()) {
			case LAT_CAN:
				super.saveSign("buoy_lateral");
				Main.main.undoRedo.add(new ChangePropertyCommand(node,
						"seamark:buoy_lateral:shape", "can"));
				break;
			case LAT_PILLAR:
				super.saveSign("buoy_lateral");
				Main.main.undoRedo.add(new ChangePropertyCommand(node,
						"seamark:buoy_lateral:shape", "pillar"));
				break;
			case LAT_SPAR:
				super.saveSign("buoy_lateral");
				Main.main.undoRedo.add(new ChangePropertyCommand(node,
						"seamark:buoy_lateral:shape", "spar"));
				break;
			case LAT_BEACON:
				super.saveSign("beacon_lateral");
				break;
			case LAT_TOWER:
				super.saveSign("beacon_lateral");
				Main.main.undoRedo.add(new ChangePropertyCommand(node,
						"seamark:beacon_lateral:shape", "tower"));
				break;
			case LAT_FLOAT:
				super.saveSign("light_float");
				break;
			default:
			}
			switch (getStyleIndex()) {
			case LAT_CAN:
			case LAT_PILLAR:
			case LAT_SPAR:
				Main.main.undoRedo.add(new ChangePropertyCommand(node,
						"seamark:buoy_lateral:category", "preferred_channel_port"));
				Main.main.undoRedo.add(new ChangePropertyCommand(node,
						"seamark:buoy_lateral:colour_pattern", "horizontal stripes"));
				if (getRegion() != SeaMark.IALA_B) {
					Main.main.undoRedo.add(new ChangePropertyCommand(node,
							"seamark:buoy_lateral:colour", "red;green;red"));
					colour = "red";
				} else {
					Main.main.undoRedo.add(new ChangePropertyCommand(node,
							"seamark:buoy_lateral:colour", "green;red;green"));
					colour = "green";
				}
				break;
			case LAT_BEACON:
			case LAT_TOWER:
				Main.main.undoRedo.add(new ChangePropertyCommand(node,
						"seamark:beacon_lateral:category", "preferred_channel_port"));
				Main.main.undoRedo.add(new ChangePropertyCommand(node,
						"seamark:beacon_lateral:colour_pattern", "horizontal stripes"));
				if (getRegion() != SeaMark.IALA_B) {
					Main.main.undoRedo.add(new ChangePropertyCommand(node,
							"seamark:beacon_lateral:colour", "red;green;red"));
					colour = "red";
				} else {
					Main.main.undoRedo.add(new ChangePropertyCommand(node,
							"seamark:beacon_lateral:colour", "green;red;green"));
					colour = "green";
				}
				break;
			case LAT_FLOAT:
				Main.main.undoRedo.add(new ChangePropertyCommand(node,
						"seamark:light_float:colour_pattern", "horizontal stripes"));
				if (getRegion() != SeaMark.IALA_B) {
					Main.main.undoRedo.add(new ChangePropertyCommand(node,
							"seamark:light_float:colour", "red;green;red"));
					colour = "red";
				} else {
					Main.main.undoRedo.add(new ChangePropertyCommand(node,
							"seamark:light_float:colour", "green;red;green"));
					colour = "green";
				}
				break;
			}
			shape = "cylinder";
			break;

		case STARBOARD_HAND:
			switch (getStyleIndex()) {
			case LAT_CONE:
				super.saveSign("buoy_lateral");
				Main.main.undoRedo.add(new ChangePropertyCommand(node,
						"seamark:buoy_lateral:shape", "conical"));
				break;
			case LAT_PILLAR:
				super.saveSign("buoy_lateral");
				Main.main.undoRedo.add(new ChangePropertyCommand(node,
						"seamark:buoy_lateral:shape", "pillar"));
				break;
			case LAT_SPAR:
				super.saveSign("buoy_lateral");
				Main.main.undoRedo.add(new ChangePropertyCommand(node,
						"seamark:buoy_lateral:shape", "spar"));
				break;
			case LAT_BEACON:
				super.saveSign("beacon_lateral");
				Main.main.undoRedo.add(new ChangePropertyCommand(node,
						"seamark:beacon_lateral:shape", "stake"));
				break;
			case LAT_TOWER:
				super.saveSign("beacon_lateral");
				Main.main.undoRedo.add(new ChangePropertyCommand(node,
						"seamark:beacon_lateral:shape", "tower"));
				break;
			case LAT_FLOAT:
				super.saveSign("light_float");
				break;
			case LAT_PERCH:
				super.saveSign("buoy_lateral");
				Main.main.undoRedo.add(new ChangePropertyCommand(node,
						"seamark:buoy_lateral:shape", "perch"));
				break;
			default:
			}
			switch (getStyleIndex()) {
			case LAT_CAN:
			case LAT_PILLAR:
			case LAT_SPAR:
				Main.main.undoRedo.add(new ChangePropertyCommand(node,
						"seamark:buoy_lateral:category", "starboard"));
				if (getRegion() != SeaMark.IALA_B) {
					Main.main.undoRedo.add(new ChangePropertyCommand(node,
							"seamark:buoy_lateral:colour", "green"));
					colour = "green";
				} else {
					Main.main.undoRedo.add(new ChangePropertyCommand(node,
							"seamark:buoy_lateral:colour", "red"));
					colour = "red";
				}
				break;
			case LAT_BEACON:
			case LAT_TOWER:
				Main.main.undoRedo.add(new ChangePropertyCommand(node,
						"seamark:beacon_lateral:category", "starboard"));
				if (getRegion() != SeaMark.IALA_B) {
					Main.main.undoRedo.add(new ChangePropertyCommand(node,
							"seamark:beacon_lateral:colour", "green"));
					colour = "green";
				} else {
					Main.main.undoRedo.add(new ChangePropertyCommand(node,
							"seamark:beacon_lateral:colour", "red"));
					colour = "red";
				}
				break;
			case LAT_FLOAT:
				if (getRegion() != SeaMark.IALA_B) {
					Main.main.undoRedo.add(new ChangePropertyCommand(node,
							"seamark:light_float:colour", "green"));
					colour = "green";
				} else {
					Main.main.undoRedo.add(new ChangePropertyCommand(node,
							"seamark:light_float:colour", "red"));
					colour = "red";
				}
				break;
			}
			shape = "cone, point up";
			break;

		case PREF_STARBOARD_HAND:
			switch (getStyleIndex()) {
			case LAT_CONE:
				super.saveSign("buoy_lateral");
				Main.main.undoRedo.add(new ChangePropertyCommand(node,
						"seamark:buoy_lateral:shape", "conical"));
				break;
			case LAT_PILLAR:
				super.saveSign("buoy_lateral");
				Main.main.undoRedo.add(new ChangePropertyCommand(node,
						"seamark:buoy_lateral:shape", "pillar"));
				break;
			case LAT_SPAR:
				super.saveSign("buoy_lateral");
				Main.main.undoRedo.add(new ChangePropertyCommand(node,
						"seamark:buoy_lateral:shape", "spar"));
				break;
			case LAT_BEACON:
				super.saveSign("beacon_lateral");
				Main.main.undoRedo.add(new ChangePropertyCommand(node,
						"seamark:beacon_lateral:shape", "stake"));
				break;
			case LAT_TOWER:
				super.saveSign("beacon_lateral");
				Main.main.undoRedo.add(new ChangePropertyCommand(node,
						"seamark:beacon_lateral:shape", "tower"));
				break;
			case LAT_FLOAT:
				super.saveSign("light_float");
				break;
			default:
			}
			switch (getStyleIndex()) {
			case LAT_CAN:
			case LAT_PILLAR:
			case LAT_SPAR:
				Main.main.undoRedo.add(new ChangePropertyCommand(node,
						"seamark:buoy_lateral:category", "preferred_channel_starboard"));
				Main.main.undoRedo.add(new ChangePropertyCommand(node,
						"seamark:buoy_lateral:colour_pattern", "horizontal stripes"));
				if (getRegion() != SeaMark.IALA_B) {
					Main.main.undoRedo.add(new ChangePropertyCommand(node,
							"seamark:buoy_lateral:colour", "green;red;green"));
					colour = "green";
				} else {
					Main.main.undoRedo.add(new ChangePropertyCommand(node,
							"seamark:buoy_lateral:colour", "red;green;red"));
					colour = "red";
				}
				break;
			case LAT_BEACON:
			case LAT_TOWER:
				Main.main.undoRedo.add(new ChangePropertyCommand(node,
						"seamark:beacon_lateral:category", "preferred_channel_starboard"));
				Main.main.undoRedo.add(new ChangePropertyCommand(node,
						"seamark:beacon_lateral:colour_pattern", "horizontal stripes"));
				if (getRegion() != SeaMark.IALA_B) {
					Main.main.undoRedo.add(new ChangePropertyCommand(node,
							"seamark:beacon_lateral:colour", "green;red;green"));
					colour = "green";
				} else {
					Main.main.undoRedo.add(new ChangePropertyCommand(node,
							"seamark:beacon_lateral:colour", "red;green;red"));
					colour = "red";
				}
				break;
			case LAT_FLOAT:
				Main.main.undoRedo.add(new ChangePropertyCommand(node,
						"seamark:light_float:colour_pattern", "horizontal stripes"));
				if (getRegion() != SeaMark.IALA_B) {
					Main.main.undoRedo.add(new ChangePropertyCommand(node,
							"seamark:light_float:colour", "green;red;green"));
					colour = "green";
				} else {
					Main.main.undoRedo.add(new ChangePropertyCommand(node,
							"seamark:light_float:colour", "red;green;red"));
					colour = "red";
				}
				break;
			}
			shape = "cone, point up";
			break;

		default:
		}

		saveTopMarkData(shape, colour);
		saveLightData(colour);

		Main.pref.put("tomsplugin.IALA", getRegion() ? "B" : "A");
	}

	public void setLightColour() {
		if (getRegion() == IALA_A
				&& (getBuoyIndex() == PORT_HAND || getBuoyIndex() == PREF_PORT_HAND)) {
			super.setLightColour("R");
		} else {
			super.setLightColour("G");
		}
	}

	public void setLightColour(String str) {
		int cat = getBuoyIndex();

		if (str == null) {
			return;
		}

		switch (cat) {
		case PORT_HAND:
		case PREF_PORT_HAND:
			if (getRegion() == IALA_A) {
				if (str.equals("red")) {
					setFired(true);
					super.setLightColour("R");
				} else {
					super.setLightColour("");
				}
			} else {
				if (str.equals("green")) {
					setFired(true);
					super.setLightColour("G");
				} else {
					super.setLightColour("");
				}
			}
			break;

		case STARBOARD_HAND:
		case PREF_STARBOARD_HAND:
			if (getRegion() == IALA_A) {
				if (str.equals("green")) {
					setFired(true);
					super.setLightColour("G");
				} else {
					super.setLightColour("");
				}
			} else {
				if (str.equals("red")) {
					setFired(true);
					super.setLightColour("R");
				} else {
					super.setLightColour("");
				}
			}
			break;

		default:
		}

	}

}