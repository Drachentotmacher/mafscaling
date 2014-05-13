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

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.Format;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.TreeMap;
import java.util.regex.Pattern;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.ListSelectionModel;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import org.apache.log4j.Logger;
import org.math.plot.Plot3DPanel;

public class LogStats extends JTabbedPane implements ActionListener {
	private enum Statistics {COUNT, MINIMUM, MAXIMUM, MEAN, MEDIAN, MODE, RANGE, VARIANCE, STDDEV};
	private enum Plot3D {GRID, BAR, LINE, SCATTER};
	private enum DataFilter {NONE, LESS, EQUAL, GREATER};
	private static final long serialVersionUID = -7486851151646396168L;
	private static final Logger logger = Logger.getLogger(LogStats.class);
    private static final int ColumnWidth = 50;
    private static final int DataTableRowCount = 50;
    private static final int DataTableColumnCount = 25;

    private JFileChooser fileChooser = new JFileChooser();
    private File logFile = null;
    private JComboBox<String> xAxisColumn = null;
    private JComboBox<String> yAxisColumn = null;
    private JComboBox<String> dataColumn = null;
    private JComboBox<String> statistics = null;
    private JComboBox<String> filters = null;
    private JFormattedTextField xAxisRoundTextBox = null;
    private JFormattedTextField yAxisRoundTextBox = null;
    private JTable dataTable = null;
    private ExcelAdapter excelAdapter = null;
    private HashMap<Double, HashMap<Double, ArrayList<Double>>> xData = null;
    private Plot3DPanel plot = null;
    private ButtonGroup rbGroup = new ButtonGroup();
    private JRadioButton rbGridPlot = null;
    private JRadioButton rbBarPlot = null;
    private JRadioButton rbLinePlot = null;
    private JRadioButton rbScatterPlot = null;
    private ArrayList<Double> xAxisArray;
    private ArrayList<Double> yAxisArray;
    private DataFilter dataFilterType = DataFilter.NONE;
    private double dataFilter = Double.NaN;
    private int dataRounding = 0;

	public LogStats(int tabPlacement) {
        super(tabPlacement);
        initialize();
    }

    private void initialize() {
        fileChooser.setCurrentDirectory(new File("."));
        excelAdapter = new ExcelAdapter();
        xAxisArray = new ArrayList<Double>();
        yAxisArray = new ArrayList<Double>();
        createDataTab();
        createGraghTab();
        createUsageTab();
    }

    //////////////////////////////////////////////////////////////////////////////////////
    // DATA TAB
    //////////////////////////////////////////////////////////////////////////////////////
    
    private void createDataTab() {
        JPanel dataPanel = new JPanel();
        add(dataPanel, "<html><div style='text-align: center;'>D<br>a<br>t<br>a</div></html>");
        GridBagLayout gbl_dataPanel = new GridBagLayout();
        gbl_dataPanel.columnWidths = new int[] {0};
        gbl_dataPanel.rowHeights = new int[] {0, 0};
        gbl_dataPanel.columnWeights = new double[]{0.0};
        gbl_dataPanel.rowWeights = new double[]{0.0, 1.0};
        dataPanel.setLayout(gbl_dataPanel);

        createControlPanel(dataPanel);
        createDataPanel(dataPanel);
    }
    
    private void createControlPanel(JPanel dataPanel) {        
        NumberFormat doubleFmt = NumberFormat.getNumberInstance();
        doubleFmt.setGroupingUsed(false);
        doubleFmt.setMaximumFractionDigits(2);
        doubleFmt.setMinimumFractionDigits(1);
        doubleFmt.setRoundingMode(RoundingMode.HALF_UP);
        
        JPanel cntlPanel = new JPanel();
        GridBagConstraints gbc_ctrlPanel = new GridBagConstraints();
        gbc_ctrlPanel.insets = new Insets(3, 3, 3, 3);
        gbc_ctrlPanel.anchor = GridBagConstraints.PAGE_START;
        gbc_ctrlPanel.fill = GridBagConstraints.HORIZONTAL;
        gbc_ctrlPanel.weightx = 1.0;
        gbc_ctrlPanel.gridx = 0;
        gbc_ctrlPanel.gridy = 0;
        dataPanel.add(cntlPanel, gbc_ctrlPanel);
        
        GridBagLayout gbl_cntlPanel = new GridBagLayout();
        gbl_cntlPanel.columnWidths = new int[]{0, 0, 0, 0, 0, 0, 0, 0};
        gbl_cntlPanel.rowHeights = new int[]{0, 0, 0};
        gbl_cntlPanel.columnWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0};
        gbl_cntlPanel.rowWeights = new double[]{0.0, 0.0, 0.0};
        cntlPanel.setLayout(gbl_cntlPanel);
        
        JButton selectLogButton = new JButton("Select Log");
        GridBagConstraints gbc_selectLogButton = new GridBagConstraints();
        gbc_selectLogButton.anchor = GridBagConstraints.PAGE_START;
        gbc_selectLogButton.insets = new Insets(3, 3, 3, 3);
        gbc_selectLogButton.gridx = 0;
        gbc_selectLogButton.gridy = 0;
        gbc_selectLogButton.gridheight = 3;
        selectLogButton.setActionCommand("selectlog");
        selectLogButton.addActionListener(this);
        cntlPanel.add(selectLogButton, gbc_selectLogButton);

