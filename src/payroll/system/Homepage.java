/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package payroll.system;
import java.awt.BorderLayout;
import java.awt.Color;
import java.util.Calendar;
import java.util.GregorianCalendar;
import javax.swing.JOptionPane;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.table.DefaultTableModel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.xy.DefaultXYDataset;

/**
 *
 * @author Acer
 */
public final class Homepage extends javax.swing.JFrame {
    Connection conn = null;
    Statement st = null;
    PreparedStatement pst = null;
    ResultSet rs = null;
    String username;

    Calendar cal = new GregorianCalendar();
    SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
    String date = sdf.format(cal.getTime());

    int second = cal.get(Calendar.SECOND);
    int minute = cal.get(Calendar.MINUTE);
    int hour = cal.get(Calendar.HOUR);
    DecimalFormat df = new DecimalFormat("00");
    String time = df.format(hour) + ":" + df.format(minute) + ":" + df.format(second);
    /**
     * Creates new form Homepage
     */
    public Homepage() {
        initComponents();
        
        top_sales_panel.setVisible(false);
        hide_button.setVisible(false);
        
        conn = db.java_db();
        greeting();
        showGenderPieChart();
        showCommissionScatterPlot();
        showEmployee();
    }
    
    public Homepage(String name){
        this.username = name;
        initComponents();
        
        top_sales_panel.setVisible(false);
        hide_button.setVisible(false);
        
        conn = db.java_db();
        greeting();
        showGenderPieChart();
        showCommissionScatterPlot();
        showEmployee();
    }
    
    private void closeResources() {
        try {
            if (rs != null) {
                rs.close();
            }
            if (st != null) {
                st.close();
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }
    
    private void greeting(){
        Calendar cal = new GregorianCalendar();
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        
        if(hour <12)
            greeting_label.setText("Good Morning");
        else
            greeting_label.setText("Good Evening");
    }
    
    public void showGenderPieChart() {
        try {
            String genderQuery = "SELECT employee_gender, COUNT(*) AS count FROM Employee GROUP BY employee_gender";
            st = conn.createStatement();
            rs = st.executeQuery(genderQuery);

            DefaultPieDataset pieDataset = new DefaultPieDataset();

            while (rs.next()) {
                String gender = rs.getString("employee_gender");
                int count = rs.getInt("count");
                pieDataset.setValue(gender, count);
            }

            JFreeChart pieChart = ChartFactory.createPieChart("Gender", pieDataset, false, true, false);

            PiePlot piePlot = (PiePlot) pieChart.getPlot();

            // Set colors for each section
            piePlot.setSectionPaint("Male", new Color(100, 149, 237));
            piePlot.setSectionPaint("Female", new Color(255, 105, 180));

            piePlot.setBackgroundPaint(Color.white);

            // Create chartPanel to display the chart (graph)
            ChartPanel pieChartPanel1 = new ChartPanel(pieChart);
            gender_bar_chart.removeAll();
            gender_bar_chart.add(pieChartPanel1, BorderLayout.CENTER);
            gender_bar_chart.validate();
            System.out.println("Pie chart displayed successfully!");

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e);
            System.err.println("SQL Exception: " + e.getMessage());
        } finally {
            // Close resources in the finally block
            closeResources();
        }
    }
    public void showCommissionScatterPlot() {
        try {
            String commissionQuery = "SELECT emp_id, salary_commission FROM Salary";
            st = conn.createStatement();
            rs = st.executeQuery(commissionQuery);

            DefaultXYDataset xyDataset = new DefaultXYDataset();

            while (rs.next()) {
                int empId = rs.getInt("emp_id");
                double commission = rs.getDouble("salary_commission");
                xyDataset.addSeries(Integer.toString(empId), new double[][]{{empId}, {commission}});
            }

            JFreeChart scatterPlot = ChartFactory.createScatterPlot(
                    "Commission Scatter Plot", // Chart title
                    "Employee ID", // X-axis label
                    "Commission", // Y-axis label
                    xyDataset,
                    PlotOrientation.VERTICAL,
                    false, // Include legend
                    true,
                    false
            );

            XYPlot xyPlot = (XYPlot) scatterPlot.getPlot();
            xyPlot.setDomainPannable(true);
            xyPlot.setRangePannable(true);


            xyPlot.setBackgroundPaint(Color.white);
            // Create chartPanel to display the scatter plot
            ChartPanel scatterPlotPanel = new ChartPanel(scatterPlot);
            commission_chart.removeAll();
            commission_chart.add(scatterPlotPanel, BorderLayout.CENTER);
            commission_chart.validate();
            System.out.println("Commission Scatter Plot displayed successfully!");

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e);
            System.err.println("SQL Exception: " + e.getMessage());
        } finally {
            // Close resources in the finally block
            closeResources();
        }
    }

