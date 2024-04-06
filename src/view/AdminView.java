package view;

import business.BookManager;
import business.BrandManager;
import business.CarManager;
import business.ModelManager;
import core.ComboItem;
import core.Helper;
import entity.Brand;
import entity.Car;
import entity.Model;
import entity.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.MaskFormatter;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.ParseException;
import java.util.ArrayList;



public class AdminView extends  Layout{
    private JPanel container;
    private JLabel lbl_welcome;
    private JPanel pnl_top;
    private JTabbedPane tab_menu;
    private JButton btn_logout;
    private JPanel pnl_brand;
    private JScrollPane scl_brand;
    private JTable tbl_brand;
    private JPanel pnl_model;
    private JScrollPane scl_model;
    private JTable tbl_model;
    private JTable tbl_car;

    private JComboBox<ComboItem> cmb_s_model_brand;

    private JComboBox <ComboItem> cmb_plate_filter;

    private JComboBox<Model.Type> cmb_s_model_type;
    private JComboBox <Model.Fuel>cmb_s_model_fuel;
    private JComboBox <Model.Gear>cmb_s_model_gear;


    private JButton btn_search_model;
    private JButton btn_cncl_model;
    private JPanel pnl_car;
    private JPanel pnl_booking_search;
    private JTable tbl_booking;
    private JComboBox<Model.Gear> cmb_booking_gear;
    private JComboBox<Model.Fuel> cmb_booking_fuel;
    private JComboBox<Model.Type> cmb_booking_type;
    private JFormattedTextField fld_start_date;
    private JFormattedTextField fld_fnsh_date;
    private JButton btn_booking_search;
    private JButton btn_cncl_booking;
    private JPanel pnl_booking_list;
    private JTable tbl_rentals;

    private User user;
    private DefaultTableModel tmdl_brand = new DefaultTableModel();
    private DefaultTableModel tmdl_model = new DefaultTableModel();
    private DefaultTableModel tmdl_car = new DefaultTableModel();
    private DefaultTableModel tmdl_booking = new DefaultTableModel();
    private DefaultTableModel tmdl_rentals = new DefaultTableModel();
    private BrandManager brandManager;
    private ModelManager modelManager;
    private CarManager carManager;
    private BookManager bookManager;

    private JPopupMenu brand_Menu;
    private JPopupMenu model_Menu;
    private JPopupMenu car_Menu;
    private JPopupMenu booking_Menu;
    private Object[] col_model;
    private Object[] col_car;






    public AdminView(User user){
        this.brandManager = new BrandManager();
        this.modelManager = new ModelManager();
        this.add(container);
        this.guiInitilaze(1000,500);
        this.user = user;
        this.carManager = new CarManager();

        if(this.user == null){
            dispose();
        }

        this.lbl_welcome.setText("Hoşgeldiniz : " + this.user.getRole());

        loadBrandTable();
        loadBrandComponent();

        loadModelTable(null);
        loadModelComponent();
        loadModelFilter();

        loadCarTable();
        loadCarComponent();

        loadBookTable(null);
        loadBookComponent();
        loadBookingFilter();


    }



