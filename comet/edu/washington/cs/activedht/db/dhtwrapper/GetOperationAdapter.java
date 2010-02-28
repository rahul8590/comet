/**
 * 
 */
package edu.washington.cs.activedht.db.dhtwrapper;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.aelitis.azureus.core.dht.transport.DHTTransportContact;
import com.aelitis.azureus.core.dht.transport.DHTTransportValue;

import edu.washington.cs.activedht.db.kahlua.dhtwrapper.NodeWrapper;

public class GetOperationAdapter extends
		UpdateNeighborsOperationAdapter {
	private final List<DHTTransportValue> values = new LinkedList<DHTTransportValue>();
	public final GetCallback callback;

	public GetOperationAdapter(Set<NodeWrapper> neighbors,
			GetCallback callback) {
		super(neighbors);
		this.callback = callback;
	}

	public void read(DHTTransportContact contact, DHTTransportValue value) {
		synchronized (values) {
			values.add(value);
		}
	}

	public void complete(boolean timeout) {
		super.complete(timeout);
		if (callback != null) {
			callback.call(values);
		}
	}
}