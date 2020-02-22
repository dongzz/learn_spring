package com.dongz.hrm.system.controllers;

import com.dongz.hrm.common.controllers.BaseController;
import com.dongz.hrm.common.entities.Result;
import com.dongz.hrm.common.enums.EnableState;
import com.dongz.hrm.common.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.crypto.hash.Md5Hash;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author dong
 * @date 2020/2/20 00:45
 * @desc
 */
@CrossOrigin
@RestController
@RequestMapping("/api/sys")
public class LoginController extends BaseController {

    @Autowired
    JwtUtils jwtUtils;

    @PostMapping("/login")
    public Result login(@RequestParam String mobile, @RequestParam String password) {
        //查询用户信息
        Map<String, Object> params = new HashMap<>();
        params.put("mobile", mobile);
        Map<String, Object> map = this.queryForObject("select t.username,t.password,t.enable_state as enableState,t.company_id as companyId,t.company_name as companyName from user t where t.mobile = :mobile and t.is_deleted = false", params);
        Assert.isTrue(password.equals(map.get("password")), "密码错误，请重新输入密码！");
        Assert.isTrue(EnableState.Enable.equals(EnableState.parse((Integer) map.get("enableState"))), "用户已被禁用，请联系管理员！");

        map.remove("password");
        String token = jwtUtils.createJwt(mobile, (String) map.get("username"), map);
        return Result.LOGINSUCCESS(token);
    }

    @GetMapping("/userInfo")
    public Result login(HttpServletRequest request) {
        String token = request.getHeader("token");
        Assert.hasText(token, "签名信息为空，请求重新登录！");

        Claims claims = jwtUtils.parseJwt(token);
        String mobile = claims.getId();
        //查询用户信息
        Map<String, Object> params = new HashMap<>();
        params.put("mobile", mobile);
        Map<String, Object> userInfo = this.queryForObject("select t.id,t.username,t.mobile,t.company_id as companyId,t.company_name as companyName from user t where t.mobile = :mobile and t.is_deleted = false", params);
        //角色权限信息
        params.clear();
        params.put("userId", userInfo.get("id"));
        List<Long> roles = this.queryForList("select t.role_id from user_role t where t.user_id = :id", params, Long.class);

        return Result.SUCCESS(userInfo);
    }
}
