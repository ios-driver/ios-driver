/*
 * Copyright 2012-2013 eBay Software Foundation and ios-driver committers
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package org.uiautomation.ios.utils;


/**
 * stores how long it took to run all the methods in the class, including dependent method.
 */
class ClassTestTime implements Comparable<ClassTestTime> {

  private String classCanonicalName;
  private long duration;

  /**
   *
   * @param classCanonicalName
   * @param duration
   */
  public ClassTestTime(String classCanonicalName, int duration) {
    this.classCanonicalName = classCanonicalName;
    this.duration = duration;
  }


  @Override
  public int compareTo(ClassTestTime o) {
    return (int) (o.duration - this.duration);
  }

  @Override
  public String toString() {
    return classCanonicalName + " (" + duration + ")";
  }

  public long getDuration() {
    return duration;
  }

  public String getClassCanonicalName() {
    return classCanonicalName;
  }
}