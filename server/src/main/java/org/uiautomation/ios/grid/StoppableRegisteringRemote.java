package org.uiautomation.ios.grid;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHttpEntityEnclosingRequest;
import org.apache.http.message.BasicHttpRequest;
import org.json.JSONObject;
import org.openqa.grid.common.RegistrationRequest;
import org.openqa.grid.common.exception.GridConfigurationException;
import org.openqa.selenium.remote.internal.HttpClientFactory;
import org.openqa.selenium.remote.server.log.LoggingManager;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Logger;

import static org.uiautomation.ios.communication.Helper.extractObject;


public class StoppableRegisteringRemote {


  private static final Logger log = Logger.getLogger(StoppableRegisteringRemote.class.getName());
  private RegistrationRequest nodeConfig;
  private final HttpClientFactory httpClientFactory;
  private boolean run = true;

  public StoppableRegisteringRemote(RegistrationRequest config) {
    this.nodeConfig = config;
    this.httpClientFactory = new HttpClientFactory();
  }

  public URL getRemoteURL() {
    String host = (String) nodeConfig.getConfiguration().get(RegistrationRequest.HOST);
    String port = (String) nodeConfig.getConfiguration().get(RegistrationRequest.PORT);
    String url = "http://" + host + ":" + port;

    try {
      return new URL(url);
    } catch (MalformedURLException e) {
      throw new GridConfigurationException("error building the node url " + e.getMessage(), e);
    }
  }


  /**
   * register the hub following the configuration : <p/> - check if the proxy is already registered before sending a reg request. <p/> - register
   * again every X ms is specified in the config of the node.
   */
  public void startRegistrationProcess() {
    log.info("using the json request : " + nodeConfig.toJSON());

    final int registerCycleInterval = nodeConfig.getConfigAsInt(RegistrationRequest.REGISTER_CYCLE, 0);
    if (registerCycleInterval > 0) {
      new Thread(new Runnable() { // Thread safety reviewed

        public void run() {
          boolean first = true;
          log.info("Starting auto register thread. Will try to register every " + registerCycleInterval + " ms.");
          synchronized (this) {
            while (run) {
              try {
                boolean checkForPresence = true;
                if (first) {
                  first = false;
                  checkForPresence = false;
                }
                registerToHub(checkForPresence);
              } catch (org.openqa.grid.common.exception.GridException e) {
                log.info("couldn't register this node : " + e.getMessage());
              }
              try {
                Thread.sleep(registerCycleInterval);
              } catch (InterruptedException e) {
                e.printStackTrace();
              }
              // While we wait for someone to rewrite server logging.
              LoggingManager.perSessionLogHandler().clearThreadTempLogs();
            }
          }
        }
      }).start();
    }
    LoggingManager.perSessionLogHandler().clearThreadTempLogs();
  }

  public void stopRegistrationProcess() {
    synchronized (this) {
      run = false;
    }
  }


  private void registerToHub(boolean checkPresenceFirst) {
    if (!checkPresenceFirst || !isAlreadyRegistered(nodeConfig)) {
      String tmp =
          "http://" + nodeConfig.getConfiguration().get(RegistrationRequest.HUB_HOST) + ":"
          + nodeConfig.getConfiguration().get(RegistrationRequest.HUB_PORT) + "/grid/register";

      HttpClient client = httpClientFactory.getHttpClient();
      try {
        URL registration = new URL(tmp);
        log.info("Registering the node to hub :" + registration);

        BasicHttpEntityEnclosingRequest r =
            new BasicHttpEntityEnclosingRequest("POST", registration.toExternalForm());
        String json = nodeConfig.toJSON();
        r.setEntity(new StringEntity(json, "UTF-8"));

        HttpHost host = new HttpHost(registration.getHost(), registration.getPort());
        HttpResponse response = client.execute(host, r);
        if (response.getStatusLine().getStatusCode() != 200) {
          throw new RuntimeException("Error sending the registration request.");
        }
      } catch (Exception e) {
        throw new org.openqa.grid.common.exception.GridException("Error sending the registration request.", e);
      }
    } else {
      log.fine("The node is already present on the hub. Skipping registration.");
    }

  }

  private boolean isAlreadyRegistered(RegistrationRequest node) {

    HttpClient client = httpClientFactory.getHttpClient();
    try {
      String tmp =
          "http://" + node.getConfiguration().get(RegistrationRequest.HUB_HOST) + ":"
          + node.getConfiguration().get(RegistrationRequest.HUB_PORT) + "/grid/api/proxy";
      URL api = new URL(tmp);
      HttpHost host = new HttpHost(api.getHost(), api.getPort());

      String id = (String) node.getConfiguration().get(RegistrationRequest.ID);
      if (id == null) {
        id = (String) node.getConfiguration().get(RegistrationRequest.REMOTE_HOST);
      }
      BasicHttpRequest r = new BasicHttpRequest("GET", api.toExternalForm() + "?id=" + id);

      HttpResponse response = client.execute(host, r);
      if (response.getStatusLine().getStatusCode() != 200) {
        throw new org.openqa.grid.common.exception.GridException("Hub is down or not responding.");
      }
      JSONObject o = extractObject(response);
      return (Boolean) o.get("success");
    } catch (Exception e) {
      throw new org.openqa.grid.common.exception.GridException("Hub is down or not responding: " + e.getMessage());
    }
  }


}
