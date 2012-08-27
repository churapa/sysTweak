package com.kalgecin.systweak;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class BootService extends Service{
	//private final long mDelay = 0;
	//private final long mPeriod = 5000;
	private final String LOGTAG = "SysTweakBoot";
	@SuppressWarnings("unused")
	private Timer mTimer;
	private class LogTask extends TimerTask {
		public void run(){
			Log.i(LOGTAG,"scheduled run");
		}
	}
	@SuppressWarnings("unused")
	private LogTask mLogTask;
	
	@Override
	public IBinder onBind(final Intent intent){
		return null;
	}
	
	@Override
	public void onCreate(){
		super.onCreate();
		Log.i(LOGTAG,"created");
		mTimer = new Timer();
		mLogTask = new LogTask();
	}
	
	@Override
	public void onStart(final Intent intent, final int startId){
		//super.onStart(intent, startId);
		Log.i(LOGTAG,"started");
		settingsDB dataSrc = new settingsDB(this);
		dataSrc.open();
		Log.i(LOGTAG,"opened DB");
		if(dataSrc.getSetting("on_boot").equalsIgnoreCase("true")){
			ProcessBuilder cmd;
			Process process;
			
	         try{
	        	cmd = new ProcessBuilder("su");
		   		process = cmd.start();
		   		cmd.redirectErrorStream();
	        	String[] args = {"su","-c","pm","enable",""};
	        	String[] checks = MainActivity.checks;
	        	String[] CHKnames = MainActivity.CHKnames;
	        	String comm = "";
	           	BufferedOutputStream bw = new BufferedOutputStream(process.getOutputStream());
	           	BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));
				for(int i=0;i<checks.length;i++){
					args[4] = CHKnames[i];
	        		 if(dataSrc.getSetting(checks[i]).equalsIgnoreCase("true")){//CBchecks[i].isChecked()){
	        			 comm = "pm enable "+args[4]+";";
	        		 }else{
	        			 comm = "pm disable "+args[4]+";";
	        		 }
	        		 Log.i("sysTweak_BOOT",args[4]+":"+args[3]);
	        		 bw.write(comm.getBytes());
	           		 bw.flush();
	           		 
				}
				br.close();
	           	bw.close();
	           	process.waitFor();
	         }catch(IOException e){
	        	 e.printStackTrace();
	         } catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else{
			Log.i("sysTweak_BOOT","on_boot is disabled");
		}
		//mTimer.schedule(mLogTask, mDelay, mPeriod);
	}
}
