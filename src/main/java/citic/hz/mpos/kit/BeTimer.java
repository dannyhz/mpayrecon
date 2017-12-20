package citic.hz.mpos.kit;

/**
 * 计算操作所用时间的辅助类
 * @author phio
 *
 */
public class BeTimer {
	
	private long begt,endt;
	
	public BeTimer() {
		reset();
	}
	
	public void reset(){
		begt = System.currentTimeMillis();
	}
	
	public long stop(){
		endt = System.currentTimeMillis();
		return endt - begt;
	}
	
}
