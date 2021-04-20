package com.admin.coredge.SpeedCalculation;

import com.admin.coredge.SpeedCalculation.NetworkConfig.SpeedConfig;
import com.admin.coredge.SpeedCalculation.NetworkConfig.TelConfig;
import com.admin.coredge.SpeedCalculation.SetupServer.SelectServer;
import com.admin.coredge.SpeedCalculation.SetupServer.ServerTestPoint;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;

public class SpeedCalculation {

    private ArrayList<ServerTestPoint> servers=new ArrayList<>();
    private ServerTestPoint selectedServer=null;
    private SpeedConfig config=new SpeedConfig();
    private TelConfig telemetryConfig=new TelConfig();
    private int state=0; //0=configs, 1=test points, 2=server selection, 3=ready, 4=testing, 5=finished

    private Object mutex=new Object();

    private String originalExtra="";

    public SpeedCalculation(){

    }

    public void setSpeedtestConfig(SpeedConfig c){
        synchronized (mutex){
            if(state!=0) throw new IllegalStateException("Cannot change config at this moment");
            config=c.clone();
            String extra=config.getTelemetry_extra();
            if(extra!=null&&!extra.isEmpty()) originalExtra=extra;
        }
    }

    public void setTelemetryConfig(TelConfig c){
        synchronized (mutex) {
            if (state != 0) throw new IllegalStateException("Cannot change config at this moment");
            telemetryConfig = c.clone();
        }
    }

    public void addTestPoint(ServerTestPoint t){
        synchronized (mutex) {
            if (state == 0) state = 1;
            if (state > 1) throw new IllegalStateException("Cannot add test points at this moment");
            servers.add(t);
        }
    }

    public void addTestPoints(ServerTestPoint[] s){
        synchronized (mutex) {
            for (ServerTestPoint t : s) addTestPoint(t);
        }
    }

    public void addTestPoint(JSONObject json){
        synchronized (mutex) {
            addTestPoint(new ServerTestPoint(json));
        }
    }

    public void addTestPoints(JSONArray json){
        synchronized (mutex) {
            for (int i = 0; i < json.length(); i++)
                try {
                    addTestPoint(json.getJSONObject(i));
                } catch (JSONException t) {
                }
        }
    }

    private static class ServerListLoader {
        private static String read(String url){
            try{
                URL u=new URL(url);
                InputStream in=u.openStream();
                BufferedReader br=new BufferedReader(new InputStreamReader(u.openStream()));
                String s="";
                try{
                    for(;;){
                        String r=br.readLine();
                        if(r==null) break; else s+=r;
                    }
                }catch(Throwable t){}
                br.close();
                in.close();
                return s;
            }catch(Throwable t){
                return null;
            }
        }

        public static ServerTestPoint[] loadServerList(String url){
            try{
                String s=null;
                if(url.startsWith("//")){
                    s=read("https:"+url);
                    if(s==null) s=read("http:"+url);
                }else s=read(url);
                if(s==null) throw new Exception("Failed");
                JSONArray a=new JSONArray(s);
                ArrayList<ServerTestPoint> ret=new ArrayList<>();
                for(int i=0;i<a.length();i++){
                    ret.add(new ServerTestPoint(a.getJSONObject(i)));
                }
                return ret.toArray(new ServerTestPoint[0]);
            }catch(Throwable t){
                return null;
            }
        }
    }
    public boolean loadServerList(String url){
        synchronized (mutex) {
            if (state == 0) state = 1;
            if (state > 1) throw new IllegalStateException("Cannot add test points at this moment");
            ServerTestPoint[] pts= ServerListLoader.loadServerList(url);
            if(pts!=null){
                addTestPoints(pts);
                return true;
            }else return false;
        }
    }

    public ServerTestPoint[] getTestPoints(){
        synchronized (mutex) {
            return servers.toArray(new ServerTestPoint[0]);
        }
    }