    private void showEmployee() {
        try {
            String sql;
            String sort = Objects.toString(sorting_combobox.getSelectedItem());
            
            if (null == sort){
                sql = "select employee_id, employee_name, employee_gender, employee_ic, employee_age, employee_email, "
                        + "employee_phone_number, employee_position, employee_basic_salary from Employee order by employee_id";
            }
            else sql = switch (sort) {
                case "Name (asc)" -> "select employee_id, employee_name, employee_gender, employee_ic, employee_age, employee_email, "
                    + "employee_phone_number, employee_position, employee_basic_salary from Employee order by employee_name";
                case "Name (des)" -> "select employee_id, employee_name, employee_gender, employee_ic, employee_age, employee_email, "
                    + "employee_phone_number, employee_position, employee_basic_salary from Employee order by employee_name DESC";
                case "Age (asc)" -> "select employee_id, employee_name, employee_gender, employee_ic, employee_age, employee_email, "
                    + "employee_phone_number, employee_position, employee_basic_salary from Employee order by employee_age";
                case "Age (des)" -> "select employee_id, employee_name, employee_gender, employee_ic, employee_age, employee_email, "
                    + "employee_phone_number, employee_position, employee_basic_salary from Employee order by employee_age DESC";
                default -> "select employee_id, employee_name, employee_gender, employee_ic, employee_age, employee_email, "
                    + "employee_phone_number, employee_position, employee_basic_salary from Employee order by employee_id";
            };
            
            st = conn.createStatement();
            rs = st.executeQuery(sql);  
            
            DefaultTableModel tblModel = (DefaultTableModel) employee_table.getModel();
            tblModel.setRowCount(0);
            
            while(rs.next()){
                String id = String.valueOf(rs.getInt("employee_id"));
                String name = rs.getString("employee_name");
                String gender = rs.getString("employee_gender");
                String ic = rs.getString("employee_ic");
                String age = String.valueOf(rs.getInt("employee_age"));
                String email = rs.getString("employee_email");
                String phone = rs.getString("employee_phone_number");
                String position = rs.getString("employee_position");
                String basic = rs.getString("employee_basic_salary");
                
                String tbData[] = {id, name, gender, ic, age, email, phone, position, basic};  
                tblModel.addRow(tbData);
            }
   
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e);
        } finally {
            closeResources();
        }
    }


    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        side_menu = new javax.swing.JPanel();
        dashboard_button1 = new javax.swing.JButton();
        employee_button = new javax.swing.JButton();
        calculation_button = new javax.swing.JButton();
        slip_button = new javax.swing.JButton();
        logo = new javax.swing.JLabel();
        payroll_label = new javax.swing.JLabel();
        payroll_label1 = new javax.swing.JLabel();
        system_label = new javax.swing.JLabel();
        audit_button = new javax.swing.JButton();
        main_panel = new javax.swing.JPanel();
        logout_button = new javax.swing.JButton();
        greeting_label = new javax.swing.JLabel();
        dashboard = new javax.swing.JLabel();
        list_employee_title = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        employee_table = new javax.swing.JTable();
        commission_chart = new javax.swing.JPanel();
        gender_bar_chart = new javax.swing.JPanel();
        jSeparator1 = new javax.swing.JSeparator();
        view_top_sales = new javax.swing.JButton();
        top_sales_panel = new javax.swing.JPanel();
        top_sales_name = new javax.swing.JLabel();
        top_sales = new javax.swing.JLabel();
        top_sales_title = new javax.swing.JLabel();
        sorting_combobox = new javax.swing.JComboBox<>();
        hide_button = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        setSize(new java.awt.Dimension(1200, 680));

        side_menu.setBackground(new java.awt.Color(0, 33, 120));
        side_menu.setPreferredSize(new java.awt.Dimension(226, 550));

        dashboard_button1.setBackground(new java.awt.Color(250, 255, 255));
        dashboard_button1.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        dashboard_button1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/payroll/system/images/dashboard_icon.png"))); // NOI18N
        dashboard_button1.setText("Dashboard");
        dashboard_button1.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        dashboard_button1.setIconTextGap(40);
        dashboard_button1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dashboard_button1ActionPerformed(evt);
            }
        });

        employee_button.setBackground(new java.awt.Color(0, 30, 120));
        employee_button.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        employee_button.setForeground(new java.awt.Color(255, 255, 255));
        employee_button.setIcon(new javax.swing.ImageIcon(getClass().getResource("/payroll/system/images/empmanagement_icon.png"))); // NOI18N
        employee_button.setText(" Employee Management");
        employee_button.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        employee_button.setIconTextGap(5);
        employee_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                employee_buttonActionPerformed(evt);
            }
        });

        calculation_button.setBackground(new java.awt.Color(0, 30, 120));
        calculation_button.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        calculation_button.setForeground(new java.awt.Color(255, 255, 255));
        calculation_button.setIcon(new javax.swing.ImageIcon(getClass().getResource("/payroll/system/images/calculation_icon.png"))); // NOI18N
        calculation_button.setText("Salary Calculation");
        calculation_button.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        calculation_button.setIconTextGap(23);
        calculation_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                calculation_buttonActionPerformed(evt);
            }
        });

        slip_button.setBackground(new java.awt.Color(0, 30, 120));
        slip_button.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        slip_button.setForeground(new java.awt.Color(255, 255, 255));
        slip_button.setIcon(new javax.swing.ImageIcon(getClass().getResource("/payroll/system/images/slip_icon.png"))); // NOI18N
        slip_button.setText("Salary Slip");
        slip_button.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        slip_button.setIconTextGap(38);
        slip_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                slip_buttonActionPerformed(evt);
            }
        });

        logo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/payroll/system/images/logo1.png"))); // NOI18N
        logo.setPreferredSize(new java.awt.Dimension(100, 130));

        payroll_label.setFont(new java.awt.Font("Californian FB", 0, 32)); // NOI18N
        payroll_label.setForeground(new java.awt.Color(255, 255, 255));
        payroll_label.setText("HR &");

        payroll_label1.setFont(new java.awt.Font("Californian FB", 0, 32)); // NOI18N
        payroll_label1.setForeground(new java.awt.Color(255, 255, 255));
        payroll_label1.setText("Payroll");

        system_label.setFont(new java.awt.Font("Californian FB", 0, 32)); // NOI18N
        system_label.setForeground(new java.awt.Color(255, 255, 255));
        system_label.setText("System");

        audit_button.setBackground(new java.awt.Color(0, 30, 120));
        audit_button.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        audit_button.setForeground(new java.awt.Color(255, 255, 255));
        audit_button.setIcon(new javax.swing.ImageIcon(getClass().getResource("/payroll/system/images/history_icon.png"))); // NOI18N
        audit_button.setText("Audit Trail");
        audit_button.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        audit_button.setIconTextGap(50);
        audit_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                audit_buttonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout side_menuLayout = new javax.swing.GroupLayout(side_menu);
        side_menu.setLayout(side_menuLayout);
        side_menuLayout.setHorizontalGroup(
            side_menuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(side_menuLayout.createSequentialGroup()
                .addGroup(side_menuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(side_menuLayout.createSequentialGroup()
                        .addGap(32, 32, 32)
                        .addComponent(logo, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(side_menuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(payroll_label, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(payroll_label1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 105, Short.MAX_VALUE)
                            .addComponent(system_label, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(side_menuLayout.createSequentialGroup()
                        .addGap(15, 15, 15)
                        .addGroup(side_menuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(audit_button, javax.swing.GroupLayout.PREFERRED_SIZE, 260, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(side_menuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(slip_button, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 260, Short.MAX_VALUE)
                                .addComponent(calculation_button, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 260, Short.MAX_VALUE)
                                .addComponent(employee_button, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 260, Short.MAX_VALUE)
                                .addComponent(dashboard_button1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 260, Short.MAX_VALUE)))))
                .addContainerGap(15, Short.MAX_VALUE))
        );
        side_menuLayout.setVerticalGroup(
            side_menuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(side_menuLayout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addGroup(side_menuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, side_menuLayout.createSequentialGroup()
                        .addComponent(payroll_label)
                        .addGap(1, 1, 1)
                        .addComponent(payroll_label1)
                        .addGap(1, 1, 1)
                        .addComponent(system_label))
                    .addComponent(logo, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(30, 30, 30)
                .addComponent(dashboard_button1, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(20, 20, 20)
                .addComponent(employee_button, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(20, 20, 20)
                .addComponent(calculation_button, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(20, 20, 20)
                .addComponent(slip_button, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(20, 20, 20)
                .addComponent(audit_button, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        main_panel.setBackground(new java.awt.Color(255, 255, 255));
        main_panel.setMinimumSize(new java.awt.Dimension(10, 0));

        logout_button.setBackground(new java.awt.Color(250, 255, 255));
        logout_button.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        logout_button.setIcon(new javax.swing.ImageIcon(getClass().getResource("/payroll/system/images/logout_icon.png"))); // NOI18N
        logout_button.setText("Logout");
        logout_button.setIconTextGap(12);
        logout_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                logout_buttonActionPerformed(evt);
            }
        });

        greeting_label.setFont(new java.awt.Font("Segoe UI", 0, 42)); // NOI18N
        greeting_label.setText("greeting");

        dashboard.setFont(new java.awt.Font("Segoe UI", 0, 36)); // NOI18N
        dashboard.setText("Dashboard");

        list_employee_title.setFont(new java.awt.Font("Segoe UI", 0, 22)); // NOI18N
        list_employee_title.setText("List of Employees");

        employee_table.setBackground(new java.awt.Color(250, 255, 255));
        employee_table.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Employee ID", "Name", "Gender", "IC", "Age", "Email", "Phone Number", "Position", "Basic Salary"
            }
        ));
        employee_table.setFocusable(false);
        employee_table.setGridColor(new java.awt.Color(204, 255, 255));
        employee_table.setRequestFocusEnabled(false);
        employee_table.setSelectionBackground(new java.awt.Color(250, 255, 255));
        jScrollPane1.setViewportView(employee_table);
        if (employee_table.getColumnModel().getColumnCount() > 0) {
            employee_table.getColumnModel().getColumn(0).setMinWidth(80);
            employee_table.getColumnModel().getColumn(0).setMaxWidth(80);
            employee_table.getColumnModel().getColumn(1).setMinWidth(150);
            employee_table.getColumnModel().getColumn(1).setMaxWidth(150);
            employee_table.getColumnModel().getColumn(2).setMinWidth(80);
            employee_table.getColumnModel().getColumn(2).setMaxWidth(80);
            employee_table.getColumnModel().getColumn(4).setMinWidth(50);
            employee_table.getColumnModel().getColumn(4).setMaxWidth(50);
            employee_table.getColumnModel().getColumn(8).setMinWidth(80);
            employee_table.getColumnModel().getColumn(8).setMaxWidth(80);
        }

        commission_chart.setBackground(new java.awt.Color(204, 255, 255));
        commission_chart.setMaximumSize(new java.awt.Dimension(400, 300));
        commission_chart.setLayout(new java.awt.BorderLayout());

        gender_bar_chart.setBackground(new java.awt.Color(204, 255, 255));
        gender_bar_chart.setMaximumSize(new java.awt.Dimension(400, 300));
        gender_bar_chart.setLayout(new java.awt.BorderLayout());

        jSeparator1.setBackground(new java.awt.Color(0, 0, 0));
        jSeparator1.setForeground(new java.awt.Color(255, 250, 255));

        view_top_sales.setBackground(new java.awt.Color(250, 255, 255));
        view_top_sales.setIcon(new javax.swing.ImageIcon(getClass().getResource("/payroll/system/images/champion.png"))); // NOI18N
        view_top_sales.setText("View Top Sales");
        view_top_sales.setIconTextGap(1);
        view_top_sales.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                view_top_salesActionPerformed(evt);
            }
        });

        top_sales_panel.setBackground(new java.awt.Color(250, 255, 255));
        top_sales_panel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        top_sales_name.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N

        top_sales.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N

        top_sales_title.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N

        javax.swing.GroupLayout top_sales_panelLayout = new javax.swing.GroupLayout(top_sales_panel);
        top_sales_panel.setLayout(top_sales_panelLayout);
        top_sales_panelLayout.setHorizontalGroup(
            top_sales_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(top_sales_panelLayout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addGroup(top_sales_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(top_sales_name, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(top_sales, javax.swing.GroupLayout.DEFAULT_SIZE, 176, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, top_sales_panelLayout.createSequentialGroup()
                        .addComponent(top_sales_title, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(29, 29, 29)))
                .addContainerGap(15, Short.MAX_VALUE))
        );
        top_sales_panelLayout.setVerticalGroup(
            top_sales_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(top_sales_panelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(top_sales_title, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(top_sales_name, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(top_sales, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(9, Short.MAX_VALUE))
        );

        sorting_combobox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Empoyee id", "Name (asc)", "Name (des)", "Age (asc)", "Age (des)", " " }));
        sorting_combobox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sorting_comboboxActionPerformed(evt);
            }
        });

        hide_button.setBackground(new java.awt.Color(250, 255, 255));
        hide_button.setIcon(new javax.swing.ImageIcon(getClass().getResource("/payroll/system/images/hide.png"))); // NOI18N
        hide_button.setText("Hide");
        hide_button.setIconTextGap(15);
        hide_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                hide_buttonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout main_panelLayout = new javax.swing.GroupLayout(main_panel);
        main_panel.setLayout(main_panelLayout);
        main_panelLayout.setHorizontalGroup(
            main_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(main_panelLayout.createSequentialGroup()
                .addGap(26, 26, 26)
                .addGroup(main_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(main_panelLayout.createSequentialGroup()
                        .addGroup(main_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(main_panelLayout.createSequentialGroup()
                                .addComponent(gender_bar_chart, javax.swing.GroupLayout.DEFAULT_SIZE, 405, Short.MAX_VALUE)
                                .addGap(40, 40, 40)
                                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 11, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(40, 40, 40))
                            .addGroup(main_panelLayout.createSequentialGroup()
                                .addGroup(main_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(main_panelLayout.createSequentialGroup()
                                        .addGap(9, 9, 9)
                                        .addComponent(dashboard, javax.swing.GroupLayout.PREFERRED_SIZE, 182, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(greeting_label, javax.swing.GroupLayout.PREFERRED_SIZE, 341, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(128, 128, 128)))
                        .addGroup(main_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(main_panelLayout.createSequentialGroup()
                                .addComponent(top_sales_panel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 27, Short.MAX_VALUE)
                                .addGroup(main_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(view_top_sales, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(logout_button, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(hide_button, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                            .addComponent(commission_chart, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(main_panelLayout.createSequentialGroup()
                        .addComponent(list_employee_title, javax.swing.GroupLayout.PREFERRED_SIZE, 177, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(sorting_combobox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(20, 20, 20))
        );
        main_panelLayout.setVerticalGroup(
            main_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(main_panelLayout.createSequentialGroup()
                .addGroup(main_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(main_panelLayout.createSequentialGroup()
                        .addGroup(main_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(main_panelLayout.createSequentialGroup()
                                .addGap(36, 36, 36)
                                .addComponent(greeting_label))
                            .addGroup(main_panelLayout.createSequentialGroup()
                                .addGap(20, 20, 20)
                                .addComponent(logout_button, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 0, 0)
                        .addGroup(main_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(dashboard)
                            .addGroup(main_panelLayout.createSequentialGroup()
                                .addComponent(hide_button)
                                .addGap(3, 3, 3)
                                .addComponent(view_top_sales, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(10, 10, 10))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, main_panelLayout.createSequentialGroup()
                        .addComponent(top_sales_panel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)))
                .addGroup(main_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jSeparator1)
                    .addGroup(main_panelLayout.createSequentialGroup()
                        .addGroup(main_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(gender_bar_chart, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(main_panelLayout.createSequentialGroup()
                                .addComponent(commission_chart, javax.swing.GroupLayout.DEFAULT_SIZE, 220, Short.MAX_VALUE)
                                .addGap(1, 1, 1)))
                        .addGap(27, 27, 27)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(main_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(main_panelLayout.createSequentialGroup()
                        .addComponent(list_employee_title)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, main_panelLayout.createSequentialGroup()
                        .addComponent(sorting_combobox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(4, 4, 4)))
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 203, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(22, 22, 22))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(1, 1, 1)
                .addComponent(side_menu, javax.swing.GroupLayout.PREFERRED_SIZE, 290, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(1, 1, 1)
                .addComponent(main_panel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(side_menu, javax.swing.GroupLayout.DEFAULT_SIZE, 680, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addGap(1, 1, 1)
                .addComponent(main_panel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(1, 1, 1))
        );

        setSize(new java.awt.Dimension(1200, 680));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void slip_buttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_slip_buttonActionPerformed
        // TODO add your handling code here:
        SalarySlip s = new SalarySlip(username);
        s.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_slip_buttonActionPerformed

    private void calculation_buttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_calculation_buttonActionPerformed
        // TODO add your handling code here:
        SalaryCalculation c = new SalaryCalculation(username);
        c.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_calculation_buttonActionPerformed

    private void employee_buttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_employee_buttonActionPerformed
        // TODO add your handling code here:
        EmployeeManagement e = new EmployeeManagement(username);
        e.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_employee_buttonActionPerformed

    private void dashboard_button1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dashboard_button1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_dashboard_button1ActionPerformed

    private void view_top_salesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_view_top_salesActionPerformed
        // TODO add your handling code here:
        try {
            top_sales_panel.setVisible(true);
            String topSalesQuery = "SELECT e.employee_name, s.salary_commission " +
            "FROM Employee e " +
            "JOIN Salary s ON e.employee_id = s.emp_id " +
            "ORDER BY s.salary_commission DESC LIMIT 1";
            st = conn.createStatement();
            rs = st.executeQuery(topSalesQuery);

            if (rs.next()) {
                String topSalesName = rs.getString("employee_name");
                double topSalesValue = rs.getDouble("salary_commission");

                top_sales_title.setText("Top Sales: ");
                top_sales_name.setText("Employee: " + topSalesName);
                top_sales.setText("Commission Value: " + topSalesValue);
            } else {
                // Handle the case where no data is retrieved
                JOptionPane.showMessageDialog(null, "No data found for top sales.");
            }
            view_top_sales.setVisible(false);
            hide_button.setVisible(true);

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e);
            System.err.println("SQL Exception: " + e.getMessage());
        } finally {
            // Close resources in the finally block
            closeResources();
        }
    }//GEN-LAST:event_view_top_salesActionPerformed

    private void logout_buttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_logout_buttonActionPerformed
        // TODO add your handling code here:
        int option = JOptionPane.showOptionDialog(
            this,
            "Are you sure you want to logout?",
            "Confirm Logout",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE,
            null,
            new Object[]{"Logout", "Cancel"},
            "Logout"
        );

        if (option == JOptionPane.YES_OPTION) {
            Login x = new Login();
            x.setVisible(true);
            this.dispose();
            
            String action = "Logout as " + username;
            String auditSQL = "INSERT INTO Audit (audit_date, audit_time, audit_type, audit_action) VALUES (?,?,?,?)";
            try {
                pst = conn.prepareStatement(auditSQL);
                pst.setString(1, date);
                pst.setString(2, time);
                pst.setString(3, "Login & Logout");
                pst.setString(4, action);
                pst.executeUpdate();
            } catch (SQLException ex) {
                Logger.getLogger(Homepage.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_logout_buttonActionPerformed

    private void sorting_comboboxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sorting_comboboxActionPerformed
        // TODO add your handling code here:
        showEmployee();
    }//GEN-LAST:event_sorting_comboboxActionPerformed

    private void hide_buttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_hide_buttonActionPerformed
        // TODO add your handling code here:
        top_sales_panel.setVisible(false);
        hide_button.setVisible(false);
        view_top_sales.setVisible(true);
    }//GEN-LAST:event_hide_buttonActionPerformed

    private void audit_buttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_audit_buttonActionPerformed
        // TODO add your handling code here:
        Audit a = new Audit(username);
        a.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_audit_buttonActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Homepage.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Homepage.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Homepage.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Homepage.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> {
            new Login().setVisible(true);
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton audit_button;
    private javax.swing.JButton calculation_button;
    private javax.swing.JPanel commission_chart;
    private javax.swing.JLabel dashboard;
    private javax.swing.JButton dashboard_button1;
    private javax.swing.JButton employee_button;
    private javax.swing.JTable employee_table;
    private javax.swing.JPanel gender_bar_chart;
    private javax.swing.JLabel greeting_label;
    private javax.swing.JButton hide_button;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JLabel list_employee_title;
    private javax.swing.JLabel logo;
    private javax.swing.JButton logout_button;
    private javax.swing.JPanel main_panel;
    private javax.swing.JLabel payroll_label;
    private javax.swing.JLabel payroll_label1;
    private javax.swing.JPanel side_menu;
    private javax.swing.JButton slip_button;
    private javax.swing.JComboBox<String> sorting_combobox;
    private javax.swing.JLabel system_label;
    private javax.swing.JLabel top_sales;
    private javax.swing.JLabel top_sales_name;
    private javax.swing.JPanel top_sales_panel;
    private javax.swing.JLabel top_sales_title;
    private javax.swing.JButton view_top_sales;
    // End of variables declaration//GEN-END:variables
}
