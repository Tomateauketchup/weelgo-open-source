package com.weelgo.core.undoredo;

public class BasicIncrementalDataWorker implements IIncrementalDataWorker{

	@Override
	public Object createIncrementalData(Object oldModel, Object newModel) {		
		return newModel;
	}

	@Override
	public Object restoreModel(Object model, Object incrementalData) {		
		return incrementalData;
	}

}
