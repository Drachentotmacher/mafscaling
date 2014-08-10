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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.Stack;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.JToolBar;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import org.apache.log4j.Logger;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartMouseEvent;
import org.jfree.chart.ChartMouseListener;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.StandardTickUnitSource;
import org.jfree.chart.plot.Marker;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.title.LegendTitle;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.RectangleAnchor;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.RectangleInsets;
import org.jfree.ui.TextAnchor;
import org.math.plot.Plot3DPanel;
import quick.dbtable.Column;
import quick.dbtable.DBTable;
import quick.dbtable.PrintProperties;
import quick.dbtable.Skin;
import quick.dbtable.Filter;

public class LogView extends JTabbedPane implements ActionListener {
	private static final long serialVersionUID = -3803091816206090707L;
    private static final Logger logger = Logger.getLogger(LogView.class);
    private final JFileChooser fileChooser = new JFileChooser();
    
    public class XYZ {
        @Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getOuterType().hashCode();
			long temp;
			temp = Double.doubleToLongBits(x);
			result = prime * result + (int) (temp ^ (temp >>> 32));
			temp = Double.doubleToLongBits(y);
			result = prime * result + (int) (temp ^ (temp >>> 32));
			temp = Double.doubleToLongBits(z);
			result = prime * result + (int) (temp ^ (temp >>> 32));
			return result;
		}
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			XYZ other = (XYZ) obj;
			if (!getOuterType().equals(other.getOuterType()))
				return false;
			if (Double.doubleToLongBits(x) != Double.doubleToLongBits(other.x))
				return false;
			if (Double.doubleToLongBits(y) != Double.doubleToLongBits(other.y))
				return false;
			if (Double.doubleToLongBits(z) != Double.doubleToLongBits(other.z))
				return false;
			return true;
		}
		private double x;
        private double y;
        private double z;
        XYZ(double x, double y, double z) {
        	this.x = x;
        	this.y = y;
        	this.z = z;
        }
		private LogView getOuterType() {
			return LogView.this;
		}
    }
    
    public class TableSkin extends Skin {
		private static final long serialVersionUID = 8263328522848779295L;
		Font headerFont = new Font("Arial", Font.PLAIN, 12);
		Font font = new Font("Arial", Font.PLAIN, 11);
		@SuppressWarnings("unchecked")
		public TableSkin() {
			put(Skin.HEADER_FONT,headerFont);
			put(Skin.TABLE_FONT,font);
			put(Skin.ROW_HEIGTH, new Integer(16));
			put(Skin.FOCUS_CELL_HIGHLIGHT_BORDER,new javax.swing.border.MatteBorder(2, 2, 2, 2, Color.BLACK));
      	}
    }
    
    public class SortingPopUp extends JPopupMenu implements ActionListener {
		private static final long serialVersionUID = -8399244173709551368L;
		JMenuItem sortAscending;
        JMenuItem sortDescending;
        int columnIndex;
        public SortingPopUp(int column) {
        	columnIndex = column;
        	sortAscending = new JMenuItem("Sort Ascending");
        	sortAscending.setActionCommand("sortascending");
        	sortAscending.addActionListener(this);
            add(sortAscending);
            sortDescending = new JMenuItem("Sort Descending");
            sortDescending.setActionCommand("sortdescending");
            sortDescending.addActionListener(this);
            add(sortDescending);
        }
		@Override
		public void actionPerformed(ActionEvent e) {
	        if ("sortascending".equals(e.getActionCommand()))
				sortAscending(columnIndex);
	        else if ("sortdescending".equals(e.getActionCommand()))
				sortDescending(columnIndex);
		}
    }
    
    class DoubleComparator implements quick.dbtable.Comparator {
    	public int compare(int column, Object currentData, Object nextData) {
    		Double v1 = Double.valueOf(currentData.toString());
    		Double v2 = Double.valueOf(nextData.toString());
    		return Double.compare(v1, v2);
    	}
    }
    
    public static class CompareFilter implements Filter {
    	public enum Condition {
    		NONE,
    		GREATER,
    		GREATER_EQUAL,
    		EQUAL,
    		LESS_EQUAL,
    		LESS
    	}
    	private int colId = 0;
    	private Condition condition = Condition.NONE;
    	private String filterString = "0";
    	private double filter = Double.NaN;
    	public void setColumn(int id) {
    		colId = id;
    	}
    	public void setCondition(Condition c) {
    		condition = c;
    	}
    	public void setFilter(String f) {
    		filterString = f;
    		filter = Double.valueOf(filterString);
    	}
    	public int[] filter(TableModel tm) {
    		if (Double.isNaN(filter) || Condition.NONE == condition)
    			return new int[0];
    		ArrayList<Integer> list = new ArrayList<Integer>();
    		double value;
    		int rounding = 0;
    		int i = 0;
			if (filterString.indexOf('.') != -1) {
				filterString = filterString.substring(filterString.indexOf('.'));
				rounding = filterString.length() - 1;
			}
    		for (i = 0; i < tm.getRowCount(); ++i) {
    			try {
	    			value = Double.valueOf((String)tm.getValueAt(i, colId + 1));
    			}
    			catch (Exception e) {
    				continue;
    			}
    	    	switch (condition) {
    	    	case LESS:
    	    		if (value < filter)
    	    			list.add(i);
    	    		break;
    	    	case LESS_EQUAL:
    	    		if (value <= filter)
    	    			list.add(i);
    	    		break;
    	    	case GREATER_EQUAL:
    	    		if (value >= filter)
    	    			list.add(i);
    	    		break;
    	    	case GREATER:
    	    		if (value > filter)
    	    			list.add(i);
    	    		break;
    	    	default:
    	        	double rndVal = value;
    	        	if (rounding > 0) {
    	        		double multiplier = Math.pow(10.0, rounding);
    	        		rndVal = Math.round(value * multiplier) / multiplier;
    	        	}
    	        	else
    	        		rndVal = Math.round(value);
    	    		if (rndVal == filter)
    	    			list.add(i);
    	    		break;
    	    	}
    		}
			int arr[] = new int[list.size()];
			for (i = 0; i < list.size(); ++i)
				arr[i] = list.get(i);
			return arr;
    	}
    }

    public class CheckboxHeaderRenderer implements TableCellRenderer {
    	private class CheckBoxIcon implements Icon {
    		private final JCheckBox check;
    		public CheckBoxIcon(JCheckBox check) { this.check = check; }
    		@Override
    		public int getIconWidth() { return check.getPreferredSize().width; }
    		@Override
    		public int getIconHeight() { return check.getPreferredSize().height; }
    		@Override
    		public void paintIcon(Component c, Graphics g, int x, int y) {
    			SwingUtilities.paintComponent(g, check, (Container) c, x, y, getIconWidth(), getIconHeight());
    	        g.setColor(check.getBackground());
    	        g.fillRect(x + 6, y + 6, 13 - 4, 13 - 4);
    		}
    	}
    	private final JCheckBox check = new JCheckBox();
    	private int colId;
    	private Color defaultColor;
    	public CheckboxHeaderRenderer(int col, JTableHeader header) {
    		colId = col;
    		check.setOpaque(false);
    		check.setFont(header.getFont());
    		header.addMouseListener(new MouseAdapter() {
    			@Override
    			public void mouseClicked(MouseEvent e) {
    				try {
	    				JTable table = ((JTableHeader) e.getSource()).getTable();
	    				TableColumnModel columnModel = table.getColumnModel();
	    				int viewColumn = columnModel.getColumnIndexAtX(e.getX());
	    				int modelColumn = table.convertColumnIndexToModel(viewColumn);
	    				if (colId != modelColumn)
	    					return;
	    				if (SwingUtilities.isLeftMouseButton(e)) {
		    				check.setSelected(!check.isSelected());
		    				if (check.isSelected()) {
		    					defaultColor = check.getBackground();
		    					check.setBackground(colors.pop());
		    					TableModel model = table.getModel();
		    					addXYSeries(model, colId, columnModel.getColumn(viewColumn).getHeaderValue().toString(), check.getBackground());
		    				}
		    				else {
		    					colors.push(check.getBackground());
		    					check.setBackground(defaultColor);
		    					removeXYSeries(colId);
		    				}
							((JTableHeader) e.getSource()).repaint();
	    				}
	    				else if (SwingUtilities.isRightMouseButton(e)) {
	    					SortingPopUp menu = new SortingPopUp(colId);
	    					menu.show(e.getComponent(), e.getX(), e.getY());
	    				}
    				}
    				catch (Exception ex) {
    		    		ex.printStackTrace();
    				}
    			}
    		});
    	}
    	@Override
    	public Component getTableCellRendererComponent(JTable tbl, Object val, boolean isS, boolean hasF, int row, int col) {
    		TableCellRenderer r = tbl.getTableHeader().getDefaultRenderer();
    		JLabel label = (JLabel) r.getTableCellRendererComponent(tbl, val, isS, hasF, row, col);
    		label.setIcon(new CheckBoxIcon(check));
    		return label;
    	}
    }
    private ChartPanel chartPanel = null;
    private XYPlot plot = null;
    private XYSeriesCollection rpmDataset = null;
    private XYSeriesCollection dataset = null;
    private XYLineAndShapeRenderer rpmPlotRenderer = null;
    private XYLineAndShapeRenderer plotRenderer = null;
    //private Stroke lineStroke = null;
    private int rpmCol = -1;
    private int displCount = 0;
    private JPanel logViewPanel = null;
    private DBTable logDataTable = null;
    private JButton loadButton = null;
    private JButton printButton = null;
    private JButton previewButton = null;
    private JButton findButton = null;
    private JButton replaceButton = null;
    private JComboBox<String> selectionCombo;
    private JComboBox<String> compareCombo;
    private JTextField  filterText;
    private JButton filterButton;
    private Plot3DPanel plot3d = null;
    private JComboBox<String> xAxisColumn = null;
    private JComboBox<String> yAxisColumn = null;
    private JMultiSelectionBox plotsColumn = null;
    private Font curveLabelFont = new Font("Verdana", Font.BOLD, 11);
    private Stack<Color> colors = new Stack<Color>();

	public LogView(int tabPlacement) {
        super(tabPlacement);
        initialize();
    }

    private void initialize() {
        createDataTab();
        createChartTab();
        createUsageTab();
    }

    //////////////////////////////////////////////////////////////////////////////////////
    // DATA TAB
    //////////////////////////////////////////////////////////////////////////////////////
    
    private void createDataTab() {
        fileChooser.setCurrentDirectory(new File("."));
        
        JPanel dataPanel = new JPanel(new BorderLayout());
        add(dataPanel, "<html><div style='text-align: center;'>D<br>a<br>t<br>a</div></html>");
        
        createToolBar(dataPanel);
        createLogViewPanel();
        createGraghPanel();
        
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, logViewPanel, chartPanel);
        splitPane.setResizeWeight(0.5);
        splitPane.setOneTouchExpandable(true);
        splitPane.setContinuousLayout(true);
        splitPane.setDividerLocation(150);
        dataPanel.add(splitPane);
    }

    private void createLogViewPanel() {
    	logViewPanel = new JPanel();
	    GridBagLayout gbl_logViewPanel = new GridBagLayout();
	    gbl_logViewPanel.columnWidths = new int[] {0};
	    gbl_logViewPanel.rowHeights = new int[] {0, 0};
	    gbl_logViewPanel.columnWeights = new double[]{1.0};
	    gbl_logViewPanel.rowWeights = new double[]{0.0, 1.0};
	    logViewPanel.setLayout(gbl_logViewPanel);
    	try {
	    	logDataTable = new DBTable();
	    	logDataTable.copyColumnHeaderNames = true;
	    	logDataTable.defaultClickCountToStartEditor = 2;
	    	logDataTable.doNotUseDatabaseSort = true;
	    	logDataTable.listenKeyPressEventsWholeWindow = true;
	    	logDataTable.createControlPanel(DBTable.READ_NAVIGATION);
	    	logDataTable.enableExcelCopyPaste();
	    	logDataTable.setSortEnabled(false); 
	    	logDataTable.setSkin(new TableSkin());
			logDataTable.refresh(new String[1][25]);
			logDataTable.setComparator(new DoubleComparator());
			logDataTable.getTable().setCellSelectionEnabled(true);
			logDataTable.getTable().setColumnSelectionAllowed(true);
			logDataTable.getTable().setRowSelectionAllowed(true);
			logDataTable.getTable().setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
			
	    	selectionCombo.removeAllItems();
			for (int i = 0; i < logDataTable.getColumnCount(); ++i) {
				Column col = logDataTable.getColumn(i);
				col.setNullable(true);
				selectionCombo.addItem(col.getHeaderValue().toString());
			}

	        GridBagConstraints gbl_logDataTable = new GridBagConstraints();
	        gbl_logDataTable.insets = new Insets(0, 0, 0, 0);
	        gbl_logDataTable.anchor = GridBagConstraints.PAGE_START;
	        gbl_logDataTable.fill = GridBagConstraints.BOTH;
	        gbl_logDataTable.gridx = 0;
	        gbl_logDataTable.gridy = 1;
	        logViewPanel.add(logDataTable, gbl_logDataTable);
		}
    	catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
		}
    }
    
    private void createToolBar(JPanel panel)
    {
		JToolBar toolBar = new JToolBar();
		panel.add(toolBar, BorderLayout.NORTH);
		
		Insets i1 = new Insets(0, 0, 0, 0);
		
		loadButton = new JButton(new ImageIcon(this.getClass().getResource("/open.png")));
		loadButton.setToolTipText("Load Log File");
		loadButton.setMargin(i1);
		loadButton.setAlignmentY(Component.CENTER_ALIGNMENT);
		loadButton.addActionListener(this);
		toolBar.add(loadButton);
		
		toolBar.addSeparator();
		
		printButton = new JButton(new ImageIcon(this.getClass().getResource("/print.png")));
		printButton.setToolTipText("Print");
		printButton.setMargin(i1);
		printButton.setAlignmentY(Component.CENTER_ALIGNMENT);
		printButton.addActionListener(this);
		toolBar.add(printButton);
		
		previewButton = new JButton(new ImageIcon(this.getClass().getResource("/print_preview.png")));
		previewButton.setToolTipText("Print Preview");
		previewButton.setMargin(i1);
		previewButton.setAlignmentY(Component.CENTER_ALIGNMENT);
		previewButton.addActionListener(this);
		toolBar.add(previewButton);
		
		findButton = new JButton(new ImageIcon(this.getClass().getResource("/find.png")));
		findButton.setToolTipText("Find");
		findButton.setMargin(i1);
		findButton.setAlignmentY(Component.CENTER_ALIGNMENT);
		findButton.addActionListener(this);
		toolBar.add(findButton);
		
		replaceButton = new JButton(new ImageIcon(this.getClass().getResource("/replace.png")));
		replaceButton.setToolTipText("Replace");
		replaceButton.setMargin(i1);
		replaceButton.setAlignmentY(Component.CENTER_ALIGNMENT);
		replaceButton.addActionListener(this);
		toolBar.add(replaceButton);
		
		toolBar.addSeparator();
		
		JLabel filterLabel = new JLabel("Filter: ");
		filterLabel.setAlignmentY(Component.CENTER_ALIGNMENT);
		toolBar.add(filterLabel);
		
		selectionCombo = new JComboBox<String>();
		selectionCombo.setMaximumSize(new Dimension(210, 20));
		selectionCombo.setAlignmentY(Component.CENTER_ALIGNMENT);
		selectionCombo.addActionListener(this);
		toolBar.add(selectionCombo);
		
		compareCombo = new JComboBox<String>();
		compareCombo.addItem(">");
		compareCombo.addItem(">=");
		compareCombo.addItem("=");
		compareCombo.addItem("<=");
		compareCombo.addItem("<");
		compareCombo.setMaximumSize(new Dimension(45, 20));
		compareCombo.setAlignmentY(Component.CENTER_ALIGNMENT);
		compareCombo.addActionListener(this);
		toolBar.add(compareCombo);
		
		filterText = new JTextField();
		filterText.setMaximumSize(new Dimension(100, 20));
		filterText.setAlignmentY(Component.CENTER_ALIGNMENT);
		toolBar.add(filterText);
		
		filterButton = new JButton("Set");
		filterButton.setMargin(i1);
		filterButton.setAlignmentY(Component.CENTER_ALIGNMENT);
		filterButton.addActionListener(this);
		toolBar.add(filterButton);
    }
    
    private void createGraghPanel() {
        JFreeChart chart = ChartFactory.createXYLineChart(null, null, null, null, PlotOrientation.VERTICAL, false, true, false);
        chartPanel = new ChartPanel(chart, true, true, true, true, true);
		chartPanel.setAutoscrolls(true);
		chart.setBackgroundPaint(new Color(60, 60, 65));

        //lineStroke = new BasicStroke(2.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 1.0f, null, 0.0f);
		rpmDataset = new XYSeriesCollection();
		rpmPlotRenderer = new XYLineAndShapeRenderer();		
		dataset = new XYSeriesCollection();
		plotRenderer = new XYLineAndShapeRenderer();
/*
        lineRenderer.setBaseToolTipGenerator(new StandardXYToolTipGenerator( 
                StandardXYToolTipGenerator.DEFAULT_TOOL_TIP_FORMAT, 
                new DecimalFormat("0.00"), new DecimalFormat("0.00")));
*/
		

		NumberAxis xAxis = new NumberAxis();
        xAxis.setTickLabelsVisible(false);
        xAxis.setTickLabelPaint(Color.WHITE);
        xAxis.setAutoRangeIncludesZero(false);
        NumberAxis yAxis = new NumberAxis();
        yAxis.setTickLabelsVisible(false);
        yAxis.setTickLabelPaint(Color.WHITE);
        yAxis.setAutoRangeIncludesZero(false);
        NumberAxis y2Axis = new NumberAxis();
        y2Axis.setTickLabelsVisible(false);
        y2Axis.setTickLabelPaint(Color.WHITE);
        y2Axis.setAutoRangeIncludesZero(false);

        plot = chartPanel.getChart().getXYPlot();
        plot.setRangePannable(true);
        plot.setDomainPannable(true);
        plot.setDomainGridlinePaint(Color.LIGHT_GRAY);
        plot.setRangeGridlinePaint(Color.LIGHT_GRAY);
        plot.setBackgroundPaint(new Color(80, 80, 85));

        plot.setDataset(0, rpmDataset);
        plot.setRenderer(0, rpmPlotRenderer);
        plot.setDomainAxis(0, xAxis);
        plot.setRangeAxis(0, yAxis);
        plot.mapDatasetToDomainAxis(0, 0);
        plot.mapDatasetToRangeAxis(0, 0);

        plot.setDataset(1, dataset);
        plot.setRenderer(1, plotRenderer);
        plot.setRangeAxis(1, y2Axis);
        plot.mapDatasetToDomainAxis(1, 0);
        plot.mapDatasetToRangeAxis(1, 1);
        
        LegendTitle legend = new LegendTitle(plot);
        legend.setItemFont(new Font("Arial", 0, 10));
        legend.setPosition(RectangleEdge.TOP);
        legend.setItemPaint(Color.WHITE);
        chart.addLegend(legend);
        
        chartPanel.addChartMouseListener(
        	new ChartMouseListener() {
    			@Override
        		public void chartMouseMoved(ChartMouseEvent event) {
        			try {
        				plot.clearDomainMarkers();
                        Rectangle2D dataArea = chartPanel.getChartRenderingInfo().getPlotInfo().getDataArea();
        		        Point2D p = chartPanel.translateScreenToJava2D(event.getTrigger().getPoint());
                        double x = plot.getDomainAxis().java2DToValue(p.getX(), dataArea, plot.getDomainAxisEdge());
                        double midX = (dataArea.getMaxX() - dataArea.getMinX()) / 2;
        				int offset = 0;
        				int col = -1;
        				RectangleAnchor rectangleAnchor = null;
        				TextAnchor textAnchor = null;
        		        if (rpmDataset.getSeriesCount() > 0 && rpmPlotRenderer.isSeriesVisible(0) && x >= 0 && rpmDataset.getSeries(0).getItemCount() > (int)x) {
        		        	if (p.getX() < midX) {
	        		        	rectangleAnchor = RectangleAnchor.TOP_RIGHT;
	        		        	textAnchor = TextAnchor.TOP_LEFT;
        		        	}
        		        	else {
            		        	rectangleAnchor = RectangleAnchor.TOP_LEFT;
	        		        	textAnchor = TextAnchor.TOP_RIGHT;
        		        	}
        		        	col = rpmCol;
            		        Marker xMarker = new ValueMarker(x);
            				xMarker.setLabelAnchor(rectangleAnchor);
            				xMarker.setLabelTextAnchor(textAnchor);
            				xMarker.setPaint(Color.WHITE);
            				xMarker.setLabelFont(curveLabelFont);
            				xMarker.setLabelPaint(rpmPlotRenderer.getSeriesPaint(0));
            				xMarker.setLabel(rpmDataset.getSeries(0).getDescription() + ": " + rpmDataset.getSeries(0).getY((int)x));
            				xMarker.setLabelOffset(new RectangleInsets(2, 5, 2, 5));
            				offset += 20;
            				plot.addDomainMarker(xMarker);
        		        }
        		        for (int i = 0; i < dataset.getSeriesCount(); ++i) {
        		        	if (rectangleAnchor == null) {
            		        	if (p.getX() < midX) {
    	        		        	rectangleAnchor = RectangleAnchor.TOP_RIGHT;
    	        		        	textAnchor = TextAnchor.TOP_LEFT;
            		        	}
            		        	else {
                		        	rectangleAnchor = RectangleAnchor.TOP_LEFT;
    	        		        	textAnchor = TextAnchor.TOP_RIGHT;
            		        	}
        		        	}
	        		        if (plotRenderer.isSeriesVisible(i) && x >= 0 && dataset.getSeries(i).getItemCount() > (int)x) {
	        		        	if (col == -1)
	        		        		col = i;
	            		        Marker xMarker = new ValueMarker(x);
	            				xMarker.setLabelAnchor(rectangleAnchor);
	            				xMarker.setLabelTextAnchor(textAnchor);
	            				xMarker.setPaint(Color.WHITE);
	            				xMarker.setLabelFont(curveLabelFont);
		        				xMarker.setLabelPaint(plotRenderer.getSeriesPaint(i));
		        				xMarker.setLabel(dataset.getSeries(i).getDescription() + ": " + dataset.getSeries(i).getY((int)x));
	            				xMarker.setLabelOffset(new RectangleInsets(2 + offset, 5, 2, 5));	            				
	            				offset += 20;
		        				plot.addDomainMarker(xMarker);
	        		        }
        		        }
        		        if (col >= 0) {
	        				chartPanel.repaint();
	        				try {
		        				logDataTable.getTable().setRowSelectionInterval((int)x, (int)x);
		        				logDataTable.getTable().changeSelection((int)x, col, false, false);
	        				}
	        				catch (Exception e) { /* ignore */ }
        		        }
        			}
        			catch (Exception e) {
        	    		e.printStackTrace();
        			}
    			}
				@Override
				public void chartMouseClicked(ChartMouseEvent arg0) {
				}
        	}
        );
    }

    //////////////////////////////////////////////////////////////////////////////////////
    // CREATE 3D CHART TAB
    //////////////////////////////////////////////////////////////////////////////////////
    
    private void createChartTab() {
        JPanel plotPanel = new JPanel();
        add(plotPanel, "<html><div style='text-align: center;'>3<br>D<br><br>C<br>h<br>a<br>r<br>t</div></html>");
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
        gbl_cntlPanel.columnWidths = new int[]{0, 0, 0, 0, 0, 0, 0, 0};
        gbl_cntlPanel.rowHeights = new int[]{0};
        gbl_cntlPanel.columnWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0};
        gbl_cntlPanel.rowWeights = new double[]{0};
        cntlPanel.setLayout(gbl_cntlPanel);

        JLabel xAxisLabel = new JLabel("X-Axis");
        xAxisLabel.setHorizontalAlignment(LEFT);
        GridBagConstraints gbc_xAxisLabel = new GridBagConstraints();
        gbc_xAxisLabel.anchor = GridBagConstraints.EAST;
        gbc_xAxisLabel.insets = new Insets(3, 3, 3, 0);
        gbc_xAxisLabel.gridx = 0;
        gbc_xAxisLabel.gridy = 0;
        cntlPanel.add(xAxisLabel, gbc_xAxisLabel);
        
        xAxisColumn = new JComboBox<String>();
        GridBagConstraints gbc_xAxisColumn = new GridBagConstraints();
        gbc_xAxisColumn.anchor = GridBagConstraints.WEST;
        gbc_xAxisColumn.insets = new Insets(3, 3, 3, 3);
        gbc_xAxisColumn.gridx = 1;
        gbc_xAxisColumn.gridy = 0;
        xAxisColumn.setPrototypeDisplayValue("XXXXXXXXXXXXXXXXXXXXXXXXXXX");
        cntlPanel.add(xAxisColumn, gbc_xAxisColumn);
    	
        JLabel yAxisLabel = new JLabel("Y-Axis");
        yAxisLabel.setHorizontalAlignment(LEFT);
        GridBagConstraints gbc_yAxisLabel = new GridBagConstraints();
        gbc_yAxisLabel.anchor = GridBagConstraints.EAST;
        gbc_yAxisLabel.insets = new Insets(3, 3, 3, 0);
        gbc_yAxisLabel.gridx = 2;
        gbc_yAxisLabel.gridy = 0;
        cntlPanel.add(yAxisLabel, gbc_yAxisLabel);
        
        yAxisColumn = new JComboBox<String>();
        GridBagConstraints gbc_yAxisColumn = new GridBagConstraints();
        gbc_yAxisColumn.anchor = GridBagConstraints.WEST;
        gbc_yAxisColumn.insets = new Insets(3, 3, 3, 3);
        gbc_yAxisColumn.gridx = 3;
        gbc_yAxisColumn.gridy = 0;
        yAxisColumn.setPrototypeDisplayValue("XXXXXXXXXXXXXXXXXXXXXXXXXXX");
        cntlPanel.add(yAxisColumn, gbc_yAxisColumn);
    	
        JLabel datasLabel = new JLabel("Plots");
        datasLabel.setHorizontalAlignment(LEFT);
        GridBagConstraints gbc_datasLabel = new GridBagConstraints();
        gbc_datasLabel.anchor = GridBagConstraints.EAST;
        gbc_datasLabel.insets = new Insets(3, 3, 3, 0);
        gbc_datasLabel.gridx = 4;
        gbc_datasLabel.gridy = 0;
        cntlPanel.add(datasLabel, gbc_datasLabel);
        
        plotsColumn = new JMultiSelectionBox(" ");
        plotsColumn.setIcon(new ImageIcon(getClass().getResource("/down.gif")));
        plotsColumn.setMargin(new Insets(3, 3, 3, 3));
        plotsColumn.setVerticalTextPosition(SwingConstants.CENTER);
        plotsColumn.setHorizontalAlignment(SwingConstants.RIGHT);
        plotsColumn.setHorizontalTextPosition(SwingConstants.LEFT);
        plotsColumn.setIconTextGap(3);
        plotsColumn.setPreferredSize(new Dimension(190, 22));
        plotsColumn.setMaximumSize(new Dimension(190, 22));
        GridBagConstraints gbc_plotsColumn = new GridBagConstraints();
        gbc_plotsColumn.anchor = GridBagConstraints.PAGE_START;
        gbc_plotsColumn.insets = new Insets(3, 3, 3, 3);
        gbc_plotsColumn.gridx = 5;
        gbc_plotsColumn.gridy = 0;
        cntlPanel.add(plotsColumn, gbc_plotsColumn);
        
        JButton btnGoButton = new JButton("View");
        GridBagConstraints gbc_btnGoButton = new GridBagConstraints();
        gbc_btnGoButton.anchor = GridBagConstraints.EAST;
        gbc_btnGoButton.insets = new Insets(3, 3, 3, 3);
        gbc_btnGoButton.gridx = 7;
        gbc_btnGoButton.gridy = 0;
        btnGoButton.setActionCommand("view");
        btnGoButton.addActionListener(this);
        cntlPanel.add(btnGoButton, gbc_btnGoButton);
        
        plot3d = new Plot3DPanel("SOUTH") {
			private static final long serialVersionUID = 7914951068593204419L;
			public void addPlotToolBar(String location) {
				super.addPlotToolBar(location);
        		super.plotToolBar.remove(7);
        		super.plotToolBar.remove(5);
        		super.plotToolBar.remove(4);
        	}        	
        };
        plot3d.setAutoBounds();
        plot3d.setAutoscrolls(true);
        plot3d.setEditable(false);
        plot3d.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        plot3d.setForeground(Color.BLACK);
        plot3d.getAxis(0).setColor(Color.BLACK);
        plot3d.getAxis(1).setColor(Color.BLACK);
        plot3d.getAxis(2).setColor(Color.BLACK);
        
        GridBagConstraints gbl_chartPanel = new GridBagConstraints();
        gbl_chartPanel.anchor = GridBagConstraints.CENTER;
        gbl_chartPanel.insets = new Insets(3, 3, 3, 3);
        gbl_chartPanel.weightx = 1.0;
        gbl_chartPanel.weighty = 1.0;
        gbl_chartPanel.fill = GridBagConstraints.BOTH;
        gbl_chartPanel.gridx = 0;
        gbl_chartPanel.gridy = 1;
        plotPanel.add(plot3d, gbl_chartPanel);
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
        bundle = ResourceBundle.getBundle("com.vgi.mafscaling.logview");
        return bundle.getString("usage"); 
    }

    //////////////////////////////////////////////////////////////////////////////////////
    // WORK FUNCTIONS
    //////////////////////////////////////////////////////////////////////////////////////
    
    private void initColors() {
    	colors.clear();
    	colors.push(new Color(255,101,0));
    	colors.push(new Color(255,154,0));
    	colors.push(new Color(255,207,0));
    	colors.push(new Color(156,207,0));
    	colors.push(new Color(49,207,206));
    	colors.push(new Color(49,101,255));
    	colors.push(new Color(255,207,156));
    	colors.push(new Color(206,154,255));
    	colors.push(new Color(255,154,206));
    	colors.push(new Color(156,207,255));
    	colors.push(new Color(255,255,156));
    	colors.push(new Color(206,255,206));
    	colors.push(new Color(206,255,255));
    	colors.push(new Color(0,207,255));
    	colors.push(new Color(0,0,255));
    	colors.push(new Color(0,130,132));
    	colors.push(new Color(132,0,0));
    	colors.push(new Color(132,0,132));
    	colors.push(new Color(0,255,255));
    	colors.push(new Color(255,255,0));
    	colors.push(new Color(255,0,255));
    	colors.push(new Color(0,0,132));
    	colors.push(new Color(206,207,255));
    	colors.push(new Color(0,101,206));
    	colors.push(new Color(255,130,132));
    	colors.push(new Color(99,0,99));
    	colors.push(new Color(206,255,255));
    	colors.push(new Color(255,255,206));
    	colors.push(new Color(156,48,99));
    	colors.push(new Color(156,154,255));
    	colors.push(new Color(221,160,221));
    	colors.push(new Color(176,196,222));
    	colors.push(new Color(32,178,170));
    	colors.push(new Color(250,240,230));
    	colors.push(new Color(210,180,140));
    	colors.push(new Color(143,188,139));
    	colors.push(new Color(219,220,37));
    	colors.push(new Color(42,214,42));
    	colors.push(new Color(241,104,60));
    	colors.push(new Color(29,139,209));
    	/*
        colors.push(Color.decode("#FF6500"));
        colors.push(Color.decode("#FF9A00"));
        colors.push(Color.decode("#FFCF00"));
        colors.push(Color.decode("#9CCF00"));
        colors.push(Color.decode("#31CFCE"));
        colors.push(Color.decode("#3165FF"));
        colors.push(Color.decode("#FFCF9C"));
        colors.push(Color.decode("#CE9AFF"));
        colors.push(Color.decode("#FF9ACE"));
        colors.push(Color.decode("#9CCFFF"));
        colors.push(Color.decode("#FFFF9C"));
        colors.push(Color.decode("#CEFFCE"));
        colors.push(Color.decode("#CEFFFF"));
        colors.push(Color.decode("#00CFFF"));
        colors.push(Color.decode("#0000FF"));
        colors.push(Color.decode("#008284"));
        colors.push(Color.decode("#840000"));
        colors.push(Color.decode("#840084"));
        colors.push(Color.decode("#00FFFF"));
        colors.push(Color.decode("#FFFF00"));
        colors.push(Color.decode("#FF00FF"));
        colors.push(Color.decode("#000084"));
        colors.push(Color.decode("#CECFFF"));
        colors.push(Color.decode("#0065CE"));
        colors.push(Color.decode("#FF8284"));
        colors.push(Color.decode("#630063"));
        colors.push(Color.decode("#CEFFFF"));
        colors.push(Color.decode("#FFFFCE"));
        colors.push(Color.decode("#9C3063"));
        colors.push(Color.decode("#9C9AFF"));
        colors.push(Color.decode("#DDA0DD"));
        colors.push(Color.decode("#B0C4DE"));
        colors.push(Color.decode("#20B2AA"));
        colors.push(Color.decode("#FAF0E6"));
        colors.push(Color.decode("#D2B48C"));
        colors.push(Color.decode("#8FBC8B"));
        colors.push(Color.decode("#DBDC25"));
        colors.push(Color.decode("#2AD62A"));
        colors.push(Color.decode("#F1683C"));
        colors.push(Color.decode("#1D8BD1"));
        */
    }
    
    private void addXYSeries(TableModel model, int column, String name, Color color) {
        setCursor(new Cursor(Cursor.WAIT_CURSOR));
    	try {
			if ((column - 1) == rpmCol) {
		        ((NumberAxis)plot.getRangeAxis(0)).setStandardTickUnits(NumberAxis.createIntegerTickUnits());
		        plot.getRangeAxis(0).setTickLabelsVisible(true);
		        rpmPlotRenderer.setSeriesPaint(0, color);
		        rpmPlotRenderer.setSeriesVisible(0, true);
			}
			else {
		        plot.getRangeAxis(1).setTickLabelsVisible(true);
		        plotRenderer.setSeriesPaint(column - 1, color);
				plotRenderer.setSeriesVisible(column - 1, true);
				displCount += 1;
			}
    	}
    	finally {
            setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    	}
    	chartPanel.revalidate();
    }
    
    private void removeXYSeries(int column) {
		if ((column - 1) == rpmCol) {
	        ((NumberAxis)plot.getRangeAxis(0)).setStandardTickUnits(new StandardTickUnitSource());
	        plot.getRangeAxis(0).setTickLabelsVisible(false);
	        rpmPlotRenderer.setSeriesVisible(0, false);
		}
		else {
			displCount -= 1;
			if (displCount == 0)
				plot.getRangeAxis(1).setTickLabelsVisible(false);
	        plotRenderer.setSeriesVisible(column - 1, false);
		}
    }
    
    private void sortAscending(int column) {
    	logDataTable.sortByColumn(column, true) ;
    }
    
    private void sortDescending(int column) {
    	logDataTable.sortByColumn(column, false) ;
    }

	private void loadLogFile() {
        if (JFileChooser.APPROVE_OPTION != fileChooser.showOpenDialog(this))
            return;
        File file = fileChooser.getSelectedFile();
        Properties prop = new Properties();
        prop.put("delimiter", ",");
        prop.put("firstRowHasColumnNames", "true");
        setCursor(new Cursor(Cursor.WAIT_CURSOR));
		logDataTable.filter(null);
		filterText.setText("");
        try {
        	JTableHeader header = logDataTable.getTableHeader();
        	for (MouseListener listener : header.getMouseListeners())
        		header.removeMouseListener(listener);
        	logDataTable.refresh(file.toURI().toURL(), prop);
        	Column col;
        	String colName;
        	String lcColName;
	        String val;
        	TableCellRenderer renderer;
        	Component comp;
        	XYSeries series;
            xAxisColumn.removeAllItems();
            yAxisColumn.removeAllItems();
            plotsColumn.removeAllItems();
            xAxisColumn.addItem("");
            yAxisColumn.addItem("");
            plotsColumn.setText("");
            plot3d.removeAllPlots();
        	rpmDataset.removeAllSeries();
        	dataset.removeAllSeries();
        	rpmCol = -1;
        	displCount = 0;
        	selectionCombo.removeAllItems();
			for (int i = 0; i < logDataTable.getColumnCount(); ++i) {
				col = logDataTable.getColumn(i);
				renderer = new CheckboxHeaderRenderer(i + 1, logDataTable.getTableHeader());
				col.setHeaderRenderer(renderer);
				colName = col.getHeaderValue().toString();
                xAxisColumn.addItem(colName);
                yAxisColumn.addItem(colName);
                plotsColumn.addItem(colName);
		    	comp = renderer.getTableCellRendererComponent(logDataTable.getTable(), colName, false, false, 0, 0);
		    	col.setPreferredWidth(comp.getPreferredSize().width + 4);
		    	series = new XYSeries(colName);
		    	series.setDescription(colName);
		    	lcColName = colName.toLowerCase();
				dataset.addSeries(series);
//				plotRenderer.setSeriesStroke(i, lineStroke);
				plotRenderer.setSeriesShapesVisible(i, false);
				plotRenderer.setSeriesVisible(i, false);
				selectionCombo.addItem(colName);
				if (rpmDataset.getSeriesCount() == 0 && (lcColName.matches(".*rpm.*") || lcColName.matches(".*eng.*speed.*"))) {
					rpmDataset.addSeries(series);
//					rpmPlotRenderer.setSeriesStroke(0, lineStroke);
					rpmPlotRenderer.setSeriesShapesVisible(0, false);
					rpmPlotRenderer.setSeriesVisible(0, false);
					rpmCol = i;
				}
				for (int j = 0; j < logDataTable.getRowCount(); ++j) {
					try {
						val = (String)logDataTable.getValueAt(j, i);
						series.add(j, Double.valueOf(val), false);
					}
					catch (Exception e) {
			            JOptionPane.showMessageDialog(null, "Invalid numeric value in column " + colName + ", row " + (j + 1), "Invalid value", JOptionPane.ERROR_MESSAGE);
			            return;
					}
				}
				series.fireSeriesChanged();
			}
			if (logDataTable.getControlPanel().getComponentCount() > 7)
				logDataTable.getControlPanel().remove(7);
			logDataTable.getControlPanel().add(new JLabel("   [" + file.getName() + "]"));
			initColors();
        }
        catch (Exception ex) {
    		ex.printStackTrace();
    		logger.error(ex);
    	}
    	finally {
            setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    	}
	}
	
	private void updateChart() {
    	Column col;
    	String colName;
        String val;
    	XYSeries series;
		int seriesIdx = 0;
		for (int i = 0; i < logDataTable.getColumnCount(); ++i) {
			col = logDataTable.getColumn(i);
			colName = col.getHeaderValue().toString();
	    	series = dataset.getSeries(seriesIdx++);
			if (!series.getDescription().equals(colName)) {
	            JOptionPane.showMessageDialog(null, "Invalid series found for the column index " + i + ": series name " + series.getDescription() + " doesn't match column name " + colName, "Invalid value", JOptionPane.ERROR_MESSAGE);
				return;
			}
			series.clear();
			for (int j = 0; j < logDataTable.getRowCount(); ++j) {
				try {
					val = (String)logDataTable.getValueAt(j, i);
					series.add(j, Double.valueOf(val), false);
				}
				catch (Exception e) {
					JOptionPane.showMessageDialog(null, "Invalid numeric value in column " + colName + ", row " + (j + 1), "Invalid value", JOptionPane.ERROR_MESSAGE);
					return;
				}
			}
			series.fireSeriesChanged();
		}
		chartPanel.repaint();
	}

    private void view3dPlots() {
    	if (xAxisColumn.getSelectedItem() == null || yAxisColumn.getSelectedItem() == null || plotsColumn.getSelectedItems() == null)
    		return;
        plot3d.removeAllPlots();
        String val;
    	String xAxisColName = (String)xAxisColumn.getSelectedItem();
    	String yAxisColName = (String)yAxisColumn.getSelectedItem();
    	List<String> dataColNames = plotsColumn.getSelectedItems();
    	if (dataColNames.size() > 5) {
            JOptionPane.showMessageDialog(null, "Sorry, only 5 plots are supported. More plots will make the graph too slow.", "Too many parameters", JOptionPane.ERROR_MESSAGE);
            return;
    	}

        int xColIdx = logDataTable.getColumnByHeaderName(xAxisColName).getModelIndex() - 1;
        int yColIdx = logDataTable.getColumnByHeaderName(yAxisColName).getModelIndex() - 1;
    	ArrayList<Color> colorsArray = new ArrayList<Color>();
    	colorsArray.add(Color.BLUE);
    	colorsArray.add(Color.RED);
    	colorsArray.add(Color.GREEN);
    	colorsArray.add(Color.ORANGE);
    	colorsArray.add(Color.GRAY);
    	double x, y, z;
    	XYZ xyz;
    	for (int j = 0; j < dataColNames.size(); ++j) {
    		HashSet<XYZ> uniqueXYZ = new HashSet<XYZ>();
	        int zColIdx = logDataTable.getColumnByHeaderName(dataColNames.get(j)).getModelIndex() - 1;
	        int count = 0;
	        double[][] xyzArrayTemp = new double[logDataTable.getRowCount()][3];
	        for (int i = 0; i < logDataTable.getRowCount(); ++i) {
	            val = (String)logDataTable.getValueAt(i, xColIdx);
	            x = Double.valueOf(val);
	            val = (String)logDataTable.getValueAt(i, yColIdx);
	            y = Double.valueOf(val);
	            val = (String)logDataTable.getValueAt(i, zColIdx);
	            z = Double.valueOf(val);
	            xyz = new XYZ(x, y, z);
	            if (uniqueXYZ.contains(xyz))
	            	continue;
	            uniqueXYZ.add(xyz);
	            xyzArrayTemp[count][0] = x;
	            xyzArrayTemp[count][1] = y;
	            xyzArrayTemp[count][2] = z;
	            count += 1;
	        }
	    	double[][] xyzArray = new double[uniqueXYZ.size()][3];
	    	for (int k = 0; k < xyzArray.length; ++k)
	    		System.arraycopy(xyzArrayTemp[k], 0, xyzArray[k], 0, 3);
	        plot3d.addScatterPlot(dataColNames.get(j), colorsArray.get(j), xyzArray);
    	}
        plot3d.setAxisLabel(0, xAxisColumn.getSelectedItem().toString());
        plot3d.setAxisLabel(1, yAxisColumn.getSelectedItem().toString());
    }

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == loadButton)
			loadLogFile();
		else if (e.getSource() == printButton)
			logDataTable.print(new PrintProperties());
	    else if (e.getSource() == previewButton)
	    	logDataTable.printPreview(new PrintProperties());
	    else if (e.getSource() == findButton)
	    	logDataTable.doFind();
	    else if (e.getSource() == replaceButton)
	    	logDataTable.doFindAndReplace();
	    else if (e.getSource() == filterButton ) {
	    	String filterString = filterText.getText();
	    	if (filterString != null && !"".equals(filterString)) {
	    		try {
		    		CompareFilter filter = new CompareFilter();
		    		filter.setCondition(CompareFilter.Condition.EQUAL);
		    		if (compareCombo.getSelectedItem().toString().equals(">"))
		    			filter.setCondition(CompareFilter.Condition.GREATER);
		    		if (compareCombo.getSelectedItem().toString().equals(">="))
		    			filter.setCondition(CompareFilter.Condition.GREATER_EQUAL);
		    		else if (compareCombo.getSelectedItem().toString().equals("<"))
		    			filter.setCondition(CompareFilter.Condition.LESS);
		    		else if (compareCombo.getSelectedItem().toString().equals("<="))
		    			filter.setCondition(CompareFilter.Condition.LESS_EQUAL);
		    		filter.setFilter(filterText.getText());
		    		filter.setColumn(selectionCombo.getSelectedIndex());
		    		logDataTable.filter(filter);
			    	updateChart();
	    		}
	    		catch (NumberFormatException ex) {
		            JOptionPane.showMessageDialog(null, "Invalid numeric value: " + filterText.getText(), "Invalid value", JOptionPane.ERROR_MESSAGE);
	    		}
	    	}
	    	else {
	    		logDataTable.filter(null);
		    	updateChart();
	    	}
	    }
		else if ("view".equals(e.getActionCommand()))
			view3dPlots();
	}

}
