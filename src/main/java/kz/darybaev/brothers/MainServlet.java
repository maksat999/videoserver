package kz.darybaev.brothers;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import com.xuggle.mediatool.IMediaReader;
import com.xuggle.mediatool.IMediaViewer;
import com.xuggle.mediatool.IMediaWriter;
import com.xuggle.mediatool.ToolFactory;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.FileItem;

import java.util.Scanner;
import java.util.regex.Pattern;
import java.io.*;

public class MainServlet extends HttpServlet {
    private Random random = new Random();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setAttribute("name", " by Darybaev Company");
        req.getRequestDispatcher("mypage.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
  /*     String description = req.getParameter("description"); // Retrieves <input type="file" name="videofile">
       String data = req.getParameter("data");*/

//проверяем является ли полученный запрос multipart/form-data
      boolean isMultipart = ServletFileUpload.isMultipartContent(req);
        if (!isMultipart) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        // Создаём класс фабрику
        DiskFileItemFactory factory = new DiskFileItemFactory();

        // Максимальный буфера данных в байтах,
        // при его привышении данные начнут записываться на диск во временную директорию
        // устанавливаем один мегабайт
        factory.setSizeThreshold(1024*1024);

        // устанавливаем временную директорию
        File tempDir = (File)getServletContext().getAttribute("javax.servlet.context.tempdir");
        factory.setRepository(tempDir);

        //Создаём сам загрузчик
        ServletFileUpload upload = new ServletFileUpload(factory);

        //максимальный размер данных который разрешено загружать в байтах
        //по умолчанию -1, без ограничений. Устанавливаем 10 мегабайт.
        upload.setSizeMax(1024 * 1024 * 20);

        try {
            List items = upload.parseRequest(req);
            Iterator iter = items.iterator();

            while (iter.hasNext()) {
                FileItem item = (FileItem) iter.next();

                if (item.isFormField()) {
                    //если принимаемая часть данных является полем формы
                    processFormField(item);
                } else {
                    //в противном случае рассматриваем как файл
                    processUploadedFile(item);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return;
        }

    }

    private void processUploadedFile(FileItem item) throws Exception {
        File uploadetFile = null;
        //вбираем файлу имя пока не найдём свободное
         String videoName = item.getName();
        do{
            String   path = getServletContext().getRealPath("/upload/"+ videoName);
            uploadetFile = new File(path);
        }while(uploadetFile.exists());

        //создаём файл
        uploadetFile.createNewFile();
        //записываем в него данные
        item.write(uploadetFile);

/*    Старый рабочий код конвертации видео    try {
         Runtime.getRuntime().exec("ffmpeg -i C:\\Projects\\VideoServer\\target\\kz.darybaevBrothers-1.0-SNAPSHOT\\upload\\"+videoName+" C:\\Projects\\VideoServer\\target\\kz.darybaevBrothers-1.0-SNAPSHOT\\upload\\12.avi");
            }
        catch (Exception ee) {System.out.print(ee);}
    }*/


                ProcessBuilder pb = new ProcessBuilder("ffmpeg","-i","C:\\Projects\\VideoServer\\target\\kz.darybaevBrothers-1.0-SNAPSHOT\\upload\\in.webm","C:\\Projects\\VideoServer\\target\\kz.darybaevBrothers-1.0-SNAPSHOT\\upload\\out.mp4");
                final Process p = pb.start();

                new Thread() {
                    public void run() {

                        Scanner sc = new Scanner(p.getErrorStream());

                        // Find duration
                        Pattern durPattern = Pattern.compile("(?<=Duration: )[^,]*");
                        String dur = sc.findWithinHorizon(durPattern, 0);
                        if (dur == null)
                            throw new RuntimeException("Could not parse duration.");
                        String[] hms = dur.split(":");
                        double totalSecs = Integer.parseInt(hms[0]) * 3600
                                + Integer.parseInt(hms[1]) *   60
                                + Double.parseDouble(hms[2]);
                        System.out.println("Total duration: " + totalSecs + " seconds.");

                        // Find time as long as possible.
                        Pattern timePattern = Pattern.compile("(?<=time=)[\\d.]*");
                        String match;
                        while (null != (match = sc.findWithinHorizon(timePattern, 0))) {
                            double progress = Double.parseDouble(match) / totalSecs;
                            System.out.printf("Progress: %.2f%%%n", progress * 100);
                        }
                    }
                }.start();

            }


    private void processFormField(FileItem item) {
        System.out.println(item.getFieldName()+"="+item.getString());
    }

    }
