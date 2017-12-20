package com.sunrun.mpayrecon.processor.inf;

import com.sunrun.mpayrecon.model.SessionContext;
import com.sunrun.mpayrecon.service.SharedService;

public interface Processor {
	
	public void execute(SessionContext sessionContext, SharedService sharedService);

}
