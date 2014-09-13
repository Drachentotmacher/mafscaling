/*
* Open-Source tuning tools
*
* This program is free software; you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation; either version 2 of the License, or
* (at your option) any later version.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License along
* with this program; if not, write to the Free Software Foundation, Inc.,
* 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
*/

package com.vgi.mafscaling;

import java.awt.Dimension;
import java.awt.Point;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

import org.apache.log4j.Logger;

public class Config {
    private static final Logger logger = Logger.getLogger(Config.class);
	public static final String DefaultWindowWidth = "300";
	public static final String DefaultWindowHeight = "200";
	public static final String DefaultWindowPositionX = "50";
	public static final String DefaultWindowPositionY = "50";
	public static final String DefaultCompareWindowWidth = "300";
	public static final String DefaultCompareWindowHeight = "300";
	public static final String DefaultCompareWindowPositionX = "50";
	public static final String DefaultCompareWindowPositionY = "50";
	public static final String DefaultClOlStatusValue = "-1";
	public static final String DefaultThrottleChangeMax = "2";
	public static final String DefaultIsLoadCompInRatio = "false";
	public static final String DefaultCLMinCellHitCount = "30";
	public static final String DefaultLCMinCellHitCount = "15";
	public static final String DefaultWOTStationaryPoint = "80";
	public static final String DefaultWBO2RowOffset = "0";
	public static final String DefaultOLCLTransitionSkipRows = "3";
	public static final String DefaultMafVMaximum = "5.0";
	public static final String DefaultMafVMinimum = "0.0";
	public static final String DefaultRPMMaximum = "5000";
	public static final String DefaultRPMMinimum = "0";
	public static final String DefaultIATMaximum = "110.0";
	public static final String DefaultLCIATMaximum = "110.0";
	public static final String DefaultWidebandAfrErrorPercent = "15.0";
	public static final String DefaultWOTEnrichment = "16.0";
	public static final String DefaultAfrMinimum = "13.7";
	public static final String DefaultAfrMaximum = "15.0";
	public static final String DefaultLCAfrMinimum = "14.2";
	public static final String DefaultLCAfrMaximum = "15.0";
	public static final String DefaultLoadMinimum = "0.2";
	public static final String DefaultDvDtMaximum = "0.7";
	public static final String DefaultLCDvDtMaximum = "0.7";
	private static final String CFG_FILE = "config.xml";
	public static final String NO_NAME = "#$#";
	private static Properties props = new Properties();

	public static String getProperty(String name) {
		return props.getProperty(name, "");
	}

	public static void setProperty(String name, String prop) {
		props.setProperty(name, prop);
	}

	public static void removeProperty(String name) {
		props.remove(name);
	}
	
	public static Dimension getWindowSize() {
		return new Dimension(Integer.parseInt(props.getProperty("WindowWidth", DefaultWindowWidth)), Integer.parseInt(props.getProperty("WindowHeight", DefaultWindowHeight)));
	}
	
	public static void setWindowSize(Dimension d) {
		props.setProperty("WindowWidth", Integer.toString(d.width));
		props.setProperty("WindowHeight", Integer.toString(d.height));
	}

	public static Point getWindowLocation() {
		return new Point(Integer.parseInt(props.getProperty("WindowPositionX", DefaultWindowPositionX)), Integer.parseInt(props.getProperty("WindowPositionY", DefaultWindowPositionY)));
	}

	public static void setWindowLocation(Point p) {
		props.setProperty("WindowPositionX", Integer.toString(p.x));
		props.setProperty("WindowPositionY", Integer.toString(p.y));
	}
	
	public static Dimension getCompWindowSize() {
		return new Dimension(Integer.parseInt(props.getProperty("CompareWindowWidth", DefaultCompareWindowWidth)), Integer.parseInt(props.getProperty("CompareWindowHeight", DefaultCompareWindowHeight)));
	}
	
