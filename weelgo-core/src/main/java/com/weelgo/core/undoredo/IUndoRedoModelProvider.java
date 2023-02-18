package com.weelgo.core.undoredo;

public interface IUndoRedoModelProvider<T> {
	
	public T getClonedModel();
	public void pushToModel(T o);
	public void modelSaved();

}
