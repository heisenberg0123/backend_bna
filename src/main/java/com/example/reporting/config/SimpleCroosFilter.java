package com.example.reporting.config;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.print.DocFlavor;
import javax.sql.rowset.serial.SerialException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Filter;
import java.util.logging.LogRecord;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class SimpleCroosFilter implements Filter {

    @Override
    public boolean isLoggable(LogRecord record) {
        return false;
    }





    private final String clientUrl="http://localhost:4200/*";

    public  void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws   ServletException, IOException {
        HttpServletResponse response=(HttpServletResponse) res;
        HttpServletRequest request=(HttpServletRequest) req;
        Map<String,String>map=new HashMap<>();
        String originHeader=request.getHeader("origin");
        response.setHeader("Access-Control-Allow-Origin",originHeader);
        response.setHeader("Access-Control-Allow-Methods","POST,GET,PUT,OPTIONS,DELETE");
        response.setHeader("Access-Control-Allow-Age","3600");
        response.setHeader("Access-Control-Allow-Headers","*");




        if("OPTIONS".equalsIgnoreCase(request.getMethod())){
            response.setStatus(HttpServletResponse.SC_OK);
        }else {
            chain.doFilter(req,res);
        }
    }
    public void init(FilterConfig filterConfig){

    }
    public  void destroy(){

    }

}