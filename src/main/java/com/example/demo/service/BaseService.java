package com.example.demo.service;


import com.example.demo.model.response.ListResult;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

/**
 * @Author
 * @Description
 * @create 2020-12-03 11:12
 * @Modified By:
 */
@Slf4j
public class BaseService<T> {

  public static final URL classLoaderURL = Thread.currentThread().getContextClassLoader()
      .getResource("");
  @Autowired
  public JdbcTemplate jdbcTemplate;

  @Autowired
  private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

  private static final Pattern humpPattern = Pattern.compile("[A-Z]");


  private static boolean isSqlInject(String sort) {
    String[] sorts = sort.trim().split("\\s+");
    if (sorts.length == 0) {
      return false;
    }

    for (int i = 2; i < sorts.length; i++) {
      for (char c : sorts[i].toCharArray()) {
        if (!StringUtils.isAlphanumeric("" + c) && c != '_' && c != '.' && c != ',') {
          return true;
        }
      }

    }
    return false;
  }

  public ListResult<T> pageBySql(String sql, int offset, int limit, Class<T> mappedClass) {
    return pageBySql(sql, new Object[]{}, offset, limit, mappedClass);
  }

  public ListResult<T> pageBySql(String sql, Object[] objs, int offset, int limit,
      Class<T> mappedClass) {
    return pageBySql(sql, objs, offset, limit, "", mappedClass);
  }

  public ListResult<T> pageBySql(String sql, String sort, int offset, int limit,
      Class<T> mappedClass) {
    return pageBySql(sql, new Object[]{}, offset, limit, sort, mappedClass);
  }

  public ListResult<T> pageBySql(String sql, String sort, Object[] objs, int offset, int limit,
      Class<T> mappedClass) {
    return pageBySql(sql, objs, offset, limit, sort, mappedClass);
  }

  public ListResult<T> pageBySql(String sql, Object[] objs, int offset, int limit, String sort,
      Class<T> mappedClass) {

    DataCount<T> dataCount = pageBySqlList(sql, objs, offset, limit, sort, mappedClass);
    return new ListResult<>(dataCount.datas, dataCount.total);
  }

  public ListResult<T> pageBySql(String sql, MapSqlParameterSource params, int offset, int limit,
      String sort, Class<T> mappedClass) {
    DataCount<T> dataCount = this.pageBySqlList(sql, params, offset, limit, sort, mappedClass);
    return new ListResult(dataCount.datas, dataCount.total);
  }

  public DataCount<T> pageBySqlList(String sql, MapSqlParameterSource params, int offset, int limit,
      String sort, Class<T> mappedClass) {
    String totalSql = "select count(1) from (" + sql + ") total";
    log.info("count sql:" + totalSql, new Object[0]);
    int total = (Integer) this.namedParameterJdbcTemplate
        .queryForObject(totalSql, params, Integer.class);
    if (total == 0) {
      return new DataCount(new ArrayList(), 0);
    } else {
      String sortDb = this.toColumnName(sort);
      if (isSqlInject(sortDb)) {
        log.error("sql inject:" + sort, new Object[0]);
        return new DataCount(new ArrayList(), 0);
      } else {
        String querySql = sql + " " + sortDb + " limit " + offset + " , " + limit;
        log.info("query sql:" + querySql, new Object[0]);
        List<T> list = this.namedParameterJdbcTemplate
            .query(querySql, params, new TBeanPropertyRowMapper(mappedClass));
        return new DataCount(list, total);
      }
    }
  }

  public T getOne(String sql, Object[] objs, Class<T> mappedClass) {
    String querySql = sql + " limit 0 , 1";
    List<T> list = jdbcTemplate.query(querySql, objs, new TBeanPropertyRowMapper<>(mappedClass));
    if (list == null || list.isEmpty()) {
      return null;
    }
    return list.get(0);
  }

  public List<T> findAll(String sql, Object[] objs, String sort, Class<T> mappedClass) {

    String querySql = sql + " " + sort;
    log.info("query sql:" + querySql);
    List<T> list = jdbcTemplate.query(querySql, objs, new TBeanPropertyRowMapper<>(mappedClass));
    return list;
  }

  public List<T> findAll(String sql, MapSqlParameterSource params, String sort,
      Class<T> mappedClass) {
    if (isSqlInject(sort)) {
      log.error("sql inject:" + sort, new Object[0]);
      return new ArrayList();
    } else {
      String querySql = sql + " " + sort;
      log.info("query sql:" + querySql, new Object[0]);
      List<T> list = this.namedParameterJdbcTemplate
          .query(querySql, params, new TBeanPropertyRowMapper(mappedClass));
      return list;
    }
  }

  public DataCount<T> pageBySqlList(String sql, Object[] objs, int offset, int limit, String sort,
      Class<T> mappedClass) {
    String totalSql = "select count(*) from (" + sql + ") total";
    log.info("count sql:" + totalSql);
    int total = jdbcTemplate.queryForObject(totalSql, objs, Integer.class);
    if (total == 0) {
      return new DataCount<T>(new ArrayList<T>(), 0);
    }
    String sortDb = toColumnName(sort);
    if (isSqlInject(sortDb)) {
      log.error("sql inject:" + sort);
      return new DataCount<T>(new ArrayList<T>(), 0);
    }
    offset = (offset - 1) * limit;
    String querySql = sql + " " + sortDb + " limit " + offset + " , " + limit;
    log.info("query sql:" + querySql);
    List<T> list = jdbcTemplate.query(querySql, objs, new TBeanPropertyRowMapper<>(mappedClass));
    return new DataCount<T>(list, total);
  }

  protected static String humpToLine2(String str) {
    Matcher matcher = humpPattern.matcher(str);
    StringBuffer sb = new StringBuffer();
    while (matcher.find()) {
      matcher.appendReplacement(sb, "_" + matcher.group(0).toLowerCase());
    }
    matcher.appendTail(sb);
    return sb.toString();
  }

  private String toColumnName(String sort) {
    //order by ss desc
    String[] sorts = sort.trim().split("\\s+");
    if (sorts == null || sort.length() < 3) {
      return "";
    }
    StringBuffer sb = new StringBuffer();
    for (char c : sorts[2].toCharArray()) {
      if (c >= 'A' && c <= 'Z') {
        sb.append("_" + String.valueOf(c).toLowerCase());
      } else {
        sb.append(c);
      }
    }

    sorts[2] = sb.toString();
    String sortExt = "";
    for (String str : sorts) {
      sortExt += " " + str;
    }
    return sortExt;
  }

  public int countBySqlList(String sql, Object[] objs) {
    String totalSql = "select count(*) from (" + sql + ") total";
    log.info("count sql:" + totalSql);
    int total = jdbcTemplate.queryForObject(totalSql, objs, Integer.class);
    return total;
  }

  public String getParamForIn(Long[] ids) {
    StringBuffer tasks = new StringBuffer();
    for (Long taskId : ids) {
      tasks.append(taskId);
      tasks.append(",");
    }
    if (tasks.length() > 0 && tasks.lastIndexOf(",") >= 0) {
      tasks.delete(tasks.lastIndexOf(","), tasks.length());
    }

    return tasks.toString();
  }

  private static class DataCount<T> {

    public List<T> datas;
    public int total;

    public DataCount(List<T> datas, int total) {
      this.datas = datas;
      this.total = total;
    }
  }
}