    private SelectServer ss=null;
    public void selectServer(final ServerSelectedHandler callback){
        synchronized (mutex) {
            if (state == 0) throw new IllegalStateException("No test points added");
            if (state == 2) throw new IllegalStateException("Server selection is in progress");
            if (state > 2) throw new IllegalStateException("Server already selected");
            state = 2;
            ss = new SelectServer(getTestPoints(), config.getPing_connectTimeout()) {
                @Override
                public void onServerSelected(ServerTestPoint server) {
                    selectedServer = server;
                    synchronized (mutex) {
                        if (server != null) state = 3; else state = 1;
                    }
                    callback.onServerSelected(server);
                }
            };
            ss.start();
        }
    }

    public void setSelectedServer(ServerTestPoint t){
        synchronized (mutex) {
            if (state == 2) throw new IllegalStateException("Server selection is in progress");
            if (t == null) throw new IllegalArgumentException("t is null");
            selectedServer = t;
            state = 3;
        }
    }

    private SpeedtestWorker st=null;
    public void start(final SpeedtestHandler callback){
        synchronized (mutex) {
            if (state < 3) throw new IllegalStateException("Server hasn't been selected yet");
            if (state == 4) throw new IllegalStateException("Test already running");
            state = 4;
            try {
                JSONObject extra = new JSONObject();
                if (originalExtra != null && !originalExtra.isEmpty())
                    extra.put("extra", originalExtra);
                extra.put("server", selectedServer.getName());
                config.setTelemetry_extra(extra.toString());
            } catch (Throwable t) {
            }
            st = new SpeedtestWorker(selectedServer, config, telemetryConfig) {
                @Override
                public void onDownloadUpdate(double dl, double progress) {
                    callback.onDownloadUpdate(dl, progress);
                }

                @Override
                public void onUploadUpdate(double ul, double progress) {
                    callback.onUploadUpdate(ul, progress);
                }

                @Override
                public void onPingJitterUpdate(double ping, double jitter, double progress) {
                    callback.onPingJitterUpdate(ping, jitter, progress);
                }

                @Override
                public void onIPInfoUpdate(String ipInfo) {
                    callback.onIPInfoUpdate(ipInfo);
                }

                @Override
                public void onTestIDReceived(String id) {
                    String shareURL=prepareShareURL(telemetryConfig);
                    if(shareURL!=null) shareURL=String.format(shareURL,id);
                    callback.onTestIDReceived(id,shareURL);
                }

                @Override
                public void onEnd() {
                    synchronized (mutex) {
                        state = 5;
                    }
                    callback.onEnd();
                }

                @Override
                public void onCriticalFailure(String err) {
                    synchronized (mutex) {
                        state = 5;
                    }
                    callback.onCriticalFailure(err);
                }
            };
        }
    }

    private String prepareShareURL(TelConfig c){
        if(c==null) return null;
        String server=c.getServer(), shareURL=c.getShareURL();
        if(server==null||server.isEmpty()||shareURL==null||shareURL.isEmpty()) return null;
        if(!server.endsWith("/")) server=server+"/";
        while(shareURL.startsWith("/")) shareURL=shareURL.substring(1);
        if(server.startsWith("//")) server="https:"+server;
        return server+shareURL;
    }

    public void abort(){
        synchronized (mutex) {
            if (state == 2) ss.stopASAP();
            if (state == 4) st.abort();
            state = 5;
        }
    }

    public static abstract class ServerSelectedHandler{
        public abstract void onServerSelected(ServerTestPoint server);
    }
    public static abstract class SpeedtestHandler{
        public abstract void onDownloadUpdate(double dl, double progress);
        public abstract void onUploadUpdate(double ul, double progress);
        public abstract void onPingJitterUpdate(double ping, double jitter, double progress);
        public abstract void onIPInfoUpdate(String ipInfo);
        public abstract void onTestIDReceived(String id, String shareURL);
        public abstract void onEnd();
        public abstract void onCriticalFailure(String err);
    }

}
