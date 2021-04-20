package com.admin.coredge.SpeedCalculation.SetupServer;

import com.admin.coredge.SpeedCalculation.NetworkConfig.SpeedConfig;
import com.admin.coredge.SpeedCalculation.Ping.PingStream;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public abstract class SelectServer {

    private ArrayList<ServerTestPoint> servers=new ArrayList<>();
    private static final int PARALLELISM=6;
    private ServerTestPoint selectedTestPoint=null;
    private int state=NOT_STARTED;
    private static final int NOT_STARTED=0, WORKING=1, DONE=2;
    private int timeout;
    private static final int PINGS=3, SLOW_THRESHOLD=500;
    private boolean stopASAP=false;

    public SelectServer(ServerTestPoint[] servers, int timeout){
        addTestPoints(servers);
        this.timeout=timeout;
    }
    public void addTestPoint(ServerTestPoint t){
        if(state!=NOT_STARTED) throw new IllegalStateException("Cannot add test points at this time");
        if(t==null) return;
        servers.add(t);
    }
    public void addTestPoint(JSONObject t){
        if(state!=NOT_STARTED) throw new IllegalStateException("Cannot add test points at this time");
        servers.add(new ServerTestPoint(t));
    }
    public void addTestPoints(JSONArray a){
        if(state!=NOT_STARTED) throw new IllegalStateException("Cannot add test points at this time");
        for(int i=0;i<a.length();i++){
            try {
                servers.add(new ServerTestPoint(a.getJSONObject(i)));
            }catch (JSONException e){}
        }
    }
    public void addTestPoints(ServerTestPoint[] servers){
        if(state!=NOT_STARTED) throw new IllegalStateException("Cannot add test points at this time");
        for(ServerTestPoint t:servers) addTestPoint(t);
    }

    public ServerTestPoint getSelectedTestPoint() {
        if(state!=DONE) throw new IllegalStateException("Test point hasn't been selected yet");
        return selectedTestPoint;
    }

    public ServerTestPoint[] getTestPoints(){
        return servers.toArray(new ServerTestPoint[0]);
    }

    private Object mutex=new Object();
    private int tpPointer=0;
    private int activeStreams=0;
    private void next(){
        if(stopASAP) return;
        synchronized (mutex) {
            if (tpPointer >= servers.size()){
                if(activeStreams<=0){
                    selectedTestPoint=null;
                    for(ServerTestPoint t:servers){
                        if(t.ping==-1) continue;
                        if(selectedTestPoint==null||t.ping<selectedTestPoint.ping) selectedTestPoint=t;
                    }
                    if(state==DONE) return;
                    state=DONE;
                    onServerSelected(selectedTestPoint);
                }
                return;
            }
            final ServerTestPoint tp=servers.get(tpPointer++);
            PingStream ps=new PingStream(tp.getServer(),tp.getPingURL(),PINGS, SpeedConfig.ONERROR_FAIL,timeout,timeout,-1,-1,null) {
                @Override
                public void onError(String err) {
                    tp.ping=-1;
                    synchronized (mutex){activeStreams--;}
                    next();
                }

                @Override
                public boolean onPong(long ns) {
                    float p=ns/1000000f;
                    if(tp.ping==-1||p<tp.ping) tp.ping=p;
                    if(stopASAP) return false;
                    return p<SLOW_THRESHOLD;
                }

                @Override
                public void onDone() {
                    synchronized (mutex){activeStreams--;}
                    next();
                }
            };
            activeStreams++;
        }
    }

    public void start(){
        if(state!=NOT_STARTED) throw new IllegalStateException("Already started");
        state=WORKING;
        for(ServerTestPoint t:servers) t.ping=-1;
        for(int i=0;i<PARALLELISM;i++) next();
    }

    public void stopASAP(){
        stopASAP=true;
    }

    public abstract void onServerSelected(ServerTestPoint server);

}
