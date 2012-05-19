package org.uiautomation.ios.ide.pages.begin;

import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;

import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.uiautomation.ios.server.IOSServer;
import org.uiautomation.ios.server.IOSServerConfiguration;
import org.uiautomation.ios.server.servlet.CustomMessage;

public class ConfigurationServerTest {


  private IOSServer server;
  private String host = "localhost";
  private String[] args = {"-port", "4444"};
  private IOSServerConfiguration config = IOSServerConfiguration.create(args);
  private String requested_url = "http://" + host + ":" + config.getPort() + "/wd/hub/ide/begin";
  private String invalid_app_name = "invalid.app";
  private String valid_app_name = "UICatalog.app";
  
  private static final String APPS_FILE = "/supportedApps.txt"; // File to locate the app
  
  @DataProvider(name = "invalidInjectionIntoAppFile")
  public Object[][] createData(){
    File app_file;
    try {
      app_file = getFromClassPath(APPS_FILE);
      FileWriter fstream = new FileWriter(app_file.getAbsolutePath());
      BufferedWriter out = new BufferedWriter(fstream);
      out.write(invalid_app_name);
      out.flush();
      out.close();
      fstream.close();
    } catch (Exception e) {
      System.err.println("Dataprovider Error.");
      e.printStackTrace();
    }
    String[][] blank = {{invalid_app_name,"success"}};
    return blank;
  }

  @DataProvider(name = "validInjectionIntoAppFile")
  public Object[][] createData2(){
    File app_file;
    try {
      app_file = getFromClassPath(APPS_FILE);
      FileWriter fstream = new FileWriter(app_file.getAbsolutePath());
      BufferedWriter out = new BufferedWriter(fstream);
      out.write(valid_app_name);
      out.flush();
      out.close();
      fstream.close();
    } catch (Exception e) {
      System.err.println("Dataprovider Error.");
      e.printStackTrace();
    }
    String[][] blank = {{valid_app_name,"success"}};
    return blank;
  }
  @Test(dataProvider="invalidInjectionIntoAppFile")
  public void invalidAppInsideAppsFileTest(String seed, String status) throws Exception{
    
    server = new IOSServer(config);
    server.start();
    
    URL url = new URL(requested_url);
    URLConnection urlConn = url.openConnection(); 
    urlConn.setDoInput(true); 
    urlConn.setUseCaches(false);

    DataInputStream dis = new DataInputStream(urlConn.getInputStream()); 
    Assert.assertEquals(PageContains("Cannot load the resource /sampleApps/"+invalid_app_name, dis), true); 

    
  }
  
  @Test(dataProvider = "validInjectionIntoAppFile")
  public void validAppInsideAppsFileTest(String seed, String status) throws Exception{
    
    server = new IOSServer(config);
    server.start();
    
    URL url = new URL(requested_url);
    URLConnection urlConn = url.openConnection(); 
    urlConn.setDoInput(true); 
    urlConn.setUseCaches(false);

    DataInputStream dis = new DataInputStream(urlConn.getInputStream()); 
    Assert.assertEquals(PageContains("Successfully load the resources!", dis), true); 

    
  }
  
  private boolean PageContains(String pattern, DataInputStream page) throws IOException {
    String seed_str = "";
    String tmp;
    while((tmp = page.readLine()) != null){
      seed_str += tmp;
    }
    System.out.println(seed_str);
    return seed_str.matches(".*"+pattern+".*");
  }

  @AfterMethod
  public void stopServer() throws Exception {
    server.stop();
  }
  
  private static File getFromClassPath(String resource) throws Exception{
    File res = null;
    URL url = null;
    try{
      url = IOSServerConfiguration.class.getResource(resource);
      if (url.toExternalForm().startsWith("file:")) {
        res = new File(url.toExternalForm().replace("file:", ""));
      }
    }
    catch(Exception e){
      throw new CustomMessage("Cannot load the resource "+resource,"error");
    }
    
    if (res == null || !res.exists()) {
      throw new CustomMessage("Couldn't locate the file from "+ url.toString(),"error");
    }
    return res;
  }
}
