package guard.server.server.model.instance;

import static guard.server.server.clientpacket.C_Gold.C_Gold_Normal;
import static guard.server.server.clientpacket.C_HunterInventory.C_HunterInventory_BuyItem;
import static guard.server.server.clientpacket.C_HunterInventory.C_HunterInventory_UseItem;
import static guard.server.server.clientpacket.C_HunterState.C_HunterState_Hp;
import static guard.server.server.clientpacket.C_HunterState.C_HunterState_Invisible;
import static guard.server.server.clientpacket.C_HunterState.C_HunterState_Life;
import static guard.server.server.clientpacket.C_HunterState.C_HunterState_Stamina;
import static guard.server.server.clientpacket.C_Projectile.C_Projectile_Request;
import static guard.server.server.clientpacket.C_Treasure.C_Treasure_TreasureReturn;
import static guard.server.server.clientpacket.ClientOpcodes.C_Gold;
import static guard.server.server.clientpacket.ClientOpcodes.C_HunterInventory;
import static guard.server.server.clientpacket.ClientOpcodes.C_HunterState;
import static guard.server.server.clientpacket.ClientOpcodes.C_PacketSymbol;
import static guard.server.server.clientpacket.ClientOpcodes.C_Projectile;
import static guard.server.server.clientpacket.ClientOpcodes.C_Treasure;
import static guard.server.server.model.instance.PlayerInstance.PlayerType_Hunter;
import guard.server.server.model.GameRoom;
import guard.server.server.model.GameProps.ChronicPotion;
import guard.server.server.model.GameProps.HunterItem;
import guard.server.server.model.GameProps.InstantPotion;
import guard.server.server.model.GameProps.Projectile;
import guard.server.server.utils.MathUtil;
import guard.server.server.utils.collections.Maps;

import java.util.Map;

public class HunterInstance extends WickedRoadPlayerInstance {

	/** 玩者 */
	private final PlayerInstance _pc;

	public PlayerInstance getActiveChar() {
		return _pc;
	}

	public String getAccountName() {
		return _pc.getAccountName();
	}

	/** 使用的房間 */
	private final GameRoom _room;
	private final GameInstance _game;

	public GameRoom getRoom() {
		return _room;
	}

	/***/
	private boolean _isSpawned;

	public boolean IsSpawned() {
		return _isSpawned;
	}

	public void SetSpawned() {
		_isSpawned = true;
	}

	/** 復活次數 */
	private int _lives;

	public int getLives() {
		return _lives;
	}

	public boolean CanRevive() {
		return _lives > 0;
	}

	/** 復活 */
	public void Revive() {
		// 活著不能復活
		if (!IsDead())
			return;
		if (!CanRevive())
			return;

		this._hp = _room.getMap().getHunterHP();
		this._stamina = this.MAX_Stamina;
		/**
		 * TODO Send Packet : lives hp isDead
		 * */
		_pc.getRoom().broadcastPacketToRoom(
				String.valueOf(C_HunterState) + C_PacketSymbol
						+ _pc.getAccountName() + C_PacketSymbol
						+ String.valueOf(C_HunterState_Hp) + ","
						+ String.valueOf(_hp) + ";"
						+ String.valueOf(C_HunterState_Stamina) + ","
						+ String.valueOf(_stamina));

	}

	/**
	 * 獵人血量相關
	 * 
	 * @param _adjustValue
	 *            正值:治癒; 負值:傷害
	 * */
	public boolean IsDead() {
		return _hp == 0;
	}

	private int _hp;

	public int getHP() {
		return _hp;
	}

	// 正值: 補血 ;負值: 傷害 ; 死人無作用
	public int ApplyHP(int _adjustValue) {
		// 死人無作用
		if (IsDead())
			return 0;
		// 補血 對於血滿無作用
		if (_adjustValue > 0) {
			if (this._hp == _pc.getRoom().getMap().getHunterHP())
				return 0;
		}

		int _deltaHP = this._hp;

		int bufferHP = this._hp + _adjustValue;

		this._hp = MathUtil.Clamp(bufferHP, 0, _room.getMap().getHunterHP());

		_deltaHP = this._hp - _deltaHP;

		/**
		 * TODO Send Packet : C_HunterState,C_HunterState_Hp
		 * */

		// 死亡時 生命減少
		if (IsDead()) {
			_lives--;
			if (this._staminaMaximize) {
				StaminaMaximizeCancel();
			}

			// 更新狀態
			_room.broadcastPacketToRoom(String.valueOf(C_HunterState)
					+ C_PacketSymbol + _pc.getAccountName() + C_PacketSymbol
					+ String.valueOf(C_HunterState_Hp) + ","
					+ String.valueOf(_hp) + ";"
					+ String.valueOf(C_HunterState_Life) + ","
					+ String.valueOf(_lives));
			// 檢查寶藏擁有者
			if (_game.getTreasure().IsOwner(this)) {

				_game.TreasureReturn();

				// TODO Send Packet 寶藏回歸 封包
				_room.broadcastPacketToRoom(String.valueOf(C_Treasure)
						+ C_PacketSymbol
						+ String.valueOf(C_Treasure_TreasureReturn));

			}
			// 若玩者死亡且不能復活 偵測遊戲是否結束
			if (_lives == 0) {
				_game.DetecteGameIsOver();
			}

		} else {
			_room.broadcastPacketToRoom(String.valueOf(C_HunterState)
					+ C_PacketSymbol + _pc.getAccountName() + C_PacketSymbol
					+ String.valueOf(C_HunterState_Hp) + ","
					+ String.valueOf(_hp));

		}

		return _deltaHP;
	}

