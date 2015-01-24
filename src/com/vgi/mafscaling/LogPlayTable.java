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
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import java.util.regex.Pattern;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import org.apache.log4j.Logger;

public class LogPlayTable extends JDialog {
	private static final long serialVersionUID = 3534186983281827915L;
	private static final Logger logger = Logger.getLogger(LogPlayTable.class);
	
    private static final int ColumnWidth = 50;
    private ExcelAdapter excelAdapter = null;
    private JTextField xText = null;
    private JTextField yText = null;
    private JTextField valueText = null;
    private JTable playTable = null;
    private JPanel glasspanel = null;
    private JPanel dataPanel = null;
    private ArrayList<Double> xaxis = new ArrayList<Double>();
    private ArrayList<Double> yaxis = new ArrayList<Double>();
    private Color highlight = new Color(255, 0, 255, 128);
    private Color cellHighlight = new Color(96, 96, 96, 128);
    private Insets insetsText = new Insets(3, 3, 3, 3);
    private Insets insetsLabel = new Insets(3, 3, 3, 0);
    private Object lock = new Object();
    private Rectangle r;
    private Double xVal = null;
    private Double yVal = null;
    private double diameter;
    private double cellWidth;
    private double radius;
    private double x, y, xPos, yPos, val, xCellVal, yCellVal;
    private double x0, x1, y0, y1, x0y0, x1y0, x0y1, x1y1;
    private int xIdx, yIdx, x0Idx, x1Idx, y0Idx, y1Idx, xMult, yMult;
    
    public LogPlayTable(Window owner, String tableName) {
    	super(owner, tableName);
    	final JDialog frame = this;
    	excelAdapter = new ExcelAdapter() {
    	    protected void onPaste(JTable table, boolean extendRows, boolean extendCols) {
    	    	super.onPaste(table, extendRows, extendCols);
    	    	validateTable(table);
    	    	diameter = table.getCellRect(0, 0, false).getHeight();
    	    	radius = diameter / 2.0;
    	    	cellWidth = table.getCellRect(0, 0, false).getWidth();
    	    	frame.pack();
    	    }
    	};
    	initialize();
        setLocationRelativeTo(owner);
    }

    /**
     * Initialize the contents of the frame.
     */
    private void initialize() {
    	try {
    		createGlassPanel();
    		createDataPanel();
	        
	        pack();
	        setVisible(true);
	        setResizable(false);
	        setAlwaysOnTop(true);
	        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	        setIconImage(null);
    	}
    	catch (Exception e) {
            logger.error(e);
    	}
    }

