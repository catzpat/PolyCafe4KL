package Controller;

import Controller.DBConnection;
import java.sql.*;
import Model.User;
import Model.Products;
import java.util.ArrayList;
import java.util.List;

public class DAO {

// Đăng nhập
    public User login(String NameAccount, String PasswordAccount) {
        try (Connection c = DBConnection.connect()) {
            String query = "SELECT * FROM V_Account WHERE NameAccount = ? AND PasswordAccount = ?";
            PreparedStatement ps = c.prepareStatement(query);
            ps.setString(1, NameAccount);
            ps.setString(2, PasswordAccount);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                User u = new User();
                u.setNameAccount(rs.getString("NameAccount"));
                u.setPasswordAccount(rs.getString("PasswordAccount"));
                u.setRoleAccount(rs.getString("RoleAccount"));
                u.setAccountStatus(rs.getBoolean("AccountStatus"));
                return u;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

// Lấy thông tin user theo NameAccount
    public User getUserByNameAccount(String NameAccount) {
        User user = null;
        String sql = "SELECT * FROM V_Account WHERE NameAccount = ?";
        try (Connection con = DBConnection.connect(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, NameAccount);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                user = new User();
                user.setIdAccount(rs.getInt("IDAccount"));
                user.setNameAccount(rs.getString("NameAccount"));
                user.setPasswordAccount(rs.getString("PasswordAccount"));
                user.setEmail(rs.getString("Email"));
                user.setUserName(rs.getString("UserName"));
                user.setSex(rs.getString("Sex"));
                user.setRoleAccount(rs.getString("RoleAccount"));
                user.setAccountStatus(rs.getBoolean("AccountStatus"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return user;
    }

    public int changePW(String NameAccount, String OldPassword, String NewPassword) {
        String sql = "{? = CALL SP_ChangePW(?, ?, ?)}";
        try (Connection conn = DBConnection.connect(); CallableStatement cs = conn.prepareCall(sql)) {

            cs.registerOutParameter(1, Types.INTEGER); // return value
            cs.setString(2, NameAccount);
            cs.setString(3, OldPassword);
            cs.setString(4, NewPassword);

            cs.execute();
            return cs.getInt(1); // Lấy kết quả: 1 (OK), 0 (sai)

        } catch (SQLException e) {
            e.printStackTrace();
            return -1; // lỗi kết nối
        }
    }

// Lấy tất cả sản phẩm
    public List<Products> getAllProducts() {
        List<Products> list = new ArrayList<>();
        String sql = "SELECT * FROM Products";

        try (Connection conn = DBConnection.connect(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Products p = new Products(
                        rs.getString("MaSP"),
                        rs.getNString("TenSP"),
                        rs.getNString("LoaiSP"),
                        rs.getInt("Gia"),
                        rs.getString("HinhAnh"),
                        rs.getInt("TrangThaiBan")
                );
                list.add(p);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

// Thêm sản phẩm mới
    public boolean insertProduct(Products p) {
        String sql = "INSERT INTO Products (MaSP, TenSP, LoaiSP, Gia, HinhAnh, TrangThaiBan) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection c = DBConnection.connect(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, p.getMaSP());
            ps.setNString(2, p.getTenSP());
            ps.setNString(3, p.getLoaiSP());
            ps.setInt(4, p.getGia());
            ps.setString(5, p.getHinhAnh());
            ps.setInt(6, p.getTrangThai());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

// Cập nhật sản phẩm
    public boolean updateProduct(Products p) {
        String sql = "UPDATE Products SET TenSP = ?, LoaiSP = ?, Gia = ?, HinhAnh = ?, TrangThaiBan = ? WHERE MaSP = ?";
        try (Connection c = DBConnection.connect(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setNString(1, p.getTenSP());
            ps.setNString(2, p.getLoaiSP());
            ps.setInt(3, p.getGia());
            ps.setString(4, p.getHinhAnh());
            ps.setInt(5, p.getTrangThai());
            ps.setString(6, p.getMaSP());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

// Ẩn sản phẩm (TrangThaiBan = 1)
    public boolean hideProduct(String maSP) {
        String sql = "UPDATE Products SET TrangThaiBan = 1 WHERE MaSP = ?";
        try (Connection c = DBConnection.connect(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, maSP);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

// Xóa sản phẩm khỏi DB (chỉ dùng khi phát triển)
    public boolean deleteProduct(String maSP) {
        String sql = "DELETE FROM Products WHERE MaSP = ?";
        try (Connection c = DBConnection.connect(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, maSP);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

// Tạo mã sp theo loại - ThemSP - CNT3
    public int TaoMaSPTheoLoai(String tienTo) {
        int count = 0;
        String sql = "SELECT COUNT(*) FROM Products WHERE MaSP LIKE ?";
        try (Connection conn = DBConnection.connect(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, tienTo + "%");
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                count = rs.getInt(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return count;
    }

// Truy vấn sản phẩm theo mã - SuaSP - CNT3
    public Products getProductById(String maSP) {
        String sql = "SELECT * FROM Products WHERE MaSP = ?";
        try (Connection conn = DBConnection.connect(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maSP);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Products(
                        rs.getString("MaSP"),
                        rs.getString("TenSP"),
                        rs.getString("LoaiSP"),
                        rs.getInt("Gia"),
                        rs.getString("HinhAnh"),
                        rs.getInt("TrangThaiBan")
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

// Hóa đơn
    public boolean insertHoaDon(String maHD, String NameAccount, int tongTien, int giamGia, int thanhToan, int tienMat, int tienTraLai) {
        String sql = "INSERT INTO HoaDon (MaHD, NgayLap, NameAccount, TongTien, GiamGia, ThanhToan, TienMat, TienTraLai) "
                + "VALUES (?, GETDATE(), ?, ?, ?, ?, ?, ?)";
        try (Connection con = DBConnection.connect(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, maHD);
            ps.setString(2, NameAccount);
            ps.setInt(3, tongTien);
            ps.setInt(4, giamGia);
            ps.setInt(5, thanhToan);
            ps.setInt(6, tienMat);
            ps.setInt(7, tienTraLai);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean insertChiTietHoaDon(String maHD, List<Object[]> chiTietList) {
        String sql = "INSERT INTO ChiTietHoaDon (MaHD, TenSP, DonGia, SoLuong, ThanhTien) VALUES (?, ?, ?, ?, ?)";
        try (Connection con = DBConnection.connect(); PreparedStatement ps = con.prepareStatement(sql)) {
            for (Object[] row : chiTietList) {
                ps.setString(1, maHD);
                ps.setString(2, row[0].toString());
                ps.setInt(3, parseTien(row[1].toString()));
                ps.setInt(4, Integer.parseInt(row[2].toString()));
                ps.setInt(5, parseTien(row[3].toString()));
                ps.addBatch();
            }
            ps.executeBatch();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private int parseTien(String tien) {
        try {
            return Integer.parseInt(tien.replace(" ", "").replace("đ", "").trim());
        } catch (Exception e) {
            return 0;
        }
    }

    public boolean insertHoaDonCho(String maHD, String thoiGian, String NameAccount) {
        String sql = "INSERT INTO HoaDonCho (MaHD, ThoiGian, NameAccount) VALUES (?,?,?)";
        try (Connection c = DBConnection.connect(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, maHD);
            ps.setString(2, thoiGian);
            ps.setString(3, NameAccount);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Lấy mã hóa đơn lớn nhất từ db
    public int getMaxSoHoaDon() {
        String sql = "SELECT MAX(CAST(SUBSTRING(MaHD, 3, LEN(MaHD)) AS INT)) AS maxHD FROM HoaDon";
        try (Connection con = DBConnection.connect(); Statement st = con.createStatement()) {
            ResultSet rs = st.executeQuery(sql);
            if (rs.next()) {
                return rs.getInt("maxHD");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0; // nếu không có hóa đơn nào
    }

    public String taoMaHoaDonMoi() {
        int so = getMaxSoHoaDon() + 1;
        return String.format("HD%03d", so); // Ví dụ: HD001, HD002
    }

    // Đếm tổng số đơn hàng trong bảng Hóa Đơn
    public int demTongSoDonHang() {
        String sql = "SELECT COUNT(*) FROM HoaDon";
        return getIntResult(sql);
    }

    // Đếm tổng số hóa đơn chờ trong bảng HóaDonCho
    public int demHoaDonCho() {
        String sql = "SELECT COUNT(*) FROM HoaDonCho";
        return getIntResult(sql);
    }

    // Đếm số hóa đơn đã thanh toán (ThanhToan >= TongTien)
    public int demHoaDonDaThanhToan() {
        String sql = "SELECT COUNT(*) FROM HoaDon WHERE ThanhToan >= TongTien";
        return getIntResult(sql);
    }

    // Lấy toàn bộ danh sách hóa đơn
    public List<Object[]> getAllHoaDon() {
        List<Object[]> list = new ArrayList<>();
        String sql = "SELECT MaHD, FORMAT(NgayLap, 'dd/MM/yyyy'), TongTien, "
                + "CASE WHEN ThanhToan >= TongTien THEN N'Đã thanh toán' ELSE N'Chưa thanh toán' END AS TrangThai, "
                + "CASE WHEN TienMat > 0 THEN N'Tiền mặt' ELSE N'Chuyển khoản' END AS HinhThuc "
                + "FROM HoaDon";
        try (Connection con = DBConnection.connect(); PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Object[] row = {
                    rs.getString("MaHD"),
                    rs.getString(2),
                    rs.getInt("TongTien"),
                    rs.getString("TrangThai"),
                    rs.getString("HinhThuc")
                };
                list.add(row);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // Tìm hóa đơn theo mã
    public List<Object[]> selectHoaDonByMa(String maHD) {
        List<Object[]> list = new ArrayList<>();
        String sql = "SELECT MaHD, FORMAT(NgayLap, 'dd/MM/yyyy'), TongTien, "
                + "CASE WHEN ThanhToan >= TongTien THEN N'Đã thanh toán' ELSE N'Chưa thanh toán' END AS TrangThai, "
                + "CASE WHEN TienMat > 0 THEN N'Tiền mặt' ELSE N'Chuyển khoản' END AS HinhThuc "
                + "FROM HoaDon WHERE MaHD LIKE ?";
        try (Connection con = DBConnection.connect(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, "%" + maHD + "%");
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Object[] row = {
                    rs.getString("MaHD"),
                    rs.getString(2),
                    rs.getInt("TongTien"),
                    rs.getString("TrangThai"),
                    rs.getString("HinhThuc")
                };
                list.add(row);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // Lọc hóa đơn theo trạng thái, hình thức, ngày - TAB4
    public List<Object[]> locHoaDon(String trangThai, String hinhThuc, Date ngayChon) {
        List<Object[]> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
                "SELECT MaHD, FORMAT(NgayLap, 'dd/MM/yyyy'), TongTien, "
                + "CASE WHEN ThanhToan >= TongTien THEN N'Đã thanh toán' ELSE N'Chưa thanh toán' END AS TrangThai, "
                + "CASE WHEN TienMat > 0 THEN N'Tiền mặt' ELSE N'Chuyển khoản' END AS HinhThuc "
                + "FROM HoaDon WHERE 1=1"
        );

        List<Object> params = new ArrayList<>();

        if (!trangThai.equals("Tất cả")) {
            if (trangThai.equals("Đã thanh toán")) {
                sql.append(" AND ThanhToan >= TongTien");
            } else if (trangThai.equals("Chưa thanh toán")) {
                sql.append(" AND ThanhToan < TongTien");
            }
        }

        if (!hinhThuc.equals("Tất cả")) {
            if (hinhThuc.equals("Tiền mặt")) {
                sql.append(" AND TienMat > 0");
            } else if (hinhThuc.equals("Chuyển khoản")) {
                sql.append(" AND TienMat = 0");
            }
        }

        if (ngayChon != null) {
            sql.append(" AND CONVERT(DATE, NgayLap) = ?");
            params.add(new java.sql.Date(ngayChon.getTime()));
        }

        try (Connection con = DBConnection.connect(); PreparedStatement ps = con.prepareStatement(sql.toString())) {

            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Object[] row = {
                    rs.getString("MaHD"),
                    rs.getString(2),
                    rs.getInt("TongTien"),
                    rs.getString("TrangThai"),
                    rs.getString("HinhThuc")
                };
                list.add(row);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    // Lấy danh sách chi tiết hóa đơn theo mã
    public List<Object[]> getChiTietHoaDon(String maHD) {
        List<Object[]> list = new ArrayList<>();
        String sql = "SELECT TenSP, DonGia, SoLuong, ThanhTien FROM ChiTietHoaDon WHERE MaHD = ?";
        try (Connection con = DBConnection.connect(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, maHD);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Object[] row = {
                    rs.getString("TenSP"),
                    rs.getInt("DonGia"),
                    rs.getInt("SoLuong"),
                    rs.getInt("ThanhTien")
                };
                list.add(row);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // Lấy thông tin của hóa đơn
    public Object[] getThongTinHoaDon(String maHD) {
        String sql = "SELECT FORMAT(NgayLap, 'dd/MM/yyyy HH:mm'), MaNV, TongTien, GiamGia, ThanhToan, TienMat, TienTraLai FROM HoaDon WHERE MaHD = ?";
        try (Connection con = DBConnection.connect(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, maHD);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Object[]{
                    rs.getString(1), // Ngày lập
                    rs.getString(2), // Mã NV
                    rs.getInt(3), // Tổng tiền SP
                    rs.getInt(4), // Giảm giá
                    rs.getInt(5), // Thành tiền
                    rs.getInt(6), // Tiền mặt
                    rs.getInt(7) // Tiền trả lại
                };
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // Hàm dùng chung để lấy 1 số nguyên từ câu truy vấn
    private int getIntResult(String sql) {
        try (Connection con = DBConnection.connect(); PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    //TAB6
    // Lấy danh sách sản phẩm bán được, sắp xếp từ cao xuống thấp, bao gồm cả sản phẩm không bán được
    public List<Object[]> thongKeSanPham() {
        List<Object[]> list = new ArrayList<>();

        String sql
                = "SELECT p.MaSP, p.TenSP, p.LoaiSP, p.Gia, p.HinhAnh, p.TrangThaiBan, "
                + "       ISNULL(SUM(ct.SoLuong), 0) AS SoLuongBan "
                + "FROM Products p "
                + "LEFT JOIN ChiTietHoaDon ct ON p.TenSP = ct.TenSP "
                + "GROUP BY p.MaSP, p.TenSP, p.LoaiSP, p.Gia, p.HinhAnh, p.TrangThaiBan "
                + "ORDER BY SoLuongBan DESC";

        try (Connection con = DBConnection.connect(); PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Object[] row = new Object[]{
                    rs.getString("MaSP"),
                    rs.getString("TenSP"),
                    rs.getString("LoaiSP"),
                    rs.getInt("Gia"),
                    rs.getString("HinhAnh"),
                    rs.getString("TrangThaiBan"),
                    rs.getInt("SoLuongBan")
                };
                list.add(row);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    // Tổng doanh thu
    public int getTongDoanhThu() {
        String sql = "SELECT SUM(ThanhToan) AS Tong FROM HoaDon";
        try (Connection con = DBConnection.connect(); PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                int value = rs.getInt("Tong");
                return rs.wasNull() ? 0 : value;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    // Tổng số hóa đơn
    public int getTongSoHoaDon() {
        String sql = "SELECT COUNT(*) AS SoLuong FROM HoaDon";
        try (Connection con = DBConnection.connect(); PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt("SoLuong");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    // Tổng số sản phẩm đã bán
    public int getTongSanPhamBan() {
        String sql = "SELECT SUM(SoLuong) AS Tong FROM ChiTietHoaDon";
        try (Connection con = DBConnection.connect(); PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                int value = rs.getInt("Tong");
                return rs.wasNull() ? 0 : value;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
}
