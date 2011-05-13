package fr.nameless.NLParts;

import android.app.TabActivity;
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
	    spec = tabHost.newTabSpec("System").setIndicator("System",
	                      res.getDrawable(R.drawable.tab_icon))
	                  .setContent(intent);
	    tabHost.addTab(spec);

	    // Lockscreen
	    intent = new Intent().setClass(this, Lockscreen.class);
	    spec = tabHost.newTabSpec("Lockscreen").setIndicator("Lockscreen",
	                      res.getDrawable(R.drawable.tab_icon))
	                  .setContent(intent);
	    tabHost.addTab(spec);

	    // Divers
	    intent = new Intent().setClass(this, Other.class);
	    spec = tabHost.newTabSpec("Autre").setIndicator("Autre",
	                      res.getDrawable(R.drawable.tab_icon))
	                  .setContent(intent);
	    tabHost.addTab(spec);

	    // Updates
	    intent = new Intent().setClass(this, Updater.class);
	    spec = tabHost.newTabSpec("Updates").setIndicator("Updates",
	                      res.getDrawable(R.drawable.tab_icon))
	                  .setContent(intent);
	    tabHost.addTab(spec);
	    
	    tabHost.setCurrentTab(0);
	}
}