    private void loadComponent(){
        this.btn_logout.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                LoginView loginView = new LoginView();
            }
        });
    }


    private void loadBookComponent(){
        tableRowSelect(this.tbl_booking);
        this.booking_Menu = new JPopupMenu();
        this.booking_Menu.add("Rezervasyon Yap").addActionListener(e ->{
            int selectCarId = this.getTableSelectedRow(this.tbl_booking, 0);
            BookingView bookingView = new BookingView(this.carManager.getById(selectCarId), this.fld_start_date.getText(),
                    this.fld_fnsh_date.getText());

            bookingView.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosed(WindowEvent e) {
                    loadBookTable(null);
                    loadBookingFilter();
                }
            });
    });
        this.tbl_booking.setComponentPopupMenu(booking_Menu);

        btn_booking_search.addActionListener(e -> {
            ArrayList<Car> carList = this.carManager.searchForBooking(fld_start_date.getText(), fld_fnsh_date.getText(),
                    (Model.Type) cmb_booking_type.getSelectedItem(),
                    (Model.Gear) cmb_booking_gear.getSelectedItem(),
                    (Model.Fuel) cmb_booking_fuel.getSelectedItem()

            );

            ArrayList<Object[]> carBookingRow = this.carManager.getForTable(this.col_car.length, carList);
            loadBookTable(carBookingRow);
        });

        btn_cncl_booking.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e){
                loadBookingFilter();
            }
        });
    }

    private void loadBookTable(ArrayList<Object[]> carList){
        Object[] col_booking_list = {"ID", "Marka", "Model", "Plaka", "Renk", "KM", "Yıl", "Tip", "Yakıt Türü", "Vites"};
        createTable(this.tmdl_booking, this.tbl_booking, col_booking_list, carList);
    }

    public void loadBookingFilter(){
        this.cmb_booking_type.setModel(new DefaultComboBoxModel<>(Model.Type.values()));
        this.cmb_booking_type.setSelectedItem(null);
        this.cmb_booking_gear.setModel(new DefaultComboBoxModel<>(Model.Gear.values()));
        this.cmb_booking_gear.setSelectedItem(null);
        this.cmb_booking_fuel.setModel(new DefaultComboBoxModel<>(Model.Fuel.values()));
        this.cmb_booking_fuel.setSelectedItem(null);
    }


    private void loadCarComponent() {
        this.tableRowSelect(this.tbl_car);

        this.car_Menu = new JPopupMenu();
        this.car_Menu.add("Yeni").addActionListener(e -> {
            CarView carView = new CarView(new Car());
            carView.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosed(WindowEvent e) {
                    loadCarTable();
                }
            });
        });
        this.car_Menu.add("Güncelle").addActionListener(e -> {
            int selectCarlId   = this.getTableSelectedRow(tbl_car,0);
            CarView carView = new CarView(this.carManager.getById(selectCarlId));
            carView.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosed(WindowEvent e) {
                    loadCarTable();
                }
            });
        });
        this.car_Menu.add("Sil").addActionListener(e -> {
            if(Helper.confirm("sure")){
                int selectCarId   = this.getTableSelectedRow(tbl_car,0);
                if(this.carManager.delete(selectCarId)){
                    Helper.showMsg("done");

                    loadCarTable();
                }else {
                    Helper.showMsg("error");
                }
            }
        });
        this.tbl_car.setComponentPopupMenu(this.car_Menu);
        }


    public void loadCarTable(){
        col_car = new Object[]{"ID", "Marka", "Model", "Plaka", "Renk", "KM", "Yıl", "Tip", "Yakıt Türü", "Vites"};
        ArrayList<Object[]> carList = this.carManager.getForTable(col_car.length, this.carManager.findAll());
        createTable(this.tmdl_car, this.tbl_car, col_car, carList);
    }

    private void loadModelComponent() {
        tableRowSelect(this.tbl_booking);
        this.model_Menu = new JPopupMenu();
        this.model_Menu.add("Yeni").addActionListener(e ->{
            ModelView modelView = new ModelView(new Model());
            modelView.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosed(WindowEvent e) {
                    loadModelTable(null);
                }
            });
        });
        this.model_Menu.add("Güncelle").addActionListener(e -> {
            int selectModelId = this.getTableSelectedRow(tbl_booking, 0);
            ModelView modelView = new ModelView(this.modelManager.getById(selectModelId));
            modelView.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosed(WindowEvent e) {
                    loadModelTable(null);
                    loadCarTable();
                    loadBookTable(null);
                }
            });

        });

        this.model_Menu.add("Sil").addActionListener(e -> {
            if(Helper.confirm("sure")){
                int selectModelId = this.getTableSelectedRow(tbl_brand,0);
                if(this.modelManager.delete(selectModelId)){
                    Helper.showMsg("done");
                    loadModelTable(null);

                }else{
                    Helper.showMsg("error");
                }
            }


        });

        this.tbl_booking.setComponentPopupMenu(model_Menu);

        this.btn_search_model.addActionListener(e -> {
            ComboItem selectedBrand = (ComboItem) this.cmb_s_model_brand.getSelectedItem();
            int brandId = 0;
            if(selectedBrand != null){
                brandId = selectedBrand.getKey();
            }
            ArrayList<Model> modelListBySearch = this.modelManager.searchForTable(
                    brandId,
                    (Model.Fuel) cmb_s_model_fuel.getSelectedItem(),
                    (Model.Gear) cmb_s_model_gear.getSelectedItem(),
                    (Model.Type) cmb_s_model_type.getSelectedItem());

            ArrayList<Object[]> modelRowListBySearch = this.modelManager.getForTable(this.col_model.length, modelListBySearch);
            loadModelTable(modelRowListBySearch);

        });
        this.btn_cncl_model.addActionListener(e -> {
            this.cmb_s_model_type.setSelectedItem(null);
            this.cmb_s_model_gear.setSelectedItem(null);
            this.cmb_s_model_fuel.setSelectedItem(null);
            this.cmb_s_model_brand.setSelectedItem(null);
            loadModelTable(null);

        });
    }

    public void loadModelTable(ArrayList<Object[]> modelList){
        this.col_model = new Object[]{"Model ID", "Marka", "Model Adı", "Tip", "Yıl", "Yakıt Türü", "Vites"};
        if(modelList == null){
            modelList = this.modelManager.getForTable(this.col_model.length, this.modelManager.findAll());
        }
        createTable(this.tmdl_model, this.tbl_booking, col_model, modelList);
    }

    public void loadModelFilter(){
        this.cmb_s_model_type.setModel(new DefaultComboBoxModel<>(Model.Type.values()));
        this.cmb_s_model_type.setSelectedItem(null);
        this.cmb_s_model_gear.setModel(new DefaultComboBoxModel<>(Model.Gear.values()));
        this.cmb_s_model_gear.setSelectedItem(null);
        this.cmb_s_model_fuel.setModel(new DefaultComboBoxModel<>(Model.Fuel.values()));
        this.cmb_s_model_fuel.setSelectedItem(null);
        loadModelFilterBrand();
    }

    public void loadBookFilterPlate() {
        this.cmb_plate_filter.removeAllItems();
        for (Brand obj : brandManager.findAll()) {
            this.cmb_plate_filter.addItem(new ComboItem(obj.getId(), obj.getName()));
        }
        this.cmb_plate_filter.setSelectedItem(null);
    }

    public void loadModelFilterBrand(){
        this.cmb_s_model_brand.removeAllItems();
        for(Brand obj : brandManager.findAll()){
            this.cmb_s_model_brand.addItem(new ComboItem(obj.getId(), obj.getName()));
        }
        this.cmb_s_model_brand.setSelectedItem(null);

    }

    public void loadBrandComponent(){
        tableRowSelect(this.tbl_brand);
        this.brand_Menu = new JPopupMenu();
        this.brand_Menu.add("Yeni").addActionListener(e ->{
            BrandView brandView = new BrandView(null);
            brandView.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosed(WindowEvent e) {
                    loadBrandTable();
                    loadModelTable(null);
                    loadModelFilterBrand();
                    loadCarTable();

                }
            });

        });
        this.brand_Menu.add("Güncelle").addActionListener(e -> {
            int selectBrandId = this.getTableSelectedRow(tbl_brand, 0);
            BrandView brandView = new BrandView(this.brandManager.getById(selectBrandId));
            brandView.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosed(WindowEvent e) {
                    loadBrandTable();
                    loadModelTable(null);
                    loadModelFilterBrand();
                    loadCarTable();
                }
            });
        });

        this.brand_Menu.add("Sil").addActionListener(e -> {
            if(Helper.confirm("sure")){
            int selectBrandId = this.getTableSelectedRow(tbl_brand,0);
                if(this.brandManager.delete(selectBrandId)){
                    Helper.showMsg("done");
                    loadBrandTable();
                    loadModelTable(null);
                    loadModelFilterBrand();
                    loadCarTable();

                }else{
                    Helper.showMsg("error");
                }
            }

        });

        this.tbl_brand.setComponentPopupMenu(this.brand_Menu);


    }


    public void loadBrandTable(){

        Object[] col_brand = {"Marka ID", "Marka Adı"};
        ArrayList<Object[]> brandList = this.brandManager.getForTable(col_brand.length);
        this.createTable(this.tmdl_brand, this.tbl_brand, col_brand, brandList);

    }

    private void createUIComponents() throws ParseException {
            this.fld_start_date = new JFormattedTextField(new MaskFormatter("##/##/####"));
            this.fld_start_date.setText("10/10/2023");
            this.fld_fnsh_date = new JFormattedTextField(new MaskFormatter("##/##/####"));
            this.fld_fnsh_date.setText("16/10/2023");



    }
}

