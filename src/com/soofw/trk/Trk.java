package com.soofw.trk;

import android.app.Application;
import android.os.Environment;
import java.io.File;

public class Trk extends Application {
	File storageDir, listFile;

	@Override
	public void onCreate() {
		this.storageDir = Environment.getExternalStorageDirectory();
		this.listFile = new File(this.storageDir, ".todo");
	}
}
