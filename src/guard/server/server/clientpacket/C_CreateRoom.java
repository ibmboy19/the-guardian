package guard.server.server.clientpacket;

import static guard.server.server.clientpacket.ClientOpcodes.C_PacketSymbol;
import guard.server.server.ClientProcess;
import guard.server.server.model.GameMap;
import guard.server.server.model.GameRoom;
import guard.server.server.model.GuardWorld;
import guard.server.server.model.GameProps.ChronicPotion;
import guard.server.server.model.GameProps.HunterItem;
import guard.server.server.model.GameProps.InstantPotion;
import guard.server.server.model.GameProps.Projectile;
import guard.server.server.model.GameProps.Trap.DetonatedTrap;
import guard.server.server.model.GameProps.Trap.SummoningTrap;
import guard.server.server.model.GameProps.Trap.TimingTrap;
import guard.server.server.model.GameProps.Trap.Trap;
import guard.server.server.model.instance.GameInstance;
import guard.server.server.model.instance.PlayerInstance;
import guard.server.server.utils.collections.Lists;

import java.util.List;

/**
 * 要求創立遊戲房
 */
public class C_CreateRoom {
	public C_CreateRoom(ClientProcess _client, String _packet) {
		PlayerInstance leader = _client.getActiveChar();

		// Check NOT IN ROOM
		if (leader.isInRoom()) {
			return;
		}
		/**
		 * Format: Map Info :
		 * (roomName,hostID,mapName,checkCode,gameMode,maxPCCount
		 * ,playTime,hunterLives) ItemList : (,,,,,);(,,,,,);(,,,,,)......
		 * TrapList : (,,,,,);(,,,,,);(,,,,,)......
		 **/

		/** 切割用符號 */
		String _symbol1 = ",", _symbol2 = ";";
		/** 地圖資料 */
		String _mapData = _packet.split(C_PacketSymbol)[1];
		/** 物品資料 */
		String _itemData = _packet.split(C_PacketSymbol)[2];
		/** 陷阱資料 */
		String _trapData = _packet.split(C_PacketSymbol)[3];

		// Initial Item And Trap
		List<HunterItem> _itemList = Lists.newList();
		List<Trap> _trapList = Lists.newList();

		/** 載入所有獵人道具 */
		for (String _idata : _itemData.split(_symbol2)) {
			HunterItem _item = null;
			switch (_idata.split(_symbol1)[1]) {
			case "InstantPotion":
				_item = new InstantPotion(Integer.valueOf(_idata
						.split(_symbol1)[0]), _idata.split(_symbol1)[2],
						Integer.valueOf(_idata.split(_symbol1)[3]),
						Integer.valueOf(_idata.split(_symbol1)[4]),
						Integer.valueOf(_idata.split(_symbol1)[5]),
						Integer.valueOf(_idata.split(_symbol1)[6]));
				break;
			case "ChronicPotion":
				_item = new ChronicPotion(Integer.valueOf(_idata
						.split(_symbol1)[0]), _idata.split(_symbol1)[2],
						Integer.valueOf(_idata.split(_symbol1)[3]),
						Integer.valueOf(_idata.split(_symbol1)[4]),
						Integer.valueOf(_idata.split(_symbol1)[5]),
						Float.valueOf(_idata.split(_symbol1)[6]));
				break;
			case "Projectile":
				_item = new Projectile(
						Integer.valueOf(_idata.split(_symbol1)[0]),
						_idata.split(_symbol1)[2], Integer.valueOf(_idata
								.split(_symbol1)[3]), Integer.valueOf(_idata
								.split(_symbol1)[4]), Integer.valueOf(_idata
								.split(_symbol1)[5]));
				break;
			}

			_itemList.add(_item);
		}
		/** 載入所有陷阱道具 */
		for (String _tdata : _trapData.split(_symbol2)) {
			Trap _trap = null;
			switch (_tdata.split(_symbol1)[1]) {
			case "SummoningTrap":
				_trap = new SummoningTrap(Integer.valueOf(_tdata
						.split(_symbol1)[0]), _tdata.split(_symbol1)[2],
						Integer.valueOf(_tdata.split(_symbol1)[3]),
						Integer.valueOf(_tdata.split(_symbol1)[4]),
						Float.valueOf(_tdata.split(_symbol1)[5]),
						Integer.valueOf(_tdata.split(_symbol1)[6]),
						Integer.valueOf(_tdata.split(_symbol1)[7]),
						Integer.valueOf(_tdata.split(_symbol1)[8]),
						Integer.valueOf(_tdata.split(_symbol1)[9]));
				break;
			case "DetonatedTrap":
				_trap = new DetonatedTrap(Integer.valueOf(_tdata
						.split(_symbol1)[0]), _tdata.split(_symbol1)[2],
						Integer.valueOf(_tdata.split(_symbol1)[3]),
						Integer.valueOf(_tdata.split(_symbol1)[4]),
						Float.valueOf(_tdata.split(_symbol1)[5]),
						Integer.valueOf(_tdata.split(_symbol1)[6]),
						Integer.valueOf(_tdata.split(_symbol1)[7]),
						Integer.valueOf(_tdata.split(_symbol1)[8]),
						Integer.valueOf(_tdata.split(_symbol1)[9]),
						Integer.valueOf(_tdata.split(_symbol1)[10]),
						Integer.valueOf(_tdata.split(_symbol1)[11]),
						Integer.valueOf(_tdata.split(_symbol1)[12]),
						Integer.valueOf(_tdata.split(_symbol1)[13]));
				break;
			case "TimingTrap":
				_trap = new TimingTrap(
						Integer.valueOf(_tdata.split(_symbol1)[0]),
						_tdata.split(_symbol1)[2], Integer.valueOf(_tdata
								.split(_symbol1)[3]), Integer.valueOf(_tdata
								.split(_symbol1)[4]), Float.valueOf(_tdata
								.split(_symbol1)[5]), Integer.valueOf(_tdata
								.split(_symbol1)[6]), Integer.valueOf(_tdata
								.split(_symbol1)[7]), Integer.valueOf(_tdata
								.split(_symbol1)[8]), Float.valueOf(_tdata
								.split(_symbol1)[9]), Float.valueOf(_tdata
								.split(_symbol1)[10]), Integer.valueOf(_tdata
								.split(_symbol1)[11]), Integer.valueOf(_tdata
								.split(_symbol1)[12]), Integer.valueOf(_tdata
								.split(_symbol1)[13]), Integer.valueOf(_tdata
								.split(_symbol1)[14]), Integer.valueOf(_tdata
								.split(_symbol1)[15]));
				break;
			}

			_trapList.add(_trap);
		}
		// System.out.println("Item List Size :"+_itemList.size());
		System.out.println("Trap List Size :" + _trapList.size());

		// Initial Game Map
		GameMap _map = new GameMap(_mapData.split(_symbol1)[2],
				_mapData.split(_symbol1)[3], Integer.valueOf(_mapData
						.split(_symbol1)[4]), Float.valueOf(_mapData
						.split(_symbol1)[6]), Integer.valueOf(_mapData
						.split(_symbol1)[9]), Integer.valueOf(_mapData
						.split(_symbol1)[10]), Integer.valueOf(_mapData
						.split(_symbol1)[7]), Integer.valueOf(_mapData
						.split(_symbol1)[8]), Integer.valueOf(_mapData
						.split(_symbol1)[11]), Integer.valueOf(_mapData
						.split(_symbol1)[12]), Integer.valueOf(_mapData
						.split(_symbol1)[13]), Float.valueOf(_mapData
						.split(_symbol1)[14]), Integer.valueOf(_mapData
						.split(_symbol1)[15]), Integer.valueOf(_mapData
						.split(_symbol1)[16]), Integer.valueOf(_mapData
						.split(_symbol1)[17]), Float.valueOf(_mapData
						.split(_symbol1)[18]), Float.valueOf(_mapData
						.split(_symbol1)[19]), Float.valueOf(_mapData
						.split(_symbol1)[20]), _itemList, _trapList);

		// Initial Game Instance
		GameInstance game = new GameInstance(leader.getAccountName(), _map);
		// Initial Game Room
		GameRoom room = new GameRoom(_mapData.split(_symbol1)[0], leader,
				Integer.valueOf(_mapData.split(_symbol1)[5]), _map, game);
		// Restore Room in World
		GuardWorld.getInstance().StoreRoom(room);
		// Put PC To Room
		leader.setRoom(room);
		// System.out.println("PC is In Room" + pc.isInRoom());
		// TODO SERVER -> CLIENT 確認封包 (ClientOpcodes.C_CreateRoom)

		leader.SendClientPacket(_packet);
	}
}