        JLabel xAxisLabel = new JLabel("X-Axis");
        xAxisLabel.setHorizontalAlignment(LEFT);
        GridBagConstraints gbc_xAxisLabel = new GridBagConstraints();
        gbc_xAxisLabel.anchor = GridBagConstraints.EAST;
        gbc_xAxisLabel.insets = new Insets(3, 3, 3, 0);
        gbc_xAxisLabel.gridx = 1;
        gbc_xAxisLabel.gridy = 0;
        cntlPanel.add(xAxisLabel, gbc_xAxisLabel);
        
        xAxisColumn = new JComboBox<String>();
        GridBagConstraints gbc_xAxisColumn = new GridBagConstraints();
        gbc_xAxisColumn.anchor = GridBagConstraints.PAGE_START;
        gbc_xAxisColumn.insets = new Insets(3, 3, 3, 3);
        gbc_xAxisColumn.gridx = 2;
        gbc_xAxisColumn.gridy = 0;
        xAxisColumn.setPrototypeDisplayValue("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
        cntlPanel.add(xAxisColumn, gbc_xAxisColumn);

        JLabel xAxisScalingLabel = new JLabel("X-Axis Step");
        xAxisScalingLabel.setHorizontalAlignment(LEFT);
        GridBagConstraints gbc_xAxisScalingLabel = new GridBagConstraints();
        gbc_xAxisScalingLabel.anchor = GridBagConstraints.EAST;
        gbc_xAxisScalingLabel.insets = new Insets(3, 3, 3, 0);
        gbc_xAxisScalingLabel.gridx = 3;
        gbc_xAxisScalingLabel.gridy = 0;
        cntlPanel.add(xAxisScalingLabel, gbc_xAxisScalingLabel);
        
        xAxisRoundTextBox = new JFormattedTextField(doubleFmt);
        xAxisRoundTextBox.setPreferredSize(new Dimension(65, 20));
        GridBagConstraints gbc_xAxisRoundTextBox = new GridBagConstraints();
        gbc_xAxisRoundTextBox.anchor = GridBagConstraints.PAGE_START;
        gbc_xAxisRoundTextBox.fill = GridBagConstraints.HORIZONTAL;
        gbc_xAxisRoundTextBox.insets = new Insets(3, 3, 3, 3);
        gbc_xAxisRoundTextBox.gridx = 4;
        gbc_xAxisRoundTextBox.gridy = 0;
        cntlPanel.add(xAxisRoundTextBox, gbc_xAxisRoundTextBox);

        JLabel yAxisLabel = new JLabel("Y-Axis");
        yAxisLabel.setHorizontalAlignment(LEFT);
        GridBagConstraints gbc_yAxisLabel = new GridBagConstraints();
        gbc_yAxisLabel.anchor = GridBagConstraints.EAST;
        gbc_yAxisLabel.insets = new Insets(3, 3, 3, 0);
        gbc_yAxisLabel.gridx = 1;
        gbc_yAxisLabel.gridy = 1;
        cntlPanel.add(yAxisLabel, gbc_yAxisLabel);
        
        yAxisColumn = new JComboBox<String>();
        GridBagConstraints gbc_yAxisColumn = new GridBagConstraints();
        gbc_yAxisColumn.anchor = GridBagConstraints.PAGE_START;
        gbc_yAxisColumn.insets = new Insets(3, 3, 3, 3);
        gbc_yAxisColumn.gridx = 2;
        gbc_yAxisColumn.gridy = 1;
        yAxisColumn.setPrototypeDisplayValue("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
        cntlPanel.add(yAxisColumn, gbc_yAxisColumn);

        JLabel yAxisScalingLabel = new JLabel("Y-Axis Step");
        yAxisScalingLabel.setHorizontalAlignment(LEFT);
        GridBagConstraints gbc_yAxisScalingLabel = new GridBagConstraints();
        gbc_yAxisScalingLabel.anchor = GridBagConstraints.EAST;
        gbc_yAxisScalingLabel.insets = new Insets(3, 3, 3, 0);
        gbc_yAxisScalingLabel.gridx = 3;
        gbc_yAxisScalingLabel.gridy = 1;
        cntlPanel.add(yAxisScalingLabel, gbc_yAxisScalingLabel);
        
        yAxisRoundTextBox = new JFormattedTextField(doubleFmt);
        yAxisRoundTextBox.setPreferredSize(new Dimension(65, 20));
        GridBagConstraints gbc_yAxisRoundTextBox = new GridBagConstraints();
        gbc_yAxisRoundTextBox.anchor = GridBagConstraints.PAGE_START;
        gbc_yAxisRoundTextBox.fill = GridBagConstraints.HORIZONTAL;
        gbc_yAxisRoundTextBox.insets = new Insets(3, 3, 3, 3);
        gbc_yAxisRoundTextBox.gridx = 4;
        gbc_yAxisRoundTextBox.gridy = 1;
        cntlPanel.add(yAxisRoundTextBox, gbc_yAxisRoundTextBox);

        JLabel datasLabel = new JLabel("Data");
        datasLabel.setHorizontalAlignment(LEFT);
        GridBagConstraints gbc_datasLabel = new GridBagConstraints();
        gbc_datasLabel.anchor = GridBagConstraints.EAST;
        gbc_datasLabel.insets = new Insets(3, 3, 3, 0);
        gbc_datasLabel.gridx = 1;
        gbc_datasLabel.gridy = 2;
        cntlPanel.add(datasLabel, gbc_datasLabel);
        
        dataColumn = new JComboBox<String>();
        GridBagConstraints gbc_dataColumn = new GridBagConstraints();
        gbc_dataColumn.anchor = GridBagConstraints.PAGE_START;
        gbc_dataColumn.insets = new Insets(3, 3, 3, 3);
        gbc_dataColumn.gridx = 2;
        gbc_dataColumn.gridy = 2;
        dataColumn.setPrototypeDisplayValue("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
        cntlPanel.add(dataColumn, gbc_dataColumn);

        JLabel statisticsLabel = new JLabel("Statistics");
        statisticsLabel.setHorizontalAlignment(LEFT);
        GridBagConstraints gbc_statisticsLabel = new GridBagConstraints();
        gbc_statisticsLabel.anchor = GridBagConstraints.EAST;
        gbc_statisticsLabel.insets = new Insets(3, 3, 3, 0);
        gbc_statisticsLabel.gridx = 3;
        gbc_statisticsLabel.gridy = 2;
        cntlPanel.add(statisticsLabel, gbc_statisticsLabel);
        
        statistics = new JComboBox<String>(new String[] {"Count", "Minimum", "Maximum", "Mean", "Median", "Mode", "Range", "Variance", "Std Deviation"});
        GridBagConstraints gbc_statistics = new GridBagConstraints();
        gbc_statistics.anchor = GridBagConstraints.PAGE_START;
        gbc_statistics.insets = new Insets(3, 3, 3, 3);
        gbc_statistics.gridx = 4;
        gbc_statistics.gridy = 2;
        cntlPanel.add(statistics, gbc_statistics);

        JLabel orLabel = new JLabel("or");
        statisticsLabel.setHorizontalAlignment(LEFT);
        GridBagConstraints gbc_orLabel = new GridBagConstraints();
        gbc_orLabel.anchor = GridBagConstraints.CENTER;
        gbc_orLabel.insets = new Insets(3, 3, 3, 0);
        gbc_orLabel.gridx = 5;
        gbc_orLabel.gridy = 0;
        gbc_orLabel.gridheight = 2;
        cntlPanel.add(orLabel, gbc_orLabel);

        JButton btnSetAxisButton = new JButton("Set Axis");
        GridBagConstraints gbc_btnSetAxisButton = new GridBagConstraints();
        gbc_btnSetAxisButton.anchor = GridBagConstraints.CENTER;
        gbc_btnSetAxisButton.insets = new Insets(3, 3, 3, 3);
        gbc_btnSetAxisButton.gridx = 6;
        gbc_btnSetAxisButton.gridy = 0;
        gbc_btnSetAxisButton.gridheight = 2;
        btnSetAxisButton.setActionCommand("setaxis");
        btnSetAxisButton.addActionListener(this);
        cntlPanel.add(btnSetAxisButton, gbc_btnSetAxisButton);
        
        filters = new JComboBox<String>(new String[] {"", "Less", "Equal", "Greater"});
        GridBagConstraints gbc_filters = new GridBagConstraints();
        gbc_filters.anchor = GridBagConstraints.PAGE_START;
        gbc_filters.fill = GridBagConstraints.HORIZONTAL;
        gbc_filters.insets = new Insets(3, 3, 3, 3);
        gbc_filters.gridx = 6;
        gbc_filters.gridy = 2;
        filters.setActionCommand("filter");
        filters.addActionListener(this);
        cntlPanel.add(filters, gbc_filters);

        JButton btnGoButton = new JButton("GO");
        GridBagConstraints gbc_btnGoButton = new GridBagConstraints();
        gbc_btnGoButton.anchor = GridBagConstraints.EAST;
        gbc_btnGoButton.insets = new Insets(3, 3, 3, 3);
        gbc_btnGoButton.weightx = 1.0;
        gbc_btnGoButton.gridx = 7;
        gbc_btnGoButton.gridy = 0;
        gbc_btnGoButton.gridheight = 3;
        btnGoButton.setActionCommand("go");
        btnGoButton.addActionListener(this);
        cntlPanel.add(btnGoButton, gbc_btnGoButton);
    }
    
    private void createDataPanel(JPanel dataPanel) {
        TableColumnModel dataTableModel = new DefaultTableColumnModel();
        dataTableModel.addColumn(new TableColumn(0, 250));
        
        dataTable = new JTable() {
            private static final long serialVersionUID = 6526901361175099297L;
            public boolean isCellEditable(int row, int column) { return false; };
        };
        dataTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        dataTable.setColumnSelectionAllowed(true);
        dataTable.setCellSelectionEnabled(true);
        dataTable.setBorder(new LineBorder(new Color(0, 0, 0)));
        dataTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF); 
        dataTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        dataTable.setModel(new DefaultTableModel(DataTableRowCount, DataTableColumnCount));
        dataTable.setTableHeader(null);
        Utils.initializeTable(dataTable, ColumnWidth);
        
        Format[][] formatMatrix = { { new DecimalFormat("0.00"), new DecimalFormat("0.00") } };
        NumberFormatRenderer renderer = (NumberFormatRenderer)dataTable.getDefaultRenderer(Object.class);
        renderer.setFormats(formatMatrix);
        
        GridBagConstraints gbc_dataTable = new GridBagConstraints();
        gbc_dataTable.insets = new Insets(3, 3, 3, 3);
        gbc_dataTable.anchor = GridBagConstraints.PAGE_START;
        gbc_dataTable.fill = GridBagConstraints.BOTH;
        gbc_dataTable.weightx = 1.0;
        gbc_dataTable.weighty = 1.0;
        gbc_dataTable.gridx = 0;
        gbc_dataTable.gridy = 1;
        gbc_dataTable.gridwidth = 14;

        JScrollPane scrollPane = new JScrollPane(dataTable);
        dataPanel.add(scrollPane, gbc_dataTable);
        excelAdapter.addTable(dataTable, false, true, true, true, true, true, true, true, true);
    }

    //////////////////////////////////////////////////////////////////////////////////////
    // CREATE CHART TAB
    //////////////////////////////////////////////////////////////////////////////////////
    
    private void createGraghTab() {
        JPanel plotPanel = new JPanel();
        add(plotPanel, "<html><div style='text-align: center;'>C<br>h<br>a<br>r<br>t</div></html>");
        GridBagLayout gbl_plotPanel = new GridBagLayout();
        gbl_plotPanel.columnWidths = new int[] {0};
        gbl_plotPanel.rowHeights = new int[] {0, 0};
        gbl_plotPanel.columnWeights = new double[]{1.0};
        gbl_plotPanel.rowWeights = new double[]{0.0, 1.0};
        plotPanel.setLayout(gbl_plotPanel);

        JPanel cntlPanel = new JPanel();
        GridBagConstraints gbl_ctrlPanel = new GridBagConstraints();
        gbl_ctrlPanel.insets = new Insets(3, 3, 3, 3);
        gbl_ctrlPanel.anchor = GridBagConstraints.NORTH;
        gbl_ctrlPanel.weightx = 1.0;
        gbl_ctrlPanel.fill = GridBagConstraints.HORIZONTAL;
        gbl_ctrlPanel.gridx = 0;
        gbl_ctrlPanel.gridy = 0;
        plotPanel.add(cntlPanel, gbl_ctrlPanel);
        
        GridBagLayout gbl_cntlPanel = new GridBagLayout();
        gbl_cntlPanel.columnWidths = new int[]{0, 0, 0, 0, 0};
        gbl_cntlPanel.rowHeights = new int[]{0};
        gbl_cntlPanel.columnWeights = new double[]{0.0, 0.0, 0.0, 0.0, 1.0};
        gbl_cntlPanel.rowWeights = new double[]{0};
        cntlPanel.setLayout(gbl_cntlPanel);

        rbGridPlot = new JRadioButton("Grid");
        GridBagConstraints gbc_rbGridPlot = new GridBagConstraints();
        gbc_rbGridPlot.anchor = GridBagConstraints.WEST;
        gbc_rbGridPlot.insets = new Insets(0, 0, 3, 3);
        gbc_rbGridPlot.gridx = 0;
        gbc_rbGridPlot.gridy = 0;
        rbGridPlot.setActionCommand("grid");
        rbGridPlot.addActionListener(this);
        rbGroup.add(rbGridPlot);
        cntlPanel.add(rbGridPlot, gbc_rbGridPlot);
        
        rbBarPlot = new JRadioButton("Bar");
        GridBagConstraints gbc_rbBarPlot = new GridBagConstraints();
        gbc_rbBarPlot.anchor = GridBagConstraints.WEST;
        gbc_rbBarPlot.insets = new Insets(0, 0, 3, 3);
        gbc_rbBarPlot.gridx = 1;
        gbc_rbBarPlot.gridy = 0;
        rbBarPlot.setActionCommand("bar");
        rbBarPlot.addActionListener(this);
        rbGroup.add(rbBarPlot);
        cntlPanel.add(rbBarPlot, gbc_rbBarPlot);
        
        rbLinePlot = new JRadioButton("Line");
        GridBagConstraints gbc_rbLinePlot = new GridBagConstraints();
        gbc_rbLinePlot.anchor = GridBagConstraints.WEST;
        gbc_rbLinePlot.insets = new Insets(0, 0, 3, 3);
        gbc_rbLinePlot.gridx = 2;
        gbc_rbLinePlot.gridy = 0;
        rbLinePlot.setActionCommand("line");
        rbLinePlot.addActionListener(this);
        rbGroup.add(rbLinePlot);
        cntlPanel.add(rbLinePlot, gbc_rbLinePlot);
        
        rbScatterPlot = new JRadioButton("Scatter");
        GridBagConstraints gbc_rbScatterPlot = new GridBagConstraints();
        gbc_rbScatterPlot.anchor = GridBagConstraints.WEST;
        gbc_rbScatterPlot.insets = new Insets(0, 0, 3, 3);
        gbc_rbScatterPlot.gridx = 3;
        gbc_rbScatterPlot.gridy = 0;
        rbScatterPlot.setActionCommand("scatter");
        rbScatterPlot.addActionListener(this);
        rbGroup.add(rbScatterPlot);
        cntlPanel.add(rbScatterPlot, gbc_rbScatterPlot);
        
        plot = new Plot3DPanel("SOUTH") {
			private static final long serialVersionUID = 7914951068593204419L;
			public void addPlotToolBar(String location) {
				super.addPlotToolBar(location);
        		super.plotToolBar.remove(4);
        		super.plotToolBar.remove(5);
        	}        	
        };
        plot.setAutoBounds();
        plot.setAutoscrolls(true);
        plot.setEditable(false);
        plot.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        plot.setForeground(Color.BLACK);
        plot.getAxis(0).setColor(Color.BLACK);
        plot.getAxis(1).setColor(Color.BLACK);
        plot.getAxis(2).setColor(Color.BLACK);
        
        GridBagConstraints gbl_chartPanel = new GridBagConstraints();
        gbl_chartPanel.anchor = GridBagConstraints.CENTER;
        gbl_chartPanel.insets = new Insets(3, 3, 3, 3);
        gbl_chartPanel.weightx = 1.0;
        gbl_chartPanel.weighty = 1.0;
        gbl_chartPanel.fill = GridBagConstraints.BOTH;
        gbl_chartPanel.gridx = 0;
        gbl_chartPanel.gridy = 1;
        plotPanel.add(plot, gbl_chartPanel);
    }

    //////////////////////////////////////////////////////////////////////////////////////
    // CREATE USAGE TAB
    //////////////////////////////////////////////////////////////////////////////////////
    
    private void createUsageTab() {
        JTextPane  usageTextArea = new JTextPane();
        usageTextArea.setMargin(new Insets(10, 10, 10, 10));
        usageTextArea.setContentType("text/html");
        usageTextArea.setText(usage());
        usageTextArea.setEditable(false);
        usageTextArea.setCaretPosition(0);

        JScrollPane textScrollPane = new JScrollPane(usageTextArea);
        textScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        textScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        add(textScrollPane, "<html><div style='text-align: center;'>U<br>s<br>a<br>g<br>e</div></html>");
    }
    
    private String usage() {
        ResourceBundle bundle;
        bundle = ResourceBundle.getBundle("com.vgi.mafscaling.logstats");
        return bundle.getString("usage"); 
    }

    //////////////////////////////////////////////////////////////////////////////////////
    // WORK FUNCTIONS
    //////////////////////////////////////////////////////////////////////////////////////
    
    private void setAxis() {
    	new LogStatsFixedAxis(xAxisArray, yAxisArray);
    	if (xAxisArray.size() > 0)
    		xAxisRoundTextBox.setValue(null);
    	if (yAxisArray.size() > 0)
    		yAxisRoundTextBox.setValue(null);
    }

    private void getLogColumns() {
        if (JFileChooser.APPROVE_OPTION != fileChooser.showOpenDialog(this))
            return;
        xAxisColumn.removeAllItems();
        yAxisColumn.removeAllItems();
        dataColumn.removeAllItems();
        logFile = fileChooser.getSelectedFile();
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(logFile.getAbsoluteFile()));
            String line = br.readLine();
            if (line != null) {
                String [] elements = line.split(",", -1);
                for (String item : elements) {
	                xAxisColumn.addItem(item);
	                yAxisColumn.addItem(item);
	                dataColumn.addItem(item);
                }
            }
        }
        catch (Exception e) {
            logger.error(e);
            JOptionPane.showMessageDialog(null, e, "Error opening file", JOptionPane.ERROR_MESSAGE);
        }
        finally {
        	if (br != null) {
                try {
                    br.close();
                }
                catch (IOException e) {
                    logger.error(e);
                }
        	}
        }
    }
    
    private Statistics getStatId() {
    	if (statistics.getSelectedItem() == null)
    		return Statistics.MEAN;
    	String name = (String)statistics.getSelectedItem();
    	if ("Count".equals(name))
    		return Statistics.COUNT;
    	if ("Minimum".equals(name))
    		return Statistics.MINIMUM;
    	if ("Maximum".equals(name))
    		return Statistics.MAXIMUM;
    	if ("Mean".equals(name))
    		return Statistics.MEAN;
    	if ("Median".equals(name))
    		return Statistics.MEDIAN;
    	if ("Mode".equals(name))
    		return Statistics.MODE;
    	if ("Range".equals(name))
    		return Statistics.RANGE;
    	if ("Variance".equals(name))
    		return Statistics.VARIANCE;
    	if ("Std Deviation".equals(name))
    		return Statistics.STDDEV;
		return Statistics.MEAN;
    }
    
    private DataFilter getType() {
    	if (filters.getSelectedItem() == null)
    		return DataFilter.NONE;
    	String name = (String)filters.getSelectedItem();
    	if ("Less".equals(name))
    		return DataFilter.LESS;
    	if ("Equal".equals(name))
    		return DataFilter.EQUAL;
    	if ("Greater".equals(name))
    		return DataFilter.GREATER;
    	return DataFilter.NONE;
    }

    private void processLog() {
    	Utils.clearTable(dataTable);
    	if (xAxisColumn.getSelectedItem() == null || yAxisColumn.getSelectedItem() == null || dataColumn.getSelectedItem() == null)
    		return;
    	if (xAxisRoundTextBox.getValue() == null && xAxisArray.size() == 0) {
    		JOptionPane.showMessageDialog(null, "X-Axis scaling is not set. Please set 'Step' or X-Axis values.", "Error", JOptionPane.ERROR_MESSAGE);
    		return;
    	}
    	if (yAxisRoundTextBox.getValue() == null && yAxisArray.size() == 0) {
    		JOptionPane.showMessageDialog(null, "Y-Axis scaling is not set. Please set 'Step' or Y-Axis values.", "Error", JOptionPane.ERROR_MESSAGE);
    		return;
    	}
    	double xRound = Double.NaN;
    	if (xAxisRoundTextBox.getValue() != null) {
    		xRound = (((Number)xAxisRoundTextBox.getValue()).doubleValue());
        	if (xRound < 0.01) {
        		JOptionPane.showMessageDialog(null, "Incorrect X-Axis scaling, minimum allowed is 0.01", "Error", JOptionPane.ERROR_MESSAGE);
        		return;
        	}
    	}
    	double yRound = Double.NaN;
    	if (yAxisRoundTextBox.getValue() != null) {
    		yRound = (((Number)yAxisRoundTextBox.getValue()).doubleValue());
	    	if (yRound < 0.01) {
	    		JOptionPane.showMessageDialog(null, "Incorrect Y-Axis scaling, minimum allowed is 0.01", "Error", JOptionPane.ERROR_MESSAGE);
	    		return;
	    	}
    	}
        setCursor(new Cursor(Cursor.WAIT_CURSOR));
    	Statistics statid = getStatId();
    	String xAxisColName = (String)xAxisColumn.getSelectedItem();
    	String yAxisColName = (String)yAxisColumn.getSelectedItem();
    	String dataColName = (String)dataColumn.getSelectedItem();

        BufferedReader br = null;
        try {
        	String [] elements;
            br = new BufferedReader(new FileReader(logFile.getAbsoluteFile()));
            String line = br.readLine();
            if (line != null) {
            	elements = line.split(",", -1);
                ArrayList<String> columns = new ArrayList<String>(Arrays.asList(elements));
                int xColIdx = columns.indexOf(xAxisColName);
                int yColIdx = columns.indexOf(yAxisColName);
                int vColIdx = columns.indexOf(dataColName);
                double x, y, val, roundedVal;
                int i = 2;
                // data struct where first hash set is X-Axis containing second hash set which is Y-Axis containing array of values
                xData = new HashMap<Double, HashMap<Double, ArrayList<Double>>>();
                HashMap<Double, ArrayList<Double>> yData;
                ArrayList<Double> data;
                try {
                	line = br.readLine();
                	boolean proceed = true;
	                while (line != null) {
	                	elements = line.split(",", -1);
	                    try {
	                    	proceed = true;
	                    	if (Double.isNaN(xRound))
	                    		x = xAxisArray.get(Utils.closestValueIndex(Double.valueOf(elements[xColIdx]), xAxisArray));
	                    	else
	                    		x = Utils.round(Double.valueOf(elements[xColIdx]), xRound);
	                    	if (Double.isNaN(yRound))
	                    		y = yAxisArray.get(Utils.closestValueIndex(Double.valueOf(elements[yColIdx]), yAxisArray));
	                    	else
	                    		y = Utils.round(Double.valueOf(elements[yColIdx]), yRound);
	                        val = Double.valueOf(elements[vColIdx]);
	                        if (dataFilterType != DataFilter.NONE && !Double.isNaN(dataFilter)) {
	                        	switch (dataFilterType) {
	                        	case LESS:
	                        		if (val > dataFilter)
	                        			proceed = false;
	                        		break;
	                        	case EQUAL:
		                        	roundedVal = val;
		                        	if (dataRounding > 0)
		                        		roundedVal = Math.round(val * dataRounding * 10.0) / (dataRounding * 10.0);
		                        	else
		                        		roundedVal = Math.round(val);
	                        		if (roundedVal != dataFilter)
	                        			proceed = false;
	                        		break;
	                        	case GREATER:
	                        		if (val < dataFilter)
	                        			proceed = false;
	                        		break;
	                        	default:
                        			proceed = true;
	                        	}
	                        }
	                        if (proceed) {
		                        yData = xData.get(x);
		                        if (yData == null) {
		                        	yData = new HashMap<Double, ArrayList<Double>>();
		                        	xData.put(x, yData);
		                        }
		                        data = yData.get(y);
		                        if (data == null) {
		                        	data = new ArrayList<Double>();
		                        	yData.put(y, data);
		                        }
		                        data.add(val);
	                        }
	                    }
	                    catch (NumberFormatException e) {
	                        logger.error(e);
	                        JOptionPane.showMessageDialog(null, "Error parsing number at line " + i + ": " + e, "Error processing file", JOptionPane.ERROR_MESSAGE);
	                        return;
	                    }
	                    line = br.readLine();
	                    i += 1;
	                }
	                processData(statid);
                }
                catch (Exception e) {
                    logger.error(e);
                    JOptionPane.showMessageDialog(null, e, "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
        catch (Exception e) {
            logger.error(e);
            JOptionPane.showMessageDialog(null, e, "Error opening file", JOptionPane.ERROR_MESSAGE);
        }
        finally {
            setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        	if (br != null) {
                try {
                    br.close();
                }
                catch (IOException e) {
                    logger.error(e);
                }
        	}
        }
    }
    
    public void processData(Statistics id) {
        HashMap<Double, ArrayList<Double>> yData;
        ArrayList<Double> data;
        try {
	        TreeMap<Double, Integer> xAxisMap = new TreeMap<Double, Integer>();
	        TreeMap<Double, Integer> yAxisMap = new TreeMap<Double, Integer>();
	        for (Map.Entry<Double, HashMap<Double, ArrayList<Double>>> xentry : xData.entrySet()) {
	        	xAxisMap.put(xentry.getKey(), 0);
	        	for (Map.Entry<Double, ArrayList<Double>> yentry : xentry.getValue().entrySet())
	        		yAxisMap.put(yentry.getKey(), 0);
	        }
	        Utils.ensureColumnCount(xAxisMap.size() + 1, dataTable);
	        int i = 0;
	        for (Map.Entry<Double, Integer> entry : xAxisMap.entrySet()) {
	        	entry.setValue(++i);
	        	dataTable.setValueAt(entry.getKey(), 0, i);
	        }
	        Utils.ensureRowCount(yAxisMap.size() + 1, dataTable);
	        i = 0;
	        for (Map.Entry<Double, Integer> entry : yAxisMap.entrySet()) {
	        	entry.setValue(++i);
	        	dataTable.setValueAt(entry.getKey(), i, 0);
	        }
	        double x, y, val;
	        for (Map.Entry<Double, HashMap<Double, ArrayList<Double>>> xentry : xData.entrySet()) {
	        	x = xentry.getKey();
	        	yData = xentry.getValue();
	        	val = 0;
	        	for (Map.Entry<Double, ArrayList<Double>> yentry : yData.entrySet()) {
	        		y = yentry.getKey();
	        		data = yentry.getValue();
	        		switch (id) {
	        		case COUNT:
	        			val = data.size();
	        			break;
	        		case MINIMUM:
	        			val = Collections.min(data);
	        			break;
	        		case MAXIMUM:
	        			val = Collections.max(data);
	        			break;
	        		case MEAN:
	        			val = Utils.mean(data);
	        			break;
	        		case MEDIAN:
	        			val = Utils.median(data);
	        			break;
	        		case MODE:
	        			val = Utils.mode(data);
	        			break;
	        		case RANGE:
	        			val = Utils.range(data);
	        			break;
	        		case VARIANCE:
	        			val = Utils.variance(data);
	        			break;
	        		case STDDEV:
	        			val = Utils.standardDeviation(data);
	        			break;
	        		}
	            	dataTable.setValueAt(val, yAxisMap.get(y), xAxisMap.get(x));
	        	}
	        }
	        if (xData.size() > 0) {
		        // remove extra rows
		        for (i = dataTable.getRowCount() - 1; i >= 0 && dataTable.getValueAt(i, 0).toString().equals(""); --i)
		            Utils.removeRow(i, dataTable);
		        // remove extra columns
		        for (i = dataTable.getColumnCount() - 1; i >= 0 && dataTable.getValueAt(0, i).toString().equals(""); --i)
		            Utils.removeColumn(i, dataTable);
		        Utils.colorTable(dataTable);
	        }
	        else {
	        	Utils.clearTable(dataTable);
	        }
	        rbGridPlot.setSelected(true);
	        display3D(Plot3D.GRID);
        }
        catch (Exception e) {
            logger.error(e);
            JOptionPane.showMessageDialog(null, e, "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public double[][] doubleZArray(double[] x, double[] y) {
    	double[][] z = new double[y.length][x.length];
    	for (int i = 0; i < x.length; ++i) {
    		for (int j = 0; j < y.length; ++j) {
    			if (!dataTable.getValueAt(j + 1, i + 1).toString().isEmpty())
    				z[j][i] = Double.valueOf(dataTable.getValueAt(j + 1, i + 1).toString());
    		}
    	}
    	return z;
    }

    private void addGridPlot() {
    	if (xData == null || xData.size() == 0)
    		return;
        TreeMap<Double, Integer> xAxisMap = new TreeMap<Double, Integer>();
        TreeMap<Double, Integer> yAxisMap = new TreeMap<Double, Integer>();
        for (Map.Entry<Double, HashMap<Double, ArrayList<Double>>> xentry : xData.entrySet()) {
        	xAxisMap.put(xentry.getKey(), 0);
        	for (Map.Entry<Double, ArrayList<Double>> yentry : xentry.getValue().entrySet())
        		yAxisMap.put(yentry.getKey(), 0);
        }
        double[] x = new double[xAxisMap.size()];
        int i = 0;
        for (Double key : xAxisMap.keySet())
        	x[i++] = key;
        double[] y = new double[yAxisMap.size()];
        i = 0;
        for (Double key : yAxisMap.keySet())
        	y[i++] = key;
        double[][] z = doubleZArray(x, y);
        plot.addGridPlot(dataColumn.getSelectedItem().toString() + " " + statistics.getSelectedItem().toString(), x, y, z);
    }

    private void addBarLineScatterPlot(Plot3D type) {
    	if (xData == null || xData.size() == 0)
    		return;
        ArrayList<Double> xAxisArray = new ArrayList<Double>();
        ArrayList<Double> yAxisArray = new ArrayList<Double>();
        ArrayList<Double> zAxisArray = new ArrayList<Double>();
    	String val;
    	double X, Y;
    	for (int i = 1; i < dataTable.getColumnCount(); ++i) {
    		val = dataTable.getValueAt(0, i).toString();
    		if (val.isEmpty())
    			break;
    		X = Double.valueOf(val.toString());
    		for (int j = 1; j < dataTable.getRowCount(); ++j) {
    			val = dataTable.getValueAt(j, 0).toString();
    			if (val.isEmpty())
    				break;
    			Y = Double.valueOf(val.toString());
    			val = dataTable.getValueAt(j, i).toString();
    			if (!val.isEmpty()) {
    				xAxisArray.add(X);
    				yAxisArray.add(Y);
    				zAxisArray.add(Double.valueOf(val));
    			}
    		}
    	}
        double[] x = new double[xAxisArray.size()];
        int i = 0;
        for (Double v : xAxisArray)
        	x[i++] = v;
        double[] y = new double[yAxisArray.size()];
        i = 0;
        for (Double v : yAxisArray)
        	y[i++] = v;
        double[] z = new double[zAxisArray.size()];
        i = 0;
        for (Double v : zAxisArray)
        	z[i++] = v;
        switch (type) {
        case BAR:
            plot.addBarPlot(dataColumn.getSelectedItem().toString() + " " + statistics.getSelectedItem().toString(), x, y, z);
            break;
        case LINE:
            plot.addLinePlot(dataColumn.getSelectedItem().toString() + " " + statistics.getSelectedItem().toString(), x, y, z);
            break;
        case SCATTER:
            plot.addScatterPlot(dataColumn.getSelectedItem().toString() + " " + statistics.getSelectedItem().toString(), x, y, z);
		default:
			break;
        }
    }

    private void display3D(Plot3D type) {
    	plot.removeAllPlots();
        switch (type) {
        case GRID:
        	addGridPlot();
            break;
        case BAR:
        	addBarLineScatterPlot(type);
            break;
        case LINE:
        	addBarLineScatterPlot(type);
            break;
        case SCATTER:
        	addBarLineScatterPlot(type);
            break;
        default:
        	return;
        }
        plot.setAxisLabel(0, xAxisColumn.getSelectedItem().toString());
        plot.setAxisLabel(1, yAxisColumn.getSelectedItem().toString());
        plot.setAxisLabel(2, dataColumn.getSelectedItem().toString());
    }
    
    private void getFilter() {
		dataFilterType = getType();
    	if (dataFilterType != DataFilter.NONE) {
    		String temp = "";
    		DecimalFormat dblFmt = new DecimalFormat("#.######");
	        JTextField filterTextField = new JTextField("");
	        if (!Double.isNaN(dataFilter))
	        	filterTextField.setText(dblFmt.format(dataFilter));
	        JComponent[] inputs = new JComponent[] {
		        		new JLabel("Set data filter value (Double)"),
		        		filterTextField
		        };
	        boolean done = false;
	        do {
	        	if (JOptionPane.OK_OPTION != JOptionPane.showConfirmDialog(null, inputs, "Data Filter", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE))
	        		return;
	        	temp = filterTextField.getText().trim();
	        	if (Pattern.matches(Utils.fpRegex, temp)) {
	        		dataFilter = Double.valueOf(temp);
	        		done = true;
	        		dataRounding = 0;
	        		if (temp.indexOf('.') != -1) {
	        			temp = temp.substring(temp.indexOf('.'));
	        			dataRounding = temp.length() - 1;
	        		}
	        	}
	        	else {
	        		if (!Double.isNaN(dataFilter))
	        			dblFmt.format(dataFilter);
	        		else
	        			filterTextField.setText("");
	        	}
	        }
	        while (!done);
    	}
    	else
			dataFilter = Double.NaN;
    }

	@Override
	public void actionPerformed(ActionEvent e) {
		if ("selectlog".equals(e.getActionCommand()))
            getLogColumns();
		else if ("go".equals(e.getActionCommand()))
            processLog();
		else if ("setaxis".equals(e.getActionCommand()))
            setAxis();
		else if ("grid".equals(e.getActionCommand())) {
	    	if (xData != null)
	    		display3D(Plot3D.GRID);
		}
		else if ("bar".equals(e.getActionCommand())) {
	    	if (xData != null)
	    		display3D(Plot3D.BAR);
		}
		else if ("line".equals(e.getActionCommand())) {
	    	if (xData != null)
	    		display3D(Plot3D.LINE);
		}
		else if ("scatter".equals(e.getActionCommand())) {
	    	if (xData != null)
	    		display3D(Plot3D.SCATTER);
		}
		else if ("filter".equals(e.getActionCommand())) {
			getFilter();
		}
	}
}
