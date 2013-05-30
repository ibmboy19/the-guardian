package guard.server.server.model;

import guard.server.server.model.instance.PlayerInstance;
import guard.server.server.utils.collections.Maps;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

public class GuardWorld {
	private static GuardWorld _instance;
	private final Map<String, PlayerInstance> _allPlayers; // 所有玩家
	private final Map<String, GameRoom> _allRooms; // 所有遊戲室

	private GuardWorld() {
		_allPlayers = Maps.newConcurrentMap();
		_allRooms = Maps.newConcurrentMap();

	}

	public static GuardWorld getInstance() {
		if (_instance == null) {
			_instance = new GuardWorld();
		}
		return _instance;
	}

	/**
	 * 玩家相關
	 */
	public boolean CheckAccountExists(String _accountName){
		if(_allPlayers.containsKey(_accountName)){
			return true;
		}
		return false;
	}
	public void StorePlayer(PlayerInstance pc) {
		if (pc == null) {
			throw new NullPointerException();
		}
		_allPlayers.put(pc.getAccountName(), pc);
	}

	public void RemovePlayer(PlayerInstance pc) {
		if (pc == null) {
			throw new NullPointerException();
		}
		_allPlayers.remove(pc.getAccountName());
	}

	private Collection<PlayerInstance> _allPlayerValues;

	public Collection<PlayerInstance> getAllPlayers() {
		Collection<PlayerInstance> vs = _allPlayerValues;
		return (vs != null) ? vs : (_allPlayerValues = Collections.unmodifiableCollection(_allPlayers.values()));
	}

	public PlayerInstance getPlayer(String id) {
		return _allPlayers.get(id);
	}

	/**
	 * 遊戲室相關
	 */
	public void StoreRoom(GameRoom room) {
		if (room == null) {
			throw new NullPointerException();
		}
		_allRooms.put(room.getLeader().getAccountName(), room);
	}

	public void RemoveRoom(GameRoom room) {
		if (room == null) {
			throw new NullPointerException();
		}
		_allRooms.remove(room.getLeader().getAccountName());
		room = null;
	}

	private Collection<GameRoom> _allRoomValues;

	public Collection<GameRoom> getAllRooms() {
		Collection<GameRoom> vs = _allRoomValues;
		return (vs != null) ? vs : (_allRoomValues = Collections.unmodifiableCollection(_allRooms.values()));
	}

	public GameRoom getRoom(String id) {
		return _allRooms.get(id);
	}

	/**
	 * 廣播相關
	 */
	/**
	 * 世界廣播
	 * 
	 * @param id
	 * @param packet
	 */
	public void broadcastPacketToAllClient(String packet) {
		for (PlayerInstance pc : getAllPlayers()) {
			pc.SendClientPacket(packet);
		}
	}

	/**
	 * 廣播給某玩家
	 * 
	 * @param id
	 * @param packet
	 */
	public void broadcastPacketToClient(String toID, String packet) {
		_allPlayers.get(toID).SendClientPacket(packet);
	}
}