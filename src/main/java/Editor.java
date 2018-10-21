import java.io.IOException;
import java.sql.* ;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Date;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.commonmark.node.*;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;

/**
 * Servlet implementation class for Servlet: ConfigurationTest
 *
 */
public class Editor extends HttpServlet {
    /**
     * The Servlet constructor
     * 
     * @see javax.servlet.http.HttpServlet#HttpServlet()
     */
    public Editor() {}

    public void init() throws ServletException
    {
        /* load the driver */
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException ex) {
            System.out.println(ex);
            return;
        }
    }
    
    public void destroy()
    {
        /*  write any servlet cleanup code here or remove this function */
    }

    /**
     * Handles HTTP GET requests
     * Actions: open, list, preview
     * @see javax.servlet.http.HttpServlet#doGet(HttpServletRequest request,
     *      HttpServletResponse response)
     */
    public void doGet(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException 
    {
        /* extract and save parameters locally */
        String action = request.getParameter("action");
        String username = request.getParameter("username");
        String postid = request.getParameter("postid");
        String title = request.getParameter("title");
        String body = request.getParameter("body");

        Connection c = null;
        PreparedStatement s = null;
        ResultSet rs = null;

        try {
            /* create an instance of a Connection object */
            c = DriverManager.getConnection("jdbc:mysql://localhost:3306/CS144", "cs144", "");

            /* handle null action */
            if(action == null) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            } else if(action.equals("open")) {
                /* test required parameters */
                if(username == null || postid == null) {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                } else {
                    if(title != null || body != null) {
                        request.setAttribute("title", title);
                        request.setAttribute("body", body);
                    } else {
                        s = c.prepareStatement("SELECT * FROM MaxIDs WHERE username = ?");
                        s.setString(1, username);
                        rs = s.executeQuery();

                        if(rs.next()) {
                            /* user already in database */
                            s = c.prepareStatement("SELECT * FROM Posts WHERE username = ? AND postid = ?");
                            s.setString(1, username);
                            s.setInt(2, Integer.parseInt(postid));
                            rs = s.executeQuery();

                            if(rs.next()) {
                                /* row exists in the database */
                                request.setAttribute("title", rs.getString("title"));
                                request.setAttribute("body", rs.getString("body"));

                                /* debug */
                                System.out.println("Debug: " + rs.getString("title") + " " + rs.getString("body"));
                            } else {
                                request.setAttribute("title", "");
                                request.setAttribute("body", "");
                            }
                        } else {
                            /* user not in database */
                            request.setAttribute("title", "");
                            request.setAttribute("body", "");
                        }
                    }
                }
                request.getRequestDispatcher("/edit.jsp").forward(request, response);
            } else if(action.equals("list")) {
                if(username == null) {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                } else {
                    s = c.prepareStatement("SELECT * FROM Posts WHERE username = ? ORDER BY postid");
                    s.setString(1, username);
                    rs = s.executeQuery();

                    ArrayList<Integer> ids = new ArrayList<>();
                    HashMap<Integer, String> titles = new HashMap<>();
                    HashMap<Integer, Date> creations = new HashMap<>();
                    HashMap<Integer, Date> modifications = new HashMap<>();
                    while(rs.next()) {
                        int id = rs.getInt("postid");
                        ids.add(id);
                        titles.put(id, rs.getString("title"));
                        creations.put(id, rs.getTimestamp("created"));
                        modifications.put(id, rs.getTimestamp("modified"));

                        /* debug */
                        System.out.println("Debug: " + id + " " + titles.get(id));
                    }

                    request.setAttribute("titles", titles);
                    request.setAttribute("modifications", modifications);
                    request.setAttribute("creations", creations);
                    request.setAttribute("ids", ids);
                }
                request.getRequestDispatcher("/list.jsp").forward(request, response);
            } else if(action.equals("preview")) {
                if(username == null || postid == null || title == null || body == null) {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                } else {
                    Parser parser = Parser.builder().build();
                    Node d_title = parser.parse(title);
                    Node d_body = parser.parse(body);
                    HtmlRenderer renderer_t = HtmlRenderer.builder().build();
                    HtmlRenderer renderer_b = HtmlRenderer.builder().build();
                    request.setAttribute("title_html", renderer_t.render(d_title));
                    request.setAttribute("body_html", renderer_b.render(d_body));
                    System.out.println("Debug: render html" + renderer_b.render(d_body) + " End");
                }
                request.getRequestDispatcher("/preview.jsp").forward(request, response);

            } else {
                /* unknown action using doGet */
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            }
        } catch (SQLException ex){
            System.out.println("SQLException caught");
            System.out.println("---");
            while ( ex != null ) {
                System.out.println("Message   : " + ex.getMessage());
                System.out.println("SQLState  : " + ex.getSQLState());
                System.out.println("ErrorCode : " + ex.getErrorCode());
                System.out.println("---");
                ex = ex.getNextException();
            }
        } finally {
            try { rs.close(); } catch (Exception e) { /* ignored */ }
            try { s.close(); } catch (Exception e) { /* ignored */ }
            try { c.close(); } catch (Exception e) { /* ignored */ }
        }
    }
    
    /**
     * Handles HTTP POST requests
     * Actions: save, delete
     * @see javax.servlet.http.HttpServlet#doPost(HttpServletRequest request,
     *      HttpServletResponse response)
     */
    public void doPost(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException 
    {
        /* extract and save parameters locally */
        String action = request.getParameter("action");
        String username = request.getParameter("username");
        String postid = request.getParameter("postid");
        String title = request.getParameter("title");
        String body = request.getParameter("body");

        System.out.println("L173: postid " + postid);

        Connection c = null;
        PreparedStatement s = null;
        ResultSet rs = null;

        try {
            /* create an instance of a Connection object */
            c = DriverManager.getConnection("jdbc:mysql://localhost:3306/CS144", "cs144", "");

            /* handle null action */
            if(action == null) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            } else if(action.equals("save")) {
                /* test required parameters */
                if(username == null || postid == null || title == null || body == null) {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                } else {
                    int parsed_id = Integer.parseInt(postid);
                    if(parsed_id <= 0) {
                        System.out.println("L193: postid <= 0");
                        s = c.prepareStatement("SELECT * FROM MaxIDs WHERE username = ?");
                        s.setString(1, username);
                        rs = s.executeQuery();
                        int id = -1;

                        if(rs.next()) {
                            /* username already in the database */
                            id = rs.getInt("maxid");
                        }
                        
                        /* update max id for the user*/
                        if(id == -1) {
                            s = c.prepareStatement("INSERT INTO MaxIDs VALUES (?, ?)");
                            s.setString(1, username);
                            s.setInt(2, 1);
                            s.executeUpdate();
                            id = 1;
                        } else {
                            s = c.prepareStatement("UPDATE MaxIDs SET maxid = ? WHERE username = ?");
                            s.setString(2, username);
                            s.setInt(1, id+1);
                            s.executeUpdate();
                            id += 1;
                        }

                        s = c.prepareStatement("INSERT INTO Posts VALUES (?, ?, ?, ?, ?, ?)");
                        s.setString(1, username);
                        s.setInt(2, id);
                        s.setString(3, title);
                        s.setString(4, body);
                        s.setTimestamp(5, new Timestamp(System.currentTimeMillis()));
                        s.setTimestamp(6, new Timestamp(System.currentTimeMillis()));
                        s.executeUpdate();
                    } else {
                        s = c.prepareStatement("SELECT 1 FROM Posts where username = ? AND postid = ?");
                        s.setString(1, username);
                        s.setInt(2, parsed_id);
                        rs = s.executeQuery();

                        if(rs.next()) {
                            /* row exitst in the database */
                            s = c.prepareStatement("UPDATE Posts SET title = ?, body = ?, modified = ? WHERE username = ? AND postid = ?");
                            s.setString(1, title);
                            s.setString(2, body);
                            s.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
                            s.setString(4, username);
                            s.setInt(5, parsed_id);
                            s.executeUpdate();
                        }
                    }
                    s = c.prepareStatement("SELECT * FROM Posts WHERE username = ? ORDER BY postid");
                    s.setString(1, username);
                    rs = s.executeQuery();

                    ArrayList<Integer> ids = new ArrayList<>();
                    HashMap<Integer, String> titles = new HashMap<>();
                    HashMap<Integer, Date> creations = new HashMap<>();
                    HashMap<Integer, Date> modifications = new HashMap<>();
                    while(rs.next()) {
                        int id = rs.getInt("postid");
                        ids.add(id);
                        titles.put(id, rs.getString("title"));
                        creations.put(id, rs.getTimestamp("created"));
                        modifications.put(id, rs.getTimestamp("modified"));

                        /* debug */
                        System.out.println("Debug: " + id + " " + titles.get(id));
                    }

                    request.setAttribute("titles", titles);
                    request.setAttribute("modifications", modifications);
                    request.setAttribute("creations", creations);
                    request.setAttribute("ids", ids);
                    request.getRequestDispatcher("/list.jsp").forward(request, response);  
                }
            } else if(action.equals("delete")) {
                if(username == null || postid == null) {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                } else {
                    s = c.prepareStatement("DELETE FROM Posts WHERE username = ? AND postid = ?");
                    s.setString(1, username);
                    s.setInt(2, Integer.parseInt(postid));
                    s.executeUpdate();

                    s = c.prepareStatement("SELECT * FROM Posts WHERE username = ? ORDER BY postid");
                    s.setString(1, username);
                    rs = s.executeQuery();

                    ArrayList<Integer> ids = new ArrayList<>();
                    HashMap<Integer, String> titles = new HashMap<>();
                    HashMap<Integer, Date> creations = new HashMap<>();
                    HashMap<Integer, Date> modifications = new HashMap<>();
                    while(rs.next()) {
                        int id = rs.getInt("postid");
                        ids.add(id);
                        titles.put(id, rs.getString("title"));
                        creations.put(id, rs.getTimestamp("created"));
                        modifications.put(id, rs.getTimestamp("modified"));

                        /* debug */
                        System.out.println("Debug: " + id + " " + titles.get(id));
                    }

                    request.setAttribute("titles", titles);
                    request.setAttribute("modifications", modifications);
                    request.setAttribute("creations", creations);
                    request.setAttribute("ids", ids);
                    request.getRequestDispatcher("/list.jsp").forward(request, response);
                }
            } else {
                /* unknown action using doPost */
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            }
        } catch (SQLException ex){
            System.out.println("SQLException caught");
            System.out.println("---");
            while ( ex != null ) {
                System.out.println("Message   : " + ex.getMessage());
                System.out.println("SQLState  : " + ex.getSQLState());
                System.out.println("ErrorCode : " + ex.getErrorCode());
                System.out.println("---");
                ex = ex.getNextException();
            }
        } finally {
            try { rs.close(); } catch (Exception e) { /* ignored */ }
            try { s.close(); } catch (Exception e) { /* ignored */ }
            try { c.close(); } catch (Exception e) { /* ignored */ }
        }
    }
}

