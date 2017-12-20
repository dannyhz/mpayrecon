package citic.hz.mpos.service.event;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import citic.hz.phio.stdmsg.PhioMsg;

/**
 * simple pub-sub event service
 * @author phio
 *
 */
public class EventService {
	
	private static ExecutorService es = new ThreadPoolExecutor(2,4,60L,TimeUnit.SECONDS,new LinkedBlockingQueue<Runnable>(100));
	private static Map<String, List<EventProcessor>> subs = new HashMap<String, List<EventProcessor>>();
	
	/**
	 * 发布
	 * @param event
	 * @param eventMsg
	 */
	public static void pub(String event,PhioMsg eventMsg){
		pub(new Event(event, eventMsg));
	}
	
	/**
	 * 发布
	 * @param e
	 */
	public static void pub(final Event e){
		List<EventProcessor> eplst = subs.get(e.getEventName());
		if(eplst != null && !eplst.isEmpty()){
			for(EventProcessor ep:eplst){
				final EventProcessor fep = ep;
				es.execute(new Runnable() {
					public void run() {
						fep.process(e);
					}
				});
			}
		}
	}
	
	/**
	 * 订阅
	 * @param event
	 * @param ep
	 */
	public static synchronized void sub(String event ,EventProcessor ep){
		List<EventProcessor> eplst = subs.get(event);
		if(eplst == null){
			eplst = new ArrayList<EventProcessor>();
			subs.put(event, eplst);
		}
		eplst.add(ep);
	}
	
}
