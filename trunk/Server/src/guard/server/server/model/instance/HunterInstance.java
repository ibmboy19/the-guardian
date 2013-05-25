package guard.server.server.model.instance;

import static guard.server.server.clientpacket.C_HunterInventory.C_HunterInventory_BuyItem;
import static guard.server.server.clientpacket.C_HunterInventory.C_HunterInventory_UseItem;
import static guard.server.server.clientpacket.ClientOpcodes.C_HunterInventory;
import static guard.server.server.clientpacket.ClientOpcodes.C_PacketSymbol;
import static guard.server.server.model.instance.PlayerInstance.PlayerType_Hunter;
import guard.server.server.model.GameRoom;
import guard.server.server.model.GameProps.HunterItem;
import guard.server.server.utils.MathUtil;
import guard.server.server.utils.collections.Maps;

import java.util.Map;

public class HunterInstance extends WickedRoadPlayerInstance {

	/** 玩者 */
	private final PlayerInstance _pc;
	/** 使用的房間 */
	private final GameRoom _room;

	public GameRoom getRoom() {
		return _room;
	}

	private int _lives;

	public int getLives() {
		return _lives;
	}

	public boolean CanRevive() {
		return _lives > 0;
	}

	/** 復活 */
	public void Revive(int _reviveHP) {
		if (!IsDead())
			return;
		if (!CanRevive())
			return;

		this._hp = _reviveHP;
		_lives--;
		/**
		 * TODO Send Packet : lives hp isDead
		 * */
		// _lives, _hp, IsDead()

	}

	public boolean IsDead() {
		return _hp == 0;
	}

	private int _hp;

	public int getHP() {
		return _hp;
	}

	/**
	 * 獵人血量相關
	 * 
	 * @param _adjustValue
	 *            正值:治癒; 負值:傷害
	 * */
	public void ApplyHP(int _adjustValue) {
		// 死人無作用
		if (IsDead())
			return;

		int bufferHP = this._hp + _adjustValue;

		this._hp = MathUtil.Clamp(bufferHP, 0, _room.getMap().getHunterHP());

		/**
		 * TODO Send Packet : C_HunterState
		 * */
		// _lives, _hp, IsDead()
	}

	// 獵人道具欄
	public final static int InventorySlotCount = 5;// 背包空間上限
	private Map<Integer, HunterItem> _hunterInventory;

	/***
	 * @param _key
	 *            購買成功後，要放置的格子編號
	 * @param _item
	 *            要買的物品
	 */
	public void BuyItem(int _slotKey, int _itemToBuyID) {

		// 空間不足
		if (_hunterInventory.size() >= InventorySlotCount) {
			// System.out.println("空間不足");
			return;

		}
		// 持有金不足
		if (_room.getMap().getItem(_itemToBuyID).getPrice() > _gold) {
			// System.out.println("金錢不足");
			return;
		}
		// 該格已有物品
		if (_hunterInventory.containsKey(_slotKey)) {
			// System.out.println("已有物品");
			return;
		}
		// 購買物品成功
		//
		HunterItem _itemToBuy = _room.getMap().getItem(_itemToBuyID);

		/*
		 * try { _itemToBuy = _room.getMap().getItem(_itemToBuyID);//.clone(); }
		 * catch (CloneNotSupportedException e) { // TODO Auto-generated catch
		 * block return; //e.printStackTrace(); }
		 */
		// if (_itemToBuy != null) {
		_hunterInventory.put(_slotKey, _itemToBuy);
		// 扣除金錢
		_gold -= _itemToBuy.getPrice();
		/**
		 * TODO Send Packet : C_HunterInventory,C_Gold
		 * 
		 * 
		 * */
		_pc.SendClientPacket(C_HunterInventory + C_PacketSymbol
				+ C_HunterInventory_BuyItem + C_PacketSymbol
				+ String.valueOf(_slotKey) + C_PacketSymbol
				+ String.valueOf(_itemToBuyID) + C_PacketSymbol
				+ String.valueOf(_gold));
		// }

	}

	// 使用道具
	public void UseItem(int _key) {
		// 查無物品
		if (!_hunterInventory.containsKey(_key))
			return;

		_hunterInventory.get(_key).UseItem(this);

		_hunterInventory.remove(_key);
		/**
		 * TODO Send Packet : C_HunterInventory
		 * 
		 * 
		 * */
		_pc.SendClientPacket(C_HunterInventory + C_PacketSymbol
				+ C_HunterInventory_UseItem + C_PacketSymbol
				+ String.valueOf(_key));
	}

	// 抵達檢查點
	public void ArriveCheckPoint() {

	}

	public HunterInstance() {
		super();
		_room = null;
		_pc = null;
		_hunterInventory = Maps.newConcurrentMap();
	}

	public HunterInstance(GameRoom _room, PlayerInstance _pc) {
		super(_room.getMap().getHunterInitGold());
		this._room = _room;
		this._pc = _pc;
		this._lives = _room.getMap().getHunterLives();
		this._hp = _room.getMap().getHunterHP();
		_hunterInventory = Maps.newConcurrentMap();
	}

	/** 取得玩者資料 */
	public String getPlayerModelData() {
		String _data = String.valueOf(PlayerType_Hunter) + ","
				+ _pc.getAccountName() + "," + String.valueOf(_lives) + ","
				+ String.valueOf(_hp) + "," + String.valueOf(_gold);
		return _data;
	}
}
