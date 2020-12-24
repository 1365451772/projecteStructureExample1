package com.example.demo.service;

import java.beans.PropertyDescriptor;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.NotWritablePropertyException;
import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.beans.TypeMismatchException;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

/**
 * @Author
 * @Description
 * @create 2020-12-03 10:43
 * @Modified By:
 */
public class TBeanPropertyRowMapper<T> implements RowMapper<T> {

  protected final Log logger = LogFactory.getLog(super.getClass());
  private Class<T> mappedClass;
  private boolean checkFullyPopulated = false;

  private boolean primitivesDefaultedForNullValue = false;

  private ConversionService conversionService = DefaultConversionService.getSharedInstance();
  private Map<String, PropertyDescriptor> mappedFields;
  private Set<String> mappedProperties;


  public TBeanPropertyRowMapper() {
  }


  public TBeanPropertyRowMapper(Class<T> mappedClass) {
    initialize(mappedClass);
  }


  public TBeanPropertyRowMapper(Class<T> mappedClass, boolean checkFullyPopulated) {
    initialize(mappedClass);
    this.checkFullyPopulated = checkFullyPopulated;
  }


  public void setMappedClass(Class<T> mappedClass) {
    if (this.mappedClass == null) {
      initialize(mappedClass);
    } else if (this.mappedClass != mappedClass) {
      throw new InvalidDataAccessApiUsageException(
          new StringBuilder().append("The mapped class can not be reassigned to map to ")
              .append(mappedClass).append(" since it is already providing mapping for ")
              .append(this.mappedClass).toString());
    }
  }


  public final Class<T> getMappedClass() {
    return this.mappedClass;
  }


  public void setCheckFullyPopulated(boolean checkFullyPopulated) {
    this.checkFullyPopulated = checkFullyPopulated;
  }


  public boolean isCheckFullyPopulated() {
    return this.checkFullyPopulated;
  }


  public void setPrimitivesDefaultedForNullValue(boolean primitivesDefaultedForNullValue) {
    this.primitivesDefaultedForNullValue = primitivesDefaultedForNullValue;
  }


  public boolean isPrimitivesDefaultedForNullValue() {
    return this.primitivesDefaultedForNullValue;
  }


  public void setConversionService(ConversionService conversionService) {
    this.conversionService = conversionService;
  }


  public ConversionService getConversionService() {
    return this.conversionService;
  }


  protected void initialize(Class<T> mappedClass) {
    this.mappedClass = mappedClass;
    this.mappedFields = new HashMap();
    this.mappedProperties = new HashSet();
    PropertyDescriptor[] pds = BeanUtils.getPropertyDescriptors(mappedClass);
    for (PropertyDescriptor pd : pds) {
      if (pd.getWriteMethod() != null) {

        this.mappedFields.put(lowerCaseName(pd.getName()), pd);
        String underscoredName = underscoreName(pd.getName());
        if (!(lowerCaseName(pd.getName()).equals(underscoredName))) {
          this.mappedFields.put(underscoredName, pd);
        }
        this.mappedProperties.add(pd.getName());
      }
    }
  }


  protected String underscoreName(String name) {
    if (!(StringUtils.hasLength(name))) {
      return "";
    }
    StringBuilder result = new StringBuilder();
    result.append(lowerCaseName(name.substring(0, 1)));
    for (int i = 1; i < name.length(); ++i) {
      String s = name.substring(i, i + 1);
      String slc = lowerCaseName(s);
      if (!(s.equals(slc))) {
        result.append("_").append(slc);
      } else {
        result.append(s);
      }
    }
    return result.toString();
  }


  protected String lowerCaseName(String name) {
    return name.toLowerCase(Locale.US);
  }


  @Override
  public T mapRow(ResultSet rs, int rowNumber) throws SQLException {
    Assert.state(this.mappedClass != null, "Mapped class was not specified");
    Object mappedObject = BeanUtils.instantiateClass(this.mappedClass);
    BeanWrapper bw = PropertyAccessorFactory.forBeanPropertyAccess(mappedObject);
    initBeanWrapper(bw);

    ResultSetMetaData rsmd = rs.getMetaData();
    int columnCount = rsmd.getColumnCount();
    Set populatedProperties = (isCheckFullyPopulated()) ? new HashSet() : null;

    for (int index = 1; index <= columnCount; ++index) {
      String column = JdbcUtils.lookupColumnName(rsmd, index);
      String field = lowerCaseName(column.replaceAll(" ", ""));
      PropertyDescriptor pd = (PropertyDescriptor) this.mappedFields.get(field);
      if (pd == null && field.startsWith("is_")) {
        pd = (PropertyDescriptor) this.mappedFields.get(field.substring(3));
      }
      if (pd != null) {
        try {
          Object value = getColumnValue(rs, index, pd);
          if ((rowNumber == 0) && (this.logger.isDebugEnabled())) {
            this.logger.debug(new StringBuilder().append("Mapping column '").append(column)
                .append("' to property '")
                .append(pd.getName()).append("' of type '")
                .append(ClassUtils.getQualifiedName(pd.getPropertyType())).append("'")
                .toString());
          }
          try {
            bw.setPropertyValue(pd.getName(), value);
          } catch (TypeMismatchException ex) {
            if ((value == null) && (this.primitivesDefaultedForNullValue)) {
              if (this.logger.isDebugEnabled()) {
                this.logger.debug(
                    new StringBuilder().append("Intercepted TypeMismatchException for row ")
                        .append(rowNumber).append(" and column '").append(column)
                        .append("' with null value when setting property '")
                        .append(pd.getName()).append("' of type '")
                        .append(ClassUtils.getQualifiedName(pd.getPropertyType()))
                        .append("' on object: ").append(mappedObject).toString(), ex);
              }

            } else {
              throw ex;
            }
          }
          if (populatedProperties != null) {
            populatedProperties.add(pd.getName());
          }
        } catch (NotWritablePropertyException ex) {
          throw new DataRetrievalFailureException(
              new StringBuilder().append("Unable to map column '").append(column)
                  .append("' to property '").append(pd.getName()).append("'")
                  .toString(), ex);
        }

      } else if ((rowNumber == 0) && (this.logger.isDebugEnabled())) {
        this.logger.debug(
            new StringBuilder().append("No property found for column '").append(column)
                .append("' mapped to field '").append(field).append("'").toString());
      }

    }

    if ((populatedProperties != null) && (!(populatedProperties.equals(this.mappedProperties)))) {
      throw new InvalidDataAccessApiUsageException(new StringBuilder().append(
          "Given ResultSet does not contain all fields necessary to populate object of class [")
          .append(this.mappedClass.getName()).append("]: ")
          .append(this.mappedProperties).toString());
    }

    /* 336 */
    return (T) mappedObject;
  }


  protected void initBeanWrapper(BeanWrapper bw) {
    ConversionService cs = getConversionService();
    if (cs != null) {
      bw.setConversionService(cs);
    }
  }


  protected Object getColumnValue(ResultSet rs, int index, PropertyDescriptor pd)
      throws SQLException {
    return JdbcUtils.getResultSetValue(rs, index, pd.getPropertyType());
  }


  public static <T> TBeanPropertyRowMapper<T> newInstance(Class<T> mappedClass) {
    return new TBeanPropertyRowMapper(mappedClass);
  }
}
