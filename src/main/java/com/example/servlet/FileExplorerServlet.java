package com.example.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.stream.Collectors;

@WebServlet("/file-explorer")
public class FileExplorerServlet extends HttpServlet {

    private static final String BASE_DIR = "C:\\Users\\";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("login") == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        String login = (String) session.getAttribute("login");

        Path userHome = Paths.get(BASE_DIR, login);

        Files.createDirectories(userHome);
        userHome = userHome.toRealPath();

        String paramPath = req.getParameter("path");
        Path requestedPath;

        if (paramPath == null || paramPath.isEmpty()) {
            requestedPath = userHome;
        } else {
            try {
                requestedPath = Paths.get(paramPath).toRealPath();
                if (!requestedPath.startsWith(userHome)) {
                    requestedPath = userHome;
                }
            } catch (IOException e) {
                requestedPath = userHome;
            }
        }

        File directory = requestedPath.toFile();
        if (!directory.exists()) {
            directory.mkdir();
        }
        //Проверка пути выше
        Path parentPath = requestedPath.getParent();
        if (parentPath != null && parentPath.startsWith(userHome)) {
            req.setAttribute("parentPath", parentPath.toString());
        } else {
            req.setAttribute("parentPath", null);
        }

        File[] files = directory.listFiles();
        req.setAttribute("files", files);
        req.setAttribute("currentPath", requestedPath.toString());
        req.setAttribute("generatedTime", new Date());

        req.getRequestDispatcher("/explorer.jsp").forward(req, resp);
    }
}