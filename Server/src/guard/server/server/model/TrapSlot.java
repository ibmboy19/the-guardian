package guard.server.server.model;

import guard.server.server.model.instance.TrapInstance;
import guard.server.server.utils.collections.Maps;

import java.util.Collection;
import java.util.Map;

public class TrapSlot {

	private Map<Integer, TrapInstance> _trapList = Maps.newConcurrentMap();
	
	public Map<Integer, TrapInstance> getTrapList(){
		return _trapList;
	}
	
	public boolean CheckSlot(int _key){
		return _trapList.containsKey(_key);
	}
	public void PutTrap(int _key,TrapInstance _trap){
		_trapList.put(_key, _trap);
	}
	public TrapSlot(){
		
	}
	
}
