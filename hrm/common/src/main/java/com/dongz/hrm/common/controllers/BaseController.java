package com.dongz.hrm.common.controllers;

import com.dongz.hrm.common.entities.PageResult;
import com.dongz.hrm.common.entities.Profile;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author dong
 * @date 2020/1/3 01:00
 * @desc
 */
public abstract class BaseController {

    protected Profile profile;

    @Autowired
    protected NamedParameterJdbcTemplate jdbcTemplate;

    /**
     * controller中每个方法前执行
     */
    @ModelAttribute
    public void init() {
        Subject subject = SecurityUtils.getSubject();
        PrincipalCollection principals = subject.getPrincipals();

        if (principals != null) {
            Object principal = principals.getPrimaryPrincipal();
            profile = (Profile) principal;
        }
        // 获取安全数据
    }

    /**
     * 合并Map，key相同时同时合并value
     * @param maps
     * @param <T>
     */
    @SafeVarargs
    public static <T> void mergeMap(final Map<String, List<T>>... maps) {
        Arrays.stream(maps).reduce((a, b) -> {
            a.keySet().stream().filter(b::containsKey).collect(Collectors.toSet())
                    //避免覆盖
                    .forEach(item -> b.get(item).addAll(a.get(item)));
            a.putAll(b);
            return a;
        });
    }

    public <T> T queryForObject(String sql, Map<String, Object> params, Class<T> t) {
        List<T> list = this.jdbcTemplate.query(sql, params, new BeanPropertyRowMapper<>(t));
        Assert.isTrue(list.size() == 1, "查询条数异常");
        return list.get(0);
    }

    public Map<String,Object> queryForObject(String sql, Map<String, Object> params) {
        List<Map<String, Object>> list = this.jdbcTemplate.queryForList(sql, params);
        Assert.isTrue(list.size() == 1, "查询条数异常");
        return list.get(0);
    }

    public <T> List<T> queryForList(String sql, Map<String,Object> params,Class<T> t) {
        return this.jdbcTemplate.queryForList(sql, params, t);
    }

    public List<Map<String, Object>> queryForList(String sql, Map<String, Object> params) {
        return this.jdbcTemplate.queryForList(sql, params);
    }

    public <T> PageResult<T> queryForPagination(StringBuilder sql, Map<String, Object> params, long page, long size,Class<T> t) {
        if (size <= 0) {
            size = 10;
        }

        long totalRow = this.jdbcTemplate.queryForObject("select count(*) from (" + sql + ") as T", params, Long.class);
        long pageCount = totalRow / size;
        if (totalRow % size > 0) {
            pageCount++;
        }
        if (page <= 0 || page > pageCount) {
            page = 1;
        }

        long offset = (page - 1) * size;

        List<T> data = this.jdbcTemplate
                .query("select * from (" + sql.toString() + ") as T limit " + offset + "," + size, params, new BeanPropertyRowMapper<>(t));
        return new PageResult<>(totalRow, pageCount, page, size, data);
    }

    public PageResult<Map<String, Object>> queryForPagination(StringBuilder sql, Map<String, Object> params, long page, long size) {
        if (size <= 0) {
            size = 10;
        }

        long totalRow = this.jdbcTemplate.queryForObject("select count(*) from (" + sql + ") as T", params, Long.class);
        long pageCount = totalRow / size;
        if (totalRow % size > 0) {
            pageCount++;
        }
        if (page <= 0 || page > pageCount) {
            page = 1;
        }

        long offset = (page - 1) * size;

        List<Map<String, Object>> data = this.jdbcTemplate
                .queryForList("select * from (" + sql.toString() + ") as T limit " + offset + "," + size, params);
        return new PageResult<>(totalRow, pageCount, page, size, data);
    }
}
