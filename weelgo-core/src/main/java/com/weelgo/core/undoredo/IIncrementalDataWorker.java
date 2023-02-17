package com.weelgo.core.undoredo;

public interface IIncrementalDataWorker {
	
	public Object createIncrementalData(Object oldModel,Object newModel);
	public Object restoreModel(Object model,Object incrementalData);

}
