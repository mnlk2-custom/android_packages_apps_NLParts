package fr.nameless.NLParts;

import android.app.TabActivity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.widget.TabHost;


public class Main extends TabActivity { 
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.main);

	    Resources res = getResources(); 
	    TabHost tabHost = getTabHost();  
	    TabHost.TabSpec spec;  
	    Intent intent;  

	    // System
	    intent = new Intent().setClass(this, System.class);
	    spec = tabHost.newTabSpec("System").setIndicator(getString(R.string.system),
	                      res.getDrawable(R.drawable.tab_icon))
	                  .setContent(intent);
	    tabHost.addTab(spec);

	    // Lockscreen
	    intent = new Intent().setClass(this, Lockscreen.class);
	    spec = tabHost.newTabSpec("Lockscreen").setIndicator(getString(R.string.lockscreen),
	                      res.getDrawable(R.drawable.tab_icon))
	                  .setContent(intent);
	    tabHost.addTab(spec);

	    // Divers
	    intent = new Intent().setClass(this, Other.class);
	    spec = tabHost.newTabSpec("Autre").setIndicator(getString(R.string.divers),
	                      res.getDrawable(R.drawable.tab_icon))
	                  .setContent(intent);
	    tabHost.addTab(spec);

	    // Updates
	    intent = new Intent().setClassName("com.jojolejobar.nlupdater", "com.jojolejobar.nlupdater.NLUpdater");
	    spec = tabHost.newTabSpec("Updates").setIndicator(getString(R.string.update),
	                      res.getDrawable(R.drawable.tab_icon))
	                  .setContent(intent);

	    tabHost.addTab(spec);
	    
	    tabHost.setCurrentTab(0);
	}
}
