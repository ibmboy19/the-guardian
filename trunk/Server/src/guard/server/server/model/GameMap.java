package guard.server.server.model;

import guard.server.server.model.GameProps.HunterItem;
import guard.server.server.model.GameProps.Item;
import guard.server.server.model.GameProps.Trap.Trap;
import guard.server.server.utils.collections.Lists;

import java.util.List;

public class GameMap {

	// 地圖名稱
	private final String _mapName;

	public final String getMapName() {
		return _mapName;
	}

	public static final int GameMode_Cooperation = 0;
	public static final int GameMode_Greedy = 1;
	
	// 遊戲模式 0: 合作 ; 1:貪婪
	private int _gameMode = 0;

	public int getGameMode() {
		return _gameMode;
	}

	// 遊戲時間
	private float _gamePlayTime = 0;

	public float getGamePlayTime() {
		return _gamePlayTime;
	}

	// 初始金錢
	private final int _hunterInitGold;

	public int getHunterInitGold() {
		return _hunterInitGold;
	}

	private final int _guardianInitGold;

	public int getGuardianInitGold() {
		return _guardianInitGold;
	}

	//獵人傷害值
	private final int _meleeAttackValue = 10;
	
	public int getMeleeAttackValue(){
		return _meleeAttackValue;
	}
	
	private final int _bulletAttackValue = 15;
	
	public int getBulletAttackValue(){
		return _bulletAttackValue;
	}
	
	// 獵人初始生命次數
	private final int _hunterLives;

	public int getHunterLives() {
		return _hunterLives;
	}

	private final int _hunterHP;

	public int getHunterHP() {
		return _hunterHP;
	}

	// 獵人擊殺獵人獎勵
	private final int _hunterSlainedReward;

	public int getHunterSlainedReward() {
		return _hunterSlainedReward;
	}

	// 到達檢查點獎勵
	private final int _arriveCheckPointReward;

	public int getArriveCheckPointReward() {
		return _arriveCheckPointReward;
	}

	// 守護神獎勵
	private final int _guardianReward;

	public int getGuardianReward() {
		return _guardianReward;
	}

	// 守護神獎勵間隔
	private final float _guardianRewardInterval;

	public float _guardianRewardInterval() {
		return _guardianRewardInterval;
	}

	// 守護神每1傷害的獎勵
	private final int _guardianDmgReward;

	public int getGuardianDmgReward() {
		return _guardianDmgReward;
	}

	// 地圖檢查碼 MD5 -確保加入的玩家地圖與HOST一樣
	private String _mapCheckCode;

	public String getMapCheckCode() {
		return _mapCheckCode;
	}

	public boolean IsSameMap(String _mapName, String _mapCheckCode) {
		return (this._mapName == _mapName)
				&& (this._mapCheckCode == _mapCheckCode);
	}

	// 獵人道具模組
	private final List<HunterItem> _itemList;

	public HunterItem getItem(int _index) {
		HunterItem _item = _index < _itemList.size() ? _itemList.get(_index)
				: null;

		return _item;
	}

	// 守護者陷阱模組
	private final List<Trap> _trapList;

	public Trap getTrap(int _index) {
		Trap _trap = _index >= _trapList.size() ? null : _trapList.get(_index);

		return _trap;
	}

	// 地圖中 已放置/未觸發 的陷阱

	public GameMap() {
		_mapName = "";
		_mapCheckCode = "";

		this._hunterInitGold = 0;
		this._guardianInitGold = 0;
		this._hunterLives = 4;
		this._hunterHP = 100;
		this._hunterSlainedReward = 0;
		this._arriveCheckPointReward = 0;
		this._guardianReward = 0;
		this._guardianRewardInterval = 0;
		this._guardianDmgReward = 0;

		this._itemList = Lists.newList();
		this._trapList = Lists.newList();

	}

	public GameMap(String _mapName, String _mapCheckCode, int _gameMode,
			float _gamePlayTime, int _hunterLives, int _hunterHP,
			int _hunterInitGold, int _guardianInitGold,
			int _hunterSlainedReward, int _arriveCheckPointReward,
			int _guardianReward, float _guardianRewardInterval,
			int _guardianDmgReward, List<HunterItem> _itemList,
			List<Trap> _trapList) {
		this._mapName = _mapName;
		this._mapCheckCode = _mapCheckCode;

		this._gameMode = _gameMode;
		this._gamePlayTime = _gamePlayTime;
		this._hunterLives = _hunterLives;
		this._hunterHP = _hunterHP;

		this._hunterInitGold = _hunterInitGold;
		this._guardianInitGold = _guardianInitGold;

		this._hunterSlainedReward = _hunterSlainedReward;
		this._arriveCheckPointReward = _arriveCheckPointReward;
		this._guardianReward = _guardianReward;
		this._guardianRewardInterval = _guardianRewardInterval;
		this._guardianDmgReward = _guardianDmgReward;

		this._itemList = _itemList;
		this._trapList = _trapList;

	}

}
