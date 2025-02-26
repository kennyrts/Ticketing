package controllers;

import mg.itu.prom16.util.ModelView;
import mg.itu.prom16.util.MySession;

import java.util.ArrayList;

import mg.itu.prom16.annotations.Get;
import mg.itu.prom16.annotations.Post;
import mg.itu.prom16.annotations.Url;
import mg.itu.prom16.annotations.Param;
import mg.itu.prom16.annotations.Controller;
import model.User;

@Controller
public class LoginController {
    
    @Get
    @Url("back_login")
    public ModelView loginPage() {
        ModelView mv = new ModelView();
        mv.setUrl("back_login.jsp");
        return mv;
    }

    @Post
    @Url("back_login_post")
    public ModelView login(@Param(name = "login") String login, 
                          @Param(name = "mdp") String mdp, 
                          MySession session) {
        ModelView mv = new ModelView();
        try {
            User user = User.login(login, mdp, "admin");    
            if (user != null) {
                session.add("id", user.getId());
                session.add("login", user.getLogin());
                session.add("ROLE-USER", user.getRole());                
                session.add("nom", user.getNom());
                mv.setUrl("vol_list");                
            } else {
                mv.setUrl("back_login.jsp");
                mv.addObject("error", "Invalid login credentials");
            }
        } catch (Exception e) {
            mv.setUrl("back_login.jsp");
            mv.addObject("error", e.getMessage());
        }
        return mv;
    }

    @Get
    @Url("logout")
    public ModelView logout(MySession session) {
        ModelView mv = new ModelView();
        mv.setUrl("front_login");  // Changed to match the login page URL
        
        // Remove all session attributes
        session.delete("id");
        session.delete("login");
        session.delete("ROLE-USER");
        session.delete("nom");
        
        return mv;
    }

    @Get
    @Url("front_login")
    public ModelView loginPageFront() {
        ModelView mv = new ModelView();
        mv.setUrl("front_login.jsp");
        return mv;
    }

    @Post
    @Url("front_login_post")
    public ModelView loginFront(@Param(name = "login") String login, 
                          @Param(name = "mdp") String mdp, 
                          MySession session) {
        ModelView mv = new ModelView();
        try {
            User user = User.login(login, mdp, "user");    
            if (user != null) {
                session.add("id", user.getId());
                session.add("login", user.getLogin());
                session.add("ROLE-USER", user.getRole());                
                session.add("nom", user.getNom());
                mv.setUrl("front_vol_search_form");                
            } else {
                mv.setUrl("front_login.jsp");
                mv.addObject("error", "Invalid login credentials");
            }
        } catch (Exception e) {
            mv.setUrl("front_login.jsp");
            mv.addObject("error", e.getMessage());
        }
        return mv;
    }
}