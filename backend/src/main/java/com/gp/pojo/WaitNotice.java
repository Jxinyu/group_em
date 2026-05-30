package com.gp.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class WaitNotice {

  /**
   * 推送的公告id
   */
  private Integer nId;

  /**
   * 推送的方式
   */
  private Integer pushWay;

  /**
   * 推送的范围
   */
  private Integer scope;
  private String pushDate;
}
