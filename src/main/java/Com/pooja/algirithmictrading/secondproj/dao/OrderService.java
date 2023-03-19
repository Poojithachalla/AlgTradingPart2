package Com.pooja.algirithmictrading.secondproj.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import Com.pooja.algirithmictrading.secondproj.model.Order;

public class OrderService {

	private OrderDAO orderDAO = new OrderDAO();

//Adding order details in to DB
	public void addOrder(Order order) {
		order.setOrder_status("Pending");
		Connection connection = null;
		PreparedStatement statement = null;
		PreparedStatement pstatement = null;
		int priority_queue;
		try {
			orderDAO.loadDriver(orderDAO.dbDriver);
			connection = orderDAO.getConnection();
			statement = connection.prepareStatement("SELECT max(priority_queue) FROM Orders");
			ResultSet rs = statement.executeQuery();
			if (rs.next()) {
				priority_queue = rs.getInt(1);

				if (priority_queue == 0) {
					priority_queue = 1;
					order.setPriority_queue(priority_queue);
				} else {
					priority_queue++;
					order.setPriority_queue(priority_queue++);
				}

			}

			pstatement = connection.prepareStatement(
					"INSERT INTO orders (company_Id, side, quantity, price,priority_queue,order_status,ExecutedQty,PendingQty) VALUES (?, ?, ?, ?, ?, ? ,?, ?)");
			order.setExecutedQty(0);
			double price = order.getPrice();
			order.setPendingQty((int) price);
			pstatement.setString(1, order.getCompany_Id());
			pstatement.setString(2, order.getSide());
			pstatement.setInt(3, order.getQuantity());
			pstatement.setDouble(4, order.getPrice());
			pstatement.setInt(5, order.getPriority_queue());
			pstatement.setString(6, order.getOrder_status());
			pstatement.setInt(7, order.getExecutedQty());
			pstatement.setInt(8, order.getPendingQty());

			int rowsInserted = pstatement.executeUpdate();

			if (rowsInserted > 0) {
				System.out.println("Order added successfully.");
			} else {
				System.out.println("Error adding order.");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (statement != null) {
					statement.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			try {
				if (connection != null) {
					connection.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

//Getting list of Orders from  DB
	public List<Order> getOrder() throws SQLException {
		List<Order> orders = new ArrayList<Order>();
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet resultSet = null;
		String sql = "SELECT * FROM Orders WHERE order_status <> 'Deleted' ORDER BY company_Id, price, side";

		try {
			orderDAO.loadDriver(orderDAO.dbDriver);
			connection = orderDAO.getConnection();
			PreparedStatement statement2 = connection.prepareStatement(sql);

			resultSet = statement2.executeQuery();

			while (resultSet.next()) {
				int order_id = resultSet.getInt("order_id");
				String company_Id = resultSet.getString("company_Id");
				String side = resultSet.getString("side");
				int quantity = resultSet.getInt("quantity");
				double price = resultSet.getDouble("price");
				String order_status = resultSet.getString("order_status");
				int ExecutedQty = resultSet.getInt("ExecutedQty");
				int PendingQty = resultSet.getInt("PendingQty");

				Order order = new Order();
				order.setOrder_id(order_id);
				order.setCompany_Id(company_Id);
				order.setSide(side);
				order.setQuantity(quantity);
				order.setPrice(price);
				order.setOrder_status(order_status);
				order.setExecutedQty(ExecutedQty);
				order.setPendingQty(PendingQty);
				orders.add(order);
			}
		} finally {

			if (resultSet != null) {
				resultSet.close();
			}
			if (statement != null) {
				statement.close();
			}
			if (connection != null) {
				connection.close();
			}

		}

		return orders;
	}

// Fetching List of Orders based on order_id from DB
	public Order fetchOrder(int order_id) throws SQLException {

		Order order = new Order();
		Connection connection = null;
		PreparedStatement statement2 = null;
		ResultSet resultSet = null;
		String sql = "select order_id, company_Id, side, quantity, price, order_status from orders where order_id="
				+ order_id;
		try {
			orderDAO.loadDriver(orderDAO.dbDriver);
			connection = orderDAO.getConnection();
			statement2 = connection.prepareStatement(sql);

			resultSet = statement2.executeQuery();
			if (resultSet.next()) {
				int order_Id = resultSet.getInt("order_id");
				String company_Id = resultSet.getString("company_Id");
				String side = resultSet.getString("side");
				int quantity = resultSet.getInt("quantity");
				double price = resultSet.getDouble("price");
				String order_status = resultSet.getString("order_status");
				order = new Order();
				order.setOrder_id(order_Id);
				order.setCompany_Id(company_Id);
				order.setSide(side);
				order.setQuantity(quantity);
				order.setPrice(price);
				order.setOrder_status(order_status);

			}

		} finally {

			if (resultSet != null) {
				resultSet.close();
			}
			if (statement2 != null) {
				statement2.close();
			}
			if (connection != null) {
				connection.close();
			}

		}
		return order;

	}

//Deleting List of Orders from DB
	public void deleteOrder(int order_id) throws SQLException {

		Connection connection = null;
		PreparedStatement statement3 = null;
		int rowdeletd = 0;
		String sql = "update orders set order_status='Deleted' where order_id=" + order_id;
		;
		try {
			orderDAO.loadDriver(orderDAO.dbDriver);
			connection = orderDAO.getConnection();
			statement3 = connection.prepareStatement(sql);
			rowdeletd = statement3.executeUpdate(sql);
			if (rowdeletd > 0) {
				System.out.println(" row deletd successfully.");
			} else {
				System.out.println("Error order delete.");
			}

		} finally {

			if (statement3 != null) {
				statement3.close();
			}
			if (connection != null) {
				connection.close();
			}

		}

	}

	@SuppressWarnings("resource")
	public void updateOrder(int order_id, int quantity, double price) throws SQLException {
		Order order = new Order();
		order.setPendingQty(quantity);
		int priority_queue = 0;
		Connection connection = null;
		PreparedStatement statement4 = null;
		int updateorder = 0;
		String sql = "update orders set quantity=?, price=?, priority_queue=?, PendingQty=? where order_id=?";

		try {
			orderDAO.loadDriver(orderDAO.dbDriver);
			connection = orderDAO.getConnection();
			statement4 = connection.prepareStatement("SELECT max(priority_queue) FROM Orders");
			ResultSet rs = statement4.executeQuery();
			if (rs.next()) {
				priority_queue = rs.getInt(1);

				if (priority_queue == 0) {
					priority_queue = 1;
					order.setPriority_queue(priority_queue);
				} else {
					priority_queue++;
					order.setPriority_queue(priority_queue);
				}
			}

			statement4 = connection.prepareStatement(sql);
			statement4.setInt(1, quantity);
			statement4.setDouble(2, price);
			statement4.setInt(3, priority_queue);
			statement4.setInt(4, order.getPendingQty());
			statement4.setInt(5, order_id);

			updateorder = statement4.executeUpdate();

			if (updateorder > 0) {
				System.out.println("Order updated");
			} else {
				System.out.println("Order not updated");
			}
		} finally {
			if (statement4 != null) {
				statement4.close();
			}
			if (connection != null) {
				connection.close();
			}
		}
	}

//Getting list of Sides i.e Buy Side and Sale Side values and comparing values based on pendingquantity
	public List<Order> getSides() throws SQLException {
		List<Order> buyorder = new ArrayList<Order>();
		List<Order> saleorder = new ArrayList<Order>();
		String sidecompany_Id = null;
		int sidependingquantity = 0;
		double sideprice = 0.0;
		int sideorder_id = 0;
		int salependingquantity = 0;

		Order order = new Order();

		Connection connection = null;
		PreparedStatement statement5 = null;
		PreparedStatement statement6 = null;
		ResultSet resultSet = null;
		String sql = "SELECT * FROM Orders WHERE side = 'Buy' AND order_status IN ('Pending', 'Partially Executed') ORDER BY company_Id ASC, price DESC, priority_queue ASC";
		try {
			orderDAO.loadDriver(orderDAO.dbDriver);
			connection = orderDAO.getConnection();
			statement5 = connection.prepareStatement(sql);
			resultSet = statement5.executeQuery();
			while (resultSet.next()) {

				int order_Id = resultSet.getInt("order_id");
				String company_Id = resultSet.getString("company_Id");
				String side = resultSet.getString("side");
				int quantity = resultSet.getInt("quantity");
				double price = resultSet.getDouble("price");
				String order_status = resultSet.getString("order_status");
				int PendingQty = resultSet.getInt("PendingQty");
				order = new Order();
				order.setOrder_id(order_Id);
				order.setCompany_Id(company_Id);
				order.setSide(side);
				order.setQuantity(quantity);
				order.setPrice(price);
				order.setOrder_status(order_status);
				order.setPendingQty(PendingQty);

				buyorder.add(order);

			}

			for (Order s : buyorder) {

				for (int i = 0; i < 1; i++) {
					sidecompany_Id = s.getCompany_Id();
					sideprice = s.getPrice();
					sidependingquantity = s.getPendingQty();

					sideorder_id = s.getOrder_id();

				}
				break;
			}
			System.out.println("buyorder is :" + buyorder);

		} catch (Exception e) {

			System.out.println(e.getMessage());
		}
		try {

			String sql2 = "select * from Orders where side = 'Sale' and order_status IN ('Pending', 'Partially Executed')";
			statement6 = connection.prepareStatement(sql2);
			resultSet = statement6.executeQuery();
			while (resultSet.next()) {

				int order_Id = resultSet.getInt("order_id");
				String company_Id = resultSet.getString("company_Id");
				String side = resultSet.getString("side");
				int quantity = resultSet.getInt("quantity");
				double price = resultSet.getDouble("price");
				String order_status = resultSet.getString("order_status");
				int PendingQty = resultSet.getInt("PendingQty");
				order = new Order();
				order.setOrder_id(order_Id);
				order.setCompany_Id(company_Id);
				order.setSide(side);
				order.setQuantity(quantity);
				order.setPrice(price);
				order.setOrder_status(order_status);
				order.setPendingQty(PendingQty);

				saleorder.add(order);

			}

			ArrayList<Order> targetside = new ArrayList<Order>();
			System.out.println(" saleorder is : " + saleorder);
			for (Order saleside : saleorder) {

				if (saleside.getCompany_Id().equals(sidecompany_Id) && saleside.getPrice() <= sideprice) {

					int saleorder_id = saleside.getOrder_id();
					String salecompany_Id = saleside.getCompany_Id();
					String saleonside = saleside.getSide();
					int salequantity = saleside.getQuantity();
					double saleprice = saleside.getPrice();
//					int salepriority_queue = saleside.getPriority_queue();
					String saleorder_status = saleside.getOrder_status();
					int saleexecutedQty = saleside.getExecutedQty();
					int salependingQty = saleside.getPendingQty();

					order = new Order();
					order.setOrder_id(saleorder_id);
					order.setCompany_Id(salecompany_Id);
					order.setSide(saleonside);
					order.setQuantity(salequantity);
					order.setPrice(saleprice);
//					order.setPriority_queue(salepriority_queue);
					order.setOrder_status(saleorder_status);
					order.setExecutedQty(saleexecutedQty);
					order.setPendingQty(salependingQty);
					System.out.println("list ig targetside orders");
					targetside.add(order);
				}

			}
			System.out.println("targetside is :" + targetside);
			for (Order newtargetside : targetside) {

				int saleorder_id = newtargetside.getOrder_id();
				System.out.println("saleorder_id is :" + saleorder_id);

				// If statement for two quantities are equal
				if (newtargetside.getPendingQty() == sidependingquantity) {
					int salependingQty = newtargetside.getPendingQty();
					String salesql = "UPDATE Orders SET PendingQty = 0,ExecutedQty =? ,order_status = 'Fully Executed' WHERE order_id = ?";
					String buysql = "UPDATE Orders SET PendingQty = 0,ExecutedQty =?, order_status = 'Fully Executed' WHERE order_id = ?";

					// Buy order update query start
					PreparedStatement ps1 = null;
					PreparedStatement ps2 = null;

					try {
						ps1 = connection.prepareStatement(buysql);
						ps1.setInt(1, sidependingquantity);
						ps1.setInt(2, sideorder_id);
						int update_buy = ps1.executeUpdate();
						if (update_buy > 0)
							System.out.println("Buy order updated");
						else
							System.out.println("Buy order not updated");

						// Sale order update query start
						ps2 = connection.prepareStatement(salesql);
						ps1.setInt(1, salependingQty);
						ps2.setInt(2, saleorder_id);
						int update_sale = ps2.executeUpdate();
						if (update_sale > 0)
							System.out.println("Sale order updated");
						else
							System.out.println("Sale order not updated");

					} catch (Exception e) {
						System.out.println(e.getMessage());
					} finally {
						if (ps1 != null) {
							ps1.close();
							ps2.close();
						}
						if (connection != null) {
							connection.close();
						}
					}

				}
				// If statement for one Salequantity > BuyQuantity
				else if (newtargetside.getPendingQty() > sidependingquantity) {
					System.out.println("newtargetside.getQuantity() > sidequantity");
					salependingquantity = newtargetside.getPendingQty();
					System.out.println("******testing*******");
					salependingquantity = (salependingquantity) - (sidependingquantity);

					String salesql1 = "UPDATE Orders SET PendingQty = ?,ExecutedQty =?, order_status = 'Partially Executed' WHERE order_id = ?";
					String buysql1 = "UPDATE Orders SET PendingQty = 0,ExecutedQty =?, order_status = 'Fully Executed' WHERE order_id = ?";
					PreparedStatement ps3 = null;
					PreparedStatement ps4 = null;

					try {
						// Buy order update here
						ps4 = connection.prepareStatement(buysql1);
					
						ps4.setInt(1, sidependingquantity);
						ps4.setInt(2, sideorder_id);
						int update_buy = ps4.executeUpdate();
						if (update_buy > 0)
							System.out.println("Buy order updated");
						else
							System.out.println("Buy order not updated");
						// Sale order update here

						ps3 = connection.prepareStatement(salesql1);
						ps3.setInt(1, salependingquantity);
						ps3.setInt(2, sidependingquantity);
						ps3.setInt(3, saleorder_id);
						int update_sale = ps3.executeUpdate();
						if (update_sale > 0)
							System.out.println("Sale order updated");
						else
							System.out.println("Sale order not updated");

					} catch (Exception e) {
						System.out.println(e.getMessage());
					} finally {
						if (ps3 != null) {
							ps3.close();
							ps4.close();
						}
						if (connection != null) {
							connection.close();
						}
					}

				} else {
					// *****************Saleorder updating
					if (newtargetside.getPendingQty() < sidependingquantity) {

						salependingquantity = newtargetside.getPendingQty();
						System.out.println("******testing*******");
						sidependingquantity = (sidependingquantity) - (salependingquantity);

						String salesql2 = "UPDATE Orders SET PendingQty = 0,ExecutedQty =?, order_status = 'Fully Executed' WHERE order_id = ? ";
						PreparedStatement ps5 = null;
						PreparedStatement ps6 = null;

						try {
							ps5 = connection.prepareStatement(salesql2);
							ps5.setInt(1, salependingquantity);
							ps5.setInt(2, saleorder_id);

							int update_sale = ps5.executeUpdate();
							if (update_sale > 0)
								System.out.println("Sale order updated");
							else
								System.out.println("Sale order not updated");
							// Buyorder updating
							String buysql2 = "UPDATE Orders SET PendingQty = ?, ExecutedQty =?,order_status = 'Partially Executed' WHERE order_id = ?";
							ps6 = connection.prepareStatement(buysql2);
							ps6.setInt(1, sidependingquantity);
							ps6.setInt(2, salependingquantity);
							ps6.setInt(3, sideorder_id);
							int update_buy = ps6.executeUpdate();
							if (update_buy > 0)
								System.out.println("Buy order updated");
							else
								System.out.println("Buy order not updated");

						} catch (Exception e) {
							System.out.println(e.getMessage());
						} finally {

							if (ps5 != null) {
								ps5.close();
								ps6.close();
							}
							if (connection != null) {
								connection.close();
							}
						}

					}

				}

			}
		} catch (Exception e) {

			System.out.println(e.getMessage());
		} finally {
			if (statement5 != null) {

				statement5.close();
			}
			if (connection != null) {
				connection.close();
			}

		}

		return saleorder;

	}

}
