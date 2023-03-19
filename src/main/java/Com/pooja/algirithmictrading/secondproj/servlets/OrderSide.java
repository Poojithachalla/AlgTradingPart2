package Com.pooja.algirithmictrading.secondproj.servlets;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import Com.pooja.algirithmictrading.secondproj.dao.OrderService;
import Com.pooja.algirithmictrading.secondproj.model.Order;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest ;
import jakarta.servlet.http.HttpServletResponse;

public class OrderSide extends HttpServlet {
	private static final long serialVersionUID = 1L;

    
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		 OrderService service = new OrderService();
		 try {
			List<Order> listsides = service.getSides();
			
			
			request.setAttribute("listsides", listsides);
			
			RequestDispatcher rd= request.getRequestDispatcher("DisplaySide.jsp");
			rd.forward(request, response);
		} catch (SQLException e) {
		
			e.printStackTrace();
		}
		
	}

	
	

}
