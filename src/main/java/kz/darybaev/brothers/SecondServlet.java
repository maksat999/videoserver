package kz.darybaev.brothers;

import com.xuggle.mediatool.IMediaReader;
import com.xuggle.mediatool.ToolFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


/**
 * Created by Максат on 07.02.2016.
 */
public class SecondServlet extends HttpServlet{

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher("secondJsp.jsp").forward(req, resp);

       IMediaReader reader = ToolFactory.newReader("input.mpg");
        reader.addListener(ToolFactory.newWriter("output.flv", reader));
        while (reader.readPacket() == null);


    }
}