    private boolean validateTable(JTable table) {
        if (table == null)
            return false;
        // check if table is empty
        if (Utils.isTableEmpty(table))
        	return true;
        // check paste format
        if (!table.getValueAt(0, 0).toString().equalsIgnoreCase("[table3d]") &&
            !((table.getValueAt(0, 0).toString().equals("")) &&
              Pattern.matches(Utils.fpRegex, table.getValueAt(0, 1).toString()) &&
              Pattern.matches(Utils.fpRegex, table.getValueAt(1, 0).toString()))) {
            JOptionPane.showMessageDialog(null, "Invalid data in table!\n\nPlease paste correct table into first cell", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        int i;
        if (table.getValueAt(0, 0).toString().equalsIgnoreCase("[table3d]")) {
            // realign if paste is from RomRaider
            if (table.getValueAt(0, 1).toString().equals("")) {
                Utils.removeRow(0, table);
                for (i = table.getColumnCount() - 2; i >= 0; --i)
                    table.setValueAt(table.getValueAt(0, i), 0, i + 1);
                table.setValueAt("", 0, 0);
            }
            // paste is probably from excel, just blank out the first cell
            else
                table.setValueAt("", 0, 0);
        }
        // remove extra rows
        for (i = table.getRowCount() - 1; i >= 0 && table.getValueAt(i, 0).toString().equals(""); --i)
            Utils.removeRow(i, table);
        // remove extra columns
        for (i = table.getColumnCount() - 1; i >= 0 && table.getValueAt(0, i).toString().equals(""); --i)
            Utils.removeColumn(i, table);
        // validate all cells are numeric
        for (i = 0; i < table.getRowCount(); ++i) {
            for (int j = 0; j < table.getColumnCount(); ++j) {
                if (i == 0 && j == 0)
                    continue;
                if (!Pattern.matches(Utils.fpRegex, table.getValueAt(i, j).toString())) {
                    JOptionPane.showMessageDialog(null, "Invalid value at row " + (i + 1) + " column " + (j + 1), "Error", JOptionPane.ERROR_MESSAGE);
                    return false;
                }
            }
        }
        for (i = 1; i < table.getColumnCount(); ++i)
        	xaxis.add(Double.valueOf(table.getValueAt(0, i).toString()));
        for (i = 1; i < table.getRowCount(); ++i)
        	yaxis.add(Double.valueOf(table.getValueAt(i, 0).toString()));
        
        Utils.colorTable(table);
        return true;
    }
    
    public void setCurrentPoint(double x, double y) {
		synchronized (lock) {
	    	xVal = x;
	    	yVal = y;
		}
    	glasspanel.repaint();
    }
    
    private void createGlassPanel() {
		glasspanel = new JPanel() {
			private static final long serialVersionUID = -9053819700839609869L;

			public void paintComponent(Graphics g) {
				if (xaxis.size() > 0 && yaxis.size() > 0 && xVal != null && yVal != null) {
					synchronized (lock) {
						x = xVal;
						y = yVal;
					}
					xIdx = Utils.closestValueIndex(x, xaxis);
					yIdx = Utils.closestValueIndex(y, yaxis);
					r = playTable.getCellRect(yIdx + 1, xIdx + 1, false);
					
    				xCellVal = xaxis.get(xIdx);
    				xMult = 1;
    				if (x < xCellVal) {
						x1Idx = xIdx;
						x1 = xCellVal;
    					if (xIdx > 0) {
    						xPos = playTable.getCellRect(yIdx + 1, xIdx, false).getX();
    						xMult = 2;
    						x0Idx = xIdx - 1;
    						x0 = xaxis.get(x0Idx);
    					}
    					else {
    						xPos = r.getX();
    						x0Idx = x1Idx;
    						x0 = x1;
    					}
    				}
    				else if (x > xCellVal) {
    					x0Idx = xIdx;
    					x0 = xCellVal;
						xPos = r.getX();
    					if (xIdx < xaxis.size() - 1) {
    						xMult = 2;
    						x1Idx = xIdx + 1;
    						x1 = xaxis.get(x1Idx);
    					}
    					else {
    						x1Idx = x0Idx;
    						x1 = x0;
    					}
    				}
    				else {
    					x0Idx = x1Idx = xIdx;
    					x0 = x1 = xCellVal;
    					xPos = r.getX();
    				}
    				yMult = 1;
    				yCellVal = yaxis.get(yIdx);
    				if (y < yCellVal) {
						y1Idx = yIdx;
						y1 = yCellVal;
    					if (yIdx > 0) {
    						yPos = playTable.getCellRect(yIdx, xIdx + 1, false).getY();
    						yMult = 2;
    						y0Idx = yIdx - 1;
    						y0 = yaxis.get(y0Idx);
    					}
    					else {
    						yPos = r.getY();
    						y0Idx = y1Idx;
    						y0 = y1;
    					}
    				}
    				else if (y > yCellVal) {
						y0Idx = yIdx;
    					y0 = yCellVal;
						yPos = r.getY();
    					if (yIdx < yaxis.size() - 1) {
    						yMult = 2;
    						y1Idx = yIdx + 1;
    						y1 = yaxis.get(y1Idx);
    					}
    					else {
    						y1Idx = y0Idx;
    						y1 = y0;
    					}
    				}
    				else {
    					y0Idx = y1Idx = yIdx;
    					y0 = y1 = yCellVal;
    					yPos = r.getY();
    				}

    				x0y0 = Double.valueOf(playTable.getValueAt(y0Idx + 1, x0Idx + 1).toString());
    				x1y0 = Double.valueOf(playTable.getValueAt(y0Idx + 1, x1Idx + 1).toString());
    				x0y1 = Double.valueOf(playTable.getValueAt(y1Idx + 1, x0Idx + 1).toString());
    				x1y1 = Double.valueOf(playTable.getValueAt(y1Idx + 1, x1Idx + 1).toString());
    				
    				val = Utils.table3DInterpolation(xVal, yVal, x0, x1, y0, y1, x0y0, x0y1, x1y0, x1y1);
    		        
    		        xText.setText(String.format("%.2f", x));
    		        yText.setText(String.format("%.2f", y));
    		        valueText.setText(String.format("%.2f", val));

    				g.setColor(cellHighlight);
    				g.fillRect((int)xPos, (int)yPos, (int)(cellWidth * xMult), (int)(diameter * yMult));
    				g.setColor(Color.BLACK);
    				g.drawRect((int)xPos, (int)yPos, (int)(cellWidth * xMult), (int)(diameter * yMult));
    				g.setColor(highlight);
    				Graphics2D g2 = (Graphics2D)g;
    				Ellipse2D.Double circle = new Ellipse2D.Double(r.getCenterX() - radius, r.getCenterY() - radius, diameter, diameter);
    				g2.fill(circle);
    				g2.setColor(Color.BLACK);
    				g2.draw(circle);
				}
			}
		};
		setGlassPane(glasspanel);
		glasspanel.setOpaque(false);
		glasspanel.setVisible(true);
    }

    private void createDataPanel() {
        dataPanel = new JPanel();
        GridBagLayout gbl_dataPanel = new GridBagLayout();
        gbl_dataPanel.columnWidths = new int[] {0, 0, 0, 0, 0, 0};
        gbl_dataPanel.rowHeights = new int[] {0, 0};
        gbl_dataPanel.columnWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 1.0};
        gbl_dataPanel.rowWeights = new double[]{1.0, 0.0};
        dataPanel.setLayout(gbl_dataPanel);
        getContentPane().add(dataPanel);
        
        addTable();
        addLabel(0, "X:");
        xText = addTextBox(1);
        addLabel(2, "Y:");
        yText = addTextBox(3);
        addLabel(4, "Value:");
        valueText = addTextBox(5);
    }
    
    private void addTable() {
        playTable = new JTable();
        playTable.setColumnSelectionAllowed(true);
        playTable.setCellSelectionEnabled(true);
        playTable.setBorder(new LineBorder(new Color(0, 0, 0)));
        playTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF); 
        playTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        playTable.setModel(new DefaultTableModel(2, 2));
        playTable.setTableHeader(null);
        Utils.initializeTable(playTable, ColumnWidth);
        excelAdapter.addTable(playTable, true, true, false, false, true, false, true, true, true);
        
        GridBagConstraints gbc_playTable = new GridBagConstraints();
        gbc_playTable.anchor = GridBagConstraints.PAGE_START;
        gbc_playTable.insets = new Insets(0, 0, 0, 0);
        gbc_playTable.fill = GridBagConstraints.BOTH;
        gbc_playTable.weightx = 1.0;
        gbc_playTable.weighty = 1.0;
        gbc_playTable.gridx = 0;
        gbc_playTable.gridy = 0;
        gbc_playTable.gridwidth = 6;
        dataPanel.add(playTable, gbc_playTable);
    }
    
    private void addLabel(int column, String text) {
        JLabel label = new JLabel(text);
        GridBagConstraints gbc_label = new GridBagConstraints();
        gbc_label.anchor = GridBagConstraints.WEST;
        gbc_label.insets = insetsLabel;
        gbc_label.gridx = column;
        gbc_label.gridy = 1;
        dataPanel.add(label, gbc_label);
    }
    
    private JTextField addTextBox(int column) {
    	JTextField text = new JTextField();
    	text.setColumns(6);
    	text.setEditable(false);
    	text.setText("0");
    	text.setHorizontalAlignment(SwingConstants.RIGHT);
        GridBagConstraints gbc_text = new GridBagConstraints();
        gbc_text.anchor = GridBagConstraints.WEST;
        gbc_text.insets = insetsText;
        gbc_text.gridx = column;
        gbc_text.gridy = 1;
        dataPanel.add(text, gbc_text);
        return text;
    }
}
