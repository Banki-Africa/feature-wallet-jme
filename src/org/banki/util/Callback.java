package org.banki.util;

import org.json.me.JSONObject;

public class Callback
{
  public Callback() {}
  
  public void call(JSONObject json) {
    System.out.println(json);
  }
}
