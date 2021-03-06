package tk.tnicy.matchbox.web;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.web.util.SavedRequest;
import org.apache.shiro.web.util.WebUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import tk.tnicy.matchbox.domain.User;
import tk.tnicy.matchbox.service.UserService;
import tk.tnicy.matchbox.service.Util;

import javax.servlet.http.HttpServletRequest;


@Controller
public class LogController {


    @Autowired
    UserService userService;
    @Autowired
    Util util;


    @GetMapping("/login")
    public String getLogin(Model model) {
        if (SecurityUtils.getSubject().isAuthenticated()) {
            return "redirect:/";
        }

        util.injectUser(model);

        return "login";
    }


    @PostMapping("/login")
    public Object postLogin(HttpServletRequest request, String username, String password, Model model) {
        util.injectUser(model);
        if (Util.login(username, password, model)) {
            SavedRequest savedRequest = WebUtils.getSavedRequest(request);
            if (savedRequest == null || savedRequest.getRequestUrl() == null) {
                return "redirect:/";
            }
            return "redirect:" + savedRequest.getRequestUrl();
        } else {
            return "login";
        }

    }


    @GetMapping("/logup")
    public String getLogup(Model model) {
        util.injectUser(model);
        return "logup";
    }

    @PostMapping("/logup")
    public String postLogup(@RequestParam("username") String username,
                            @RequestParam("password") String password,
                            Model model) {

        util.injectUser(model);

        boolean result = userService.registerUser(username, password);
        if (result) {

            Util.login(username, password, model);
            return "redirect:/";

        }
        model.addAttribute("msg", "用户名已存在");
        return "logup";

    }

    @RequestMapping("/logout")
    public String logout() {
        SecurityUtils.getSubject().logout();
        return "redirect:/login";
    }


    @RequiresPermissions("normal")
    @RequestMapping("/deleteMe")
    public String getUserInfo() {

        User user = util.getCurrentUser();
        userService.delete(user);
        SecurityUtils.getSubject().logout();
        return "redirect:/login";

    }
}
