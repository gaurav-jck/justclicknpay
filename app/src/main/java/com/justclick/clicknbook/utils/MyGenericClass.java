package com.justclick.clicknbook.utils;

/**
 * Created by gaurav.singhal on 10/24/2017.
 */

public class MyGenericClass<T> {
  T obj;
  public void add(T obj){this.obj=obj;}
  T get(){return obj;}
}
