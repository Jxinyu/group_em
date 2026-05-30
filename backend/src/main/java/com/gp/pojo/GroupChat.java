package com.gp.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class GroupChat {

  private Integer id;

  private String username;

  private String content;

  private Integer contentType;

  private String createDate;

  private String chatName;

}