	public static void setCompWindowSize(Dimension d) {
		props.setProperty("CompareWindowWidth", Integer.toString(d.width));
		props.setProperty("CompareWindowHeight", Integer.toString(d.height));
	}

	public static Point getCompWindowLocation() {
		return new Point(Integer.parseInt(props.getProperty("CompareWindowPositionX", DefaultCompareWindowPositionX)), Integer.parseInt(props.getProperty("CompareWindowPositionY", DefaultCompareWindowPositionY)));
	}

	public static void setCompWindowLocation(Point p) {
		props.setProperty("CompareWindowPositionX", Integer.toString(p.x));
		props.setProperty("CompareWindowPositionY", Integer.toString(p.y));
	}
	
	public static String getThrottleAngleColumnName() {
		return props.getProperty("ThrottleAngleColumnName", "#$#");
	}
	
	public static void setThrottleAngleColumnName(String name) {
		props.setProperty("ThrottleAngleColumnName", name);
	}

	public static String getMafVoltageColumnName() {
		return props.getProperty("MafVoltageColumnName", "#$#");
	}
	
	public static void setMafVoltageColumnName(String name) {
		props.setProperty("MafVoltageColumnName", name);
	}

	public static String getIatColumnName() {
		return props.getProperty("IATColumnName", "#$#");
	}
	
	public static void setIatColumnName(String name) {
		props.setProperty("IATColumnName", name);
	}

	public static String getWidebandAfrColumnName() {
		return props.getProperty("WidebandAfrColumnName", "#$#");
	}
	
	public static void setWidebandAfrColumnName(String name) {
		props.setProperty("WidebandAfrColumnName", name);
	}

	public static String getAfrColumnName() {
		return props.getProperty("AfrColumnName", "#$#");
	}
	
	public static void setAfrColumnName(String name) {
		props.setProperty("AfrColumnName", name);
	}

	public static String getMpColumnName() {
		return props.getProperty("MPColumnName", "#$#");
	}
	
	public static void setMpColumnName(String name) {
		props.setProperty("MPColumnName", name);
	}

	public static String getRpmColumnName() {
		return props.getProperty("RpmColumnName", "#$#");
	}
	
	public static void setRpmColumnName(String name) {
		props.setProperty("RpmColumnName", name);
	}

	public static String getLoadColumnName() {
		return props.getProperty("LoadColumnName", "#$#");
	}
	
	public static void setLoadColumnName(String name) {
		props.setProperty("LoadColumnName", name);
	}
	
	public static String getCommandedAfrColumnName() {
		return props.getProperty("CommandedAfrColumnName", "#$#");
	}
	
	public static void setCommandedAfrColumnName(String name) {
		props.setProperty("CommandedAfrColumnName", name);
	}

	public static String getTimeColumnName() {
		return props.getProperty("TimeColumnName", "#$#");
	}
	
	public static void setTimeColumnName(String name) {
		props.setProperty("TimeColumnName", name);
	}

	public static String getClOlStatusColumnName() {
		return props.getProperty("ClOlStatusColumnName", "#$#");
	}
	
	public static void setClOlStatusColumnName(String name) {
		props.setProperty("ClOlStatusColumnName", name);
	}

	public static String getAfLearningColumnName() {
		return props.getProperty("AfLearningColumnName", "#$#");
	}
	
	public static void setAfLearningColumnName(String name) {
		props.setProperty("AfLearningColumnName", name);
	}

	public static String getAfCorrectionColumnName() {
		return props.getProperty("AfCorrectionColumnName", "#$#");
	}
	
	public static void setAfCorrectionColumnName(String name) {
		props.setProperty("AfCorrectionColumnName", name);
	}

	public static int getClOlStatusValue() {
		return Integer.parseInt(props.getProperty("ClOlStatusValue", DefaultClOlStatusValue));
	}

	public static void setClOlStatusValue(int v) {
		props.setProperty("ClOlStatusValue", Integer.toString(v));
	}

	public static int getThrottleChangeMaxValue() {
		return Integer.parseInt(props.getProperty("ThrottleChangeMax", DefaultThrottleChangeMax));
	}

