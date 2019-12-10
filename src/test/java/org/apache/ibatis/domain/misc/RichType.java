/**
 *    Copyright 2009-2015 the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.apache.ibatis.domain.misc;

<<<<<<< HEAD
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class RichType{
=======
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RichType {
>>>>>>> 5301c684afb0817920e573143b83a7605127b2e0

  private RichType richType;

  private String richField;

  private String richProperty;

  private Map richMap = new HashMap();

<<<<<<< HEAD
  private Map<String, Object> parametersMap = new HashMap();

  private String nihao;

=======
>>>>>>> 5301c684afb0817920e573143b83a7605127b2e0
  private List richList = new ArrayList() {
    {
      add("bar");
    }
  };

  public RichType getRichType() {
    return richType;
  }

  public void setRichType(RichType richType) {
    this.richType = richType;
  }

  public String getRichProperty() {
    return richProperty;
  }

  public void setRichProperty(String richProperty) {
    this.richProperty = richProperty;
  }

  public List getRichList() {
    return richList;
  }

  public void setRichList(List richList) {
    this.richList = richList;
  }

  public Map getRichMap() {
    return richMap;
  }

  public void setRichMap(Map richMap) {
    this.richMap = richMap;
  }
<<<<<<< HEAD

  public String getNihao() {
    return nihao;
  }

  public void setNihao(String nihao) {
    this.nihao = nihao;
  }
=======
>>>>>>> 5301c684afb0817920e573143b83a7605127b2e0
}
