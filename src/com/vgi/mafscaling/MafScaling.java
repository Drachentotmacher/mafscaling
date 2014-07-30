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
import java.awt.EventQueue;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import javax.swing.UIManager;
import org.apache.log4j.Logger;

public class MafScaling {
    private static final Logger logger = Logger.getLogger(MafScaling.class);
    private static final String Title = "MAF Scaling - v1.5.3";
    private static final String LVTabName = "<html>Log View</html>";
    private static final String OLTabName = "<html>Open Loop</html>";
    private static final String CLTabName = "<html>Closed Loop</html>";
    private static final String RTabName = "<html>Rescale</html>";
    private static final String LCTabName = "<html>Load Comp</html>";
    private static final String LSTabName = "<html>Log Stats</html>";
    private JFrame frame;

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }
        catch (Exception e) {
            e.printStackTrace();
            logger.error(e);
        }
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    MafScaling window = new MafScaling();
                    window.frame.setVisible(true);
                }
                catch (Exception e) {
                    e.printStackTrace();
                    logger.error(e);
                }
            }
        });
    }

    /**
     * Create the application.
     */
    public MafScaling() {
        Config.load();
        initialize();
    }

    /**
     * Initialize the contents of the frame.
     */
    private void initialize() {
    	PrimaryOpenLoopFuelingTable pofFuelingTable = new PrimaryOpenLoopFuelingTable();
        MafCompare mafCompare = new MafCompare();

        ImageIcon chartImage = new ImageIcon(getClass().getResource("/chart.jpg"));
    	
        frame = new JFrame();
        frame.setTitle(Title);
        frame.setBounds(100, 100, 621, 372);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(Config.getWindowSize());
        frame.setLocation(Config.getWindowLocation());
        frame.setIconImage(chartImage.getImage());
        frame.addWindowListener(new WindowAdapter() {
        	public void windowClosing(WindowEvent e) {
                Config.setWindowSize(frame.getSize());
                Config.setWindowLocation(frame.getLocation());
        		Config.save();
        	}
        });
        
        JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
        tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
        frame.getContentPane().add(tabbedPane, BorderLayout.CENTER);
        
        JTabbedPane ol = new OpenLoop(JTabbedPane.LEFT, pofFuelingTable, mafCompare);
        ol.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
        tabbedPane.add(ol, OLTabName);

        JTabbedPane cl = new ClosedLoop(JTabbedPane.LEFT, pofFuelingTable, mafCompare);
        cl.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
        tabbedPane.add(cl, CLTabName);

        JTabbedPane r = new Rescale(JTabbedPane.LEFT);
        r.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
        tabbedPane.add(r, RTabName);

        JTabbedPane lc = new LoadComp(JTabbedPane.LEFT);
        lc.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
        tabbedPane.add(lc, LCTabName);

        JTabbedPane ls = new LogStats(JTabbedPane.LEFT);
        ls.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
        tabbedPane.add(ls, LSTabName);
        
        JTabbedPane lv = new LogView(JTabbedPane.LEFT);
        lv.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
        tabbedPane.add(lv, LVTabName);
    }
}