	public static void setThrottleChangeMaxValue(int v) {
		props.setProperty("ThrottleChangeMax", Integer.toString(v));
	}
	
	public static boolean getIsLoadCompInRatio() {
		return Boolean.parseBoolean(props.getProperty("IsLoadCompInRatio", DefaultIsLoadCompInRatio));
	}
	
	public static void setIsLoadCompInRatio(boolean v) {
		props.setProperty("IsLoadCompInRatio", Boolean.toString(v));
	}

	public static int getCLMinCellHitCount() {
		return Integer.parseInt(props.getProperty("CLMinCellHitCount", DefaultCLMinCellHitCount));
	}

	public static void setCLMinCellHitCount(int v) {
		props.setProperty("CLMinCellHitCount", Integer.toString(v));
	}

	public static int getLCMinCellHitCount() {
		return Integer.parseInt(props.getProperty("LCMinCellHitCount", DefaultLCMinCellHitCount));
	}

	public static void setLCMinCellHitCount(int v) {
		props.setProperty("LCMinCellHitCount", Integer.toString(v));
	}

	public static int getWOTStationaryPointValue() {
		return Integer.parseInt(props.getProperty("WOTStationaryPoint", DefaultWOTStationaryPoint));
	}

	public static void setWOTStationaryPointValue(int v) {
		props.setProperty("WOTStationaryPoint", Integer.toString(v));
	}

	public static int getWBO2RowOffset() {
		return Integer.parseInt(props.getProperty("WBO2RowOffset", DefaultWBO2RowOffset));
	}

	public static void setWBO2RowOffset(int v) {
		props.setProperty("WBO2RowOffset", Integer.toString(v));
	}

	public static int getOLCLTransitionSkipRows() {
		return Integer.parseInt(props.getProperty("OLCLTransitionSkipRows", DefaultOLCLTransitionSkipRows));
	}

	public static void setOLCLTransitionSkipRows(int v) {
		props.setProperty("OLCLTransitionSkipRows", Integer.toString(v));
	}

	public static double getMafVMaximumValue() {
		return Double.parseDouble(props.getProperty("MafVMaximum", DefaultMafVMaximum));
	}

	public static void setMafVMaximumValue(double v) {
		props.setProperty("MafVMaximum", Double.toString(v));
	}

	public static double getMafVMinimumValue() {
		return Double.parseDouble(props.getProperty("MafVMinimum", DefaultMafVMinimum));
	}

	public static void setMafVMinimumValue(double v) {
		props.setProperty("MafVMinimum", Double.toString(v));
	}

	public static int getRPMMaximumValue() {
		return Integer.parseInt(props.getProperty("RPMMaximum", DefaultRPMMaximum));
	}

	public static void setRPMMaximumValue(int v) {
		props.setProperty("RPMMaximum", Integer.toString(v));
	}

	public static int getRPMMinimumValue() {
		return Integer.parseInt(props.getProperty("RPMMinimum", DefaultRPMMinimum));
	}

	public static void setRPMMinimumValue(int v) {
		props.setProperty("RPMMinimum", Integer.toString(v));
	}

	public static double getIatMaximumValue() {
		return Double.parseDouble(props.getProperty("IATMaximum", DefaultIATMaximum));
	}

	public static void setIatMaximumValue(double v) {
		props.setProperty("IATMaximum", Double.toString(v));
	}
	
	public static double getLCIatMaximumValue() {
		return Double.parseDouble(props.getProperty("LCIATMaximum", DefaultLCIATMaximum));
	}

	public static void setLCIatMaximumValue(double v) {
		props.setProperty("LCIATMaximum", Double.toString(v));
	}

	public static double getWidebandAfrErrorPercentValue() {
		return Double.parseDouble(props.getProperty("WidebandAfrErrorPercent", DefaultWidebandAfrErrorPercent));
	}
	
	public static void setWidebandAfrErrorPercentValue(double v) {
		props.setProperty("WidebandAfrErrorPercent", Double.toString(v));
	}
	