	public void ApplyCostStamina(float _adjustValue) {
		if (IsDead())
			return;
		if (this._staminaMaximize)// 耐力藥水狀態下 不會增減
			return;
		if (_adjustValue > 0 && this._stamina == MAX_Stamina) {
			return;
		}

		float bufferStamina = this._stamina + _adjustValue;

		this._stamina = MathUtil.Clamp(bufferStamina, MIN_Stamina, MAX_Stamina);

		// TODO Send Packet
		_pc.SendClientPacket(String.valueOf(C_HunterState) + C_PacketSymbol
				+ _pc.getAccountName() + C_PacketSymbol
				+ String.valueOf(C_HunterState_Stamina) + ","
				+ String.valueOf(_stamina));

	}

	/** 獵人移動相關 */
	// 預計要以什麼方式移動
	private boolean _runOrWalk = false;
	private MoveState _moveState = MoveState.Idle;

	// 切換移動方式
	private void SwitchRunWalk(boolean _runwalkState) {
		if (_lockStamina) {
			if (_runOrWalk) {
				_runOrWalk = false;
			}
			return;
		}
		if (_runOrWalk != _runwalkState) {
			this._runOrWalk = _runwalkState;
		}
	}

	// 更新移動狀態
	public void UpdateMoveState(int _fb, int _lr, boolean _runwalkState) {
		if (_fb == 0 && _lr == 0) {
			_moveState = MoveState.Idle;
		} else if (_runOrWalk && !_lockStamina) {
			_moveState = MoveState.Run;
		} else {
			_moveState = MoveState.Walk;
		}
		SwitchRunWalk(_runwalkState);
	}

	// 耐力
	public static final float MIN_Stamina = 0.0f, MAX_Stamina = 1.0f;
	private float _stamina = MAX_Stamina;
	private boolean _staminaConsumeFlag;
	private float _staminaConsumeTime;
	// 鎖耐力
	private boolean _lockStamina = false;
	// 狀態 - 耐力最大
	private boolean _staminaMaximize = false;
	private float _staminaMaximizeDuration, _staminaMaximizeRecordTime;

	// 最大化耐力
	private void StaminaMaxmize(float _staminaMaximizeTime,
			float _staminaMaximizeRecordTime) {
		this._staminaMaximizeDuration = _staminaMaximizeTime;
		this._staminaMaximizeRecordTime = _staminaMaximizeRecordTime;
		_stamina = MAX_Stamina;
		_staminaMaximize = true;
		// TODO Send Packet 耐力最大化
		_pc.SendClientPacket(String.valueOf(C_HunterState) + C_PacketSymbol
				+ _pc.getAccountName() + C_PacketSymbol
				+ String.valueOf(C_HunterState_Stamina) + ",2");
	}

	// 耐力最大化失效
	private void StaminaMaximizeCancel() {
		_staminaMaximize = false;
		_pc.SendClientPacket(String.valueOf(C_HunterState) + C_PacketSymbol
				+ _pc.getAccountName() + C_PacketSymbol
				+ String.valueOf(C_HunterState_Stamina) + ",-1");

	}

	// 隱形狀態
	private boolean _invisible = false;
	private float _invisibleDuration, _invisibleRecordTime;

	private void SetInvisibleState(float _invisibleDuration,
			float _invisibleRecordTime) {
		this._invisibleDuration = _invisibleDuration;
		this._invisibleRecordTime = _invisibleRecordTime;
		_invisible = true;
		// TODO Send Packet 隱形狀態
		_pc.getRoom().broadcastPacketToRoom(
				String.valueOf(C_HunterState) + C_PacketSymbol
						+ _pc.getAccountName() + C_PacketSymbol
						+ String.valueOf(C_HunterState_Invisible) + ","
						+ String.valueOf(1));
	}

	private void ConsumeStamina(float gameTime) {
		if (_stamina != MIN_Stamina) {// 可消耗狀態
			_stamina = MathUtil.Clamp(_stamina
					- _room.getMap().getStaminaConsumValue(), MIN_Stamina,
					MAX_Stamina);// 消耗耐力
			_staminaConsumeFlag = true;// 設置消耗狀態
			_staminaConsumeTime = gameTime;// 設置消耗時間，計算CD
			// Send Packet
			_pc.SendClientPacket(String.valueOf(C_HunterState) + C_PacketSymbol
					+ _pc.getAccountName() + C_PacketSymbol
					+ String.valueOf(C_HunterState_Stamina) + ","
					+ String.valueOf(_stamina));
		} else {// 耐力見底
			_lockStamina = true;// 設置耐力用完的CD狀態
			// auto switch run walk
			_moveState = MoveState.Walk;// 強迫更新移動狀態成走路
			_runOrWalk = false;//
		}
	}

