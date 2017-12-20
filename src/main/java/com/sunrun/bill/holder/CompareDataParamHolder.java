package com.sunrun.bill.holder;

import com.sunrun.bill.db.DbOpt;
import com.sunrun.bill.file.FileOpt;

public class CompareDataParamHolder {

	private FileOpt fileOpt;
	private DbOpt dbOpt;
	public FileOpt getFileOpt() {
		return fileOpt;
	}
	public DbOpt getDbOpt() {
		return dbOpt;
	}
	public CompareDataParamHolder(FileOpt fileOpt,DbOpt dbOpt){
		this.dbOpt=dbOpt;
		this.fileOpt=fileOpt;
	}
}
