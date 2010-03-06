package edu.washington.cs.activedht.db.kahlua.dhtwrapper;

import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import org.gudy.azureus2.core3.util.HashWrapper;

import se.krka.kahlua.stdlib.BaseLib;
import se.krka.kahlua.vm.JavaFunction;
import se.krka.kahlua.vm.LuaCallFrame;
import se.krka.kahlua.vm.LuaClosure;
import se.krka.kahlua.vm.LuaState;
import se.krka.kahlua.vm.LuaTable;
import se.krka.kahlua.vm.LuaTableImpl;
import se.krka.kahlua.vm.serialize.Serializer;

import com.aelitis.azureus.core.dht.control.DHTControl;

import edu.washington.cs.activedht.db.dhtwrapper.GetAction;
import edu.washington.cs.activedht.db.dhtwrapper.GetCallback;
import edu.washington.cs.activedht.db.dhtwrapper.GetOperationAdapter;
import edu.washington.cs.activedht.db.dhtwrapper.LookupAction;
import edu.washington.cs.activedht.db.dhtwrapper.PutAction;
import edu.washington.cs.activedht.db.dhtwrapper.UpdateNeighborsOperationAdapter;

public class DhtWrapper implements JavaFunction {

	public static enum Function {
		SYS_TIME("sysTime"), KEY("key"), LOCAL_NODE("localNode"), GET("get"), PUT(
				"put"), DELETE("delete"), LOOKUP("lookup");
		String name;

		Function(String name) {
			this.name = name;

		}
	}

	private final Queue<Runnable> postActions = new LinkedList<Runnable>();

	private final Function function;
	private final NodeWrapper localNode;
	private final Map<HashWrapper, Set<NodeWrapper>> neighbors;
	private final DHTControl control;

	private final LuaState state;

	private final HashWrapper key;

	public DhtWrapper(Function function, LuaState state, HashWrapper key,
			NodeWrapper localNode,
			Map<HashWrapper, Set<NodeWrapper>> neighbors, DHTControl control) {
		this.function = function;
		this.state = state;
		this.key = key;
		this.localNode = localNode;
		this.neighbors = neighbors;
		this.control = control;
	}

	public int call(LuaCallFrame callFrame, int nArguments) {
		switch (function) {
		case SYS_TIME:
			return getSystemTime(callFrame, nArguments);
		case KEY:
			return getKey(callFrame, nArguments);
		case LOCAL_NODE:
			return localNode(callFrame, nArguments);
		case GET:
			return get(callFrame, nArguments);
		case PUT:
			return put(callFrame, nArguments);
		case DELETE:
			return delete(callFrame, nArguments);
		case LOOKUP:
			return lookup(callFrame, nArguments);
		default:
			return 0;
		}
	}

	private int lookup(LuaCallFrame callFrame, int nArguments) {
		HashWrapper key = this.key;
		if (nArguments > 0) {
			key = (HashWrapper) callFrame.get(0);
		}
		postActions.offer(new LookupAction(key, control,
				new UpdateNeighborsOperationAdapter(neighbors.get(key))));
		return 0;
	}

	private int delete(LuaCallFrame callFrame, int nArguments) {
		HashWrapper key = this.key;
		if (nArguments > 0) {
			key = (HashWrapper) callFrame.get(0);
		}
		postActions.offer(new DeleteAction(key, control,
				new UpdateNeighborsOperationAdapter(neighbors.get(key))));
		return 0;
	}

	private int put(LuaCallFrame callFrame, int nArguments) {
		HashWrapper key = this.key;
		if (nArguments < 1) {
			BaseLib.luaAssert(nArguments >= 1, "Not enough arguments");
		}
		int valIndex = 0;
		if (nArguments > 1) {
			key = (HashWrapper) callFrame.get(0);
			++valIndex;
		}
		byte[] value = Serializer.serialize(callFrame.get(valIndex), state.getEnvironment());
		postActions.offer(new PutAction(key, value, control,
				new UpdateNeighborsOperationAdapter(neighbors.get(key))));
		return 0;
	}

	private int get(LuaCallFrame callFrame, int nArguments) {
		HashWrapper key = this.key;
		if (nArguments > 0) {
			key = (HashWrapper) callFrame.get(0);
		}
		int maxValues = 0;
		if (nArguments > 1) {
			maxValues = (Integer) callFrame.get(1);
		}
		GetCallback callback = null;
		if (nArguments > 2) {
			final LuaClosure closure = (LuaClosure) callFrame.get(2);
			callback = new LuaGetCallback(closure, state);
		}
		postActions.offer(new GetAction(key, this.key, maxValues, control,
				new GetOperationAdapter(neighbors.get(key), callback)));
		return 0;
	}

	private int localNode(LuaCallFrame callFrame, int nArguments) {
		callFrame.push(localNode);
		return 1;
	}

	private int getKey(LuaCallFrame callFrame, int nArguments) {
		callFrame.push(key);
		return 1;
	}

	private int getSystemTime(LuaCallFrame callFrame, int nArguments) {
		callFrame.push(System.currentTimeMillis());
		return 1;
	}

	public Function getFunction() {
		return function;
	}

	public Queue<Runnable> getPostActions() {
		return postActions;
	}

	public static void register(LuaState state, HashWrapper key,
			Map<HashWrapper, Set<NodeWrapper>> neighbors, DHTControl control) {
		LuaTable dht = new LuaTableImpl();
		state.getEnvironment().rawset("dht", dht);
		NodeWrapper node = null;
		if (control != null) {
			node = new NodeWrapper(control.getTransport().getLocalContact());
		}

		for (Function function : Function.values()) {
			dht.rawset(function.name, new DhtWrapper(function, state, key,
					node,
					neighbors, control));
		}
	}

}