	private void RecoveryStamina(float gameTime) {
		if (_staminaConsumeFlag) {// 耐力回復CD中
			if (gameTime - _staminaConsumeTime > _room.getMap()
					.getStaminaRecoveryCD()) {
				_staminaConsumeFlag = false;
			}
		} else if (_stamina != MAX_Stamina) {// 耐力還可回復
			_stamina = MathUtil.Clamp(_stamina
					+ _room.getMap().getStaminaRecoveryValue(), MIN_Stamina,
					MAX_Stamina);

			// Send Packet
			_pc.SendClientPacket(String.valueOf(C_HunterState) + C_PacketSymbol
					+ _pc.getAccountName() + C_PacketSymbol
					+ String.valueOf(C_HunterState_Stamina) + ","
					+ String.valueOf(_stamina));
			if (_lockStamina) {// 限制跑步狀態
				if (_stamina >= .15f) {
					_lockStamina = false;
				}
			}
		}
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

	/**
	 * 主要更新函式
	 * */
	public void Update(float gameTime) {
		if (this.IsDead())
			return;
		if (!_staminaMaximize) {// 不為耐力藥水狀態下
			switch (_moveState) {
			case Run:
				ConsumeStamina(gameTime);
				break;
			default:
				RecoveryStamina(gameTime);
				break;
			}
		} else if (gameTime - _staminaMaximizeRecordTime > _staminaMaximizeDuration) {
			StaminaMaximizeCancel();
		}

		if (_invisible) {
			// Check Time Expire
			if (gameTime - _invisibleRecordTime > _invisibleDuration) {
				// TODO Send Packet - C_HunterState解除隱形
				_pc.getRoom().broadcastPacketToRoom(
						String.valueOf(C_HunterState) + C_PacketSymbol
								+ _pc.getAccountName() + C_PacketSymbol
								+ String.valueOf(C_HunterState_Invisible) + ","
								+ String.valueOf(0));
				_invisible = false;
			}
		}
	}

	// 使用道具
	public void UseItem(int _key) {
		// 查無物品
		if (!_hunterInventory.containsKey(_key))
			return;
		HunterItem _hItem = _hunterInventory.get(_key);
		// _hunterInventory.get(_key).UseItem(this);
		if (_hItem instanceof InstantPotion) {
			if (ApplyHP(((InstantPotion) _hItem).getRecoveryValue(_room
					.getMap().getHunterHP())) == 0) {
				return;
			}
		} else if (_hItem instanceof ChronicPotion) {
			switch (((ChronicPotion) _hItem).getPotionType()) {
			case Stamina:// 耐力藥水 - 送封包,設定耐力最大化狀態
				if (_staminaMaximize)
					return;
				StaminaMaxmize(((ChronicPotion) _hItem).getEffectDuration(),
						_pc.getRoom().getGame().getTime());
				break;
			case Invisible:// 隱形藥水 - 送封包,設定隱形狀態
				if (_invisible)
					return;
				SetInvisibleState(((ChronicPotion) _hItem).getEffectDuration(),
						_pc.getRoom().getGame().getTime());
				break;

			}
		} else if (_hItem instanceof Projectile) {
			_pc.SendClientPacket(String.valueOf(C_Projectile) + C_PacketSymbol
					+ String.valueOf(C_Projectile_Request) + C_PacketSymbol
					+ String.valueOf(((Projectile) _hItem).getModelID()));
		}

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

	// 抵達檢查點 - 獵人only
	public void ArriveCheckPoint(int _checkPointID, int _checkPointIndex) {
		_gold += _room.getMap().getArriveCheckPointReward();
		// TODO Send Packet C_Gold
		_pc.SendClientPacket(C_Gold + C_PacketSymbol
				+ String.valueOf(C_Gold_Normal) + C_PacketSymbol
				+ String.valueOf(_gold));
	}

	public void AquireGold(int _gold) {
		this._gold += _gold;
		// TODO Send Packet C_Gold
		_pc.SendClientPacket(C_Gold + C_PacketSymbol
				+ String.valueOf(C_Gold_Normal) + C_PacketSymbol
				+ String.valueOf(_gold));
	}

	public HunterInstance() {
		super();
		_room = null;
		_pc = null;
		_game = null;
		_hunterInventory = Maps.newConcurrentMap();
	}

	public HunterInstance(GameRoom _room, PlayerInstance _pc) {
		super(_room.getMap().getHunterInitGold());
		this._room = _room;
		this._game = _room.getGame();
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

	public enum MoveState {
		Run(0), Walk(1), Idle(2);

		private int value;

		MoveState(int value) {
			this.value = value;
		}

		public int getValue() {
			return value;
		}
	}
}
