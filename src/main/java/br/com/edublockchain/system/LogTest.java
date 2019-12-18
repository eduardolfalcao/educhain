package br.com.edublockchain.system;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

public class LogTest {

  public static Logger logger = Logger.getLogger(LogTest.class.getName());

  public static void main(String[] args) {
//    System.out.println(logger.getLevel());
//    logger.setLevel(Level.DEBUG);
//    System.out.println(logger.getLevel());
//    System.out.println(logger.isDebugEnabled());
    
    logger.debug("aaaaaaaaaaa");
  }
}