	public static double getWOTEnrichmentValue() {
		return Double.parseDouble(props.getProperty("WOTEnrichment", DefaultWOTEnrichment));
	}
	
	public static void setWOTEnrichmentValue(double v) {
		props.setProperty("WOTEnrichment", Double.toString(v));
	}

	public static double getAfrMinimumValue() {
		return Double.parseDouble(props.getProperty("AfrMinimum", DefaultAfrMinimum));
	}

	public static void setAfrMinimumValue(double v) {
		props.setProperty("AfrMinimum", Double.toString(v));
	}

	public static double getAfrMaximumValue() {
		return Double.parseDouble(props.getProperty("AfrMaximum", DefaultAfrMaximum));
	}

	public static void setAfrMaximumValue(double v) {
		props.setProperty("AfrMaximum", Double.toString(v));
	}
	
	public static double getLCAfrMinimumValue() {
		return Double.parseDouble(props.getProperty("LCAfrMinimum", DefaultLCAfrMinimum));
	}

	public static void setLCAfrMinimumValue(double v) {
		props.setProperty("LCAfrMinimum", Double.toString(v));
	}

	public static double getLCAfrMaximumValue() {
		return Double.parseDouble(props.getProperty("LCAfrMaximum", DefaultLCAfrMaximum));
	}

	public static void setLCAfrMaximumValue(double v) {
		props.setProperty("LCAfrMaximum", Double.toString(v));
	}
	
	public static double getLoadMinimumValue() {
		return Double.parseDouble(props.getProperty("LoadMinimum", DefaultLoadMinimum));
	}

	public static void setLoadMinimumValue(double v) {
		props.setProperty("LoadMinimum", Double.toString(v));
	}

	public static double getDvDtMaximumValue() {
		return Double.parseDouble(props.getProperty("DvDtMaximum", DefaultDvDtMaximum));
	}

	public static void setDvDtMaximumValue(double v) {
		props.setProperty("DvDtMaximum", Double.toString(v));
	}

	public static double getLCDvDtMaximumValue() {
		return Double.parseDouble(props.getProperty("LCDvDtMaximum", DefaultLCDvDtMaximum));
	}

	public static void setLCDvDtMaximumValue(double v) {
		props.setProperty("LCDvDtMaximum", Double.toString(v));
	}

	public static String getDefaultPOLFueling() {
		return props.getProperty("DefaultPOLFueling", "");
	}

	public static void setDefaultPOLFueling(String s) {
		props.setProperty("DefaultPOLFueling", s);
	}

	public static String getPOLFuelingFiles() {
		return props.getProperty("POLFuelingFiles", "");
	}

	public static void setPOLFuelingFiles(String s) {
		props.setProperty("POLFuelingFiles", s);
	}

	public static String getXAxisTemplates() {
		return props.getProperty("XAxisTemplates", ",");
	}

	public static void setXAxisTemplates(String s) {
		props.setProperty("XAxisTemplates", s);
	}

	public static String getYAxisTemplates() {
		return props.getProperty("YAxisTemplates", ",");
	}

	public static void setYAxisTemplates(String s) {
		props.setProperty("YAxisTemplates", s);
	}
	
	public static void load() {
		InputStream is = null;
		try {
			is = new FileInputStream(new File(CFG_FILE));
			props.loadFromXML(is);
		}
		catch (Exception e) {
            logger.error(e);
		}
		finally {
			if (is != null) {
				try {
					is.close();
				}
				catch (IOException e) {
		            logger.error(e);
				}
			}
		}
	}
	
	public static void save() {
		OutputStream os = null;
		try {
			os = new FileOutputStream(new File(CFG_FILE));
			props.storeToXML(os, "settings");
		}
		catch (Exception e) {
            logger.error(e);
		}
		finally {
			if (os != null) {
				try {
					os.close();
				}
				catch (IOException e) {
		            logger.error(e);
				}
			}
		}
	}

}
