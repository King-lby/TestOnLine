package com.lby.filter;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class EncodingFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        System.out.println("初始化filter");
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        System.out.println("过滤器开始");
        /**
         * filter使用场景
         */
        HttpServletRequest request = (HttpServletRequest)servletRequest;
        request.setCharacterEncoding("utf-8");
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        response.setContentType("text/html; charset=UTF-8");
        //2. 如何防⽌⽤户未登录就执⾏后续操作
        String requestURI = request.getRequestURI();
        System.out.println("requestURI="+requestURI);
        Object username1 = request.getSession().getAttribute("username1");

        if (requestURI.endsWith("<script>right.location.href='right.jsp'</script>")&&username1==null){
            response.sendRedirect("login.jsp");
        }

        filterChain.doFilter(servletRequest, servletResponse);

    }

    @Override
    public void destroy() {
        System.out.println("销毁filter");
    }
}