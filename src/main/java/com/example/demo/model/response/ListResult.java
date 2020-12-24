package com.example.demo.model.response;

import java.io.Serializable;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @param <T>
 *
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ListResult<T> implements Serializable {

  private static final long serialVersionUID = 5967522243581183569L;

  private List<T> rows;

  private int total;

}
