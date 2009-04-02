package edu.washington.cs.activedht.db.dhtactionexecutor.exedhtaction;

import com.aelitis.azureus.core.dht.control.DHTControl;

import edu.washington.cs.activedht.code.insecure.dhtaction.DHTAction;

/**
 * Add here whenever you add a new DHTAction type.
 * 
 * TODO(roxana): We could also make this be automatic, using some naming
 * convention.
 * 
 * @author roxana
 */
public interface ExecutableDHTActionFactory {
	@SuppressWarnings("unchecked")
	public ExecutableDHTAction createAction(DHTAction action,
                                            DHTControl control,
                                            long running_timeout);
}